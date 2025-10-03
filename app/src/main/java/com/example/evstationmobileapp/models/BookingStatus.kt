package com.example.evstationmobileapp.models

enum class BookingStatus {
    PENDING,
    COMPLETED,
    ONGOING,
    CANCELED
}

data class Booking(
    val id: String,
    val stationName: String,
    val location: String,
    val date: String,
    val time: String,
    val status: BookingStatus
)