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
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Live Speed Telemetry",
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Real-time bandwidth throughput and network diagnostics",
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Large Futuristic Speed Gauge
        SpeedometerDial(
            currentSpeedBps = speedSample.downloadBps,
            formattedSpeed = speedSample.downloadFormatted,
            modifier = Modifier.size(240.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Real-time Upload & Download Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SpeedTelemetryCard(
                title = "CURRENT DOWNLOAD",
                speed = speedSample.downloadFormatted,
                peak = "Peak: ${FormatUtils.formatSpeed(speedSample.peakDownloadBps)}",
                icon = Icons.Default.ArrowDownward,
                accentColor = CyanPulse,
                modifier = Modifier.weight(1f)
            )

            SpeedTelemetryCard(
                title = "CURRENT UPLOAD",
                speed = speedSample.uploadFormatted,
                peak = "Peak: ${FormatUtils.formatSpeed(speedSample.peakUploadBps)}",
                icon = Icons.Default.ArrowUpward,
                accentColor = NeonPurple,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live Continuous Wave Graph
        GlassContainer(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = "CONTINUOUS THROUGHPUT GRAPH",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
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
        GlassContainer(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NetworkCheck,
                        contentDescription = "Diagnostics",
                        tint = CyanPulse,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NETWORK DIAGNOSTICS",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                DiagnosticRow(label = "Active Interface", value = speedSample.connectionName)
                DiagnosticRow(label = "Local IP Address", value = speedSample.ipAddress)
                DiagnosticRow(label = "Estimated Latency", value = "${speedSample.pingMs} ms")
                DiagnosticRow(label = "Connection State", value = speedSample.connectionType.name)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SpeedometerDial(
    currentSpeedBps: Double,
    formattedSpeed: String,
    modifier: Modifier = Modifier
) {
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
                color = Color(0xFF131D30),
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
                    0.4f to CyanPulse,
                    0.8f to NeonPurple,
                    1.0f to CyanPulse
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
                color = Color.White,
                radius = 6.dp.toPx(),
                center = Offset(cx, cy)
            )
        }

        // Center Speed Readout
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Speed,
                contentDescription = null,
                tint = CyanPulse,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formattedSpeed,
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Live Bandwidth",
                color = TextTertiary,
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
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF0F1728))
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
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
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = speed,
                color = TextPrimary,
                fontSize = 19.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = peak,
                color = TextTertiary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun DiagnosticRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextSecondary, fontSize = 12.sp)
        Text(text = value, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
