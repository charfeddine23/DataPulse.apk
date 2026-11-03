package com.example.model

import android.graphics.drawable.Drawable
import java.util.Locale

enum class NetworkType {
    WIFI,
    CELLULAR_5G,
    CELLULAR_4G,
    CELLULAR_3G,
    CELLULAR_GENERIC,
    ETHERNET,
    OFFLINE
}

enum class QuotaStatus {
    SAFE,       // < 80%
    WARNING,    // 80% .. 94.9%
    CRITICAL,   // 95% .. 99.9%
    EXCEEDED    // >= 100%
}

enum class ThemeMode(val title: String) {
    SYSTEM("Follow System"),
    LIGHT("Light Mode"),
    DARK("Dark Mode")
}

enum class TimeRange(val displayName: String) {
    TODAY("Today"),
    WEEK("This Week"),
    MONTH("This Month")
}

enum class InterfaceFilter(val displayName: String) {
    ALL("All Traffic"),
    MOBILE("Mobile Data"),
    WIFI("Wi-Fi")
}

data class NetworkUsageSummary(
    val mobileTodayBytes: Long = 0L,
    val mobileMonthBytes: Long = 0L,
    val wifiTodayBytes: Long = 0L,
    val wifiMonthBytes: Long = 0L,
    val mobileTotalBytes: Long = 0L,
    val wifiTotalBytes: Long = 0L,
    val hasUsagePermission: Boolean = false
) {
    val totalTodayBytes: Long get() = mobileTodayBytes + wifiTodayBytes
    val totalMonthBytes: Long get() = mobileMonthBytes + wifiMonthBytes
}

data class AppUsageItem(
    val packageName: String,
    val appName: String,
    val icon: Drawable? = null,
    val mobileBytes: Long = 0L,
    val wifiBytes: Long = 0L,
    val totalBytes: Long = mobileBytes + wifiBytes,
    val percentageOfTotal: Float = 0f
)

data class LiveSpeedSample(
    val downloadBps: Double = 0.0,
    val uploadBps: Double = 0.0,
    val peakDownloadBps: Double = 0.0,
    val peakUploadBps: Double = 0.0,
    val downloadFormatted: String = "0.0 Kbps",
    val uploadFormatted: String = "0.0 Kbps",
    val connectionType: NetworkType = NetworkType.OFFLINE,
    val connectionName: String = "Disconnected",
    val ipAddress: String = "Unknown",
    val pingMs: Long = 0L,
    val timestamp: Long = System.currentTimeMillis()
)

data class SimCardInfo(
    val subId: Int,
    val slotIndex: Int,
    val carrierName: String,
    val displayName: String,
    val isDefaultData: Boolean = false
)

data class DataPlanQuota(
    val planLimitGB: Float = 10.0f,
    val billingCycleStartDay: Int = 1,
    val alertThreshold80: Boolean = true,
    val alertThreshold95: Boolean = true,
    val usedBytes: Long = 0L,
    val remainingBytes: Long = 0L,
    val usagePercent: Float = 0f,
    val daysRemaining: Int = 30,
    val status: QuotaStatus = QuotaStatus.SAFE
)

object FormatUtils {
    fun formatBytes(bytes: Long): String {
        if (bytes <= 0L) return "0 B"
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        val tb = gb / 1024.0
        return when {
            tb >= 1.0 -> String.format(Locale.US, "%.2f TB", tb)
            gb >= 1.0 -> String.format(Locale.US, "%.2f GB", gb)
            mb >= 1.0 -> String.format(Locale.US, "%.1f MB", mb)
            kb >= 1.0 -> String.format(Locale.US, "%.1f KB", kb)
            else -> "$bytes B"
        }
    }

    fun formatSpeed(bps: Double): String {
        if (bps <= 0.0) return "0.0 Kbps"
        val kbps = bps / 1000.0
        val mbps = kbps / 1000.0
        val gbps = mbps / 1000.0
        return when {
            gbps >= 1.0 -> String.format(Locale.US, "%.2f Gbps", gbps)
            mbps >= 1.0 -> String.format(Locale.US, "%.1f Mbps", mbps)
            else -> String.format(Locale.US, "%.0f Kbps", kbps)
        }
    }
}
