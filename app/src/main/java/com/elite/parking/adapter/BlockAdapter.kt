package com.elite.parking.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elite.parking.FloorAdapter
import com.elite.parking.Model.parkingslots.Block
import com.elite.parking.R

class BlockAdapter(private val blocks: List<Block>) :
    RecyclerView.Adapter<BlockAdapter.BlockViewHolder>() {

    inner class BlockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val blockTitle: TextView = itemView.findViewById(R.id.blockTitle)
        val floorsRecyclerView: RecyclerView = itemView.findViewById(R.id.floorsRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_block, parent, false)
        return BlockViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlockViewHolder, position: Int) {
        val block = blocks[position]
        holder.blockTitle.text = "Block ${block.blockNo}"

        holder.floorsRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.floorsRecyclerView.adapter = FloorAdapter(block.floors)
        holder.floorsRecyclerView.isNestedScrollingEnabled = false
    }

    override fun getItemCount() = blocks.size
}