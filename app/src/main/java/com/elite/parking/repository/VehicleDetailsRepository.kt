package com.elite.parking.repository

import com.elite.parking.Model.VehicleDetailResponse
import com.elite.parking.Model.login.VehicleResponse
import com.elite.parking.apis.ApiService
import retrofit2.Call
import retrofit2.Response

class VehicleDetailsRepositoryy(private val apiService: ApiService) {
    fun getVehicleDetails(vehicleId: String): Call<VehicleDetailResponse> {
        return apiService.getVehicleDetailsById(vehicleId)
    }
}