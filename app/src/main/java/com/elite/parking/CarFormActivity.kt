package com.elite.parking

import android.app.Activity
import android.Manifest
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elite.parking.LoginActivity
import com.elite.parking.Model.VehicleCheckInRequest
import com.elite.parking.Model.parkingslots.ListItem
import com.elite.parking.apis.ApiService.Companion.api
import com.elite.parking.loader.NetworkUtils
import com.elite.parking.loader.ProgressBarUtility
import com.elite.parking.repository.FileUploadRepository
import com.elite.parking.storage.SharedPreferencesHelper
import com.elite.parking.viewModel.FileUploadViewModel
import com.elite.parking.viewModel.ParkingViewModel
import com.elite.parking.viewModel.VehicleCheckInViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.zxing.integration.android.IntentIntegrator
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeParseException
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CarFormActivity : AppCompatActivity() {

    companion object {
        private const val CAMERA_PERMISSION_CODE = 101
    }

    private val selectedImages = mutableListOf<Uri>()
    private lateinit var fileUploadViewModel: FileUploadViewModel
    private val PICK_IMAGES_REQUEST = 1001
    private val TAKE_PHOTO_REQUEST = 1002
    private val REQUEST_CODE = 1003

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var vehicleNoEditText: EditText
    private var selectedVehicleType: String? = null
    private lateinit var inTimeEditText: TextView
    private lateinit var toolBarback: TextView
    private lateinit var lnrInTime: LinearLayout
    private lateinit var selectedSlotNumber: TextView
    private lateinit var validationResultText: TextView
    private lateinit var inDateEditText: TextView
    private lateinit var parkingSlot: LinearLayout
    private lateinit var hookNumberEditText: EditText
    private lateinit var barcodeButton: ImageButton
    private lateinit var notesEditText: TextInputEditText
    private lateinit var submitButton: Button

    private var selectedHour = -1
    private var selectedMinute = -1

    private lateinit var vehicleCheckInViewModel: VehicleCheckInViewModel


    private val selectedImageUris = mutableListOf<Uri>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewParking: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var lnrLytCamera: LinearLayout
    private lateinit var imgBtnCamera: ImageButton
    private var imageUri: Uri? = null
    private var imageFile: File? = null
    private lateinit var userId: String
    private lateinit var token: String
    private lateinit var companyId: String
    private var parkingLotNumber: String = ""
    private var parkingimageUrl: String = ""
    private var isValidVehicleNumber: String = ""

    private val parkingViewModel: ParkingViewModel by viewModels()
    private lateinit var parkingAdapter: SectionedParkingAdapter

    private var lastSelectedLayout: LinearLayout? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_form)

        validationResultText = findViewById(R.id.validationResultText)
        vehicleNoEditText = findViewById(R.id.vehicleNoEditText)
        inTimeEditText = findViewById(R.id.inTimeEditText)
        toolBarback = findViewById(R.id.toolBarback)
        lnrInTime = findViewById(R.id.lnrInTime)
        hookNumberEditText = findViewById(R.id.hookNumber)
        notesEditText = findViewById(R.id.notesEditText)
        submitButton = findViewById(R.id.checkOutButton)
        inDateEditText = findViewById(R.id.inDateEditText)
        parkingSlot = findViewById(R.id.parkingSlot)
        barcodeButton = findViewById(R.id.barcodeButton)
        selectedSlotNumber = findViewById(R.id.selectedSlotNumber)
        sharedPreferencesHelper = SharedPreferencesHelper(this)
        val loginResponse = sharedPreferencesHelper.getLoginResponse()


        val repository = FileUploadRepository(api)
        fileUploadViewModel = ViewModelProvider(
            this,
            ViewModelFactory.ViewModelFactoryFileUploadRepository(repository)
        )[FileUploadViewModel::class.java]

        toolBarback.setOnClickListener {
            finish()
        }
        fileUploadViewModel.uploadResult.observe(this) { result ->
            result.onSuccess { imageUrl ->
                ProgressBarUtility.dismissProgressDialog()
                Toast.makeText(this, "image Upload Successful!", Toast.LENGTH_SHORT).show()
                //selectedImages.add(Uri.parse(imageUrl.content.url))  // Add uploaded image URL
                /* selectedImages.forEach {
                     Log.e("Images","Added Images"+result)
                 }*/
                parkingimageUrl = imageUrl.content.url
                imageAdapter.notifyDataSetChanged()
            }
            result.onFailure {
                ProgressBarUtility.dismissProgressDialog()
                Toast.makeText(this, "Upload Failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
        loginResponse?.let {
            val loginData = it.content.firstOrNull()
            if (loginData != null) {
                userId = loginData.uuid ?: "N/A"
                token = loginData.token ?: "N/A"
                companyId = loginData.companyId ?: "N/A"
            }
        } ?: run {
            Toast.makeText(this, "Please Logout and Login Once.", Toast.LENGTH_SHORT).show()
        }

        val llSuv = findViewById<LinearLayout>(R.id.ll_suv)
        val llLuxury = findViewById<LinearLayout>(R.id.ll_luxury)
        val llSedan = findViewById<LinearLayout>(R.id.ll_sedan)
        val llHatchback = findViewById<LinearLayout>(R.id.ll_hatchback)
        // lnrLytCamera = findViewById(R.id.lnrLytCamera)
        imgBtnCamera = findViewById(R.id.imgBtnCamera)

        recyclerView = findViewById(R.id.recyclerViewImages)
        recyclerViewParking = findViewById(R.id.recyclerViewParkingSlots)
        imageAdapter = ImageAdapter(this, selectedImageUris)
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = imageAdapter

        val btnUploadPhotos: LinearLayout = findViewById(R.id.btn_upload_photos)
        // val parkingLotSpinner: Spinner = findViewById(R.id.spinner_parkinglot)
        val btnSubmit: Button = findViewById(R.id.checkOutButton)
        // Initialize the ViewModel
        vehicleCheckInViewModel = ViewModelProvider(this).get(VehicleCheckInViewModel::class.java)

        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)

        inDateEditText.setText(formattedDate.toString())
        inTimeEditText.setText(getFormattedTime())
        val maxLength = 10
        vehicleNoEditText.filters = arrayOf(
            InputFilter.LengthFilter(maxLength),
            InputFilter.AllCaps()
        )
        vehicleNoEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                val vehicleNumber = charSequence.toString()
                validateVehicleNumber(vehicleNumber)
            }

            override fun afterTextChanged(editable: Editable?) {
                // Not used, can be left empty
            }
        })
        val selectedSlot = intent.getStringExtra("selectedSlot")
        val selectedFloor = intent.getStringExtra("selectedFloor")
        val selectedBlock = intent.getStringExtra("selectedBlock")
        val selectedSlotUUID = intent.getStringExtra("selectedSlotUuid")
        parkingLotNumber = selectedSlotUUID.toString();
        if (!parkingLotNumber.equals("null")&&!TextUtils.isEmpty(parkingLotNumber)) {
            selectedSlotNumber.visibility = View.VISIBLE
            selectedSlotNumber.setText("Block: ${selectedBlock}  : Floor : ${selectedFloor} : Parking No: ${selectedSlot}")
        }else{
            selectedSlotNumber.visibility = View.GONE
        }
        barcodeButton.setOnClickListener {
            startBarcodeScanner()
        }
        btnSubmit.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(this)) {
                if (validateForm()) {
                    ProgressBarUtility.showProgressDialog(this)
                    val inputFormatter = DateTimeFormatter.ofPattern("[h:mm][hh:mm] a dd/MM/yyyy")
                    val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    val dateTime = LocalDateTime.parse(
                        inTimeEditText.text.toString() + " " + inDateEditText.text.toString(),
                        inputFormatter
                    )
                    val formattedDateTime = dateTime.format(outputFormatter)

                    val vehicleCheckInRequest = VehicleCheckInRequest(
                        parkingId = parkingLotNumber,
                        userId = userId,
                        vehicleNo = vehicleNoEditText.text.toString(),
                        vehicleType = selectedVehicleType.toString(),
                        companyId = companyId,
                        hookNo = hookNumberEditText.text.toString(),
                        notes = notesEditText.text.toString(),
                        inTime = formattedDateTime.toString(),
                        imageUrl = parkingimageUrl,
                        createdDate = inDateEditText.text.toString(),
                        modifiedDate = "",
                        status = 1
                    )
                    vehicleCheckInViewModel.checkIn(token, vehicleCheckInRequest)
                }
            } else {
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }
        vehicleCheckInViewModel.vehicleCheckInResponse.observe(this, Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    ProgressBarUtility.showProgressDialog(this)
                }

                is Resource.Success -> {
                    val successMessage = resource.data?.mssg ?: "Vehicle checked in successfully"
                    Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show()
                    ProgressBarUtility.dismissProgressDialog()
                    finish()
                }

                is Resource.Failure -> {
                    ProgressBarUtility.dismissProgressDialog()
                    //  Toast.makeText(this, "Error: ${resource.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })

        parkingSlot.setOnClickListener {
            //showParkingDialog()
            val intent = Intent(this@CarFormActivity, ParkingSlotsActivity::class.java)
            startActivity(intent)
            finish()
        }
        btnUploadPhotos.setOnClickListener { checkCameraPermission() }
        setSelected(llSuv)
        selectedVehicleType = "SUV"

        llSuv.setOnClickListener {
            setSelected(llSuv)
            selectedVehicleType = "SUV"
        }

        llLuxury.setOnClickListener {
            setSelected(llLuxury)
            selectedVehicleType = "Luxury"
        }
        llSedan.setOnClickListener {
            setSelected(llSedan)
            selectedVehicleType = "Sedan"
        }

        llHatchback.setOnClickListener {
            setSelected(llHatchback)
            selectedVehicleType = "Hatchback"
        }
        /*   lnrLytCamera.setOnClickListener {
               val intent = Intent(this, OcrActivity::class.java)
               startActivityForResult(intent, REQUEST_CODE)
           }*/

        imgBtnCamera.setOnClickListener {
            val intent = Intent(this, OcrActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }

        lnrInTime.setOnClickListener {
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
                    inTimeEditText.setText(formattedTime)

                },
                hour,
                minute,
                false // 24-hour format based on device settings
            )

            timePickerDialog.setTitle("Select Booking Time")
            timePickerDialog.show()
        }

    }

    private fun formatTime(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        return SimpleDateFormat("h:mm a", Locale.getDefault()).format(calendar.time)
    }

    private fun setSelected(selectedLayout: LinearLayout) {
        // If the selected item is already selected, do nothing
        if (selectedLayout == lastSelectedLayout) return
        lastSelectedLayout?.setBackgroundResource(R.drawable.unselector_bg)  // Set unselected state
        selectedLayout.setBackgroundResource(R.drawable.selector_bg)  // Set selected state
        lastSelectedLayout = selectedLayout
    }

    private fun validateForm(): Boolean {
        val vehicleNo = vehicleNoEditText.text.toString().trim()
        if (vehicleNo.isEmpty()) {
            showToast("Vehicle Number is required.")
            return false
        } else if (isValidVehicleNumber.isEmpty()) {
            showToast("Enter Proper Vehicle Number")
            return false
        }

        // Validate In Time
        val inTime = inTimeEditText.text.toString().trim()
        if (inTime.isEmpty()) {
            showToast("In Time is required.")
            return false
        }

        // Validate Hook Number
        val hookNumber = hookNumberEditText.text.toString().trim()
        if (hookNumber.isEmpty()) {
            showToast("Serial Number is required.")
            return false
        }


        if (parkingLotNumber.isEmpty()) {
            showToast("Parking Lot is required.")
            return false
        }

        /*if (parkingimageUrl.isEmpty()) {
            showToast("Please Capture Image")
            return false
        }*/
        /*val model = vehicleModelEditText.text.toString().trim()
        if (model.isEmpty()) {
            showToast("Vehicle Model is required.")
            return false
        }*/
        /*val notes = notesEditText.text.toString().trim()
        if (notes.isEmpty()) {
            showToast("Notes is required.")
            return false
        }*/

        if (selectedVehicleType == null) {
            showToast("Please select a vehicle type")
            return false
        }
        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showParkingDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_parking_slots, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val recyclerView: RecyclerView = dialogView.findViewById(R.id.recyclerViewParking)
        val closeButton: ImageView = dialogView.findViewById(R.id.close_button)

        closeButton.setOnClickListener {
            dialog.dismiss()
        }
        //recyclerView.layoutManager = GridLayoutManager(this,3)


        recyclerView.layoutManager = GridLayoutManager(this, 4).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (parkingAdapter.getItemViewType(position)) {
                        SectionedParkingAdapter.TYPE_BLOCK_HEADER -> 4
                        SectionedParkingAdapter.TYPE_FLOOR_HEADER -> 4
                        else -> 1
                    }
                }
            }
        }

        parkingAdapter = SectionedParkingAdapter(this, emptyList()) { selectedSlot ->
            //parkingLotNumber= "Block: ${selectedSlot.blockNo}  : Floor: ${selectedSlot.floorNo} : Parking No: ${selectedSlot.parkingNo}"
            parkingLotNumber = selectedSlot.uuid
            // Toast.makeText(this, "Selected Slot:  ${selectedSlot.floorNo}  :  ${selectedSlot.parkingNo}", Toast.LENGTH_SHORT).show()
            selectedSlotNumber.setText("Block: ${selectedSlot.blockNo}  : Floor : ${selectedSlot.floorNo} : Parking No: ${selectedSlot.parkingNo}")
            selectedSlotNumber.visibility = View.VISIBLE
            dialog.dismiss()
        }

        recyclerView.adapter = parkingAdapter

        val authToken = token
        parkingViewModel.fetchParkingSlots(companyId, authToken)

        parkingViewModel.parkingSlots.observe(this) { slots ->
            parkingAdapter.updateData(slots)
        }

        dialog.show()
    }


    // Handle Camera Permission & Open Camera
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageUri?.let {
                    selectedImageUris.add(it)
                    uploadCapturedImage(it)
                } ?: Toast.makeText(this, "Failed to get image URI", Toast.LENGTH_SHORT).show()
            }
        }

    private fun openCamera() {
        try {
            imageFile = createImageFile()
            imageUri = FileProvider.getUriForFile(this, "$packageName.provider", imageFile!!)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            cameraLauncher.launch(intent)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to create file", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                // Handle image picker result (gallery)
                PICK_IMAGES_REQUEST -> {
                    if (data != null) {
                        if (data.clipData != null) {
                            // Multiple images selected
                            val count = data.clipData!!.itemCount
                            for (i in 0 until count) {
                                val imageUri = data.clipData!!.getItemAt(i).uri
                                selectedImages.add(data.clipData!!.getItemAt(i).uri)
                                val bitmapFile = convertUriToFile(imageUri)  // Convert URI to file
                                selectedImageUris.add(imageUri)
                                showToast("Picker Image 1")
                            }
                        } else if (data.data != null) {
                            showToast("Picker Image 2")
                            // Single image selected
                            selectedImageUris.add(data.data!!)
                            val imageUri = data.data!!
                            selectedImages.add(imageUri)
                            val bitmapFile = convertUriToFile(imageUri)
//
                        }
                    }
                }

                REQUEST_CODE -> {

                    val resultValIntent = data?.getStringExtra("key")
                    if (resultValIntent != null) {
                        vehicleNoEditText.setText("" + resultValIntent)
                    } else {
                        Toast.makeText(this, "Number is null", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            imageAdapter.notifyDataSetChanged()  // Notify the adapter that data has changed
        }

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                hookNumberEditText.setText(result.contents)
                Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

        // Restore the orientation after the scan is finished (optional)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_$timeStamp", ".jpg", storageDir).apply {
            deleteOnExit() // Auto-delete if needed
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap): File {
        val file = File(cacheDir, "captured_image.png")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return file
    }

    private fun convertUriToFile(imageUri: Uri): File {
        val inputStream = contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        // Save the bitmap to a file
        return saveBitmapToFile(bitmap)
    }

    private fun uploadCapturedImage(uri: Uri) {
        imageFile?.let { file ->
            val token = token
            val fileFromUriC = uriToFile(this, uri);
            if (fileFromUriC != null) {
                ProgressBarUtility.showProgressDialog(this)
                fileUploadViewModel.uploadImage(token, fileFromUriC)
            } else {
                ProgressBarUtility.dismissProgressDialog()
                showToast("Image is issue")
            }
        } ?: Toast.makeText(this, "No image to upload", Toast.LENGTH_SHORT).show()
    }

    fun uriToFile(context: Context, uri: Uri): File? {
        val contentResolver = context.contentResolver
        val fileName = "temp_image_${System.currentTimeMillis()}.jpg" // Unique name
        Log.e("Upload Image", "Filename : $fileName")
        val file = File(context.cacheDir, fileName) // Store in cache directory

        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output) // Copy content
                }
            }
            return file
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun startBarcodeScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setPrompt("Scan a token barcode") // Optional prompt
        integrator.setBeepEnabled(true) // Optional beep on scan
        integrator.setOrientationLocked(true) // Optional lock orientation
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES) // Scan all barcode types
        integrator.initiateScan() // Start the scan
    }

    private fun validateVehicleNumber(vehicleNumber: String) {
        // Modified regex to allow no spaces between components
        val regex = "^[A-Z]{2}\\d{1,2}[A-Z]{1,2}\\d{1,4}$".toRegex()

        // Check if the input matches the regex
        if (vehicleNumber.matches(regex)) {
            isValidVehicleNumber = "Valid Vehicle Number"
            validationResultText.text = "Valid Vehicle Number"
            validationResultText.visibility = View.GONE
            validationResultText.setTextColor(resources.getColor(android.R.color.holo_green_dark))
        } else {
            isValidVehicleNumber == ""
            validationResultText.text = "Invalid Vehicle Number"
            validationResultText.visibility = View.VISIBLE
            validationResultText.setTextColor(resources.getColor(android.R.color.holo_red_dark))
        }
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
}