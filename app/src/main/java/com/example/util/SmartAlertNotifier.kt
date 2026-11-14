package com.example.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.model.FormatUtils
import com.example.model.QuotaStatus

/**
 * Robust System Push & Floating Heads-up Notification Engine for 'Data Pulse'.
 * Configured with IMPORTANCE_HIGH to display instant floating alert banners
 * outside the application when critical quota, forecast depletion, or app limits trigger.
 */
class SmartAlertNotifier(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    /**
     * Initializes dedicated Notification Channels with IMPORTANCE_HIGH for Heads-up popups.
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val cyanColor = Color.parseColor("#00F5D4")
            val vibrationPattern = longArrayOf(0, 250, 150, 250)

            // 1. Data Cap & Quota Threshold Alerts (High Importance / Heads-up)
            val quotaChannel = NotificationChannel(
                CHANNEL_DATA_CAP_ALERTS,
                "Data Pulse: Quota & Limit Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical heads-up alerts when data limit thresholds (80%, 95%) are reached"
                enableVibration(true)
                this.vibrationPattern = vibrationPattern
                enableLights(true)
                lightColor = cyanColor
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }

            // 2. Smart Forecast & Burn Rate Depletion Alerts (High Importance / Heads-up)
            val forecastChannel = NotificationChannel(
                CHANNEL_FORECAST_ALERTS,
                "Data Pulse: Smart Depletion Forecasts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Proactive warnings when daily burn rate will deplete your monthly cap early"
                enableVibration(true)
                this.vibrationPattern = vibrationPattern
                enableLights(true)
                lightColor = cyanColor
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }

            // 3. Network Status & Telemetry Alerts
            val networkChannel = NotificationChannel(
                CHANNEL_NETWORK_ALERTS,
                "Data Pulse: Network & Connectivity",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Updates on cellular & Wi-Fi connectivity and high-speed telemetry events"
                enableVibration(false)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }

            notificationManager.createNotificationChannel(quotaChannel)
            notificationManager.createNotificationChannel(forecastChannel)
            notificationManager.createNotificationChannel(networkChannel)
        }
    }

    /**
     * Verifies whether the app has notification posting permissions.
     */
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    /**
     * Builds standard navigation PendingIntent to open MainActivity at a designated destination.
     */
    private fun createNavPendingIntent(destination: String, requestCode: Int): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_NAV_DESTINATION, destination)
        }
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Builds dismiss PendingIntent triggering NotificationActionReceiver.
     */
    private fun createDismissPendingIntent(notificationId: Int): PendingIntent {
        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_DISMISS
            putExtra(NotificationActionReceiver.EXTRA_NOTIFICATION_ID, notificationId)
        }
        return PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Posts a High-Importance Heads-Up banner for Data Plan Quota thresholds (80%, 95%, Exceeded).
     */
    fun notifyQuotaThreshold(
        status: QuotaStatus,
        usedBytes: Long,
        limitBytes: Long,
        percentage: Float,
        isArabic: Boolean = false
    ) {
        if (!hasNotificationPermission()) return

        val formattedUsed = FormatUtils.formatBytes(usedBytes)
        val formattedLimit = FormatUtils.formatBytes(limitBytes)
        val percentStr = "%.1f".format(percentage)

        val (title, message) = if (isArabic) {
            when (status) {
                QuotaStatus.WARNING ->
                    "⚠️ تنبيه الباقة: استهلكت 80% من بياناتك!" to
                    "لقد استهلكت $formattedUsed من أصل $formattedLimit ($percentStr%). يُنصح بالتحكم في استهلاك الوسائط."
                QuotaStatus.CRITICAL ->
                    "🚨 تحذير حرج: وصلت إلى 95% من حد الباقة!" to
                    "تبقى القليل جداً من بيانات باقتك الشهرية ($formattedUsed / $formattedLimit). قد تنفد قريباً."
                QuotaStatus.EXCEEDED ->
                    "🛑 نفاد الباقة: تم تجاوز سعة البيانات المحددة!" to
                    "تجاوزت سعة الباقة المحددة ($formattedLimit). تحقق من التطبيقات النشطة في الخلفية."
                else -> return
            }
        } else {
            when (status) {
                QuotaStatus.WARNING ->
                    "⚠️ Data Pulse Alert: 80% Data Used" to
                    "You have used $formattedUsed of your $formattedLimit cap ($percentStr%). Consider saving bandwidth."
                QuotaStatus.CRITICAL ->
                    "🚨 Critical Alert: 95% Data Limit Reached!" to
                    "You are very close to your monthly allowance ($formattedUsed / $formattedLimit)."
                QuotaStatus.EXCEEDED ->
                    "🛑 Plan Exceeded: Quota Reached!" to
                    "You have exceeded your plan limit ($formattedLimit). Tap to review high-consumption apps."
                else -> return
            }
        }

        val detailsLabel = if (isArabic) "عرض الباقة" else "View Details"
        val dismissLabel = if (isArabic) "تجاهل" else "Dismiss"

        val contentPendingIntent = createNavPendingIntent("DASHBOARD", NOTIFICATION_ID_QUOTA)
        val dismissPendingIntent = createDismissPendingIntent(NOTIFICATION_ID_QUOTA)

        val builder = NotificationCompat.Builder(context, CHANNEL_DATA_CAP_ALERTS)
            .setSmallIcon(R.drawable.ic_notification_pulse)
            .setColor(Color.parseColor("#00F5D4"))
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(0, detailsLabel, contentPendingIntent)
            .addAction(0, dismissLabel, dismissPendingIntent)

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_QUOTA, builder.build())
        } catch (_: SecurityException) {}
    }

    /**
     * Posts a High-Importance Heads-Up banner for Smart Depletion Forecast.
     * Alerts users outside the app when current burn velocity will exhaust data early.
     */
    fun notifySmartDepletionWarning(
        daysRemaining: Int,
        planLimitGB: Float,
        burnRateFormatted: String,
        isArabic: Boolean = false
    ) {
        if (!hasNotificationPermission()) return

        val title = if (isArabic) {
            "⚠️ تنبيه التحليل الذكي: الباقة ستنفد خلال $daysRemaining أيام!"
        } else {
            "⚠️ Data Warning: Only $daysRemaining days left based on your usage"
        }

        val message = if (isArabic) {
            "بمعدل الحرق الحالي ($burnRateFormatted)، ستنفد باقتك (${planLimitGB.toInt()} جيجابايت) قبل موعد التجديد. اضغط لاستعراض خطة التوفير."
        } else {
            "At your current burn rate of $burnRateFormatted, your ${planLimitGB.toInt()} GB data cap will deplete before cycle end. Tap to see savings recommendations."
        }

        val detailsLabel = if (isArabic) "التحليل الذكي" else "View Forecast"
        val dismissLabel = if (isArabic) "تجاهل" else "Dismiss"

        val contentPendingIntent = createNavPendingIntent("FORECAST", NOTIFICATION_ID_FORECAST)
        val dismissPendingIntent = createDismissPendingIntent(NOTIFICATION_ID_FORECAST)

        val builder = NotificationCompat.Builder(context, CHANNEL_FORECAST_ALERTS)
            .setSmallIcon(R.drawable.ic_notification_pulse)
            .setColor(Color.parseColor("#00F5D4"))
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(0, detailsLabel, contentPendingIntent)
            .addAction(0, dismissLabel, dismissPendingIntent)

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_FORECAST, builder.build())
        } catch (_: SecurityException) {}
    }

    /**
     * Posts an alert when an individual app exceeds its daily limit.
     */
    fun notifyAppDailyQuotaExceeded(
        appName: String,
        usedBytes: Long,
        thresholdBytes: Long = 3L * 1024L * 1024L * 1024L,
        isArabic: Boolean = true
    ) {
        if (!hasNotificationPermission()) return

        val formattedUsed = FormatUtils.formatBytes(usedBytes)
        val formattedLimit = FormatUtils.formatBytes(thresholdBytes)

        val title = if (isArabic) {
            "تحذير استهلاك: $appName تجاوز $formattedLimit اليوم!"
        } else {
            "Usage Alert: $appName exceeded $formattedLimit today!"
        }

        val message = if (isArabic) {
            "استهلك تطبيق $appName ما يقارب $formattedUsed اليوم متجاوزاً حد الاستهلاك اليومي المحدد ($formattedLimit)."
        } else {
            "App $appName has consumed $formattedUsed today, exceeding your customized daily limit of $formattedLimit."
        }

        val detailsLabel = if (isArabic) "استهلاك التطبيقات" else "App Usage"
        val dismissLabel = if (isArabic) "تجاهل" else "Dismiss"

        val notificationId = NOTIFICATION_ID_APP_BASE + (appName.hashCode() and 0xFFFF)
        val contentPendingIntent = createNavPendingIntent("USAGE", notificationId)
        val dismissPendingIntent = createDismissPendingIntent(notificationId)

        val builder = NotificationCompat.Builder(context, CHANNEL_DATA_CAP_ALERTS)
            .setSmallIcon(R.drawable.ic_notification_pulse)
            .setColor(Color.parseColor("#00F5D4"))
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(0, detailsLabel, contentPendingIntent)
            .addAction(0, dismissLabel, dismissPendingIntent)

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {}
    }

    /**
     * Sends an instant test Heads-Up notification so users can verify notification banners.
     */
    fun sendTestHeadsUpNotification(isArabic: Boolean = false) {
        if (!hasNotificationPermission()) return

        val title = if (isArabic) "⚡ نبض البيانات: إشعار عائم تجريبي" else "⚡ Data Pulse: Heads-Up Alert Test"
        val message = if (isArabic) {
            "تم تفعيل الإشعارات العائمة (Heads-Up) بنجاح. ستتلقى تنبيهات فورية عند وصول الباقة لحدود الخطر أو تسارع معدل الحرق."
        } else {
            "Heads-Up Push notifications are active! You will receive floating alerts for critical quota thresholds and burn rate depletion."
        }

        val detailsLabel = if (isArabic) "فتح التطبيق" else "Open App"
        val dismissLabel = if (isArabic) "إغلاق" else "Dismiss"

        val contentPendingIntent = createNavPendingIntent("DASHBOARD", NOTIFICATION_ID_TEST)
        val dismissPendingIntent = createDismissPendingIntent(NOTIFICATION_ID_TEST)

        val builder = NotificationCompat.Builder(context, CHANNEL_DATA_CAP_ALERTS)
            .setSmallIcon(R.drawable.ic_notification_pulse)
            .setColor(Color.parseColor("#00F5D4"))
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(0, detailsLabel, contentPendingIntent)
            .addAction(0, dismissLabel, dismissPendingIntent)

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_TEST, builder.build())
        } catch (_: SecurityException) {}
    }

    companion object {
        const val CHANNEL_DATA_CAP_ALERTS = "data_pulse_quota_alerts"
        const val CHANNEL_FORECAST_ALERTS = "data_pulse_forecast_alerts"
        const val CHANNEL_NETWORK_ALERTS = "data_pulse_network_alerts"

        const val EXTRA_NAV_DESTINATION = "extra_nav_destination"

        const val NOTIFICATION_ID_QUOTA = 1001
        const val NOTIFICATION_ID_FORECAST = 1002
        const val NOTIFICATION_ID_NETWORK = 1003
        const val NOTIFICATION_ID_TEST = 1004
        const val NOTIFICATION_ID_APP_BASE = 2000
    }
}
