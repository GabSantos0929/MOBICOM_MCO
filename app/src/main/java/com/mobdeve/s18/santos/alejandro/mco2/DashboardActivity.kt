package com.mobdeve.s18.santos.alejandro.mco2

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalTime
import java.time.Duration
import androidx.core.net.toUri

class DashboardActivity : BaseActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var adapter: DashboardAdapter
    private lateinit var tvNextMinutes: TextView
    private lateinit var tvNextMeta: TextView
    private lateinit var btnOpenRoute: MaterialButton
    private lateinit var classDb: ClassDbHelper

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_layout)

        // bottom nav
        setupBottomNav(R.id.navDashboard)

        // views
        rv = findViewById(R.id.rvClasses)
        tvNextMinutes = findViewById(R.id.tvNextMinutes)
        tvNextMeta = findViewById(R.id.tvNextMeta)
        btnOpenRoute = findViewById(R.id.btnOpenRoute)

        // list
        adapter = DashboardAdapter { item -> openDetailsOrMap(item) }
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        renderData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun renderData() {
        classDb = ClassDbHelper(applicationContext)
        val classes = classDb.getClassesForToday()
        adapter.submitList(classes)

        val now = LocalTime.now()
        val next = classes.minByOrNull { Duration.between(now, LocalTime.parse(it.start24)).toMinutes() }
            ?.takeIf { LocalTime.parse(it.start24).isAfter(now) }

        if (next != null) {
            val mins = Duration.between(now, LocalTime.parse(next.start24)).toMinutes()
            tvNextMinutes.text = "$mins mins"
            tvNextMeta.text = "${next.title}  •  ${next.building}  •  ${next.room}"
            btnOpenRoute.isVisible = true
            btnOpenRoute.setOnClickListener { openMaps(next) }
        } else {
            tvNextMinutes.text = "—"
            tvNextMeta.text = "No upcoming classes"
            btnOpenRoute.isVisible = false
        }
    }

    private fun openMaps(item: ClassItem) {
        val query = "${item.building} DLSU"
        val uri = "geo:0,0?q=${URLEncoder.encode(query, StandardCharsets.UTF_8.name())}".toUri()
        val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
        }
        startActivity(mapIntent)
    }

    private fun openDetailsOrMap(item: ClassItem) {

        val i = Intent(this, MapScreenActivity::class.java).apply {
            putExtra("title", item.title)
            putExtra("building", item.building)
            putExtra("room", item.room)
            putExtra("start", item.start24)
            putExtra("end", item.end24)
        }
        startActivity(i)
    }
}
