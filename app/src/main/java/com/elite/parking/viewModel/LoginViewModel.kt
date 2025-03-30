package com.elite.parking.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elite.parking.Model.login.LoginRequest
import com.elite.parking.Model.login.LoginResponse
import com.elite.parking.apis.ApiCallback
import com.elite.parking.apis.ApiRepository
import com.elite.parking.apis.ApiService
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {

    private val apiRepository = ApiRepository()
    private val _apiResponse = MutableLiveData<LoginResponse>()
    val apiResponse: LiveData<LoginResponse> get() = _apiResponse

    fun login(username: String, password: String, osType: Int) {
        apiRepository.login(username, password, osType, object : ApiCallback {
            override fun onSuccess(response: LoginResponse) {
                _apiResponse.value = response
            }

            override fun onFailure(errorMessage: String) {
                // Handle failure here if needed
            }
        })
    }
}