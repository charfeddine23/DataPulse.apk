package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
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
import com.example.model.AppLanguage
import com.example.model.AppThemeMode
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
    val isDark: Boolean = true,
    val neumorphicBase: Color = Color(0xFF0F1423),
    val neumorphicSurface: Color = Color(0xFF141C2E),
    val neumorphicHighlight: Color = Color(0x384E699A),
    val neumorphicShadow: Color = Color(0xE603050B),
    val neumorphicBorderLight: Color = Color(0x2BFFFFFF),
    val neumorphicBorderDark: Color = Color(0x66000000)
)

val LocalLiquidGlass = staticCompositionLocalOf {
    createLiquidGlassStyle(AppThemeMode.LIQUID_GLASS_DEFAULT)
}

/**
 * Creates Material 3 darkColorScheme corresponding to each AppThemeMode
 */
fun createAppColorScheme(mode: AppThemeMode): ColorScheme = when (mode) {
    AppThemeMode.LIQUID_GLASS_DEFAULT -> darkColorScheme(
        primary = LiquidGlassDefaultPrimary,
        onPrimary = Color.White,
        primaryContainer = Color(0xFF003080),
        onPrimaryContainer = Color(0xFFCCE0FF),
        secondary = LiquidGlassDefaultAccent,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFF381480),
        onSecondaryContainer = Color(0xFFEADBFF),
        tertiary = ElectricBlue,
        onTertiary = Color.White,
        background = LiquidGlassDefaultBackground,
        onBackground = TextPrimary,
        surface = LiquidGlassDefaultSurface,
        onSurface = TextPrimary,
        surfaceVariant = Color(0xFF131D31),
        onSurfaceVariant = TextSecondary,
        outline = Color(0x4094A3B8),
        error = RedAlert,
        onError = TextPrimary
    )

    AppThemeMode.LIQUID_NEON_CYAN -> darkColorScheme(
        primary = LiquidNeonCyanPrimary,
        onPrimary = Color(0xFF00363D),
        primaryContainer = Color(0xFF004F59),
        onPrimaryContainer = LiquidNeonCyanPrimary,
        secondary = LiquidNeonCyanAccent,
        onSecondary = Color(0xFF003920),
        secondaryContainer = Color(0xFF005330),
        onSecondaryContainer = LiquidNeonCyanAccent,
        tertiary = CyanPulse,
        onTertiary = Color(0xFF00382E),
        background = LiquidNeonCyanBackground,
        onBackground = TextPrimary,
        surface = LiquidNeonCyanSurface,
        onSurface = TextPrimary,
        surfaceVariant = Color(0xFF0E1A2F),
        onSurfaceVariant = TextSecondary,
        outline = Color(0x4000E5FF),
        error = RedAlert,
        onError = TextPrimary
    )

    AppThemeMode.AMOLED_PITCH_BLACK -> darkColorScheme(
        primary = AmoledPitchBlackPrimary,
        onPrimary = Color.White,
        primaryContainer = Color(0xFF1E3A8A),
        onPrimaryContainer = Color(0xFFDBEAFE),
        secondary = AmoledPitchBlackAccent,
        onSecondary = Color(0xFF083344),
        secondaryContainer = Color(0xFF155E75),
        onSecondaryContainer = Color(0xFFCFFAFE),
        tertiary = ElectricBlue,
        onTertiary = Color.White,
        background = AmoledPitchBlackBackground,
        onBackground = TextPrimary,
        surface = AmoledPitchBlackSurface,
        onSurface = TextPrimary,
        surfaceVariant = Color(0xFF1A1A1A),
        onSurfaceVariant = TextSecondary,
        outline = Color(0x33FFFFFF),
        error = RedAlert,
        onError = TextPrimary
    )
}

/**
 * Creates custom LiquidGlassStyle tokens corresponding to each AppThemeMode
 */
