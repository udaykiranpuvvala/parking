package com.elite.parking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elite.parking.Model.login.Vehicle
import com.google.android.material.imageview.ShapeableImageView

class VehicleAdapter(
    private val context: Context,
    private val vehicleList: List<Vehicle>,
    private val onItemClick: (Vehicle) -> Unit  // Add a lambda function to handle item click
) : RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.parking_item, parent, false)
        return VehicleViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val vehicle = vehicleList[position]
        holder.bind(vehicle)
    }

    override fun getItemCount(): Int = vehicleList.size

    inner class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val vehicleNo: TextView = itemView.findViewById(R.id.vehicleNo)
        private val inTime: TextView = itemView.findViewById(R.id.inTime)
        private val status: TextView = itemView.findViewById(R.id.status)
        private val hookNumber: TextView = itemView.findViewById(R.id.hookNumber)
        private val imageView: ShapeableImageView = itemView.findViewById(R.id.imageView)
        private val lnrBackground: LinearLayout = itemView.findViewById(R.id.lnrBackground)

        fun bind(vehicle: Vehicle) {
            vehicleNo.text = vehicle.vehicleNo
            inTime.text = vehicle.inTime
            hookNumber.text = vehicle.hookNo
            status.text = if (vehicle.status == 1) "Parked" else "Left"
            if (vehicle.status == 1) {
                lnrBackground.setBackgroundResource(R.drawable.status_ribbon_bg)
            } else {
                lnrBackground.setBackgroundResource(R.drawable.status_ribbon_left)
            }

            // Load the vehicle image
            Glide.with(itemView.context)
                .load(vehicle.imageUrl)
                .placeholder(R.drawable.car3)
                .error(R.drawable.car3)
                .into(imageView)

            // Set the click listener on the itemView
            itemView.setOnClickListener {
                // Trigger the listener when the item is clicked
                onItemClick(vehicle)
            }
        }
    }
}
