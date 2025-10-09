package com.example.evstationmobileapp.models

import com.google.gson.annotations.SerializedName

/**
 * Represents the paginated response from the /api/Bookings endpoint.
 * It contains a list of bookings and pagination metadata.
 */
data class BookingResponse(
    @SerializedName("bookings")
    val bookings: List<Booking>,

    @SerializedName("totalCount")
    val totalCount: Int
)

