package com.mindcoin.valetparking.apis

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.mindcoinapps.com/"
    private const val BASE_URL_APP_UPDATE = "https://api.mindcoinapps.com/appmanagementapi/"

    val instance: Retrofit by lazy {
        // Create a logging interceptor
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Logs request and response body
        }

        // Create OkHttpClient with the interceptor
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        // Create Retrofit instance with OkHttpClient
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)  // Set OkHttpClient with logging interceptor
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> create(service: Class<T>): T {
        return instance.create(service)
    }

    val instanceAppUpdate: Retrofit by lazy {
        val loggingInterceptorAppUpdate = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val okHttpClientAppUpdate = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptorAppUpdate)
            .build()
        Retrofit.Builder()
            .baseUrl(BASE_URL_APP_UPDATE)
            .client(okHttpClientAppUpdate)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> createAppUpdate(service: Class<T>): T {
        return instanceAppUpdate.create(service)
    }
}