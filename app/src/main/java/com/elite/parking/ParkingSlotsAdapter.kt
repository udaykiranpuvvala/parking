package com.elite.parking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.elite.parking.Model.ParkingSlot

class ParkingSlotsAdapter(
    private val context: Context,
    private var parkingSlots: List<ParkingSlot>,
    private val listener: OnParkingSlotSelectedListener,
    private val dialog: AlertDialog
) : RecyclerView.Adapter<ParkingSlotsAdapter.ParkingSlotViewHolder>() {

    private var selectedPosition: Int = -1

    inner class ParkingSlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val slotTextView: TextView = itemView.findViewById(R.id.slotTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                val parkingSlot = parkingSlots[position]
                if (parkingSlot.isAvailable) {
                    selectSlot(position)  // Select this slot
                    listener.onParkingSlotSelected(parkingSlot)  // Notify the listener (activity)
                    notifyDataSetChanged()  // Refresh the view

                    // Dismiss the dialog after selecting a parking slot
                    dialog.dismiss()  // Dismiss the dialog
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingSlotViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_parking_slot, parent, false)
        return ParkingSlotViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ParkingSlotViewHolder, position: Int) {
        val parkingSlot = parkingSlots[position]
        holder.slotTextView.text = parkingSlot.name

        if (parkingSlot.isAvailable) {
            if (position == selectedPosition) {
                holder.slotTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.maroon))
            } else {
                holder.slotTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
            }
        } else {
            holder.slotTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
        }
    }

    override fun getItemCount(): Int {
        return parkingSlots.size
    }

    fun updateParkingSlots(newParkingSlots: List<ParkingSlot>) {
        parkingSlots = newParkingSlots
        notifyDataSetChanged()
    }

    private fun selectSlot(position: Int) {
        val previousSelectedPosition = selectedPosition
        if (previousSelectedPosition != -1) {
            parkingSlots[previousSelectedPosition].isSelected = false
        }
        selectedPosition = position
        parkingSlots[selectedPosition].isSelected = true
    }
}



