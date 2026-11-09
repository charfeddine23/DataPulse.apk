package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import kotlin.math.roundToInt

/**
 * Encapsulates the 30-Level Dynamic Consumption-Based Pulse State.
 */
data class PulseTierState(
    val tier: Int,                // 1..30
    val progress: Float,           // 0.0f .. 1.0f
    val primaryColor: Color,       // Reactive heart color
    val secondaryColor: Color,     // Dual-tone companion
    val ambientAuraColor: Color,   // Soft backplate aura
    val tempoDurationMs: Int,      // Accelerated heartbeat duration (2000ms -> 450ms)
    val phaseName: String,         // Zone name
    val urgencyMultiplier: Float   // Scale & glow amplification factor
)

/**
 * 30-Tier Fine-Grained Consumption Calculator with continuous color interpolation (`lerp`).
 *
 * Tier Zones:
 * - Levels 1 to 5: Fresh & Stable (Pure White ⟶ Neon Cyan)
 * - Levels 6 to 12: Moderate Flow (Neon Cyan ⟶ Soft Amber)
 * - Levels 13 to 20: Heavy Usage Zone (Soft Amber ⟶ Vibrant Orange)
 * - Levels 21 to 27: High Alert Warning (Vibrant Orange ⟶ Coral Red)
 * - Levels 28 to 30: Critical Capacity Limit (Coral Red ⟶ Deep Crimson Red)
 */
object PulseEngineCalculator {
    private val COLOR_PURE_WHITE = Color(0xFFFFFFFF)
    private val COLOR_NEON_CYAN = Color(0xFF00F5D4)
    private val COLOR_SOFT_AMBER = Color(0xFFFFB703)
    private val COLOR_VIBRANT_ORANGE = Color(0xFFFF7A00)
    private val COLOR_CORAL_RED = Color(0xFFFF3366)
    private val COLOR_DEEP_CRIMSON = Color(0xFF990000)

    fun calculate(percentage: Float, isArabic: Boolean = false): PulseTierState {
        val clampedPercent = percentage.coerceIn(0f, 100f)
        val fraction = clampedPercent / 100f

        // Continuous mapping across 30 tiers: 1.0f to 30.0f
        val tierFloat = (1f + fraction * 29f).coerceIn(1f, 30f)
        val tier = tierFloat.roundToInt().coerceIn(1, 30)

        // Smooth fine-grained color interpolation without discrete steps
        val primaryColor = when {
            tierFloat <= 5f -> {
                val t = (tierFloat - 1f) / 4f
                androidx.compose.ui.graphics.lerp(COLOR_PURE_WHITE, COLOR_NEON_CYAN, t.coerceIn(0f, 1f))
            }
            tierFloat <= 12f -> {
                val t = (tierFloat - 5f) / 7f
                androidx.compose.ui.graphics.lerp(COLOR_NEON_CYAN, COLOR_SOFT_AMBER, t.coerceIn(0f, 1f))
            }
            tierFloat <= 20f -> {
                val t = (tierFloat - 12f) / 8f
                androidx.compose.ui.graphics.lerp(COLOR_SOFT_AMBER, COLOR_VIBRANT_ORANGE, t.coerceIn(0f, 1f))
            }
            tierFloat <= 27f -> {
                val t = (tierFloat - 20f) / 7f
                androidx.compose.ui.graphics.lerp(COLOR_VIBRANT_ORANGE, COLOR_CORAL_RED, t.coerceIn(0f, 1f))
            }
            else -> {
                val t = (tierFloat - 27f) / 3f
                androidx.compose.ui.graphics.lerp(COLOR_CORAL_RED, COLOR_DEEP_CRIMSON, t.coerceIn(0f, 1f))
            }
        }

        val secondaryColor = when {
            tierFloat <= 5f -> androidx.compose.ui.graphics.lerp(primaryColor, COLOR_NEON_CYAN, 0.45f)
            tierFloat <= 12f -> androidx.compose.ui.graphics.lerp(primaryColor, COLOR_SOFT_AMBER, 0.45f)
            tierFloat <= 20f -> androidx.compose.ui.graphics.lerp(primaryColor, COLOR_VIBRANT_ORANGE, 0.45f)
            tierFloat <= 27f -> androidx.compose.ui.graphics.lerp(primaryColor, COLOR_CORAL_RED, 0.45f)
            else -> androidx.compose.ui.graphics.lerp(primaryColor, COLOR_DEEP_CRIMSON, 0.45f)
        }

        // Dynamic pulse tempo scaling from 2000ms at level 1 down to urgent 450ms at level 30
        val tempoFactor = (tierFloat - 1f) / 29f
        val tempoDurationMs = (2000f - tempoFactor * (2000f - 450f)).roundToInt()

        // Urgency multiplier for cardiac contraction strength
        val urgencyMultiplier = 1.0f + (tempoFactor * 0.28f)

        val phaseName = if (isArabic) {
            when {
                tier <= 5 -> "مستقر ونقي"
                tier <= 12 -> "تدفق معتدل"
                tier <= 20 -> "استهلاك نشط"
                tier <= 27 -> "تنبيه مرتفع"
                else -> "حد حرج"
            }
        } else {
            when {
                tier <= 5 -> "Stable & Pure"
                tier <= 12 -> "Moderate Flow"
                tier <= 20 -> "Heavy Zone"
                tier <= 27 -> "High Alert"
                else -> "Critical Limit"
            }
        }

        return PulseTierState(
            tier = tier,
            progress = fraction,
            primaryColor = primaryColor,
            secondaryColor = secondaryColor,
            ambientAuraColor = primaryColor.copy(alpha = 0.30f),
            tempoDurationMs = tempoDurationMs,
            phaseName = phaseName,
            urgencyMultiplier = urgencyMultiplier
        )
    }
}

