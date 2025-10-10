package com.example.evstationmobileapp.models

/**
 * Data Transfer Object for creating a new EV Owner
 * This matches the backend CreateEVOwnerDto structure
 */
data class CreateEVOwnerDto(
    val nic: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val password: String
)
