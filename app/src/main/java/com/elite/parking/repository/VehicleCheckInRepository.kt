package com.elite.parking.repository

import com.elite.parking.Model.VehicleCheckInRequest
import com.elite.parking.Model.VehicleCheckInResponse
import com.elite.parking.apis.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VehicleCheckInRepository {

    private val api = ApiService.api

    fun checkInVehicle(
        vehicleCheckInRequest: VehicleCheckInRequest,
        callback: (VehicleCheckInResponse?, String?) -> Unit
    ) {
        api.checkIn(vehicleCheckInRequest).enqueue(object : Callback<VehicleCheckInResponse> {
            override fun onResponse(
                call: Call<VehicleCheckInResponse>,
                response: Response<VehicleCheckInResponse>
            ) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<VehicleCheckInResponse>, t: Throwable) {
                callback(null, "Failure: ${t.message}")
            }
        })
    }
}
