package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.FormatUtils
import com.example.model.SimCardInfo
import com.example.ui.MainViewModel
import com.example.ui.components.CircularQuotaProgressGauge
import com.example.ui.components.DashboardHeader
import com.example.ui.components.GlassContainer
import com.example.ui.components.LiveSpeedCard
import com.example.ui.components.QuotaProgressGauge
import com.example.ui.components.UsagePermissionCard
import com.example.ui.components.neumorphicDepth
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onRequestUsagePermission: () -> Unit,
    onOpenAppInfo: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    modifier: Modifier = Modifier
) {
    val speedSample by viewModel.speedSample.collectAsStateWithLifecycle()
    val speedHistory by viewModel.speedHistory.collectAsStateWithLifecycle()
    val usageSummary by viewModel.usageSummary.collectAsStateWithLifecycle()
    val quota by viewModel.dataPlanQuota.collectAsStateWithLifecycle()
    val simCards by viewModel.simCards.collectAsStateWithLifecycle()
    val selectedSimIndex by viewModel.selectedSimIndex.collectAsStateWithLifecycle()
    val hasUsagePerm by viewModel.hasUsagePermission.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Branding Header with external app icon and clean title
        item {
            DashboardHeader(speedSample = speedSample, appLanguage = appLanguage)
        }

        // Usage Permission Warning if not granted
        if (!hasUsagePerm) {
            item {
                UsagePermissionCard(
                    onRequestPermission = onRequestUsagePermission,
                    onOpenAppInfo = onOpenAppInfo,
                    appLanguage = appLanguage
                )
            }
        }

        // Dual SIM Selector / Status
        if (simCards.isNotEmpty()) {
            item {
                DualSimSelector(
                    simCards = simCards,
                    selectedIndex = selectedSimIndex,
                    onSelect = { viewModel.setSimIndex(it) }
                )
            }
        }

        // Monthly Quota Circular Progress Gauge with Heartbeat & Dynamic Live Colors
        item {
            val limitBytes = (quota.planLimitGB * 1024L * 1024L * 1024L).toLong()
            val isInternetConnected = (speedSample.connectionType != com.example.model.NetworkType.OFFLINE)
            CircularQuotaProgressGauge(
                usedBytes = quota.usedBytes,
                limitBytes = limitBytes,
                percentage = quota.usagePercent,
                status = quota.status,
                daysRemaining = quota.daysRemaining,
                appLanguage = appLanguage,
                isInternetConnected = isInternetConnected,
                onConfigureClick = onNavigateToSettings
            )
        }

        // 3. Quick Summaries for Data Usage (Breakdown Grid)
        item {
            val glassStyle = com.example.ui.theme.LocalLiquidGlass.current
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = com.example.model.AppStrings.dataBreakdownTitle(appLanguage),
                    color = glassStyle.textSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                IconButton(
                    onClick = { viewModel.refreshAll() },
                    modifier = Modifier.size(28.dp).testTag("refresh_dashboard_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = com.example.model.AppStrings.refresh(appLanguage),
                        tint = glassStyle.accentPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            DataBreakdownGrid(
                usageSummary = usageSummary,
                appLanguage = appLanguage,
                onViewFullAnalytics = onNavigateToAnalytics
            )
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun DualSimSelector(
    simCards: List<SimCardInfo>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current
    val trayShape = RoundedCornerShape(18.dp)

    // Indented neumorphic tray
    Box(
        modifier = modifier
            .fillMaxWidth()
            .neumorphicDepth(
                shape = trayShape,
                lightColor = Color.Transparent,
                darkColor = glassStyle.neumorphicShadow,
                offsetDp = 3.dp,
                isPressed = true
            )
            .clip(trayShape)
            .background(
                androidx.compose.ui.graphics.Brush.linearGradient(
                    listOf(glassStyle.cardSubtleBg, glassStyle.neumorphicSurface)
                )
            )
            .border(1.dp, glassStyle.neumorphicBorderDark, trayShape)
            .padding(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            simCards.forEachIndexed { index, sim ->
                val isSelected = (index == selectedIndex)
                val itemShape = RoundedCornerShape(14.dp)

                val itemScale by androidx.compose.animation.core.animateFloatAsState(
                    targetValue = if (isSelected) 0.98f else 1.0f,
                    animationSpec = androidx.compose.animation.core.tween(100),
                    label = "sim_scale"
                )

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .scale(itemScale)
                        .neumorphicDepth(
                            shape = itemShape,
                            lightColor = if (isSelected) Color.Transparent else glassStyle.neumorphicHighlight.copy(alpha = 0.4f),
                            darkColor = glassStyle.neumorphicShadow,
                            offsetDp = 2.dp,
                            isPressed = isSelected
                        )
                        .clip(itemShape)
                        .background(
                            if (isSelected) {
                                androidx.compose.ui.graphics.Brush.linearGradient(
                                    listOf(
                                        glassStyle.cardSubtleBg,
                                        glassStyle.accentPrimary.copy(alpha = 0.2f)
                                    )
                                )
                            } else {
                                androidx.compose.ui.graphics.Brush.linearGradient(
                                    listOf(
                                        glassStyle.neumorphicSurface,
                                        glassStyle.neumorphicBase
                                    )
                                )
                            }
                        )
                        .border(
                            1.dp,
                            if (isSelected) {
                                androidx.compose.ui.graphics.Brush.linearGradient(
                                    listOf(glassStyle.accentPrimary.copy(alpha = 0.7f), glassStyle.neumorphicBorderDark)
                                )
                            } else {
                                androidx.compose.ui.graphics.Brush.linearGradient(
                                    listOf(glassStyle.neumorphicBorderLight.copy(alpha = 0.2f), glassStyle.neumorphicBorderDark)
                                )
                            },
                            itemShape
                        )
                        .clickable { onSelect(index) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SimCard,
                        contentDescription = sim.displayName,
                        tint = if (isSelected) glassStyle.accentPrimary else glassStyle.textTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = sim.displayName,
                            color = if (isSelected) glassStyle.accentPrimary else glassStyle.textSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        Text(
                            text = sim.carrierName,
                            color = if (isSelected) glassStyle.textPrimary else glassStyle.textTertiary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DataBreakdownGrid(
    usageSummary: com.example.model.NetworkUsageSummary,
    appLanguage: com.example.model.AppLanguage,
    onViewFullAnalytics: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BreakdownMetricCard(
                title = com.example.model.AppStrings.todayCellular(appLanguage),
                value = FormatUtils.formatBytes(usageSummary.mobileTodayBytes),
                icon = Icons.Default.SignalCellularAlt,
                tint = glassStyle.accentPrimary,
                modifier = Modifier.weight(1f)
            )
            BreakdownMetricCard(
                title = com.example.model.AppStrings.todayWifi(appLanguage),
                value = FormatUtils.formatBytes(usageSummary.wifiTodayBytes),
                icon = Icons.Default.Wifi,
                tint = ElectricBlue,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BreakdownMetricCard(
                title = com.example.model.AppStrings.monthlyCellular(appLanguage),
                value = FormatUtils.formatBytes(usageSummary.mobileMonthBytes),
                icon = Icons.Default.CellTower,
                tint = glassStyle.accentSecondary,
                modifier = Modifier.weight(1f)
            )
            BreakdownMetricCard(
                title = com.example.model.AppStrings.monthlyWifi(appLanguage),
                value = FormatUtils.formatBytes(usageSummary.wifiMonthBytes),
                icon = Icons.Default.TrendingUp,
                tint = glassStyle.accentPrimary,
                modifier = Modifier.weight(1f)
            )
        }

        // View App-by-App Analytics Button Card
        GlassContainer(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onViewFullAnalytics() },
            borderColor = glassStyle.accentPrimary.copy(alpha = 0.35f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(glassStyle.accentPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = com.example.model.AppStrings.viewAppAnalytics(appLanguage),
                            tint = glassStyle.accentPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = com.example.model.AppStrings.viewAppAnalytics(appLanguage),
                            color = glassStyle.textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = com.example.model.AppStrings.viewAppAnalyticsSub(appLanguage),
                            color = glassStyle.textSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
                Text(
                    text = com.example.model.AppStrings.explore(appLanguage),
                    color = glassStyle.accentPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun BreakdownMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current
    val cardShape = RoundedCornerShape(18.dp)

    Box(
        modifier = modifier
            .neumorphicDepth(
                shape = cardShape,
                lightColor = glassStyle.neumorphicHighlight.copy(alpha = 0.5f),
                darkColor = glassStyle.neumorphicShadow,
                offsetDp = 3.dp
            )
            .clip(cardShape)
            .background(
                androidx.compose.ui.graphics.Brush.linearGradient(
                    listOf(glassStyle.neumorphicSurface, glassStyle.neumorphicBase)
                )
            )
            .border(
                1.dp,
                androidx.compose.ui.graphics.Brush.linearGradient(
                    listOf(glassStyle.neumorphicBorderLight.copy(alpha = 0.3f), tint.copy(alpha = 0.35f))
                ),
                cardShape
            )
            .padding(14.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = tint,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    color = glassStyle.textSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = glassStyle.textPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
