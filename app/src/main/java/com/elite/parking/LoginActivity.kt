package com.elite.parking

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.elite.parking.Model.UserSession
import com.elite.parking.Model.login.LoginResponse
import com.elite.parking.Model.login.User
import com.elite.parking.loader.NetworkUtils
import com.elite.parking.loader.ProgressBarUtility
import com.elite.parking.storage.SharedPreferencesHelper
import com.elite.parking.viewModel.LoginViewModel
import com.google.gson.Gson

class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        val sharedPreferencesHelper = SharedPreferencesHelper(this)

        val emailInput: EditText = findViewById(R.id.email_input)
        val passwordInput: EditText = findViewById(R.id.password_input)
        val loginButton: Button = findViewById(R.id.login_button)
        val forgotPassword: TextView = findViewById(R.id.forgot_password)
        val parentLayout: FrameLayout = findViewById(android.R.id.content)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please fill Mobile Number", Toast.LENGTH_SHORT).show()
            } else if (password.isEmpty()) {
                Toast.makeText(this, "Please fill Password", Toast.LENGTH_SHORT).show()
            } else {
                if (NetworkUtils.isNetworkAvailable(this)) {
                    hideKeyboard()
                    ProgressBarUtility.showProgressDialog(this)
                    loginViewModel.login(email, password, 1)
                    loginViewModel.apiResponse.observe(this, Observer { response ->
                        response?.let {
                            if (it.status == 1) {
                                storeUserSession(it.content[0])
                                sharedPreferencesHelper.storeLoginResponse(response)
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                                ProgressBarUtility.dismissProgressDialog()
                            } else {
                                ProgressBarUtility.dismissProgressDialog()
                                Toast.makeText(this, "Login failed: ${it.mssg}", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    })
                }else{
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
    private fun storeUserSession(user: User) {
        UserSession.uuid = user.uuid
        UserSession.name = user.name
        UserSession.mobileNumber = user.mobileNumber
        UserSession.email = user.email
        UserSession.token = user.token
        UserSession.address = user.address
        UserSession.companyId = user.companyId
        UserSession.roleId = user.roleId
        UserSession.designation = user.designation
        UserSession.status = user.status
    }
    fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // Get the current focused view
        val currentFocusView = currentFocus
        if (currentFocusView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
        }
    }
}