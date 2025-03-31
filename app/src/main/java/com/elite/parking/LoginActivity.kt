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
import com.elite.parking.Model.login.User
import com.elite.parking.loader.NetworkUtils
import com.elite.parking.loader.ProgressBarUtility
import com.elite.parking.viewModel.LoginViewModel

class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var progressBarUtility: ProgressBarUtility

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        progressBarUtility = ProgressBarUtility(this)
        progressBarUtility.setProgressBarColor(colorRes = R.color.maroon)

        val emailInput: EditText = findViewById(R.id.email_input)
        val passwordInput: EditText = findViewById(R.id.password_input)
        val loginButton: Button = findViewById(R.id.login_button)
        val forgotPassword: TextView = findViewById(R.id.forgot_password)
        val parentLayout: FrameLayout = findViewById(android.R.id.content) // This gets the root layout of your activity

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
                    progressBarUtility.show(parentLayout)
                    loginViewModel.login("9492445605", "12345", 1)
                    loginViewModel.apiResponse.observe(this, Observer { response ->
                        response?.let {
                            if (it.status == 1) {
                                storeUserSession(it.content[0])
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                                progressBarUtility.hide()
                            } else {
                                progressBarUtility.hide()
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