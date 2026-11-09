package com.example.ui.components

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.model.FormatUtils
import com.example.model.LiveSpeedSample
import com.example.model.NetworkType
import com.example.model.QuotaStatus
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.LocalLiquidGlass
import com.example.ui.theme.LocalAppDimensions
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.RedAlert

/**
 * Liquid Glass Container with multi-layered blur, frosted backdrops,
 * and adaptive colors for both Light & Dark modes.
 */
@Composable
fun GlassContainer(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(22.dp),
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    enableBlur: Boolean = true,
    content: @Composable () -> Unit
) {
    val glassStyle = LocalLiquidGlass.current
    val effectiveBg = backgroundColor ?: glassStyle.surfaceColor
    val effectiveBorder = borderColor ?: glassStyle.borderColor

    Box(
        modifier = modifier.clip(shape)
    ) {
        // Frosted Blur Layer (on Android 12+)
        if (enableBlur && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .blur(glassStyle.blurRadius)
                    .background(if (glassStyle.isDark) Color(0x550B1222) else Color(0x33CBD5E1))
            )
        }

        // Semi-transparent Backdrop Gradient & Rim Highlight
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            effectiveBg,
                            effectiveBg.copy(alpha = if (glassStyle.isDark) 0.85f else 0.95f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            effectiveBorder,
                            effectiveBorder.copy(alpha = 0.2f),
                            effectiveBorder.copy(alpha = 0.45f)
                        )
                    ),
                    shape = shape
                )
        )

        // Card Content
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
fun DashboardHeader(
    speedSample: LiveSpeedSample,
    appLanguage: AppLanguage,
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val glassStyle = LocalLiquidGlass.current
    val dimensions = LocalAppDimensions.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Outer App Icon converted and displayed inside the app
                Box(
                    modifier = Modifier
                        .size(dimensions.headerIconBoxSize)
                        .clip(RoundedCornerShape(14.dp))
                        .background(glassStyle.cardSubtleBg)
                        .border(1.dp, glassStyle.accentPrimary.copy(alpha = 0.45f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Data Pulse",
                        modifier = Modifier.size(dimensions.headerIconSize)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Data Pulse",
                    color = glassStyle.textPrimary,
                    fontSize = dimensions.textTitleLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            // Connection Status Badge + adjacent 3-line Hamburger Menu Button on far right
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ConnectionStatusPill(speedSample = speedSample, appLanguage = appLanguage)

                LiquidGlassHamburgerMenuButton(
                    onClick = onOpenSettings,
                    contentDescription = AppStrings.openSettingsMenu(appLanguage)
                )
            }
        }
    }
}

/**
 * Premium 3-line hamburger menu button styled with Liquid Glass Dark Mode aesthetic.
 * 3 stacked horizontal lines with glowing accents.
 */
@Composable
fun LiquidGlassHamburgerMenuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = "Open Settings"
) {
    val glassStyle = LocalLiquidGlass.current

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(glassStyle.cardSubtleBg)
            .border(
                1.dp,
                Brush.linearGradient(
                    colors = listOf(
                        glassStyle.accentPrimary.copy(alpha = 0.55f),
                        glassStyle.accentSecondary.copy(alpha = 0.25f)
                    )
                ),
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .testTag("top_bar_settings_menu_button"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(3.5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top line with glowing primary gradient
            Box(
                modifier = Modifier
                    .width(18.dp)
                    .height(2.2.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(glassStyle.accentPrimary, glassStyle.accentSecondary)
                        )
                    )
            )
            // Middle line with glowing highlight
            Box(
                modifier = Modifier
                    .width(18.dp)
                    .height(2.2.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(glassStyle.textPrimary, glassStyle.accentPrimary)
                        )
                    )
            )
            // Bottom line with secondary accent glow
            Box(
                modifier = Modifier
                    .width(14.dp)
                    .height(2.2.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(glassStyle.accentSecondary, glassStyle.accentPrimary)
                        )
                    )
            )
        }
    }
}

