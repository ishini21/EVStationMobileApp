package com.example.evstationmobileapp.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.evstationmobileapp.MainActivity
import com.example.evstationmobileapp.R
import com.example.evstationmobileapp.models.ChargingStation
import java.text.SimpleDateFormat
import java.util.*

class NewReservationActivity : AppCompatActivity() {

    private lateinit var station: ChargingStation
    private lateinit var etReservationDate: EditText
    private lateinit var etTimeFrom: EditText
    private lateinit var etTimeTo: EditText
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhoneNo: EditText
    private lateinit var etVehicleNumber: EditText
    private lateinit var tvStationLocation: TextView
    private lateinit var tvStationPrice: TextView
    private lateinit var tvDateError: TextView

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MM/dd/yy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_reservation)

        // Get station data from intent
        station = ChargingStation(
            id = intent.getIntExtra("STATION_ID", 0),
            name = intent.getStringExtra("STATION_NAME") ?: "",
            location = intent.getStringExtra("STATION_LOCATION") ?: "",
            availableSlots = intent.getIntExtra("AVAILABLE_SLOTS", 0),
            totalSlots = intent.getIntExtra("TOTAL_SLOTS", 0),
            operatingHours = intent.getStringExtra("OPERATING_HOURS") ?: "",
            pricePerKwh = intent.getDoubleExtra("PRICE_PER_KWH", 0.0),
            is24Hours = intent.getBooleanExtra("IS_24_HOURS", false)
        )

        initializeViews()
        setupStationInfo()
        setupDateTimePickers()
        setupNavigation()
    }

    private fun initializeViews() {
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        tvStationLocation = findViewById(R.id.tvStationLocation)
        tvStationPrice = findViewById(R.id.tvStationPrice)
        tvDateError = findViewById(R.id.tvDateError)

        etReservationDate = findViewById(R.id.etReservationDate)
        etTimeFrom = findViewById(R.id.etTimeFrom)
        etTimeTo = findViewById(R.id.etTimeTo)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPhoneNo = findViewById(R.id.etPhoneNo)
        etVehicleNumber = findViewById(R.id.etVehicleNumber)

        findViewById<Button>(R.id.btnBookNow).setOnClickListener {
            validateAndBookReservation()
        }
    }

    private fun setupStationInfo() {
        tvStationLocation.text = station.location
        tvStationPrice.text = "Rs. ${station.pricePerKwh.toInt()}/kWh"
    }

    private fun setupDateTimePickers() {
        // Date Picker
        etReservationDate.setOnClickListener {
            val today = Calendar.getInstance()
            val maxDate = Calendar.getInstance()
            maxDate.add(Calendar.DAY_OF_YEAR, 7)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    etReservationDate.setText(dateFormat.format(calendar.time))
                    tvDateError.visibility = android.view.View.GONE
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
        etTimeFrom.setOnClickListener {
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    etTimeFrom.setText(timeFormat.format(calendar.time))
                },
                currentHour,
                currentMinute,
                false
            ).show()
        }

        // Time To Picker
        etTimeTo.setOnClickListener {
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    etTimeTo.setText(timeFormat.format(calendar.time))
                },
                currentHour,
                currentMinute,
                false
            ).show()
        }
    }

    private fun validateAndBookReservation() {
        val date = etReservationDate.text.toString()
        val timeFrom = etTimeFrom.text.toString()
        val timeTo = etTimeTo.text.toString()
        val name = etName.text.toString()
        val email = etEmail.text.toString()
        val phoneNo = etPhoneNo.text.toString()
        val vehicleNumber = etVehicleNumber.text.toString()

        // Validation
        when {
            date.isEmpty() -> {
                Toast.makeText(this, "Please select a reservation date", Toast.LENGTH_SHORT).show()
                tvDateError.visibility = android.view.View.VISIBLE
                return
            }
            timeFrom.isEmpty() -> {
                Toast.makeText(this, "Please select start time", Toast.LENGTH_SHORT).show()
                return
            }
            timeTo.isEmpty() -> {
                Toast.makeText(this, "Please select end time", Toast.LENGTH_SHORT).show()
                return
            }
            name.isEmpty() -> {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
                return
            }
            email.isEmpty() -> {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                return
            }
            phoneNo.isEmpty() -> {
                Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show()
                return
            }
            vehicleNumber.isEmpty() -> {
                Toast.makeText(this, "Please enter your vehicle number", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // If all validations pass, proceed with booking
        bookReservation(date, timeFrom, timeTo, name, email, phoneNo, vehicleNumber)
    }

    private fun bookReservation(
        date: String,
        timeFrom: String,
        timeTo: String,
        name: String,
        email: String,
        phoneNo: String,
        vehicleNumber: String
    ) {
        // Here you would typically make an API call to your backend
        // For now, just show a success message
        Toast.makeText(this, "Reservation booked successfully!", Toast.LENGTH_LONG).show()

        // Navigate to bookings page or back to home
        finish()
    }

    private fun setupNavigation() {
        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navStations = findViewById<LinearLayout>(R.id.navStations)
        val navBookings = findViewById<LinearLayout>(R.id.navBookings)
        val navProfile = findViewById<LinearLayout>(R.id.navProfile)

        navHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        navStations.setOnClickListener {
            val intent = Intent(this, StationsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        navBookings.setOnClickListener {
            // Navigate to Bookings
            // val intent = Intent(this, BookingsActivity::class.java)
            // startActivity(intent)
        }

        navProfile.setOnClickListener {
            // Navigate to Profile
            // val intent = Intent(this, ProfileActivity::class.java)
            // startActivity(intent)
        }
    }
}