fun createLiquidGlassStyle(mode: AppThemeMode): LiquidGlassStyle = when (mode) {
    AppThemeMode.LIQUID_GLASS_DEFAULT -> LiquidGlassStyle(
        surfaceColor = LiquidGlassDefaultSurface,
        surfaceTranslucent = LiquidGlassDefaultSurfaceTranslucent,
        borderColor = LiquidGlassDefaultBorder,
        textPrimary = TextPrimary,
        textSecondary = TextSecondary,
        textTertiary = TextTertiary,
        accentPrimary = LiquidGlassDefaultPrimary,
        accentSecondary = LiquidGlassDefaultAccent,
        cardBackground = LiquidGlassDefaultCardBg,
        cardSubtleBg = LiquidGlassDefaultCardSubtleBg,
        rimLightBrush = Brush.linearGradient(
            listOf(Color(0x4D0066FF), Color(0x1AFFFFFF), Color(0x4D7C4DFF))
        ),
        backdropBrush = Brush.verticalGradient(
            listOf(Color(0xFF141C2E), Color(0xFF0F1423))
        ),
        blurRadius = 16.dp,
        isDark = true,
        neumorphicBase = NeumorphicSlateBase,
        neumorphicSurface = NeumorphicSlateSurface,
        neumorphicHighlight = NeumorphicSlateHighlight,
        neumorphicShadow = NeumorphicSlateShadow,
        neumorphicBorderLight = NeumorphicBorderHighlight,
        neumorphicBorderDark = NeumorphicBorderShadow
    )

    AppThemeMode.LIQUID_NEON_CYAN -> LiquidGlassStyle(
        surfaceColor = LiquidNeonCyanSurface,
        surfaceTranslucent = LiquidNeonCyanSurfaceTranslucent,
        borderColor = LiquidNeonCyanBorder,
        textPrimary = TextPrimary,
        textSecondary = TextSecondary,
        textTertiary = TextTertiary,
        accentPrimary = LiquidNeonCyanPrimary,
        accentSecondary = LiquidNeonCyanAccent,
        cardBackground = LiquidNeonCyanCardBg,
        cardSubtleBg = LiquidNeonCyanCardSubtleBg,
        rimLightBrush = Brush.linearGradient(
            listOf(Color(0x4D00E5FF), Color(0x1A00FFA3), Color(0x4D00E5FF))
        ),
        backdropBrush = Brush.verticalGradient(
            listOf(Color(0xFF0D1527), Color(0xFF080D1A))
        ),
        blurRadius = 16.dp,
        isDark = true,
        neumorphicBase = LiquidNeonCyanBackground,
        neumorphicSurface = LiquidNeonCyanSurface,
        neumorphicHighlight = Color(0x3300E5FF),
        neumorphicShadow = Color(0xCC03060C),
        neumorphicBorderLight = Color(0x2E00E5FF),
        neumorphicBorderDark = Color(0x66000000)
    )

    AppThemeMode.AMOLED_PITCH_BLACK -> LiquidGlassStyle(
        surfaceColor = AmoledPitchBlackSurface,
        surfaceTranslucent = AmoledPitchBlackSurfaceTranslucent,
        borderColor = AmoledPitchBlackBorder,
        textPrimary = TextPrimary,
        textSecondary = TextSecondary,
        textTertiary = TextTertiary,
        accentPrimary = AmoledPitchBlackPrimary,
        accentSecondary = AmoledPitchBlackAccent,
        cardBackground = AmoledPitchBlackCardBg,
        cardSubtleBg = AmoledPitchBlackCardSubtleBg,
        rimLightBrush = Brush.linearGradient(
            listOf(Color(0x4D3B82F6), Color(0x1AFFFFFF), Color(0x4D06B6D4))
        ),
        backdropBrush = Brush.verticalGradient(
            listOf(Color(0xFF111116), Color(0xFF000000))
        ),
        blurRadius = 8.dp,
        isDark = true,
        neumorphicBase = AmoledPitchBlackBackground,
        neumorphicSurface = AmoledPitchBlackSurface,
        neumorphicHighlight = Color(0x263B82F6),
        neumorphicShadow = Color(0xE6000000),
        neumorphicBorderLight = Color(0x1FFFFFFF),
        neumorphicBorderDark = Color(0x80000000)
    )
}

/**
 * Production-ready Theme Wrapper Composable that dynamically applies the selected AppThemeMode.
 */
@Composable
fun DataPulseTheme(
    appThemeMode: AppThemeMode = AppThemeMode.LIQUID_GLASS_DEFAULT,
    content: @Composable () -> Unit
) {
    val colorScheme = createAppColorScheme(appThemeMode)
    val liquidGlassStyle = createLiquidGlassStyle(appThemeMode)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

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

/**
 * Backward compatibility wrapper for existing MyApplicationTheme calls
 */
@Composable
fun MyApplicationTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    appThemeMode: AppThemeMode = AppThemeMode.LIQUID_GLASS_DEFAULT,
    appLanguage: AppLanguage = AppLanguage.ARABIC,
    content: @Composable () -> Unit
) {
    DataPulseTheme(
        appThemeMode = appThemeMode,
        content = content
    )
}
