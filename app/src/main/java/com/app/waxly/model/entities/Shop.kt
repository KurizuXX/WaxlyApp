package com.app.waxly.model.entities

// A simple data class to represent a Shop entity.
data class Shop(
    val id: Int,         // Example: A unique identifier
    val name: String,
    val address: String,
    val latitude: Double,// Example: For map integration
    val longitude: Double,   // Example: For map integration
    val distance: Double
)
