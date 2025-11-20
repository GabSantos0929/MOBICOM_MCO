package com.mobdeve.s18.santos.alejandro.mco2

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build

class ClassEase : Application() {
    override fun onCreate() {
        super.onCreate()

        // Create notification channel for class alerts
        createNotificationChannels()

        val prefs = getSharedPreferences("settings_prefs", MODE_PRIVATE)
        val isNotifEnabled = prefs.getBoolean("enable_notif", true)

        if (isNotifEnabled) {
            scheduleClassAlerts()
        } else {
            cancelAllClassAlerts()
        }
    }

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundChannel = NotificationChannel(
                "class_alert_channel_sound",
                "Class Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for upcoming classes with sound"
                setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI, null)
            }

            val silentChannel = NotificationChannel(
                "class_alert_channel_silent",
                "Class Alerts (Silent)",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for upcoming classes without sound"
                setSound(null, null)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(soundChannel)
            notificationManager.createNotificationChannel(silentChannel)
        }
    }

    fun scheduleClassAlerts() {
        val classDb = ClassDbHelper(applicationContext)
        val classesToday = classDb.getClassesForToday()
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager

        classesToday.forEach { classItem ->
            val prefs = applicationContext.getSharedPreferences("settings_prefs", MODE_PRIVATE)
            val selected = prefs.getString("notif_timing", "Smart (Floor-based)")
            val alertOffset = classDb.computeAlertOffset(selected!!, classItem.room)
            val alertTime = calculateAlertTime(classItem, alertOffset) ?: return@forEach

            val intent = Intent(this, ClassAlertReceiver::class.java).apply {
                putExtra("title", "Class starts in $alertOffset minutes")
                putExtra("message", "${classItem.title}: ${classItem.start24} - ${classItem.end24} ${classItem.room}")
                putExtra("classId", classItem.id)
                putExtra("course", classItem.title)
                putExtra("building", classItem.building)
                putExtra("room", classItem.room)
                putExtra("start", classItem.start24)
                putExtra("end", classItem.end24)
                putExtra("dayIndex", classItem.dayIndex)
            }

            val pendingIntent = android.app.PendingIntent.getBroadcast(
                this,
                classItem.id.toInt(), // unique ID per class
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) android.app.PendingIntent.FLAG_MUTABLE else 0
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                // Could show a message or fallback to approximate alarm
                alarmManager.set(
                    android.app.AlarmManager.RTC_WAKEUP,
                    alertTime,
                    pendingIntent
                )
            } else {
                // Use exact alarm
                alarmManager.setExactAndAllowWhileIdle(
                    android.app.AlarmManager.RTC_WAKEUP,
                    alertTime,
                    pendingIntent
                )
            }
        }
    }

    fun cancelAllClassAlerts() {
        val classDb = ClassDbHelper(applicationContext)
        val classesToday = classDb.getClassesForToday()
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager

        classesToday.forEach { classItem ->
            val intent = Intent(this, ClassAlertReceiver::class.java)
            val pendingIntent = android.app.PendingIntent.getBroadcast(
                this,
                classItem.id.toInt(),
                intent,
                android.app.PendingIntent.FLAG_NO_CREATE or
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) android.app.PendingIntent.FLAG_MUTABLE else 0
            )
            pendingIntent?.let { alarmManager.cancel(it) }
        }
    }

    private fun calculateAlertTime(classItem: ClassItem, alertOffset: Int): Long? {
        val formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm")
        val classStart = java.time.LocalTime.parse(classItem.start24, formatter)

        val now = java.time.LocalDateTime.now()
        val classDateTime = java.time.LocalDateTime.of(
            java.time.LocalDate.now(),
            classStart
        ).minusMinutes(alertOffset.toLong())

        return if (classDateTime.isAfter(now)) {
            classDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        } else null // already passed
    }
}
