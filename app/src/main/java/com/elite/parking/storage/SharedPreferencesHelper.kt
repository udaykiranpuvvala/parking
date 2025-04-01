package com.elite.parking.storage

import android.content.Context
import android.content.SharedPreferences
import com.elite.parking.Model.login.LoginResponse
import com.google.gson.Gson

class SharedPreferencesHelper(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    // Store the login response as a JSON string and login status
    fun storeLoginResponse(loginResponse: LoginResponse) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val loginResponseJson = gson.toJson(loginResponse)  // Serialize the object to a JSON string
        editor.putString("loginResponse", loginResponseJson)
        editor.putBoolean("isLoggedIn", true)  // Mark user as logged in
        editor.apply()
    }

    // Retrieve the login response from SharedPreferences
    fun getLoginResponse(): LoginResponse? {
        val gson = Gson()
        val loginResponseJson = sharedPreferences.getString("loginResponse", null)
        return if (loginResponseJson != null) {
            gson.fromJson(loginResponseJson, LoginResponse::class.java)  // Deserialize the JSON string back to an object
        } else {
            null  // If no login response is found
        }
    }

    // Check if the user is logged in
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    // Clear the stored login data (for example, when logging out)
    fun clearLoginData() {
        val editor = sharedPreferences.edit()
        editor.remove("loginResponse")
        editor.remove("isLoggedIn")
        editor.apply()
    }
}

