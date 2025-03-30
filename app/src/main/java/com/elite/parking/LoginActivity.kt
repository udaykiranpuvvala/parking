package com.elite.parking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.elite.parking.Model.UserSession
import com.elite.parking.Model.login.User
import com.elite.parking.viewModel.LoginViewModel

class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val emailInput: EditText = findViewById(R.id.email_input)
        val passwordInput: EditText = findViewById(R.id.password_input)
        val loginButton: Button = findViewById(R.id.login_button)
        val forgotPassword: TextView = findViewById(R.id.forgot_password)

       /* loginButton.setOnClickListener {

            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please fill Email", Toast.LENGTH_SHORT).show()
            } else if (password.isEmpty()) {
                Toast.makeText(this, "Please fill Password", Toast.LENGTH_SHORT).show()
            } else {
                callAPIForLogin()
                 val intent = Intent(this@LoginActivity, MainActivity::class.java)
                 startActivity(intent)
            }
        }*/
        // Observe LiveData for API response


        loginButton.setOnClickListener {

            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please fill Mobile Number", Toast.LENGTH_SHORT).show()
            } else if (password.isEmpty()) {
                Toast.makeText(this, "Please fill Password", Toast.LENGTH_SHORT).show()
            } else {
                loginViewModel.login("9492445605", "12345", 1)
                loginViewModel.apiResponse.observe(this, Observer { response ->
                    response?.let {
                        if (it.status == 1) {
                            storeUserSession(it.content[0])
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            // Handle failure or unsuccessful login
                            Toast.makeText(this, "Login failed: ${it.mssg}", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }


    }
    private fun storeUserSession(user: User) {
        // Store user data globally
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
}