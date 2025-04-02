package com.elite.parking

import com.elite.parking.Model.ParkingSlot

interface OnParkingSlotSelectedListener {
    fun onParkingSlotSelected(parkingSlot: ParkingSlot)
}
