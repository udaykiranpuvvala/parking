package com.mindcoin.valetparking.repository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mindcoin.valetparking.Model.CheckOutRequest
import com.mindcoin.valetparking.Model.UpdatePasswordRequest
import com.mindcoin.valetparking.Model.UpdatePasswordResponse
import com.mindcoin.valetparking.Model.VehicleDetailResponse
import com.mindcoin.valetparking.Model.login.VehicleResponse
import com.mindcoin.valetparking.apis.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VehicleRepository(private val apiService: ApiService) {
    fun getVehicleDetails(userId: String,authToken : String): Call<VehicleResponse> {
        return apiService.getVehicleDetails(userId,"Bearer $authToken")
    }

    fun getVehicleDetailsByItem(id: String,authToken : String): Call<VehicleDetailResponse> {
        return apiService.getVehicleDetailsById(id,"Bearer $authToken")
    }

    fun checkOutVehicle(authToken: String, checkOutRequest: CheckOutRequest): LiveData<VehicleDetailResponse> {
        val result = MutableLiveData<VehicleDetailResponse>()
        apiService.checkOut("Bearer $authToken", checkOutRequest).enqueue(object : Callback<VehicleDetailResponse> {
            override fun onResponse(call: Call<VehicleDetailResponse>, response: Response<VehicleDetailResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    result.postValue(response.body())
                } else {
                    // Handle error response
                   // result.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<VehicleDetailResponse>, t: Throwable) {
                // Handle failure
                //result.postValue(null)
            }
        })
        return result
    }

    fun upDatePassword(authToken: String, request: UpdatePasswordRequest): LiveData<UpdatePasswordResponse> {
        val result = MutableLiveData<UpdatePasswordResponse>()
        apiService.upDatePassword("Bearer $authToken", request).enqueue(object : Callback<UpdatePasswordResponse> {
            override fun onResponse(call: Call<UpdatePasswordResponse>, response: Response<UpdatePasswordResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    result.postValue(response.body())
                } else {
                    // Handle error response
                   // result.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<UpdatePasswordResponse>, t: Throwable) {
                // Handle failure
                //result.postValue(null)
            }
        })
        return result
    }

}
