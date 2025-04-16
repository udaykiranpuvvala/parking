package com.elite.parking


import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import com.budiyev.android.codescanner.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class QRScannerDialog(private val onResult: (String) -> Unit) : DialogFragment() {

    private var codeScanner: CodeScanner? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.activity_qrcode, null)
        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .setCancelable(true)
            .create()

        codeScanner = CodeScanner(requireContext(), scannerView).apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                val result = it.text
                requireActivity().runOnUiThread {
                    onResult(result)
                    dialog.dismiss()
                }
            }

            errorCallback = ErrorCallback {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Camera error: ${it.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        scannerView.setOnClickListener {
            codeScanner?.startPreview()
        }

        dialog.setOnShowListener {
            codeScanner?.startPreview()
        }

        dialog.setOnDismissListener {
            codeScanner?.releaseResources()
        }

        return dialog
    }

    override fun onPause() {
        codeScanner?.releaseResources()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        codeScanner?.startPreview()
    }
}

