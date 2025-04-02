package com.elite.parking

import android.app.Activity
import android.Manifest
import android.R.attr.fragment
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elite.parking.LoginActivity
import com.elite.parking.Model.ParkingSlot
import com.elite.parking.Model.UserSession
import com.elite.parking.Model.VehicleCheckInRequest
import com.elite.parking.loader.NetworkUtils
import com.elite.parking.storage.SharedPreferencesHelper
import com.elite.parking.viewModel.VehicleCheckInViewModel
import com.google.android.material.textfield.TextInputEditText
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CarFormActivity : AppCompatActivity() {

    companion object {
        private const val CAMERA_PERMISSION_CODE = 101
    }

    private val PICK_IMAGES_REQUEST = 1001
    private val TAKE_PHOTO_REQUEST = 1002
    private val REQUEST_CODE = 1003

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var vehicleNoEditText: TextInputEditText
    private var selectedVehicleType: String? = null
    private lateinit var inTimeEditText: TextView
    private lateinit var inDateEditText: TextView
    private lateinit var parkingSlot: Button
    private lateinit var hookNumberEditText: TextInputEditText
    private lateinit var notesEditText: TextInputEditText
    private lateinit var vehicleModelEditText: TextInputEditText
    private lateinit var submitButton: Button


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
    private lateinit var parkingLotNumber: String
    private lateinit var dialog: AlertDialog

    private lateinit var parkingSlotsAdapter: ParkingSlotsAdapter

    private var lastSelectedLayout: LinearLayout? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_form)

        vehicleNoEditText = findViewById(R.id.vehicleNoEditText)
        inTimeEditText = findViewById(R.id.inTimeEditText)
        hookNumberEditText = findViewById(R.id.hookNumber)
        notesEditText = findViewById(R.id.notesEditText)
        submitButton = findViewById(R.id.submitButton)
        vehicleModelEditText = findViewById(R.id.et_vehicleModel)
        inDateEditText = findViewById(R.id.inDateEditText)
        parkingSlot = findViewById(R.id.parkingSlot)
        sharedPreferencesHelper = SharedPreferencesHelper(this)

        val loginResponse = sharedPreferencesHelper.getLoginResponse()
        loginResponse?.let {
            val loginData = it.content.firstOrNull()
            if (loginData != null) {
                userId = loginData.uuid ?: "N/A"
            }
        } ?: run {
            Toast.makeText(this, "Please Logout and Login Once.", Toast.LENGTH_SHORT).show()
        }

        val llSuv = findViewById<LinearLayout>(R.id.ll_suv)
        val llLuxury = findViewById<LinearLayout>(R.id.ll_luxury)
        val llSedan = findViewById<LinearLayout>(R.id.ll_sedan)
        val llHatchback = findViewById<LinearLayout>(R.id.ll_hatchback)
        lnrLytCamera = findViewById(R.id.lnrLytCamera)
        imgBtnCamera = findViewById(R.id.imgBtnCamera)

        recyclerView = findViewById(R.id.recyclerViewImages)
        recyclerViewParking = findViewById(R.id.recyclerViewParkingSlots)
        imageAdapter = ImageAdapter(this, selectedImageUris)
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = imageAdapter

        val btnUploadPhotos: Button = findViewById(R.id.btn_upload_photos)
       // val parkingLotSpinner: Spinner = findViewById(R.id.spinner_parkinglot)
        val btnSubmit: Button = findViewById(R.id.submitButton)
        // Initialize the ViewModel
        vehicleCheckInViewModel = ViewModelProvider(this).get(VehicleCheckInViewModel::class.java)

        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val formattedTime = currentTime.format(formatter)

        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)

        inDateEditText.setText(formattedDate.toString())
        inTimeEditText.setText(formattedTime.toString())


        btnSubmit.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(this)) {
                if (validateForm()) {
                    Toast.makeText(this, "Form is valid! Submitting...", Toast.LENGTH_SHORT).show()
                    val vehicleCheckInRequest = VehicleCheckInRequest(
                        parkingId = parkingLotNumber,
                        userId = userId,
                        vehicleNo = vehicleNoEditText.text.toString(),
                        vehicleType = selectedVehicleType.toString(),
                        hookNo = hookNumberEditText.text.toString(),
                        notes = notesEditText.text.toString(),
                        inTime = inTimeEditText.text.toString(),
                        imageUrl = "",
                        createdDate = "2025-03-28",
                        modifiedDate = "2025-03-28",
                        status = 1
                    )
                    vehicleCheckInViewModel.checkIn(vehicleCheckInRequest)
                }
            }else{
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }
        vehicleCheckInViewModel.vehicleCheckInResponse.observe(this, Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show()
                }

                is Resource.Success -> {
                    val successMessage = resource.data?.mssg ?: "Vehicle checked in successfully"
                    Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show()
                    finish()
                }

                is Resource.Failure -> {
                    Toast.makeText(this, "Error: ${resource.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })

        parkingSlot.setOnClickListener {
            showParkingDialog()
        }
        btnUploadPhotos.setOnClickListener { showPopupMenu(it) }

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
        lnrLytCamera.setOnClickListener {
            val intent = Intent(this, OcrActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }

        imgBtnCamera.setOnClickListener {
            val intent = Intent(this, OcrActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }

        inTimeEditText.setOnClickListener {
            // Get the current time
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY) // Get hour in 24-hour format
            val minute = calendar.get(Calendar.MINUTE)

            // Create a TimePickerDialog
            val timePickerDialog = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->
                    // Format the time in HH:mm format
                    val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)

                    // Set the selected time to the EditText
                    inTimeEditText.setText(formattedTime)
                },
                hour, // Current hour
                minute, // Current minute
                true // Use 24-hour format
            )

            // Show the TimePickerDialog
            timePickerDialog.show()
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.menu_image_picker, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_camera -> checkCameraPermission()
                R.id.menu_gallery -> openImagePicker()
            }
            true
        }
        popupMenu.show()
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
//            val photo = result.data?.extras?.get("data") as Bitmap
                //imageView.setImageBitmap(photo)
