package com.example

import com.example.model.FormatUtils
import com.example.model.QuotaStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class DataPulseUnitTest {

    @Test
    fun testFormatBytes() {
        assertEquals("0 B", FormatUtils.formatBytes(0L))
        assertEquals("500 B", FormatUtils.formatBytes(500L))
        assertEquals("1.5 KB", FormatUtils.formatBytes(1536L))
        assertEquals("10.0 MB", FormatUtils.formatBytes(10L * 1024L * 1024L))
        assertEquals("2.50 GB", FormatUtils.formatBytes((2.5 * 1024L * 1024L * 1024L).toLong()))
    }

    @Test
    fun testFormatSpeed() {
        assertEquals("0.0 Kbps", FormatUtils.formatSpeed(0.0))
        assertEquals("500 Kbps", FormatUtils.formatSpeed(500_000.0))
        assertEquals("15.5 Mbps", FormatUtils.formatSpeed(15_500_000.0))
    }

    @Test
    fun testQuotaStatusThresholds() {
        val statuses = listOf(
            QuotaStatus.SAFE,
            QuotaStatus.WARNING,
            QuotaStatus.CRITICAL,
            QuotaStatus.EXCEEDED
        )
        assertEquals(4, statuses.size)
    }

    @Test
    fun testSmartDataForecastPrediction() {
        val gb = 1024L * 1024L * 1024L
        val planLimitGB = 120.0f
        // Used 100 GB out of 120 GB with high daily burn rate (10 GB/day)
        val usedBytes = 100L * gb
        val todayBytes = 10L * gb

        val forecast = com.example.model.DataForecastCalculator.calculate(
            usedMobileBytes = usedBytes,
            planLimitGB = planLimitGB,
            billingCycleStartDay = 1,
            daysRemainingInCycle = 15,
            mobileTodayBytes = todayBytes
        )

        // 20 GB remaining with >= 10 GB/day burn rate will deplete in <= 3 days -> CRITICAL_DEPLETION
        org.junit.Assert.assertTrue(forecast.predictedDaysRemaining <= 3)
        assertEquals(com.example.model.ForecastStatus.CRITICAL_DEPLETION, forecast.status)
        org.junit.Assert.assertTrue(forecast.willDepleteBeforeCycleEnd)
    }

    @Test
    fun testSmartDataForecastSustainable() {
        val gb = 1024L * 1024L * 1024L
        val planLimitGB = 120.0f
        // Used 20 GB out of 120 GB, with only 1 GB today and 10 days remaining
        val usedBytes = 20L * gb
        val todayBytes = 1L * gb

        val forecast = com.example.model.DataForecastCalculator.calculate(
            usedMobileBytes = usedBytes,
            planLimitGB = planLimitGB,
            billingCycleStartDay = 1,
            daysRemainingInCycle = 10,
            mobileTodayBytes = todayBytes
        )

        // With 100 GB remaining and 10 days left in cycle, pace is sustainable
        assertEquals(com.example.model.ForecastStatus.SUSTAINABLE, forecast.status)
        org.junit.Assert.assertFalse(forecast.willDepleteBeforeCycleEnd)
        org.junit.Assert.assertTrue(forecast.isSurplus)
    }
}

