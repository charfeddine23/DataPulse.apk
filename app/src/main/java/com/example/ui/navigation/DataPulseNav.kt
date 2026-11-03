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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SpeedMeterScreen
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextTertiary

enum class NavDestination(val label: String, val icon: ImageVector, val tag: String) {
    DASHBOARD("Pulse", Icons.Default.GraphicEq, "nav_dashboard"),
    ANALYTICS("Analytics", Icons.Default.Equalizer, "nav_analytics"),
    SPEED("Speed Meter", Icons.Default.Speed, "nav_speed"),
    SETTINGS("Settings", Icons.Default.Settings, "nav_settings")
}

@Composable
fun DataPulseApp(
    viewModel: MainViewModel,
    onRequestUsagePermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentDestination by remember { mutableStateOf(NavDestination.DASHBOARD) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF090D16),
        bottomBar = {
            LiquidGlassBottomNav(
                currentDestination = currentDestination,
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
                        onNavigateToSettings = { currentDestination = NavDestination.SETTINGS },
                        onNavigateToAnalytics = { currentDestination = NavDestination.ANALYTICS }
                    )
                    NavDestination.ANALYTICS -> AnalyticsScreen(
                        viewModel = viewModel,
                        onRequestUsagePermission = onRequestUsagePermission
                    )
                    NavDestination.SPEED -> SpeedMeterScreen(
                        viewModel = viewModel
                    )
                    NavDestination.SETTINGS -> SettingsScreen(
                        viewModel = viewModel,
                        onRequestUsagePermission = onRequestUsagePermission
                    )
                }
            }
        }
    }
}

@Composable
fun LiquidGlassBottomNav(
    currentDestination: NavDestination,
    onSelectDestination: (NavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xEE111A2C),
                        Color(0xF50D1422)
                    )
                )
            )
            .border(
                1.dp,
                Brush.linearGradient(
                    colors = listOf(
                        CyanPulse.copy(alpha = 0.35f),
                        Color(0x15FFFFFF),
                        CyanPulse.copy(alpha = 0.2f)
                    )
                ),
                RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavDestination.values().forEach { destination ->
                val isSelected = (destination == currentDestination)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) CyanPulse.copy(alpha = 0.15f) else Color.Transparent)
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
                            contentDescription = destination.label,
                            tint = if (isSelected) CyanPulse else TextTertiary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = destination.label,
                            color = if (isSelected) TextPrimary else TextTertiary,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
