package com.example.evstationmobileapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.evstationmobileapp.R // Ensure R is imported
import com.example.evstationmobileapp.databinding.ActivityStationDashboardBinding
import com.example.evstationmobileapp.models.Operator
import com.example.evstationmobileapp.remote.StationApiService
import com.example.evstationmobileapp.utils.SessionManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

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

        binding.cardViewStationDetails.setOnClickListener {
            navigateToStationDetails()
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

    private fun navigateToStationDetails() {
        // Get the current user ID from session
        val userId = sessionManager.getUserId()
        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Fetch station ID for the current operator
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val stationId = getStationIdForOperator(userId)
                if (stationId != null) {
                    val intent = Intent(this@StationDashboardActivity, OperatorStationDetailsActivity::class.java)
                    intent.putExtra("stationId", stationId)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@StationDashboardActivity, 
                        "No station assigned to this operator", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@StationDashboardActivity, 
                    "Error loading station details: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun getStationIdForOperator(operatorId: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                // This is the raw JSON string from your API
                val responseString = StationApiService.getStationActiveOperators()

                // FIX: Directly parse the JSON array using Gson
                val gson = Gson()
                val operatorsType = object : TypeToken<List<Operator>>() {}.type
                val operators: List<Operator> = gson.fromJson(responseString, operatorsType)

                // Now, find the operator in the successfully parsed list
                val currentOperator = operators.find { it.id == operatorId }

                // Return the stationId if the operator was found, otherwise null
                return@withContext currentOperator?.stationId

            } catch (e: Exception) {
                // This will catch network errors or JSON parsing errors
                e.printStackTrace()
                return@withContext null
            }
        }
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