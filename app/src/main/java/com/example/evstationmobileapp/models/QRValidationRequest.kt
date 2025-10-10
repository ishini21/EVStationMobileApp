package com.example.evstationmobileapp.models

import com.google.gson.annotations.SerializedName

/**
 * Data class for QR code validation request
 */
data class QRValidationRequest(
    @SerializedName("bookingId")
    val bookingId: String,

    @SerializedName("evOwnerNIC")
    val evOwnerNIC: String,

    @SerializedName("stationId")
    val stationId: String,

    @SerializedName("reservationDate")
    val reservationDate: String
)

/**
 * Data class for QR code validation response
 */
data class QRValidationResponse(
    @SerializedName("isValid")
    val isValid: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("bookingDetails")
    val bookingDetails: Booking? = null,

    @SerializedName("errorCode")
    val errorCode: String? = null
)

/**
 * Data class for QR code data structure
 */
data class QRCodeData(
    @SerializedName("bookingId")
    val bookingId: String,

    @SerializedName("evOwnerNIC")
    val evOwnerNIC: String,

    @SerializedName("stationId")
    val stationId: String,

    @SerializedName("reservationDate")
    val reservationDate: String
)
