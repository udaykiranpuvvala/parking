package com.elite.parking.Model

// VehicleViewModel.kt
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elite.parking.repository.VehicleDetailsRepositoryy
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VehicleDetailViewModel(private val repository: VehicleDetailsRepositoryy) : ViewModel() {

    private val _vehicleList = MutableLiveData<List<VehicleDetail>>()
    val vehicleList: LiveData<List<VehicleDetail>> = _vehicleList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchVehicleDetails(userId: String) {
        _isLoading.value = true

        repository.getVehicleDetails(userId).enqueue(object : Callback<VehicleDetailResponse> {
            override fun onResponse(call: Call<VehicleDetailResponse>, response: Response<VehicleDetailResponse>) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    // No need for flatten(), just directly post the response content
                    _vehicleList.postValue(response.body()!!.content)
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


