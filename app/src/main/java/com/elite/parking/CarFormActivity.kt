package com.elite.parking

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CarFormActivity : AppCompatActivity() {

    private val PICK_IMAGES_REQUEST = 1001
    private val TAKE_PHOTO_REQUEST = 1002


    private val selectedImageUris = mutableListOf<Uri>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var lnrLytCamera: LinearLayout
    private lateinit var imgBtnCamera: ImageButton


    private var lastSelectedLayout: LinearLayout? = null
    private lateinit var intimeEditText: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_form)

        val llSuv = findViewById<LinearLayout>(R.id.ll_suv)
        val llLuxury = findViewById<LinearLayout>(R.id.ll_luxury)
        val llSedan = findViewById<LinearLayout>(R.id.ll_sedan)
        val llHatchback = findViewById<LinearLayout>(R.id.ll_hatchback)
        intimeEditText = findViewById(R.id.intime)
        lnrLytCamera = findViewById(R.id.lnrLytCamera)
        imgBtnCamera = findViewById(R.id.imgBtnCamera)

        recyclerView = findViewById(R.id.recyclerViewImages)
        imageAdapter = ImageAdapter(this, selectedImageUris)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = imageAdapter

        val btnUploadPhotos: Button = findViewById(R.id.btn_upload_photos)

        // Initialize the spinner
        val parkingLotSpinner: Spinner = findViewById(R.id.spinner_parkinglot)

        // Create an adapter using the string array defined in strings.xml
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.parking_lot_items, // reference to the string array
            android.R.layout.simple_spinner_item // layout for the dropdown items
        )

        // Set the dropdown layout for the spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        parkingLotSpinner.adapter = adapter

        // Set an item selected listener to handle user selection
        parkingLotSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                // Get the selected item
                val selectedParkingLot = parentView?.getItemAtPosition(position).toString()

                // You can perform actions based on the selected parking lot (e.g., store it, display it)
                println("Selected Parking Lot: $selectedParkingLot")

            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Handle case where no selection is made (optional)
            }
        }

        btnUploadPhotos.setOnClickListener { openImagePicker() }

        // Set click listeners for each item
        llSuv.setOnClickListener {
            setSelected(llSuv)
        }

        llLuxury.setOnClickListener {
            setSelected(llLuxury)
        }

        lnrLytCamera.setOnClickListener {
            startActivity(Intent(this@CarFormActivity, OcrActivity::class.java))
        }

        imgBtnCamera.setOnClickListener {
            startActivity(Intent(this@CarFormActivity, OcrActivity::class.java))
        }

        llSedan.setOnClickListener {
            setSelected(llSedan)
        }

        llHatchback.setOnClickListener {
            setSelected(llHatchback)
        }

        intimeEditText.setOnClickListener {
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
                    intimeEditText.setText(formattedTime)
                },
                hour, // Current hour
                minute, // Current minute
                true // Use 24-hour format
            )

            // Show the TimePickerDialog
            timePickerDialog.show()
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


    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            // Create a file for storing the image
            val photoFile: File? = try {
                createImageFile() // You'll need to implement this to create a file
            } catch (e: IOException) {
                null
            }
            photoFile?.let {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "com.elite.parking.fileprovider",  // Your app's file provider authority
                    it
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(intent, TAKE_PHOTO_REQUEST)
            }
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
                                selectedImageUris.add(imageUri)
                            }
                        } else if (data.data != null) {
                            // Single image selected
                            selectedImageUris.add(data.data!!)
                        }
                    }
                }

                // Handle image from camera capture
                TAKE_PHOTO_REQUEST -> {
                    val photoUri = data?.data
                    if (photoUri != null) {
                        selectedImageUris.add(photoUri)
                    }
                }
            }
            imageAdapter.notifyDataSetChanged()  // Notify the adapter that data has changed
        }
    }
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create a timestamp for the image file name
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        // Get the app's external files directory for pictures
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        // Create and return the image file
        return File.createTempFile(
            "JPEG_${timestamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

}