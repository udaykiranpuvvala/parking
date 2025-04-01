package com.elite.parking.Model

data class ParkingSlot(
    val slotId: String,
    val isAvailable: Boolean // True if available, false if not available
)