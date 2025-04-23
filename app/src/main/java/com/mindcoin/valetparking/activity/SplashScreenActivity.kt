package com.mindcoin.valetparking.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.mindcoin.valetparking.R
import com.mindcoin.valetparking.selfUpdate.AppSelfUpdateInputReq
import com.mindcoin.valetparking.selfUpdate.AppUpdateViewModel
import com.mindcoin.valetparking.selfUpdate.SelfUpdaterActivity
import com.mindcoin.valetparking.storage.SharedPreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var userId: String
    private lateinit var userName: String
    private lateinit var versionCode: String
    private lateinit var versionName: String
    private lateinit var applicationName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        sharedPreferencesHelper = SharedPreferencesHelper(this)
        GlobalScope.launch {
            delay(2000)  //
            withContext(Dispatchers.Main) {
                if (!sharedPreferencesHelper.isLoggedIn()) {
                    val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    CallAPI()
                  /*  val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()*/
                }


            }
        }
    }
    private fun CoroutineScope.CallAPI() {
        sharedPreferencesHelper = SharedPreferencesHelper(this@SplashScreenActivity)
        val loginResponse = sharedPreferencesHelper.getLoginResponse()

        loginResponse?.let {
            val loginData = it.content.firstOrNull()
            if (loginData != null) {
                userName = loginData.name ?: "N/A"
                userId = loginData.uuid ?: "N/A"
            }
        } ?: run {
            Toast.makeText(this@SplashScreenActivity, "Please Logout and Login Once.", Toast.LENGTH_SHORT).show()
        }

        val viewModel = ViewModelProvider(this@SplashScreenActivity)[AppUpdateViewModel::class.java]
        val request = AppSelfUpdateInputReq(
            userId = userId,
            appName = "Valet_Parking_Test",
            userName = userName,
            verName = getAppVersionInfo(this@SplashScreenActivity).second,
            verCode = getAppVersionInfo(this@SplashScreenActivity).first,
            osType = 1,
            deviceInfo = "${Build.MANUFACTURER} ${Build.MODEL} Android ${Build.VERSION.RELEASE}",
            deviceId = Settings.Secure.getString(this@SplashScreenActivity.contentResolver, Settings.Secure.ANDROID_ID),
            packageName = applicationContext.packageName
        )

        viewModel.updateInfo.observe(this@SplashScreenActivity) { update ->
            update?.let {
                versionCode=it.content.get(0).verCode.toString()
                versionName=it.content.get(0).verName.toString()
                applicationName=it.content.get(0).appName.toString()

                if(getAppVersionInfo(this@SplashScreenActivity).second.equals(it.content.get(0).verName.toString())){
                    val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    val intent = Intent(this@SplashScreenActivity, SelfUpdaterActivity::class.java)
                    intent.putExtra("applicationName", it.content.get(0).appName.toString())
                    intent.putExtra("apkUrl", it.content.get(0).apkUrl.toString())
                    startActivity(intent)
                    finish()
                }
            }
        }

        viewModel.error.observe(this@SplashScreenActivity) { error ->
            error?.let {
                Toast.makeText(this@SplashScreenActivity, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.sendUpdateRequest(request)
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