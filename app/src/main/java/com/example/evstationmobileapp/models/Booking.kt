package com.example.evstationmobileapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * This data class represents a single booking object as returned by the backend API.
 * It is used to parse the JSON response into a type-safe Kotlin object.
 */

@Parcelize
data class Booking(
    @SerializedName("id")
    val id: String,

    @SerializedName("stationName")
    val stationName: String,

    // The start time of the reservation in ISO 8601 format (e.g., "2025-10-13T05:00:46.4Z")
    @SerializedName("reservationStartTime")
    val reservationStartTime: String,

    // The current status of the booking (e.g., "Confirmed", "Cancelled")
    @SerializedName("status")
    val status: String,

    @SerializedName("qrCode")
    val qrCode: String? = null
) : Parcelable

