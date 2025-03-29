package com.elite.parking

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elite.parking.Model.Vehicle
import com.google.android.material.floatingactionbutton.FloatingActionButton

private lateinit var recyclerView: RecyclerView

private lateinit var vehicleAdapter: VehicleAdapter
private lateinit var vehicleList: MutableList<Vehicle>

class HistoryFragment : Fragment(),VehicleAdapter.OnItemClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding= inflater.inflate(R.layout.fragment_history, container, false)
        recyclerView = binding.findViewById(R.id.vehicleRecyclerView)
        val fabAddVehicle: FloatingActionButton=binding.findViewById(R.id.fabAddVehicle)

        recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        fabAddVehicle.setOnClickListener {
            val intent = Intent(requireActivity(), CarFormActivity::class.java)
            startActivity(intent)
        }
        vehicleList = mutableListOf(
            Vehicle("AP39CZ9999", "Mahindra", "Mahindra XUV700","8","InProgress",R.drawable.car1),
            Vehicle("TS01AB0098", "Maruthi", "Vitara Brezza","9","InProgress",R.drawable.car2),
            Vehicle("GSGB3B8849", "Mahindra", "Mahindra XUV500","10","Completed",R.drawable.car3),
            Vehicle("TS01AB9004", "Honda", "CIAZ","12","InProgress",R.drawable.car4),
            Vehicle("KA01AB1234", "Toyota", "Toyota Fortuner","15","Completed",R.drawable.car1),
            Vehicle("TN10XY4567", "Honda", "Honda Civic","21","Completed",R.drawable.car2)
        )

        vehicleAdapter = VehicleAdapter(vehicleList,requireActivity(),this)
        recyclerView.adapter = vehicleAdapter
        return  binding
    }

    override fun onItemClick(position: Int) {
        val vehicle = vehicleList[position]
        val intent = Intent(requireActivity(), PaymentActivity::class.java)
        startActivity(intent)
    }

}