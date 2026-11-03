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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.model.ThemeMode

@Immutable
data class LiquidGlassStyle(
    val surfaceColor: Color,
    val surfaceTranslucent: Color,
    val borderColor: Color,
    val rimLightBrush: Brush,
    val backdropBrush: Brush,
    val blurRadius: Dp = 16.dp,
    val glowColor: Color = CyanPulse
)

val LocalLiquidGlass = staticCompositionLocalOf {
    LiquidGlassStyle(
        surfaceColor = GlassSurfaceDark,
        surfaceTranslucent = GlassSurfaceTranslucent,
        borderColor = GlassCardBorder,
        rimLightBrush = LiquidGradients.glassRimLight,
        backdropBrush = LiquidGradients.glassCardBackdrop,
        blurRadius = 16.dp,
        glowColor = CyanPulse
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
    onSurfaceVariant = OnSurfaceVariantDark,
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
    background = Color(0xFFF1F5F9),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    error = RedAlert,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
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
            rimLightBrush = LiquidGradients.glassRimLight,
            backdropBrush = LiquidGradients.glassCardBackdrop,
            blurRadius = 16.dp,
            glowColor = CyanPulse
        )
    } else {
        LiquidGlassStyle(
            surfaceColor = Color(0xF0FFFFFF),
            surfaceTranslucent = Color(0xD0F8FAFC),
            borderColor = Color(0x330D9488),
            rimLightBrush = Brush.linearGradient(
                listOf(Color(0x660D9488), Color(0x33FFFFFF), Color(0x667C3AED))
            ),
            backdropBrush = Brush.verticalGradient(
                listOf(Color(0xFFFFFFFF), Color(0xF2F1F5F9))
            ),
            blurRadius = 16.dp,
            glowColor = Color(0xFF0D9488)
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

    CompositionLocalProvider(LocalLiquidGlass provides liquidGlassStyle) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
