package com.example.ui.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SpeedMeterScreen
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.TextPrimary
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.ui.theme.TextTertiary

enum class NavDestination(val icon: ImageVector, val tag: String) {
    DASHBOARD(Icons.Default.GraphicEq, "nav_dashboard"),
    ANALYTICS(Icons.Default.Equalizer, "nav_analytics"),
    SPEED(Icons.Default.Speed, "nav_speed"),
    SETTINGS(Icons.Default.Settings, "nav_settings")
}

fun NavDestination.getLabel(lang: AppLanguage): String = when (this) {
    NavDestination.DASHBOARD -> AppStrings.navPulse(lang)
    NavDestination.ANALYTICS -> AppStrings.navUsage(lang)
    NavDestination.SPEED -> AppStrings.navSpeed(lang)
    NavDestination.SETTINGS -> AppStrings.navSettings(lang)
}

@Composable
fun DataPulseApp(
    viewModel: MainViewModel,
    onRequestUsagePermission: () -> Unit,
    onOpenAppInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentDestination by remember { mutableStateOf(NavDestination.DASHBOARD) }
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
        bottomBar = {
            LiquidGlassBottomNav(
                currentDestination = currentDestination,
                appLanguage = appLanguage,
                onSelectDestination = { currentDestination = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentDestination, label = "screen_crossfade") { dest ->
                when (dest) {
                    NavDestination.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        onRequestUsagePermission = onRequestUsagePermission,
                        onOpenAppInfo = onOpenAppInfo,
                        onNavigateToSettings = { currentDestination = NavDestination.SETTINGS },
                        onNavigateToAnalytics = { currentDestination = NavDestination.ANALYTICS }
                    )
                    NavDestination.ANALYTICS -> AnalyticsScreen(
                        viewModel = viewModel,
                        onRequestUsagePermission = onRequestUsagePermission,
                        onOpenAppInfo = onOpenAppInfo
                    )
                    NavDestination.SPEED -> SpeedMeterScreen(
                        viewModel = viewModel
                    )
                    NavDestination.SETTINGS -> SettingsScreen(
                        viewModel = viewModel,
                        onRequestUsagePermission = onRequestUsagePermission,
                        onOpenAppInfo = onOpenAppInfo
                    )
                }
            }
        }
    }
}

@Composable
fun LiquidGlassBottomNav(
    currentDestination: NavDestination,
    appLanguage: AppLanguage,
    onSelectDestination: (NavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(brush = glassStyle.backdropBrush)
            .border(
                1.dp,
                glassStyle.rimLightBrush,
                RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sequence displayed on screen (Left to Right):
                // 1. SETTINGS (الاعدادات - أقصى اليسار / Far Left)
                // 2. SPEED (عداد السرعة)
                // 3. ANALYTICS (الاستهلاك / Usage)
                // 4. DASHBOARD (النبض - أقصى اليمين / Far Right)
                val navigationItems = listOf(
                    NavDestination.SETTINGS,
                    NavDestination.SPEED,
                    NavDestination.ANALYTICS,
                    NavDestination.DASHBOARD
                )

            navigationItems.forEach { destination ->
                val isSelected = (destination == currentDestination)
                val destLabel = destination.getLabel(appLanguage)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) glassStyle.accentPrimary.copy(alpha = if (glassStyle.isDark) 0.18f else 0.12f) else Color.Transparent)
                        .clickable { onSelectDestination(destination) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag(destination.tag),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = destLabel,
                            tint = if (isSelected) glassStyle.accentPrimary else glassStyle.textTertiary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = destLabel,
                            color = if (isSelected) glassStyle.textPrimary else glassStyle.textTertiary,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                        }
                    }
                }
            }
        }
    }
}


