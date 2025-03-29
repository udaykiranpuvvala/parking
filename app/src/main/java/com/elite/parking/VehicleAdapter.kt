package com.elite.parking

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elite.parking.Model.Vehicle

class VehicleAdapter (private val vehicleList: List<Vehicle>, private val context: Context, private val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.parking_item, parent, false)
        return VehicleViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val vehicle = vehicleList[position]
        holder.vehicleNumber.text = vehicle.vehicleNumber
        holder.make.text = vehicle.make
        holder.model.text = vehicle.model
        holder.hookNumber.text = vehicle.hookNumber
        holder.status.text = vehicle.status
        if (holder.status.text.toString().equals("InProgress")){
            holder.status.setTextColor(Color.RED)
        }else{
            holder.status.setTextColor(Color.GREEN)
        }
        /*if (!vehicle.imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(vehicle.imageUrl)
                .into(holder.imageView) // This loads the image into the ImageView
        } else {
            // Set a placeholder or default image if no URL is provided
           holder.imageView.setImageResource(R.drawable.ic_launcher_background) // Change to your placeholder image
        }*/
        Glide.with(holder.itemView.context)
            .load(vehicle.imageUrl)
            .into(holder.imageView) // This loads the image into the ImageView
    }

    override fun getItemCount(): Int = vehicleList.size

    inner class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vehicleNumber: TextView = itemView.findViewById(R.id.vehicleNumber)
        val make: TextView = itemView.findViewById(R.id.make)
        val model: TextView = itemView.findViewById(R.id.model)
        val hookNumber: TextView = itemView.findViewById(R.id.hookNumber)
        val status: TextView = itemView.findViewById(R.id.status)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        init {
            // 3. Set the click listener for the itemView
            itemView.setOnClickListener {
                // Pass the adapter position (i.e., the clicked item) to the listener
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Trigger the item click event
                    onItemClickListener.onItemClick(position)
                }
            }
        }

    }
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}