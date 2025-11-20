package com.mobdeve.s18.santos.alejandro.mco2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class ClassAlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val classItem = ClassItem(
            id = intent.getLongExtra("classId", -1),
            title = intent.getStringExtra("course") ?: "",
            building = intent.getStringExtra("building") ?: "",
            room = intent.getStringExtra("room") ?: "",
            start24 = intent.getStringExtra("start") ?: "",
            end24 = intent.getStringExtra("end") ?: "",
            dayIndex = intent.getIntExtra("dayIndex", 0)
        )

        val notifDb = NotifDbHelper(context)

        // Only insert if this class ID doesn't already exist
        if (!notifDb.isNotificationInsertedToday(classItem.id)) {
            notifDb.insertNotification(classItem, context)
        }

        val prefs = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
        val playSound = prefs.getBoolean("play_sound", true)

        val title = intent.getStringExtra("title") ?: "Upcoming Class"
        val message = intent.getStringExtra("message") ?: "Your class is starting soon!"
        val channelId = if (playSound) "class_alert_channel_sound" else "class_alert_channel_silent"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted — skip notification or log it
                return
            }
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.full_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), notification)
        }
    }
}
