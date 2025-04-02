package com.elite.parking.repository

import com.elite.parking.Model.login.VehicleResponse
import com.elite.parking.apis.ApiService
import retrofit2.Call

class VehicleRepository(private val apiService: ApiService) {
    fun getVehicleDetails(userId: String,authToken : String): Call<VehicleResponse> {
        return apiService.getVehicleDetails(userId,"Bearer $authToken")
    }
}
