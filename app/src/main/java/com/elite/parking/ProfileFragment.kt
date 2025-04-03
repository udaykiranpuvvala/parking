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
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.elite.parking.Model.UserSession
import com.elite.parking.SplashScreenActivity
import com.elite.parking.apis.ApiService
import com.elite.parking.apis.RetrofitClient
import com.elite.parking.repository.AuthRepository
import com.elite.parking.storage.SharedPreferencesHelper
import com.elite.parking.viewModel.AuthViewModel
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ProfileFragment : Fragment() {
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var authToken: String
    private lateinit var userId: String

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
        val logout: TextView= view.findViewById(R.id.logout)


        logout.setOnClickListener {

            sharedPreferencesHelper.clearLoginData()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
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
        val androidVersion = "Android Version: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})"
        version.text = androidVersion


        return view
    }
    fun getCurrentTime(): String {
        val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return current.format(formatter)
    }
}