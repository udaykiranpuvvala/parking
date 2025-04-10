package com.elite.parking.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elite.parking.Model.parkingslots.ListItem
import com.elite.parking.Model.parkingslots.ParkingResponse
import com.elite.parking.Model.parkingslots.ParkingSlot
import com.elite.parking.apis.ApiService
import com.elite.parking.repository.ParkingRepository
import kotlinx.coroutines.launch

class ParkingViewModel : ViewModel() {

    private val repository = ParkingRepository(ApiService.api)

    private val _parkingSlots = MutableLiveData<List<ListItem>>()
    val parkingSlots: LiveData<List<ListItem>> get() = _parkingSlots

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchParkingSlots(companyId: String, authToken: String) {
        viewModelScope.launch {
            try {
                val response = repository.getAvailableParkingSlots(companyId, authToken)
                if (response.isSuccessful && response.body() != null) {
                    val slots = response.body()!!.content
                    val groupedList = groupByFloor(slots)
                    _parkingSlots.postValue(groupedList)
                } else {
                    _errorMessage.postValue("Failed to load data")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error: ${e.message}")
            }
        }
    }

    /* private fun groupByFloor(slots: List<ParkingSlot>): List<ListItem> {
         val groupedList = mutableListOf<ListItem>()
         val floorMap = slots.groupBy { it.floorNo }

         for ((floorNo, slots) in floorMap) {
             groupedList.add(ListItem.Header(floorNo))
             slots.forEach { groupedList.add(ListItem.ParkingSlotItem(it)) }
         }

         return groupedList
     }*/
    private fun groupByFloor(slots: List<ParkingSlot>): List<ListItem> {
        val groupedList = mutableListOf<ListItem>()
        val blockMap = slots.groupBy { it.blockNo }

        for ((blockNo, floorSlots) in blockMap) {
            groupedList.add(ListItem.FloorHeader(blockNo))

            val floorMap = floorSlots.groupBy { it.floorNo }
            for ((floorNo, floorSlots) in floorMap) {
                groupedList.add(ListItem.BLockHeader(floorNo))

                floorSlots.forEach {
                    groupedList.add(ListItem.ParkingSlotItem(it))
                }
            }
        }
        return groupedList
    }

    class ParkingViewModelData() : ViewModel() {

        private val parkingRepository: ParkingRepository = ParkingRepository(ApiService.api)

        private val _parkingResponse = MutableLiveData<ParkingResponse>()
        val parkingResponse: LiveData<ParkingResponse> get() = _parkingResponse

        fun getAvailableSlotsData(companyId: String, token: String) {
            viewModelScope.launch {
                val response = parkingRepository.getAvailableSlotsData(companyId, token)
                _parkingResponse.value = response as ParkingResponse?
            }
        }
    }

}