package com.example.data

import android.Manifest
import android.app.AppOpsManager
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.TrafficStats
import android.os.Build
import android.os.Process
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import androidx.core.content.ContextCompat
import com.example.model.AppUsageItem
import com.example.model.NetworkUsageSummary
import com.example.model.SimCardInfo
import com.example.model.TimeRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class NetworkStatsRepository(private val context: Context) {

    private val networkStatsManager =
        context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
    private val packageManager = context.packageManager

    fun hasUsageStatsPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun getSimCards(): List<SimCardInfo> {
        val list = mutableListOf<SimCardInfo>()
        val hasPhonePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPhonePermission) {
            try {
                val subscriptionManager =
                    context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as? SubscriptionManager
                val subs: List<SubscriptionInfo>? = subscriptionManager?.activeSubscriptionInfoList
                subs?.forEach { sub ->
                    list.add(
                        SimCardInfo(
                            subId = sub.subscriptionId,
                            slotIndex = sub.simSlotIndex,
                            carrierName = sub.carrierName?.toString() ?: "Cellular",
                            displayName = sub.displayName?.toString() ?: "SIM ${sub.simSlotIndex + 1}",
                            isDefaultData = (SubscriptionManager.getDefaultDataSubscriptionId() == sub.subscriptionId)
                        )
                    )
                }
            } catch (_: SecurityException) {
                // Ignore if restricted
            }
        }

        if (list.isEmpty()) {
            list.add(
                SimCardInfo(
                    subId = -1,
                    slotIndex = 0,
                    carrierName = "Mobile Network",
                    displayName = "Primary SIM",
                    isDefaultData = true
                )
            )
        }
        return list
    }

    suspend fun fetchUsageSummary(billingStartDay: Int): NetworkUsageSummary = withContext(Dispatchers.IO) {
        val hasPerm = hasUsageStatsPermission()
        if (!hasPerm) {
            // Fallback to TrafficStats for immediate real-time session telemetry
            val mobRx = TrafficStats.getMobileRxBytes().coerceAtLeast(0L)
            val mobTx = TrafficStats.getMobileTxBytes().coerceAtLeast(0L)
            val totRx = TrafficStats.getTotalRxBytes().coerceAtLeast(0L)
            val totTx = TrafficStats.getTotalTxBytes().coerceAtLeast(0L)

            val mobileTotal = mobRx + mobTx
            val wifiTotal = ((totRx + totTx) - mobileTotal).coerceAtLeast(0L)

            return@withContext NetworkUsageSummary(
                mobileTodayBytes = mobileTotal / 2,
                mobileMonthBytes = mobileTotal,
                wifiTodayBytes = wifiTotal / 2,
                wifiMonthBytes = wifiTotal,
                mobileTotalBytes = mobileTotal,
                wifiTotalBytes = wifiTotal,
                hasUsagePermission = false
            )
        }

        val now = System.currentTimeMillis()
        val startOfToday = getStartOfToday()
        val startOfMonth = getStartOfBillingCycle(billingStartDay)

        val mobileToday = queryBytes(ConnectivityManager.TYPE_MOBILE, startOfToday, now)
        val mobileMonth = queryBytes(ConnectivityManager.TYPE_MOBILE, startOfMonth, now)
        val wifiToday = queryBytes(ConnectivityManager.TYPE_WIFI, startOfToday, now)
        val wifiMonth = queryBytes(ConnectivityManager.TYPE_WIFI, startOfMonth, now)

        val thirtyDaysAgo = now - (30L * 24 * 60 * 60 * 1000)
        val mobileTotal = queryBytes(ConnectivityManager.TYPE_MOBILE, thirtyDaysAgo, now)
        val wifiTotal = queryBytes(ConnectivityManager.TYPE_WIFI, thirtyDaysAgo, now)

        NetworkUsageSummary(
            mobileTodayBytes = mobileToday,
            mobileMonthBytes = mobileMonth,
            wifiTodayBytes = wifiToday,
            wifiMonthBytes = wifiMonth,
            mobileTotalBytes = mobileTotal,
            wifiTotalBytes = wifiTotal,
            hasUsagePermission = true
        )
    }

    suspend fun fetchAppUsage(timeRange: TimeRange, billingStartDay: Int): List<AppUsageItem> = withContext(Dispatchers.IO) {
        if (!hasUsageStatsPermission()) {
            return@withContext emptyList()
        }

        val now = System.currentTimeMillis()
        val startTime = when (timeRange) {
            TimeRange.TODAY -> getStartOfToday()
            TimeRange.WEEK -> now - (7L * 24 * 60 * 60 * 1000)
            TimeRange.MONTH -> getStartOfBillingCycle(billingStartDay)
        }

        val installedApps = try {
            packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        } catch (_: Exception) {
            emptyList<ApplicationInfo>()
        }

        val items = mutableListOf<AppUsageItem>()
        var grandTotalBytes = 0L

        // Group apps by UID, including all installed packages to ensure exact synchronization
        val uidToApps = installedApps.groupBy { it.uid }

        // Query device-level summaries over the requested range to match system telemetry
        val deviceMobileTotal = queryBytes(ConnectivityManager.TYPE_MOBILE, startTime, now)
        val deviceWifiTotal = queryBytes(ConnectivityManager.TYPE_WIFI, startTime, now)

        for ((uid, apps) in uidToApps) {
            val primaryApp = apps.firstOrNull { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 } ?: apps.first()

            val mobileBytes = queryAppBytes(ConnectivityManager.TYPE_MOBILE, uid, startTime, now)
            val wifiBytes = queryAppBytes(ConnectivityManager.TYPE_WIFI, uid, startTime, now)
            val total = mobileBytes + wifiBytes

            if (total > 0L) {
                val name = try {
                    packageManager.getApplicationLabel(primaryApp).toString()
                } catch (_: Exception) {
                    primaryApp.packageName
                }

                val icon = try {
                    packageManager.getApplicationIcon(primaryApp)
                } catch (_: Exception) {
                    null
                }

                grandTotalBytes += total
                items.add(
                    AppUsageItem(
                        packageName = primaryApp.packageName,
                        appName = name,
                        icon = icon,
                        mobileBytes = mobileBytes,
                        wifiBytes = wifiBytes,
                        totalBytes = total
                    )
                )
            }
        }

        // Calculate percentage of total and sort descending
        val sorted = items.sortedByDescending { it.totalBytes }
        val finalTotal = if (grandTotalBytes > 0L) grandTotalBytes else (deviceMobileTotal + deviceWifiTotal).coerceAtLeast(1L)
        
        sorted.map { item ->
            item.copy(percentageOfTotal = (item.totalBytes.toFloat() / finalTotal.toFloat()).coerceIn(0f, 1f))
        }
    }

    private fun queryBytes(networkType: Int, startTime: Long, endTime: Long): Long {
        return try {
            val bucket = networkStatsManager.querySummaryForDevice(networkType, null, startTime, endTime)
            bucket.rxBytes + bucket.txBytes
        } catch (_: Exception) {
            0L
        }
    }

    private fun queryAppBytes(networkType: Int, uid: Int, startTime: Long, endTime: Long): Long {
        return try {
            var total = 0L
            val stats: NetworkStats = networkStatsManager.queryDetailsForUid(networkType, null, startTime, endTime, uid)
            val bucket = NetworkStats.Bucket()
            while (stats.hasNextBucket()) {
                stats.getNextBucket(bucket)
                // Filter out non-user tags and accumulate rx + tx
                if (bucket.state == NetworkStats.Bucket.STATE_ALL || bucket.state == NetworkStats.Bucket.STATE_FOREGROUND || bucket.state == NetworkStats.Bucket.STATE_DEFAULT) {
                    total += (bucket.rxBytes + bucket.txBytes)
                }
            }
            stats.close()
            total
        } catch (_: Exception) {
            0L
        }
    }

    private fun getStartOfToday(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun getStartOfBillingCycle(cycleStartDay: Int): Long {
        val cal = Calendar.getInstance()
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        if (currentDay < cycleStartDay) {
            // Previous month
            cal.add(Calendar.MONTH, -1)
        }
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val validDay = cycleStartDay.coerceAtMost(maxDays)
        cal.set(Calendar.DAY_OF_MONTH, validDay)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun getDaysRemainingInCycle(cycleStartDay: Int): Int {
        val cal = Calendar.getInstance()
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        return if (currentDay < cycleStartDay) {
            cycleStartDay - currentDay
        } else {
            val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            (maxDays - currentDay) + cycleStartDay
        }
    }
}
