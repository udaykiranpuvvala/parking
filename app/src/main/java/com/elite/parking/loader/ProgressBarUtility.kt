package com.elite.parking.loader

import android.graphics.PorterDuff
import android.os.Build
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.content.Context
import androidx.core.content.ContextCompat

class ProgressBarUtility(private val context: Context) {

    private var progressBar: ProgressBar? = null
    private var progressBarContainer: FrameLayout? = null

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

        progressBarContainer?.addView(progressBar)
        progressBarContainer?.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))

        // Make the container non-clickable and non-focusable
        progressBarContainer?.isClickable = false
        progressBarContainer?.isFocusable = false
    }

    // Function to show the progress bar dynamically
    fun show(parentLayout: FrameLayout) {
        if (progressBarContainer?.parent == null) {
            parentLayout.addView(progressBarContainer)
        }
        progressBarContainer?.visibility = FrameLayout.VISIBLE
    }

    // Function to hide the progress bar dynamically
    fun hide() {
        progressBarContainer?.visibility = FrameLayout.GONE
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



