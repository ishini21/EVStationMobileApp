package com.example.evstationmobileapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.evstationmobileapp.R
import com.example.evstationmobileapp.models.ChargingStation

class ChargingStationAdapter(
    private var stations: List<ChargingStation>,
    private val onItemClicked: (ChargingStation) -> Unit
) : RecyclerView.Adapter<ChargingStationAdapter.StationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_charging_station, parent, false)
        return StationViewHolder(view)
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        val station = stations[position]
        holder.bind(station, onItemClicked)
    }

    override fun getItemCount(): Int = stations.size

    fun updateStations(filteredStations: List<ChargingStation>) {
        this.stations = filteredStations
        notifyDataSetChanged()
    }

    class StationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.stationName)
        private val locationTextView: TextView = itemView.findViewById(R.id.stationLocation)
        private val totalSlotsTextView: TextView = itemView.findViewById(R.id.totalSlots)
        private val operatingHoursTextView: TextView = itemView.findViewById(R.id.operatingHours)

        // These views from your XML are not supported by the API data provided
        private val availableSlotsTextView: TextView = itemView.findViewById(R.id.availableSlots)
        private val priceRateTextView: TextView = itemView.findViewById(R.id.priceRate)


        fun bind(station: ChargingStation, onItemClicked: (ChargingStation) -> Unit) {
            nameTextView.text = station.stationName
            locationTextView.text = station.location.address
            totalSlotsTextView.text = "/${station.totalSlots} Slots"

            // Format operating hours
            if (station.operatingHours.is24Hours) {
                operatingHoursTextView.text = "24/7"
            } else {
                operatingHoursTextView.text = "${station.operatingHours.openTime} - ${station.operatingHours.closeTime}"
            }

            // Hide fields that are not available from the API
            availableSlotsTextView.visibility = View.GONE
            priceRateTextView.visibility = View.GONE


            itemView.setOnClickListener {
                onItemClicked(station)
            }
        }
    }
}