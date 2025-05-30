package com.mindcoin.valetparking.fragment

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mindcoin.valetparking.Model.VehicleViewModelFactory
import com.mindcoin.valetparking.R
import com.mindcoin.valetparking.activity.CarFormActivity
import com.mindcoin.valetparking.activity.PaymentActivity
import com.mindcoin.valetparking.adapter.VehicleAdapter
import com.mindcoin.valetparking.apis.ApiService
import com.mindcoin.valetparking.apis.RetrofitClient
import com.mindcoin.valetparking.repository.VehicleRepository
import com.mindcoin.valetparking.storage.SharedPreferencesHelper
import com.mindcoin.valetparking.viewModel.VehicleViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HistoryFragment : Fragment()  {
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var vehicleAdapter: VehicleAdapter
    private lateinit var vehicleViewModel: VehicleViewModel.VehicleViewModelList
    private lateinit var userId: String
    private lateinit var authToken: String
    private lateinit var lnrNoData: LinearLayout
    private lateinit var shimmerLayout: ShimmerFrameLayout

    private lateinit var childFab1: FloatingActionButton
    private lateinit var childFab2: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for the fragment
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        shimmerLayout = view.findViewById(R.id.shimmerLayout)
        recyclerView = view.findViewById(R.id.vehicleRecyclerView)
        lnrNoData = view.findViewById(R.id.lnrNoData)
        recyclerView.layoutManager = LinearLayoutManager(context)
        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        val loginResponse = sharedPreferencesHelper.getLoginResponse()
        val fabAddVehicle: FloatingActionButton = view.findViewById(R.id.fabAddVehicle)
        childFab1 = view.findViewById(R.id.childFab1)
        childFab2 = view.findViewById(R.id.childFab2)
        toggleChildFabs()
        loginResponse?.let {
            val loginData = it.content.firstOrNull()
            if (loginData != null) {
                userId = loginData.uuid ?: "N/A"
                authToken = loginData.token ?: "N/A"
            }
        } ?: run {
            Toast.makeText(context, "Please Logout and Login Once.", Toast.LENGTH_SHORT).show()
        }
        shimmerLayout.startShimmer()

        // Simulate data loading (e.g., API call)
       /* Handler(Looper.getMainLooper()).postDelayed({
            // Stop shimmer effect & show RecyclerView
            shimmerLayout.stopShimmer()
            shimmerLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }, 3000) // 3 seconds delay*/

        childFab1.setOnClickListener {
            val intent = Intent(requireActivity(), CarFormActivity::class.java)
            startActivity(intent)
        }
        childFab2.setOnClickListener {
            val intent = Intent(requireActivity(), PaymentActivity::class.java)
            startActivity(intent)
        }
        initialAPICall()

        return view
    }

    private fun initialAPICall() {
        // Initialize ViewModel
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val repository = VehicleRepository(apiService)
        vehicleViewModel = ViewModelProvider(this, VehicleViewModelFactory(repository)).get(
            VehicleViewModel.VehicleViewModelList::class.java)

        // Observe LiveData for vehicle list
       /* vehicleViewModel.vehicleList.observe(viewLifecycleOwner, { vehicleList ->
            vehicleAdapter = VehicleAdapter(requireActivity(), vehicleList)
            recyclerView.adapter = vehicleAdapter
        })*/
        vehicleViewModel.vehicleList.observe(viewLifecycleOwner, { vehicleList ->
            if(vehicleList.size>=1){
                lnrNoData.visibility= View.GONE
            }else{
                lnrNoData.visibility= View.VISIBLE
            }

            shimmerLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            vehicleAdapter = VehicleAdapter(requireContext(), vehicleList) { vehicle ->
                val intent = Intent(requireActivity(), PaymentActivity::class.java)
                intent.putExtra("vehicleUuid", vehicle.uuid)
                startActivity(intent)
            }
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
        vehicleViewModel.fetchVehicleDetails(userId, authToken)
    }

    override fun onResume() {
        super.onResume()
        initialAPICall()
    }

    private fun toggleChildFabs() {
        if (childFab1.visibility == View.GONE) {
            // Show child FABs with animations
            showChildFabs()
        } else {
            // Hide child FABs with animations
            hideChildFabs()
        }
    }

    // Show child FABs with animations
    private fun showChildFabs() {
        childFab1.visibility = View.VISIBLE
        childFab2.visibility = View.VISIBLE

        val animator1 = ObjectAnimator.ofFloat(childFab1, "translationY", 0f, -10f)
        val animator2 = ObjectAnimator.ofFloat(childFab2, "translationY", 0f, -200f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(animator1, animator2)
        animatorSet.duration = 600
        animatorSet.start()
    }

    // Hide child FABs with animations
    private fun hideChildFabs() {
        val animator1 = ObjectAnimator.ofFloat(childFab1, "translationY", -10f, 0f)
        val animator2 = ObjectAnimator.ofFloat(childFab2, "translationY", -200f, 0f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(animator1, animator2)
        animatorSet.duration = 300
        animatorSet.start()

        // Hide child FABs after the animation
        animatorSet.addListener(object : Animator.AnimatorListener {

            override fun onAnimationStart(p0: Animator) {
                TODO("Not yet implemented")
            }

            override fun onAnimationEnd(p0: Animator) {
                childFab1.visibility = View.GONE
                childFab2.visibility = View.GONE
            }

            override fun onAnimationCancel(p0: Animator) {
                TODO("Not yet implemented")
            }

            override fun onAnimationRepeat(p0: Animator) {
                TODO("Not yet implemented")
            }
        })
    }



}