package com.elite.parking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elite.parking.Model.login.Vehicle

class VehicleAdapter(private val context: Context,private val vehicleList: List<Vehicle>) : RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {

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
        private val vehicleType: TextView = itemView.findViewById(R.id.vehicleType)
        private val notes: TextView = itemView.findViewById(R.id.notes)
        private val inTime: TextView = itemView.findViewById(R.id.inTime)
        private val status: TextView = itemView.findViewById(R.id.status)
        private val hookNumber: TextView = itemView.findViewById(R.id.hookNumber)
        private val parkingId: TextView = itemView.findViewById(R.id.parkingId)
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(vehicle: Vehicle) {
            vehicleNo.text = vehicle.vehicleNo
            vehicleType.text = vehicle.vehicleType
            notes.text = vehicle.notes
            inTime.text = vehicle.inTime
            hookNumber.text = vehicle.hookNo
            parkingId.text = vehicle.parkingId
            status.text = if (vehicle.status == 1) "Parked" else "Left"

            Glide.with(context).load(R.drawable.car2).into(imageView)
        }
    }
}