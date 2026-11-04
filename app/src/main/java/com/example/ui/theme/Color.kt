package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Liquid Glass Palette
val VoidBlack = Color(0xFF050811)
val DeepObsidian = Color(0xFF0B0F19) // Deep dark background tone (#0B0F19)
val GlassSurfaceDark = Color(0xD90E1628)      // ~85% opacity
val GlassSurfaceTranslucent = Color(0x99131F37) // ~60% opacity
val GlassSurfaceUltraLight = Color(0x33FFFFFF)  // Frosted sheen
val GlassCardBorder = Color(0x334FD1C5)
val GlassBorderSubtle = Color(0x1FFFFFFF)
val GlassRimHighlight = Color(0x55FFFFFF)

// Accents & Neons
val CyanPulse = Color(0xFF00F5D4)
val ElectricBlue = Color(0xFF00B4D8)
val NeonPurple = Color(0xFFA855F7)
val DeepViolet = Color(0xFF7C3AED)
val AmberAlert = Color(0xFFF59E0B)
val RedAlert = Color(0xFFEF4444)
val EmeraldSafe = Color(0xFF10B981)

// Text & Content
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextTertiary = Color(0xFF64748B)

// M3 Color Scheme bindings
val PrimaryDark = CyanPulse
val OnPrimaryDark = Color(0xFF00382E)
val SecondaryDark = NeonPurple
val OnSecondaryDark = Color(0xFF2E004E)
val TertiaryDark = ElectricBlue
val OnTertiaryDark = Color(0xFF00223A)
val BackgroundDark = DeepObsidian
val OnBackgroundDark = TextPrimary
val SurfaceDark = GlassSurfaceDark
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
