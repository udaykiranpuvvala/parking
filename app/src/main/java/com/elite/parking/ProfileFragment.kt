package com.elite.parking

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TimeUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.elite.parking.Model.LogoutRequest
import com.elite.parking.Model.UserSession
import com.elite.parking.Model.UserSession.token
import com.elite.parking.Model.VehicleCheckInRequest
import com.elite.parking.Resource
import com.elite.parking.SplashScreenActivity
import com.elite.parking.apis.ApiService
import com.elite.parking.apis.RetrofitClient
import com.elite.parking.loader.NetworkUtils
import com.elite.parking.loader.ProgressBarUtility
import com.elite.parking.repository.AuthRepository
import com.elite.parking.storage.SharedPreferencesHelper
import com.elite.parking.viewModel.AuthViewModel
import com.elite.parking.viewModel.LoginViewModel
import com.elite.parking.viewModel.VehicleCheckInViewModel
import com.google.zxing.client.android.BuildConfig
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.String

class ProfileFragment : Fragment() {
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var authToken: String
    private lateinit var userId: String

    private lateinit var logoutViewModel: AuthViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        val loginResponse = sharedPreferencesHelper.getLoginResponse()

        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvEmail: TextView = view.findViewById(R.id.tvEmail)
        val tvMobile: TextView = view.findViewById(R.id.tvPhone)
        val tvdesignation: TextView = view.findViewById(R.id.tvdesignation)
        val tvAddress: TextView = view.findViewById(R.id.tvAddress)
        val version: TextView= view.findViewById(R.id.tvVersion)
        val logout: Button= view.findViewById(R.id.btnLogout)
        logoutViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        logoutViewModel.response.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    ProgressBarUtility.showProgressDialog(requireContext())
                }

                is Resource.Success -> {
                    val successMessage = resource.data?.mssg ?: "Vehicle checked in successfully"
                    ProgressBarUtility.dismissProgressDialog()
                    Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
                   // finish()
                    sharedPreferencesHelper.clearLoginData()
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                }

                is Resource.Failure -> {
                    Toast.makeText(context, "Error: ${resource.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })



        logout.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                showLogoutDialog()
            }else{
                Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }

        }

        loginResponse?.let {
            val loginData = it.content.firstOrNull()
            if (loginData != null) {
                val name = loginData.name ?: "N/A"
                val email = loginData.email ?: "N/A"
                val mobile = loginData.mobileNumber ?: "N/A"
                val address = loginData.address ?: "N/A"
                val designation = loginData.designation ?: "N/A"
                authToken = loginData.token
                userId = loginData.uuid

                tvName.text = name
                tvEmail.text = email
                tvMobile.text = mobile
                tvdesignation.text = designation
                tvAddress.text = address
            }
        } ?: run {
            Toast.makeText(context, "Please Logout and Login Once.", Toast.LENGTH_SHORT).show()
        }
        val gradleVersion =/* BuildConfig.GRADLE_VERSION*/"1.1.1"
        val gradleVersionText = "Version: $gradleVersion"
        version.text = gradleVersionText


        return view
    }

    private fun showLogoutDialog() {
        // Create an AlertDialog builder
        val builder = AlertDialog.Builder(requireContext())

        // Set dialog title and message
        builder.setTitle("Logout")
        builder.setMessage("Do you want to logout?")

        // Set positive button (Logout)
        builder.setPositiveButton("Yes") { dialog, which ->
            val formattedTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val currentTime = LocalTime.now()
                val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                currentTime.format(formatter)
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                val logoutRequest = LogoutRequest(
                    userId = userId,
                    activityTime = formattedTime.toString()?:"",
                    osType = 3,
                )
                logoutViewModel.logout(authToken,logoutRequest)
            }else{
                Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }

        }

        // Set negative button (Cancel)
        builder.setNegativeButton("No") { dialog, which ->
            // Dismiss the dialog and do nothing
            dialog.dismiss()
        }

        // Show the AlertDialog
        builder.show()
    }

}