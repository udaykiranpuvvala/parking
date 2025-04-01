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
                .replace(R.id.nav_host_fragment, HistoryFragment()) // Default Fragment
                .commit()
        }

        // Using the new setOnItemSelectedListener API
        bottomNavView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Set Home as selected and History as unselected
                    setBottomNavItemIcons(R.id.nav_home, R.drawable.ic_home_selected, R.drawable.ic_home_unselected)
                    setBottomNavItemIcons(R.id.nav_history, R.drawable.ic_history_unselected, R.drawable.ic_history_selected)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, ProfileFragment()) // Home Fragment
                        .commit()
                    true
                }
                R.id.nav_history -> {
                    // Set History as selected and Home as unselected
                    setBottomNavItemIcons(R.id.nav_history, R.drawable.ic_history_selected, R.drawable.ic_history_unselected)
                    setBottomNavItemIcons(R.id.nav_home, R.drawable.ic_home_unselected, R.drawable.ic_home_selected)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, HistoryFragment()) // History Fragment
                        .commit()
                    true
                }
                else -> false
            }
        }

        // Initial state: Set default unselected icons for both items
        setBottomNavItemIcons(R.id.nav_home, R.drawable.ic_home_selected, R.drawable.ic_home_unselected)
        setBottomNavItemIcons(R.id.nav_history, R.drawable.ic_history_selected, R.drawable.ic_history_unselected)
    }

    private fun setBottomNavItemIcons(itemId: Int, selectedIconResId: Int, unselectedIconResId: Int) {
        val item = bottomNavView.menu.findItem(itemId)

        // Set the selected icon for selected item, unselected for the rest
        if (item?.isChecked == true) {
            item.icon = resources.getDrawable(selectedIconResId, theme)
        } else {
            item.icon = resources.getDrawable(unselectedIconResId, theme)
        }
    }
}
