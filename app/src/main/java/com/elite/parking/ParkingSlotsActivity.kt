package com.elite.parking

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elite.parking.Model.parkingslots.Block
import com.elite.parking.Model.parkingslots.Floor
import com.elite.parking.Model.parkingslots.ParkingSlot
import com.elite.parking.Model.parkingslots.ParkingSlots
import com.elite.parking.adapter.BlockAdapter
import com.elite.parking.adapter.BlockTwoAdapter
import com.elite.parking.loader.ProgressBarUtility
import com.elite.parking.storage.SharedPreferencesHelper
import com.elite.parking.viewModel.ParkingViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.getValue

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

    private lateinit var toolBarback: TextView
    private lateinit var refreshButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_slots)

        val vehicleNo= intent.getStringExtra("vehicleNo")
        val serialNumber= intent.getStringExtra("serialNumber")
        val checkintime= intent.getStringExtra("checkintime")
        val vehicleType= intent.getStringExtra("vehicleType")
        val vehicleImage= intent.getStringExtra("vehicleImage")
        toolBarback = findViewById(R.id.toolBarback)
        refreshButton = findViewById(R.id.refreshButton)

        toolBarback.setOnClickListener {
            finish()
        }
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
            val intent = Intent(this, CarFormActivity::class.java)
            intent.putExtra("selectedSlot", slot.parkingNo)
            intent.putExtra("selectedFloor", floor.floorName)
            intent.putExtra("selectedBlock", block.blockName)
            intent.putExtra("selectedSlotUuid", slot.uuid)
            intent.putExtra("vehicleNo",vehicleNo)
            intent.putExtra("serialNumber", serialNumber)
            intent.putExtra("checkintime", checkintime)
            intent.putExtra("vehicleType", vehicleType)
            intent.putExtra("vehicleImage", vehicleImage)
            startActivity(intent)
            finish()
            //Toast.makeText(this, "${block.blockNo+" "+floor.floorNo+" "+slot.parkingNo+" "+slot.uuid} ", Toast.LENGTH_SHORT).show()
        }




        callApi(onSlotSelected)
        refreshButton.setOnClickListener {
            callApi(onSlotSelected)

        }

    }

    private fun callApi(onSlotSelected: (ParkingSlots, Floor, Block) -> Unit) {
        parkingViewModelData.getAvailableSlotsData(companyId, token)

        parkingViewModelData.isLoading.observe(this, Observer { isLoading ->
            ProgressBarUtility.showProgressDialog(this)
        })

        parkingViewModelData.error.observe(this, Observer { errorMessage ->
            ProgressBarUtility.dismissProgressDialog()
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        })

        parkingViewModelData.parkingResponse.observe(this, Observer { response ->
            if (response != null) {
                recyclerViewTwo.visibility=View.VISIBLE
                lnrNoData.visibility=View.GONE
                ProgressBarUtility.dismissProgressDialog()
                recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                recyclerViewTwo.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

                if (response.content != null) {
                    val slots: List<ParkingSlot> = response.content

                    val blockList = slots
                        .groupBy { it.blockName }
                        .map { (blockNo, blockSlots) ->
                            val floors = blockSlots
                                .groupBy { it.floorName }
                                .map { (floorNo, floorSlots) ->
                                    val parkingSlots = floorSlots.map {
                                        ParkingSlots(
                                            parkingNo = it.parkingNo,
                                            availabilityStatus = it.availabilityStatus,
                                            type = it.type,
                                            uuid = it.uuid
                                        )
                                    }
                                    Floor(floorNo, parkingSlots)
                                }
                            Block(blockNo, floors)
                        }

                    // Initialize BlockAdapter
                    blockAdapter = BlockAdapter(this, blockList, onSlotSelected) { selectedPosition ->
                        // Update BlockTwoAdapter when a block is selected from BlockAdapter
                        blockAdapter2.updateSelectedBlockPosition(selectedPosition)
                    }
                    recyclerView.adapter = blockAdapter

                    // Initialize BlockTwoAdapter
                    blockAdapter2 = BlockTwoAdapter(this, blockList, onSlotSelected)
                    recyclerViewTwo.adapter = blockAdapter2
                }
            }
        })

    }
}