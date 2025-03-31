package com.elite.parking.loader

import android.graphics.PorterDuff
import android.os.Build
import android.os.Handler
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat

class ProgressBarUtility(private val context: Context) {

    private var progressBar: ProgressBar? = null
    private var progressBarContainer: FrameLayout? = null
    private var overlayView: View? = null

    init {
        // Initialize ProgressBar container and ProgressBar
        progressBarContainer = FrameLayout(context)
        progressBar = ProgressBar(context)

        // Set up layout parameters for the ProgressBar
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        params.gravity = android.view.Gravity.CENTER // Center the progress bar

        progressBar?.layoutParams = params
        progressBar?.isIndeterminate = true

        // Create a transparent overlay to block interactions behind
        overlayView = View(context)
        overlayView?.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        overlayView?.setBackgroundColor(Color.parseColor("#99000000")) // Semi-transparent black
        overlayView?.setClickable(true) // Block all touch interactions

        progressBarContainer?.addView(progressBar)
        progressBarContainer?.addView(overlayView) // Add the overlay on top of the container
    }

    // Function to show the progress bar dynamically
    fun show(parentLayout: FrameLayout) {
        if (progressBarContainer?.parent == null) {
            parentLayout.addView(progressBarContainer)
        }
        // Ensure the progress bar and overlay are visible
        progressBarContainer?.visibility = View.VISIBLE
        overlayView?.visibility = View.VISIBLE
    }

    // Function to hide the progress bar dynamically
    fun hide() {
        // Hide progress bar and overlay
        progressBarContainer?.visibility = View.GONE
        overlayView?.visibility = View.GONE
    }

    // Function to update the progress (can be used for determinate progress bars)
    fun updateProgress(progress: Int) {
        progressBar?.apply {
            isIndeterminate = false
            this.progress = progress
        }
    }

    // Function to change the color of the progress bar
    fun setProgressBarColor(colorRes: Int) {
        val color = ContextCompat.getColor(context, colorRes)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // For API 21 and above
            progressBar?.progressDrawable?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        } else {
            // For older Android versions
            progressBar?.indeterminateDrawable?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
    }
}
