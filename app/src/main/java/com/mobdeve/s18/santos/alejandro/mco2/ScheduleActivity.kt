package com.mobdeve.s18.santos.alejandro.mco2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ScheduleActivity : BaseActivity() {

    private val dayLabels = listOf("Mon","Tue","Wed","Thu","Fri","Sat")

    private lateinit var rv: RecyclerView
    private lateinit var adapter: ScheduleAdapter
    private val items = mutableListOf<ClassItem>()

    // launcher to open AddClassActivity and receive the new item
    private val addClassLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            val newItem = res.data?.getParcelableExtra<ClassItem>("newClass") ?: return@registerForActivityResult
            items += newItem
            refreshList()
            Toast.makeText(this, "Added: ${newItem.title}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.schedule_screen)
        setupBottomNav(R.id.navSchedule)

        rv = findViewById(R.id.rvWeek)
        val fab = findViewById<FloatingActionButton>(R.id.fabAddClass)

        rv.layoutManager = LinearLayoutManager(this)
        rv.setHasFixedSize(true)

        // seed demo data
        items.clear()
        items += loadWeekDemo()

        // initial adapter
        adapter = ScheduleAdapter(buildRows(items)) { item ->
            Toast.makeText(this, "${item.title} • ${item.start24}", Toast.LENGTH_SHORT).show()
        }
        rv.adapter = adapter

        // open Add Class screen
        fab.setOnClickListener {
            addClassLauncher.launch(Intent(this, AddClassActivity::class.java))
        }


        adapter = ScheduleAdapter(buildRows(items)) { item ->
            startActivity(
                Intent(this, ClassDetailsActivity::class.java)
                    .putExtra("classItem", item)
            )
        }
        rv.adapter = adapter

    }

    private fun refreshList() {

        adapter = ScheduleAdapter(buildRows(items)) { item ->
            Toast.makeText(this, "${item.title} • ${item.start24}", Toast.LENGTH_SHORT).show()
        }
        rv.adapter = adapter
    }

    private fun buildRows(items: List<ClassItem>): List<WeekRow> {
        val sorted = items.sortedWith(compareBy<ClassItem>({ it.dayIndex }, { it.start24 }))
        val groups = sorted.groupBy { it.dayIndex }
        val result = mutableListOf<WeekRow>()

        val nextId = sorted.firstOrNull()?.id

        dayLabels.forEachIndexed { idx, label ->
            result += WeekRow.DayHeader(label)
            groups[idx].orEmpty().forEach { ci ->
                result += WeekRow.ClassEntry(ci, isNext = (ci.id == nextId))
            }
        }
        return result
    }

    // Demo data
    private fun loadWeekDemo(): List<ClassItem> = listOf(
        ClassItem(1,"MOBICOM","Gokongwei Bldg","202","09:15","10:45",0),
        ClassItem(2,"MOBICOM","Gokongwei Bldg","202","14:30","16:00",0),
        ClassItem(3,"MOBICOM","St. La Salle Hall","403","09:15","10:45",1),
        ClassItem(4,"MOBICOM","La Salle Hall","403","10:00","11:00",2),
        ClassItem(5,"MOBICOM","Andrew Bldg","902","11:15","12:45",2)
    )
}
