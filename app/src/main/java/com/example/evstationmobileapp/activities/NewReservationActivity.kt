package com.example.evstationmobileapp.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.evstationmobileapp.databinding.NewReservationBinding
import com.example.evstationmobileapp.models.ChargingStation
import com.example.evstationmobileapp.remote.ApiClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NewReservationActivity : AppCompatActivity() {

    // The binding object that gives you direct access to all views in your layout
    private lateinit var binding: NewReservationBinding
    private lateinit var station: ChargingStation

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout using View Binding
        binding = NewReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get station data passed from the previous activity
        station = ChargingStation(
            id = intent.getIntExtra("STATION_ID", 0),
            name = intent.getStringExtra("STATION_NAME") ?: "Unknown",
            location = intent.getStringExtra("STATION_LOCATION") ?: "Unknown",
            pricePerKwh = intent.getDoubleExtra("PRICE_PER_KWH", 0.0),

            // Add these lines to retrieve the missing data
            availableSlots = intent.getIntExtra("AVAILABLE_SLOTS", 0),
            totalSlots = intent.getIntExtra("TOTAL_SLOTS", 0),
            operatingHours = intent.getStringExtra("OPERATING_HOURS") ?: "N/A"
            // Add any other fields your ChargingStation data class requires
        )

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        // Use the binding object to set initial values
        binding.tvStationLocation.text = station.location
        binding.tvStationPrice.text = "Rs. ${station.pricePerKwh.toInt()}/kWh"
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish() // Go back to the previous screen
        }

        binding.btnBookNow.setOnClickListener {
            validateAndBookReservation()
        }

        setupDateTimePickers()
    }

    private fun setupDateTimePickers() {
        // Date Picker
        binding.etReservationDate.setOnClickListener {
            val today = Calendar.getInstance()
            val maxDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 7) }

            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    binding.etReservationDate.setText(dateFormat.format(calendar.time))
                    binding.tvDateError.visibility = View.GONE
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.minDate = today.timeInMillis
            datePickerDialog.datePicker.maxDate = maxDate.timeInMillis
            datePickerDialog.show()
        }

        // Time From Picker
        binding.etTimeFrom.setOnClickListener {
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    binding.etTimeFrom.setText(timeFormat.format(calendar.time))
                },
                currentHour,
                currentMinute,
                false // Use 'false' for 12-hour format with AM/PM
            ).show()
        }

        // Time To Picker
        binding.etTimeTo.setOnClickListener {
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    binding.etTimeTo.setText(timeFormat.format(calendar.time))
                },
                currentHour,
                currentMinute,
                false
            ).show()
        }
    }

    private fun validateAndBookReservation() {
        // Access text from EditTexts using the binding object
        val date = binding.etReservationDate.text.toString()
        val timeFrom = binding.etTimeFrom.text.toString()
        val timeTo = binding.etTimeTo.text.toString()
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phoneNo = binding.etPhoneNo.text.toString().trim()
        val vehicleNumber = binding.etVehicleNumber.text.toString().trim()

        // Validation logic remains the same
        if (date.isEmpty()) {
            binding.tvDateError.visibility = View.VISIBLE
            Toast.makeText(this, "Please select a reservation date", Toast.LENGTH_SHORT).show()
            return
        }
        if (timeFrom.isEmpty()) {
            Toast.makeText(this, "Please select a start time", Toast.LENGTH_SHORT).show()
            return
        }
        // ... Add the rest of your validation for other fields ...

        // If validation passes, proceed to book the reservation
        bookReservation(date, timeFrom, timeTo)
    }

    private fun bookReservation(date: String, timeFrom: String, timeTo: String) {
        // Launch a coroutine to make the network call
        lifecycleScope.launch {
            try {
                // TODO: Create a reservation object and an 'addReservation' function in your ApiClient
                // val reservation = Reservation(...)
                // val resultJsonString = ApiClient.addReservation(reservation)
                // val resultJson = JSONObject(resultJsonString)

                // if (!resultJson.has("error")) {
                //    Toast.makeText(this@NewReservationActivity, "Reservation booked successfully!", Toast.LENGTH_LONG).show()
                //    finish() // Close activity on success
                // } else {
                //    val errorMessage = resultJson.getString("error")
                //    Toast.makeText(this@NewReservationActivity, "Booking failed: $errorMessage", Toast.LENGTH_LONG).show()
                // }

                // For now, we'll just show a success message for testing
                Toast.makeText(this@NewReservationActivity, "Reservation booked successfully!", Toast.LENGTH_LONG).show()
                finish()

            } catch (e: Exception) {
                Toast.makeText(this@NewReservationActivity, "An error occurred: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}