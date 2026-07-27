package com.example.myfirstapplication.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.myfirstapplication.data.Schedule
import java.util.Calendar

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(schedule: Schedule) {
        if (!schedule.enabled) {
            cancel(schedule)
            return
        }

        val triggerTime = nextOccurrenceMillis(schedule.dayOfWeek, schedule.hour, schedule.minute)

        val intent = Intent(context, RingerModeReceiver::class.java).apply {
            putExtra("schedule_id", schedule.id)
            putExtra("target_mode", schedule.mode)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            schedule.id, // unique request code per schedule
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }

    fun cancel(schedule: Schedule) {
        val intent = Intent(context, RingerModeReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            schedule.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    /**
     * Calculates the next epoch-millis timestamp for the given day-of-week + time.
     * If that day/time already passed this week (or is today but time already passed),
     * it rolls forward to next week.
     */
    private fun nextOccurrenceMillis(dayOfWeek: Int, hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, dayOfWeek)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (target.timeInMillis <= now.timeInMillis) {
            target.add(Calendar.DAY_OF_YEAR, 7)
        }
        return target.timeInMillis
    }
}