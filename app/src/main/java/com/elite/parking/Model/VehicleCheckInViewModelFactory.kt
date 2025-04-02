package com.elite.parking.Model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.elite.parking.apis.ApiService
import com.elite.parking.viewModel.VehicleCheckInViewModel

class VehicleCheckInViewModelFactory(
    private val api: ApiService
) : ViewModelProvider.Factory {

}