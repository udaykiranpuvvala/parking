package com.elite.parking.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.elite.parking.Model.parkingslots.Block
import com.elite.parking.Model.parkingslots.Floor
import com.elite.parking.Model.parkingslots.ParkingSlots
import com.elite.parking.R

class SlotAdapter(
    private val context: Context,
    private val slots: List<ParkingSlots>,
    private val floor: Floor,
    private val block: Block,
    private val onSlotSelected: (ParkingSlots, Floor, Block) -> Unit
) : RecyclerView.Adapter<SlotAdapter.SlotViewHolder>() {

    class SlotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val slotTitle: TextView = view.findViewById(R.id.slotTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_slot, parent, false)
        return SlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val slot = slots[position]
        holder.slotTitle.text = "Slot: ${slot.parkingNo}"

        // Set the slot color based on its availability status
        when (slot.availabilityStatus) {
            1 -> holder.slotTitle.setBackgroundColor(ContextCompat.getColor(context, R.color.green)) // Available
            else -> holder.slotTitle.setBackgroundColor(ContextCompat.getColor(context, R.color.red)) // Unavailable
        }

        // Set click listener for slot
        holder.itemView.setOnClickListener {
            onSlotSelected(slot, floor, block) // Notify the activity when a slot is selected
        }
    }

    override fun getItemCount(): Int = slots.size
}



