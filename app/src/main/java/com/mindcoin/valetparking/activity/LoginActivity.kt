package com.mindcoin.valetparking.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.mindcoin.valetparking.Model.UserSession
import com.mindcoin.valetparking.Model.login.User
import com.mindcoin.valetparking.R
import com.mindcoin.valetparking.activity.SplashScreenActivity
import com.mindcoin.valetparking.loader.NetworkUtils
import com.mindcoin.valetparking.loader.ProgressBarUtility
import com.mindcoin.valetparking.storage.SharedPreferencesHelper
import com.mindcoin.valetparking.viewModel.LoginViewModel

class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val sharedPreferencesHelper = SharedPreferencesHelper(this)

        val emailInput: EditText = findViewById(R.id.email_input)
        val passwordInput: EditText = findViewById(R.id.password_input)
        val loginButton: Button = findViewById(R.id.login_button)
        val version: TextView = findViewById(R.id.version)
        val forgotPassword: TextView = findViewById(R.id.forgot_password)
        val parentLayout: FrameLayout = findViewById(android.R.id.content)

        //val gradleVersionText = "Version: ${"1.0.3"}"
        val gradleVersionText = "Version: ${getAppVersionInfo(this@LoginActivity).second}"
        version.text = gradleVersionText

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
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        // Get the current focused view
        val currentFocusView = currentFocus
        if (currentFocusView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
        }
    }

    fun getAppVersionInfo(context: Context): Pair<Long, String> {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return Pair(
            first = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                packageInfo.versionCode.toLong()
            },
            second = packageInfo.versionName ?: "N/A"
        )
    }
}