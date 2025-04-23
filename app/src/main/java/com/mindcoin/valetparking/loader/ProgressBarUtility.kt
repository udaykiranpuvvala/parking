package com.mindcoin.valetparking.loader


import com.mindcoin.valetparking.R


import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.widget.TextView

object ProgressBarUtility {
    private var progressDialog: AlertDialog? = null
    private var isDismissing = false

    fun showProgressDialog(context: Context, message: String = "Loading...") {
        // If already showing or in process of dismissing, wait
        if (progressDialog?.isShowing == true || isDismissing) {
            return
        }

        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.progress_dialog_layout, null)
        dialogView.findViewById<TextView>(R.id.tvProgressMessage).text = message

        // Cancel any existing dialog properly
        progressDialog?.dismiss()

        progressDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create().apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setOnDismissListener {
                    progressDialog = null
                    isDismissing = false
                }
                show()
            }
    }

    fun dismissProgressDialog() {
        if (progressDialog?.isShowing == true) {
            isDismissing = true
            progressDialog?.dismiss()
        }
        progressDialog = null
    }
}
