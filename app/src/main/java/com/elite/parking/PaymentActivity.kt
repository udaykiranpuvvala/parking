package com.elite.parking

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
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
import androidx.annotation.RequiresApi
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
import com.elite.parking.loader.ProgressBarUtility
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
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.jvm.java

class PaymentActivity : AppCompatActivity() {

    private lateinit var vehicleViewModel: VehicleViewModel.VehicleViewModelListItem
    private lateinit var vehicleDetailCheckOutViewModel: VehicleViewModel.VehicleDetailCheckOutViewModel
    private val REQUEST_CODE = 1003
    private lateinit var userId: String
    private  var authToken: String=""
    private lateinit var vehicleuuId: String
    private lateinit var companyId: String

    private lateinit var parkingId: TextView
    private lateinit var vehicleNumber: TextView
    private lateinit var vehicleType: TextView
    private lateinit var hookNumber: TextView
    private lateinit var checkinTime: TextView
    private lateinit var checkoutTime: TextView
    private lateinit var lnrOutTime: LinearLayout
    private lateinit var lnrNoData: LinearLayout
    private lateinit var createdDate: TextView
    private lateinit var parkingNote: TextView
    private lateinit var image: ImageView

    private lateinit var timeEditText: EditText
    private lateinit var checkOutButton: Button
    private lateinit var inputSection: LinearLayout
    private lateinit var successSection: RelativeLayout
    private lateinit var checkmarkImage: ImageView
    private lateinit var confirmedTimeText: TextView
    private lateinit var toolBarback: TextView
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        parkingId = findViewById(R.id.parkingId)
        vehicleNumber = findViewById(R.id.vehicleNumber)
        vehicleType = findViewById(R.id.vehicleType)
        hookNumber = findViewById(R.id.hookNumber)
        checkinTime = findViewById(R.id.checkinTime)
        checkoutTime = findViewById(R.id.checkoutTime)
        createdDate = findViewById(R.id.createdDate)
        parkingNote = findViewById(R.id.parkingNote)
        image = findViewById(R.id.image)
        toolBarback = findViewById(R.id.toolBarback)
        lnrNoData = findViewById(R.id.lnrNoData)


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
        lnrOutTime = findViewById(R.id.lnrOutTime)
        sharedPreferencesHelper = SharedPreferencesHelper(this)
        val loginResponse = sharedPreferencesHelper.getLoginResponse()

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

        val vehicleUuid = intent.getStringExtra("vehicleUuid")
        if(!TextUtils.isEmpty(vehicleUuid)){
            initialAPICall(vehicleUuid.toString())
        }
        toolBarback.setOnClickListener {
            finish()
        }
        searchButton.setOnClickListener {
            val token = tokenEditText.text.toString().trim()
            if (token.isNotEmpty()) {
                searchToken(token)
            } else {
                Toast.makeText(this, "Please enter a Serial number", Toast.LENGTH_SHORT).show()
            }
        }

        barcodeButton.setOnClickListener {
            startBarcodeScanner()
        }
        timeEditText.setText(getFormattedTime())
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
            val currentDate = Date()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(currentDate)
            val inputFormatter = DateTimeFormatter.ofPattern("[h:mm][hh:mm] a dd/MM/yyyy")
            val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val dateTime = LocalDateTime.parse(timeEditText.text.toString()+" "+formattedDate.toString(), inputFormatter)
            val formattedDateTime = dateTime.format(outputFormatter)

            val timeInput = formattedDateTime.toString()

