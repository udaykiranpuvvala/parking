package com.mindcoin.valetparking.Model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mindcoin.valetparking.repository.VehicleRepository
import com.mindcoin.valetparking.viewModel.VehicleViewModel

class VehicleViewModelFactory(private val repository: VehicleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VehicleViewModel.VehicleViewModelList(repository) as T
    }
}
class VehicleViewModelItemFactory(private val repository: VehicleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VehicleViewModel.VehicleViewModelListItem(repository) as T
    }
}
class VehicleViewCheckOutFactory(private val repository: VehicleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VehicleViewModel.VehicleDetailCheckOutViewModel(repository) as T
    }
}

class UpdatePasswordFactory(private val repository: VehicleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VehicleViewModel.UpDatePasswordViewModel(repository) as T
    }
}
