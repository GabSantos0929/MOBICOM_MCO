package com.mobdeve.s18.santos.alejandro.mco2

import android.os.Bundle
import android.widget.Switch

class SettingsScreenActivity : BaseActivity() {
    private val PREFS_NAME = "settings_prefs"
    private val DARK_MODE = "dark_mode"
    private val ENABLE_NOTIF = "enable_notif"
    private val PLAY_SOUND = "play_sound"
    private val SHOW_PINS = "show_pins"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_screen)
        setupSettings()
        setupBottomNav(R.id.navSettings)
    }

    private fun setupSettings() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        val darkMode = SettingsItemHelper(findViewById(R.id.darkMode))
        darkMode.populate(R.drawable.sun, "Dark Mode", "Switch between light and dark theme", hasSwitch = true)

        val darkModeSwitch = darkMode.root.findViewById<Switch>(R.id.itemSwitch)
        darkModeSwitch.isChecked = prefs.getBoolean(DARK_MODE, false)

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(DARK_MODE, isChecked).apply()
            recreate()
        }

        val enableNotif = SettingsItemHelper(findViewById(R.id.enableNotif))
        enableNotif.populate(R.drawable.bell2, "Enable Notifications", "Receive reminders before classes", hasSwitch = true)

        val enableNotifSwitch = enableNotif.root.findViewById<Switch>(R.id.itemSwitch)
        enableNotifSwitch.isChecked = prefs.getBoolean(ENABLE_NOTIF, true)

        enableNotifSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(ENABLE_NOTIF, isChecked).apply()

            if (isChecked) {
                (application as ClassEase).scheduleClassAlerts()
            } else {
                (application as ClassEase).cancelAllClassAlerts()
            }
        }

        val playSound = SettingsItemHelper(findViewById(R.id.sound))
        playSound.populate(R.drawable.sound, "Sound", "Play sound with notifications", hasSwitch = true)

        val playSoundSwitch = playSound.root.findViewById<Switch>(R.id.itemSwitch)
        playSoundSwitch.isChecked = prefs.getBoolean(PLAY_SOUND, true)

        playSoundSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(PLAY_SOUND, isChecked).apply()

            val app = application as ClassEase
            app.createNotificationChannels()
        }

        val notifTiming = SettingsItemHelper(findViewById(R.id.notifTiming))
        notifTiming.populate(R.drawable.clock, "Notification Timing", "Choose when to receive class reminders", hasSpinner = true)
        notifTiming.setupSpinner(
            this,
            options = listOf("Smart (Floor-based)", "10 minutes before", "15 minutes before", "20 minutes before", "30 minutes before", "1 hour before"),
            preferenceKey = "notif_timing",
            defaultValue = "Smart (Floor-based)"
        ) { selectedValue ->
            // Save the new preference
            prefs.edit().putString("notif_timing", selectedValue).apply()

            // Cancel old alarms and schedule new ones with the updated timing
            val app = application as ClassEase
            app.cancelAllClassAlerts()
            app.scheduleClassAlerts()
        }

        val showPins = SettingsItemHelper(findViewById(R.id.showPins))
        showPins.populate(R.drawable.map_pin2, "Show Location Pins", "Display building markers on map", hasSwitch = true)

        val showPinsSwitch = showPins.root.findViewById<Switch>(R.id.itemSwitch)
        showPinsSwitch.isChecked = prefs.getBoolean(SHOW_PINS, true)

        showPinsSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(SHOW_PINS, isChecked).apply()
        }

        val help = SettingsItemHelper(findViewById(R.id.help))
        help.populate(R.drawable.about, "Help & Support", showButton = true)
        help.startActivityOnClick(this, help, HelpActivity::class.java)

        val privacyPolicy = SettingsItemHelper(findViewById(R.id.privacyPolicy))
        privacyPolicy.populate(R.drawable.shield, "Privacy Policy", showButton = true)
        privacyPolicy.startActivityOnClick(this, privacyPolicy, PrivacyActivity::class.java)

        val about = SettingsItemHelper(findViewById(R.id.about))
        about.populate(R.drawable.info, "About ClassEase", showButton = true)
        about.startActivityOnClick(this, about, AboutActivity::class.java)
    }
}
