package com.mindcoin.valetparking.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mindcoin.valetparking.Model.parkingslots.Block
import com.mindcoin.valetparking.Model.parkingslots.Floor
import com.mindcoin.valetparking.Model.parkingslots.ParkingSlots
import com.mindcoin.valetparking.R

class FloorAdapter(
    private val context: Context,
    private var floors: List<Floor>,
    private val block: Block,
    private val onSlotSelected: (ParkingSlots, Floor, Block) -> Unit
) : RecyclerView.Adapter<FloorAdapter.FloorViewHolder>() {

    class FloorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val floorTitle: TextView = view.findViewById(R.id.floorName)
        val slotRecycler: RecyclerView = view.findViewById(R.id.slotRecycler)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FloorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_floor, parent, false)
        return FloorViewHolder(view)
    }

    override fun onBindViewHolder(holder: FloorViewHolder, position: Int) {
        val floor = floors[position]
        holder.floorTitle.text = "${floor.floorName} (${floor.slots.size} slots)"
        holder.slotRecycler.layoutManager = GridLayoutManager(holder.itemView.context, 5)
        holder.slotRecycler.adapter = SlotAdapter(context, floor.slots, floor, block, onSlotSelected)
    }

    override fun getItemCount(): Int = floors.size

    // 🔍 Optional: Update floors list for filtering or refreshing
    fun updateFloors(newFloors: List<Floor>) {
        this.floors = newFloors
        notifyDataSetChanged()
    }
}