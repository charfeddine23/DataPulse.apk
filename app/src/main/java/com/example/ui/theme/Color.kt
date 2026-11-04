package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// =========================================================================
// 1. LIQUID_GLASS_DEFAULT Palette
// Background: Deep dark glass background (#0B0F19)
// Surface: Translucent dark glass (~85% opacity #D90E1628)
// Primary: Vibrant Blue (#0066FF)
// Accent: Violet (#7C4DFF)
// =========================================================================
val LiquidGlassDefaultBackground = Color(0xFF0B0F19)
val LiquidGlassDefaultSurface = Color(0xD90E1628)
val LiquidGlassDefaultSurfaceTranslucent = Color(0x99131F37)
val LiquidGlassDefaultPrimary = Color(0xFF0066FF)
val LiquidGlassDefaultAccent = Color(0xFF7C4DFF)
val LiquidGlassDefaultBorder = Color(0x330066FF)
val LiquidGlassDefaultCardBg = Color(0xFF0F1728)
val LiquidGlassDefaultCardSubtleBg = Color(0xFF0B111E)

// =========================================================================
// 2. LIQUID_NEON_CYAN Palette
// Background: Dark slate background (#020617)
// Surface: Translucent dark slate (#D90F172A)
// Primary: Bright cyan (#00E5FF)
// Accent: Neon green / teal (#00FFA3)
// =========================================================================
val LiquidNeonCyanBackground = Color(0xFF020617)
val LiquidNeonCyanSurface = Color(0xD90F172A)
val LiquidNeonCyanSurfaceTranslucent = Color(0x990F172A)
val LiquidNeonCyanPrimary = Color(0xFF00E5FF)
val LiquidNeonCyanAccent = Color(0xFF00FFA3)
val LiquidNeonCyanBorder = Color(0x3300E5FF)
val LiquidNeonCyanCardBg = Color(0xFF090E1F)
val LiquidNeonCyanCardSubtleBg = Color(0xFF030712)

// =========================================================================
// 3. AMOLED_PITCH_BLACK Palette
// Background: Pure pitch-black (#000000) for OLED battery saving
// Surface: Dark gray surface (#121212)
// Primary: Electric blue (#3B82F6)
// Accent: Crisp Cyan (#06B6D4)
// =========================================================================
val AmoledPitchBlackBackground = Color(0xFF000000)
val AmoledPitchBlackSurface = Color(0xFF121212)
val AmoledPitchBlackSurfaceTranslucent = Color(0xCC121212)
val AmoledPitchBlackPrimary = Color(0xFF3B82F6)
val AmoledPitchBlackAccent = Color(0xFF06B6D4)
val AmoledPitchBlackBorder = Color(0x333B82F6)
val AmoledPitchBlackCardBg = Color(0xFF0D0D0D)
val AmoledPitchBlackCardSubtleBg = Color(0xFF050505)

// Shared Common Colors & Status Alerts
val CyanPulse = Color(0xFF00F5D4)
val ElectricBlue = Color(0xFF00B4D8)
val NeonPurple = Color(0xFFA855F7)
val DeepViolet = Color(0xFF7C3AED)
val AmberAlert = Color(0xFFF59E0B)
val RedAlert = Color(0xFFEF4444)
val EmeraldSafe = Color(0xFF10B981)

val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextTertiary = Color(0xFF64748B)

// Preserved aliases for existing references
val VoidBlack = Color(0xFF050811)
val DeepObsidian = LiquidGlassDefaultBackground
val GlassSurfaceDark = LiquidGlassDefaultSurface
val GlassSurfaceTranslucent = LiquidGlassDefaultSurfaceTranslucent
val GlassSurfaceUltraLight = Color(0x33FFFFFF)
val GlassCardBorder = LiquidGlassDefaultBorder
val GlassBorderSubtle = Color(0x1FFFFFFF)
val GlassRimHighlight = Color(0x55FFFFFF)

val PrimaryDark = LiquidGlassDefaultPrimary
val OnPrimaryDark = Color(0xFFFFFFFF)
val SecondaryDark = LiquidGlassDefaultAccent
val OnSecondaryDark = Color(0xFFFFFFFF)
val TertiaryDark = ElectricBlue
val OnTertiaryDark = Color(0xFF00223A)
val BackgroundDark = LiquidGlassDefaultBackground
val OnBackgroundDark = TextPrimary
val SurfaceDark = LiquidGlassDefaultSurface
val OnSurfaceDark = TextPrimary
val SurfaceVariantDark = Color(0xFF131D31)
val OnSurfaceVariantDark = TextSecondary
val OutlineDark = Color(0x4094A3B8)

// Liquid Glass Gradients
object LiquidGradients {
    val glassCardBackdrop = Brush.verticalGradient(
        colors = listOf(
            Color(0xEE121C30),
            Color(0xCC0C1424)
        )
    )

    val glassRimLight = Brush.linearGradient(
        colors = listOf(
            Color(0x4D00F5D4),
            Color(0x1AFFFFFF),
            Color(0x4DA855F7)
        )
    )

    val subtleBorder = Brush.linearGradient(
        colors = listOf(
            Color(0x33FFFFFF),
            Color(0x0DFFFFFF),
            Color(0x264FD1C5)
        )
    )

    val cyanToViolet = Brush.horizontalGradient(
        colors = listOf(CyanPulse, ElectricBlue, NeonPurple)
    )
}
