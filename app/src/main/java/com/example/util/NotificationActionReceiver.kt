package com.example.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

/**
 * Handles notification dismissal and inline action events outside the UI.
 */
class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action == ACTION_DISMISS) {
            val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1)
            if (notificationId != -1) {
                try {
                    NotificationManagerCompat.from(context).cancel(notificationId)
                } catch (_: Exception) {}
            }
        }
    }

    companion object {
        const val ACTION_DISMISS = "com.example.ACTION_NOTIFICATION_DISMISS"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    }
}
