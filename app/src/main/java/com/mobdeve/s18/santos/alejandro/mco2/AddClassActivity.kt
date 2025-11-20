package com.mobdeve.s18.santos.alejandro.mco2

import android.app.Activity
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class AddClassActivity : BaseActivity() {

    private lateinit var etSubject: EditText
    private lateinit var etStart: EditText
    private lateinit var etEnd: EditText
    private lateinit var etBuilding: EditText
    private lateinit var etRoom: EditText
    private lateinit var etFloor: EditText
    private lateinit var etDayDD: MaterialAutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addclass_screen)
        setupBottomNav(R.id.navSchedule)

        etSubject = findViewById(R.id.etSubject)
        etStart = findViewById(R.id.etStart)
        etEnd = findViewById(R.id.etEnd)
        etBuilding = findViewById(R.id.etBuilding)
        etRoom = findViewById(R.id.etRoom)
        etFloor = findViewById(R.id.etFloor)
        etDayDD = findViewById(R.id.etDay)

        // Day dropdown
        val dayLabels = resources.getStringArray(R.array.days_week)
        etDayDD.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, dayLabels))
        etDayDD.setText(dayLabels.first(), false) // default to Monday

        // Time pickers
        etStart.setOnClickListener { pickTimeInto(etStart) }
        etEnd.setOnClickListener { pickTimeInto(etEnd) }

        findViewById<MaterialButton>(R.id.btnSave).setOnClickListener {
            val title = etSubject.text.toString().trim()
            val start = etStart.text.toString().trim()
            val end = etEnd.text.toString().trim()
            val building = etBuilding.text.toString().trim()
            val room = etRoom.text.toString().trim()
            val dayIndex = dayLabels.indexOf(etDayDD.text?.toString()).let { if (it >= 0) it else 0 }
            val alertOffset = 15

            if (title.isEmpty() || start.isEmpty() || end.isEmpty()) {
                Toast.makeText(this, "Please fill Subject, Start, and End.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val item = ClassItem(
                id = System.currentTimeMillis(),
                title = title,
                building = building,
                room = room,
                start24 = start,
                end24 = end,
                dayIndex = dayIndex
            )

            setResult(Activity.RESULT_OK, intent.putExtra("newClass", item))
            finish()
        }
    }

    private fun pickTimeInto(target: EditText) {
        TimePickerDialog(
            this,
            { _, h, m -> target.setText(String.format("%02d:%02d", h, m)) },
            9, 0, true
        ).show()
    }
}
