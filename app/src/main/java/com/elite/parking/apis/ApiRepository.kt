package com.elite.parking.apis

import com.elite.parking.Model.login.LoginRequest
import com.elite.parking.Model.login.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ApiRepository {

    private val apiService: ApiService = RetrofitClient.instance.create(ApiService::class.java)

    fun login(username: String, password: String, osType: Int, callback: ApiCallback) {
        val request = LoginRequest(username, password, osType)

        apiService.loginByMobilePassword(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    callback.onSuccess(response.body()!!)
                } else {
                    callback.onFailure("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                callback.onFailure(t.message ?: "Unknown error")
            }
        })
    }
}
