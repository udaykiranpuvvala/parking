package com.elite.parking.apis

import com.elite.parking.Model.Vehicle
import com.elite.parking.Model.login.LoginRequest
import com.elite.parking.Model.login.LoginResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("dservicevaletparkingapi/auth/user/loginByMobilePassword")
    fun loginByMobilePassword(@Body request: LoginRequest): Call<LoginResponse>
}