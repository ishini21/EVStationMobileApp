package com.example.evstationmobileapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.evstationmobileapp.databinding.ActivityStationDashboardBinding

class StationDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStationDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStationDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the toolbar as the app's action bar
        setSupportActionBar(binding.toolbar)

        // Handle navigation item clicks
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                // R.id values must match the IDs in your menu XML file
                com.example.evstationmobileapp.R.id.nav_station_dashboard -> {
                    // TODO: Load the main dashboard fragment or content
                    Toast.makeText(this, "Dashboard Clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                com.example.evstationmobileapp.R.id.nav_station_bookings -> {
                    // TODO: Load the bookings fragment or start BookingsActivity
                    Toast.makeText(this, "Bookings Clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                com.example.evstationmobileapp.R.id.nav_station_profile -> {
                    // Navigate to the ProfileActivity
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}