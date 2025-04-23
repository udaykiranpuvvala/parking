package com.mindcoin.valetparking.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mindcoin.valetparking.fragment.ProfileFragment
import com.mindcoin.valetparking.R
import com.mindcoin.valetparking.fragment.HistoryFragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavView = findViewById(R.id.bottom_navigation)

        // Set default fragment when the app starts
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, HistoryFragment())
                .commit()
        }

        // Using the new setOnItemSelectedListener API
        bottomNavView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                  supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, ProfileFragment())
                        .commit()
                    true
                }
                R.id.nav_history -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, HistoryFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
        addTopIndicator(R.id.nav_history)
        bottomNavView.setOnNavigationItemSelectedListener { item ->
            addTopIndicator(item.itemId)
            when (item.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, ProfileFragment())
                        .commit()
                    true
                }
                R.id.nav_history -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, HistoryFragment())
                        .commit()
                    true
                }
                else -> false
            }
            true
        }

    }
    @SuppressLint("RestrictedApi")
    fun addTopIndicator(selectedItemId: Int) {
        for (i in 0 until bottomNavView.menu.size()) {
            val view = bottomNavView.getChildAt(0) as BottomNavigationMenuView
            val itemView = view.getChildAt(i) as BottomNavigationItemView
            val selected = bottomNavView.menu.getItem(i).itemId == selectedItemId

            val indicator = itemView.findViewById<View>(R.id.bottom_navigation) ?: View(this).apply {
                id = R.id.bottom_navigation
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 8)
                setBackgroundColor(ContextCompat.getColor(context, R.color.maroon))
                itemView.addView(this)
            }
            indicator.visibility = if (selected) View.VISIBLE else View.INVISIBLE
        }
    }
}