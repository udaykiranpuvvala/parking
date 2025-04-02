package com.elite.parking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

    }
}
