package com.mindcoin.valetparking.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindcoin.valetparking.Model.parkingslots.ListItem
import com.mindcoin.valetparking.Model.parkingslots.ParkingResponse
import com.mindcoin.valetparking.Model.parkingslots.ParkingSlot
import com.mindcoin.valetparking.apis.ApiService
import com.mindcoin.valetparking.repository.ParkingRepository
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
        val blockMap = slots.groupBy { it.blockNoId }

        for ((blockNo, floorSlots) in blockMap) {
            groupedList.add(ListItem.FloorHeader(blockNo))

            val floorMap = floorSlots.groupBy { it.floorNoId }
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

        private val _isLoading = MutableLiveData<Boolean>()
        val isLoading: LiveData<Boolean> get() = _isLoading


        private val _error = MutableLiveData<String>()
        val error: LiveData<String> get() = _error

        fun getAvailableSlotsData(companyId: String, token: String) {
            _isLoading.value = true
            viewModelScope.launch {
                val response = parkingRepository.getAvailableSlotsData(companyId, token)

                _isLoading.value = false
                if (response != null) {
                    _parkingResponse.value = response as ParkingResponse?
                } else {
                    _error.postValue("Failed to Update the Password.")
                }


            }
        }
    }

}