package com.elite.parking.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elite.parking.Model.login.Vehicle
import com.elite.parking.Model.login.VehicleResponse
import com.elite.parking.repository.VehicleRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VehicleViewModel(private val repository: VehicleRepository) : ViewModel() {

    private val _vehicleList = MutableLiveData<List<Vehicle>>()
    val vehicleList: LiveData<List<Vehicle>> = _vehicleList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchVehicleDetails(userId: String) {
        _isLoading.value = true

        repository.getVehicleDetails(userId).enqueue(object : Callback<VehicleResponse> {
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