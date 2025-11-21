package com.mobdeve.s18.santos.alejandro.mco2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class DashboardAdapter(
    private val onClick: (ClassItem) -> Unit
) : ListAdapter<ClassItem, DashboardAdapter.VH>(diff) {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val card: MaterialCardView = view.findViewById(R.id.cardClass)
        private val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        private val tvTime: TextView = view.findViewById(R.id.tvTime)
        private val tvLocation: TextView = view.findViewById(R.id.tvLocation)

        fun bind(item: ClassItem) {
            tvTitle.text = item.title
            tvTime.text = "${item.start24.replace(':','•').replace('•',':')} – ${item.end24}"
            tvLocation.text = "${item.building} • ${item.room}"

            card.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.dashboard_card_layout, parent, false)
        return VH(v)
    }
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    private companion object {
        val diff = object : DiffUtil.ItemCallback<ClassItem>() {
            override fun areItemsTheSame(a: ClassItem, b: ClassItem) = a.id == b.id
            override fun areContentsTheSame(a: ClassItem, b: ClassItem) = a == b
        }
    }
}
