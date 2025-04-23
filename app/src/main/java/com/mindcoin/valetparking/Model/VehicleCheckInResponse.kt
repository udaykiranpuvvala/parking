package com.mindcoin.valetparking.Model

data class VehicleCheckInResponse(
    val mssg: String,
    val content: List<VehicleDetails>,
    val status: Int
)

data class VehicleDetails(
    val uuid: String,
    val parkingId: String,
    val userId: String,
    val vehicleNo: String,
    val vehicleType: String,
    val hookNo: String,
    val notes: String,
    val imageUrl: String,
    val inTime: String,
    val outTime: String?,
    val createdDate: String,
    val modifiedDate: String,
    val status: Int
)