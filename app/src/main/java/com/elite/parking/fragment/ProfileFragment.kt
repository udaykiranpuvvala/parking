package com.elite.parking.fragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.elite.parking.Model.LogoutRequest
import com.elite.parking.Model.UpdatePasswordFactory
import com.elite.parking.R
import com.elite.parking.Resource
import com.elite.parking.activity.LoginActivity
import com.elite.parking.apis.ApiService
import com.elite.parking.apis.RetrofitClient
import com.elite.parking.loader.NetworkUtils
import com.elite.parking.loader.ProgressBarUtility
import com.elite.parking.repository.VehicleRepository
import com.elite.parking.storage.SharedPreferencesHelper
import com.elite.parking.viewModel.AuthViewModel
import com.elite.parking.viewModel.VehicleViewModel.UpDatePasswordViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.String

class ProfileFragment : Fragment() {
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var authToken: String
    private lateinit var userId: String

    private lateinit var logoutViewModel: AuthViewModel
    private lateinit var upDatePasswordViewModel: UpDatePasswordViewModel


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
        val version: TextView= view.findViewById(R.id.tvVersion)
        val logout: LinearLayout= view.findViewById(R.id.btnLogout)
        val changePassword: LinearLayout= view.findViewById(R.id.changePassword)
        changePassword.setOnClickListener {
            showStyledPasswordDialog()
        }

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



        private fun showStyledPasswordDialog() {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_change_password, null)

            // Initialize views
            val etCurrentPassword = dialogView.findViewById<EditText>(R.id.etCurrentPassword)
            val etNewPassword = dialogView.findViewById<EditText>(R.id.etNewPassword)
            val etConfirmPassword = dialogView.findViewById<EditText>(R.id.etConfirmPassword)

            val currentPasswordLayout = dialogView.findViewById<TextInputLayout>(R.id.currentPasswordLayout)
            val newPasswordLayout = dialogView.findViewById<TextInputLayout>(R.id.newPasswordLayout)
            val confirmPasswordLayout = dialogView.findViewById<TextInputLayout>(R.id.confirmPasswordLayout)

            val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
            val btnChange = dialogView.findViewById<Button>(R.id.btnChange)

            val dialog = Dialog(requireContext()).apply {
                setContentView(dialogView)
                //window?.setBackgroundDrawableResource(android.R.color.transparent)
                window?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
                setCancelable(false)
            }

            // Button click listeners
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnChange.setOnClickListener {
                val currentPass = etCurrentPassword.text.toString()
                val newPass = etNewPassword.text.toString()
                val confirmPass = etConfirmPassword.text.toString()

                if (validatePasswordInputs(
                        currentPass,
                        newPass,
                        confirmPass,
                        currentPasswordLayout,
                        newPasswordLayout,
                        confirmPasswordLayout)) {

                    upDatePasswordAPICall(currentPass,confirmPass,dialog)

                  /*  GlobalScope.launch {
                        delay(2000)
                        withContext(Dispatchers.Main) {
                            dialog.dismiss()
                            showPasswordChangeSuccess()
                        }
                    }*/
                }
            }

            // Show keyboard when dialog appears
            dialog.setOnShowListener {
                etCurrentPassword.requestFocus()
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(etCurrentPassword, InputMethodManager.SHOW_IMPLICIT)
            }

            dialog.show()
        }

        private fun validatePasswordInputs(
            currentPass: String,
            newPass: String,
            confirmPass: String,
            currentLayout: TextInputLayout,
            newLayout: TextInputLayout,
            confirmLayout: TextInputLayout
        ): Boolean {
            var isValid = true

            // Reset errors
            currentLayout.error = null
            newLayout.error = null
            confirmLayout.error = null

            if (currentPass.isEmpty()) {
                currentLayout.error = "Current password is required"
                isValid = false
            } /*else if (currentPass.length < 6) {
                currentLayout.error = "Password is too short"
                isValid = false
            }*/

            if (newPass.isEmpty()) {
                newLayout.error = "New password is required"
                isValid = false
            } /*else if (newPass.length < 8) {
                newLayout.error = "Use 8+ characters for better security"
                isValid = false
            } else if (!newPass.matches(".*[A-Z].*".toRegex())) {
                newLayout.error = "Include at least one uppercase letter"
                isValid = false
            } else if (!newPass.matches(".*[0-9].*".toRegex())) {
                newLayout.error = "Include at least one number"
                isValid = false
            }*/

            if (confirmPass != newPass) {
                confirmLayout.error = "Passwords don't match"
                isValid = false
            }

            return isValid
        }

        private fun showPasswordChangeSuccess() {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Password Changed")
                .setMessage("Your password has been updated successfully!")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .setIcon(R.drawable.car_crash)
                .show()
        }

    private fun upDatePasswordAPICall(oldPassword: String,newPassword: String,dialog: Dialog) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val repository = VehicleRepository(apiService)
        upDatePasswordViewModel = ViewModelProvider(this, UpdatePasswordFactory(repository)).get(UpDatePasswordViewModel::class.java)
        upDatePasswordViewModel.isLoading.observe(this, Observer { isLoading ->
            ProgressBarUtility.showProgressDialog(requireContext())
        })

        upDatePasswordViewModel.error.observe(this, Observer { errorMessage ->
            ProgressBarUtility.dismissProgressDialog()
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        })

        upDatePasswordViewModel.upDatePasswordDetail.observe(
            this,
            Observer { vehicleDetailResponse ->
                vehicleDetailResponse?.let {
                    ProgressBarUtility.dismissProgressDialog()
                    Toast.makeText(context, vehicleDetailResponse.mssg, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            })

        upDatePasswordViewModel.updatePasswordReq(authToken,oldPassword, userId,newPassword)

    }

}