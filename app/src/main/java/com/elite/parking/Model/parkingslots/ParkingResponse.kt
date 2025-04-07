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
    data class BLockHeader(val blockNo: String) : ListItem()
    data class FloorHeader(val floorNo: String) : ListItem()
    data class ParkingSlotItem(val slot: ParkingSlot) : ListItem()

  /*  data class FloorHeader(val floorNo: String) : ListItem()
    data class BLockHeader(val blockNo: String, val parkingSlots: List<ParkingSlotItem>) : ListItem()
    data class ParkingSlotItem(val slot: ParkingSlot) : ListItem()
    data class ParkingSlot(val parkingNo: String, val availabilityStatus: Int)*/
}

