package com.elite.parking.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elite.parking.repository.FileUploadRepository
import kotlinx.coroutines.launch
import java.io.File

class FileUploadViewModel(private val repository: FileUploadRepository) : ViewModel() {

    private val _uploadResult = MutableLiveData<Result<String>>()
    val uploadResult: LiveData<Result<String>> = _uploadResult

    fun uploadImage(token: String, file: File) {
        Log.e("Response Body","Step 2 uploadImage()")
        viewModelScope.launch {
            Log.e("Response Body","Step 3 launch")
            val result = repository.uploadImage(token, file)
            _uploadResult.postValue(result)
        }
    }
}



