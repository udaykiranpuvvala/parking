package com.mindcoin.valetparking.selfUpdate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class AppUpdateViewModel : ViewModel() {
    private val repository = AppUpdateRepository()

    private val _updateInfo = MutableLiveData<AppSelfUpdate?>()
    val updateInfo: LiveData<AppSelfUpdate?> = _updateInfo

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun sendUpdateRequest(request: AppSelfUpdateInputReq) {
        viewModelScope.launch {
            val result = repository.postAppUpdate(request)
            result.onSuccess {
                _updateInfo.value = it
            }.onFailure {
                _error.value = it.message
            }
        }
    }
}