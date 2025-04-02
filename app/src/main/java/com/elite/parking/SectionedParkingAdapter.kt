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

/*class ParkingSlotsAdapter(
    private val context: Context,
    private var items: List<ListItem>,
    private val listener: OnParkingSlotSelectedListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
         const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headerText: TextView = itemView.findViewById(R.id.headerText)
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val slotTextView: TextView = itemView.findViewById(R.id.slotTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val parkingSlotItem = items[position] as? ListItem.ParkingSlotItem
                    parkingSlotItem?.let { slotItem ->
                        if (slotItem.slot.isAvailable) {
                            updateSelectedSlot(position)
                            listener.onParkingSlotSelected(slotItem.slot)
                        }
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ListItem.Header -> TYPE_HEADER
            is ListItem.ParkingSlotItem -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.item_parking_slot, parent, false)
            ItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ListItem.Header -> (holder as HeaderViewHolder).headerText.text = item.title
            is ListItem.ParkingSlotItem -> {
                val parkingSlot = item.slot
                val itemHolder = holder as ItemViewHolder
                itemHolder.slotTextView.text = parkingSlot.name

                if (parkingSlot.isAvailable) {
                    if (position == selectedPosition) {
                        // Selected state
                        itemHolder.slotTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.maroon))
                        itemHolder.slotTextView.setTextColor(ContextCompat.getColor(context, R.color.white)) // Change text color
                    } else {
                        // Normal state
                        itemHolder.slotTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
                        itemHolder.slotTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
                    }
                } else {
                    // Unavailable state
                    itemHolder.slotTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
                    itemHolder.slotTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    private fun updateSelectedSlot(position: Int) {
        val previousSelectedPosition = selectedPosition
        selectedPosition = position

        if (previousSelectedPosition != RecyclerView.NO_POSITION) notifyItemChanged(previousSelectedPosition)
        notifyItemChanged(selectedPosition)
    }
}*/

class SectionedParkingAdapter(
    private val context: Context,
    private var items: List<ListItem>,
    private val onSlotSelected: (ParkingSlot) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
         const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headerText: TextView = itemView.findViewById(R.id.headerText)
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val slotTextView: TextView = itemView.findViewById(R.id.slotTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val parkingSlotItem = items[position] as? ListItem.ParkingSlotItem
                    parkingSlotItem?.let { slotItem ->
                        if (slotItem.slot.availabilityStatus == 2) {
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
            is ListItem.Header -> TYPE_HEADER
            is ListItem.ParkingSlotItem -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.item_parking_slot, parent, false)
            ItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ListItem.Header -> (holder as HeaderViewHolder).headerText.text = "Floor No: ${item.floorNo}"
            is ListItem.ParkingSlotItem -> {
                val parkingSlot = item.slot
                val itemHolder = holder as ItemViewHolder
                itemHolder.slotTextView.text = "Block: ${parkingSlot.blockNo}"

                if (parkingSlot.availabilityStatus == 2) {
                    if (position == selectedPosition) {
                        itemHolder.slotTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.maroon))
                        itemHolder.slotTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
                    } else {
                        itemHolder.slotTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
                        itemHolder.slotTextView.setTextColor(ContextCompat.getColor(context, R.color.black))
                    }
                } else {
                    itemHolder.slotTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
                    itemHolder.slotTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    private fun updateSelectedSlot(position: Int) {
        val previousSelectedPosition = selectedPosition
        selectedPosition = position

        if (previousSelectedPosition != RecyclerView.NO_POSITION) notifyItemChanged(previousSelectedPosition)
        notifyItemChanged(selectedPosition)
    }

    fun updateData(newItems: List<ListItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}






