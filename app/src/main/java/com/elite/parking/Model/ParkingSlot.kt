package com.elite.parking.Model

data class ParkingSlot(
    val name: String,
    val isAvailable: Boolean,
    var isSelected: Boolean = false)
