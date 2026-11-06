package com.example.model

import java.util.Calendar

enum class ForecastStatus {
    CRITICAL_DEPLETION,  // Depletes in <= 3 days or already exhausted
    EARLY_DEPLETION,     // Depletes before the billing cycle ends (> 3 days)
    SUSTAINABLE,         // Data will comfortably last through the billing cycle
    EXHAUSTED            // Quota already at or above 100%
}

data class SmartDataForecast(
    val dailyBurnRateBytes: Long = 0L,
    val predictedDaysRemaining: Int = 0,
    val daysRemainingInCycle: Int = 30,
    val willDepleteBeforeCycleEnd: Boolean = false,
    val projectedEndOfCycleUsageBytes: Long = 0L,
    val projectedSurplusDeficitBytes: Long = 0L,
    val status: ForecastStatus = ForecastStatus.SUSTAINABLE,
    val planLimitBytes: Long = 0L,
    val usedBytes: Long = 0L,
    val planLimitGB: Float = 10.0f
) {
    val dailyBurnRateFormatted: String
        get() = "${FormatUtils.formatBytes(dailyBurnRateBytes)} / day"

    val isSurplus: Boolean
        get() = projectedSurplusDeficitBytes >= 0L
}

object DataForecastCalculator {

    /**
     * Calculates the smart forecasting metrics based on historical consumption,
     * days elapsed in billing cycle, remaining data cap, and current burn rate.
     */
    fun calculate(
        usedMobileBytes: Long,
        planLimitGB: Float,
        billingCycleStartDay: Int,
        daysRemainingInCycle: Int,
        mobileTodayBytes: Long
    ): SmartDataForecast {
        val planLimitBytes = (planLimitGB * 1024L * 1024L * 1024L).toLong()
        val remainingBytes = (planLimitBytes - usedMobileBytes).coerceAtLeast(0L)

        // Calculate days elapsed in current cycle
        val now = System.currentTimeMillis()
        val cycleStartMs = getStartOfBillingCycle(billingCycleStartDay)
        val elapsedMillis = (now - cycleStartMs).coerceAtLeast(0L)
        val daysElapsed = (elapsedMillis / (1000L * 60 * 60 * 24)).toInt().coerceAtLeast(1)

        // Calculate average daily burn rate
        val cycleDailyAverage = if (usedMobileBytes > 0L) usedMobileBytes / daysElapsed else 0L
        // Smooth burn rate: weight with today's activity if non-zero
        val burnRate = when {
            cycleDailyAverage > 0L -> cycleDailyAverage
            mobileTodayBytes > 0L -> mobileTodayBytes
            else -> 0L
        }

        // Check if already exhausted
        if (planLimitBytes > 0L && usedMobileBytes >= planLimitBytes) {
            return SmartDataForecast(
                dailyBurnRateBytes = burnRate,
                predictedDaysRemaining = 0,
                daysRemainingInCycle = daysRemainingInCycle,
                willDepleteBeforeCycleEnd = true,
                projectedEndOfCycleUsageBytes = usedMobileBytes + (burnRate * daysRemainingInCycle),
                projectedSurplusDeficitBytes = -usedMobileBytes,
                status = ForecastStatus.EXHAUSTED,
                planLimitBytes = planLimitBytes,
                usedBytes = usedMobileBytes,
                planLimitGB = planLimitGB
            )
        }

        // Predict days remaining until exhaustion
        val predictedDays = if (burnRate > 0L) {
            (remainingBytes.toDouble() / burnRate.toDouble()).toInt().coerceAtLeast(0)
        } else {
            daysRemainingInCycle.coerceAtLeast(1)
        }

        val willDepleteEarly = (burnRate > 0L) && (predictedDays < daysRemainingInCycle)

        val status = when {
            burnRate <= 0L -> ForecastStatus.SUSTAINABLE
            predictedDays <= 3 -> ForecastStatus.CRITICAL_DEPLETION
            willDepleteEarly -> ForecastStatus.EARLY_DEPLETION
            else -> ForecastStatus.SUSTAINABLE
        }

        val projectedTotal = usedMobileBytes + (burnRate * daysRemainingInCycle.coerceAtLeast(0))
        val surplusDeficit = planLimitBytes - projectedTotal

        return SmartDataForecast(
            dailyBurnRateBytes = burnRate,
            predictedDaysRemaining = predictedDays,
            daysRemainingInCycle = daysRemainingInCycle,
            willDepleteBeforeCycleEnd = willDepleteEarly,
            projectedEndOfCycleUsageBytes = projectedTotal,
            projectedSurplusDeficitBytes = surplusDeficit,
            status = status,
            planLimitBytes = planLimitBytes,
            usedBytes = usedMobileBytes,
            planLimitGB = planLimitGB
        )
    }

    private fun getStartOfBillingCycle(cycleStartDay: Int): Long {
        val cal = Calendar.getInstance()
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        if (currentDay < cycleStartDay) {
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
}
