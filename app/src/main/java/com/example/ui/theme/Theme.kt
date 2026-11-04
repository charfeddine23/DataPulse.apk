package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.model.ThemeMode

@Immutable
data class LiquidGlassStyle(
    val surfaceColor: Color,
    val surfaceTranslucent: Color,
    val borderColor: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val accentPrimary: Color,
    val accentSecondary: Color,
    val cardBackground: Color,
    val cardSubtleBg: Color,
    val rimLightBrush: Brush,
    val backdropBrush: Brush,
    val blurRadius: Dp = 16.dp,
    val isDark: Boolean = true
)

val LocalLiquidGlass = staticCompositionLocalOf {
    LiquidGlassStyle(
        surfaceColor = GlassSurfaceDark,
        surfaceTranslucent = GlassSurfaceTranslucent,
        borderColor = GlassCardBorder,
        textPrimary = TextPrimary,
        textSecondary = TextSecondary,
        textTertiary = TextTertiary,
        accentPrimary = CyanPulse,
        accentSecondary = NeonPurple,
        cardBackground = Color(0xFF0F1728),
        cardSubtleBg = Color(0xFF0B111E),
        rimLightBrush = LiquidGradients.glassRimLight,
        backdropBrush = LiquidGradients.glassCardBackdrop,
        blurRadius = 16.dp,
        isDark = true
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = Color(0xFF0F3D37),
    onPrimaryContainer = CyanPulse,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = Color(0xFF3B1F54),
    onSecondaryContainer = NeonPurple,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    outline = OutlineDark,
    error = RedAlert,
    onError = TextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0D9488),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCCFBF1),
    onPrimaryContainer = Color(0xFF115E59),
    secondary = Color(0xFF7C3AED),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9FE),
    onSecondaryContainer = Color(0xFF4C1D95),
    tertiary = Color(0xFF0284C7),
    onTertiary = Color.White,
    background = Color(0xFFF1F5F9), // Soft, eye-pleasing slate background
    onBackground = Color(0xFF0F172A), // High contrast dark slate text
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF8FAFC),
    onSurfaceVariant = Color(0xFF334155),
    outline = Color(0xFFCBD5E1),
    error = RedAlert,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    themeMode: ThemeMode = ThemeMode.DARK, // Explicit Dark by default; only follows phone if user sets SYSTEM
    appLanguage: com.example.model.AppLanguage = com.example.model.AppLanguage.ARABIC,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemInDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    val liquidGlassStyle = if (isDark) {
        LiquidGlassStyle(
            surfaceColor = GlassSurfaceDark,
            surfaceTranslucent = GlassSurfaceTranslucent,
            borderColor = GlassCardBorder,
            textPrimary = Color(0xFFF8FAFC),
            textSecondary = Color(0xFF94A3B8),
            textTertiary = Color(0xFF64748B),
            accentPrimary = CyanPulse,
            accentSecondary = NeonPurple,
            cardBackground = Color(0xFF0F1728),
            cardSubtleBg = Color(0xFF0B111E),
            rimLightBrush = LiquidGradients.glassRimLight,
            backdropBrush = LiquidGradients.glassCardBackdrop,
            blurRadius = 16.dp,
            isDark = true
        )
    } else {
        // High-contrast, elegant, eye-pleasing Light Mode Liquid Glass
        LiquidGlassStyle(
            surfaceColor = Color(0xFFFFFFFF),
            surfaceTranslucent = Color(0xFAF8FAFC),
            borderColor = Color(0xFFCBD5E1),
            textPrimary = Color(0xFF0F172A),
            textSecondary = Color(0xFF334155),
            textTertiary = Color(0xFF64748B),
            accentPrimary = Color(0xFF0D9488),
            accentSecondary = Color(0xFF7C3AED),
            cardBackground = Color(0xFFFFFFFF),
            cardSubtleBg = Color(0xFFF1F5F9),
            rimLightBrush = Brush.linearGradient(
                listOf(Color(0x4D0D9488), Color(0x33CBD5E1), Color(0x4D7C3AED))
            ),
            backdropBrush = Brush.verticalGradient(
                listOf(Color(0xFFFFFFFF), Color(0xFFF8FAFC))
            ),
            blurRadius = 16.dp,
            isDark = false
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = if (isDark) DeepObsidian.toArgb() else Color(0xFFF1F5F9).toArgb()
                window.navigationBarColor = if (isDark) VoidBlack.toArgb() else Color(0xFFE2E8F0).toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    // Enforce LTR layout direction to prevent layout reflection when using Arabic
    CompositionLocalProvider(
        LocalLiquidGlass provides liquidGlassStyle,
        LocalLayoutDirection provides LayoutDirection.Ltr
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
