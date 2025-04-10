package com.elite.parking

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.elite.parking.Model.parkingslots.ParkingResponse
import com.elite.parking.Model.parkingslots.ParkingSlot
import com.elite.parking.Model.parkingslots.ParkingSlots
import com.elite.parking.adapter.BlockAdapter
import com.elite.parking.adapter.ParkingDataProcessor
import com.elite.parking.storage.SharedPreferencesHelper
import com.elite.parking.viewModel.ParkingViewModel
import com.elite.parking.viewModel.ParkingViewModel.ParkingViewModelData
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlin.getValue

class ParkingSlotsActivity : AppCompatActivity() {

    private val parkingViewModel: ParkingViewModel by viewModels()
    private lateinit var parkingViewModelData: ParkingViewModel.ParkingViewModelData
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var userId: String
    private lateinit var token: String
    private lateinit var companyId: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var blockAdapter: BlockAdapter

    private lateinit var toolBarback: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_slots)
        toolBarback = findViewById(R.id.toolBarback)
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
        parkingViewModelData.getAvailableSlotsData(companyId, token)
        val onSlotSelected: (ParkingSlots, Floor, Block) -> Unit = { slot, floor, block ->
            val intent = Intent(this, CarFormActivity::class.java)
            intent.putExtra("selectedSlot", slot.parkingNo)
            intent.putExtra("selectedFloor", floor.floorNo)
            intent.putExtra("selectedBlock", block.blockNo)
            intent.putExtra("selectedSlotUuid", slot.uuid)
            startActivity(intent)
            finish()
            //Toast.makeText(this, "${block.blockNo+" "+floor.floorNo+" "+slot.parkingNo+" "+slot.uuid} ", Toast.LENGTH_SHORT).show()
        }
        parkingViewModelData.parkingResponse.observe(this, Observer { response ->
            if (response != null) {
                recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)

                if (response.content != null) {
                    val slots: List<ParkingSlot> = response.content

                    val blockList = slots
                        .groupBy { it.blockNo }
                        .map { (blockNo, blockSlots) ->
                            val floors = blockSlots
                                .groupBy { it.floorNo }
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

                    blockAdapter = BlockAdapter(this, blockList, onSlotSelected)
                    recyclerView.adapter = blockAdapter
                }
            }
        })

    }
}