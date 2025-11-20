//package com.mobdeve.s18.santos.alejandro.mco2
//
//import android.os.Build
//import androidx.annotation.RequiresApi
//import java.time.Duration
//import java.time.LocalTime
//import java.time.format.DateTimeFormatter
//
//object ClassesRepository {
//    @RequiresApi(Build.VERSION_CODES.O)
//    private val fmt = DateTimeFormatter.ofPattern("HH:mm")
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun loadToday(): List<ClassItem> = listOf(
//        ClassItem(1, "MOBICOM", "Gokongwei Bldg", "GK202", "14:30", "16:00"),
//        ClassItem(2, "STDISCM", "Br. Andrew Gonzales Hall", "AG1109", "16:10", "17:40"),
//        ClassItem(3, "GEWORLD", "LS Hall", "LS227", "09:15", "10:45"),
//        ClassItem(4, "STMATH", "Andrew Bldg", "AG902", "13:00", "14:30"),
//    ).sortedBy { LocalTime.parse(it.start24, fmt) }
//
//    fun loadWeek(): List<ClassItem> = listOf(
//        ClassItem(1,"STDISCM","Gokongwei Bldg","GK202","09:15","10:45",0),
//        ClassItem(2,"STDISCM","St. La Salle Hall","LS403","09:15","10:45",1),
//        ClassItem(3,"STDISCM","Andrew Bldg","AG902","11:15","12:45",2),
//        ClassItem(4,"STDISCM","Andrew Bldg","AG902","08:15","10:45",3),
//        ClassItem(5,"STDISCM","Gokongwei Bldg","GK202","09:15","10:45",4),
//        ClassItem(6,"STDISCM","Br. Andrew Gonzales Hall","AG1109","09:15","10:45",5),
//    )
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun nextClass(now: LocalTime = LocalTime.now()): ClassItem? {
//        val all = loadToday()
//        return all.firstOrNull { LocalTime.parse(it.start24, fmt).isAfter(now) }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun minutesUntilStart(item: ClassItem, now: LocalTime = LocalTime.now()): Long {
//        val start = LocalTime.parse(item.start24, fmt)
//        return Duration.between(now, start).toMinutes()
//    }
//
//    fun timeRange(item: ClassItem): String =
//        "${item.start24.replace(':', '.')} - ${item.end24.replace(':', '.')}"
//}
