package com.example.evstationmobileapp.models

import com.google.gson.annotations.SerializedName

data class Station(
    @SerializedName("id")
    val id: String,

    @SerializedName("stationName")
    val stationName: String,

    @SerializedName("stationCode")
    val stationCode: String,

    @SerializedName("location")
    val location: Location,

    @SerializedName("stationType")
    val stationType: String,

    @SerializedName("noOfSlots")
    val noOfSlots: Int,

    @SerializedName("phoneNumber")
    val phoneNumber: String,

    @SerializedName("operatingHours")
    val operatingHours: OperatingHours,

    @SerializedName("status")
    val status: String,

    @SerializedName("operatorIds")
    val operatorIds: List<String> = emptyList(),

    @SerializedName("operators")
    val operators: List<Operator> = emptyList(),

    @SerializedName("slots")
    val slots: List<StationSlot> = emptyList()
)


data class Operator(
    @SerializedName("id")
    val id: String,

    @SerializedName("firstName")
    val firstName: String,

    @SerializedName("lastName")
    val lastName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("phoneNumber")
    val phoneNumber: String,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("status")
    val status: String,

    @SerializedName("stationId")
    val stationId: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("lastLogin")
    val lastLogin: String? = null,

    @SerializedName("profileImage")
    val profileImage: String? = null
)
