package com.elite.parking

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.elite.parking.storage.SharedPreferencesHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        sharedPreferencesHelper = SharedPreferencesHelper(this)
        GlobalScope.launch {
            delay(2000)  //
            withContext(Dispatchers.Main) {
                if (!sharedPreferencesHelper.isLoggedIn()) {
                    val intent= Intent(this@SplashScreenActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent= Intent(this@SplashScreenActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }


            }
        }
    }
}