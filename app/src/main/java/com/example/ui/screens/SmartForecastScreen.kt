package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.model.ForecastStatus
import com.example.model.FormatUtils
import com.example.model.SmartDataForecast
import com.example.ui.MainViewModel
import com.example.ui.components.ConnectionStatusPill
import com.example.ui.components.GlassContainer
import com.example.ui.components.LiquidGlassHamburgerMenuButton
import com.example.ui.components.SmartDataForecastingCard
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.LocalLiquidGlass
import com.example.ui.theme.RedAlert

/**
 * Dedicated "Smart Analytics & Forecast" screen for Data Pulse.
 * Features the prediction algorithm breakdown, daily burn rate runway,
 * velocity gauge, and sleek Liquid Glass prediction & warning cards.
 */
@Composable
fun SmartForecastScreen(
    viewModel: MainViewModel,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dataForecast by viewModel.dataForecast.collectAsStateWithLifecycle()
    val usageSummary by viewModel.usageSummary.collectAsStateWithLifecycle()
    val speedSample by viewModel.speedSample.collectAsStateWithLifecycle()
    val quota by viewModel.dataPlanQuota.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val glassStyle = LocalLiquidGlass.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Top Header with title and Hamburger Menu Icon on far right
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(glassStyle.accentPrimary.copy(alpha = 0.16f))
                                .border(1.dp, glassStyle.accentPrimary.copy(alpha = 0.45f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Insights,
                                contentDescription = null,
                                tint = glassStyle.accentPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = AppStrings.smartAnalyticsTitle(appLanguage),
                            color = glassStyle.textPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.4.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = AppStrings.smartAnalyticsSubtitle(appLanguage),
                        color = glassStyle.textSecondary,
                        fontSize = 11.sp
                    )
                }

                // Top right actions: Status pill + Hamburger menu icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ConnectionStatusPill(speedSample = speedSample, appLanguage = appLanguage)
                    LiquidGlassHamburgerMenuButton(
                        onClick = onNavigateToSettings,
                        contentDescription = AppStrings.navSettings(appLanguage)
                    )
                }
            }
        }

        // 2. Primary Smart Data Forecasting & Warning Card
        item {
            SmartDataForecastingCard(
                forecast = dataForecast,
                appLanguage = appLanguage,
                onConfigurePlanClick = onNavigateToSettings
            )
        }

        // 3. Billing Cycle Runway & Velocity Meter
        item {
            CycleRunwayCard(
                forecast = dataForecast,
                appLanguage = appLanguage,
                onConfigurePlanClick = onNavigateToSettings
            )
        }

        // 4. Average Daily Consumption Breakdown Card
        item {
            DailyConsumptionBreakdownCard(
                forecast = dataForecast,
                usageSummary = usageSummary,
                appLanguage = appLanguage
            )
        }

        // 5. Smart Data Runway Recommendations Card
        item {
            SmartRunwayTipsCard(
                forecast = dataForecast,
                appLanguage = appLanguage
            )
        }

        // Bottom padding
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * Interactive card comparing actual burn rate against the safe runway budget.
 */
