package com.elite.parking.Model

data class VehicleCheckInRequest(
    val parkingId: String,
    val userId: String,
    val vehicleNo: String,
    val vehicleType: String,
    val hookNo: String,
    val notes: String,
    val inTime: String,
    val imageUrl: String,
    val createdDate: String,
    val modifiedDate: String,
    val status: Int
)