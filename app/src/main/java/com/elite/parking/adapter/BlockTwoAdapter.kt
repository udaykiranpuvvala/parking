package com.elite.parking.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elite.parking.FloorAdapter
import com.elite.parking.Model.parkingslots.Block
import com.elite.parking.Model.parkingslots.Floor
import com.elite.parking.Model.parkingslots.ParkingSlots
import com.elite.parking.R

class BlockTwoAdapter(
    private val context: Context,
    private var blockList: List<Block>, // This will be updated using updateBlockList()
    private val onSlotSelected: (ParkingSlots, Floor, Block) -> Unit,
    private var selectedBlockPosition: Int = -1
) : RecyclerView.Adapter<BlockTwoAdapter.BlockViewHolder>() {

    inner class BlockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val floorRecyclerView: RecyclerView = itemView.findViewById(R.id.floorRecycler)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_block_two, parent, false)
        return BlockViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlockViewHolder, position: Int) {
        val block = blockList[position]

        val floorAdapter = FloorAdapter(context, block.floors, block, onSlotSelected)
        holder.floorRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        holder.floorRecyclerView.adapter = floorAdapter

        // Only the first block's floors should be expanded by default
        holder.floorRecyclerView.visibility = if (position == 0) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            updateSelectedBlockPosition(position)
        }
    }

    override fun getItemCount(): Int = blockList.size

    // Move selected block to top
    fun updateSelectedBlockPosition(position: Int) {
        if (position in blockList.indices) {
            val selectedBlock = blockList[position]
            val newList = blockList.toMutableList()
            newList.removeAt(position)
            newList.add(0, selectedBlock)
            blockList = newList
            notifyDataSetChanged()
        }
    }

    // Call this from activity when filter/search is applied
    fun updateBlockList(newBlockList: List<Block>) {
        blockList = newBlockList
        notifyDataSetChanged()
    }
}
