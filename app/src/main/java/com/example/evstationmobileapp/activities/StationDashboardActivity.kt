package com.example.evstationmobileapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.evstationmobileapp.R // Ensure R is imported
import com.example.evstationmobileapp.databinding.ActivityStationDashboardBinding
import com.example.evstationmobileapp.utils.SessionManager

class StationDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStationDashboardBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStationDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // 1. REMOVE this line. The new layout has no toolbar.
        // setSupportActionBar(binding.toolbar)

        setupClickListeners()
        setupBottomNavigation()
    }

    private fun setupClickListeners() {
        // 2. Use the binding object directly to access views. It's cleaner!
        binding.fabScanQr.setOnClickListener {
            launchQrScanner()
        }

        binding.cardViewBookings.setOnClickListener {
            Toast.makeText(this, "Bookings Clicked", Toast.LENGTH_SHORT).show()
            // Or navigate to a bookings activity when ready
        }
    }

    private fun launchQrScanner() {
        val intent = Intent(this, QRCodeScannerActivity::class.java)
        startActivity(intent)
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_station_dashboard -> {
                    // Already on the dashboard
                    true
                }
                R.id.nav_station_bookings -> {
                    Toast.makeText(this, "Bookings Clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_station_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}