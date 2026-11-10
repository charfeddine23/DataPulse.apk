package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// =========================================================================
// 0. LIGHT_MODE Palette (Luminous Crisp Daylight Glass)
// Background: Clean luminous daylight slate-50 (#F8FAFC)
// Surface: Crystalline pure white (#FFFFFF)
// SurfaceTranslucent: Frosted white glass (#F2FFFFFF)
// Primary: High-vibrancy Royal Blue (#0066FF)
// Accent: Vibrant Electric Violet (#7C3AED)
// Border: Refined crystalline glass rim (#1F0F172A)
// CardBg: Pure White (#FFFFFF)
// CardSubtleBg: Soft Pearl / Slate 100 (#F1F5F9)
// =========================================================================
val LightModeBackground = Color(0xFFF8FAFC)
val LightModeSurface = Color(0xFFFFFFFF)
val LightModeSurfaceTranslucent = Color(0xF2FFFFFF)
val LightModePrimary = Color(0xFF0066FF)
val LightModeAccent = Color(0xFF7C3AED)
val LightModeBorder = Color(0x1F0F172A)
val LightModeCardBg = Color(0xFFFFFFFF)
val LightModeCardSubtleBg = Color(0xFFF1F5F9)

val LightModeTextPrimary = Color(0xFF0F172A)
val LightModeTextSecondary = Color(0xFF475569)
val LightModeTextTertiary = Color(0xFF94A3B8)

// =========================================================================
// 1. NEUMORPHIC_DARK_DEFAULT Palette (Base #0F1423)
// Background: Sleek dark neumorphic slate (#0F1423)
// Surface: Soft tactile dark surface (#141C2E)
// Primary: Vibrant Blue (#0066FF)
// Accent: Violet (#7C4DFF)
// Highlights & Shadows: Dual shadows for soft extruded / indented depth
// =========================================================================
val NeumorphicSlateBase = Color(0xFF0F1423)
val NeumorphicSlateSurface = Color(0xFF141C2E)
val NeumorphicSlateSurfaceSubtle = Color(0xFF101726)
val NeumorphicSlateHighlight = Color(0x384E699A) // Soft top-left light highlight
val NeumorphicSlateShadow = Color(0xE603050B)    // Deep bottom-right ambient shadow
val NeumorphicBorderHighlight = Color(0x2BFFFFFF)
val NeumorphicBorderShadow = Color(0x66000000)

val LiquidGlassDefaultBackground = NeumorphicSlateBase
val LiquidGlassDefaultSurface = NeumorphicSlateSurface
val LiquidGlassDefaultSurfaceTranslucent = Color(0xD9141C2E)
val LiquidGlassDefaultPrimary = Color(0xFF0066FF)
val LiquidGlassDefaultAccent = Color(0xFF7C4DFF)
val LiquidGlassDefaultBorder = Color(0x2B4A6A9E)
val LiquidGlassDefaultCardBg = NeumorphicSlateSurface
val LiquidGlassDefaultCardSubtleBg = NeumorphicSlateSurfaceSubtle

// =========================================================================
// 2. LIQUID_NEON_CYAN Palette (Neumorphic Dark Cyan)
// Background: Deep dark slate background (#080D1A)
// Surface: Soft dark slate surface (#0D1527)
// Primary: Bright cyan (#00E5FF)
// Accent: Neon green / teal (#00FFA3)
// =========================================================================
val LiquidNeonCyanBackground = Color(0xFF080D1A)
val LiquidNeonCyanSurface = Color(0xFF0D1527)
val LiquidNeonCyanSurfaceTranslucent = Color(0xD90D1527)
val LiquidNeonCyanPrimary = Color(0xFF00E5FF)
val LiquidNeonCyanAccent = Color(0xFF00FFA3)
val LiquidNeonCyanBorder = Color(0x3300E5FF)
val LiquidNeonCyanCardBg = Color(0xFF0D1527)
val LiquidNeonCyanCardSubtleBg = Color(0xFF080D19)

// =========================================================================
// 3. AMOLED_PITCH_BLACK Palette (Neumorphic OLED Soft Dark)
// Background: Pure pitch-black (#000000) for OLED battery saving
// Surface: Soft tactile deep dark surface (#111116)
// Primary: Electric blue (#3B82F6)
// Accent: Crisp Cyan (#06B6D4)
// =========================================================================
val AmoledPitchBlackBackground = Color(0xFF000000)
val AmoledPitchBlackSurface = Color(0xFF111116)
val AmoledPitchBlackSurfaceTranslucent = Color(0xD9111116)
val AmoledPitchBlackPrimary = Color(0xFF3B82F6)
val AmoledPitchBlackAccent = Color(0xFF06B6D4)
val AmoledPitchBlackBorder = Color(0x2E3B82F6)
val AmoledPitchBlackCardBg = Color(0xFF111116)
val AmoledPitchBlackCardSubtleBg = Color(0xFF0A0A0E)

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
