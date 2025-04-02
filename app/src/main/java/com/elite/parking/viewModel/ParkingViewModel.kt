package com.elite.parking.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elite.parking.Model.parkingslots.ListItem
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

    fun fetchParkingSlots(authToken: String) {
        viewModelScope.launch {
            try {
                val response = repository.getAvailableParkingSlots(authToken)
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

    private fun groupByFloor(slots: List<ParkingSlot>): List<ListItem> {
        val groupedList = mutableListOf<ListItem>()
        val floorMap = slots.groupBy { it.floorNo }

        for ((floorNo, slots) in floorMap) {
            groupedList.add(ListItem.Header(floorNo))
            slots.forEach { groupedList.add(ListItem.ParkingSlotItem(it)) }
        }

        return groupedList
    }
}