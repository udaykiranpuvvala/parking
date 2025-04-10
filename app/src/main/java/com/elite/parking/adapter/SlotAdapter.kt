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

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    class SlotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val slotTitle: TextView = view.findViewById(R.id.slotTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_slot, parent, false)
        return SlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val slot = slots[position]
        holder.slotTitle.text = "${slot.parkingNo}"

        when (slot.availabilityStatus) {
            1 -> { // Available
                if (position == selectedPosition) {
                    holder.slotTitle.setBackgroundColor(ContextCompat.getColor(context, R.color.maroon))
                    holder.slotTitle.setTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    holder.slotTitle.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
                    holder.slotTitle.setTextColor(ContextCompat.getColor(context, R.color.black))
                }
            }
            else -> { // Unavailable
                holder.slotTitle.setBackgroundColor(ContextCompat.getColor(context, R.color.viewColor))
                holder.slotTitle.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        }

        // Set click listener for slot
        holder.itemView.setOnClickListener {
            if (slot.availabilityStatus == 1) {
                selectedPosition = position
                notifyDataSetChanged()
                // Notify the activity or fragment that a slot has been selected
                onSlotSelected(slot, floor, block)
            }
        }
    }

    override fun getItemCount(): Int = slots.size
}


