package com.elite.parking.Model.parkingslots


data class ParkingResponse(
    val mssg: String,
    val content: List<ParkingSlot>
)

data class ParkingSlot(
    val parkingNo: String,
    val companyId: String,
    val blockNoId : String,
    val blockName : String,
    val floorNoId : String,
    val floorName : String,
    val modifiedDate: String?,
    val createdBy: String,
    val uuid: String,
    val availabilityStatus: Int,
    val hookNo: String?,
    val status: Int,
    val type: Int,
    val createdDate: String
)

// For sectioned list with headers
sealed class ListItem {
    data class BLockHeader(val blockName: String) : ListItem()
    data class FloorHeader(val floorName: String) : ListItem()
    data class ParkingSlotItem(val slot: ParkingSlot) : ListItem()

  /*  data class FloorHeader(val floorNo: String) : ListItem()
    data class BLockHeader(val blockNo: String, val parkingSlots: List<ParkingSlotItem>) : ListItem()
    data class ParkingSlotItem(val slot: ParkingSlot) : ListItem()
    data class ParkingSlot(val parkingNo: String, val availabilityStatus: Int)*/
}
data class Block(
    val blockName: String?="",
    val floors: List<Floor>,
    var selectedFloor: Floor? = null // NEW
)


data class Floor(
    val floorName: String?="",
    val slots: List<ParkingSlots>,
    var isExpanded: Boolean = false // default collapsed
)



data class ParkingSlots(
    val parkingNo: String?="",
    val availabilityStatus: Int,
    val type: Int,
    val uuid: String
)



