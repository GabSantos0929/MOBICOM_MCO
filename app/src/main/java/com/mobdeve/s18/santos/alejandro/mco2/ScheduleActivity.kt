
package com.mobdeve.s18.santos.alejandro.mco2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ScheduleActivity : BaseActivity() {


    private lateinit var rv: RecyclerView
    private lateinit var adapter: ScheduleAdapter
    private val items = mutableListOf<ClassItem>()
    private lateinit var db: ClassDbHelper
    private val dayLabels by lazy { resources.getStringArray(R.array.days_week).toList() }



    private val addClassLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            refreshList()
        }
    }

    private val detailsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            refreshList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.schedule_screen)
        setupBottomNav(R.id.navSchedule)

        db = ClassDbHelper(this)

        db = ClassDbHelper(this)

        rv = findViewById(R.id.rvWeek)
        rv.layoutManager = LinearLayoutManager(this)
        rv.setHasFixedSize(true)

        refreshList()

        findViewById<FloatingActionButton>(R.id.fabAddClass).setOnClickListener {
            addClassLauncher.launch(Intent(this, AddClassActivity::class.java))
        }
    }

    private fun refreshList() {
        items.clear()
        items += db.getAllClasses()

        adapter = ScheduleAdapter(buildRows(items)) { clicked ->
            detailsLauncher.launch(
                Intent(this, ClassDetailsActivity::class.java)
                    .putExtra(EXTRA_RESULT_CLASS, clicked)
            )
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
}
