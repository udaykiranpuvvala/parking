package com.elite.parking

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.elite.parking.Model.VehicleDetailViewModel
import com.elite.parking.viewModel.VehicleViewModel
import com.google.android.material.progressindicator.CircularProgressIndicator

class PaymentActivity : AppCompatActivity() {

   // private val vehicleViewModel: VehicleDetailViewModel by viewModels()

    private lateinit var vehicleDetailsTextView: TextView
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var errorMessageTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment)
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
}