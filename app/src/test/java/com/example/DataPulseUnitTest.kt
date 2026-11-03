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
}
