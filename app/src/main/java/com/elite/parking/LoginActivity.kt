package com.elite.parking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.elite.parking.Model.Vehicle
import com.elite.parking.apis.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val emailInput: EditText = findViewById(R.id.email_input)
        val passwordInput: EditText = findViewById(R.id.password_input)
        val loginButton: Button = findViewById(R.id.login_button)
        val forgotPassword: TextView = findViewById(R.id.forgot_password)

        loginButton.setOnClickListener {

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
        }
    }

    private fun callAPIForLogin() {
        RetrofitClient.instance.getUsers().enqueue(object : Callback<List<Vehicle>> {
            override fun onResponse(call: Call<List<Vehicle>>, response: Response<List<Vehicle>>) {
                if (response.isSuccessful) {
                    val users = response.body()
                } else {
                    Log.e("API_ERROR", "Response failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Vehicle>>, t: Throwable) {
                Log.e("API_ERROR", "Network error: ${t.message}")
            }
        })
    }
}