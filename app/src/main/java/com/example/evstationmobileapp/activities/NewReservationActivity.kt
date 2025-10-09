package com.example.evstationmobileapp.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.evstationmobileapp.databinding.NewReservationBinding
import com.example.evstationmobileapp.models.BookingRequest
import com.example.evstationmobileapp.models.ChargingStation
import com.example.evstationmobileapp.models.Slot
import com.example.evstationmobileapp.remote.ApiClient
import com.example.evstationmobileapp.utils.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
class NewReservationActivity : AppCompatActivity() {

    private lateinit var binding: NewReservationBinding
    private var station: ChargingStation? = null
    private lateinit var sessionManager: SessionManager

    // This will hold the list of slots returned by the availability API
    private var availableSlotsForTime: List<Slot> = listOf()

    private val startCalendar: Calendar = Calendar.getInstance()
    private val endCalendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NewReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        val stationId = intent.getStringExtra("STATION_ID")
        if (stationId.isNullOrEmpty()) {
            Toast.makeText(this, "Error: Station ID missing.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        fetchStationDetails(stationId)
        setupClickListeners()
        setupInitialUIState()
    }

    private fun setupInitialUIState() {
        // Spinner is disabled until the user selects a valid time range
        binding.slotSpinner.isEnabled = false
    }

    private fun fetchStationDetails(stationId: String) {
        lifecycleScope.launch {
            try {
                // Corrected: Removed the authentication token from the API call as it's not required by the interface definition.
                val response = ApiClient.apiService.getStationById(stationId)
                if (response.isSuccessful && response.body() != null) {
                    station = response.body()
                    setupUI()
                } else {
                    Toast.makeText(this@NewReservationActivity, "Failed to load station details: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@NewReservationActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupUI() {
        station?.let {
            binding.tvStationName.text = it.stationName
            binding.tvStationLocation.text = it.location.address
        }
        binding.etName.setText(sessionManager.fetchUserfirstName() ?: "")
        binding.etEmail.setText(sessionManager.fetchUserIdentifier() ?: "")
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnBookNow.setOnClickListener { validateAndBookReservation() }
        setupDateTimePickers()
    }

    private fun setupDateTimePickers() {
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)

        binding.etReservationDate.setOnClickListener {
            val today = Calendar.getInstance()
            // Set max date to 7 days from today
            val maxDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 7) }

            DatePickerDialog(this, { _, year, month, day ->
                startCalendar.set(year, month, day)
                endCalendar.set(year, month, day)
                binding.etReservationDate.setText(dateFormat.format(startCalendar.time))
                clearSlotSelection()
            }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.minDate = today.timeInMillis
                datePicker.maxDate = maxDate.timeInMillis
            }.show()
        }

        binding.etTimeFrom.setOnClickListener {
            TimePickerDialog(this, { _, hour, minute ->
                startCalendar.set(Calendar.HOUR_OF_DAY, hour)
                startCalendar.set(Calendar.MINUTE, minute)
                binding.etTimeFrom.setText(timeFormat.format(startCalendar.time))
                clearSlotSelection()
            }, startCalendar.get(Calendar.HOUR_OF_DAY), startCalendar.get(Calendar.MINUTE), false).show()
        }

        binding.etTimeTo.setOnClickListener {
            TimePickerDialog(this, { _, hour, minute ->
                endCalendar.set(Calendar.HOUR_OF_DAY, hour)
                endCalendar.set(Calendar.MINUTE, minute)
                binding.etTimeTo.setText(timeFormat.format(endCalendar.time))
                // After selecting the end time, call the API to get available slots
                fetchAvailableSlotsFromServer()
            }, endCalendar.get(Calendar.HOUR_OF_DAY), endCalendar.get(Calendar.MINUTE), false).show()
        }
    }

    private fun fetchAvailableSlotsFromServer() {
        if (station == null) {
            Toast.makeText(this, "Station data not loaded yet.", Toast.LENGTH_SHORT).show()
            return
        }
        if (startCalendar.timeInMillis >= endCalendar.timeInMillis) {
            Toast.makeText(this, "End time must be after start time.", Toast.LENGTH_SHORT).show()
            clearSlotSelection()
            return
        }

        val token = sessionManager.fetchAuthToken()
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Authentication error. Please log in again.", Toast.LENGTH_SHORT).show()
            return
        }

        val startTimeIso = formatToIsoUtc(startCalendar)
        val endTimeIso = formatToIsoUtc(endCalendar)
        val stationId = station!!.id

        lifecycleScope.launch {
            try {
                // Corrected: Added "Bearer " prefix and called the instance method
                val response = ApiClient.apiService.getAvailableSlots("Bearer $token", stationId, startTimeIso, endTimeIso)
                if (response.isSuccessful && response.body() != null) {
                    availableSlotsForTime = response.body()!!
                    populateAvailableSlotsSpinner(availableSlotsForTime)
                } else {
                    Toast.makeText(this@NewReservationActivity, "Could not check slot availability: ${response.code()}", Toast.LENGTH_SHORT).show()
                    clearSlotSelection()
                }
            } catch (e: Exception) {
                Toast.makeText(this@NewReservationActivity, "Network error while checking slots: ${e.message}", Toast.LENGTH_SHORT).show()
                clearSlotSelection()
            }
        }
    }

    private fun populateAvailableSlotsSpinner(slots: List<Slot>) {
        if (slots.isEmpty()) {
            Toast.makeText(this, "No slots available for the selected time.", Toast.LENGTH_SHORT).show()
            clearSlotSelection()
            return
        }

        val slotStrings = slots.map { "${it.connectorType} - ${it.powerRating} (Rs. ${it.pricePerKWh.toInt()})" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, slotStrings)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.slotSpinner.adapter = adapter
        binding.slotSpinner.isEnabled = true
    }

    private fun clearSlotSelection() {
        binding.slotSpinner.adapter = null
        binding.slotSpinner.isEnabled = false
        availableSlotsForTime = emptyList()
    }

    private fun validateAndBookReservation() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhoneNo.text.toString().trim()
        val nic = binding.etNicNumber.text.toString().trim()
        val kwh = binding.etEstimatedKwh.text.toString()

        if (listOf(name, email, phone, nic, kwh, binding.etReservationDate.text, binding.etTimeFrom.text, binding.etTimeTo.text).any { it.toString().isEmpty() }) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedSlotIndex = binding.slotSpinner.selectedItemPosition
        if (selectedSlotIndex < 0 || selectedSlotIndex >= availableSlotsForTime.size) {
            Toast.makeText(this, "Please select an available connector.", Toast.LENGTH_SHORT).show()
            return
        }
        val selectedSlot = availableSlotsForTime[selectedSlotIndex]

        val durationMillis = endCalendar.timeInMillis - startCalendar.timeInMillis
        val durationMinutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis).toInt()

        val bookingRequest = BookingRequest(
            customerNic = nic,
            customerName = name,
            customerEmail = email,
            customerPhone = phone,
            stationId = station!!.id,
            slotId = selectedSlot.id,
            reservationStartTime = formatToIsoUtc(startCalendar),
            durationMinutes = durationMinutes,
            estimatedKWh = kwh.toInt(),
            notes = "Booking from Android App"
        )

        val token = sessionManager.fetchAuthToken()
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Authentication error. Please log in again.", Toast.LENGTH_SHORT).show()
            return
        }
        bookReservation("Bearer $token", bookingRequest)
    }

    private fun bookReservation(token: String, request: BookingRequest) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.createBooking(token, request)
                if (response.isSuccessful) {
                    Toast.makeText(this@NewReservationActivity, "Booking successful!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(this@NewReservationActivity, "Booking failed: ${response.code()} - $errorBody", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@NewReservationActivity, "An error occurred: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun formatToIsoUtc(calendar: Calendar): String {
        val instant = Instant.ofEpochMilli(calendar.timeInMillis)
        return DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("UTC")).format(instant)
    }
}

