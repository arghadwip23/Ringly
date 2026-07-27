package com.example.myfirstapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// dayOfWeek uses Calendar constants: 1=Sunday ... 7=Saturday
const val MODE_RING = 0
const val MODE_VIBRATE = 1
const val MODE_SILENT = 2

@Entity(tableName = "schedules")
data class Schedule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dayOfWeek: Int,      // 1-7 (Calendar.SUNDAY..Calendar.SATURDAY)
    val hour: Int,           // 0-23
    val minute: Int,         // 0-59
    val mode: Int,           // MODE_RING / MODE_VIBRATE / MODE_SILENT
    val label: String = "",
    val enabled: Boolean = true
)