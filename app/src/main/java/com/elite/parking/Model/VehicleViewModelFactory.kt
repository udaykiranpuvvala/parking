package com.elite.parking.Model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.elite.parking.repository.VehicleRepository
import com.elite.parking.viewModel.VehicleViewModel

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