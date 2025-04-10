package com.elite.parking.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
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

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    inner class BlockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val blockName: TextView = itemView.findViewById(R.id.blockName)
        val tickOverlay: ImageView = itemView.findViewById(R.id.tickIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_block, parent, false)
        return BlockViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlockViewHolder, position: Int) {
        val block = blockList[position]
        holder.blockName.text = "Block ${block.blockNo}"
        holder.tickOverlay.visibility = if (selectedPosition == position) View.VISIBLE else View.GONE

       /* if (position == selectedPosition) {
            holder.blockName.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, R.drawable.ic_tick, 0 // Tick icon on the right
            )
        } else {
            // No tick
            holder.blockName.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, 0, 0
            )
        }*/

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onBlockSelected(position)
        }
    }

    override fun getItemCount(): Int = blockList.size
}











