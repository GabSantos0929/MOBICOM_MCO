package com.mobdeve.s18.santos.alejandro.mco2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomnavigation.BottomNavigationView

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    protected fun setupBottomNav(selectedItemId: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = selectedItemId
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navDashboard -> {
                    if (this !is DashboardActivity) {
                        startActivity(Intent(this, DashboardActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.navSchedule -> {
                    if (this !is ScheduleActivity) {
                        startActivity(Intent(this, ScheduleActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.navMap -> {
                    if (this !is MapScreenActivity) {
                        startActivity(Intent(this, MapScreenActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.navNotifications -> {
                    if (this !is NotifScreenActivity) {
                        startActivity(Intent(this, NotifScreenActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.navSettings -> {
                    if (this !is SettingsScreenActivity) {
                        startActivity(Intent(this, SettingsScreenActivity::class.java))
                        finish()
                    }
                    true
                }
                else -> false
            }
        }
    }
}
