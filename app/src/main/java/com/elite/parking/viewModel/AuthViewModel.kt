package com.elite.parking.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elite.parking.Model.LogoutRequest
import com.elite.parking.Model.LogoutResponse
import com.elite.parking.Model.VehicleCheckInRequest
import com.elite.parking.Model.VehicleCheckInResponse
import com.elite.parking.Model.login.LoginResponse
import com.elite.parking.Resource
import com.elite.parking.apis.ApiService
import com.elite.parking.repository.AuthRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AuthViewModel(): ViewModel() {

    private val logoutResponse = MutableLiveData<Resource<LogoutResponse>>()
    val response: LiveData<Resource<LogoutResponse>> get() = logoutResponse

    fun logout(authToken: String, response: LogoutRequest) {
        logoutResponse.value = Resource.Loading()

        ApiService.api.logout("Bearer $authToken",response).enqueue(object : Callback<LogoutResponse> {
            override fun onResponse(
                call: Call<LogoutResponse>,
                response: Response<LogoutResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    logoutResponse.value = Resource.Success(response.body()!!)
                } else {
                    logoutResponse.value = Resource.Failure("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                logoutResponse.value = Resource.Failure("Failure: ${t.message}")
                Log.e("VehicleCheckIn", "API Call Failure: ${t.message}")
            }
        })
    }




}


