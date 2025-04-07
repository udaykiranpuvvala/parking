package com.elite.parking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.elite.parking.Model.parkingslots.ListItem
import com.elite.parking.Model.parkingslots.ParkingSlot

class SectionedParkingAdapter(
    private val context: Context,
    private var items: List<ListItem>,
    private val onSlotSelected: (ParkingSlot) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        internal const val TYPE_BLOCK_HEADER = 0
        internal const val TYPE_FLOOR_HEADER = 1
        private const val TYPE_PARKING_SLOT = 2
    }

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    /*  sealed class ListItem {
          data class FloorHeader(val floorNo: String) : ListItem()
          data class BlockHeader(val blockNo: String) : ListItem()
          data class ParkingSlotItem(val slot: ParkingSlot) : ListItem()
      }*/



    inner class BlockHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headerText: TextView = itemView.findViewById(R.id.tvBlockHeader)
    }

    inner class FloorHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subHeaderText: TextView = itemView.findViewById(R.id.tvFloorHeader)
    }

    inner class ParkingSlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val slotTextView: TextView = itemView.findViewById(R.id.slotTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = items[position] as? ListItem.ParkingSlotItem
                    item?.let { slotItem ->
                        if (slotItem.slot.availabilityStatus == 1) {
                            updateSelectedSlot(position)
                            onSlotSelected(slotItem.slot)
                        }
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ListItem.FloorHeader -> TYPE_FLOOR_HEADER
            is ListItem.BLockHeader -> TYPE_BLOCK_HEADER
            is ListItem.ParkingSlotItem -> TYPE_PARKING_SLOT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_BLOCK_HEADER -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_block_header, parent, false)
                BlockHeaderViewHolder(view)
            }
            TYPE_FLOOR_HEADER -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_header, parent, false)
                FloorHeaderViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_parking_slot, parent, false)
                ParkingSlotViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ListItem.BLockHeader -> {
                (holder as BlockHeaderViewHolder).headerText.text = "Floor: ${item.blockNo}"
            }
            is ListItem.FloorHeader -> {
                (holder as FloorHeaderViewHolder).subHeaderText.text = "Block: ${item.floorNo}"
            }

            is ListItem.ParkingSlotItem -> {
                val parkingSlot = item.slot
                val itemHolder = holder as ParkingSlotViewHolder
                itemHolder.slotTextView.text = parkingSlot.parkingNo

                when (parkingSlot.availabilityStatus) {
                    1 -> { // Available
                        if (position == selectedPosition) {
                            itemHolder.slotTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.maroon))
                            itemHolder.slotTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
                        } else {
                            itemHolder.slotTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
                            itemHolder.slotTextView.setTextColor(ContextCompat.getColor(context, R.color.black))
                        }
                    }
                    else -> { // Unavailable
                        itemHolder.slotTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.viewColor))
                        itemHolder.slotTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    private fun updateSelectedSlot(position: Int) {
        val previousSelectedPosition = selectedPosition
        selectedPosition = position
        if (previousSelectedPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(previousSelectedPosition)
        }
        notifyItemChanged(selectedPosition)
    }

    fun updateData(newItems: List<ListItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}