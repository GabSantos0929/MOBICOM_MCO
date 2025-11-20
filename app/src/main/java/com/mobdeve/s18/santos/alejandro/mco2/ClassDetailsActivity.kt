package com.mobdeve.s18.santos.alejandro.mco2

import android.os.Bundle
import android.widget.TextView

class ClassDetailsActivity : BaseActivity() {

    private val dayLabels = listOf("Mon","Tue","Wed","Thu","Fri","Sat")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.class_details)
        setupBottomNav(R.id.navSchedule)

        val item = intent.getParcelableExtra<ClassItem>("classItem")
            ?: run { finish(); return }

        // Title
        findViewById<TextView>(R.id.tvTitle).text = item.title

        // Day
        val day = dayLabels.getOrElse(item.dayIndex.coerceIn(0, dayLabels.lastIndex)) { "Mon" }
        findViewById<TextView>(R.id.tvDayValue).text = day

        // Time
        findViewById<TextView>(R.id.tvTimeValue).text = "${item.start24} – ${item.end24}"

        // Location
        findViewById<TextView>(R.id.tvLocationValue).text = "${item.building} • ${item.room}"


    }
}
