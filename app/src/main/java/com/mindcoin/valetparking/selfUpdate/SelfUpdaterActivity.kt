package com.mindcoin.valetparking.selfUpdate

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.mindcoin.valetparking.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class SelfUpdaterActivity : AppCompatActivity() {
    private lateinit var txtTitle: TextView
    private lateinit var txtDes: TextView
    private lateinit var txtBtn: TextView
    private var apkFile: File? = null
    private val STORAGE_PERMISSION_CODE = 101
    private lateinit var progressDialog: Dialog
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView

    private lateinit var apkUrl: String
    private lateinit var applicationName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_self_updater)


        txtTitle = findViewById(R.id.txt_title)
        txtDes = findViewById(R.id.txt_des)
        txtBtn = findViewById(R.id.txt_btn)

        txtBtn.setOnClickListener {
            checkPermissions()
        }

         applicationName = intent.getStringExtra("applicationName").toString()
         apkUrl = intent.getStringExtra("apkUrl").toString()

    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showDownloadDialog()
            downloadApk()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }

    }

    private fun downloadApk() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(apkUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    throw Exception("Server returned HTTP ${connection.responseCode} ${connection.responseMessage}")
                }

                apkFile = File(cacheDir, "${applicationName}.apk")

                if (apkFile!!.exists()) {
                    apkFile!!.delete()
                }

                connection.inputStream.use { inputStream ->
                    FileOutputStream(apkFile!!).use { outputStream ->
                        val data = ByteArray(1024)
                        var total = 0
                        val fileLength = connection.contentLength

                        var count: Int
                        while (inputStream.read(data).also { count = it } != -1) {
                            total += count
                            outputStream.write(data, 0, count)

                            val progress = (total * 100 / fileLength)
                            withContext(Dispatchers.Main) {
                                updateProgressDialog(progress)
                            }
                        }
                        outputStream.flush()
                    }
                }

                withContext(Dispatchers.Main) {
                    hideProgressDialog()
                    installApk(apkFile!!)
                }

            } catch (e: Exception) {
                Log.e("Download", "Download failed: ${e.message}")
                withContext(Dispatchers.Main) {
                    hideProgressDialog()
                    Toast.makeText(
                        this@SelfUpdaterActivity,
                        "Download failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun installApk(file: File) {
        if (file.exists()) {
            val apkUri: Uri = FileProvider.getUriForFile(this, "${packageName}.files", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)
        } else {
            Toast.makeText(
                this@SelfUpdaterActivity, "APK file not found!", Toast.LENGTH_LONG
            ).show()
        }
    }


    private fun showDownloadDialog() {
        progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.progress_dialog)
        progressDialog.setCancelable(false)
        progressBar = progressDialog.findViewById(R.id.progressBar)
        progressText = progressDialog.findViewById(R.id.tvProgress)
        progressDialog.show()
    }

    private fun updateProgressDialog(progress: Int) {
        progressBar.progress = progress
        progressText.text = "File Downloading...$progress%"
    }

    private fun hideProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }
}