package com.mobdeve.s18.santos.alejandro.mco2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.mobdeve.s18.santos.alejandro.mco2.databinding.NotifLayoutBinding

class NotifAdapter(private val data: ArrayList<ClassNotification>): Adapter<NotifViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifViewHolder {
        val viewBinding: NotifLayoutBinding = NotifLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        val myViewHolder = NotifViewHolder(viewBinding)
        return myViewHolder
    }

    override fun onBindViewHolder(holder: NotifViewHolder, position: Int) {
        holder.bindData(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}