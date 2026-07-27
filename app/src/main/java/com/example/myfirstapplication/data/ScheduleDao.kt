package com.example.myfirstapplication.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedules ORDER BY dayOfWeek, hour, minute")
    fun getAll(): Flow<List<Schedule>>

    @Query("SELECT * FROM schedules WHERE dayOfWeek = :day ORDER BY hour, minute")
    fun getForDay(day: Int): Flow<List<Schedule>>

    @Query("SELECT * FROM schedules WHERE enabled = 1")
    suspend fun getAllEnabledOnce(): List<Schedule>

    @Query("SELECT * FROM schedules WHERE id = :id")
    suspend fun getById(id: Int): Schedule?

    @Insert
    suspend fun insert(schedule: Schedule): Long

    @Update
    suspend fun update(schedule: Schedule)

    @Delete
    suspend fun delete(schedule: Schedule)
}