package com.example.myfirstapplication

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log

class RingerModeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val targetMode = intent.getIntExtra("target_mode", AudioManager.RINGER_MODE_VIBRATE)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (!notificationManager.isNotificationPolicyAccessGranted) {
            Log.e("RingerModeReceiver", "No DND access — cannot change ringer mode")
            return
        }

        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        try {
            audioManager.ringerMode = targetMode
            Log.d("RingerModeReceiver", "Ringer mode changed to $targetMode")
        } catch (e: SecurityException) {
            Log.e("RingerModeReceiver", "SecurityException while changing ringer mode", e)
        }
    }
}