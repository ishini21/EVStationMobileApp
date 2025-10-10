package com.example.evstationmobileapp.models

/**
 * Data Transfer Object for updating an existing EV Owner
 * This matches the backend UpdateEVOwnerDto structure
 */
data class UpdateEVOwnerDto(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val password: String? = null // Optional password update
)
