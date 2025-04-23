package com.mindcoin.valetparking.apis

import com.mindcoin.valetparking.Model.CheckOutRequest
import com.mindcoin.valetparking.Model.LogoutRequest
import com.mindcoin.valetparking.Model.LogoutResponse
import com.mindcoin.valetparking.Model.UpdatePasswordRequest
import com.mindcoin.valetparking.Model.UpdatePasswordResponse
import com.mindcoin.valetparking.Model.UploadResponse
import com.mindcoin.valetparking.Model.VehicleCheckInRequest
import com.mindcoin.valetparking.Model.VehicleCheckInResponse
import com.mindcoin.valetparking.Model.VehicleDetailResponse
import com.mindcoin.valetparking.Model.VehicleDetailsByHookNumberRequest
import com.mindcoin.valetparking.Model.login.LoginRequest
import com.mindcoin.valetparking.Model.login.LoginResponse
import com.mindcoin.valetparking.Model.login.VehicleResponse
import com.mindcoin.valetparking.Model.parkingslots.ParkingResponse
import com.mindcoin.valetparking.selfUpdate.AppSelfUpdate
import com.mindcoin.valetparking.selfUpdate.AppSelfUpdateInputReq
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

    @GET("valetparkingapi/parkingDetails/getByUserId/{userId}")
    fun getVehicleDetails(@Path("userId") userId: String, @Header("Authorization") authToken: String): Call<VehicleResponse>

    @GET("valetparkingapi/parkingDetails/getById/{id}")
    fun getVehicleDetailsById(@Path("id") vehicleId: String,@Header("Authorization") authToken: String): Call<VehicleDetailResponse>

    @POST("valetparkingapi/parkingDetails/checkIn")
    fun checkIn(
        @Header("Authorization") authToken: String,
        @Body vehicleCheckInRequest: VehicleCheckInRequest
    ): Call<VehicleCheckInResponse>

    @POST("valetparkingapi/parkingDetails/checkOut")
    fun checkOut(@Header("Authorization") authToken: String, @Body request: CheckOutRequest): Call<VehicleDetailResponse>

    @POST("valetparkingapi/parkingDetails/getVehicleDetailsByHookNoOrVehicleNo")
     fun getVehicleDetailsbyHookNumber(@Header("Authorization") token: String, @Body request: VehicleDetailsByHookNumberRequest): Call<VehicleDetailResponse>

     @POST("valetparkingapi/auth/user/updatePassword")
    fun upDatePassword(@Header("Authorization") authToken: String, @Body request: UpdatePasswordRequest): Call<UpdatePasswordResponse>


    companion object {
        val api: ApiService = RetrofitClient.create(ApiService::class.java)
        val apiAppUpdate: ApiService = RetrofitClient.createAppUpdate(ApiService::class.java)
    }
    @GET("valetparkingapi/parking/getAllAvailableSlots/{companyId}")
    suspend fun getAvailableSlots(@Path("companyId") companyId: String ,@Header("Authorization") authToken: String): Response<ParkingResponse>


    @Multipart
    @POST("valetparkingapi/local/uploadFile")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>

    @POST("valetparkingapi/auth/user/logout")
     fun logout(@Header("Authorization") authToken: String, @Body logoutRequest: LogoutRequest): Call<LogoutResponse>

    @POST("appLog/addUpdate")
    suspend fun postAppUpdate(@Body request: AppSelfUpdateInputReq): Response<AppSelfUpdate>

}