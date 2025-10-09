package com.example.evstationmobileapp.models

import com.google.gson.annotations.SerializedName

data class BookingRequest(
    @SerializedName("customerNic")
    val customerNic: String,

    @SerializedName("customerName")
    val customerName: String,

    @SerializedName("customerEmail")
    val customerEmail: String,

    @SerializedName("customerPhone")
    val customerPhone: String,

    @SerializedName("stationId")
    val stationId: String,

    @SerializedName("slotId")
    val slotId: String,

    @SerializedName("reservationStartTime")
    val reservationStartTime: String, // ISO 8601 format (e.g., "2025-10-09T17:45:18.639Z")

    @SerializedName("durationMinutes")
    val durationMinutes: Int,

    @SerializedName("estimatedKWh")
    val estimatedKWh: Int,

    @SerializedName("notes")
    val notes: String
)
