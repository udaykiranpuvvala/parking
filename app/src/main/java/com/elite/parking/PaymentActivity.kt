package com.elite.parking

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.elite.parking.Model.VehicleViewModelItemFactory
import com.elite.parking.apis.ApiService
import com.elite.parking.apis.RetrofitClient
import com.elite.parking.repository.VehicleRepository
import com.elite.parking.storage.SharedPreferencesHelper
import com.elite.parking.viewModel.VehicleViewModel
import com.elite.parking.viewModel.VehicleViewModel.VehicleDetailViewModel
import com.google.android.material.textfield.TextInputEditText

class PaymentActivity : AppCompatActivity() {

    private lateinit var vehicleViewModel: VehicleViewModel.VehicleViewModelListItem
    private val REQUEST_CODE = 1003
    private lateinit var lnrLytCamera: LinearLayout
    private lateinit var imgBtnCamera: ImageButton
    private lateinit var vehicleNoEditText: TextInputEditText
    private lateinit var userId: String
    private lateinit var authToken: String

    private lateinit var parkingId: TextView
    private lateinit var vehicleNumber: TextView
    private lateinit var vehicleType: TextView
    private lateinit var hookNumber: TextView
    private lateinit var checkinTime: TextView
    private lateinit var createdDate: TextView
    private lateinit var parkingNote: TextView
    private lateinit var image: ImageView

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    private lateinit var vehicleDetailViewModel: VehicleDetailViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        lnrLytCamera = findViewById(R.id.lnrLytCamera)
        imgBtnCamera = findViewById(R.id.imgBtnCamera)
        vehicleNoEditText = findViewById(R.id.vehicleNoEditText)

        parkingId = findViewById(R.id.parkingId)
        vehicleNumber = findViewById(R.id.vehicleNumber)
        vehicleType = findViewById(R.id.vehicleType)
        hookNumber = findViewById(R.id.hookNumber)
        checkinTime = findViewById(R.id.checkinTime)
        createdDate = findViewById(R.id.createdDate)
        parkingNote = findViewById(R.id.parkingNote)
        image = findViewById(R.id.image)

        sharedPreferencesHelper = SharedPreferencesHelper(this)
        val loginResponse = sharedPreferencesHelper.getLoginResponse()

      //  vehicleDetailViewModel = ViewModelProvider(this).get(VehicleDetailViewModel::class.java)

        // Observe LiveData for updates
//        vehicleDetailViewModel.isLoading.observe(this, Observer { isLoading ->
//            // Show or hide loading indicator based on isLoading
//            //progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
//        })
//
//        vehicleDetailViewModel.error.observe(this, Observer { errorMessage ->
//            // Show error message in case of failure
//            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
//        })

//        vehicleDetailViewModel.vehicleDetail.observe(this, Observer { vehicleDetailResponse ->
//            // Update the UI with vehicle detail data
//            vehicleDetailResponse?.let {
//                // Update the UI with vehicle details
//                //vehicleDetail = it.content.firstOrNull()  // Assuming only one vehicle in the response
//            }
//        })

        // Trigger the check-out when a button is clicked
       // checkOutButton.setOnClickListener {
            val checkInId = "802adaf8-d3a6-4580-9810-a3d9d5ff0831" // Get this from your data source
            val checkOutTime = "2025-03-28 18:00:00"
           // vehicleDetailViewModel.checkOutVehicle(authToken, checkInId, checkOutTime)
       // }



        lnrLytCamera.setOnClickListener {
            val intent = Intent(this, OcrActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
        imgBtnCamera.setOnClickListener {
            val intent = Intent(this, OcrActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }

        loginResponse?.let {
            val loginData = it.content.firstOrNull()
            if (loginData != null) {
                userId = loginData.uuid ?: "N/A"
                authToken = loginData.token ?: "N/A"
            }
        } ?: run {
            Toast.makeText(this, "Please Logout and Login Once.", Toast.LENGTH_SHORT).show()
        }
        initialAPICall()

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

    private fun initialAPICall() {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val repository = VehicleRepository(apiService)
        vehicleViewModel = ViewModelProvider(
            this,
            VehicleViewModelItemFactory(repository)
        ).get(VehicleViewModel.VehicleViewModelListItem::class.java)
        vehicleViewModel.vehicleListItem.observe(this, { vehicleList ->
            if (vehicleList != null) {
                parkingId.setText(vehicleList.get(0).parkingId ?: "")
                vehicleNumber.setText(vehicleList.get(0).vehicleNo ?: "")
                vehicleType.setText(vehicleList.get(0).vehicleType ?: "")
                hookNumber.setText(vehicleList.get(0).hookNo ?: "")
                checkinTime.setText(vehicleList.get(0).inTime ?: "")
                createdDate.setText(vehicleList.get(0).createdDate ?: "")
                parkingNote.setText(vehicleList.get(0).notes ?: "")
                Glide.with(this)
                    .load(vehicleList.get(0).imageUrl)
                    .placeholder(R.drawable.car3)
                    .error(R.drawable.car3)
                    .into(image)
            }

        })

        // Observe loading state
        vehicleViewModel.isLoading.observe(this, { isLoading ->
            // Show or hide loading indicator
            // e.g., loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        // Observe error state
        vehicleViewModel.error.observe(this, { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        })

        // Fetch vehicle details
        vehicleViewModel.fetchVehicleDetails("672a6e8d-dba2-41c8-bbfd-ca9d298ca734", authToken)
    }

}