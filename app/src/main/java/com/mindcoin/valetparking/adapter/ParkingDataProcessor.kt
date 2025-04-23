package com.mindcoin.valetparking.adapter

object ParkingDataProcessor {
    /*fun processToHierarchy(response: ParkingResponse): List<Block> {
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
    }*/
}