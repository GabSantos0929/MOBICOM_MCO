package com.mobdeve.s18.santos.alejandro.mco2

import android.app.Activity
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import android.content.Intent
import android.widget.ImageButton


class AddClassActivity : BaseActivity() {

    private lateinit var etSubject: EditText
    private lateinit var etStart: EditText
    private lateinit var etEnd: EditText
    private lateinit var etBuilding: EditText
    private lateinit var etRoom: EditText
    private lateinit var etDayDD: MaterialAutoCompleteTextView
    private lateinit var btnSave: MaterialButton

    private lateinit var db: ClassDbHelper
    private var editing: ClassItem? = null
    private var selectedDayIndex = 0
    private lateinit var dayItems: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addclass_screen)

        val backButton = findViewById<ImageButton>(R.id.addClassGoBackBtn)
        backButton.setOnClickListener {
            finish()
        }

        db = ClassDbHelper(this)

        etSubject = findViewById(R.id.etSubject)
        etStart   = findViewById(R.id.etStart)
        etEnd     = findViewById(R.id.etEnd)
        etBuilding= findViewById(R.id.etBuilding)
        etRoom    = findViewById(R.id.etRoom)
        etDayDD   = findViewById(R.id.etDay)
        btnSave   = findViewById(R.id.btnSave)

        dayItems = resources.getStringArray(R.array.days_week).toList()
        etDayDD.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, dayItems))
        etDayDD.setOnItemClickListener { _, _, pos, _ -> selectedDayIndex = pos }

        // EDIT mode?
        editing = intent.getParcelableExtra(EXTRA_EDIT_CLASS)
        if (editing != null) {
            val e = editing!!
            etSubject.setText(e.title)
            etStart.setText(e.start24)
            etEnd.setText(e.end24)
            etBuilding.setText(e.building)
            etRoom.setText(e.room)
            selectedDayIndex = e.dayIndex
            etDayDD.setText(dayItems.getOrNull(selectedDayIndex) ?: dayItems.first(), false)
            btnSave.text = "Save Changes"
        } else {
            selectedDayIndex = 0
            etDayDD.setText(dayItems.first(), false)
        }

        etStart.setOnClickListener { pickTimeInto(etStart) }
        etEnd.setOnClickListener   { pickTimeInto(etEnd) }

        btnSave.setOnClickListener {
            val title = etSubject.text.toString().trim()
            val start = etStart.text.toString().trim()
            val end   = etEnd.text.toString().trim()
            val building = etBuilding.text.toString().trim()
            val room     = etRoom.text.toString().trim()

            if (title.isEmpty() || start.isEmpty() || end.isEmpty()) {
                Toast.makeText(this, "Please fill Subject, Start, and End.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (editing == null) {
                val newItem = ClassItem(
                    title = title,
                    building = building,
                    room = room,
                    start24 = start,
                    end24 = end,
                    dayIndex = selectedDayIndex
                )
                val id = db.insertClass(newItem)
                if (id <= 0) { Toast.makeText(this,"Save failed.",Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_RESULT_CLASS, newItem.copy(id = id)))
            } else {
                val updated = editing!!.copy(
                    title = title,
                    building = building,
                    room = room,
                    start24 = start,
                    end24 = end,
                    dayIndex = selectedDayIndex
                )
                db.updateClass(updated)
                setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_RESULT_CLASS, updated))
            }
            finish()
        }
    }

    private fun pickTimeInto(target: EditText) {
        val (initH, initM) = run {
            val t = target.text?.toString() ?: ""
            val p = t.split(":")
            if (p.size == 2) {
                val h = p[0].toIntOrNull(); val m = p[1].toIntOrNull()
                if (h != null && m != null) h to m else 9 to 0
            } else 9 to 0
        }
        TimePickerDialog(this, { _, h, m ->
            target.setText(String.format("%02d:%02d", h, m))
        }, initH, initM, true).show()
    }
}