package com.elite.parking

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.elite.parking.Model.UserSession

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Access user data from UserSession
        val name = UserSession.name ?: "N/A"
        val email = UserSession.email ?: "N/A"
        val mobile = UserSession.mobileNumber ?: "N/A"
        val address = UserSession.address ?: "N/A"
        val designation = UserSession.designation ?: "N/A"

        // Use findViewById to get references to UI elements and set data
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvEmail: TextView = view.findViewById(R.id.tvEmail)
        val tvMobile: TextView = view.findViewById(R.id.tvPhone)
        val tvdesignation: TextView = view.findViewById(R.id.tvdesignation)
        val tvAddress: TextView = view.findViewById(R.id.tvAddress)
        val version: TextView= view.findViewById(R.id.tvVersion)

        val androidVersion = "Android Version: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})"
        version.text = androidVersion
        tvName.text = name
        tvEmail.text = email
        tvMobile.text = mobile
        tvdesignation.text = designation
        tvAddress.text = address

        return view
    }
}