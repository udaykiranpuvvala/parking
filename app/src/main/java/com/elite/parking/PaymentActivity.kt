package com.elite.parking

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.elite.parking.Model.VehicleCheckInRequest
import com.elite.parking.Model.VehicleDetailsByHookNumberRequest
import com.elite.parking.Model.VehicleViewCheckOutFactory
import com.elite.parking.Model.VehicleViewModelItemFactory
import com.elite.parking.Resource
import com.elite.parking.apis.ApiService
import com.elite.parking.apis.RetrofitClient
import com.elite.parking.loader.NetworkUtils
import com.elite.parking.repository.VehicleRepository
import com.elite.parking.storage.SharedPreferencesHelper
import com.elite.parking.viewModel.VehicleCheckInViewModel
import com.elite.parking.viewModel.VehicleViewModel
import com.elite.parking.viewModel.VehicleViewModel.VehicleDetailCheckOutViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.jvm.java

class PaymentActivity : AppCompatActivity() {

    private lateinit var vehicleViewModel: VehicleViewModel.VehicleViewModelListItem
    private lateinit var vehicleDetailCheckOutViewModel: VehicleViewModel.VehicleDetailCheckOutViewModel
    private val REQUEST_CODE = 1003
    private lateinit var userId: String
    private lateinit var authToken: String
    private lateinit var vehicleuuId: String
    private lateinit var companyId: String

    private lateinit var parkingId: TextView
    private lateinit var vehicleNumber: TextView
    private lateinit var vehicleType: TextView
    private lateinit var hookNumber: TextView
    private lateinit var checkinTime: TextView
    private lateinit var createdDate: TextView
    private lateinit var parkingNote: TextView
    private lateinit var image: ImageView

    private lateinit var timeEditText: EditText
    private lateinit var checkOutButton: Button
    private lateinit var inputSection: LinearLayout
    private lateinit var successSection: RelativeLayout
    private lateinit var checkmarkImage: ImageView
    private lateinit var confirmedTimeText: TextView
    private lateinit var tokenEditText: EditText
    private lateinit var searchButton: ImageButton
    private lateinit var barcodeButton: ImageButton
    private lateinit var lnrCheckOut: LinearLayout
    private lateinit var parkingCard: CardView
    private var isExpanded = false

    private var selectedHour = -1
    private var selectedMinute = -1

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    private lateinit var vehicledetailsByHookNumberViewModel: VehicleViewModel.VehicleDetailsbyHookNumberViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        parkingId = findViewById(R.id.parkingId)
        vehicleNumber = findViewById(R.id.vehicleNumber)
        vehicleType = findViewById(R.id.vehicleType)
        hookNumber = findViewById(R.id.hookNumber)
        checkinTime = findViewById(R.id.checkinTime)
        createdDate = findViewById(R.id.createdDate)
        parkingNote = findViewById(R.id.parkingNote)
        image = findViewById(R.id.image)


        timeEditText = findViewById(R.id.timeEditText)
        checkOutButton = findViewById(R.id.checkOutButton)
        inputSection = findViewById(R.id.inputSection)
        successSection = findViewById(R.id.successSection)
        checkmarkImage = findViewById(R.id.checkmarkImage)
        confirmedTimeText = findViewById(R.id.confirmedTimeText)
        val expandButton = findViewById<Button>(R.id.expandButton)
        val expandableSection = findViewById<LinearLayout>(R.id.expandableSection)
        tokenEditText = findViewById(R.id.tokenEditText)
        searchButton = findViewById(R.id.searchButton)
        barcodeButton = findViewById(R.id.barcodeButton)
        parkingCard = findViewById(R.id.parkingCard)
        lnrCheckOut = findViewById(R.id.lnrCheckOut)


        sharedPreferencesHelper = SharedPreferencesHelper(this)
        val loginResponse = sharedPreferencesHelper.getLoginResponse()

        searchButton.setOnClickListener {
            val token = tokenEditText.text.toString().trim()
            if (token.isNotEmpty()) {
                searchToken(token)
            } else {
                Toast.makeText(this, "Please enter a token number", Toast.LENGTH_SHORT).show()
            }
        }

