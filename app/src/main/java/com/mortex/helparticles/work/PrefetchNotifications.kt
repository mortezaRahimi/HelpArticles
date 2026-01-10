package com.mortex.helparticles.work

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mortex.helparticles.R

object PrefetchNotifications {

    const val CHANNEL_ID = "prefetch_status"
    private const val CHANNEL_NAME = "Background updates"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(NotificationManager::class.java)
        val existing = manager.getNotificationChannel(CHANNEL_ID)
        if (existing != null) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications for background refresh results"
        }

        manager.createNotificationChannel(channel)
    }

    @SuppressLint("MissingPermission")
    fun showSuccess(context: Context) {
        ensureChannel(context)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher) // ✅ add a proper small icon
            .setContentTitle("Articles updated")
            .setContentText("Background refresh finished successfully.")
            .setAutoCancel(false)
            .build()

        NotificationManagerCompat.from(context)
            .notify(generateNotificationId(), notification)
    }

    private fun generateNotificationId(): Int {
        // unique-ish per run, safe for repeated periodic work
        return (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
    }
}
