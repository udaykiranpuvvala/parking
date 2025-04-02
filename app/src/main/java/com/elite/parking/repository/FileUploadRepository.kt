package com.elite.parking.repository

import com.elite.parking.Model.UploadResponse
import com.elite.parking.apis.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File


class FileUploadRepository(private val apiService: ApiService) {

    suspend fun uploadImage(token: String, file: File): Result<String> {
        return try {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val response = apiService.uploadImage(token, body)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.content.url)
            } else {
                Result.failure(Exception("Upload failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
