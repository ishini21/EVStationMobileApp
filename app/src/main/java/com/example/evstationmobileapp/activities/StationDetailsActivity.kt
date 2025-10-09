package com.example.evstationmobileapp.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.evstationmobileapp.MainActivity
import com.example.evstationmobileapp.R
import com.example.evstationmobileapp.databinding.ActivityStationDetailsBinding
import com.example.evstationmobileapp.models.ChargingStation

class StationDetailsActivity : AppCompatActivity() {

    // The binding object for direct view access
    private lateinit var binding: ActivityStationDetailsBinding
    private lateinit var station: ChargingStation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout using View Binding
        binding = ActivityStationDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get station data from intent (no change here)
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

        setupViews()
        setupClickListeners()
        setupNavigation()
    }

    private fun setupViews() {
        // Use the binding object to access views
        binding.tvStationName.text = station.name
        binding.tvStationLocation.text = station.location
        binding.tvAvailableSlots.text = station.availableSlots.toString()
        binding.tvTotalSlots.text = "/${station.totalSlots}"
        binding.tvOperatingHours.text = station.operatingHours
        binding.tvChargingRate.text = "Rs. ${station.pricePerKwh.toInt()}/kWh"
    }

    private fun setupClickListeners() {
        // Back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Open in Map button
        binding.btnOpenInMap.setOnClickListener {
            openInMap()
        }

        // Floating Action Button to book
        binding.fabBookNow.setOnClickListener {
            bookStation()
        }
    }

    private fun setupNavigation() {
        // Use the single listener for the BottomNavigationView
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_owner_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_owner_stations -> {
                    // Already on a station-related screen, you might not need to do anything
                    // or you can navigate back to the main list
                    startActivity(Intent(this, StationsActivity::class.java))
                    true
                }
                R.id.nav_owner_bookings -> {
                    startActivity(Intent(this, BookingsActivity::class.java))
                    true
                }
                R.id.nav_owner_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun openInMap() {
        // Replace with actual coordinates if available, otherwise search by location name
        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(station.location)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        }
    }

    private fun bookStation() {
        // This is the code that sends all the necessary data to NewReservationActivity
        val intent = Intent(this, NewReservationActivity::class.java).apply {
            putExtra("STATION_ID", station.id)
            putExtra("STATION_NAME", station.name)
            putExtra("STATION_LOCATION", station.location)
            putExtra("AVAILABLE_SLOTS", station.availableSlots)
            putExtra("TOTAL_SLOTS", station.totalSlots)
            putExtra("OPERATING_HOURS", station.operatingHours)
            putExtra("PRICE_PER_KWH", station.pricePerKwh)
            putExtra("IS_24_HOURS", station.is24Hours)
        }
        startActivity(intent)
    }
}