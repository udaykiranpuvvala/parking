package com.elite.parking.apis

import com.elite.parking.Model.Vehicle
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("users")
    fun getUsers(): Call<List<Vehicle>>
}