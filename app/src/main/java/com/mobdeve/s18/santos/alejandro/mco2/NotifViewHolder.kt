package com.mobdeve.s18.santos.alejandro.mco2

import android.text.format.DateUtils
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s18.santos.alejandro.mco2.databinding.NotifLayoutBinding

class NotifViewHolder(private val viewBinding: NotifLayoutBinding): RecyclerView.ViewHolder(viewBinding.root) {
    fun bindData(notif: ClassNotification) {
        this.viewBinding.notifMessage.text = notif.message
        this.viewBinding.courseCode.text = notif.courseCode
        this.viewBinding.roomNumber.text = "${notif.room}, "
        this.viewBinding.classBuilding.text = notif.building
        this.viewBinding.floorNumber.text = notif.floor

        // Convert the CREATED_AT timestamp to a human-readable "time ago" format
        val createdAt = notif.createdAt
        val now = System.currentTimeMillis() // Current time in milliseconds
        val timeAgo = getTimeAgo(createdAt, now)
        this.viewBinding.timeAgo.text = " • $timeAgo"
    }

    private fun getTimeAgo(createdAt: Long, currentTime: Long): String {
        // Use DateUtils to get a human-readable time ago string
        return DateUtils.getRelativeTimeSpanString(createdAt, currentTime, DateUtils.MINUTE_IN_MILLIS).toString()
    }
}