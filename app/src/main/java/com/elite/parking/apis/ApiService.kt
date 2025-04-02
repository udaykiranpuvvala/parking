package com.elite.parking.apis

import com.elite.parking.Model.UploadResponse
import com.elite.parking.Model.VehicleCheckInRequest
import com.elite.parking.Model.VehicleCheckInResponse
import com.elite.parking.Model.VehicleDetailResponse
import com.elite.parking.Model.login.LoginRequest
import com.elite.parking.Model.login.LoginResponse
import com.elite.parking.Model.login.VehicleResponse
import com.elite.parking.Model.parkingslots.ParkingResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @POST("valetparkingapi/auth/user/loginByMobilePassword")
    fun loginByMobilePassword(@Body request: LoginRequest): Call<LoginResponse>

    @GET("valetparkingapi/vehicleDetails/getByUserId/{userId}")
    fun getVehicleDetails(@Path("userId") userId: String,@Header("Authorization") authToken: String): Call<VehicleResponse>

    @GET("valetparkingapi/vehicleDetails/getById/{id}")
     fun getVehicleDetailsById(@Path("id") vehicleId: String): Call<VehicleDetailResponse>

    @POST("valetparkingapi/vehicleDetails/checkIn")
    fun checkIn(@Header("Authorization") authToken: String, @Body vehicleCheckInRequest: VehicleCheckInRequest): Call<VehicleCheckInResponse>

    companion object {
        val api: ApiService = RetrofitClient.create(ApiService::class.java)
    }

    @GET("valetparkingapi/parking/getAllAvailableSlots")
    suspend fun getAvailableSlots(@Header("Authorization") authToken: String
    ): Response<ParkingResponse>


        @Multipart
        @POST("valetparkingapi/local/uploadFile")
        suspend fun uploadImage(
            @Header("Authorization") token: String,
            @Part file: MultipartBody.Part
        ): Response<UploadResponse>
}