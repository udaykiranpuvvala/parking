package com.elite.parking

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.elite.parking.Model.VehicleDetailViewModel
import com.elite.parking.viewModel.VehicleViewModel
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText

class PaymentActivity : AppCompatActivity() {

   // private val vehicleViewModel: VehicleDetailViewModel by viewModels()
   private val REQUEST_CODE = 1003

    private lateinit var vehicleDetailsTextView: TextView
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var errorMessageTextView: TextView
    private lateinit var lnrLytCamera: LinearLayout
    private lateinit var imgBtnCamera: ImageButton
    private lateinit var vehicleNoEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        lnrLytCamera = findViewById(R.id.lnrLytCamera)
        imgBtnCamera = findViewById(R.id.imgBtnCamera)
        vehicleNoEditText = findViewById(R.id.vehicleNoEditText)

        lnrLytCamera.setOnClickListener {
            val intent = Intent(this, OcrActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
        imgBtnCamera.setOnClickListener {
            val intent = Intent(this, OcrActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
       /* vehicleDetailsTextView = findViewById(R.id.vehicleDetailsTextView)
        progressBar = findViewById(R.id.progressBar)
        errorMessageTextView = findViewById(R.id.errorMessageTextView)*/
        // Observe LiveData
       /* vehicleViewModel.vehicleList.observe(this, Observer { vehicleDetail ->
            vehicleDetail?.let {
                *//*vehicleDetailsTextView.text = "Vehicle No: ${it.vehicleNo}\n" +
                        "Vehicle Type: ${it.vehicleType}\n" +
                        "Parking: ${it.parkingId}\n" +
                        "Notes: ${it.notes}"*//*
            }
        })

        vehicleViewModel.isLoading.observe(this, Observer { isLoading ->
           // progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        vehicleViewModel.error.observe(this, Observer { errorMessage ->
            errorMessage?.let {
               // errorMessageTextView.text = it
              //  errorMessageTextView.visibility = View.VISIBLE
            }
        })*/


        // Fetch vehicle details when the activity starts
        //vehicleViewModel.fetchVehicleDetails("5f10e726-096f-4967-8c41-5fdd2453cf77")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                REQUEST_CODE -> {

                    val resultValIntent = data?.getStringExtra("key")
                    if (resultValIntent != null) {
                        vehicleNoEditText.setText("" + resultValIntent)
                    } else {
                        Toast.makeText(this, "Number is null", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}