package com.mobdeve.s18.santos.alejandro.mco2

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobdeve.s18.santos.alejandro.mco2.databinding.NotifScreenBinding

class NotifScreenActivity : BaseActivity() {
    private lateinit var dbHelper: NotifDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewBinding : NotifScreenBinding = NotifScreenBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        setupBottomNav(R.id.navNotifications)

        dbHelper = NotifDbHelper(applicationContext)
        val notifData = dbHelper.getAllNotifications()

        viewBinding.notifList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        viewBinding.notifList.adapter = NotifAdapter(notifData)
    }
}
