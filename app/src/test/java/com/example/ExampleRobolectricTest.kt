package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.FormatUtils
import com.example.ui.components.BiologicalHeartCalculator
import com.example.ui.components.BiologicalHeartState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Data Pulse", appName)
  }

  @Test
  fun `verify byte formatting utility`() {
    assertEquals("0 B", FormatUtils.formatBytes(0L))
    assertEquals("1.0 KB", FormatUtils.formatBytes(1024L))
    assertEquals("1.0 MB", FormatUtils.formatBytes(1024L * 1024L))
    assertEquals("1.00 GB", FormatUtils.formatBytes(1024L * 1024L * 1024L))
  }

  @Test
  fun `verify 30-level biological living and freezing heart engine states`() {
    // 1. Living State (Levels 1 to 15)
    val living0 = BiologicalHeartCalculator.calculate(0f)
    assertEquals(1, living0.level)
    assertEquals(BiologicalHeartState.LIVING, living0.biologicalState)
    assertEquals(1400, living0.cycleDurationMs)
    assertEquals(0f, living0.freezeFactor, 0.01f)

    val living30 = BiologicalHeartCalculator.calculate(30f)
    assertTrue(living30.level in 1..15)
    assertEquals(BiologicalHeartState.LIVING, living30.biologicalState)

    // 2. Straining State (Levels 16 to 27)
    val strain60 = BiologicalHeartCalculator.calculate(60f)
    assertTrue(strain60.level in 16..27)
    assertEquals(BiologicalHeartState.STRAINING, strain60.biologicalState)
    assertTrue(strain60.strainFactor > 0f)
    assertTrue(strain60.cycleDurationMs in 500..1000)

    // 3. Frozen State (Levels 28 to 30)
    val frozen95 = BiologicalHeartCalculator.calculate(95f)
    assertTrue(frozen95.level in 28..30)
    assertEquals(BiologicalHeartState.FROZEN, frozen95.biologicalState)
    assertTrue(frozen95.freezeFactor > 0f)

    val frozen100 = BiologicalHeartCalculator.calculate(100f)
    assertEquals(30, frozen100.level)
    assertEquals(BiologicalHeartState.FROZEN, frozen100.biologicalState)
    assertEquals(1.0f, frozen100.freezeFactor, 0.01f)
  }

  @Test
  fun `verify rank localization and combined data calculation`() {
    // 1. Strings localization
    assertEquals("الرتبة", com.example.model.AppStrings.navRank(com.example.model.AppLanguage.ARABIC))
    assertEquals("Rank", com.example.model.AppStrings.navRank(com.example.model.AppLanguage.ENGLISH))
    assertEquals("الرتبة الأولى", com.example.model.AppStrings.rankFirst(com.example.model.AppLanguage.ARABIC))
    assertEquals("1st Place", com.example.model.AppStrings.rankFirst(com.example.model.AppLanguage.ENGLISH))

    // 2. Combined Wi-Fi and Cellular sorting
    val itemA = com.example.model.AppUsageItem("pkg.a", "App A", mobileBytes = 500L, wifiBytes = 1500L) // 2000 total
    val itemB = com.example.model.AppUsageItem("pkg.b", "App B", mobileBytes = 3000L, wifiBytes = 200L) // 3200 total
    val itemC = com.example.model.AppUsageItem("pkg.c", "App C", mobileBytes = 100L, wifiBytes = 100L) // 200 total

    val list = listOf(itemA, itemB, itemC)
    val sorted = list.sortedByDescending { it.mobileBytes + it.wifiBytes }

    assertEquals("pkg.b", sorted[0].packageName)
    assertEquals(3200L, sorted[0].totalBytes)
    assertEquals("pkg.a", sorted[1].packageName)
    assertEquals(2000L, sorted[1].totalBytes)
    assertEquals("pkg.c", sorted[2].packageName)
    assertEquals(200L, sorted[2].totalBytes)
  }
}
