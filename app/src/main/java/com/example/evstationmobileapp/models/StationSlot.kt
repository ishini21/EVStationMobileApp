package com.example.evstationmobileapp.models

import com.google.gson.annotations.SerializedName

data class StationSlot(
    @SerializedName("id")
    val id: String,

    @SerializedName("stationId")
    val stationId: String,

    @SerializedName("slotCode")
    val slotCode: String,

    @SerializedName("connectorType")
    val connectorType: ConnectorType,

    @SerializedName("powerRating")
    val powerRating: PowerRating,

    @SerializedName("pricePerKWh")
    val pricePerKWh: Double,

    @SerializedName("status")
    val status: SlotStatus
)

enum class ConnectorType {
    @SerializedName("CHAdeMO_CCS2_DualPort")
    CHAdeMO_CCS2_DualPort,

    @SerializedName("CCS2_SinglePort")
    CCS2_SinglePort,

    @SerializedName("CHAdeMO_SinglePort")
    CHAdeMO_SinglePort,

    @SerializedName("Type2")
    Type2
}

enum class SlotStatus {
    @SerializedName("Available")
    Available,

    @SerializedName("Occupied")
    Occupied,

    @SerializedName("Maintenance")
    Maintenance
}

enum class PowerRating(val value: Int) {
    @SerializedName("kW22")
    kW22(22),

    @SerializedName("kW30")
    kW30(30),

    @SerializedName("kW50")
    kW50(50),

    @SerializedName("kW100")
    kW100(100)
}

data class UpdateSlotAvailabilityDto(
    @SerializedName("slotStatus")
    val slotStatus: SlotStatus
)