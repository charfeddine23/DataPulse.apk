package com.example.ui.screens

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.ui.components.GlassContainer
import com.example.ui.components.SmartDataForecastingCard
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.LocalLiquidGlass
import com.example.ui.theme.RedAlert

/**
 * Dedicated "Smart Analytics & Forecast" screen for Data Pulse.
 * Features harmonized containers, balanced typography, and symmetrical Liquid Glass cards.
 */
@Composable
fun SmartForecastScreen(
    viewModel: MainViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (onNavigateBack != null) {
        androidx.activity.compose.BackHandler { onNavigateBack() }
    }
    val dataForecast by viewModel.dataForecast.collectAsStateWithLifecycle()
    val quota by viewModel.dataPlanQuota.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val glassStyle = LocalLiquidGlass.current
    val dimensions = com.example.ui.theme.LocalAppDimensions.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensions.screenPaddingHorizontal),
        verticalArrangement = Arrangement.spacedBy(dimensions.sectionSpacing)
    ) {
        // 1. Top Screen Title Header
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(dimensions.headerIconBoxSize)
                            .clip(RoundedCornerShape(12.dp))
                            .background(glassStyle.accentPrimary.copy(alpha = 0.16f))
                            .border(1.dp, glassStyle.accentPrimary.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Insights,
                            contentDescription = null,
                            tint = glassStyle.accentPrimary,
                            modifier = Modifier.size(dimensions.headerIconSize * 0.55f)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = AppStrings.smartAnalyticsTitle(appLanguage),
                        color = glassStyle.textPrimary,
                        fontSize = dimensions.textTitleLarge,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = AppStrings.smartAnalyticsSubtitle(appLanguage),
                    color = glassStyle.textSecondary,
                    fontSize = dimensions.textCaption,
                    lineHeight = 16.sp
                )
            }
        }

        if (dimensions.isTablet) {
            // Adaptive 2-Column Tablet Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimensions.gridSpacing)
                ) {
                    // Left Column (50%): Smart Data Forecasting Card
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(dimensions.gridSpacing)
                    ) {
                        SmartDataForecastingCard(
                            forecast = dataForecast,
                            appLanguage = appLanguage,
                            onConfigurePlanClick = onNavigateToSettings
                        )
                    }

                    // Right Column (50%): Runway Velocity & Tips
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(dimensions.gridSpacing)
                    ) {
                        CycleRunwayCard(
                            forecast = dataForecast,
                            appLanguage = appLanguage
                        )

                        SmartRunwayTipsCard(
                            forecast = dataForecast,
                            appLanguage = appLanguage
                        )
                    }
                }
            }
        } else {
            // Phone Single-Column Stack
            item {
                SmartDataForecastingCard(
                    forecast = dataForecast,
                    appLanguage = appLanguage,
                    onConfigurePlanClick = onNavigateToSettings
                )
            }

            item {
                CycleRunwayCard(
                    forecast = dataForecast,
                    appLanguage = appLanguage
                )
            }

            item {
                SmartRunwayTipsCard(
                    forecast = dataForecast,
                    appLanguage = appLanguage
                )
            }
        }

        // Bottom padding
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Interactive card comparing actual burn rate against the safe runway budget.
 * Harmonized with 16.dp internal padding, balanced 78.dp metric boxes, and 8.dp progress track.
 */
@Composable
private fun CycleRunwayCard(
    forecast: SmartDataForecast,
    appLanguage: AppLanguage,
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
                .padding(16.dp)
        ) {
            // Header Row: Title + Velocity Badge
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
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.2.sp
                    )
                }

                // Velocity Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(velocityColor.copy(alpha = 0.16f))
                        .border(1.dp, velocityColor.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = velocityText,
                        color = velocityColor,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Cycle Timeline Progress Track (Harmonized to 8.dp)
            val progressAnimated by animateFloatAsState(
                targetValue = forecast.cycleProgressPercent,
                animationSpec = tween(600, easing = FastOutSlowInEasing),
                label = "cycle_progress"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppStrings.cycleDaysElapsed(
                        elapsed = forecast.daysElapsedInCycle,
                        total = forecast.totalCycleDays,
                        lang = appLanguage
                    ),
                    color = glassStyle.textSecondary,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${forecast.daysRemainingInCycle} ${AppStrings.forecastDays(forecast.daysRemainingInCycle, appLanguage).split(" ").last()}",
                    color = glassStyle.accentPrimary,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (glassStyle.isDark) Color(0xFF0F172A) else Color(0xFFE2E8F0))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressAnimated)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(glassStyle.accentSecondary, glassStyle.accentPrimary)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Safe Runway vs Current Burn Rate comparison (Balanced symmetrical chips)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Safe Daily Budget Box
                RunwaySubMetricBox(
                    label = AppStrings.safeRunwayBudget(appLanguage),
                    value = forecast.recommendedSafeDailyFormatted,
                    subtext = AppStrings.safeRunwayDesc(forecast.daysRemainingInCycle, appLanguage),
                    accentColor = EmeraldSafe,
                    modifier = Modifier.weight(1f)
                )

                // Current Burn Pace Box
                val burnSubtext = if (appLanguage == AppLanguage.ARABIC) {
                    if (forecast.willDepleteBeforeCycleEnd) "يتجاوز المعدل الآمن" else "ضمن المعدل الآمن"
                } else {
                    if (forecast.willDepleteBeforeCycleEnd) "Exceeds safe runway" else "Healthy pace"
                }
                RunwaySubMetricBox(
                    label = AppStrings.forecastBurnRate(appLanguage),
                    value = forecast.dailyBurnRateFormatted,
                    subtext = burnSubtext,
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
            .heightIn(min = 78.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(glassStyle.cardSubtleBg)
            .border(1.dp, glassStyle.borderColor, RoundedCornerShape(14.dp))
            .padding(11.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = glassStyle.textTertiary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = accentColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                color = glassStyle.textSecondary,
                fontSize = 9.5.sp,
                maxLines = 1
            )
        }
    }
}



/**
 * Actionable tips card adapting dynamically to user's forecast status.
 * Beautifully styled with 14.dp padding, glowing badge, and comfortable typography.
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
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = tipText,
                    color = glassStyle.textPrimary,
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
