package com.elite.parking.repository

import com.elite.parking.Model.LogoutRequest
import com.elite.parking.Model.LogoutResponse
import com.elite.parking.Model.login.LoginRequest
import com.elite.parking.Model.login.LoginResponse
import com.elite.parking.apis.ApiCallback
import com.elite.parking.apis.ApiService
import com.elite.parking.apis.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository() {
    private val apiService: ApiService = RetrofitClient.instance.create(ApiService::class.java)

    fun login(userid: String, time: String, osType: Int, callback: ApiCallback) {
        val request = LogoutRequest(userid, time, osType)

    }
}


