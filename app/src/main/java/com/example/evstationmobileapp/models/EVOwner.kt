package com.example.evstationmobileapp.models

data class EVOwner(
    val nic: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val password: String,
    val isActive: Boolean = true
)