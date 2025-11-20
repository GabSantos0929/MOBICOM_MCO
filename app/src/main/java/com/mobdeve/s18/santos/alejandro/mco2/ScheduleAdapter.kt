package com.mobdeve.s18.santos.alejandro.mco2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

sealed class WeekRow {
    data class DayHeader(val label: String) : WeekRow()
    data class ClassEntry(val item: ClassItem, val isNext: Boolean) : WeekRow()
}

class ScheduleAdapter(
    private val rows: List<WeekRow>,
    private val onClassClick: (ClassItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_CLASS = 1
    }

    override fun getItemViewType(position: Int): Int = when (rows[position]) {
        is WeekRow.DayHeader -> TYPE_HEADER
        is WeekRow.ClassEntry -> TYPE_CLASS
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inf = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {

            val v = inf.inflate(R.layout.day_chip, parent, false)
            HeaderVH(v)
        } else {

            val v = inf.inflate(R.layout.dashboard_card_layout, parent, false)
            ClassVH(v)
        }
    }

    override fun getItemCount(): Int = rows.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val row = rows[position]) {
            is WeekRow.DayHeader -> (holder as HeaderVH).bind(row)
            is WeekRow.ClassEntry -> (holder as ClassVH).bind(row.item, row.isNext, onClassClick)
        }
    }



    class HeaderVH(view: View) : RecyclerView.ViewHolder(view) {
        private val tvDay: TextView = view.findViewById(R.id.tvDay)
        fun bind(row: WeekRow.DayHeader) {
            tvDay.text = row.label
        }
    }

    class ClassVH(view: View) : RecyclerView.ViewHolder(view) {
        private val card: MaterialCardView = view.findViewById(R.id.cardClass)
        private val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        private val tvTime: TextView = view.findViewById(R.id.tvTime)
        private val tvLocation: TextView = view.findViewById(R.id.tvLocation)

        fun bind(item: ClassItem, isNext: Boolean, onClick: (ClassItem) -> Unit) {
            tvTitle.text = item.title
            tvTime.text = "${item.start24} – ${item.end24}"
            tvLocation.text = "${item.building} • ${item.room}"


            card.setOnClickListener { onClick(item) }
        }
    }
}
