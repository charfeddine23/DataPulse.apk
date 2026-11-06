package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.model.FormatUtils
import com.example.ui.theme.LocalLiquidGlass
import kotlin.random.Random

/**
 * Snowflake particle model for interactive offline state
 */
private data class Snowflake(
    val initialXRatio: Float,
    val speed: Float,
    val radiusDp: Float,
    val alpha: Float,
    val swayAmplitude: Float,
    val swayPhase: Float
)

/**
 * Ultra-Smooth Continuous Throughput Graph with:
 * 1. Fluid continuous wave interpolation (Animatable + harmonic pulse wave).
 * 2. Dynamic speed-based color shifting (High speed -> Red/Orange, Low speed -> Green/Teal).
 * 3. Special interactive offline mode ("Ice & Snow" effect with falling snowflakes and frosty glass).
 * 4. Deep Liquid Glass aesthetic integration (#0B0F19 background with glowing specular borders).
 */
@Composable
fun ContinuousThroughputGraph(
    dataPoints: List<Float>,
    currentSpeedBps: Double,
    isOffline: Boolean,
    appLanguage: AppLanguage,
    modifier: Modifier = Modifier
) {
    val glassStyle = LocalLiquidGlass.current

    // Normalize speed ratio (0.0 to 1.0) with logarithmic curve up to 50 Mbps
    val speedMbps = (currentSpeedBps / 1_000_000.0).toFloat().coerceAtLeast(0f)
    val speedRatio = (speedMbps / 25.0f).coerceIn(0f, 1f)

    // Dynamic Color Shifting:
    // Low speed -> Vibrant Green / Teal
    // High speed -> Vibrant Red / Orange
    val targetPrimaryColor = when {
        isOffline -> Color(0xFFE0F7FA) // Icy frost white
        speedRatio > 0.5f -> Color(0xFFFF3B30) // High speed -> Vibrant Red
        speedRatio > 0.2f -> Color(0xFFFF9500) // Medium speed -> Vibrant Orange
        else -> Color(0xFF00E676) // Low speed -> Vibrant Green/Teal
    }

    val targetSecondaryColor = when {
        isOffline -> Color(0xFF80DEEA) // Frost Cyan
        speedRatio > 0.5f -> Color(0xFFFF9100) // Orange accent
        speedRatio > 0.2f -> Color(0xFFFFD600) // Warm gold
        else -> Color(0xFF00B4D8) // Electric teal/cyan
    }

    val animatedPrimaryColor by animateColorAsState(
        targetValue = targetPrimaryColor,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "graph_primary_color"
    )

    val animatedSecondaryColor by animateColorAsState(
        targetValue = targetSecondaryColor,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "graph_secondary_color"
    )

    // Continuous flow phase animation for smooth harmonic pulse propagation
    val infiniteTransition = rememberInfiniteTransition(label = "wave_motion")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * Math.PI.toFloat()),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    // Falling snow particle animation time loop (0f to 1f over 4 seconds)
    val snowTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "snow_fall"
    )

    // Pre-calculated snowflakes for deterministic, smooth rendering
    val snowflakes = remember {
        val rand = Random(42)
        List(32) {
            Snowflake(
                initialXRatio = rand.nextFloat(),
                speed = 0.6f + rand.nextFloat() * 0.8f,
                radiusDp = 1.5f + rand.nextFloat() * 2.5f,
                alpha = 0.35f + rand.nextFloat() * 0.55f,
                swayAmplitude = 8f + rand.nextFloat() * 16f,
                swayPhase = rand.nextFloat() * 6.28f
            )
        }
    }

    // Smooth line height animation transition
    val latestSpeedNormalized = if (dataPoints.isNotEmpty()) {
        val maxVal = (dataPoints.maxOrNull() ?: 1f).coerceAtLeast(1f)
        (dataPoints.last() / maxVal).coerceIn(0.05f, 1f)
    } else 0.05f

    val smoothedHeightAnim = remember { Animatable(latestSpeedNormalized) }
    LaunchedEffect(latestSpeedNormalized) {
        smoothedHeightAnim.animateTo(
            targetValue = latestSpeedNormalized,
            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
        )
    }

    // Liquid Glass Container with dynamic frosted glow borders
    val containerBorderBrush = remember(isOffline, animatedPrimaryColor, animatedSecondaryColor) {
        if (isOffline) {
            Brush.linearGradient(
                listOf(Color(0xFFE0F7FA).copy(alpha = 0.6f), Color(0xFF80DEEA).copy(alpha = 0.3f), Color(0xFFFFFFFF).copy(alpha = 0.4f))
            )
        } else {
            Brush.linearGradient(
                listOf(animatedPrimaryColor.copy(alpha = 0.5f), Color(0x1FFFFFFF), animatedSecondaryColor.copy(alpha = 0.5f))
            )
        }
    }

    val graphShape = RoundedCornerShape(22.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .neumorphicDepth(
                shape = graphShape,
                lightColor = if (isOffline) Color(0x3380DEEA) else glassStyle.neumorphicHighlight.copy(alpha = 0.5f),
                darkColor = glassStyle.neumorphicShadow,
                offsetDp = 4.dp
            )
            .clip(graphShape)
            .background(
                brush = Brush.linearGradient(
                    colors = if (isOffline) {
                        listOf(Color(0xFF0F1E29), Color(0xFF08121A)) // Frosty dark ice
                    } else {
                        listOf(glassStyle.neumorphicSurface, glassStyle.neumorphicBase)
                    }
                )
            )
            .border(1.dp, containerBorderBrush, graphShape)
            .padding(16.dp)
    ) {
        Column {
            // Header Row: Title, Speed telemetry & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(animatedPrimaryColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isOffline) Icons.Default.AcUnit else Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = animatedPrimaryColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = if (appLanguage == AppLanguage.ARABIC) "مخطط تدفق البيانات المستمر" else "Continuous Throughput",
                            color = glassStyle.textPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = if (isOffline) {
                                if (appLanguage == AppLanguage.ARABIC) "وضع التجمد - غير متصل بالشبكة" else "Frozen State - Network Offline"
                            } else {
                                if (speedRatio > 0.5f) {
                                    if (appLanguage == AppLanguage.ARABIC) "تدفق فائق (عالي السرعة)" else "High Speed Burst"
                                } else {
                                    if (appLanguage == AppLanguage.ARABIC) "تدفق مستقر (عادي)" else "Stable Low Latency"
                                }
                            },
                            color = animatedPrimaryColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Real-time speed or offline indicator badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(animatedPrimaryColor.copy(alpha = 0.12f))
                        .border(1.dp, animatedPrimaryColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isOffline) {
                            if (appLanguage == AppLanguage.ARABIC) "مجمد" else "Frozen"
                        } else {
                            FormatUtils.formatSpeed(currentSpeedBps)
                        },
                        color = animatedPrimaryColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Canvas: Wave animation + Particle Snowflakes
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    if (isOffline) {
                        // ========================================================
                        // OFFLINE "ICE & SNOW" STATE
                        // Flatline frozen wave + falling snowflake particles
                        // ========================================================

                        // 1. Ice ground / frozen flatline with slight icy shimmer
                        val flatY = canvasHeight * 0.75f
                        val icePath = Path().apply {
                            moveTo(0f, flatY)
                            // Subtle static jagged frost contour
                            val steps = 8
                            for (i in 1..steps) {
                                val x = (canvasWidth / steps) * i
                                val jitter = if (i % 2 == 0) -2.dp.toPx() else 2.dp.toPx()
                                lineTo(x, flatY + jitter)
                            }
                            lineTo(canvasWidth, canvasHeight)
                            lineTo(0f, canvasHeight)
                            close()
                        }

                        // Frozen under-fill
                        drawPath(
                            path = icePath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF80DEEA).copy(alpha = 0.25f),
                                    Color(0xFF006064).copy(alpha = 0.05f)
                                )
                            )
                        )

                        // Frozen line
                        drawLine(
                            color = Color(0xFFE0F7FA),
                            start = Offset(0f, flatY),
                            end = Offset(canvasWidth, flatY),
                            strokeWidth = 2.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )

                        // 2. Falling Snowflakes Animation
                        snowflakes.forEach { flake ->
                            val currentYProgress = (snowTime * flake.speed + flake.initialXRatio) % 1f
                            val flakeY = currentYProgress * canvasHeight
                            val swayOffset = kotlin.math.sin((snowTime * 6.28f + flake.swayPhase).toDouble()).toFloat() * flake.swayAmplitude.dp.toPx()
                            val flakeX = (flake.initialXRatio * canvasWidth + swayOffset).coerceIn(0f, canvasWidth)

                            drawCircle(
                                color = Color.White.copy(alpha = flake.alpha),
                                radius = flake.radiusDp.dp.toPx(),
                                center = Offset(flakeX, flakeY)
                            )
                        }
                    } else {
                        // ========================================================
                        // ONLINE ULTRA-SMOOTH CONTINUOUS WAVE STATE
                        // Real-time interpolated bezier wave with speed gradient
                        // ========================================================
                        if (dataPoints.isNotEmpty()) {
                            val maxVal = (dataPoints.maxOrNull() ?: 1f).coerceAtLeast(1f)
                            val wavePath = Path()
                            val fillPath = Path()

                            val pointCount = dataPoints.size
                            val stepX = canvasWidth / (pointCount - 1).coerceAtLeast(1)

                            // Compute points with harmonic pulse wave modulation for fluid continuous flow
                            val points = dataPoints.mapIndexed { i, value ->
                                val x = i * stepX
                                val norm = (value / maxVal).coerceIn(0f, 1f)
                                // Add continuous phase harmonic wave
                                val harmonic = kotlin.math.sin((wavePhase + (i * 0.45f)).toDouble()).toFloat() * 3.dp.toPx()
                                val y = (canvasHeight - (norm * (canvasHeight * 0.72f)) - (canvasHeight * 0.12f) + harmonic)
                                    .coerceIn(6.dp.toPx(), canvasHeight - 6.dp.toPx())
                                Offset(x, y)
                            }

                            // Build smooth cubic bezier curve
                            wavePath.moveTo(points.first().x, points.first().y)
                            fillPath.moveTo(points.first().x, canvasHeight)
                            fillPath.lineTo(points.first().x, points.first().y)

                            for (i in 0 until points.size - 1) {
                                val p0 = points[i]
                                val p1 = points[i + 1]
                                val cx = (p0.x + p1.x) / 2f
                                wavePath.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                                fillPath.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                            }

                            fillPath.lineTo(canvasWidth, canvasHeight)
                            fillPath.close()

                            // Translucent Wave Gradient Fill
                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        animatedPrimaryColor.copy(alpha = 0.35f),
                                        animatedSecondaryColor.copy(alpha = 0.08f),
                                        Color.Transparent
                                    )
                                )
                            )

                            // Dynamic Speed-Based Stroke
                            drawPath(
                                path = wavePath,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        animatedSecondaryColor,
                                        animatedPrimaryColor
                                    )
                                ),
                                style = Stroke(
                                    width = 3.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            )

                            // Glowing Tip Indicator Dot at newest telemetry point
                            val lastPoint = points.last()
                            drawCircle(
                                color = animatedPrimaryColor.copy(alpha = 0.35f),
                                radius = 7.dp.toPx(),
                                center = lastPoint
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 3.5.dp.toPx(),
                                center = lastPoint
                            )
                        }
                    }
                }
            }
        }
    }
}
