package com.mobdeve.s18.santos.alejandro.mco2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
class ClassDetailsActivity : BaseActivity() {

    private val dayLabels = listOf("Monday","Tuesday","Wednesday","Thursday","Friday","Saturday")
    private lateinit var db: ClassDbHelper
    private lateinit var item: ClassItem

    private val editLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            val updated = res.data?.getParcelableExtra<ClassItem>(EXTRA_RESULT_CLASS) ?: return@registerForActivityResult
            item = updated
            bind(item)
            setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_RESULT_CLASS, updated))
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.class_details)

        val backButton = findViewById<ImageButton>(R.id.schedGoBackBtn)
        backButton.setOnClickListener {
            finish()
        }

        db = ClassDbHelper(this)


        item = intent.getParcelableExtra(EXTRA_RESULT_CLASS)
            ?: intent.getParcelableExtra("classItem")
                    ?: run { finish(); return }

        bind(item)

        findViewById<MaterialButton>(R.id.btnEdit).setOnClickListener {
            val i = Intent(this, AddClassActivity::class.java).putExtra(EXTRA_EDIT_CLASS, item)
            editLauncher.launch(i)
        }

        findViewById<MaterialButton>(R.id.btnDelete).setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Delete class?")
                .setMessage("This cannot be undone.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete") { _, _ ->
                    db.deleteClass(item.id)

                    val app = application as ClassEase
                    app.cancelAllClassAlerts()
                    app.scheduleClassAlerts()
                    setResult(Activity.RESULT_OK, Intent().putExtra("deletedId", item.id))
                    finish()
                }.show()
        }



    }

    private fun bind(ci: ClassItem) {
        val dayLabels = resources.getStringArray(R.array.days_week)
        findViewById<TextView>(R.id.tvTitle).text = ci.title
        findViewById<TextView>(R.id.tvDayValue).text = dayLabels.getOrNull(ci.dayIndex) ?: "—"
        findViewById<TextView>(R.id.tvTimeValue).text = "${ci.start24} – ${ci.end24}"
        findViewById<TextView>(R.id.tvLocationValue).text = "${ci.building} • ${ci.room}"
    }
}
