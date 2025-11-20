package com.mobdeve.s18.santos.alejandro.mco2

data class ClassNotification (
    val id: Long,
    val classId: Long,
    val courseCode: String,
    val building: String,
    val room: String,
    val floor: String,
    val message: String,
    val createdAt: Long
)