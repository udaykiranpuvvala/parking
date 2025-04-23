package com.mindcoin.valetparking.adapter

class ParkingSlotAdapter() {

    // ViewHolder for Block items
    /*inner class BlockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val blockTitle: TextView = itemView.findViewById(R.id.tv_block_title)
        val floorsRecyclerView: RecyclerView = itemView.findViewById(R.id.rv_floors)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_parking_block, parent, false)
        return BlockViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlockViewHolder, position: Int) {
        val block = blocks[position]

        // Set block title
        holder.blockTitle.text = "Block ${block.blockNo}"

        // Setup nested RecyclerView for floors
        holder.floorsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = FloorAdapter(block.floors)
            isNestedScrollingEnabled = false  // Improve scrolling performance
            setHasFixedSize(true)            // Optimize performance
        }
    }

    override fun getItemCount(): Int = blocks.size

    // Nested FloorAdapter
    inner class FloorAdapter(private val floors: List<Floor>) :
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

            // Set floor title
            holder.floorTitle.text = "Floor ${floor.floorNo}"

            // Setup nested RecyclerView for slots
            holder.slotsRecyclerView.apply {
                layoutManager = GridLayoutManager(context, 3)  // 3 columns
                adapter = SlotAdapter(floor.slots)
                isNestedScrollingEnabled = false
                setHasFixedSize(true)
            }
        }

        override fun getItemCount(): Int = floors.size

        // Nested SlotAdapter
        inner class SlotAdapter(private val slots: List<ParkingSlot>) :
            RecyclerView.Adapter<SlotAdapter.SlotViewHolder>() {

            inner class SlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                val slotCard: CardView = itemView.findViewById(R.id.card_slot)
                val slotNumber: TextView = itemView.findViewById(R.id.tv_slot_number)
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_parking_slot, parent, false)
                return SlotViewHolder(view)
            }

            override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
                val slot = slots[position]

                // Set slot number
                holder.slotNumber.text = slot.parkingNo

                // Set color based on availability
                val backgroundColor = if (slot.availabilityStatus == 1) {
                    ContextCompat.getColor(holder.itemView.context, R.color.green)
                } else {
                    ContextCompat.getColor(holder.itemView.context, R.color.viewColor)
                }
                holder.slotCard.setCardBackgroundColor(backgroundColor)

                // Handle slot click
                holder.itemView.setOnClickListener {
                    // Handle slot selection
                }
            }

            override fun getItemCount(): Int = slots.size
        }
    }*/
}