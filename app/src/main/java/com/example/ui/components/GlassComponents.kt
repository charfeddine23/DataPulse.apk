package com.example.ui.components

import android.os.Build
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
import androidx.compose.ui.window.Dialog
import com.example.model.LiveSpeedSample
import com.example.model.NetworkType
import com.example.model.QuotaStatus
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.LocalLiquidGlass
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
    modifier: Modifier = Modifier
) {
    val glassStyle = LocalLiquidGlass.current
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
                                colors = listOf(
                                    glassStyle.accentPrimary.copy(alpha = 0.25f * glowAlpha),
                                    glassStyle.accentSecondary.copy(alpha = 0.2f)
                                )
                            )
                        )
                        .border(1.dp, glassStyle.accentPrimary.copy(alpha = 0.6f * glowAlpha), RoundedCornerShape(14.dp)),
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
                            color = glassStyle.accentPrimary,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Data Pulse",
                        color = glassStyle.textPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "تطوير: Charfeddine Ka",
                        color = glassStyle.accentPrimary,
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
            text = "الإصدار 1.0.0 | تحديث نوفمبر 2026",
            color = glassStyle.textTertiary,
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
    val glassStyle = LocalLiquidGlass.current
    val (icon, tint, label) = when (speedSample.connectionType) {
        NetworkType.WIFI -> Triple(Icons.Default.Wifi, glassStyle.accentPrimary, "واي فاي نشط")
        NetworkType.CELLULAR_5G -> Triple(Icons.Default.SignalCellularAlt, glassStyle.accentSecondary, "شبكة 5G")
        NetworkType.CELLULAR_4G, NetworkType.CELLULAR_3G, NetworkType.CELLULAR_GENERIC ->
            Triple(Icons.Default.SignalCellularAlt, ElectricBlue, "بيانات الجوال")
        NetworkType.ETHERNET -> Triple(Icons.Default.Wifi, EmeraldSafe, "إيثرنت")
        NetworkType.OFFLINE -> Triple(Icons.Default.WifiOff, RedAlert, "غير متصل")
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
                    text = "مقياس السرعة اللحظي",
                    color = glassStyle.textSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "${speedSample.pingMs} مللي ثانية",
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
                    title = "تنزيل (Download)",
                    speedFormatted = speedSample.downloadFormatted,
                    peakSpeed = "الذروة: ${(speedSample.peakDownloadBps / 1_000_000.0).let { "%.1f ميغابت/ث".format(it) }}",
                    icon = Icons.Default.ArrowDownward,
                    color = glassStyle.accentPrimary,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                SpeedMetricItem(
                    title = "رفع (Upload)",
                    speedFormatted = speedSample.uploadFormatted,
                    peakSpeed = "الذروة: ${(speedSample.peakUploadBps / 1_000_000.0).let { "%.1f ميغابت/ث".format(it) }}",
                    icon = Icons.Default.ArrowUpward,
                    color = glassStyle.accentSecondary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Live Waveform Sparkline
            Text(
                text = "مخطط تدفق البيانات المباشر",
                color = glassStyle.textTertiary,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
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
fun QuotaProgressGauge(
    usedBytes: Long,
    limitBytes: Long,
    percentage: Float,
    status: QuotaStatus,
    daysRemaining: Int,
    onConfigureClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassStyle = LocalLiquidGlass.current
    val statusColor = when (status) {
        QuotaStatus.SAFE -> glassStyle.accentPrimary
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
                        text = "باقة البيانات الشهرية",
                        color = glassStyle.textSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    if (status != QuotaStatus.SAFE) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = "تنبيه",
                            tint = statusColor,
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
                        text = "تعديل الحد",
                        color = glassStyle.accentPrimary,
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
                        text = "%.1f%% مستهلك".format(percentage),
                        color = statusColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$daysRemaining أيام متبقية في الدورة",
                        color = glassStyle.textTertiary,
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
                        .background(if (glassStyle.isDark) Color(0xFF0F172A) else Color(0xFFE2E8F0))
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
                        QuotaStatus.WARNING -> "تنبيه: تم استهلاك 80% من باقة البيانات. يرجى مراقبة الاستهلاك."
                        QuotaStatus.CRITICAL -> "تحذير حرج: تم استهلاك 95% من الباقة! اقتربت من النفاد."
                        QuotaStatus.EXCEEDED -> "تم تجاوز سعة باقة البيانات المحددة!"
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

/**
 * Usage Permission Card with Privacy Transparency and Android 13+ Restricted Settings Guide.
 */
@Composable
fun UsagePermissionCard(
    onRequestPermission: () -> Unit,
    onOpenAppInfo: () -> Unit,
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
                    contentDescription = "إذن الوصول للاستخدام",
                    tint = AmberAlert,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "مطلوب إذن الوصول للاستخدام (محلياً فقط)",
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
                    contentDescription = "خصوصية مؤكدة",
                    tint = EmeraldSafe,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "آمن وخصوصية 100% - لا يتم جمع أو مشاركة أي بيانات إطلاقاً.",
                    color = EmeraldSafe,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "يحتاج تطبيق Data Pulse إلى إذن الوصول للاستخدام لحساب استهلاك التطبيقات وبيانات الجوال والواي فاي بدقة عبر نظام أندرويد. لا يتم رفع أو حفظ أي بيانات خاصة خارج جهازك.",
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
                        text = "منح إذن الوصول",
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
                        contentDescription = "مساعدة",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "إعداد مقيّد؟",
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
            onOpenUsageSettings = onRequestPermission
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
    onOpenUsageSettings: () -> Unit
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
                            text = "حل مشكلة الإعدادات المقيدة",
                            color = glassStyle.textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق", tint = glassStyle.textTertiary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "إذا ظهرت لك رسالة أندرويد 13+: 'إعداد مقيّد - حفاظاً على أمانك، هذا الإعداد غير متاح حالياً'، اتبع الخطوات السهلة التالية:",
                    color = glassStyle.textSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Step 1
                GuideStepItem(
                    stepNumber = "1",
                    title = "فتح معلومات التطبيق",
                    description = "اضغط على زر 'معلومات التطبيق' بالأسفل للانتقال لصفحة Data Pulse بالنظام."
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Step 2
                GuideStepItem(
                    stepNumber = "2",
                    title = "السماح بالإعدادات المقيدة",
                    description = "اضغط على القائمة (⋮) أعلى اليمين، واختر 'السماح بالإعدادات المقيدة' ثم قم بتأكيد بصمتك أو رمز القفل."
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Step 3
                GuideStepItem(
                    stepNumber = "3",
                    title = "تفعيل إذن الوصول للاستخدام",
                    description = "عد للتطبيق واضغط على 'إذن الاستخدام' لتفعيل مفتاح Data Pulse بنجاح."
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
                            text = "1. معلومات التطبيق",
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
                            text = "2. إذن الاستخدام",
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
