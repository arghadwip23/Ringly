package com.example.myfirstapplication.data

import kotlinx.coroutines.flow.Flow

class ScheduleRepository(private val dao: ScheduleDao) {
    val allSchedules: Flow<List<Schedule>> = dao.getAll()

    fun schedulesForDay(day: Int): Flow<List<Schedule>> = dao.getForDay(day)

    suspend fun insert(schedule: Schedule): Long = dao.insert(schedule)
    suspend fun update(schedule: Schedule) = dao.update(schedule)
    suspend fun delete(schedule: Schedule) = dao.delete(schedule)
    suspend fun getById(id: Int): Schedule? = dao.getById(id)
    suspend fun getAllEnabled(): List<Schedule> = dao.getAllEnabledOnce()
}