            if (!TextUtils.isEmpty(timeInput)) {
                checkOutAPICall(timeInput)
            } else {
                timeEditText.error = "Please enter valid time (HH:MM)"
            }
        }



        vehicledetailsByHookNumberViewModel = ViewModelProvider(this).get(VehicleViewModel.VehicleDetailsbyHookNumberViewModel::class.java)
        vehicledetailsByHookNumberViewModel.vehicleCheckInResponse.observe(
            this,
            Observer { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        ProgressBarUtility.showProgressDialog(this)
                    }

                    is Resource.Success -> {
                        val successMessage = resource.data?.mssg ?: "Vehicle Data Fetched in successfully"
                        ProgressBarUtility.dismissProgressDialog()

                        // Check if content is not null and contains at least one item
                        val content = resource.data?.content
                        if (content != null && content.isNotEmpty()) {
                            val vehicleDetails = content[0]
                            lnrCheckOut.visibility= View.VISIBLE
                            parkingCard.visibility= View.VISIBLE
                            lnrNoData.visibility=View.GONE
                            // Populate the UI with vehicle details
                            parkingId.setText(vehicleDetails.parkingId ?: "")
                            vehicleNumber.setText(vehicleDetails.vehicleNo ?: "")
                            vehicleType.setText(vehicleDetails.vehicleType ?: "")
                            hookNumber.setText(vehicleDetails.hookNo ?: "")
                            checkinTime.setText(vehicleDetails.inTime ?: "")
                            checkoutTime.setText(vehicleDetails.outTime ?: "")
                            if(!TextUtils.isEmpty(vehicleDetails.outTime)){
                                lnrOutTime.visibility= View.VISIBLE
                                checkoutTime.text =vehicleDetails.outTime
                                lnrCheckOut.visibility= View.GONE
                            }else{
                                lnrOutTime.visibility= View.GONE
                                lnrCheckOut.visibility= View.VISIBLE
                            }
                            createdDate.setText(vehicleDetails.createdDate ?: "")
                            parkingNote.setText(vehicleDetails.notes ?: "")
                            vehicleuuId = vehicleDetails.parkingId ?: ""

                            // Load image using Glide
                            Glide.with(this)
                                .load(vehicleDetails.imageUrl)
                                .placeholder(R.drawable.ic_default_image)
                                .error(R.drawable.ic_default_image)
                                .into(image)
                        } else {
                            lnrCheckOut.visibility= View.GONE
                            parkingCard.visibility= View.GONE
                            lnrNoData.visibility=View.VISIBLE

                            Toast.makeText(this, "No vehicle details available", Toast.LENGTH_SHORT).show()
                        }
                    }

                    is Resource.Failure -> {
                        ProgressBarUtility.dismissProgressDialog()
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
            ProgressBarUtility.showProgressDialog(this)
        })

        vehicleDetailCheckOutViewModel.error.observe(this, Observer { errorMessage ->
            ProgressBarUtility.dismissProgressDialog()
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        })

        vehicleDetailCheckOutViewModel.vehicleDetail.observe(
            this,
            Observer { vehicleDetailResponse ->
                vehicleDetailResponse?.let {
                    ProgressBarUtility.dismissProgressDialog()
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

        val checkOutTime = timeInput
        vehicleDetailCheckOutViewModel.checkOutVehicle(authToken, vehicleuuId, checkOutTime)

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


    private fun showSuccessAnimation() {
        // Hide input section
        inputSection.visibility = View.GONE

        // Show success section
        successSection.visibility = View.VISIBLE

        // Set the confirmed time
        confirmedTimeText.text = timeEditText.text.toString().trim()

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

    private fun initialAPICall(vehicleUuid: String) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val repository = VehicleRepository(apiService)
        vehicleViewModel = ViewModelProvider(this, VehicleViewModelItemFactory(repository)
        ).get(VehicleViewModel.VehicleViewModelListItem::class.java)
        vehicleViewModel.vehicleListItem.observe(this, { vehicleList ->
            ProgressBarUtility.dismissProgressDialog()
            if (vehicleList != null) {
                lnrCheckOut.visibility= View.VISIBLE
                parkingCard.visibility= View.VISIBLE
                lnrNoData.visibility=View.GONE
                parkingId.setText(vehicleList.get(0).parkingId ?: "")
                vehicleNumber.setText(vehicleList.get(0).vehicleNo ?: "")
                vehicleType.setText(vehicleList.get(0).vehicleType ?: "")
                hookNumber.setText(vehicleList.get(0).hookNo ?: "")
                checkinTime.setText(convertDateFormatCheckIn(vehicleList.get(0).inTime) ?: "")
                checkoutTime.setText(convertDateFormatCheckIn(vehicleList.get(0).outTime) ?: "")
                if(!TextUtils.isEmpty(vehicleList.get(0).outTime)){
                    lnrOutTime.visibility= View.VISIBLE
                    checkoutTime.text =(convertDateFormatCheckIn(vehicleList.get(0).outTime))
                    lnrCheckOut.visibility= View.GONE
                }else{
                    lnrOutTime.visibility= View.GONE
                    lnrCheckOut.visibility= View.VISIBLE
                }
                createdDate.setText(convertDateFormat(vehicleList.get(0).createdDate) ?: "")
                parkingNote.setText(vehicleList.get(0).notes ?: "")
                vehicleuuId = vehicleList.get(0).uuid ?: ""
                Glide.with(this)
                    .load(vehicleList.get(0).imageUrl)
                    .placeholder(R.drawable.ic_default_image)
                    .error(R.drawable.ic_default_image)
                    .into(image)
            }

        })

        // Observe loading state
        vehicleViewModel.isLoading.observe(this, { isLoading ->
            ProgressBarUtility.showProgressDialog(this)
        })

        // Observe error state
        vehicleViewModel.error.observe(this, { errorMessage ->
            ProgressBarUtility.dismissProgressDialog()
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        })

        vehicleViewModel.fetchVehicleDetails(vehicleUuid, authToken)
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
        integrator.setPrompt("Scan a token barcode") // Optional prompt
        integrator.setBeepEnabled(true) // Optional beep on scan
        integrator.setOrientationLocked(true) // Optional lock orientation
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES) // Scan all barcode types
        integrator.initiateScan() // Start the scan
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                tokenEditText.setText(result.contents)
                searchToken(result.contents)
                Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    // Optional: Clear the search when returning to the activity
    override fun onResume() {
        super.onResume()
        tokenEditText.setText("")
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getFormattedTime(): String {
        val currentTime = LocalTime.now()

        // Builder pattern for more control
        val formatter = DateTimeFormatterBuilder()
            .appendPattern("hh:mm")  // 12-hour format
            .appendLiteral(" ")
            .appendPattern("a")      // AM/PM marker
            .toFormatter(Locale.getDefault())  // Respects device locale

        return currentTime.format(formatter)
    }

    fun convertDateFormatCheckIn(inputDate: String?): String {
        try {
            val inputFormat =
                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val outputFormat = SimpleDateFormat("hh:mm a, dd MMM", Locale.getDefault())
            val date = inputFormat.parse(inputDate)
            date ?: throw IllegalArgumentException("Invalid input time format")
            return outputFormat.format(date)
        } catch (e: Exception) {
            return "Error: ${e.message}"
        }
    }

    fun convertDateFormat(inputDate: String?): String {
        try {
            val inputFormat =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("hh:mm a, dd MMM", Locale.getDefault())
            val date = inputFormat.parse(inputDate)
            date ?: throw IllegalArgumentException("Invalid input time format")
            return outputFormat.format(date)
        } catch (e: Exception) {
            return "Error: ${e.message}"
        }
    }
}