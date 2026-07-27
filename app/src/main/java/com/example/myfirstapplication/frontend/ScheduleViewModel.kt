package com.example.myfirstapplication.frontend

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstapplication.alarm.AlarmScheduler
import com.example.myfirstapplication.data.AppDatabase
import com.example.myfirstapplication.data.Schedule
import com.example.myfirstapplication.data.ScheduleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ScheduleRepository
    private val alarmScheduler = AlarmScheduler(application)

    init {
        val dao = AppDatabase.getInstance(application).scheduleDao()
        repository = ScheduleRepository(dao)
    }

    val allSchedules = repository.allSchedules.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun addSchedule(schedule: Schedule) {
        viewModelScope.launch {
            val newId = repository.insert(schedule)
            val saved = schedule.copy(id = newId.toInt())
            if (saved.enabled) alarmScheduler.schedule(saved)
        }
    }

    fun updateSchedule(schedule: Schedule) {
        viewModelScope.launch {
            repository.update(schedule)
            if (schedule.enabled) {
                alarmScheduler.schedule(schedule)
            } else {
                alarmScheduler.cancel(schedule)
            }
        }
    }

    fun deleteSchedule(schedule: Schedule) {
        viewModelScope.launch {
            alarmScheduler.cancel(schedule)
            repository.delete(schedule)
        }
    }

    fun toggleEnabled(schedule: Schedule) {
        val updated = schedule.copy(enabled = !schedule.enabled)
        updateSchedule(updated)
    }

    suspend fun getById(id: Int): Schedule? = repository.getById(id)
}