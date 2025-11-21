package com.mobdeve.s18.santos.alejandro.mco2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomnavigation.BottomNavigationView

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        applySavedTheme()
        super.onCreate(savedInstanceState)
    }

    private fun applySavedTheme() {
        val prefs = getSharedPreferences("settings_prefs", MODE_PRIVATE)
        val themeValue = prefs.getString("dark_mode", "System Default")

        val mode = when (themeValue) {
            "Light" -> AppCompatDelegate.MODE_NIGHT_NO
            "Dark" -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
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
