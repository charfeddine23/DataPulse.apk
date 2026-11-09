package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricBlue

/**
 * Clean, organic pulsating heart component for the Data Pulse dashboard.
 * Provides a soothing, rhythmic heartbeat animation without arbitrary levels or tiers.
 */
@Composable
fun PulseHeart(
    statusColor: Color = CyanPulse,
    isInternetConnected: Boolean = true,
    modifier: Modifier = Modifier,
    heartSize: Dp = 28.dp,
    auraSize: Dp = 80.dp,
    showTitle: Boolean = true
) {
    val animatedColor by animateColorAsState(
        targetValue = statusColor,
        animationSpec = tween(durationMillis = 500),
        label = "pulse_heart_color"
    )

    // Continuous cardiac phase accumulator (0f .. 1f)
    var cardiacPhase by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isInternetConnected) {
        var lastNanos = withFrameNanos { it }
        while (true) {
            val currentNanos = withFrameNanos { it }
            val dtSec = (currentNanos - lastNanos) / 1_000_000_000f
            lastNanos = currentNanos

            if (isInternetConnected) {
                // Natural, relaxing cardiac tempo (~72 bpm, 1.35 seconds per cycle)
                val cycleSec = 1.35f
                cardiacPhase = (cardiacPhase + (dtSec / cycleSec)) % 1.0f
            }
        }
    }

    // Physiological cardiac curve calculation:
    // Ventricular systole ("Lub-Dub") -> elastic recoil -> diastolic resting pause
    val (cardiacScale, cardiacGlowAlpha) = remember(cardiacPhase, isInternetConnected) {
        if (!isInternetConnected) {
            1.0f to 0.12f
        } else {
            val p = cardiacPhase
            when {
                // 1. "Lub" First Ventricular Contraction (0.00 -> 0.15)
                p < 0.15f -> {
                    val t = p / 0.15f
                    val s = 1.00f + 0.15f * FastOutSlowInEasing.transform(t)
                    val g = 0.20f + 0.40f * FastOutSlowInEasing.transform(t)
                    s to g
                }
                // 2. Inter-systolic recoil (0.15 -> 0.26)
                p < 0.26f -> {
                    val t = (p - 0.15f) / 0.11f
                    val s = 1.15f - 0.10f * FastOutSlowInEasing.transform(t)
                    val g = 0.60f - 0.25f * FastOutSlowInEasing.transform(t)
                    s to g
                }
                // 3. "Dub" Second Major Contraction (0.26 -> 0.40)
                p < 0.40f -> {
                    val t = (p - 0.26f) / 0.14f
                    val s = 1.05f + 0.20f * FastOutSlowInEasing.transform(t)
                    val g = 0.35f + 0.50f * FastOutSlowInEasing.transform(t)
                    s to g
                }
                // 4. Elastic recoil (0.40 -> 0.54)
                p < 0.54f -> {
                    val t = (p - 0.40f) / 0.14f
                    val s = 1.25f - 0.18f * FastOutSlowInEasing.transform(t)
                    val g = 0.85f - 0.45f * FastOutSlowInEasing.transform(t)
                    s to g
                }
                // 5. Diastolic relaxation (0.54 -> 0.68)
                p < 0.68f -> {
                    val t = (p - 0.54f) / 0.14f
                    val s = 1.07f - 0.07f * FastOutSlowInEasing.transform(t)
                    val g = 0.40f - 0.22f * FastOutSlowInEasing.transform(t)
                    s to g
                }
                // 6. Resting diastolic pause (0.68 -> 1.00)
                else -> {
                    1.00f to 0.18f
                }
            }
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // 1. Ambient Radial Backplate Aura
        Box(
            modifier = Modifier
                .size(auraSize)
                .graphicsLayer {
                    scaleX = cardiacScale
                    scaleY = cardiacScale
                }
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            animatedColor.copy(alpha = (cardiacGlowAlpha * 0.40f).coerceIn(0f, 1f)),
                            animatedColor.copy(alpha = (cardiacGlowAlpha * 0.15f).coerceIn(0f, 1f)),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 2. Beating Heart Vector Canvas
            Box(
                modifier = Modifier.graphicsLayer {
                    scaleX = cardiacScale
                    scaleY = cardiacScale
                },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(heartSize)) {
                    val path = Path().apply {
                        val width = size.width
                        val height = size.height
                        moveTo(width / 2f, height * 0.85f)
                        // Left lobe
                        cubicTo(
                            width * 0.12f, height * 0.55f,
                            width * -0.05f, height * 0.22f,
                            width * 0.25f, height * 0.08f
                        )
                        cubicTo(
                            width * 0.42f, height * 0.02f,
                            width * 0.50f, height * 0.25f,
                            width * 0.50f, height * 0.28f
                        )
                        // Right lobe
                        cubicTo(
                            width * 0.50f, height * 0.25f,
                            width * 0.58f, height * 0.02f,
                            width * 0.75f, height * 0.08f
                        )
                        cubicTo(
                            width * 1.05f, height * 0.22f,
                            width * 0.88f, height * 0.55f,
                            width / 2f, height * 0.85f
                        )
                        close()
                    }

                    drawPath(
                        path = path,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                animatedColor,
                                animatedColor.copy(alpha = 0.80f)
                            )
                        )
                    )
                }
            }

            if (showTitle) {
                Spacer(modifier = Modifier.height(2.dp))

                // 3. Central "Pulse" Title with Dynamic Glow
                Text(
                    text = "Pulse",
                    color = animatedColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

/**
 * Backward compatibility wrapper for LivingPulseHeart that routes to PulseHeart
 * with levels removed.
 */
@Composable
fun LivingPulseHeart(
    percentage: Float,
    isInternetConnected: Boolean,
    appLanguage: AppLanguage,
    modifier: Modifier = Modifier,
    heartSize: Dp = 28.dp,
    auraSize: Dp = 80.dp,
    showTierBadge: Boolean = false
) {
    PulseHeart(
        statusColor = CyanPulse,
        isInternetConnected = isInternetConnected,
        modifier = modifier,
        heartSize = heartSize,
        auraSize = auraSize,
        showTitle = true
    )
}
