package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WarningAmber
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.model.ForecastStatus
import com.example.model.FormatUtils
import com.example.model.SmartDataForecast
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.LocalLiquidGlass
import com.example.ui.theme.RedAlert

/**
 * Harmonized "Smart Data Forecasting" card featuring the Liquid Glass Dark Mode aesthetic.
 * Symmetrical containers, consistent typography, and dynamic glow lighting.
 */
@Composable
fun SmartDataForecastingCard(
    forecast: SmartDataForecast,
    appLanguage: AppLanguage,
    onConfigurePlanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassStyle = LocalLiquidGlass.current

    // Semantic status color
    val statusColor = when (forecast.status) {
        ForecastStatus.CRITICAL_DEPLETION, ForecastStatus.EXHAUSTED -> RedAlert
        ForecastStatus.EARLY_DEPLETION -> AmberAlert
        ForecastStatus.SUSTAINABLE -> glassStyle.accentPrimary
    }

    // Subtle pulsing rim light animation for critical & warning states
    val infiniteTransition = rememberInfiniteTransition(label = "forecast_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = if (forecast.status == ForecastStatus.CRITICAL_DEPLETION) 0.85f else 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = statusColor.copy(alpha = glowAlpha),
        label = "border_color"
    )

    GlassContainer(
        modifier = modifier
            .fillMaxWidth()
            .testTag("smart_data_forecasting_card"),
        shape = RoundedCornerShape(22.dp),
        backgroundColor = glassStyle.cardBackground,
        borderColor = animatedBorderColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 1. Header: Icon + Title + Live Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = 0.18f))
                            .border(1.dp, statusColor.copy(alpha = 0.45f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = AppStrings.smartForecastTitle(appLanguage),
                            tint = statusColor,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = AppStrings.smartForecastTitle(appLanguage),
                            color = glassStyle.textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.3.sp,
                            lineHeight = 20.sp
                        )
                        Text(
                            text = AppStrings.smartForecastSubtitle(appLanguage),
                            color = glassStyle.textTertiary,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }

                // Status Badge Pill
                val statusLabel = when (forecast.status) {
                    ForecastStatus.CRITICAL_DEPLETION -> AppStrings.statusCritical(appLanguage)
                    ForecastStatus.EARLY_DEPLETION -> AppStrings.statusEarlyWarning(appLanguage)
                    ForecastStatus.SUSTAINABLE -> AppStrings.statusSustainable(appLanguage)
                    ForecastStatus.EXHAUSTED -> AppStrings.statusExhausted(appLanguage)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(statusColor.copy(alpha = 0.16f))
                        .border(1.dp, statusColor.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = statusLabel,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Prominent Warning / Status Banner Container
            val bannerBg = when (forecast.status) {
                ForecastStatus.CRITICAL_DEPLETION, ForecastStatus.EXHAUSTED -> RedAlert.copy(alpha = 0.12f)
                ForecastStatus.EARLY_DEPLETION -> AmberAlert.copy(alpha = 0.12f)
                ForecastStatus.SUSTAINABLE -> glassStyle.accentPrimary.copy(alpha = 0.10f)
            }

            val bannerBorder = when (forecast.status) {
                ForecastStatus.CRITICAL_DEPLETION, ForecastStatus.EXHAUSTED -> RedAlert.copy(alpha = 0.45f)
                ForecastStatus.EARLY_DEPLETION -> AmberAlert.copy(alpha = 0.45f)
                ForecastStatus.SUSTAINABLE -> glassStyle.accentPrimary.copy(alpha = 0.35f)
            }

            val bannerText = when (forecast.status) {
                ForecastStatus.EXHAUSTED -> AppStrings.exhaustedBanner(appLanguage)
                ForecastStatus.CRITICAL_DEPLETION -> AppStrings.warningCriticalBanner(
                    days = forecast.predictedDaysRemaining,
                    planCapGB = forecast.planLimitGB,
                    lang = appLanguage
                )
                ForecastStatus.EARLY_DEPLETION -> AppStrings.warningEarlyBanner(
                    days = forecast.predictedDaysRemaining,
                    cycleDays = forecast.daysRemainingInCycle,
                    lang = appLanguage
                )
                ForecastStatus.SUSTAINABLE -> {
                    val surplusFormatted = FormatUtils.formatBytes(forecast.projectedSurplusDeficitBytes.coerceAtLeast(0L))
                    AppStrings.safeBanner(
                        surplusFormatted = surplusFormatted,
                        daysLeft = forecast.daysRemainingInCycle,
                        lang = appLanguage
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(bannerBg)
                    .border(1.dp, bannerBorder, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (forecast.status == ForecastStatus.SUSTAINABLE) Icons.Default.AutoAwesome else Icons.Default.WarningAmber,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = bannerText,
                        color = glassStyle.textPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Three Symmetrical Metric Chips (Uniform Height & Balanced Alignment)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. Daily Burn Rate
                ForecastMetricChip(
                    title = AppStrings.forecastBurnRate(appLanguage),
                    value = FormatUtils.formatBytes(forecast.dailyBurnRateBytes),
                    unit = if (appLanguage == AppLanguage.ARABIC) "/ يوم" else "/ day",
                    icon = Icons.Default.LocalFireDepartment,
                    accentColor = AmberAlert,
                    modifier = Modifier.weight(1f)
                )

                // 2. Depletion In (Days)
                val depletionValue = if (forecast.status == ForecastStatus.EXHAUSTED) {
                    if (appLanguage == AppLanguage.ARABIC) "منفدة" else "0 Days"
                } else {
                    AppStrings.forecastDays(forecast.predictedDaysRemaining, appLanguage)
                }
                ForecastMetricChip(
                    title = AppStrings.forecastDaysToDeplete(appLanguage),
                    value = depletionValue,
                    unit = "",
                    icon = Icons.Default.DateRange,
                    accentColor = statusColor,
                    modifier = Modifier.weight(1f)
                )

                // 3. Cycle End Projection
                val endProjectionFormatted = FormatUtils.formatBytes(forecast.projectedEndOfCycleUsageBytes)
                ForecastMetricChip(
                    title = AppStrings.forecastEndOfCycle(appLanguage),
                    value = endProjectionFormatted,
                    unit = "",
                    icon = if (forecast.isSurplus) Icons.Default.TrendingDown else Icons.Default.TrendingUp,
                    accentColor = if (forecast.isSurplus) EmeraldSafe else RedAlert,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Projected Consumption Track (Aligned With Consistent 8.dp Height)
            val limit = forecast.planLimitBytes.toFloat().coerceAtLeast(1f)
            val currentUsedFraction = (forecast.usedBytes.toFloat() / limit).coerceIn(0f, 1f)
            val projectedFraction = (forecast.projectedEndOfCycleUsageBytes.toFloat() / limit).coerceIn(0f, 1.25f)

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${AppStrings.used(appLanguage)}: ${FormatUtils.formatBytes(forecast.usedBytes)} / ${forecast.planLimitGB.toInt()} GB",
                        color = glassStyle.textSecondary,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )

                    val deltaText = if (forecast.isSurplus) {
                        "+${FormatUtils.formatBytes(forecast.projectedSurplusDeficitBytes)} ${AppStrings.forecastSurplus(appLanguage)}"
                    } else {
                        "-${FormatUtils.formatBytes(-forecast.projectedSurplusDeficitBytes)} ${AppStrings.forecastDeficit(appLanguage)}"
                    }
                    Text(
                        text = deltaText,
                        color = if (forecast.isSurplus) EmeraldSafe else RedAlert,
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
                    // Projected track (semi-transparent extension)
                    if (projectedFraction > currentUsedFraction) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(projectedFraction.coerceAtMost(1f))
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    statusColor.copy(alpha = if (glassStyle.isDark) 0.35f else 0.25f)
                                )
                        )
                    }

                    // Current used track
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(currentUsedFraction)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        glassStyle.accentSecondary,
                                        statusColor
                                    )
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer link to adjust monthly plan cap
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onConfigurePlanClick() }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${AppStrings.editCap(appLanguage)} (${forecast.planLimitGB.toInt()} GB) →",
                    color = glassStyle.accentPrimary,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Harmonized metric chip with uniform height, balanced padding, and clean typography.
 */
@Composable
private fun ForecastMetricChip(
    title: String,
    value: String,
    unit: String,
    icon: ImageVector,
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
            .padding(horizontal = 10.dp, vertical = 9.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = title,
                    color = glassStyle.textTertiary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Column {
                Text(
                    text = value,
                    color = glassStyle.textPrimary,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                if (unit.isNotEmpty()) {
                    Text(
                        text = unit,
                        color = glassStyle.textSecondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}
