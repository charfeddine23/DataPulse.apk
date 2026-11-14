package com.example.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Dedicated color token system for the Tactile Neumorphism (Soft UI) theme.
 *
 * Uses a soft ice-gray canvas (#EEF2F6) paired with dual highlight (#FFFFFF) and
 * cool ambient shadow (#D1D9E6) tokens to sculpt physical extruded and pressed depth.
 */
object NeumorphicColors {
    val Background = Color(0xFFEEF2F6)
    val Surface = Color(0xFFEEF2F6)
    val SurfaceSubtle = Color(0xFFE4EAF1)
    val Highlight = Color(0xFFFFFFFF)
    val DarkShadow = Color(0xFFD1D9E6)

    val TextPrimary = Color(0xFF1E293B)
    val TextSecondary = Color(0xFF64748B)
    val TextTertiary = Color(0xFF94A3B8)

    val AccentBlue = Color(0xFF2563EB)
    val AccentMint = Color(0xFF10B981)
    val AccentRed = Color(0xFFEF4444)

    val BorderLight = Color(0xD9FFFFFF)
    val BorderDark = Color(0x99D1D9E6)
}
