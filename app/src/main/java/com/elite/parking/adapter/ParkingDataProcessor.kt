package com.elite.parking.adapter

import com.elite.parking.Model.parkingslots.Block
import com.elite.parking.Model.parkingslots.Floor
import com.elite.parking.Model.parkingslots.ParkingResponse

object ParkingDataProcessor {
    fun processToHierarchy(response: ParkingResponse): List<Block> {
        return response.content
            .groupBy { it.blockNo }
            .map { (blockNo, slots) ->
                Block(
                    blockNo = blockNo,
                    floors = slots.groupBy { it.floorNo }
                        .map { (floorNo, floorSlots) ->
                            Floor(
                                floorNo = floorNo,
                                slots = floorSlots.sortedBy { it.parkingNo }
                            )
                        }.sortedBy { it.floorNo }
                )
            }.sortedBy { it.blockNo }
    }
}