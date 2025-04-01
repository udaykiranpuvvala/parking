package com.elite.parking.Model


data class VehicleDetailResponse(
    val mssg: String,
    val content: List<VehicleDetail>,
    val status: Int
)

data class VehicleDetail(
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
