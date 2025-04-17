package com.elite.parking.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elite.parking.Model.parkingslots.Block
import com.elite.parking.Model.parkingslots.Floor
import com.elite.parking.Model.parkingslots.ParkingSlots
import com.elite.parking.R
import com.elite.parking.adapter.BlockAdapter
import com.elite.parking.adapter.BlockTwoAdapter
import com.elite.parking.loader.ProgressBarUtility
import com.elite.parking.storage.SharedPreferencesHelper
import com.elite.parking.viewModel.ParkingViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ParkingSlotsActivity : AppCompatActivity() {

    private val parkingViewModel: ParkingViewModel by viewModels()
    private lateinit var parkingViewModelData: ParkingViewModel.ParkingViewModelData
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var userId: String
    private lateinit var token: String
    private lateinit var companyId: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewTwo: RecyclerView
    private lateinit var lnrNoData: LinearLayout
    private lateinit var blockAdapter: BlockAdapter
    private lateinit var blockAdapter2: BlockTwoAdapter

    private lateinit var toolBarback: ImageView
    private lateinit var refreshButton: FloatingActionButton
    private lateinit var searchEditText: EditText

    private var originalBlockList: List<Block> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_slots)

        val vehicleNo = intent.getStringExtra("vehicleNo")
        val serialNumber = intent.getStringExtra("serialNumber")
        val checkintime = intent.getStringExtra("checkintime")
        val vehicleType = intent.getStringExtra("vehicleType")
        val vehicleImage = intent.getStringExtra("vehicleImage")

        toolBarback = findViewById(R.id.toolBarback)
        refreshButton = findViewById(R.id.refreshButton)
        searchEditText = findViewById(R.id.searchEditText)

        toolBarback.setOnClickListener { finish() }

        parkingViewModelData = ViewModelProvider(this).get(ParkingViewModel.ParkingViewModelData::class.java)
        sharedPreferencesHelper = SharedPreferencesHelper(this)

        val loginResponse = sharedPreferencesHelper.getLoginResponse()
        loginResponse?.let {
            val loginData = it.content.firstOrNull()
            if (loginData != null) {
                userId = loginData.uuid ?: "N/A"
                token = loginData.token ?: "N/A"
                companyId = loginData.companyId ?: "N/A"
            }
        } ?: run {
            Toast.makeText(this, "Please Logout and Login Once.", Toast.LENGTH_SHORT).show()
        }

        recyclerView = findViewById(R.id.blocksRecyclerView)
        recyclerViewTwo = findViewById(R.id.blocks2RecyclerView)
        lnrNoData = findViewById(R.id.lnrNoData)

        val onSlotSelected: (ParkingSlots, Floor, Block) -> Unit = { slot, floor, block ->
            val intent = Intent(this, CarFormActivity::class.java).apply {
                putExtra("selectedSlot", slot.parkingNo)
                putExtra("selectedFloor", floor.floorName)
                putExtra("selectedBlock", block.blockName)
                putExtra("selectedSlotUuid", slot.uuid)
                putExtra("vehicleNo", vehicleNo)
                putExtra("serialNumber", serialNumber)
                putExtra("checkintime", checkintime)
                putExtra("vehicleType", vehicleType)
                putExtra("vehicleImage", vehicleImage)
            }
            startActivity(intent)
            finish()
        }

        callApi(onSlotSelected)

        refreshButton.setOnClickListener {
            callApi(onSlotSelected)
        }

        // Search logic
        /*searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(query: CharSequence?, start: Int, before: Int, count: Int) {
                val searchTerm = query.toString().trim()
                val filteredList = originalBlockList.filter { block ->
                    block.blockName?.contains(searchTerm, ignoreCase = true) == true ||
                            block.floors.any { floor ->
                                floor.floorName?.contains(searchTerm, ignoreCase = true) == true ||
                                        // Parking slot's parking number contains the search term
                                        floor.slots.any { slot ->
                                            slot.parkingNo?.contains(searchTerm, ignoreCase = true) == true
                                        }
                            }
                }

                // Update the adapter with the filtered list
                blockAdapter2.updateBlockList(filteredList)
            }
        })*/
       /* searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(query: CharSequence?, start: Int, before: Int, count: Int) {
                val searchTerm = query.toString().trim()

                // Filter the block list based on search term in ParkingSlot's parkingNo
                val filteredList = originalBlockList.filter { block ->
                    block.floors.any { floor ->
                        floor.slots.any { slot ->
                            // Only check parkingNo for slots
                            slot.parkingNo?.contains(searchTerm, ignoreCase = true) == true
                        }
                    }
                }

                // Update the adapter with the filtered list
                blockAdapter2.updateBlockList(filteredList)
            }
        })*/

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(query: CharSequence?, start: Int, before: Int, count: Int) {
                val searchTerm = query.toString().trim()

                if (searchTerm.isEmpty()) {
                    // Reset to full list if search is empty
                    blockAdapter2.updateBlockList(originalBlockList)
                    return
                }

                // Filter only the slots that match the search term
                val filteredBlocks = originalBlockList.mapNotNull { block ->
                    val filteredFloors = block.floors.mapNotNull { floor ->
                        val matchedSlots = floor.slots.filter { slot ->
                            slot.parkingNo?.contains(searchTerm, ignoreCase = true) == true
                        }
                        if (matchedSlots.isNotEmpty()) {
                            floor.copy(slots = matchedSlots) // Keep only matched slots
                        } else null
                    }

                    if (filteredFloors.isNotEmpty()) {
                        block.copy(floors = filteredFloors) // Keep only floors with matching slots
                    } else null
                }

                blockAdapter2.updateBlockList(filteredBlocks)
            }
        })



    }

    private fun callApi(onSlotSelected: (ParkingSlots, Floor, Block) -> Unit) {
        parkingViewModelData.getAvailableSlotsData(companyId, token)

        parkingViewModelData.isLoading.observe(this, Observer {
            ProgressBarUtility.showProgressDialog(this)
        })

        parkingViewModelData.error.observe(this, Observer {
            ProgressBarUtility.dismissProgressDialog()
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        parkingViewModelData.parkingResponse.observe(this, Observer { response ->
            ProgressBarUtility.dismissProgressDialog()
            if (response != null && response.content != null) {
                val slots = response.content
                val blockList = slots.groupBy { it.blockName }.map { (blockName, blockSlots) ->
                    val floors =
                        blockSlots.groupBy { it.floorName }.map { (floorName, floorSlots) ->
                            val parkingSlots = floorSlots.map {
                                ParkingSlots(
                                    parkingNo = it.parkingNo,
                                    availabilityStatus = it.availabilityStatus,
                                    type = it.type,
                                    uuid = it.uuid
                                )
                            }
                            Floor(floorName, parkingSlots)
                        }
                    Block(blockName, floors)
                }

                originalBlockList = blockList

                blockAdapter = BlockAdapter(this, blockList, onSlotSelected) { selectedPosition ->
                    val selectedBlock = blockList[selectedPosition]
                    blockAdapter2.updateBlockList(listOf(selectedBlock))
                }

                recyclerView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                recyclerView.adapter = blockAdapter

                blockAdapter2 = BlockTwoAdapter(this, listOf(blockList.first()), onSlotSelected)
                recyclerViewTwo.layoutManager = LinearLayoutManager(this)
                recyclerViewTwo.adapter = blockAdapter2

                lnrNoData.visibility = View.GONE
                recyclerViewTwo.visibility = View.VISIBLE
            } else {
                lnrNoData.visibility = View.VISIBLE
                recyclerViewTwo.visibility = View.GONE
            }
        })
    }
}