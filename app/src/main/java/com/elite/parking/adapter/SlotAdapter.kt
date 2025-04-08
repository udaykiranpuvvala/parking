package com.elite.parking.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.elite.parking.Model.parkingslots.ParkingSlot
import com.elite.parking.R

class SlotAdapter(private val slots: List<ParkingSlot>) :
    RecyclerView.Adapter<SlotAdapter.SlotViewHolder>() {

    inner class SlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val slotNumber: TextView = itemView.findViewById(R.id.slotNumber)
        val slotContainer: CardView = itemView.findViewById(R.id.slotContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_slot, parent, false)
        return SlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val slot = slots[position]
        holder.slotNumber.text = slot.parkingNo

        val colorRes = if (slot.availabilityStatus == 1) {
            R.color.green
        } else {
            R.color.viewColor
        }

        holder.slotContainer.setCardBackgroundColor(
            ContextCompat.getColor(holder.itemView.context, colorRes)
        )

        holder.itemView.setOnClickListener {
            // Handle slot click
        }
    }

    override fun getItemCount() = slots.size
}
