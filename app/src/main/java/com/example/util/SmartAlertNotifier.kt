package com.example.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.model.FormatUtils
import com.example.model.QuotaStatus

class SmartAlertNotifier(private val context: Context) {

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Data Pulse Quota Alerts"
            val descriptionText = "Notifications for data limit threshold warnings"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun notifyQuotaThreshold(status: QuotaStatus, usedBytes: Long, limitBytes: Long, percentage: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        val title = when (status) {
            QuotaStatus.WARNING -> "Data Pulse Alert: 80% Data Used"
            QuotaStatus.CRITICAL -> "Data Pulse Alert: 95% Data Limit Reached!"
            QuotaStatus.EXCEEDED -> "Data Pulse Alert: Plan Quota Exceeded!"
            else -> return
        }

        val message = "You have used ${FormatUtils.formatBytes(usedBytes)} of ${FormatUtils.formatBytes(limitBytes)} (%.1f%%).".format(percentage)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
        } catch (_: SecurityException) {
            // Permission denied or revoked
        }
    }

    fun notifyAppDailyQuotaExceeded(
        appName: String,
        usedBytes: Long,
        thresholdBytes: Long = 3L * 1024L * 1024L * 1024L,
        isArabic: Boolean = true
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        val formattedUsed = FormatUtils.formatBytes(usedBytes)
        val formattedLimit = FormatUtils.formatBytes(thresholdBytes)

        val title = if (isArabic) {
            "تحذير استهلاك: $appName تجاوز 3 جيجابايت اليوم!"
        } else {
            "Usage Alert: $appName exceeded 3 GB today!"
        }

        val message = if (isArabic) {
            "استهلك تطبيق $appName ما يقارب $formattedUsed اليوم متجاوزاً حد $formattedLimit. اضغط لمراجعة التفاصيل."
        } else {
            "App $appName has consumed $formattedUsed today, exceeding $formattedLimit. Tap to review details."
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            appName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            val notificationId = APP_ALERT_BASE_ID + (appName.hashCode() and 0xFFFF)
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Permission denied or revoked
        }
    }

    companion object {
        const val CHANNEL_ID = "data_pulse_quota_alerts"
        const val NOTIFICATION_ID = 1001
        const val APP_ALERT_BASE_ID = 2000
    }
}
