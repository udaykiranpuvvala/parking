package com.elite.parking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FloorAdapter(
    private val context: Context,
    private val floors: List<String>,  // List of floors to be displayed horizontally
    private val onFloorSelected: (String) -> Unit
) : RecyclerView.Adapter<FloorAdapter.FloorViewHolder>() {

    inner class FloorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val floorTextView: TextView = itemView.findViewById(R.id.tvFloorHeader)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedFloor = floors[position]
                    onFloorSelected(selectedFloor)  // Notify selection of floor
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FloorViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_header, parent, false)
        return FloorViewHolder(view)
    }

    override fun onBindViewHolder(holder: FloorViewHolder, position: Int) {
        val floor = floors[position]
        holder.floorTextView.text = floor
    }

    override fun getItemCount(): Int = floors.size
}
