package com.example.evstationmobileapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.evstationmobileapp.R
import com.example.evstationmobileapp.adapters.ChargingStationAdapter
import com.example.evstationmobileapp.models.ChargingStation
import com.example.evstationmobileapp.remote.ApiClient
import kotlinx.coroutines.launch

class StationsActivity : AppCompatActivity() {

    private lateinit var adapter: ChargingStationAdapter
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private var allStations = mutableListOf<ChargingStation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stations)

        // Initialize views
        searchView = findViewById(R.id.searchView)
        recyclerView = findViewById(R.id.stationsRecyclerView)

        // Setup adapter with an empty list initially
        setupRecyclerView()

        // Fetch data from the API
        fetchStations()

        // Setup search functionality
        setupSearch()
    }

    private fun setupRecyclerView() {
        adapter = ChargingStationAdapter(allStations) { station ->
            // Handle item click
            onStationClicked(station)
        }
        recyclerView.adapter = adapter
    }

    private fun fetchStations() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getStations()
                if (response.isSuccessful && response.body() != null) {
                    allStations.clear()
                    allStations.addAll(response.body()!!)
                    adapter.updateStations(allStations)
                } else {
                    Toast.makeText(this@StationsActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("StationsActivity", "Error fetching stations", e)
                Toast.makeText(this@StationsActivity, "Failed to connect to server.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterStations(newText.orEmpty())
                return true
            }
        })
    }

    private fun filterStations(query: String) {
        val filteredList = if (query.isEmpty()) {
            allStations
        } else {
            allStations.filter {
                it.stationName.contains(query, ignoreCase = true) ||
                        it.location.address.contains(query, ignoreCase = true)
            }
        }
        adapter.updateStations(filteredList)
    }

    private fun onStationClicked(station: ChargingStation) {
        val intent = Intent(this, StationDetailsActivity::class.java).apply {
            // ONLY pass the unique ID
            putExtra("STATION_ID", station.id)
        }
        startActivity(intent)
    }
}