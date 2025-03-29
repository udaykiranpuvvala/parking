package com.elite.parking.Model

data class Vehicle(
    val vehicleNumber: String,
    val make: String,
    val model: String,
    val hookNumber: String,
    val status: String,
    val imageUrl: Int ? = null
)