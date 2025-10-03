package com.example.evstationmobileapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.evstationmobileapp.R
import com.example.evstationmobileapp.models.ChargingStation

class ChargingStationAdapter(
    private var stations: List<ChargingStation>,
    private val onItemClick: (ChargingStation) -> Unit
) : RecyclerView.Adapter<ChargingStationAdapter.StationViewHolder>() {

    inner class StationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stationName: TextView = view.findViewById(R.id.stationName)
        val stationLocation: TextView = view.findViewById(R.id.stationLocation)
        val availableSlots: TextView = view.findViewById(R.id.availableSlots)
        val totalSlots: TextView = view.findViewById(R.id.totalSlots)
        val operatingHours: TextView = view.findViewById(R.id.operatingHours)
        val priceRate: TextView = view.findViewById(R.id.priceRate)
        val timeIcon: ImageView = view.findViewById(R.id.timeIcon)

        fun bind(station: ChargingStation) {
            stationName.text = station.name
            stationLocation.text = station.location
            availableSlots.text = station.availableSlots.toString()
            totalSlots.text = "/${station.totalSlots} Slots"
            operatingHours.text = station.operatingHours
            priceRate.text = "Rs. ${station.pricePerKwh.toInt()}/kWh"

            itemView.setOnClickListener {
                onItemClick(station)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_charging_station, parent, false)
        return StationViewHolder(view)
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        holder.bind(stations[position])
    }

    override fun getItemCount() = stations.size

    fun updateStations(newStations: List<ChargingStation>) {
        stations = newStations
        notifyDataSetChanged()
    }
}