package com.elite.parking.apis

import com.elite.parking.Model.VehicleCheckInRequest
import com.elite.parking.Model.VehicleCheckInResponse
import com.elite.parking.Model.VehicleDetailResponse
import com.elite.parking.Model.login.LoginRequest
import com.elite.parking.Model.login.LoginResponse
import com.elite.parking.Model.login.VehicleResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("dservicevaletparkingapi/auth/user/loginByMobilePassword")
    fun loginByMobilePassword(@Body request: LoginRequest): Call<LoginResponse>

    @GET("dservicevaletparkingapi/vehicleDetails/getByUserId/{userId}")
    fun getVehicleDetails(@Path("userId") userId: String): Call<VehicleResponse>

    @GET("dservicevaletparkingapi/vehicleDetails/getById/{id}")
     fun getVehicleDetailsById(@Path("id") vehicleId: String): Call<VehicleDetailResponse>

    @POST("dservicevaletparkingapi/vehicleDetails/checkIn")
    fun checkIn(@Body vehicleCheckInRequest: VehicleCheckInRequest): Call<VehicleCheckInResponse>

    companion object {
        val api: ApiService = RetrofitClient.create(ApiService::class.java)
    }
}