@Composable
private fun CycleRunwayCard(
    forecast: SmartDataForecast,
    appLanguage: AppLanguage,
    onConfigurePlanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassStyle = LocalLiquidGlass.current

    val velocityColor = when {
        forecast.status == ForecastStatus.CRITICAL_DEPLETION || forecast.status == ForecastStatus.EXHAUSTED -> RedAlert
        forecast.willDepleteBeforeCycleEnd -> AmberAlert
        else -> EmeraldSafe
    }

    val velocityText = when {
        forecast.status == ForecastStatus.EXHAUSTED -> AppStrings.statusExhausted(appLanguage)
        forecast.willDepleteBeforeCycleEnd -> "${AppStrings.burnVelocityFast(appLanguage)} (${String.format("%.1fx", forecast.burnRateVelocityRatio)})"
        else -> AppStrings.burnVelocityOptimal(appLanguage)
    }

    GlassContainer(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        backgroundColor = glassStyle.cardBackground,
        borderColor = glassStyle.borderColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timeline,
                        contentDescription = null,
                        tint = glassStyle.accentPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.cycleProgress(appLanguage),
                        color = glassStyle.textPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Velocity Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(velocityColor.copy(alpha = 0.15f))
                        .border(1.dp, velocityColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = velocityText,
                        color = velocityColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Cycle Timeline Progress Track
            val progressAnimated by animateFloatAsState(
                targetValue = forecast.cycleProgressPercent,
                animationSpec = tween(600, easing = FastOutSlowInEasing),
                label = "cycle_progress"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = AppStrings.cycleDaysElapsed(
                        elapsed = forecast.daysElapsedInCycle,
                        total = forecast.totalCycleDays,
                        lang = appLanguage
                    ),
                    color = glassStyle.textSecondary,
                    fontSize = 11.sp
                )
                Text(
                    text = "${forecast.daysRemainingInCycle} ${AppStrings.forecastDays(forecast.daysRemainingInCycle, appLanguage).split(" ").last()}",
                    color = glassStyle.accentPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (glassStyle.isDark) Color(0xFF0F172A) else Color(0xFFE2E8F0))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressAnimated)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(glassStyle.accentSecondary, glassStyle.accentPrimary)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Safe Runway vs Current Burn Rate comparison
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Safe Daily Budget
                RunwaySubMetricBox(
                    label = AppStrings.safeRunwayBudget(appLanguage),
                    value = forecast.recommendedSafeDailyFormatted,
                    subtext = AppStrings.safeRunwayDesc(forecast.daysRemainingInCycle, appLanguage),
                    accentColor = EmeraldSafe,
                    modifier = Modifier.weight(1f)
                )

                // Current Burn Pace
                RunwaySubMetricBox(
                    label = AppStrings.forecastBurnRate(appLanguage),
                    value = forecast.dailyBurnRateFormatted,
                    subtext = if (forecast.willDepleteBeforeCycleEnd) "Exceeds runway" else "Healthy pace",
                    accentColor = if (forecast.willDepleteBeforeCycleEnd) AmberAlert else CyanPulse,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun RunwaySubMetricBox(
    label: String,
    value: String,
    subtext: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val glassStyle = LocalLiquidGlass.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(glassStyle.cardSubtleBg)
            .border(1.dp, glassStyle.borderColor, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = label,
                color = glassStyle.textTertiary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = accentColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                color = glassStyle.textSecondary,
                fontSize = 9.sp,
                maxLines = 1
            )
        }
    }
}

/**
 * Breakdown of consumption metrics: Cellular, Wi-Fi, and today's burn rate.
 */
@Composable
private fun DailyConsumptionBreakdownCard(
    forecast: SmartDataForecast,
    usageSummary: com.example.model.NetworkUsageSummary,
    appLanguage: AppLanguage,
    modifier: Modifier = Modifier
) {
    val glassStyle = LocalLiquidGlass.current

    GlassContainer(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        backgroundColor = glassStyle.cardBackground,
        borderColor = glassStyle.borderColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = AppStrings.dataBreakdownTitle(appLanguage),
                color = glassStyle.textSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BreakdownRowChip(
                    title = AppStrings.todayCellular(appLanguage),
                    value = FormatUtils.formatBytes(usageSummary.mobileTodayBytes),
                    icon = Icons.Default.SignalCellularAlt,
                    tint = glassStyle.accentPrimary,
                    modifier = Modifier.weight(1f)
                )

                BreakdownRowChip(
                    title = AppStrings.todayWifi(appLanguage),
                    value = FormatUtils.formatBytes(usageSummary.wifiTodayBytes),
                    icon = Icons.Default.Wifi,
                    tint = ElectricBlue,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BreakdownRowChip(
                    title = AppStrings.monthlyCellular(appLanguage),
                    value = FormatUtils.formatBytes(usageSummary.mobileMonthBytes),
                    icon = Icons.Default.CellTower,
                    tint = glassStyle.accentSecondary,
                    modifier = Modifier.weight(1f)
                )

                BreakdownRowChip(
                    title = AppStrings.monthlyWifi(appLanguage),
                    value = FormatUtils.formatBytes(usageSummary.wifiMonthBytes),
                    icon = Icons.Default.TrendingUp,
                    tint = CyanPulse,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BreakdownRowChip(
    title: String,
    value: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    val glassStyle = LocalLiquidGlass.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(glassStyle.cardSubtleBg)
            .border(1.dp, glassStyle.borderColor, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = tint,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    color = glassStyle.textTertiary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    color = glassStyle.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Actionable tips card adapting dynamically to user's forecast status.
 */
@Composable
private fun SmartRunwayTipsCard(
    forecast: SmartDataForecast,
    appLanguage: AppLanguage,
    modifier: Modifier = Modifier
) {
    val glassStyle = LocalLiquidGlass.current

    val (tipText, tipColor, tipIcon) = when (forecast.status) {
        ForecastStatus.CRITICAL_DEPLETION, ForecastStatus.EXHAUSTED ->
            Triple(AppStrings.recCritical(appLanguage), RedAlert, Icons.Default.WarningAmber)
        ForecastStatus.EARLY_DEPLETION ->
            Triple(AppStrings.recWarning(appLanguage), AmberAlert, Icons.Default.Speed)
        ForecastStatus.SUSTAINABLE ->
            Triple(AppStrings.recSafe(appLanguage), EmeraldSafe, Icons.Default.AutoAwesome)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(tipColor.copy(alpha = if (glassStyle.isDark) 0.10f else 0.08f))
            .border(1.dp, tipColor.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(tipColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = tipIcon,
                    contentDescription = null,
                    tint = tipColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = AppStrings.recommendationsTitle(appLanguage),
                    color = tipColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = tipText,
                    color = glassStyle.textPrimary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
