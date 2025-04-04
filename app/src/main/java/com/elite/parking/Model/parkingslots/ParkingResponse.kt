package com.elite.parking.Model.parkingslots


data class ParkingResponse(
    val mssg: String,
    val content: List<ParkingSlot>
)

data class ParkingSlot(
    val uuid: String,
    val blockNo: String,
    val floorNo: String,
    val companyId: String,
    val parkingNo: String,
    val availabilityStatus: Int, // 2 means notavailable
    val type: String,
    val status: Int
)

// For sectioned list with headers
sealed class ListItem {
    data class Header(val floorNo: String) : ListItem()
    data class ParkingSlotItem(val slot: ParkingSlot) : ListItem()
}

