package com.elite.parking

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elite.parking.Model.login.Vehicle
import com.google.android.material.imageview.ShapeableImageView
import java.text.SimpleDateFormat
import java.util.*
import java.util.*

class VehicleAdapter(
    private val context: Context,
    private var vehicleList: List<Vehicle>,
    private val onItemClick: (Vehicle) -> Unit
) : RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {

    // Sort vehicles by inTime (AM/PM format) and then by createdDate
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

    // Helper method to sort vehicles by inTime and createdDate
    private fun sortVehicles(list: List<Vehicle>): List<Vehicle> {
        return list.sortedWith(
            compareByDescending<Vehicle> { vehicle ->
                // First, sort by inTime
                parseTime(vehicle.inTime)
            }.thenByDescending { vehicle ->
                // Then, sort by createdDate
                parseDate(vehicle.createdDate)
            }
        )
    }

    private fun parseTime(timeString: String): Date {
        return try {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            sdf.parse(timeString) ?: Date(0)
        } catch (e: Exception) {
            Date(0)  // Default date in case of parsing error
        }
    }

    private fun parseDate(dateString: String): Date {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.parse(dateString) ?: Date(0)
        } catch (e: Exception) {
            Date(0)
        }
    }

    // Method to format inTime for display in the "dd MMM hh:mm a" format
    private fun formatInTime(dateString: String): String {
        return try {
            val sdf =
                SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())  // Format with AM/PM
            val date = sdf.parse(dateString)
            SimpleDateFormat("dd MMM hh:mm a", Locale.getDefault()).format(date ?: Date())
        } catch (e: Exception) {
            dateString  // Return the original date string if parsing fails
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

        fun bind(vehicle: Vehicle, position: Int) {
            // Set vehicle data
            vehicleNo.text = vehicle.vehicleNo
            inTime.text =
                "Check In: ${formatInTime(vehicle.inTime + "   " + convertDateFormat(vehicle.createdDate))}"

            outTime.text = if (!TextUtils.isEmpty(vehicle.outTime)) {
                "Check Out: ${vehicle.outTime.toString() + " " + convertDateFormat(vehicle.modifiedDate)}"
            } else {
                "Check Out: --/--/-- --:--"
            }

            hookNumber.text = vehicle.hookNo
            status.text = if (vehicle.status == 1) "Parked" else "Completed"

            // Set background based on status
            lnrBackground.setBackgroundResource(
                if (vehicle.status == 1) R.drawable.status_ribbon_bg
                else R.drawable.status_ribbon_left
            )

            // Load vehicle image
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
        if (inputDate.isNullOrEmpty()) {
            return ""  // Return a message or handle it as per your need
        }

        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("d-MMM-yyyy", Locale.getDefault())

        return try {
            val date: Date = inputFormat.parse(inputDate) ?: throw IllegalArgumentException("")
            outputFormat.format(date)
        } catch (e: Exception) {
            ""
        }
    }

}



