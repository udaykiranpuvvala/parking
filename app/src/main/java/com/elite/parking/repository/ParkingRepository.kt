package com.elite.parking.repository

import com.elite.parking.Model.login.VehicleResponse
import com.elite.parking.Model.parkingslots.ParkingResponse
import com.elite.parking.apis.ApiService
import com.elite.parking.apis.RetrofitClient
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response

class ParkingRepository (private val apiService: ApiService) {

    suspend fun getAvailableParkingSlots(companyId : String,authToken: String): Response<ParkingResponse> {
        return apiService.getAvailableSlots(companyId,"Bearer $authToken")
    }

    suspend fun getAvailableSlotsData(companyId: String,authToken: String): Any? {
        return try {
            // Make the API call and get the response as an ApiResponse object directly
            val response = apiService.getAvailableSlots(companyId,"Bearer $authToken")

            // Check if the response is successful
            if (response.isSuccessful) {
                return response.body() // Return the parsed ApiResponse object
            } else {
                null // Return null if the response is not successful
            }
        } catch (e: Exception) {
            // Handle error and return null if any exception occurs
            null
        }
    }

}