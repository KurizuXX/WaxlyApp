package com.app.waxly.model.entities

// A simple data class to represent a Shop entity.
data class Shop(
    val id: Int,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val distance: Double // Assuming this is calculated and added
)