package com.example.myfirstapplication.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.myfirstapplication.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = AppDatabase.getInstance(context).scheduleDao()
                val scheduler = AlarmScheduler(context)
                dao.getAllEnabledOnce().forEach { scheduler.schedule(it) }
            } finally {
                pendingResult.finish()
            }
        }
    }
}