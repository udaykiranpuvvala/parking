package com.elite.parking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
                .replace(R.id.nav_host_fragment, ProfileFragment()) // Default Fragment
                .commit()
        }

        bottomNavView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Set Home to maroon and Profile to gray
                    setBottomNavItemColors(R.id.nav_home, R.color.colorSelected) // maroon for home
                    setBottomNavItemColors(R.id.nav_history, R.color.  colorSelected) // gray for profile
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, ProfileFragment())
                        .commit()
                    true
                }
                R.id.nav_history -> {
                    // Set Profile to maroon and Home to gray
                    setBottomNavItemColors(R.id.nav_history, R.color. colorSelected) // maroon for profile
                    setBottomNavItemColors(R.id.nav_home, R.color.colorSelected) // gray for home
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, HistoryFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }

        // Set default unselected color for all items
        setBottomNavItemColors(R.id.nav_home, R.color.colorSelected) // gray for home initially
        setBottomNavItemColors(R.id.nav_history, R.color.colorSelected) // gray for profile initially
    }

    private fun setBottomNavItemColors(itemId: Int, colorResId: Int) {
        val color = ContextCompat.getColor(this, colorResId)

        bottomNavView.menu.findItem(itemId)?.let { item ->
            // Change the color of the icon and text for the selected/unselected item
            item.icon?.setTint(color) // Set the icon color
            bottomNavView.setItemTextColor(ContextCompat.getColorStateList(this, colorResId)) // Set text color
        }
    }
}
