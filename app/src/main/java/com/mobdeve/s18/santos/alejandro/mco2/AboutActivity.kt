package com.mobdeve.s18.santos.alejandro.mco2

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_layout)

        val backButton = findViewById<ImageButton>(R.id.aboutGoBackBtn)
        backButton.setOnClickListener {
            finish()
        }
    }
}