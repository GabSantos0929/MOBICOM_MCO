package com.mobdeve.s18.santos.alejandro.mco2

import android.content.Context
import android.content.ContentValues
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ClassDbHelper(context: Context) {
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

    private fun getCurrentDayIndex(): Int {
        val calendar = java.util.Calendar.getInstance()
        val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK) // 1=Sunday, 2=Monday ...
        return when(dayOfWeek) {
            java.util.Calendar.MONDAY -> 0
            java.util.Calendar.TUESDAY -> 1
            java.util.Calendar.WEDNESDAY -> 2
            java.util.Calendar.THURSDAY -> 3
            java.util.Calendar.FRIDAY -> 4
            java.util.Calendar.SATURDAY -> 5
            java.util.Calendar.SUNDAY -> 6
            else -> 0 // Sunday defaults to Monday
        }
    }

    fun computeAlertOffset(selected: String, room: String): Int {
        return when (selected) {
            "Smart (Floor-based)" -> {
                val floor = extractFloor(room)
                if (floor >= 7) 30 else 15
            }
            "10 minutes before" -> 10
            "15 minutes before" -> 15
            "20 minutes before" -> 20
            "30 minutes before" -> 30
            "1 hour before" -> 60
            else -> 15
        }
    }

    fun getClassesForToday(): List<ClassItem> {
        val todayClasses = mutableListOf<ClassItem>()
        val db = dbHelper.readableDatabase
        val currentDayIndex = getCurrentDayIndex()

        val cursor = db.query(
            DbReferences.CLASS_TABLE,
            null,
            "${DbReferences.CLASS_DAY_INDEX} = ?",
            arrayOf(currentDayIndex.toString()),
            null, null, "${DbReferences.CLASS_START_TIME} ASC"
        )

        while (cursor.moveToNext()) {
            todayClasses.add(
                ClassItem(
                    cursor.getLong(cursor.getColumnIndexOrThrow(DbReferences.CLASS_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.CLASS_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.CLASS_BUILDING)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.CLASS_ROOM)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.CLASS_START_TIME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.CLASS_END_TIME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DbReferences.CLASS_DAY_INDEX))
                )
            )
        }

        cursor.close()
        db.close()
        return todayClasses
    }

    fun insertClass(item: ClassItem): Long{
        val db = dbHelper.writableDatabase


        val values = ContentValues().apply {
            put(DbReferences.CLASS_TITLE, item.title)
            put(DbReferences.CLASS_BUILDING, item.building)
            put(DbReferences.CLASS_ROOM, item.room)
            put(DbReferences.CLASS_START_TIME, item.start24)
            put(DbReferences.CLASS_END_TIME, item.end24)
            put(DbReferences.CLASS_DAY_INDEX, item.dayIndex)
        }

        val newId = db.insert(DbReferences.CLASS_TABLE, null, values)
        db.close()
        return newId
    }

    fun deleteClass(id: Long): Int {
        val db = dbHelper.writableDatabase
        val rows = db.delete(
            DbReferences.CLASS_TABLE,
            "${DbReferences.CLASS_ID}=?",
            arrayOf(id.toString())
        )
        db.close()
        return rows
    }


    fun getAllClasses(): List<ClassItem> {
        val out = mutableListOf<ClassItem>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DbReferences.CLASS_TABLE,
            null,
            null, null, null, null,
            "${DbReferences.CLASS_DAY_INDEX} ASC, ${DbReferences.CLASS_START_TIME} ASC"
        )

        while (cursor.moveToNext()) {
            out += ClassItem(
                id        = cursor.getLong(cursor.getColumnIndexOrThrow(DbReferences.CLASS_ID)),
                title     = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.CLASS_TITLE)),
                building  = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.CLASS_BUILDING)),
                room      = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.CLASS_ROOM)),
                start24   = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.CLASS_START_TIME)),
                end24     = cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.CLASS_END_TIME)),
                dayIndex  = cursor.getInt(cursor.getColumnIndexOrThrow(DbReferences.CLASS_DAY_INDEX))
            )
        }
        cursor.close()
        db.close()
        return out
    }



    fun updateClass(item: ClassItem): Int {
        val db = dbHelper.writableDatabase
        val floorNumber = extractFloor(item.room)
        val alertOffset = if (floorNumber >= 7) 30 else 15

        val values = ContentValues().apply {
            put(DbReferences.CLASS_TITLE, item.title)
            put(DbReferences.CLASS_BUILDING, item.building)
            put(DbReferences.CLASS_ROOM, item.room)
            put(DbReferences.CLASS_START_TIME, item.start24)
            put(DbReferences.CLASS_END_TIME, item.end24)
            put(DbReferences.CLASS_DAY_INDEX, item.dayIndex)
        }
        val rows = db.update(
            DbReferences.CLASS_TABLE, values,
            "${DbReferences.CLASS_ID}=?", arrayOf(item.id.toString())
        )
        db.close()
        return rows
    }



    object DbReferences {
        const val CLASS_TABLE = "classes"
        const val CLASS_ID = "id"
        const val CLASS_TITLE = "title"
        const val CLASS_BUILDING = "building"
        const val CLASS_ROOM = "room"
        const val CLASS_START_TIME = "start_time"
        const val CLASS_END_TIME = "end_time"
        const val CLASS_DAY_INDEX = "day_index"


        const val CREATE_CLASS_TABLE =
            "CREATE TABLE IF NOT EXISTS $CLASS_TABLE (" +
                    "$CLASS_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$CLASS_TITLE TEXT NOT NULL, " +
                    "$CLASS_BUILDING TEXT NOT NULL, " +
                    "$CLASS_ROOM TEXT NOT NULL, " +
                    "$CLASS_START_TIME TEXT NOT NULL, " +
                    "$CLASS_END_TIME TEXT NOT NULL, " +
                    "$CLASS_DAY_INDEX INTEGER DEFAULT -1) "

        const val INSERT_CLASS_TABLE =
            "INSERT INTO $CLASS_TABLE (" +
                    "$CLASS_TITLE, " +
                    "$CLASS_BUILDING, " +
                    "$CLASS_ROOM, " +
                    "$CLASS_START_TIME, " +
                    "$CLASS_END_TIME, " +
                    "$CLASS_DAY_INDEX) " +
            "VALUES ('MOBICOM', 'Gokongwei Hall', 'GK213', '12:45', '14:15', 3), " +
                    "('STDISCM', 'Andrew Gonzalez Hall', 'AG1110', '14:30', '16:00', 4), " +
                    "('GESPORT', 'Enrique Razon', 'ER7T', '14:30', '16:00', 0), " +
                    "('GETEAMS', 'Enrique Razon', 'ER7T', '18:00', '19:30', 0)"

        const val DROP_CLASS_TABLE = "DROP TABLE IF EXISTS $CLASS_TABLE"
    }
}
