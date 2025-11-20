package com.mobdeve.s18.santos.alejandro.mco2

import android.content.Context
import android.content.ContentValues

class NotifDbHelper(context: Context) {
    private lateinit var dbHelper : DbHelper

    init {
        this.dbHelper = DbHelper(context)
    }

    private fun extractFloor(room: String): Int {
        // Remove non-digit prefix
        val digits = room.filter { it.isDigit() }
        return if (digits.length <= 2) {
            digits.toIntOrNull() ?: 0  // e.g., "202" -> "2" floor
        } else {
            digits.dropLast(2).toIntOrNull() ?: 0  // e.g., "1109" -> "11", "2011" -> "20"
        }
    }

    private fun floorText(floorNumber: Int): String = when (floorNumber) {
        1 -> "1st Floor"
        2 -> "2nd Floor"
        3 -> "3rd Floor"
        else -> "${floorNumber}th Floor"
    }

    fun isNotificationInsertedToday(classId: Long): Boolean {
        val db = dbHelper.readableDatabase

        val todayStart = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis

        val todayEnd = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 23)
            set(java.util.Calendar.MINUTE, 59)
            set(java.util.Calendar.SECOND, 59)
            set(java.util.Calendar.MILLISECOND, 999)
        }.timeInMillis

        val cursor = db.query(
            "notifications",
            arrayOf("id"),
            "class_id = ? AND created_at BETWEEN ? AND ?",
            arrayOf(classId.toString(), todayStart.toString(), todayEnd.toString()),
            null, null, null
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    fun getAllNotifications(): ArrayList<ClassNotification> {
        val notifications = ArrayList<ClassNotification>()
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            DbReferences.NOTIF_TABLE,
            null,
            null, null, null, null,
            "${DbReferences.NOTIF_CREATED_AT} DESC"
        )

        while (cursor.moveToNext()) {
            notifications.add(
                ClassNotification(
                    cursor.getLong(cursor.getColumnIndexOrThrow(DbReferences.NOTIF_ID)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(DbReferences.NOTIF_CLASS_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.NOTIF_COURSE_CODE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.NOTIF_BUILDING)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.NOTIF_ROOM)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.NOTIF_FLOOR)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.NOTIF_MESSAGE)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(DbReferences.NOTIF_CREATED_AT))
                )
            )
        }

        cursor.close()
        db.close()
        return notifications
    }

    fun insertNotification(item: ClassItem, context: Context) {
        val db = dbHelper.writableDatabase
        val classDb = ClassDbHelper(context)

        val floorNumber = extractFloor(item.room)
        val floorText = floorText(floorNumber)

        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val selected = prefs.getString("notif_timing", "Smart (Floor-based)")
        val alertOffset = classDb.computeAlertOffset(selected!!, item.room)

        val values = ContentValues().apply {
            put(DbReferences.NOTIF_CLASS_ID, item.id)
            put(DbReferences.NOTIF_COURSE_CODE, item.title)
            put(DbReferences.NOTIF_BUILDING, item.building)
            put(DbReferences.NOTIF_ROOM, item.room)
            put(DbReferences.NOTIF_FLOOR, floorText)
            put(DbReferences.NOTIF_MESSAGE, "Class starts in $alertOffset minutes")
            put(DbReferences.NOTIF_CREATED_AT, System.currentTimeMillis())
        }

        db.insert(DbReferences.NOTIF_TABLE, null, values)
        db.close()
    }

    object DbReferences {
        const val NOTIF_TABLE = "notifications"
        const val NOTIF_ID = "id"
        const val NOTIF_CLASS_ID = "class_id"
        const val NOTIF_COURSE_CODE = "course_code"
        const val NOTIF_BUILDING = "building"
        const val NOTIF_ROOM = "room"
        const val NOTIF_FLOOR = "floor"
        const val NOTIF_MESSAGE = "message"
        const val NOTIF_CREATED_AT = "created_at"

        const val CREATE_NOTIF_TABLE =
            "CREATE TABLE IF NOT EXISTS $NOTIF_TABLE (" +
                    "$NOTIF_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$NOTIF_CLASS_ID INTEGER NOT NULL, " +
                    "$NOTIF_COURSE_CODE TEXT NOT NULL, " +
                    "$NOTIF_BUILDING TEXT NOT NULL, " +
                    "$NOTIF_ROOM TEXT NOT NULL, " +
                    "$NOTIF_FLOOR TEXT NOT NULL, " +
                    "$NOTIF_MESSAGE TEXT NOT NULL, " +
                    "$NOTIF_CREATED_AT INTEGER NOT NULL)"

        val hoursAgoTimestamp = System.currentTimeMillis() - 3 * 60 * 60 * 1000
        val yesterdayTimestamp = System.currentTimeMillis() - 24 * 60 * 60 * 1000
        val lastWeekTimestamp = System.currentTimeMillis() - (24*7) * 60 * 60 * 1000

        val INSERT_NOTIF_TABLE =
            "INSERT INTO $NOTIF_TABLE (" +
                    "$NOTIF_CLASS_ID, " +
                    "$NOTIF_COURSE_CODE, " +
                    "$NOTIF_BUILDING, " +
                    "$NOTIF_ROOM, " +
                    "$NOTIF_FLOOR, " +
                    "$NOTIF_MESSAGE, " +
                    "$NOTIF_CREATED_AT) " +
            "VALUES (2, 'STDISCM', 'Andrew Gonzalez Hall', 'AG1110', '11th Floor', 'Class starts in 30 minutes', $hoursAgoTimestamp), " +
                    "(3, 'GETEAMS', 'Enrique Razon', 'ER7T', '7th Floor', 'Class starts in 30 minutes', $yesterdayTimestamp), " +
                    "(1, 'MOBICOM', 'Gokongwei Hall', 'GK213', '2nd Floor', 'Class starts in 15 minutes', $lastWeekTimestamp)"

        const val DROP_NOTIF_TABLE = "DROP TABLE IF EXISTS $NOTIF_TABLE"
    }
}
