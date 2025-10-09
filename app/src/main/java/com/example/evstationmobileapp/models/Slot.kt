// In com/example/evstationmobileapp/models/Slot.kt
package com.example.evstationmobileapp.models

import com.google.gson.annotations.SerializedName

data class Slot(
    @SerializedName("id")
    val id: String,

    @SerializedName("connectorType")
    val connectorType: String,

    @SerializedName("powerRating")
    val powerRating: String,

    @SerializedName("pricePerKWh")
    val pricePerKWh: Double,

    @SerializedName("status")
    val status: String // e.g., "Available", "Occupied", "Maintenance"
)