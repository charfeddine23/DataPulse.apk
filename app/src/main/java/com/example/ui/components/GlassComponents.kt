package com.example.ui.components

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
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.model.LiveSpeedSample
import com.example.model.NetworkType
import com.example.model.QuotaStatus
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.RedAlert
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun GlassContainer(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(22.dp),
    backgroundColor: Color = Color(0xCC111B2E),
    borderColor: Color = Color(0x334FD1C5),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = 0.85f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        borderColor,
                        borderColor.copy(alpha = 0.1f),
                        borderColor.copy(alpha = 0.35f)
                    )
                ),
                shape = shape
            )
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun DashboardHeader(
    speedSample: LiveSpeedSample,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Glowing Pulse Icon
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(CyanPulse.copy(alpha = 0.25f * glowAlpha), NeonPurple.copy(alpha = 0.2f))
                            )
                        )
                        .border(1.dp, CyanPulse.copy(alpha = 0.6f * glowAlpha), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(24.dp)) {
                        val path = Path().apply {
                            moveTo(size.width * 0.1f, size.height * 0.5f)
                            lineTo(size.width * 0.3f, size.height * 0.5f)
                            lineTo(size.width * 0.42f, size.height * 0.2f)
                            lineTo(size.width * 0.58f, size.height * 0.8f)
                            lineTo(size.width * 0.7f, size.height * 0.5f)
                            lineTo(size.width * 0.9f, size.height * 0.5f)
                        }
                        drawPath(
                            path = path,
                            color = CyanPulse,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Data Pulse",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Developed by Charfeddine Ka",
                        color = CyanPulse,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Connection Status Badge
            ConnectionStatusPill(speedSample = speedSample)
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Version & Last Updated info
        Text(
            text = "Version 1.0.0 | Updated Nov 2026",
            color = TextTertiary,
            fontSize = 11.sp,
            modifier = Modifier.padding(start = 54.dp)
        )
    }
}

@Composable
fun ConnectionStatusPill(
    speedSample: LiveSpeedSample,
    modifier: Modifier = Modifier
) {
    val (icon, tint, label) = when (speedSample.connectionType) {
        NetworkType.WIFI -> Triple(Icons.Default.Wifi, CyanPulse, "Wi-Fi Active")
        NetworkType.CELLULAR_5G -> Triple(Icons.Default.SignalCellularAlt, NeonPurple, "5G Cell")
        NetworkType.CELLULAR_4G, NetworkType.CELLULAR_3G, NetworkType.CELLULAR_GENERIC ->
            Triple(Icons.Default.SignalCellularAlt, ElectricBlue, "LTE Data")
        NetworkType.ETHERNET -> Triple(Icons.Default.Wifi, EmeraldSafe, "Ethernet")
        NetworkType.OFFLINE -> Triple(Icons.Default.WifiOff, RedAlert, "Offline")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF131D30))
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
                color = TextPrimary,
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
    modifier: Modifier = Modifier
) {
    GlassContainer(
        modifier = modifier.fillMaxWidth(),
        borderColor = CyanPulse.copy(alpha = 0.4f)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LIVE SPEED METER",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${speedSample.pingMs} ms Latency",
                    color = CyanPulse,
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
                // Download Metric
                SpeedMetricItem(
                    title = "DOWNLOAD",
                    speedFormatted = speedSample.downloadFormatted,
                    peakSpeed = "Peak: ${(speedSample.peakDownloadBps / 1_000_000.0).let { "%.1f Mbps".format(it) }}",
                    icon = Icons.Default.ArrowDownward,
                    color = CyanPulse,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Upload Metric
                SpeedMetricItem(
                    title = "UPLOAD",
                    speedFormatted = speedSample.uploadFormatted,
                    peakSpeed = "Peak: ${(speedSample.peakUploadBps / 1_000_000.0).let { "%.1f Mbps".format(it) }}",
                    icon = Icons.Default.ArrowUpward,
                    color = NeonPurple,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Live Waveform Sparkline
            Text(
                text = "REAL-TIME TRAFFIC WAVE",
                color = TextTertiary,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            SpeedSparkline(
                dataPoints = speedHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
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
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F1728))
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
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = speedFormatted,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = peakSpeed,
                color = TextTertiary,
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
                    CyanPulse.copy(alpha = 0.25f),
                    CyanPulse.copy(alpha = 0.0f)
                )
            )
        )

        // Line Stroke
        drawPath(
            path = path,
            brush = Brush.horizontalGradient(
                colors = listOf(NeonPurple, CyanPulse)
            ),
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw glowing point at the newest sample
        val lastIdx = dataPoints.size - 1
        val lastX = lastIdx * stepX
        val lastNorm = (dataPoints[lastIdx] / maxVal).coerceIn(0f, 1f)
        val lastY = size.height - (lastNorm * (size.height * 0.8f)) - (size.height * 0.1f)
        drawCircle(
            color = CyanPulse,
            radius = 4.dp.toPx(),
            center = Offset(lastX, lastY)
        )
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
    val statusColor = when (status) {
        QuotaStatus.SAFE -> CyanPulse
        QuotaStatus.WARNING -> AmberAlert
        QuotaStatus.CRITICAL, QuotaStatus.EXCEEDED -> RedAlert
    }

    GlassContainer(
        modifier = modifier.fillMaxWidth(),
        borderColor = statusColor.copy(alpha = 0.4f)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "MONTHLY DATA PLAN",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    if (status == QuotaStatus.WARNING || status == QuotaStatus.CRITICAL || status == QuotaStatus.EXCEEDED) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = "Alert",
                            tint = statusColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E293B))
                        .clickable { onConfigureClick() }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Set Limit",
                        color = CyanPulse,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Bar with Dual Glow
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "%.1f%% used".format(percentage),
                        color = statusColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$daysRemaining days remaining",
                        color = TextTertiary,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Custom Liquid Progress Track
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((percentage / 100f).coerceIn(0.01f, 1f))
                            .height(14.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        statusColor.copy(alpha = 0.6f),
                                        statusColor
                                    )
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Warning Banner if >= 80%
            if (status != QuotaStatus.SAFE) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(statusColor.copy(alpha = 0.12f))
                        .border(1.dp, statusColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    val alertText = when (status) {
                        QuotaStatus.WARNING -> "Warning: 80% of your plan reached. Monitor usage!"
                        QuotaStatus.CRITICAL -> "Critical: 95% data consumed! Approaching cap."
                        QuotaStatus.EXCEEDED -> "Quota exceeded! Overage charges may apply."
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
fun UsagePermissionCard(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassContainer(
        modifier = modifier.fillMaxWidth(),
        borderColor = AmberAlert.copy(alpha = 0.6f)
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = "Usage Permission Needed",
                    tint = AmberAlert,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Usage Access Required",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "To track per-app data usage and historical monthly statistics accurately via Android NetworkStats, please grant Usage Access permission in system settings.",
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onRequestPermission,
                colors = ButtonDefaults.buttonColors(containerColor = AmberAlert),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("grant_usage_permission_button")
            ) {
                Text(
                    text = "Grant Usage Access in Settings",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
