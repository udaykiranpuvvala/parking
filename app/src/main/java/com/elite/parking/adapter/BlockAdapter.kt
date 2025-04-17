package com.elite.parking.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
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
    private val onSlotSelected: (ParkingSlots, Floor, Block) -> Unit,
    private val onBlockSelected: (Int) -> Unit
) : RecyclerView.Adapter<BlockAdapter.BlockViewHolder>() {

    private var selectedPosition: Int = 0  // Select first item by default

    inner class BlockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val blockName: TextView = itemView.findViewById(R.id.blockName)
        val tickOverlay: ImageView = itemView.findViewById(R.id.tickIcon)
        val linearBlock: LinearLayout = itemView.findViewById(R.id.linearBlock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_block, parent, false)
        return BlockViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlockViewHolder, position: Int) {
        val block = blockList[position]
        holder.blockName.text = "${block.blockName}"

        if (position == selectedPosition) {
            holder.linearBlock.setBackgroundResource(R.drawable.selector_bg)
            // holder.tickOverlay.visibility = View.VISIBLE
        } else {
            holder.linearBlock.setBackgroundResource(R.drawable.unselector_bg)
            // holder.tickOverlay.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onBlockSelected(position)
        }
    }

    override fun getItemCount(): Int = blockList.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        if (blockList.isNotEmpty()) {
            Handler(Looper.getMainLooper()).post {
                onBlockSelected(selectedPosition)
            }
        }
    }
}












