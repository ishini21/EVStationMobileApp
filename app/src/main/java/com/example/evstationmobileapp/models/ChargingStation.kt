package com.example.evstationmobileapp.models

data class ChargingStation(
    val id: Int,
    val name: String,
    val location: String,
    val availableSlots: Int,
    val totalSlots: Int,
    val operatingHours: String,
    val pricePerKwh: Double,
    val is24Hours: Boolean = false
)