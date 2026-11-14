package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.FormatUtils
import com.example.ui.components.BiologicalHeartCalculator
import com.example.ui.components.BiologicalHeartState
import com.example.ui.navigation.NavDestination
import com.example.ui.navigation.getLabel
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

  @Test
  fun `verify multi theme engine and light mode configuration`() {
    // 1. Verify 6 theme modes (including SYSTEM and NEUMORPHISM)
    val modes = com.example.model.AppThemeMode.values()
    assertEquals(6, modes.size)
    assertTrue(modes.contains(com.example.model.AppThemeMode.SYSTEM))
    assertTrue(modes.contains(com.example.model.AppThemeMode.LIGHT_MODE))
    assertTrue(modes.contains(com.example.model.AppThemeMode.NEUMORPHISM))
    assertTrue(modes.contains(com.example.model.AppThemeMode.LIQUID_GLASS_DEFAULT))
    assertTrue(modes.contains(com.example.model.AppThemeMode.LIQUID_NEON_CYAN))
    assertTrue(modes.contains(com.example.model.AppThemeMode.AMOLED_PITCH_BLACK))

    // 2. Verify Pearl & Platinum Light Mode properties
    val light = com.example.model.AppThemeMode.LIGHT_MODE
    assertEquals(false, light.isDark)
    assertEquals("الوضع النهاري الفاخر", light.getDisplayName(com.example.model.AppLanguage.ARABIC))
    assertEquals("Pearl & Platinum", light.getDisplayName(com.example.model.AppLanguage.ENGLISH))

    // 3. Verify Neumorphism theme properties & color tokens
    val neumorphism = com.example.model.AppThemeMode.NEUMORPHISM
    assertEquals(false, neumorphism.isDark)
    assertEquals("نيومورفيزم", neumorphism.getDisplayName(com.example.model.AppLanguage.ARABIC))
    assertEquals("Neumorphism", neumorphism.getDisplayName(com.example.model.AppLanguage.ENGLISH))

    val neuroStyle = com.example.ui.theme.createLiquidGlassStyle(com.example.model.AppThemeMode.NEUMORPHISM)
    assertEquals(false, neuroStyle.isDark)
    assertEquals(true, neuroStyle.isNeumorphic)
    assertEquals(androidx.compose.ui.graphics.Color(0xFFEEF2F6), neuroStyle.surfaceColor)
    assertEquals(androidx.compose.ui.graphics.Color(0xFFFFFFFF), neuroStyle.neumorphicHighlight)
    assertEquals(androidx.compose.ui.graphics.Color(0xFFD1D9E6), neuroStyle.neumorphicShadow)
    assertEquals(androidx.compose.ui.graphics.Color(0xFF1E293B), neuroStyle.textPrimary)
    assertEquals(androidx.compose.ui.graphics.Color(0xFF64748B), neuroStyle.textSecondary)
    assertEquals(androidx.compose.ui.graphics.Color(0xFF2563EB), neuroStyle.accentPrimary)
    assertEquals(androidx.compose.ui.graphics.Color(0xFF10B981), neuroStyle.accentSecondary)
    assertEquals(androidx.compose.ui.graphics.Color(0xFFEF4444), com.example.ui.theme.NeumorphicColors.AccentRed)

    // 4. Verify LiquidGlassStyle tokens for Pearl & Platinum Light Mode
    val lightStyle = com.example.ui.theme.createLiquidGlassStyle(com.example.model.AppThemeMode.LIGHT_MODE)
    assertEquals(false, lightStyle.isDark)
    assertEquals(false, lightStyle.isNeumorphic)
    assertEquals(com.example.ui.theme.PearlPlatinumLightBase, lightStyle.neumorphicBase)
    assertEquals(com.example.ui.theme.PearlPlatinumTextPrimary, lightStyle.textPrimary)

    // 5. Verify SYSTEM theme mode dynamic resolution
    val systemLightStyle = com.example.ui.theme.createLiquidGlassStyle(com.example.model.AppThemeMode.SYSTEM, isSystemDark = false)
    assertEquals(false, systemLightStyle.isDark)
    val systemDarkStyle = com.example.ui.theme.createLiquidGlassStyle(com.example.model.AppThemeMode.SYSTEM, isSystemDark = true)
    assertEquals(true, systemDarkStyle.isDark)

    // 6. Verify Localized Strings
    assertEquals("Multi-Theme Engine", com.example.model.AppStrings.multiThemeEngineSection(com.example.model.AppLanguage.ENGLISH))
    assertTrue(com.example.model.AppStrings.multiThemeEngineSection(com.example.model.AppLanguage.ARABIC).contains("Multi-Theme Engine"))
  }

  @Test
  fun `verify monthly data plan limit and edit cap strings`() {
    assertEquals("تعديل الحد", com.example.model.AppStrings.editCap(com.example.model.AppLanguage.ARABIC))
    assertEquals("Edit Cap", com.example.model.AppStrings.editCap(com.example.model.AppLanguage.ENGLISH))
    assertEquals("حد باقة البيانات الشهرية", com.example.model.AppStrings.planLimitSection(com.example.model.AppLanguage.ARABIC))
    assertEquals("MONTHLY DATA PLAN LIMIT", com.example.model.AppStrings.planLimitSection(com.example.model.AppLanguage.ENGLISH))
  }

  @Test
  fun `verify private dns presets including adguard recommendation`() {
    val presets = com.example.model.PrivateDnsPreset.values()
    assertEquals(7, presets.size)
    assertEquals(5, com.example.model.PrivateDnsPreset.integratedServers.size)

    val adguard = com.example.model.PrivateDnsPreset.ADGUARD
    assertEquals("dns.adguard.com", adguard.hostname)
    assertEquals("94.140.14.14", adguard.primaryIp)
    assertEquals("94.140.15.15", adguard.secondaryIp)
    assertTrue(adguard.isRecommended)
    assertEquals("AdGuard DNS", adguard.getDisplayName(com.example.model.AppLanguage.ENGLISH))
    assertEquals("AdGuard DNS", adguard.getDisplayName(com.example.model.AppLanguage.ARABIC))

    val cloudflare = com.example.model.PrivateDnsPreset.CLOUDFLARE
    assertEquals("one.one.one.one", cloudflare.hostname)
    assertEquals("1.1.1.1", cloudflare.primaryIp)
    assertEquals("1.0.0.1", cloudflare.secondaryIp)
    assertEquals("Cloudflare (1.1.1.1)", cloudflare.getDisplayName(com.example.model.AppLanguage.ENGLISH))
    assertEquals(false, cloudflare.isRecommended)

    val google = com.example.model.PrivateDnsPreset.GOOGLE
    assertEquals("dns.google", google.hostname)
    assertEquals("8.8.8.8", google.primaryIp)
    assertEquals("8.8.4.4", google.secondaryIp)
    assertEquals("Google Public DNS", google.getDisplayName(com.example.model.AppLanguage.ENGLISH))

    val quad9 = com.example.model.PrivateDnsPreset.QUAD9
    assertEquals("dns.quad9.net", quad9.hostname)
    assertEquals("9.9.9.9", quad9.primaryIp)
    assertEquals("149.112.112.112", quad9.secondaryIp)
    assertEquals("Quad9 DNS", quad9.getDisplayName(com.example.model.AppLanguage.ENGLISH))

    val openDns = com.example.model.PrivateDnsPreset.OPENDNS
    assertEquals("dns.opendns.com", openDns.hostname)
    assertEquals("208.67.222.222", openDns.primaryIp)
    assertEquals("208.67.220.220", openDns.secondaryIp)
    assertEquals("Cisco OpenDNS", openDns.getDisplayName(com.example.model.AppLanguage.ENGLISH))

    val off = com.example.model.PrivateDnsPreset.OFF
    assertEquals(null, off.hostname)

    val custom = com.example.model.PrivateDnsPreset.CUSTOM
    assertEquals(null, custom.hostname)

    assertTrue(com.example.model.AppStrings.privateDnsAdGuardBadge(com.example.model.AppLanguage.ARABIC).contains("موصى به"))
    assertTrue(com.example.model.AppStrings.privateDnsAdGuardBadge(com.example.model.AppLanguage.ENGLISH).contains("RECOMMENDED"))
  }

  @Test
  fun `verify dns navigation destination and dedicated dns screen strings`() {
    assertEquals("DNS", com.example.ui.navigation.NavDestination.DNS.getLabel(com.example.model.AppLanguage.ENGLISH))
    assertEquals("DNS", com.example.ui.navigation.NavDestination.DNS.getLabel(com.example.model.AppLanguage.ARABIC))
    assertEquals("التحليل الذكي", NavDestination.FORECAST.getLabel(com.example.model.AppLanguage.ARABIC))
    assertEquals("Smart Forecast", NavDestination.FORECAST.getLabel(com.example.model.AppLanguage.ENGLISH))
    assertEquals("درع DNS الذكي", com.example.model.AppStrings.dnsScreenTitle(com.example.model.AppLanguage.ARABIC))
    assertEquals("Smart DNS Shield", com.example.model.AppStrings.dnsScreenTitle(com.example.model.AppLanguage.ENGLISH))
  }
}
