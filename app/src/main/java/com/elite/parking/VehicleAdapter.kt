package com.elite.parking

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.text.format.DateUtils.formatDateTime
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elite.parking.Model.login.Vehicle
import com.google.android.material.imageview.ShapeableImageView
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.*

class VehicleAdapter(
    private val context: Context,
    private var vehicleList: List<Vehicle>,
    private val onItemClick: (Vehicle) -> Unit
) : RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {

    init {
        vehicleList = sortVehicles(vehicleList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.parking_item, parent, false)
        return VehicleViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val vehicle = vehicleList[position]
        holder.bind(vehicle, position)
    }

    override fun getItemCount(): Int = vehicleList.size

    fun updateData(newList: List<Vehicle>) {
        vehicleList = sortVehicles(newList)
        notifyDataSetChanged()
    }

    private fun sortVehicles(list: List<Vehicle>): List<Vehicle> {
        return list.sortedWith(
            compareByDescending<Vehicle> { vehicle ->
                parseTime(vehicle.inTime)
            }
        )
    }

    private fun parseTime(timeString: String): Date {
        return try {
            val sdf =
                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) // Matching inTime format
            sdf.parse(timeString) ?: Date(0)
        } catch (e: Exception) {
            Date(0)
        }
    }

    inner class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val vehicleNo: TextView = itemView.findViewById(R.id.vehicleNo)
        private val inTime: TextView = itemView.findViewById(R.id.inTime)
        private val outTime: TextView = itemView.findViewById(R.id.outTime)
        private val status: TextView = itemView.findViewById(R.id.status)
        private val hookNumber: TextView = itemView.findViewById(R.id.hookNumber)
        private val imageView: ShapeableImageView = itemView.findViewById(R.id.imageView)
        private val lnrBackground: LinearLayout = itemView.findViewById(R.id.lnrBackground)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(vehicle: Vehicle, position: Int) {
            vehicleNo.text = vehicle.vehicleNo
            inTime.text =
                "Check In: ${(convertDateFormat(vehicle.inTime))}"

            outTime.text = if (!TextUtils.isEmpty(vehicle.outTime)) {
                "Check Out: ${convertDateFormat(vehicle.outTime.toString())}"
            } else {
                "Check Out: --/--/-- --:--"
            }

            hookNumber.text = vehicle.hookNo
            status.text = if (vehicle.status == 1) "Parked" else "Completed"

            lnrBackground.setBackgroundResource(
                if (vehicle.status == 1) R.drawable.status_ribbon_bg
                else R.drawable.status_ribbon_left
            )
            Glide.with(itemView.context)
                .load(vehicle.imageUrl)
                .placeholder(R.drawable.ic_default_image)
                .error(R.drawable.ic_default_image)
                .into(imageView)

            // Highlight recent items
            if (position == 0) {
                itemView.post {
                    itemView.animate()
                        .scaleX(1.02f)
                        .scaleY(1.02f)
                        .setDuration(200)
                        .withEndAction {
                            itemView.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start()
                        }
                        .start()
                }
            }

            itemView.setOnClickListener { onItemClick(vehicle) }
        }
    }

    fun convertDateFormat(inputDate: String?): String {
        try {
            val inputFormat =
                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) // Matching input format
            val outputFormat = SimpleDateFormat("hh:mm a, dd MMM", Locale.getDefault())
            val date = inputFormat.parse(inputDate)
            date ?: throw IllegalArgumentException("Invalid input time format")
            return outputFormat.format(date)
        } catch (e: Exception) {
            return "Error: ${e.message}"
        }
    }
}




