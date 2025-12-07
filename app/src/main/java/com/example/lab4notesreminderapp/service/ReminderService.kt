/**
 * Simple background Service that waits 5 seconds and posts a notification.
 */
package com.example.lab4notesreminderapp.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.lab4notesreminderapp.R
import kotlinx.coroutines.*

class ReminderService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        CoroutineScope(Dispatchers.IO).launch {
            delay(5000) // 5 seconds
            showNotification()
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun showNotification() {

        // ✅ ANDROID 13+ NOTIFICATION PERMISSION CHECK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionStatus = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            )

            if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted → do NOT crash
                return
            }
        }

        val channelId = "notes_channel"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // ✅ Notification Channel (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notes Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Reminder")
            .setContentText("Check your notes!")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .build()

        manager.notify(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
