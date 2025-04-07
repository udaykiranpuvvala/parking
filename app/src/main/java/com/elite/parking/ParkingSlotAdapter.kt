package com.elite.parking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.elite.parking.Model.parkingslots.ParkingSlot

class ParkingSlotAdapter(
    private val context: Context,
    private val parkingSlots: List<ParkingSlot>,
    private val onSlotSelected: (ParkingSlot) -> Unit
) : RecyclerView.Adapter<ParkingSlotAdapter.ParkingSlotViewHolder>() {

    // ViewHolder for Parking Slot
    inner class ParkingSlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val slotTextView: TextView = itemView.findViewById(R.id.slotTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val parkingSlot = parkingSlots[position]
                    if (parkingSlot.availabilityStatus == 1) { // Check if available
                        onSlotSelected(parkingSlot)
                    }
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
        holder.slotTextView.text = parkingSlot.parkingNo

        when (parkingSlot.availabilityStatus) {
            1 -> { // Available
                holder.slotTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
                holder.slotTextView.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
            else -> { // Unavailable
                holder.slotTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.viewColor))
                holder.slotTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        }
    }

    override fun getItemCount(): Int = parkingSlots.size
}
