package com.elite.parking.repository

import android.util.Log
import android.widget.Toast
import com.elite.parking.Model.UploadResponse
import com.elite.parking.apis.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File


class FileUploadRepository(private val apiService: ApiService) {

    suspend fun uploadImage(token: String, file: File): Result<UploadResponse> {
        return try {
            Log.e("Upload", "Step 4: Preparing Multipart Request")

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val response = apiService.uploadImage("Bearer $token", body)

            if (response.isSuccessful && response.body() != null) {
                val uploadResponse = response.body()!!
                Log.e("Upload", "Step 5: Success! Image URL: ${uploadResponse.content.url}")
                Result.success(uploadResponse)  // Returning full response
            } else {
                Log.e("Upload", "Step 5: Failed! Error Code: ${response.code()}")
                Result.failure(Exception("Upload failed: ${response.errorBody()?.string()}"))
            }

        } catch (e: Exception) {
            Log.e("Upload", "Step 5: Exception! ${e.message}")
            Result.failure(e)
        }
    }
}


