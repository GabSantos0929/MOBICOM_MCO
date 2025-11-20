package com.mobdeve.s18.santos.alejandro.mco2

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoadingScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_screen)

        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        progressBar.max = 100

        val totalTime = 5000L
        val interval = 50L
        val appStatus: TextView = findViewById(R.id.appStatus)

        // Delay for n seconds, then go to NotifScreenActivity
        object : CountDownTimer(totalTime, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val progress = ((totalTime - millisUntilFinished) * 100 / totalTime).toInt()
                progressBar.progress = progress
            }

            override fun onFinish() {
                appStatus.text = "Ready!"
                progressBar.progress = 100
                startActivity(Intent(this@LoadingScreen, DashboardActivity::class.java))
                finish()
            }
        }.start()
    }
}