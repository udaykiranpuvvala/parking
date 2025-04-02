package com.elite.parking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.elite.parking.repository.FileUploadRepository
import com.elite.parking.viewModel.FileUploadViewModel


class ViewModelFactory(private val repository: FileUploadRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FileUploadViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FileUploadViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