//            selectedImageUris.add()
                imageUri?.let {
                    selectedImageUris.add(it)
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

    private fun setSelected(selectedLayout: LinearLayout) {
        // If the selected item is already selected, do nothing
        if (selectedLayout == lastSelectedLayout) return
        lastSelectedLayout?.setBackgroundResource(R.drawable.unselector_bg)  // Set unselected state
        selectedLayout.setBackgroundResource(R.drawable.selector_bg)  // Set selected state
        lastSelectedLayout = selectedLayout
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Allow multiple selections
        startActivityForResult(intent, PICK_IMAGES_REQUEST)
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
                                selectedImageUris.add(imageUri)
                            }
                        } else if (data.data != null) {
                            // Single image selected
                            selectedImageUris.add(data.data!!)
                        }
                    }
                }

                // Handle image from camera capture
                /*TAKE_PHOTO_REQUEST -> {
                    val photoUri = data?.data
                    if (photoUri != null) {
                        selectedImageUris.add(photoUri)
                    }
                }*/
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
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_$timeStamp", ".jpg", storageDir).apply {
            deleteOnExit() // Auto-delete if needed
        }
    }

    private fun validateForm(): Boolean {
        // Validate Vehicle Number
        val vehicleNo = vehicleNoEditText.text.toString().trim()
        if (vehicleNo.isEmpty()) {
            showToast("Vehicle Number is required.")
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
            showToast("Hook Number is required.")
            return false
        }


        if (parkingLotNumber.isEmpty()) {
            showToast("Parking Lot is required.")
            return false
        }
        /*val model = vehicleModelEditText.text.toString().trim()
        if (model.isEmpty()) {
            showToast("Vehicle Model is required.")
            return false
        }*/
        val notes = notesEditText.text.toString().trim()
        if (notes.isEmpty()) {
            showToast("Notes is required.")
            return false
        }

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
        val dialogView = layoutInflater.inflate(R.layout.dialog_parking_slots, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)

        // Initialize RecyclerView
        val recyclerViewParking: RecyclerView = dialogView.findViewById(R.id.recyclerViewParking)

        recyclerViewParking.layoutManager = GridLayoutManager(this, 3)

        val parkingSlotsAdapter = ParkingSlotsAdapter(
            this,
            getAllParkingSlots(),
            object : OnParkingSlotSelectedListener {
                override fun onParkingSlotSelected(parkingSlot: ParkingSlot) {
                    parkingLotNumber =parkingSlot.name
                    Toast.makeText(this@CarFormActivity, "You Selected Slot is : ${parkingSlot.name}", Toast.LENGTH_SHORT).show()
                }
            },
            dialogBuilder.create()  // Pass the dialog reference to the adapter
        )

        recyclerViewParking.adapter = parkingSlotsAdapter
        val dialog = dialogBuilder.create()

        val closeButton: ImageView = dialogView.findViewById(R.id.close_button)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }





    // Combine parking slots from all areas into one list
    private fun getAllParkingSlots(): List<ParkingSlot> {
        return getParkingSlotsForBasement1() + getParkingSlotsForBasement2() +
                getParkingSlotsForBasement3() + getParkingSlotsForBasement4()
    }

    // Sample functions to get parking slots for each basement
    private fun getParkingSlotsForBasement1(): List<ParkingSlot> {
        return listOf(
            ParkingSlot("B1-Slot 1", true),
            ParkingSlot("B1-Slot 2", false),
            ParkingSlot("B1-Slot 3", true)
        )
    }

    private fun getParkingSlotsForBasement2(): List<ParkingSlot> {
        return listOf(
            ParkingSlot("B2-Slot 1", false),
            ParkingSlot("B2-Slot 2", true),
            ParkingSlot("B2-Slot 3", false)
        )
    }

    private fun getParkingSlotsForBasement3(): List<ParkingSlot> {
        return listOf(
            ParkingSlot("B3-Slot 1", true),
            ParkingSlot("B3-Slot 2", false)
        )
    }

    private fun getParkingSlotsForBasement4(): List<ParkingSlot> {
        return listOf(
            ParkingSlot("B4-Slot 1", true),
            ParkingSlot("B4-Slot 2", false)
        )
    }


}