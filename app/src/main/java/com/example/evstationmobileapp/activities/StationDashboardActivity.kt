package com.example.evstationmobileapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.evstationmobileapp.databinding.ActivityStationDashboardBinding
import com.example.evstationmobileapp.utils.SessionManager
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class StationDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStationDashboardBinding
    private lateinit var sessionManager: SessionManager

    // Modern activity result launcher for the QR scanner
    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(this, "Scan cancelled", Toast.LENGTH_LONG).show()
        } else {
            // Since there's no API, we just display the scanned content.
            // This is where you would add logic to handle the QR code data in the future.
            val scannedQrCode = result.contents
            Toast.makeText(this, "Scanned: $scannedQrCode", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStationDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setSupportActionBar(binding.toolbar)
        setupClickListeners()
        setupBottomNavigation()
    }

    private fun setupClickListeners() {
        binding.fabScanQr.setOnClickListener {
            launchQrScanner()
        }
    }

    private fun launchQrScanner() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan a booking QR code")
        options.setCameraId(0) // Use a specific camera of the device
        options.setBeepEnabled(true)
        options.setBarcodeImageEnabled(true)
        barcodeLauncher.launch(options)
    }

    // This function has been removed as there is no API to call.

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                com.example.evstationmobileapp.R.id.nav_station_dashboard -> {
                    Toast.makeText(this, "Dashboard Clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                com.example.evstationmobileapp.R.id.nav_station_bookings -> {
                    Toast.makeText(this, "Bookings Clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                com.example.evstationmobileapp.R.id.nav_station_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}

