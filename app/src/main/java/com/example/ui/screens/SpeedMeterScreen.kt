package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.FormatUtils
import com.example.ui.MainViewModel
import com.example.ui.components.GlassContainer
import com.example.ui.components.SpeedSparkline
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SpeedMeterScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val speedSample by viewModel.speedSample.collectAsStateWithLifecycle()
    val speedHistory by viewModel.speedHistory.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = com.example.model.AppStrings.speedMeterTitle(appLanguage),
            color = glassStyle.textPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = com.example.model.AppStrings.speedMeterSubtitle(appLanguage),
            color = glassStyle.textSecondary,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Large Futuristic Speed Gauge
        SpeedometerDial(
            currentSpeedBps = speedSample.downloadBps,
            formattedSpeed = speedSample.downloadFormatted,
            appLanguage = appLanguage,
            modifier = Modifier.size(240.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Real-time Upload & Download Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val peakPrefix = if (appLanguage == com.example.model.AppLanguage.ARABIC) "الذروة: " else "Peak: "
            SpeedTelemetryCard(
                title = com.example.model.AppStrings.currentDownload(appLanguage),
                speed = speedSample.downloadFormatted,
                peak = "$peakPrefix${FormatUtils.formatSpeed(speedSample.peakDownloadBps)}",
                icon = Icons.Default.ArrowDownward,
                accentColor = glassStyle.accentPrimary,
                modifier = Modifier.weight(1f)
            )

            SpeedTelemetryCard(
                title = com.example.model.AppStrings.currentUpload(appLanguage),
                speed = speedSample.uploadFormatted,
                peak = "$peakPrefix${FormatUtils.formatSpeed(speedSample.peakUploadBps)}",
                icon = Icons.Default.ArrowUpward,
                accentColor = glassStyle.accentSecondary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live Continuous Wave Graph
        GlassContainer(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = com.example.model.AppStrings.continuousThroughput(appLanguage),
                    color = glassStyle.textSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                SpeedSparkline(
                    dataPoints = speedHistory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Network Diagnostics Glass Card
        val connectionNameTranslated = if (appLanguage == com.example.model.AppLanguage.ARABIC) {
            when (speedSample.connectionType) {
                com.example.model.NetworkType.WIFI -> "واي فاي"
                com.example.model.NetworkType.CELLULAR_5G -> "شبكة الجيل الخامس 5G"
                com.example.model.NetworkType.CELLULAR_4G -> "شبكة الجيل الرابع 4G"
                com.example.model.NetworkType.CELLULAR_3G -> "شبكة الجيل الثالث 3G"
                com.example.model.NetworkType.CELLULAR_GENERIC -> "بيانات الجوال"
                com.example.model.NetworkType.ETHERNET -> "إيثرنت"
                com.example.model.NetworkType.OFFLINE -> "غير متصل"
            }
        } else {
            when (speedSample.connectionType) {
                com.example.model.NetworkType.WIFI -> "Wi-Fi"
                com.example.model.NetworkType.CELLULAR_5G -> "5G Cellular"
                com.example.model.NetworkType.CELLULAR_4G -> "4G LTE"
                com.example.model.NetworkType.CELLULAR_3G -> "3G Mobile"
                com.example.model.NetworkType.CELLULAR_GENERIC -> "Cellular Data"
                com.example.model.NetworkType.ETHERNET -> "Ethernet"
                com.example.model.NetworkType.OFFLINE -> "Disconnected"
            }
        }

        val disconnectedLabel = if (appLanguage == com.example.model.AppLanguage.ARABIC) "غير متصل" else "Disconnected"
        val unknownLabel = if (appLanguage == com.example.model.AppLanguage.ARABIC) "غير متوفر" else "Unavailable"
        val latencyUnit = if (appLanguage == com.example.model.AppLanguage.ARABIC) "مللي ثانية" else "ms"

        GlassContainer(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NetworkCheck,
                        contentDescription = com.example.model.AppStrings.networkDiagnostics(appLanguage),
                        tint = glassStyle.accentPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = com.example.model.AppStrings.networkDiagnostics(appLanguage),
                        color = glassStyle.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                DiagnosticRow(
                    label = com.example.model.AppStrings.activeInterface(appLanguage),
                    value = if (speedSample.connectionName == "Disconnected") disconnectedLabel else speedSample.connectionName
                )
                DiagnosticRow(
                    label = com.example.model.AppStrings.localIp(appLanguage),
                    value = if (speedSample.ipAddress == "Unknown") unknownLabel else speedSample.ipAddress
                )
                DiagnosticRow(
                    label = com.example.model.AppStrings.estimatedLatency(appLanguage),
                    value = "${speedSample.pingMs} $latencyUnit"
                )
                DiagnosticRow(
                    label = com.example.model.AppStrings.connectionState(appLanguage),
                    value = connectionNameTranslated
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SpeedometerDial(
    currentSpeedBps: Double,
    formattedSpeed: String,
    appLanguage: com.example.model.AppLanguage,
    modifier: Modifier = Modifier
) {
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current
    // 0 to 100 Mbps mapping
    val currentMbps = (currentSpeedBps / 1_000_000.0).toFloat().coerceIn(0f, 100f)
    val sweepFraction = (currentMbps / 100f).coerceIn(0.02f, 1f)

    val animatedSweep by animateFloatAsState(
        targetValue = sweepFraction,
        animationSpec = tween(durationMillis = 600),
        label = "speedometer_sweep"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            val arcSize = size.width - strokeWidth * 2
            val topLeft = Offset(strokeWidth, strokeWidth)

            // Background Track Arc (240 degrees, from 150 to 390)
            drawArc(
                color = if (glassStyle.isDark) Color(0xFF131D30) else Color(0xFFE2E8F0),
                startAngle = 150f,
                sweepAngle = 240f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(arcSize, arcSize),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Foreground Active Arc
            val activeSweep = 240f * animatedSweep
            drawArc(
                brush = Brush.sweepGradient(
                    0.4f to glassStyle.accentPrimary,
                    0.8f to glassStyle.accentSecondary,
                    1.0f to glassStyle.accentPrimary
                ),
                startAngle = 150f,
                sweepAngle = activeSweep,
                useCenter = false,
                topLeft = topLeft,
                size = Size(arcSize, arcSize),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Indicator dot at tip
            val angleRad = Math.toRadians((150f + activeSweep).toDouble())
            val radius = arcSize / 2f
            val cx = size.width / 2f + radius * cos(angleRad).toFloat()
            val cy = size.height / 2f + radius * sin(angleRad).toFloat()

            drawCircle(
                color = if (glassStyle.isDark) Color.White else glassStyle.textPrimary,
                radius = 6.dp.toPx(),
                center = Offset(cx, cy)
            )
        }

        // Center Speed Readout
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Speed,
                contentDescription = null,
                tint = glassStyle.accentPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formattedSpeed,
                color = glassStyle.textPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = com.example.model.AppStrings.liveBandwidth(appLanguage),
                color = glassStyle.textTertiary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun SpeedTelemetryCard(
    title: String,
    speed: String,
    peak: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(glassStyle.cardBackground)
            .border(
                1.dp,
                if (glassStyle.isDark) accentColor.copy(alpha = 0.35f) else glassStyle.borderColor,
                RoundedCornerShape(18.dp)
            )
            .padding(14.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    color = glassStyle.textSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = speed,
                color = glassStyle.textPrimary,
                fontSize = 19.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = peak,
                color = glassStyle.textTertiary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun DiagnosticRow(label: String, value: String) {
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = glassStyle.textSecondary, fontSize = 12.sp)
        Text(text = value, color = glassStyle.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
