package com.elite.parking.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elite.parking.Model.UploadResponse
import com.elite.parking.repository.FileUploadRepository
import kotlinx.coroutines.launch
import java.io.File

class FileUploadViewModel(private val repository: FileUploadRepository) : ViewModel() {

    private val _uploadResult = MutableLiveData<Result<UploadResponse>>()
    val uploadResult: LiveData<Result<UploadResponse>> = _uploadResult

    fun uploadImage(token: String, file: File) {
        Log.e("Upload", "Step 2: uploadImage() Called")
        viewModelScope.launch {
            Log.e("Upload", "Step 3: Launch Coroutine")

            try {
                val result = repository.uploadImage(token, file)

                result.onSuccess { response ->
                    Log.e("Upload Success", "Response: ${response.mssg}, URL: ${response.content.url}")
                    _uploadResult.postValue(Result.success(response))
                }

                result.onFailure { error ->
                    Log.e("Upload Error", "Failed: ${error.message}")
                    _uploadResult.postValue(Result.failure(error))
                }

            } catch (e: Exception) {
                Log.e("Upload Exception", "Error: ${e.message}")
                _uploadResult.postValue(Result.failure(e))
            }
        }
    }
}





