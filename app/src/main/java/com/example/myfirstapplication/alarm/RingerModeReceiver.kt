package com.example.myfirstapplication.alarm

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.myfirstapplication.data.AppDatabase
import com.example.myfirstapplication.data.MODE_RING
import com.example.myfirstapplication.data.MODE_SILENT
import com.example.myfirstapplication.data.MODE_VIBRATE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RingerModeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val scheduleId = intent.getIntExtra("schedule_id", -1)
        val targetMode = intent.getIntExtra("target_mode", MODE_VIBRATE)

        applyRingerMode(context, targetMode)

        // Reschedule this same schedule for next week, if it's still enabled in DB
        if (scheduleId != -1) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val dao = AppDatabase.getInstance(context).scheduleDao()
                    val schedule = dao.getById(scheduleId)
                    if (schedule != null && schedule.enabled) {
                        AlarmScheduler(context).schedule(schedule)
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    private fun applyRingerMode(context: Context, targetMode: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (!notificationManager.isNotificationPolicyAccessGranted) {
            Log.e("RingerModeReceiver", "No DND access — cannot change ringer mode")
            return
        }

        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val androidMode = when (targetMode) {
            MODE_RING -> AudioManager.RINGER_MODE_NORMAL
            MODE_VIBRATE -> AudioManager.RINGER_MODE_VIBRATE
            MODE_SILENT -> AudioManager.RINGER_MODE_SILENT
            else -> AudioManager.RINGER_MODE_NORMAL
        }

        try {
            audioManager.ringerMode = androidMode
            Log.d("RingerModeReceiver", "Ringer mode changed to $androidMode")
        } catch (e: SecurityException) {
            Log.e("RingerModeReceiver", "SecurityException", e)
        }
    }
}