        barcodeButton.setOnClickListener {
            startBarcodeScanner()
        }
        expandButton.setOnClickListener {
            isExpanded = !isExpanded

            if (isExpanded) {
                expandableSection.visibility = View.VISIBLE
                expandButton.text = "Hide Details"
                expandButton.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.ic_expand_less, 0
                )
            } else {
                expandableSection.visibility = View.GONE
                expandButton.text = "Show Details"
                expandButton.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.ic_expand_more, 0
                )
            }
        }
        timeEditText.setOnClickListener {
            showTimePickerDialog()
        }
        checkOutButton.setOnClickListener {
            val timeInput = timeEditText.text.toString().trim()

            if (isValidTime(timeInput)) {
                checkOutAPICall(timeInput)
            } else {
                timeEditText.error = "Please enter valid time (HH:MM)"
            }
        }

        loginResponse?.let {
            val loginData = it.content.firstOrNull()
            if (loginData != null) {
                userId = loginData.uuid ?: "N/A"
                authToken = loginData.token ?: "N/A"
                companyId = loginData.companyId ?: "N/A"
            }
        } ?: run {
            Toast.makeText(this, "Please Logout and Login Once.", Toast.LENGTH_SHORT).show()
        }

        vehicledetailsByHookNumberViewModel = ViewModelProvider(this).get(VehicleViewModel.VehicleDetailsbyHookNumberViewModel::class.java)
        vehicledetailsByHookNumberViewModel.vehicleCheckInResponse.observe(
            this,
            Observer { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show()
                    }

                    is Resource.Success -> {
                        val successMessage = resource.data?.mssg ?: "Vehicle Data Fetched in successfully"
                        Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show()

                        // Check if content is not null and contains at least one item
                        val content = resource.data?.content
                        if (content != null && content.isNotEmpty()) {
                            val vehicleDetails = content[0]
                            lnrCheckOut.visibility= View.VISIBLE
                            parkingCard.visibility= View.VISIBLE
                            // Populate the UI with vehicle details
                            parkingId.setText(vehicleDetails.parkingId ?: "")
                            vehicleNumber.setText(vehicleDetails.vehicleNo ?: "")
                            vehicleType.setText(vehicleDetails.vehicleType ?: "")
                            hookNumber.setText(vehicleDetails.hookNo ?: "")
                            checkinTime.setText(vehicleDetails.inTime ?: "")
                            createdDate.setText(vehicleDetails.createdDate ?: "")
                            parkingNote.setText(vehicleDetails.notes ?: "")
                            vehicleuuId = vehicleDetails.parkingId ?: ""

                            // Load image using Glide
                            Glide.with(this)
                                .load(vehicleDetails.imageUrl)
                                .placeholder(R.drawable.car3)
                                .error(R.drawable.car3)
                                .into(image)
                        } else {
                            lnrCheckOut.visibility= View.GONE
                            parkingCard.visibility= View.GONE
                            Toast.makeText(this, "No vehicle details available", Toast.LENGTH_SHORT).show()
                        }
                    }

                    is Resource.Failure -> {
                        Toast.makeText(this, "Error: ${resource.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            })


    }


    /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)
         if (resultCode == Activity.RESULT_OK) {
             when (requestCode) {

                 REQUEST_CODE -> {

                     val resultValIntent = data?.getStringExtra("key")
                     if (resultValIntent != null) {
                       //  vehicleNoEditText.setText("" + resultValIntent)
                     } else {
                         Toast.makeText(this, "Number is null", Toast.LENGTH_SHORT).show()
                     }
                 }
             }
         }
     }*/


    private fun checkOutAPICall(timeInput: String) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val repository = VehicleRepository(apiService)
        vehicleDetailCheckOutViewModel =
            ViewModelProvider(this, VehicleViewCheckOutFactory(repository)).get(
                VehicleDetailCheckOutViewModel::class.java
            )
        vehicleDetailCheckOutViewModel.isLoading.observe(this, Observer { isLoading ->
            checkOutButton.text = "Processing..."
        })

        vehicleDetailCheckOutViewModel.error.observe(this, Observer { errorMessage ->
            // Show error message in case of failure
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        })

        vehicleDetailCheckOutViewModel.vehicleDetail.observe(
            this,
            Observer { vehicleDetailResponse ->
                // Update the UI with vehicle detail data
                vehicleDetailResponse?.let {
                    if (vehicleDetailResponse.status != 0) {
                        checkOutButton.isEnabled = false
                        showSuccessAnimation()
                    } else {
                        checkOutButton.isEnabled = true
                        checkOutButton.text = "CheckOut"
                        Toast.makeText(this, vehicleDetailResponse.mssg, Toast.LENGTH_SHORT).show()
                    }
                }
            })

        val checkInId =
            "802adaf8-d3a6-4580-9810-a3d9d5ff0831" // Get this from your data source // Get this from your data source
        val checkOutTime = timeInput
        vehicleDetailCheckOutViewModel.checkOutVehicle(authToken, checkInId, checkOutTime)

    }

    private fun isValidTime(time: String): Boolean {
        return try {
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            format.isLenient = false
            format.parse(time)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minuteOfHour ->
                // Store selected time
                selectedHour = hourOfDay
                selectedMinute = minuteOfHour

                // Update EditText with formatted time
                val formattedTime = formatTime(hourOfDay, minuteOfHour)
                timeEditText.setText(formattedTime)

                // Enable submit button
                checkOutButton.isEnabled = true
            },
            hour,
            minute,
            false // 24-hour format based on device settings
        )

        timePickerDialog.setTitle("Select Booking Time")
        timePickerDialog.show()
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        return SimpleDateFormat("h:mm a", Locale.getDefault()).format(calendar.time)
    }

    private fun processTimeConfirmation(timeInput: String) {
        // Show loading state
        checkOutButton.text = "Processing..."
        checkOutButton.isEnabled = false

        // Format time for display (convert to AM/PM format)
        val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val formattedTime = try {
            val date = inputFormat.parse(timeInput)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            timeInput
            showSuccessAnimation()
        }

        /*GlobalScope.launch {
            delay(2000)  // Wait for 3 seconds
            withContext(Dispatchers.Main) {

            }
        }*/
    }

    private fun showSuccessAnimation() {
        // Hide input section
        inputSection.visibility = View.GONE

        // Show success section
        successSection.visibility = View.VISIBLE

        // Set the confirmed time
        confirmedTimeText.text = formatTime(selectedHour, selectedMinute)

        // Animate the checkmark
        animateCheckmark()
        GlobalScope.launch {
            delay(2000)
            withContext(Dispatchers.Main) {
              finish()
            }
        }
    }

    private fun animateCheckmark() {
        // Scale down animation
        val scaleDown = ScaleAnimation(
            1f, 0.8f,
            1f, 0.8f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 200
            fillAfter = true
        }

        // Scale up with bounce
        val scaleUp = ScaleAnimation(
            0.8f, 1.2f,
            0.8f, 1.2f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 300
            startOffset = 200
            interpolator = AccelerateInterpolator()
        }

        // Return to normal scale
        val scaleNormal = ScaleAnimation(
            1.2f, 1f,
            1.2f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 200
            startOffset = 500
        }

        scaleDown.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                checkmarkImage.startAnimation(scaleUp)
            }
        })

        scaleUp.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                checkmarkImage.startAnimation(scaleNormal)
            }
        })

        checkmarkImage.startAnimation(scaleDown)
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
                vehicleuuId = vehicleList.get(0).uuid ?: ""
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

    private fun searchToken(tokenNumber: String) {
        if (NetworkUtils.isNetworkAvailable(this)) {

            val request = VehicleDetailsByHookNumberRequest(
                companyId = companyId,
                hookNo = tokenNumber,
                vehicleNo = "",
            )
            vehicledetailsByHookNumberViewModel.getVehicleDetailsbyHookNumber(authToken, request)
        }
    }

    private fun startBarcodeScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setPrompt("Scan a token barcode")
        integrator.setBeepEnabled(true)
        integrator.setOrientationLocked(true)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                // Handle successful scan
                val scannedToken = result.contents
                tokenEditText.setText(scannedToken)
                searchToken(scannedToken)
            } else {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Optional: Clear the search when returning to the activity
    override fun onResume() {
        super.onResume()
        tokenEditText.setText("")
    }
}