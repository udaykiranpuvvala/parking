package com.elite.parking

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elite.parking.storage.SharedPreferencesHelper
import com.elite.parking.viewModel.ParkingViewModel
import com.google.gson.Gson
import kotlin.getValue

class ParkingSlotsActivity : AppCompatActivity() {

    private val parkingViewModel: ParkingViewModel by viewModels()
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var userId: String
    private lateinit var token: String
    private lateinit var companyId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_slots)

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
        val recyclerView = findViewById<RecyclerView>(R.id.blocksRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load data (in a real app, this would be from API)
        loadParkingSlots()
    }

    private fun loadParkingSlots() {

        parkingViewModel.fetchParkingSlots(companyId,token)

       /* parkingViewModel.parkingSlots.observe(this, Observer { slots ->
            if (slots.isNullOrEmpty()) {
                Toast.makeText(this, "No parking slots available", Toast.LENGTH_SHORT).show()
            } else {
                // Update the RecyclerView with the fetched parking slots
               // parkingSlotAdapter.submitList(slots)

                val response = Gson().fromJson(slots.toString(), ParkingResponse::class.java)
                val blocks = ParkingDataProcessor.processToHierarchy(response)

                val recyclerView = findViewById<RecyclerView>(R.id.blocksRecyclerView)
                recyclerView.adapter = BlockAdapter(blocks)
            }

        })*/

        parkingViewModel.parkingSlots.observe(this) { slots ->
           /* */
        }


    }
}