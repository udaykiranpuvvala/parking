package com.elite.parking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elite.parking.Model.parkingslots.Block
import com.elite.parking.Model.parkingslots.Floor
import com.elite.parking.Model.parkingslots.ParkingSlots
import com.elite.parking.adapter.SlotAdapter

class FloorAdapter(private val context: Context, private val floors: List<Floor>, private val block: Block, private val onSlotSelected: (ParkingSlots, Floor, Block) -> Unit) : RecyclerView.Adapter<FloorAdapter.FloorViewHolder>() {

    class FloorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val floorTitle: TextView = view.findViewById(R.id.floorName)
        val view: View = view.findViewById(R.id.view)
        val slotRecycler: RecyclerView = view.findViewById(R.id.slotRecycler)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FloorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_floor, parent, false)
        return FloorViewHolder(view)
    }

    override fun onBindViewHolder(holder: FloorViewHolder, position: Int) {
        val floor = floors[position]
        holder.floorTitle.text = "Floor: ${floor.floorNo}"
        holder.slotRecycler.visibility = if (floor.isExpanded) View.VISIBLE else View.GONE
        holder.view.visibility = if (floor.isExpanded) View.VISIBLE else View.GONE
        holder.floorTitle.setOnClickListener {
            floor.isExpanded = !floor.isExpanded
            notifyItemChanged(position)
        }

        if (floor.isExpanded) {
            holder.slotRecycler.layoutManager = GridLayoutManager(holder.itemView.context, 3)
            holder.slotRecycler.adapter = SlotAdapter(context, floor.slots, floor, block, onSlotSelected) // Pass block and floor
        }
    }

    override fun getItemCount(): Int = floors.size
}


