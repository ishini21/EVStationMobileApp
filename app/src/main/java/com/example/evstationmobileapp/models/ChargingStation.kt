package com.example.evstationmobileapp.models

import com.google.gson.annotations.SerializedName

data class ChargingStation(
    @SerializedName("id")
    val id: String,

    @SerializedName("stationName")
    val stationName: String,

    @SerializedName("noOfSlots")
    val totalSlots: Int,

    @SerializedName("location")
    val location: Location,

    @SerializedName("operatingHours")
    val operatingHours: OperatingHours,

    @SerializedName("slots")
    val slots: List<Slot>
    // Note: Fields like pricePerKwh and availableSlots are not in your API response.
    // The adapter will need to handle their absence.
)

data class Location(
    @SerializedName("address")
    val address: String,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double
)

data class OperatingHours(
    @SerializedName("openTime")
    val openTime: String,

    @SerializedName("closeTime")
    val closeTime: String,

    @SerializedName("is24Hours")
    val is24Hours: Boolean
)