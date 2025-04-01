package com.elite.parking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.elite.parking.Model.ParkingSlot

class ParkingSlotsAdapter(private val context: Context, private val parkingSlots: List<ParkingSlot>) :
    RecyclerView.Adapter<ParkingSlotsAdapter.ParkingSlotViewHolder>() {

    private var selectedPosition = -1  // To keep track of the selected item

    // ViewHolder to bind each parking slot item
    inner class ParkingSlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val slotTextView: TextView = itemView.findViewById(R.id.slotTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                // Check if it's an available slot (green)
                if (position != RecyclerView.NO_POSITION && parkingSlots[position].isAvailable) {
                    // Toggle selection for available slots only
                    if (selectedPosition == position) {
                        selectedPosition = -1  // Deselect if it's already selected
                    } else {
                        selectedPosition = position  // Select the clicked item
                    }
                    // Notify the adapter to refresh the state of the selected item
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingSlotViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_parking_slot, parent, false)
        return ParkingSlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParkingSlotViewHolder, position: Int) {
        val parkingSlot = parkingSlots[position]
        holder.slotTextView.text = parkingSlot.slotId

        // Set background color based on availability
        if (parkingSlot.isAvailable) {
            holder.slotTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.green))  // Available slot (green)
        } else {
            holder.slotTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.red))    // Unavailable slot (red)
        }

        // Highlight the selected item with a different color (blue) for available slots
        if (position == selectedPosition && parkingSlot.isAvailable) {
            holder.slotTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.maroon))  // Selected item highlighted
        }

    }

    override fun getItemCount(): Int {
        return parkingSlots.size
    }
}
