package com.elite.parking.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elite.parking.Model.CheckOutRequest
import com.elite.parking.Model.UpdatePasswordRequest
import com.elite.parking.Model.UpdatePasswordResponse
import com.elite.parking.Model.VehicleCheckInResponse
import com.elite.parking.Model.VehicleDetail
import com.elite.parking.Model.VehicleDetailResponse
import com.elite.parking.Model.VehicleDetails
import com.elite.parking.Model.VehicleDetailsByHookNumberRequest
import com.elite.parking.Model.login.Vehicle
import com.elite.parking.Model.login.VehicleResponse
import com.elite.parking.Resource
import com.elite.parking.apis.ApiService
import com.elite.parking.repository.VehicleRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.flatten

class VehicleViewModel {
    class VehicleViewModelList(private val repository: VehicleRepository) : ViewModel() {

        private val _vehicleList = MutableLiveData<List<Vehicle>>()
        val vehicleList: LiveData<List<Vehicle>> = _vehicleList

        private val _isLoading = MutableLiveData<Boolean>()
        val isLoading: LiveData<Boolean> = _isLoading

        private val _error = MutableLiveData<String>()
        val error: LiveData<String> = _error

        fun fetchVehicleDetails(userId: String, authToken: String) {
            _isLoading.value = true

            repository.getVehicleDetails(userId, authToken)
                .enqueue(object : Callback<VehicleResponse> {
                    override fun onResponse(
                        call: Call<VehicleResponse>,
                        response: Response<VehicleResponse>
                    ) {
                        _isLoading.value = false
                        if (response.isSuccessful && response.body() != null) {
                            _vehicleList.postValue(response.body()!!.content.flatten())
                        } else {
                            _error.value = "Failed to load data."
                        }
                    }

                    override fun onFailure(call: Call<VehicleResponse>, t: Throwable) {
                        _isLoading.value = false
                        _error.value = t.message ?: "An unknown error occurred."
                    }
                })
        }
    }

    class VehicleViewModelListItem(private val repository: VehicleRepository) : ViewModel() {

        private val _vehicleItem = MutableLiveData<List<VehicleDetail>>()
        val vehicleListItem: LiveData<List<VehicleDetail>> = _vehicleItem

        private val _isLoading = MutableLiveData<Boolean>()
        val isLoading: LiveData<Boolean> = _isLoading

        private val _error = MutableLiveData<String>()
        val error: LiveData<String> = _error

        fun fetchVehicleDetails(userId: String, authToken: String) {
            _isLoading.value = true

            repository.getVehicleDetailsByItem(userId, authToken)
                .enqueue(object : Callback<VehicleDetailResponse> {
                    override fun onResponse(
                        call: Call<VehicleDetailResponse>,
                        response: Response<VehicleDetailResponse>
                    ) {
                        _isLoading.value = false
                        if (response.isSuccessful && response.body() != null) {
                            _vehicleItem.postValue(response.body()!!.content)
                        } else {
                            _error.value = "Failed to load data."
                        }
                    }

                    override fun onFailure(call: Call<VehicleDetailResponse>, t: Throwable) {
                        _isLoading.value = false
                        _error.value = t.message ?: "An unknown error occurred."
                    }
                })
        }
    }

    class VehicleDetailCheckOutViewModel(private val repository: VehicleRepository) : ViewModel() {

        private val _vehicleDetail = MutableLiveData<VehicleDetailResponse>()
        val vehicleDetail: LiveData<VehicleDetailResponse> get() = _vehicleDetail

        private val _isLoading = MutableLiveData<Boolean>()
        val isLoading: LiveData<Boolean> get() = _isLoading

        private val _error = MutableLiveData<String>()
        val error: LiveData<String> get() = _error

        fun checkOutVehicle(authToken: String, checkInId: String, checkOutTime: String) {
            _isLoading.value = true
            val checkOutRequest = CheckOutRequest(checkInId, checkOutTime)

            repository.checkOutVehicle(authToken, checkOutRequest).observeForever { response ->
                _isLoading.value = false
                if (response != null) {
                    _vehicleDetail.postValue(response)
                } else {
                    _error.postValue("Failed to check out the vehicle.")
                }
            }
        }
    }

    class UpDatePasswordViewModel(private val repository: VehicleRepository) : ViewModel() {

        private val _vehicleDetail = MutableLiveData<UpdatePasswordResponse>()
        val upDatePasswordDetail: LiveData<UpdatePasswordResponse> get() = _vehicleDetail

        private val _isLoading = MutableLiveData<Boolean>()
        val isLoading: LiveData<Boolean> get() = _isLoading

        private val _error = MutableLiveData<String>()
        val error: LiveData<String> get() = _error

        fun updatePasswordReq(authToken: String, password: String, userId: String,newpassword: String) {
            _isLoading.value = true
            val passRequest = UpdatePasswordRequest(password, userId,newpassword)

            repository.upDatePassword(authToken, passRequest).observeForever { response ->
                _isLoading.value = false
                if (response != null) {
                    _vehicleDetail.postValue(response)
                } else {
                    _error.postValue("Failed to Update the Password.")
                }
            }
        }
    }


    class VehicleDetailsbyHookNumberViewModel : ViewModel() {
        private val _vehicleCheckInResponse = MutableLiveData<Resource<VehicleDetailResponse>>()
        val vehicleCheckInResponse: LiveData<Resource<VehicleDetailResponse>> get() = _vehicleCheckInResponse

        fun getVehicleDetailsbyHookNumber(
            authToken: String,
            request: VehicleDetailsByHookNumberRequest
        ) {
            _vehicleCheckInResponse.value = Resource.Loading()

            // Make the API call
            ApiService.api.getVehicleDetailsbyHookNumber("Bearer $authToken", request)
                .enqueue(object : Callback<VehicleDetailResponse> {
                    override fun onResponse(
                        call: Call<VehicleDetailResponse>,
                        response: Response<VehicleDetailResponse>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            _vehicleCheckInResponse.value = Resource.Success(response.body()!!)
                        } else {
                            _vehicleCheckInResponse.value =
                                Resource.Failure("Error: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<VehicleDetailResponse>, t: Throwable) {
                        _vehicleCheckInResponse.value = Resource.Failure("Failure: ${t.message}")
                    }
                })
        }
    }

}