/**
 * Living, breathing 30-level dynamic Pulse Heart component.
 * Uses a phase accumulator tied to display vsync (`withFrameNanos`) to ensure
 * tempo changes transition with zero phase jumps or stuttering at 60fps/120fps.
 */
@Composable
fun LivingPulseHeart(
    percentage: Float,
    isInternetConnected: Boolean,
    appLanguage: AppLanguage,
    modifier: Modifier = Modifier,
    heartSize: Dp = 28.dp,
    auraSize: Dp = 78.dp,
    showTierBadge: Boolean = true
) {
    val isArabic = (appLanguage == AppLanguage.ARABIC)
    val tierState = remember(percentage, isArabic) {
        PulseEngineCalculator.calculate(percentage, isArabic)
    }

    // Smoothly animated primary and secondary colors across tier boundaries
    val animatedPrimaryColor by animateColorAsState(
        targetValue = tierState.primaryColor,
        animationSpec = tween(durationMillis = 600),
        label = "pulse_primary_color"
    )

    val animatedSecondaryColor by animateColorAsState(
        targetValue = tierState.secondaryColor,
        animationSpec = tween(durationMillis = 600),
        label = "pulse_secondary_color"
    )

    // Continuous cardiac phase accumulator (0f .. 1f)
    var cardiacPhase by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isInternetConnected, tierState.tempoDurationMs) {
        var lastNanos = withFrameNanos { it }
        while (true) {
            val currentNanos = withFrameNanos { it }
            val dtSec = (currentNanos - lastNanos) / 1_000_000_000f
            lastNanos = currentNanos

            if (isInternetConnected) {
                val cycleSec = (tierState.tempoDurationMs / 1000f).coerceAtLeast(0.35f)
                cardiacPhase = (cardiacPhase + (dtSec / cycleSec)) % 1.0f
            }
        }
    }

    // Physiological cardiac curve calculation:
    // Ventricular systole ("Lub-Dub") -> elastic recoil -> diastolic resting pause
    val (cardiacScale, cardiacGlowAlpha) = remember(cardiacPhase, isInternetConnected, tierState.urgencyMultiplier) {
        if (!isInternetConnected) {
            1.0f to 0.12f
        } else {
            val p = cardiacPhase
            val mult = tierState.urgencyMultiplier
            when {
                // 1. "Lub" First Ventricular Contraction (0.00 -> 0.14)
                p < 0.14f -> {
                    val t = p / 0.14f
                    val s = 1.00f + (0.16f * mult) * FastOutSlowInEasing.transform(t)
                    val g = 0.20f + 0.45f * FastOutSlowInEasing.transform(t)
                    s to g
                }
                // 2. Inter-systolic recoil (0.14 -> 0.24)
                p < 0.24f -> {
                    val t = (p - 0.14f) / 0.10f
                    val s = (1.00f + 0.16f * mult) - (0.12f * mult) * FastOutSlowInEasing.transform(t)
                    val g = 0.65f - 0.30f * FastOutSlowInEasing.transform(t)
                    s to g
                }
                // 3. "Dub" Second Major Contraction (0.24 -> 0.38)
                p < 0.38f -> {
                    val t = (p - 0.24f) / 0.14f
                    val s = (1.04f) + (0.24f * mult) * FastOutSlowInEasing.transform(t)
                    val g = 0.35f + 0.55f * FastOutSlowInEasing.transform(t)
                    s to g
                }
                // 4. Elastic recoil (0.38 -> 0.50)
                p < 0.50f -> {
                    val t = (p - 0.38f) / 0.12f
                    val s = (1.04f + 0.24f * mult) - (0.18f * mult) * FastOutSlowInEasing.transform(t)
                    val g = 0.90f - 0.50f * FastOutSlowInEasing.transform(t)
                    s to g
                }
                // 5. Diastolic relaxation (0.50 -> 0.64)
                p < 0.64f -> {
                    val t = (p - 0.50f) / 0.14f
                    val s = 1.10f - 0.10f * FastOutSlowInEasing.transform(t)
                    val g = 0.40f - 0.22f * FastOutSlowInEasing.transform(t)
                    s to g
                }
                // 6. Resting diastolic pause (0.64 -> 1.00)
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
        // 1. Ambient Radial Backplate Aura (Pulsing synchronously with tier color)
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
                            animatedPrimaryColor.copy(alpha = (cardiacGlowAlpha * 0.45f).coerceIn(0f, 1f)),
                            animatedSecondaryColor.copy(alpha = (cardiacGlowAlpha * 0.22f).coerceIn(0f, 1f)),
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
                                animatedPrimaryColor,
                                animatedSecondaryColor.copy(alpha = 0.85f)
                            )
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // 3. Central "Pulse" Title with Dynamic Glow
            Text(
                text = "Pulse",
                color = animatedPrimaryColor,
                fontSize = 21.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            // 4. Subtle 30-Level Status Pill Badge
            if (showTierBadge) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(animatedPrimaryColor.copy(alpha = 0.14f))
                        .border(0.8.dp, animatedPrimaryColor.copy(alpha = 0.40f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 1.5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(animatedPrimaryColor)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isArabic) "المرحلة ${tierState.tier}/30" else "Tier ${tierState.tier}/30",
                            color = animatedPrimaryColor,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
