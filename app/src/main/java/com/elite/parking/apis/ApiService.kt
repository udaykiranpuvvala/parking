package com.elite.parking.apis

import com.elite.parking.Model.CheckOutRequest
import com.elite.parking.Model.LogoutRequest
import com.elite.parking.Model.LogoutResponse
import com.elite.parking.Model.UpdatePasswordRequest
import com.elite.parking.Model.UpdatePasswordResponse
import com.elite.parking.Model.UploadResponse
import com.elite.parking.Model.VehicleCheckInRequest
import com.elite.parking.Model.VehicleCheckInResponse
import com.elite.parking.Model.VehicleDetail
import com.elite.parking.Model.VehicleDetailResponse
import com.elite.parking.Model.VehicleDetailsByHookNumberRequest
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
import retrofit2.http.Query

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
}