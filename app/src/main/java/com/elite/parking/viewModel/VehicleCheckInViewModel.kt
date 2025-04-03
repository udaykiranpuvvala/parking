package com.elite.parking.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elite.parking.Model.VehicleCheckInRequest
import com.elite.parking.Model.VehicleCheckInResponse
import com.elite.parking.Resource
import com.elite.parking.apis.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VehicleCheckInViewModel : ViewModel() {

    private val _vehicleCheckInResponse = MutableLiveData<Resource<VehicleCheckInResponse>>()
    val vehicleCheckInResponse: LiveData<Resource<VehicleCheckInResponse>> get() = _vehicleCheckInResponse

     fun checkIn(authToken: String, vehicleCheckInRequest: VehicleCheckInRequest) {
        _vehicleCheckInResponse.value = Resource.Loading()

        // Make the API call
        ApiService.api.checkIn("Bearer $authToken",vehicleCheckInRequest).enqueue(object : Callback<VehicleCheckInResponse> {
            override fun onResponse(
                call: Call<VehicleCheckInResponse>,
                response: Response<VehicleCheckInResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    _vehicleCheckInResponse.value = Resource.Success(response.body()!!)
                } else {
                    _vehicleCheckInResponse.value = Resource.Failure("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<VehicleCheckInResponse>, t: Throwable) {
                _vehicleCheckInResponse.value = Resource.Failure("Failure: ${t.message}")
                Log.e("VehicleCheckIn", "API Call Failure: ${t.message}")
            }
        })
    }
}

