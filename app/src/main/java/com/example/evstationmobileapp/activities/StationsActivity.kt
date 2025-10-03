package com.example.evstationmobileapp.activities

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.evstationmobileapp.R
import com.example.evstationmobileapp.adapters.ChargingStationAdapter
import com.example.evstationmobileapp.models.ChargingStation

class StationsActivity : AppCompatActivity() {

    private lateinit var adapter: ChargingStationAdapter
    private lateinit var searchView: SearchView
    private var allStations = listOf<ChargingStation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stations)

        // Initialize views
        searchView = findViewById(R.id.searchView)
        val stationsContainer = findViewById<LinearLayout>(R.id.stationsContainer)

        // Sample data - Replace with your backend data
        allStations = getSampleStations()

        // Setup adapter
        adapter = ChargingStationAdapter(allStations) { station ->
            // Handle item click
            onStationClicked(station)
        }

        // Setup search functionality
        setupSearch()

        // For now, manually add cards
        // When you integrate with backend, use RecyclerView instead
        displayStations()
    }

    private fun getSampleStations(): List<ChargingStation> {
        return listOf(
            ChargingStation(
                id = 1,
                name = "Station Name",
                location = "Station Location",
                availableSlots = 5,
                totalSlots = 8,
                operatingHours = "24/7",
                pricePerKwh = 50.0,
                is24Hours = true
            ),
            ChargingStation(
                id = 2,
                name = "Station Name",
                location = "Station Location",
                availableSlots = 5,
                totalSlots = 8,
                operatingHours = "6 AM - 10 PM",
                pricePerKwh = 50.0,
                is24Hours = false
            )
        )
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterStations(newText ?: "")
                return true
            }
        })
    }

    private fun filterStations(query: String) {
        val filteredList = if (query.isEmpty()) {
            allStations
        } else {
            allStations.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.location.contains(query, ignoreCase = true)
            }
        }
        adapter.updateStations(filteredList)
    }

    private fun displayStations() {
        // This is for manual card display
        // For dynamic rendering, convert stationsContainer to RecyclerView
        val stationsContainer = findViewById<LinearLayout>(R.id.stationsContainer)
        stationsContainer.removeAllViews()

        allStations.forEach { station ->
            val view = layoutInflater.inflate(R.layout.item_charging_station, stationsContainer, false)

            view.findViewById<TextView>(R.id.stationName).text = station.name
            view.findViewById<TextView>(R.id.stationLocation).text = station.location
            view.findViewById<TextView>(R.id.availableSlots).text = station.availableSlots.toString()
            view.findViewById<TextView>(R.id.totalSlots).text = "/${station.totalSlots} Slots"
            view.findViewById<TextView>(R.id.operatingHours).text = station.operatingHours
            view.findViewById<TextView>(R.id.priceRate).text = "Rs. ${station.pricePerKwh.toInt()}/kWh"

            view.setOnClickListener {
                onStationClicked(station)
            }

            stationsContainer.addView(view)
        }
    }

    private fun onStationClicked(station: ChargingStation) {
        // Handle station click - navigate to details, etc.
    }
}