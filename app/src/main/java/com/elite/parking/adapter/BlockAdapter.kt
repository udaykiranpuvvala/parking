package com.elite.parking.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elite.parking.FloorAdapter
import com.elite.parking.Model.parkingslots.Block
import com.elite.parking.Model.parkingslots.Floor
import com.elite.parking.Model.parkingslots.ParkingSlots
import com.elite.parking.R

class BlockAdapter(
    private val context: Context,
    private val blockList: List<Block>,
    private val onSlotSelected: (ParkingSlots, Floor, Block) -> Unit
) : RecyclerView.Adapter<BlockAdapter.BlockViewHolder>() {

    // Store the selected block position
    private var selectedBlockPosition: Int = -1

    inner class BlockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val blockName: TextView = itemView.findViewById(R.id.blockName)
        val floorRecyclerView: RecyclerView = itemView.findViewById(R.id.floorRecycler)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_block, parent, false)
        return BlockViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlockViewHolder, position: Int) {
        val block = blockList[position]
        holder.blockName.text = "Block ${block.blockNo}"

        // Set up the floor RecyclerView for this block
        val floorAdapter = FloorAdapter(context, block.floors, block, onSlotSelected)
        holder.floorRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.VERTICAL, false)
        holder.floorRecyclerView.adapter = floorAdapter

        // If this block is selected, make the floor RecyclerView visible
        holder.floorRecyclerView.visibility = if (position == selectedBlockPosition) View.VISIBLE else View.GONE

        // Handle block click event to toggle visibility of its floor RecyclerView
        holder.itemView.setOnClickListener {
            if (selectedBlockPosition == position) {
                // If the block is already selected, unselect it (hide the floors)
                selectedBlockPosition = -1
            } else {
                // If this block is clicked, show its floors and hide others
                selectedBlockPosition = position
            }
            notifyDataSetChanged()  // Refresh the recycler view to apply changes
        }
    }

    override fun getItemCount(): Int = blockList.size
}








