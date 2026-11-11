package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// =========================================================================
// 0. LUXURIOUS PEARL & PLATINUM LIGHT PALETTE (Flagship Luxury Aesthetic)
// Background: Soft, warm pearl/platinum off-white (#F4F6F9 to #E9ECEF)
// Surface: Crystalline pure white (#FFFFFF)
// SurfaceTranslucent: Frosted white glass (#F8FFFFFF with ultra-fine blur)
// Primary: High-end Royal Electric Azure (#0066FF)
// Accent: Refined Royal Violet (#7C3AED)
// Border: Ultra-fine frosted glass rim (#14111827 ~ 8% opacity)
// BorderSubtle: Soft ambient border (#0A111827 ~ 4% opacity)
// CardBg: Pure White (#FFFFFF)
// CardSubtleBg: Soft Pearl / Platinum (#F0F3F8)
//
// Typography: Razor-sharp high contrast without eye strain
// Primary: Deep obsidian/charcoal (#111827)
// Secondary: Refined slate (#4B5563)
// Tertiary: Muted neutral slate (#6B7280)
//
// Accents & Pulse Glows (Refined for light surfaces):
// Ocean Cyan: #0284C7 (Crisp and vivid on light backgrounds)
// Amber Alert: #D97706 (Amber 600, warm and saturated)
// Orange Accent: #EA580C (Orange 600)
// Coral Red: #DC2626 (Red 600)
// Crimson Limit: #991B1B (Deep rich ruby red)
// =========================================================================
val PearlPlatinumLightBase = Color(0xFFF4F6F9)
val PearlPlatinumLightEnd = Color(0xFFE9ECEF)
val PearlPlatinumSurface = Color(0xFFFFFFFF)
val PearlPlatinumSurfaceTranslucent = Color(0xF8FFFFFF)
val PearlPlatinumPrimary = Color(0xFF0066FF)
val PearlPlatinumAccent = Color(0xFF7C3AED)
val PearlPlatinumBorder = Color(0x14111827)        // Ultra-fine frosted border (~8%)
val PearlPlatinumBorderSubtle = Color(0x0A111827)  // Soft ambient border (~4%)
val PearlPlatinumCardBg = Color(0xFFFFFFFF)
val PearlPlatinumCardSubtleBg = Color(0xFFF0F3F8)

val PearlPlatinumTextPrimary = Color(0xFF111827)   // Deep obsidian/charcoal #111827
val PearlPlatinumTextSecondary = Color(0xFF4B5563) // Refined slate #4B5563
val PearlPlatinumTextTertiary = Color(0xFF6B7280)  // Muted neutral slate #6B7280

// Light Mode Pulse Accents (Refined contrast for light surfaces)
val LightPulseOceanCyan = Color(0xFF0284C7)
val LightPulseTeal = Color(0xFF0D9488)
val LightPulseAmber = Color(0xFFD97706)
val LightPulseOrange = Color(0xFFEA580C)
val LightPulseCoralRed = Color(0xFFDC2626)
val LightPulseCrimson = Color(0xFF991B1B)
val LightPulseIceSlate = Color(0xFF64748B)

// Aliases mapped to the new Pearl & Platinum tokens
val LightModeBackground = PearlPlatinumLightBase
val LightModeSurface = PearlPlatinumSurface
val LightModeSurfaceTranslucent = PearlPlatinumSurfaceTranslucent
val LightModePrimary = PearlPlatinumPrimary
val LightModeAccent = PearlPlatinumAccent
val LightModeBorder = PearlPlatinumBorder
val LightModeCardBg = PearlPlatinumCardBg
val LightModeCardSubtleBg = PearlPlatinumCardSubtleBg
val LightModeTextPrimary = PearlPlatinumTextPrimary
val LightModeTextSecondary = PearlPlatinumTextSecondary
val LightModeTextTertiary = PearlPlatinumTextTertiary

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
