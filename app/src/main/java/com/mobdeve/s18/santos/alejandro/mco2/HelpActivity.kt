package com.mobdeve.s18.santos.alejandro.mco2

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.help_support_layout)

        val backButton = findViewById<ImageButton>(R.id.helpGoBackBtn)
        backButton.setOnClickListener {
            finish()
        }
    }
}