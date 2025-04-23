package com.mindcoin.valetparking.Model

import androidx.lifecycle.ViewModelProvider
import com.mindcoin.valetparking.apis.ApiService

class VehicleCheckInViewModelFactory(
    private val api: ApiService
) : ViewModelProvider.Factory {

}