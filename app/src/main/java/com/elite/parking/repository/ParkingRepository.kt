package com.elite.parking.repository

import com.elite.parking.Model.login.VehicleResponse
import com.elite.parking.Model.parkingslots.ParkingResponse
import com.elite.parking.apis.ApiService
import com.elite.parking.apis.RetrofitClient
import retrofit2.Call
import retrofit2.Response

class ParkingRepository (private val apiService: ApiService) {

    suspend fun getAvailableParkingSlots(companyId : String,authToken: String): Response<ParkingResponse> {
        return apiService.getAvailableSlots(companyId,"Bearer $authToken")
    }
}