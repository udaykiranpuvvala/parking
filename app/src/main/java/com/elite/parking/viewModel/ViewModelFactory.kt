package com.elite.parking.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.elite.parking.repository.FileUploadRepository

class ViewModelFactory{
    class ViewModelFactoryFileUploadRepository(private val repository: FileUploadRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FileUploadViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FileUploadViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}