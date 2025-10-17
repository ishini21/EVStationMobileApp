package com.example.evstationmobileapp.activities

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.evstationmobileapp.MainActivity
import com.example.evstationmobileapp.R
import com.example.evstationmobileapp.databinding.ActivityStationDetailsBinding
import com.example.evstationmobileapp.models.ChargingStation
import com.example.evstationmobileapp.models.Slot
import com.example.evstationmobileapp.remote.ApiClient
import kotlinx.coroutines.launch

class StationDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStationDetailsBinding
    private var station: ChargingStation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStationDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val stationId = intent.getStringExtra("STATION_ID")
        if (stationId == null) {
            Toast.makeText(this, "Station ID not found", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        fetchStationDetails(stationId)
        setupClickListeners()
        // `setupNavigation()` can be called here if it doesn't depend on station data
    }

    private fun fetchStationDetails(stationId: String) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getStationById(stationId)
                if (response.isSuccessful && response.body() != null) {
                    station = response.body()
                    station?.let { setupViews(it) }
                } else {
                    Toast.makeText(this@StationDetailsActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@StationDetailsActivity, "Failed to connect to server.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupViews(station: ChargingStation) {
        binding.tvStationName.text = station.stationName
        binding.tvStationLocation.text = station.location.address

        // Calculate available slots from the list
        val availableSlots = station.slots.count { it.status.equals("Available", ignoreCase = true) }
        binding.tvAvailableSlots.text = availableSlots.toString()
        binding.tvTotalSlots.text = "/${station.totalSlots}"

        // Format operating hours
        binding.tvOperatingHours.text = if (station.operatingHours.is24Hours) {
            "24/7"
        } else {
            "${station.operatingHours.openTime} - ${station.operatingHours.closeTime}"
        }

        // Display a price range if prices vary
        val minPrice = station.slots.minOfOrNull { it.pricePerKWh }?.toInt()
        val maxPrice = station.slots.maxOfOrNull { it.pricePerKWh }?.toInt()
        binding.tvChargingRate.text = if (minPrice != null && maxPrice != null) {
            if (minPrice == maxPrice) "Rs. $minPrice/kWh" else "Rs. $minPrice - $maxPrice/kWh"
        } else {
            "N/A"
        }

        // Dynamically add slot details
        populateSlots(station.slots)
    }

    private fun populateSlots(slots: List<Slot>) {
        binding.slotsContainer.removeAllViews() // Clear old views if any
        val inflater = LayoutInflater.from(this)

        slots.forEach { slot ->
            val slotView = inflater.inflate(R.layout.item_slot_detail, binding.slotsContainer, false)

            val tvConnectorType: TextView = slotView.findViewById(R.id.tvConnectorType)
            val tvSlotStatus: TextView = slotView.findViewById(R.id.tvSlotStatus)
            val tvSlotPrice: TextView = slotView.findViewById(R.id.tvSlotPrice)

            tvConnectorType.text = "${slot.connectorType} - ${slot.powerRating}"
            tvSlotPrice.text = "Rs. ${slot.pricePerKWh.toInt()}/kWh"
            tvSlotStatus.text = slot.status

            // Set status color
            when (slot.status.lowercase()) {
                "available" -> tvSlotStatus.background = ContextCompat.getDrawable(this, R.drawable.status_background_available)
                "occupied" -> tvSlotStatus.background = ContextCompat.getDrawable(this, R.drawable.status_background_occupied)
                else -> tvSlotStatus.background = ContextCompat.getDrawable(this, R.drawable.status_background_maintenance)
            }

            binding.slotsContainer.addView(slotView)
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnOpenInMap.setOnClickListener {
            station?.let { openInMap(it) }
        }

        binding.fabBookNow.setOnClickListener {
            station?.let { bookStation(it) }
        }
    }

    private fun openInMap(station: ChargingStation) {
        val lat = station.location.latitude
        val long = station.location.longitude
        val label = Uri.encode(station.stationName)
        val gmmIntentUri = Uri.parse("geo:0,0?q=$lat,$long($label)")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    private fun bookStation(station: ChargingStation) {
        // Pass only the necessary data to the next activity
        val intent = Intent(this, NewReservationActivity::class.java).apply {
            putExtra("STATION_ID", station.id)
            putExtra("STATION_NAME", station.stationName)
        }
        startActivity(intent)
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
}



//package com.example.evstationmobileapp.activities
//
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import com.example.evstationmobileapp.MainActivity
//import com.example.evstationmobileapp.R
//import com.example.evstationmobileapp.databinding.ActivityStationDetailsBinding
//import com.example.evstationmobileapp.models.ChargingStation
//
//class StationDetailsActivity : AppCompatActivity() {
//
//    // The binding object for direct view access
//    private lateinit var binding: ActivityStationDetailsBinding
//    private lateinit var station: ChargingStation
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        // Inflate the layout using View Binding
//        binding = ActivityStationDetailsBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Get station data from intent (no change here)
//        station = ChargingStation(
//            id = intent.getIntExtra("STATION_ID", 0),
//            name = intent.getStringExtra("STATION_NAME") ?: "",
//            location = intent.getStringExtra("STATION_LOCATION") ?: "",
//            availableSlots = intent.getIntExtra("AVAILABLE_SLOTS", 0),
//            totalSlots = intent.getIntExtra("TOTAL_SLOTS", 0),
//            operatingHours = intent.getStringExtra("OPERATING_HOURS") ?: "",
//            pricePerKwh = intent.getDoubleExtra("PRICE_PER_KWH", 0.0),
//            is24Hours = intent.getBooleanExtra("IS_24_HOURS", false)
//        )
//
//        setupViews()
//        setupClickListeners()
//        setupNavigation()
//    }
//
//    private fun setupViews() {
//        // Use the binding object to access views
//        binding.tvStationName.text = station.name
//        binding.tvStationLocation.text = station.location
//        binding.tvAvailableSlots.text = station.availableSlots.toString()
//        binding.tvTotalSlots.text = "/${station.totalSlots}"
//        binding.tvOperatingHours.text = station.operatingHours
//        binding.tvChargingRate.text = "Rs. ${station.pricePerKwh.toInt()}/kWh"
//    }
//
//    private fun setupClickListeners() {
//        // Back button
//        binding.btnBack.setOnClickListener {
//            finish()
//        }
//
//        // Open in Map button
//        binding.btnOpenInMap.setOnClickListener {
//            openInMap()
//        }
//
//        // Floating Action Button to book
//        binding.fabBookNow.setOnClickListener {
//            bookStation()
//        }
//    }
//
//    private fun setupNavigation() {
//        // Use the single listener for the BottomNavigationView
//        binding.bottomNavigation.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.nav_owner_home -> {
//                    startActivity(Intent(this, MainActivity::class.java))
//                    true
//                }
//                R.id.nav_owner_stations -> {
//                    // Already on a station-related screen, you might not need to do anything
//                    // or you can navigate back to the main list
//                    startActivity(Intent(this, StationsActivity::class.java))
//                    true
//                }
//                R.id.nav_owner_bookings -> {
//                    startActivity(Intent(this, BookingsActivity::class.java))
//                    true
//                }
//                R.id.nav_owner_profile -> {
//                    startActivity(Intent(this, ProfileActivity::class.java))
//                    true
//                }
//                else -> false
//            }
//        }
//    }
//
//    private fun openInMap() {
//        // Replace with actual coordinates if available, otherwise search by location name
//        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(station.location)}")
//        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//        mapIntent.setPackage("com.google.android.apps.maps")
//
//        if (mapIntent.resolveActivity(packageManager) != null) {
//            startActivity(mapIntent)
//        }
//    }
//
//    private fun bookStation() {
//        // This is the code that sends all the necessary data to NewReservationActivity
//        val intent = Intent(this, NewReservationActivity::class.java).apply {
//            putExtra("STATION_ID", station.id)
//            putExtra("STATION_NAME", station.name)
//            putExtra("STATION_LOCATION", station.location)
//            putExtra("AVAILABLE_SLOTS", station.availableSlots)
//            putExtra("TOTAL_SLOTS", station.totalSlots)
//            putExtra("OPERATING_HOURS", station.operatingHours)
//            putExtra("PRICE_PER_KWH", station.pricePerKwh)
//            putExtra("IS_24_HOURS", station.is24Hours)
//        }
//        startActivity(intent)
//    }
//}