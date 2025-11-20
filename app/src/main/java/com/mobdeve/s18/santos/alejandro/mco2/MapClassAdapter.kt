package com.mobdeve.s18.santos.alejandro.mco2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MapClassAdapter(private val classes: List<ClassItem>) :
    RecyclerView.Adapter<MapClassAdapter.ClassViewHolder>() {

    inner class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvClassName: TextView = itemView.findViewById(R.id.className)
        val tvClassTime: TextView = itemView.findViewById(R.id.classTime)
        val tvClassRoom: TextView = itemView.findViewById(R.id.classRoom)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.map_item_class, parent, false)
        return ClassViewHolder(view)
    }

    override fun getItemCount(): Int = classes.size

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val item = classes[position]
        holder.tvClassName.text = item.title
        holder.tvClassTime.text = "${item.start24} - ${item.end24}"
        holder.tvClassRoom.text = item.room
    }
}
