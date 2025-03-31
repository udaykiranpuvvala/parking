package com.elite.parking

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elite.parking.Model.UserSession
import com.elite.parking.Model.VehicleViewModelFactory
import com.elite.parking.Model.login.Vehicle
import com.elite.parking.apis.ApiService
import com.elite.parking.apis.RetrofitClient
import com.elite.parking.repository.VehicleRepository
import com.elite.parking.viewModel.VehicleViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var vehicleAdapter: VehicleAdapter
    private lateinit var vehicleViewModel: VehicleViewModel
    private lateinit var userId: String
    private lateinit var shimmerLayout: ShimmerFrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for the fragment
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        shimmerLayout = view.findViewById(R.id.shimmerLayout)
        recyclerView = view.findViewById(R.id.vehicleRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        userId = UserSession.uuid ?: "N/A"
        val fabAddVehicle: FloatingActionButton=view.findViewById(R.id.fabAddVehicle)

        shimmerLayout.startShimmer()

        // Simulate data loading (e.g., API call)
        Handler(Looper.getMainLooper()).postDelayed({
            // Stop shimmer effect & show RecyclerView
            shimmerLayout.stopShimmer()
            shimmerLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }, 3000) // 3 seconds delay

        fabAddVehicle.setOnClickListener {
            val intent = Intent(requireActivity(), CarFormActivity::class.java)
            startActivity(intent)
        }
        initialAPICall()

        return view
    }

    private fun initialAPICall() {
        // Initialize ViewModel
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val repository = VehicleRepository(apiService)
        vehicleViewModel = ViewModelProvider(this, VehicleViewModelFactory(repository)).get(VehicleViewModel::class.java)

        // Observe LiveData for vehicle list
        vehicleViewModel.vehicleList.observe(viewLifecycleOwner, { vehicleList ->
            vehicleAdapter = VehicleAdapter(requireActivity(),vehicleList)
            recyclerView.adapter = vehicleAdapter
        })

        // Observe loading state
        vehicleViewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            // Show or hide loading indicator
            // e.g., loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        // Observe error state
        vehicleViewModel.error.observe(viewLifecycleOwner, { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        })

        // Fetch vehicle details
        vehicleViewModel.fetchVehicleDetails(userId)
    }

    override fun onResume() {
        super.onResume()
        initialAPICall()
    }
}