package com.elite.parking.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elite.parking.Model.CheckOutRequest
import com.elite.parking.Model.VehicleDetail
import com.elite.parking.Model.VehicleDetailResponse
import com.elite.parking.Model.login.Vehicle
import com.elite.parking.Model.login.VehicleResponse
import com.elite.parking.repository.VehicleRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.flatten

class VehicleViewModel{
    class VehicleViewModelList(private val repository: VehicleRepository) : ViewModel() {

        private val _vehicleList = MutableLiveData<List<Vehicle>>()
        val vehicleList: LiveData<List<Vehicle>> = _vehicleList

        private val _isLoading = MutableLiveData<Boolean>()
        val isLoading: LiveData<Boolean> = _isLoading

        private val _error = MutableLiveData<String>()
        val error: LiveData<String> = _error

        fun fetchVehicleDetails(userId: String,authToken: String) {
            _isLoading.value = true

            repository.getVehicleDetails(userId, authToken).enqueue(object : Callback<VehicleResponse> {
                override fun onResponse(call: Call<VehicleResponse>, response: Response<VehicleResponse>) {
                    _isLoading.value = false
                    if (response.isSuccessful && response.body() != null) {
                        _vehicleList.postValue(response.body()!!.content.flatten())
                    } else {
                        _error.value = "Failed to load data."
                    }
                }

                override fun onFailure(call: Call<VehicleResponse>, t: Throwable) {
                    _isLoading.value = false
                    _error.value = t.message ?: "An unknown error occurred."
                }
            })
        }
    }
    class VehicleViewModelListItem(private val repository: VehicleRepository) : ViewModel() {

        private val _vehicleItem = MutableLiveData<List<VehicleDetail>>()
        val vehicleListItem: LiveData<List<VehicleDetail>> = _vehicleItem

        private val _isLoading = MutableLiveData<Boolean>()
        val isLoading: LiveData<Boolean> = _isLoading

        private val _error = MutableLiveData<String>()
        val error: LiveData<String> = _error

        fun fetchVehicleDetails(userId: String,authToken: String) {
            _isLoading.value = true

            repository.getVehicleDetailsByItem(userId, authToken).enqueue(object : Callback<VehicleDetailResponse> {
                override fun onResponse(call: Call<VehicleDetailResponse>, response: Response<VehicleDetailResponse>) {
                    _isLoading.value = false
                    if (response.isSuccessful && response.body() != null) {
                        _vehicleItem.postValue(response.body()!!.content)
                    } else {
                        _error.value = "Failed to load data."
                    }
                }
                override fun onFailure(call: Call<VehicleDetailResponse>, t: Throwable) {
                    _isLoading.value = false
                    _error.value = t.message ?: "An unknown error occurred."
                }
            })
        }
    }


    class VehicleDetailViewModel(private val repository: VehicleRepository) : ViewModel() {

        private val _vehicleDetail = MutableLiveData<VehicleDetailResponse>()
        val vehicleDetail: LiveData<VehicleDetailResponse> get() = _vehicleDetail

        private val _isLoading = MutableLiveData<Boolean>()
        val isLoading: LiveData<Boolean> get() = _isLoading

        private val _error = MutableLiveData<String>()
        val error: LiveData<String> get() = _error

        fun checkOutVehicle(authToken: String, checkInId: String, checkOutTime: String) {
            _isLoading.value = true
            val checkOutRequest = CheckOutRequest(checkInId, checkOutTime)

            repository.checkOutVehicle(authToken, checkOutRequest).observeForever { response ->
                _isLoading.value = false
                if (response != null) {
                    _vehicleDetail.postValue(response)
                } else {
                    _error.postValue("Failed to check out the vehicle.")
                }
            }
        }
    }

}