@Composable
fun ConnectionStatusPill(
    speedSample: LiveSpeedSample,
    appLanguage: AppLanguage,
    modifier: Modifier = Modifier
) {
    val glassStyle = LocalLiquidGlass.current
    val (icon, tint, label) = when (speedSample.connectionType) {
        NetworkType.WIFI -> Triple(Icons.Default.Wifi, glassStyle.accentPrimary, AppStrings.wifiActive(appLanguage))
        NetworkType.CELLULAR_5G -> Triple(Icons.Default.SignalCellularAlt, glassStyle.accentSecondary, AppStrings.cellular5g(appLanguage))
        NetworkType.CELLULAR_4G, NetworkType.CELLULAR_3G, NetworkType.CELLULAR_GENERIC ->
            Triple(Icons.Default.SignalCellularAlt, ElectricBlue, AppStrings.cellularGeneric(appLanguage))
        NetworkType.ETHERNET -> Triple(Icons.Default.Wifi, EmeraldSafe, AppStrings.ethernet(appLanguage))
        NetworkType.OFFLINE -> Triple(Icons.Default.WifiOff, RedAlert, AppStrings.disconnected(appLanguage))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(glassStyle.cardSubtleBg)
            .border(1.dp, tint.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(tint)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = glassStyle.textPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun LiveSpeedCard(
    speedSample: LiveSpeedSample,
    speedHistory: List<Float>,
    appLanguage: AppLanguage,
    modifier: Modifier = Modifier
) {
    val glassStyle = LocalLiquidGlass.current

    GlassContainer(
        modifier = modifier.fillMaxWidth(),
        borderColor = glassStyle.accentPrimary.copy(alpha = 0.45f)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppStrings.liveSpeedTitle(appLanguage),
                    color = glassStyle.textSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "${speedSample.pingMs} ${AppStrings.ms(appLanguage)}",
                    color = glassStyle.accentPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Download & Upload Speeds
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SpeedMetricItem(
                    title = AppStrings.download(appLanguage),
                    speedFormatted = speedSample.downloadFormatted,
                    peakSpeed = "${AppStrings.peak(appLanguage)}: ${FormatUtils.formatSpeed(speedSample.peakDownloadBps)}",
                    icon = Icons.Default.ArrowDownward,
                    color = glassStyle.accentPrimary,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                SpeedMetricItem(
                    title = AppStrings.upload(appLanguage),
                    speedFormatted = speedSample.uploadFormatted,
                    peakSpeed = "${AppStrings.peak(appLanguage)}: ${FormatUtils.formatSpeed(speedSample.peakUploadBps)}",
                    icon = Icons.Default.ArrowUpward,
                    color = glassStyle.accentSecondary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Live Waveform Sparkline
            SpeedSparkline(
                dataPoints = speedHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            )
        }
    }
}

@Composable
fun SpeedMetricItem(
    title: String,
    speedFormatted: String,
    peakSpeed: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    val glassStyle = LocalLiquidGlass.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(glassStyle.cardSubtleBg)
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(15.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    color = glassStyle.textSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = speedFormatted,
                color = glassStyle.textPrimary,
                fontSize = 19.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = peakSpeed,
                color = glassStyle.textTertiary,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun SpeedSparkline(
    dataPoints: List<Float>,
    modifier: Modifier = Modifier
) {
    val glassStyle = LocalLiquidGlass.current

    Canvas(modifier = modifier) {
        if (dataPoints.isEmpty()) return@Canvas

        val maxVal = (dataPoints.maxOrNull() ?: 1f).coerceAtLeast(1f)
        val stepX = size.width / (dataPoints.size - 1).coerceAtLeast(1)

        val path = Path()
        val fillPath = Path()

        dataPoints.forEachIndexed { index, value ->
            val x = index * stepX
            val normalized = (value / maxVal).coerceIn(0f, 1f)
            val y = size.height - (normalized * (size.height * 0.8f)) - (size.height * 0.1f)

            if (index == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, size.height)
                fillPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }

        fillPath.lineTo(size.width, size.height)
        fillPath.close()

        // Gradient Fill
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    glassStyle.accentPrimary.copy(alpha = if (glassStyle.isDark) 0.25f else 0.15f),
                    glassStyle.accentPrimary.copy(alpha = 0.0f)
                )
            )
        )

        // Line Stroke
        drawPath(
            path = path,
            brush = Brush.horizontalGradient(
                colors = listOf(glassStyle.accentSecondary, glassStyle.accentPrimary)
            ),
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw glowing point at newest sample
        val lastIdx = dataPoints.size - 1
        val lastX = lastIdx * stepX
        val lastNorm = (dataPoints[lastIdx] / maxVal).coerceIn(0f, 1f)
        val lastY = size.height - (lastNorm * (size.height * 0.8f)) - (size.height * 0.1f)
        drawCircle(
            color = glassStyle.accentPrimary,
            radius = 4.dp.toPx(),
            center = Offset(lastX, lastY)
        )
    }
}

@Composable
fun CircularQuotaProgressGauge(
    usedBytes: Long,
    limitBytes: Long,
    percentage: Float,
    status: QuotaStatus,
    daysRemaining: Int,
    appLanguage: AppLanguage,
    onConfigureClick: () -> Unit,
    isInternetConnected: Boolean = true,
    modifier: Modifier = Modifier
) {
    val glassStyle = LocalLiquidGlass.current
    val dimensions = LocalAppDimensions.current
    val statusColor = when (status) {
        QuotaStatus.SAFE -> glassStyle.accentPrimary
        QuotaStatus.WARNING -> AmberAlert
        QuotaStatus.CRITICAL, QuotaStatus.EXCEEDED -> RedAlert
    }

    val isArabic = (appLanguage == AppLanguage.ARABIC)
    val tierState = remember(percentage, isArabic) {
        PulseEngineCalculator.calculate(percentage, isArabic)
    }

    val animatedTierColor by animateColorAsState(
        targetValue = tierState.primaryColor,
        animationSpec = tween(durationMillis = 500),
        label = "gauge_tier_primary_color"
    )
    val animatedTierSecondaryColor by animateColorAsState(
        targetValue = tierState.secondaryColor,
        animationSpec = tween(durationMillis = 500),
        label = "gauge_tier_secondary_color"
    )

    // Dynamic Live Color Shifting in Background (Cycles continuously through Neon Cyan, Electric Blue, Violet, Rose Magenta, and Emerald)
    val infiniteTransition = rememberInfiniteTransition(label = "live_color_shift_transition")
    val colorShiftAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6500, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "live_color_shift"
    )

    val animatedSweep by animateFloatAsState(
        targetValue = (percentage / 100f).coerceIn(0.01f, 1f),
        animationSpec = tween(durationMillis = 800),
        label = "circular_quota_sweep"
    )

    // Computed dynamic live colors based on rotation phase
    val dynamicLiveColors = remember(colorShiftAngle) {
        val rad = Math.toRadians(colorShiftAngle.toDouble())
        val cosVal = ((kotlin.math.cos(rad) + 1.0) / 2.0).toFloat()
        val sinVal = ((kotlin.math.sin(rad) + 1.0) / 2.0).toFloat()
        
        val liveColorA = Color(
            red = 0.05f + 0.50f * cosVal,
            green = 0.70f + 0.25f * (1f - cosVal),
            blue = 0.90f + 0.10f * sinVal,
            alpha = 0.45f
        )
        val liveColorB = Color(
            red = 0.65f + 0.30f * sinVal,
            green = 0.15f + 0.20f * cosVal,
            blue = 0.95f,
            alpha = 0.40f
        )
        val liveColorC = Color(
            red = 0.00f,
            green = 0.85f + 0.15f * sinVal,
            blue = 0.75f + 0.25f * cosVal,
            alpha = 0.35f
        )
        listOf(liveColorA, liveColorB, liveColorC)
    }

    GlassContainer(
        modifier = modifier.fillMaxWidth(),
        borderColor = animatedTierColor.copy(alpha = 0.45f)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Row: Title & Edit Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = AppStrings.monthlyPlanTitle(appLanguage),
                        color = glassStyle.textSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    if (status != QuotaStatus.SAFE) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = "Warning",
                            tint = animatedTierColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(glassStyle.cardSubtleBg)
                        .clickable { onConfigureClick() }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = AppStrings.editCap(appLanguage),
                        color = animatedTierColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Central Circular Progress Gauge with 30-Level Pulse Engine
            Box(
                modifier = Modifier.size(dimensions.circularGaugeSize),
                contentAlignment = Alignment.Center
            ) {
                // Background Track and Active Sweep Arc
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = dimensions.circularGaugeStroke.toPx()
                    val arcSize = size.width - strokeWidth * 2
                    val topLeft = Offset(strokeWidth, strokeWidth)

                    // 1. Track Background Circle
                    drawArc(
                        color = if (glassStyle.isDark) Color(0xFF131D30) else Color(0xFFE2E8F0),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(arcSize, arcSize),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // 2. Active Arc with 30-Level Dynamic Color Infusion
                    val sweepAngle = 360f * animatedSweep
                    drawArc(
                        brush = Brush.sweepGradient(
                            0.0f to dynamicLiveColors[0],
                            0.35f to animatedTierSecondaryColor,
                            1.0f to animatedTierColor
                        ),
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(arcSize, arcSize),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                // Heartbeat Center Content Powered by 30-Level Engine
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    LivingPulseHeart(
                        percentage = percentage,
                        isInternetConnected = isInternetConnected,
                        appLanguage = appLanguage,
                        heartSize = if (dimensions.isTablet) 34.dp else 28.dp,
                        auraSize = if (dimensions.isTablet) 98.dp else 80.dp,
                        showTierBadge = true
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "%.1f%%".format(percentage),
                        color = animatedTierColor,
                        fontSize = dimensions.textMetricValue,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${FormatUtils.formatBytes(usedBytes)} / ${FormatUtils.formatBytes(limitBytes)}",
                        color = glassStyle.textTertiary,
                        fontSize = dimensions.textCaption
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Days remaining pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(animatedTierColor.copy(alpha = 0.10f))
                    .border(1.dp, animatedTierColor.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = AppStrings.daysRemaining(daysRemaining, appLanguage),
                    color = animatedTierColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Warning Banner if >= 80% or >= 95%
            if (status != QuotaStatus.SAFE) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(statusColor.copy(alpha = 0.12f))
                        .border(1.dp, statusColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    val alertText = when (status) {
                        QuotaStatus.WARNING -> AppStrings.alert80Warning(appLanguage)
                        QuotaStatus.CRITICAL -> AppStrings.alert95Critical(appLanguage)
                        QuotaStatus.EXCEEDED -> AppStrings.quotaExceeded(appLanguage)
                        else -> ""
                    }
                    Text(
                        text = alertText,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun QuotaProgressGauge(
    usedBytes: Long,
    limitBytes: Long,
    percentage: Float,
    status: QuotaStatus,
    daysRemaining: Int,
    onConfigureClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CircularQuotaProgressGauge(
        usedBytes = usedBytes,
        limitBytes = limitBytes,
        percentage = percentage,
        status = status,
        daysRemaining = daysRemaining,
        appLanguage = AppLanguage.ARABIC,
        onConfigureClick = onConfigureClick,
        modifier = modifier
    )
}

/**
 * Usage Permission Card with Privacy Transparency and Android 13+ Restricted Settings Guide.
 */
@Composable
fun UsagePermissionCard(
    onRequestPermission: () -> Unit,
    onOpenAppInfo: () -> Unit,
    appLanguage: AppLanguage = AppLanguage.ARABIC,
    modifier: Modifier = Modifier
) {
    val glassStyle = LocalLiquidGlass.current
    var showRestrictedDialog by remember { mutableStateOf(false) }

    GlassContainer(
        modifier = modifier.fillMaxWidth(),
        borderColor = AmberAlert.copy(alpha = 0.6f)
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = AppStrings.usageAccess(appLanguage),
                    tint = AmberAlert,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = AppStrings.permCardTitle(appLanguage),
                    color = glassStyle.textPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Privacy guarantee
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = AppStrings.privacySafe(appLanguage),
                    tint = EmeraldSafe,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = AppStrings.privacySafe(appLanguage),
                    color = EmeraldSafe,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = AppStrings.permCardDesc(appLanguage),
                color = glassStyle.textSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRequestPermission,
                    colors = ButtonDefaults.buttonColors(containerColor = AmberAlert),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("grant_usage_permission_button")
                ) {
                    Text(
                        text = AppStrings.grantPerm(appLanguage),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                // Android 13+ Restricted Settings Helper
                OutlinedButton(
                    onClick = { showRestrictedDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = glassStyle.accentPrimary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(glassStyle.accentPrimary, glassStyle.accentSecondary))
                    ),
                    modifier = Modifier.testTag("restricted_settings_help_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = "Help",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = AppStrings.restrictedSettingsHelp(appLanguage),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    if (showRestrictedDialog) {
        RestrictedSettingsGuideDialog(
            onDismiss = { showRestrictedDialog = false },
            onOpenAppInfo = onOpenAppInfo,
            onOpenUsageSettings = onRequestPermission,
            appLanguage = appLanguage
        )
    }
}

/**
 * Interactive Dialog explaining Android 13+ "Restricted Settings" (Sideload Protection).
 */
@Composable
fun RestrictedSettingsGuideDialog(
    onDismiss: () -> Unit,
    onOpenAppInfo: () -> Unit,
    onOpenUsageSettings: () -> Unit,
    appLanguage: AppLanguage = AppLanguage.ARABIC
) {
    val glassStyle = LocalLiquidGlass.current

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(glassStyle.cardBackground)
                .border(
                    1.dp,
                    Brush.linearGradient(listOf(glassStyle.accentPrimary, glassStyle.accentSecondary)),
                    RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(AmberAlert.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = AmberAlert,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = AppStrings.restrictedDialogTitle(appLanguage),
                            color = glassStyle.textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = glassStyle.textTertiary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = AppStrings.restrictedDialogDesc(appLanguage),
                    color = glassStyle.textSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Step 1
                GuideStepItem(
                    stepNumber = "1",
                    title = AppStrings.step1Title(appLanguage),
                    description = AppStrings.step1Desc(appLanguage)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Step 2
                GuideStepItem(
                    stepNumber = "2",
                    title = AppStrings.step2Title(appLanguage),
                    description = AppStrings.step2Desc(appLanguage)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Step 3
                GuideStepItem(
                    stepNumber = "3",
                    title = AppStrings.step3Title(appLanguage),
                    description = AppStrings.step3Desc(appLanguage)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onOpenAppInfo()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = glassStyle.accentPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = AppStrings.btnAppInfo(appLanguage),
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            onOpenUsageSettings()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = glassStyle.cardSubtleBg),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = AppStrings.btnUsagePerm(appLanguage),
                            color = glassStyle.accentPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GuideStepItem(
    stepNumber: String,
    title: String,
    description: String
) {
    val glassStyle = LocalLiquidGlass.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(glassStyle.cardSubtleBg)
            .padding(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(glassStyle.accentPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber,
                color = Color.Black,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                color = glassStyle.textPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = glassStyle.textTertiary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}
