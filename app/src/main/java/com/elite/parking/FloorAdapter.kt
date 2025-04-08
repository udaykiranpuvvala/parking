package com.elite.parking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elite.parking.Model.parkingslots.Floor
import com.elite.parking.adapter.SlotAdapter

class FloorAdapter(private val floors: List<Floor>) :
    RecyclerView.Adapter<FloorAdapter.FloorViewHolder>() {

    inner class FloorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val floorTitle: TextView = itemView.findViewById(R.id.tv_floor_title)
        val slotsRecyclerView: RecyclerView = itemView.findViewById(R.id.rv_slots)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FloorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_floor, parent, false)
        return FloorViewHolder(view)
    }

    override fun onBindViewHolder(holder: FloorViewHolder, position: Int) {
        val floor = floors[position]
        holder.floorTitle.text = "Floor ${floor.floorNo}"

        holder.slotsRecyclerView.layoutManager = GridLayoutManager(holder.itemView.context, 3)
        holder.slotsRecyclerView.adapter = SlotAdapter(floor.slots)
        holder.slotsRecyclerView.isNestedScrollingEnabled = false
    }

    override fun getItemCount() = floors.size
}

