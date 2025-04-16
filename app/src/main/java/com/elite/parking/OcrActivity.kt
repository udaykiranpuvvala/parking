package com.elite.parking

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class OcrActivity : AppCompatActivity() {

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    private lateinit var toolBarback: TextView
    private lateinit var btnSubmit: Button
    private lateinit var refresh: ImageView
    private lateinit var lnrRefresh: LinearLayout
    private lateinit var overlayDim: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocr)

        imageView = findViewById(R.id.imageView)
        textView = findViewById(R.id.textView)
        btnSubmit = findViewById(R.id.btnSubmit)
        refresh = findViewById(R.id.refresh)
        lnrRefresh = findViewById(R.id.lnrRefresh)
        //toolBarback = findViewById(R.id.toolBarback)
        overlayDim = findViewById(R.id.overlayDim)
        val captureButton: Button = findViewById(R.id.captureButton)

        // Request camera permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101)
        } else {
            startCamera()
        }

      /*  toolBarback.setOnClickListener {
            finish()
        }*/

        captureButton.setOnClickListener {
            try {
                takePhoto()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@OcrActivity, "Please provide valid image to Capture Number Plate", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        btnSubmit.setOnClickListener {
            if (!textView.text.toString().isNullOrBlank()) {
                val resultIntent = Intent()
                resultIntent.putExtra("key", textView.text.toString())
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Provide proper Number Plate to proceed", Toast.LENGTH_SHORT).show()
            }
        }

        refresh.setOnClickListener {
            imageView.setImageDrawable(null)
            lnrRefresh.visibility= View.GONE
            btnSubmit.visibility= View.GONE
            textView.setText("Capture Image again")
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(findViewById<PreviewView>(R.id.viewFinder).surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                Log.e("CameraX", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        imageCapture.takePicture(ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val bitmap = imageToBitmap(image)
                imageView.setImageBitmap(bitmap)
                processImageForOCR(bitmap)
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraX", "Photo capture failed: ${exception.message}", exception)
            }
        })
    }

    private fun imageToBitmap(image: ImageProxy): Bitmap {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun processImageForOCR(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                if (!visionText.text.isNullOrBlank()) {
                    var extractedText = visionText.text
                    extractedText = extractedText.replace("IND", "", ignoreCase = true) // Remove "IND"
                    extractedText = extractedText.replace("\\s+".toRegex(), "") // Remove all whitespaces
                    extractedText = extractedText.trim()

                    textView.text = extractedText

                    lnrRefresh.visibility = View.VISIBLE
                    btnSubmit.visibility = View.VISIBLE
                } else {
                    textView.text = ""
                    Toast.makeText(this, "Please provide a valid image to Capture Number Plate", Toast.LENGTH_LONG).show()
                    lnrRefresh.visibility = View.GONE
                    btnSubmit.visibility = View.GONE
                }
            }
            .addOnFailureListener { e ->
                Log.e("OCR", "Text recognition failed", e)
                textView.text = "OCR failed"
                lnrRefresh.visibility = View.GONE
                btnSubmit.visibility = View.GONE
            }
    }



    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
