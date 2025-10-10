package com.example.evstationmobileapp.models

/**
 * Data Transfer Object for EV Owner response from the API
 * This matches the backend EVOwnerDto structure
 */
data class EVOwnerDto(
    val nic: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val isActive: Boolean
)
