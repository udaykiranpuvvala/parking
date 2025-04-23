package com.mindcoin.valetparking.selfUpdate

import com.mindcoin.valetparking.apis.ApiService

class AppUpdateRepository {
    suspend fun postAppUpdate(request: AppSelfUpdateInputReq): Result<AppSelfUpdate> {
        return try {
            val response = ApiService.apiAppUpdate.postAppUpdate(request)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Server Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
