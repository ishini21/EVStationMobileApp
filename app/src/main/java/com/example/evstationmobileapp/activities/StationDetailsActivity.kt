package com.example.evstationmobileapp.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.evstationmobileapp.MainActivity
import com.example.evstationmobileapp.R
import com.example.evstationmobileapp.models.ChargingStation
import com.google.android.material.floatingactionbutton.FloatingActionButton

class StationDetailsActivity : AppCompatActivity() {

    private lateinit var station: ChargingStation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.station_details)

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

        setupViews()
        setupNavigation()
    }

    private fun setupViews() {
        // Back button
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Set station details
        findViewById<TextView>(R.id.tvStationName).text = station.name
        findViewById<TextView>(R.id.tvStationLocation).text = station.location
        findViewById<TextView>(R.id.tvAvailableSlots).text = station.availableSlots.toString()
        findViewById<TextView>(R.id.tvTotalSlots).text = "/${station.totalSlots}"
        findViewById<TextView>(R.id.tvOperatingHours).text = station.operatingHours
        findViewById<TextView>(R.id.tvChargingRate).text = "Rs. ${station.pricePerKwh.toInt()}/kWh"

        // Open in Map button
        findViewById<Button>(R.id.btnOpenInMap).setOnClickListener {
            openInMap()
        }

        // Floating Action Button
        findViewById<FloatingActionButton>(R.id.fabBookNow).setOnClickListener {
            bookStation()
        }
    }

    private fun setupNavigation() {
        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navStations = findViewById<LinearLayout>(R.id.navStations)
        val navBookings = findViewById<LinearLayout>(R.id.navBookings)
        val navProfile = findViewById<LinearLayout>(R.id.navProfile)

        navHome.setOnClickListener {
            // Navigate to Home
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        navStations.setOnClickListener {
            // Navigate to Stations
            val intent = Intent(this, StationsActivity::class.java)
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

    private fun openInMap() {
        // Open location in Google Maps
        // Replace with actual coordinates
        val gmmIntentUri = Uri.parse("geo:0,0?q=${station.location}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        }
    }

    private fun bookStation() {
        // Navigate to booking page or show booking dialog
        // You can implement your booking logic here
    }
}