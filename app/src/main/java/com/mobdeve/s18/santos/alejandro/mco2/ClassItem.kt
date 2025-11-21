package com.mobdeve.s18.santos.alejandro.mco2

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClassItem (
    val id: Long = 0L,
    val title: String,
    val building: String,
    val room: String,
    val start24: String,
    val end24: String,
    val dayIndex: Int = -1
): Parcelable
