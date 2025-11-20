package com.mobdeve.s18.santos.alejandro.mco2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Switch

class SettingsItemHelper(val root: LinearLayout) {
    private val icon: ImageView = root.findViewById(R.id.itemIcon)
    private val title: TextView = root.findViewById(R.id.itemTitle)
    private val desc: TextView = root.findViewById(R.id.itemDesc)
    private val btn: ImageButton = root.findViewById(R.id.itemBtn)
    private val toggle: Switch = root.findViewById(R.id.itemSwitch)
    private val spinner: Spinner = root.findViewById(R.id.spinner)

    fun populate(iconRes: Int, titleText: String, descText: String? = null, showButton: Boolean = false, hasSwitch: Boolean = false, hasSpinner: Boolean = false) {
        icon.setImageResource(iconRes)
        title.text = titleText

        if (!descText.isNullOrEmpty()) {
            desc.visibility = View.VISIBLE
            desc.text = descText
            title.setTypeface(title.typeface, android.graphics.Typeface.BOLD)

            // Set marginTop of icon to 8dp
            val params = icon.layoutParams as ViewGroup.MarginLayoutParams
            params.topMargin = (4 * root.resources.displayMetrics.density).toInt()
            icon.layoutParams = params
        } else {
            desc.visibility = View.GONE
        }

        toggle.visibility = if (hasSwitch) View.VISIBLE else View.GONE
        btn.visibility = if (showButton) View.VISIBLE else View.GONE
        spinner.visibility = if (hasSpinner) View.VISIBLE else View.GONE
    }

    fun setupSpinner(
        context: Context,
        options: List<String>,
        preferenceKey: String,
        defaultValue: String,
        onValueChanged: ((String) -> Unit)? = null
    ) {
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val saved = prefs.getString(preferenceKey, defaultValue)
        spinner.setSelection(options.indexOf(saved))

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                pos: Int,
                id: Long
            ) {
                val selectedValue = options[pos]
                val oldValue = prefs.getString(preferenceKey, defaultValue)

                if (selectedValue != oldValue) {
                    prefs.edit().putString(preferenceKey, selectedValue).apply()
                    onValueChanged?.invoke(selectedValue)  // <-- call the lambda here
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    fun setOnRowClickListener(listener: () -> Unit) {
        root.setOnClickListener { listener() }
        btn.setOnClickListener { listener() }
    }

    fun startActivityOnClick(activity: Activity, item: SettingsItemHelper, targetActivity: Class<*>) {
        item.setOnRowClickListener {
            val intent = Intent(activity, targetActivity)
            activity.startActivity(intent)
        }
    }
}