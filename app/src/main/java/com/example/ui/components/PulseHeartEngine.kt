package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * The 3 distinct biological life states of the Heart Engine tied to 30 consumption levels:
 * - LIVING (Levels 1..15): Full physiological vigor, vibrant liquid aura, pure white to amber.
 * - STRAINING (Levels 16..27): Accelerated rhythm, stress tremors, dimmed aura, amber to coral red.
 * - FROZEN (Levels 28..30): Flatlined, rigid shape, crystal ice glass shards, extinguished aura.
 */
enum class BiologicalHeartState {
    LIVING,
    STRAINING,
    FROZEN
}

/**
 * Calculated parameters for the biological heart at any consumption level.
 */
data class BiologicalHeartParams(
    val level: Int,                   // 1..30
    val biologicalState: BiologicalHeartState,
    val primaryColor: Color,          // Core body color
    val secondaryColor: Color,        // Shading / gradient tone
    val auraColor: Color,             // Radial backplate liquid glow
    val cycleDurationMs: Int,         // Heartbeat tempo duration
    val strainFactor: Float,          // 0f (calm) -> 1f (severe strain)
    val freezeFactor: Float,          // 0f (fully fluid) -> 1f (completely frozen)
    val stateTitle: String            // Diagnostic state label
)

/**
 * 30-Level Biological Heart Calculator.
 * Maps monthly data usage percentage (0% to 100%) into 30 levels and 3 biological states.
 */
object BiologicalHeartCalculator {
    private val COLOR_PURE_WHITE = Color(0xFFFFFFFF)
    private val COLOR_NEON_CYAN = Color(0xFF00F5D4)
    private val COLOR_SOFT_AMBER = Color(0xFFFFB703)
    private val COLOR_VIBRANT_ORANGE = Color(0xFFFF7A00)
    private val COLOR_CORAL_RED = Color(0xFFFF3366)
    private val COLOR_FROST_CRIMSON = Color(0xFF4A0E17)
    private val COLOR_FROST_CYAN = Color(0xFFB2EBF2)
    private val COLOR_ICE_GRAY = Color(0xFF90A4AE)

    fun calculate(percentage: Float, isArabic: Boolean = false): BiologicalHeartParams {
        val clamped = percentage.coerceIn(0f, 100f)
        val fraction = clamped / 100f
        val levelFloat = (1f + fraction * 29f).coerceIn(1f, 30f)
        val level = levelFloat.roundToInt().coerceIn(1, 30)

        val biologicalState = when {
            level <= 15 -> BiologicalHeartState.LIVING
            level <= 27 -> BiologicalHeartState.STRAINING
            else -> BiologicalHeartState.FROZEN
        }

        // Smooth continuous color interpolation across the 30-tier gradient
        val primaryColor = when {
            levelFloat <= 5f -> {
                val t = (levelFloat - 1f) / 4f
                lerp(COLOR_PURE_WHITE, COLOR_NEON_CYAN, t.coerceIn(0f, 1f))
            }
            levelFloat <= 15f -> {
                val t = (levelFloat - 5f) / 10f
                lerp(COLOR_NEON_CYAN, COLOR_SOFT_AMBER, t.coerceIn(0f, 1f))
            }
            levelFloat <= 22f -> {
                val t = (levelFloat - 15f) / 7f
                lerp(COLOR_SOFT_AMBER, COLOR_VIBRANT_ORANGE, t.coerceIn(0f, 1f))
            }
            levelFloat <= 27f -> {
                val t = (levelFloat - 22f) / 5f
                lerp(COLOR_VIBRANT_ORANGE, COLOR_CORAL_RED, t.coerceIn(0f, 1f))
            }
            else -> {
                val t = (levelFloat - 27f) / 3f
                lerp(COLOR_CORAL_RED, COLOR_FROST_CRIMSON, t.coerceIn(0f, 1f))
            }
        }

        val secondaryColor = when (biologicalState) {
            BiologicalHeartState.LIVING -> lerp(primaryColor, COLOR_NEON_CYAN, 0.40f)
            BiologicalHeartState.STRAINING -> lerp(primaryColor, COLOR_CORAL_RED, 0.45f)
            BiologicalHeartState.FROZEN -> COLOR_ICE_GRAY
        }

        // Freeze factor: 0.0 at <27.5, ramping smoothly to 1.0 at level 30
        val freezeFactor = if (levelFloat < 27.5f) {
            0f
        } else {
            ((levelFloat - 27.5f) / 2.5f).coerceIn(0f, 1f)
        }

        // Strain factor: 0.0 at <=15, ramping to 1.0 at level 27
        val strainFactor = if (levelFloat <= 15f) {
            0f
        } else if (levelFloat <= 27f) {
            ((levelFloat - 15f) / 12f).coerceIn(0f, 1f)
        } else {
            1f
        }

        // Dynamic heartbeat tempo:
        // Living: Relaxed 1400ms down to 1050ms
        // Straining: Tachycardic 1000ms down to 500ms
        // Frozen: Flatline locked
        val cycleDurationMs = when (biologicalState) {
            BiologicalHeartState.LIVING -> {
                val t = (levelFloat - 1f) / 14f
                (1400f - t * 350f).roundToInt()
            }
            BiologicalHeartState.STRAINING -> {
                val t = (levelFloat - 15f) / 12f
                (1000f - t * 500f).roundToInt()
            }
            BiologicalHeartState.FROZEN -> 10000 // Infinite flatline
        }

        val auraColor = when (biologicalState) {
            BiologicalHeartState.LIVING -> primaryColor.copy(alpha = 0.45f)
            BiologicalHeartState.STRAINING -> primaryColor.copy(alpha = (0.38f - strainFactor * 0.18f).coerceAtLeast(0.12f))
            BiologicalHeartState.FROZEN -> COLOR_FROST_CYAN.copy(alpha = (0.12f * (1f - freezeFactor)).coerceAtLeast(0.02f))
        }

        val stateTitle = if (isArabic) {
            when (biologicalState) {
                BiologicalHeartState.LIVING -> "نبض حيوي نشط"
                BiologicalHeartState.STRAINING -> "نبض تحت الإجهاد"
                BiologicalHeartState.FROZEN -> "متجمد / نفاد الباقة"
            }
        } else {
            when (biologicalState) {
                BiologicalHeartState.LIVING -> "Active Living"
                BiologicalHeartState.STRAINING -> "Strained"
                BiologicalHeartState.FROZEN -> "Frozen Depleted"
            }
        }

        return BiologicalHeartParams(
            level = level,
            biologicalState = biologicalState,
            primaryColor = primaryColor,
            secondaryColor = secondaryColor,
            auraColor = auraColor,
            cycleDurationMs = cycleDurationMs,
            strainFactor = strainFactor,
            freezeFactor = freezeFactor,
            stateTitle = stateTitle
        )
    }
}

/**
 * Dynamic "Liquid Glass" Aura Overlay that reacts directly to heartbeat frequency
 * and systolic cardiac contraction.
 *
 * Utilizes hardware-accelerated blur shader (RenderEffect) to mimic deep-glass refraction:
 * 1. Deep Refraction Subsurface: Multi-pass high-radius blur diffusing light through a volumetric glass volume.
 * 2. Frequency-Reactive Lens: As heart frequency accelerates (from 0.7Hz to 2.0Hz under strain),
 *    the glass dispersion expands, caustic rings tighten, and chromatic ripples accelerate.
 * 3. Specular Caustic Edges: Curvature highlights simulating light focusing through a convex glass dome.
 * 4. Systolic Shockwave Caustics: Dynamic expanding wavefront emitted with each heartbeat pulse.
 * 5. Cryo-Glass Frosted Dispersion: Shifts from liquid glass to frosted crystalline ice haze when frozen.
 */
@Composable
fun LiquidGlassRefractionAura(
    heartParams: BiologicalHeartParams,
    cardiacScale: Float,
    cardiacGlowAlpha: Float,
    cardiacPhase: Float,
    auraSize: Dp,
    isInternetConnected: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedAuraColor by animateColorAsState(
        targetValue = heartParams.auraColor,
        animationSpec = tween(durationMillis = 500),
        label = "aura_liquid_color"
    )
    val animatedSecondaryColor by animateColorAsState(
        targetValue = heartParams.secondaryColor,
        animationSpec = tween(durationMillis = 500),
        label = "aura_secondary_color"
    )

    // Calculate dynamic heartbeat frequency in Hertz (Hz)
    // Range: ~0.71 Hz (calm 1400ms) -> 2.0 Hz (straining 500ms) -> 0.0 Hz (frozen)
    val heartbeatFreqHz = remember(heartParams.cycleDurationMs, heartParams.biologicalState, isInternetConnected) {
        if (!isInternetConnected || heartParams.biologicalState == BiologicalHeartState.FROZEN) {
            0.0f
        } else {
            1000f / heartParams.cycleDurationMs.toFloat()
        }
    }

    // Dynamic blur radius that reacts in real-time to heartbeat frequency and cardiac systole
    // Stronger ventricular contraction produces an instantaneous burst in caustic diffusion
    val frequencyFactor = (heartbeatFreqHz / 0.72f).coerceIn(0.6f, 2.5f)
    val dynamicBlurRadius = (18.dp + (10.dp * (cardiacScale - 1f) * frequencyFactor)).coerceIn(12.dp, 36.dp)

    // Continuous refraction caustics rotation reflecting fluid motion inside glass
    val infiniteTransition = rememberInfiniteTransition(label = "glass_caustics_transition")
    val causticsRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (6000 / frequencyFactor).roundToInt().coerceIn(2000, 7000),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "caustics_rotation_anim"
    )

    // Systolic shockwave ripple: Emitted during ventricular systole (cardiacPhase 0..0.4)
    val shockwavePhase = (cardiacPhase * 1.35f) % 1.0f
    val shockwaveScale = 0.55f + 0.50f * shockwavePhase
    val shockwaveAlpha = ((1f - shockwavePhase) * cardiacGlowAlpha * 0.40f * (1f - heartParams.freezeFactor)).coerceIn(0f, 1f)

    Box(
        modifier = modifier.size(auraSize * 1.35f),
        contentAlignment = Alignment.Center
    ) {
        if (heartParams.freezeFactor < 0.95f) {
            // LAYER 1: Deep-Glass Volumetric Blur Shader (Subsurface Scattering)
            Box(
                modifier = Modifier
                    .size(auraSize)
                    .graphicsLayer {
                        scaleX = cardiacScale * (1f + (heartbeatFreqHz - 0.72f) * 0.05f)
                        scaleY = cardiacScale * (1f + (heartbeatFreqHz - 0.72f) * 0.05f)
                    }
                    .blur(radius = dynamicBlurRadius, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                animatedAuraColor.copy(alpha = (cardiacGlowAlpha * 0.65f * (1f - heartParams.freezeFactor)).coerceIn(0f, 1f)),
                                animatedSecondaryColor.copy(alpha = (cardiacGlowAlpha * 0.35f * (1f - heartParams.freezeFactor)).coerceIn(0f, 1f)),
                                Color.Transparent
                            )
                        )
                    )
            )

            // LAYER 2: Liquid Glass Lens Refraction & Caustic Waves (Reacts to Heartbeat Frequency)
            Canvas(
                modifier = Modifier
                    .size(auraSize)
                    .graphicsLayer {
                        rotationZ = causticsRotation
                        scaleX = cardiacScale
                        scaleY = cardiacScale
                    }
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val baseRadius = size.width * 0.44f

                // Concentric glass refraction rings (Fresnel caustics)
                // Ring 1: Inner chromatic refraction fringe (Cyan/Violet dispersion)
                val chromaticInnerColor = animatedAuraColor.copy(
                    alpha = (cardiacGlowAlpha * 0.38f * (1f - heartParams.freezeFactor)).coerceIn(0f, 1f)
                )
                drawCircle(
                    brush = Brush.sweepGradient(
                        0.0f to chromaticInnerColor,
                        0.35f to animatedSecondaryColor.copy(alpha = chromaticInnerColor.alpha * 0.8f),
                        0.70f to Color.White.copy(alpha = chromaticInnerColor.alpha * 0.5f),
                        1.0f to chromaticInnerColor
                    ),
                    radius = baseRadius * 0.78f,
                    center = center,
                    style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round)
                )

                // Ring 2: Outer refractive lens perimeter highlight
                val lensRimAlpha = (cardiacGlowAlpha * 0.50f * (1f - heartParams.freezeFactor)).coerceIn(0f, 1f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            animatedAuraColor.copy(alpha = lensRimAlpha * 0.35f),
                            Color.White.copy(alpha = lensRimAlpha * 0.75f)
                        ),
                        center = center,
                        radius = baseRadius
                    ),
                    radius = baseRadius,
                    center = center,
                    style = Stroke(width = 1.8.dp.toPx())
                )

                // Specular Glass Caustic Glints (Simulating light refraction curves on curved glass)
                val glintAngle = Math.toRadians(causticsRotation.toDouble())
                val glintX = center.x + (baseRadius * 0.85f * kotlin.math.cos(glintAngle)).toFloat()
                val glintY = center.y + (baseRadius * 0.85f * kotlin.math.sin(glintAngle)).toFloat()
                drawCircle(
                    color = Color.White.copy(alpha = (lensRimAlpha * 0.85f).coerceIn(0f, 1f)),
                    radius = 2.2.dp.toPx(),
                    center = Offset(glintX, glintY)
                )
            }

            // LAYER 3: Frequency-Driven Systolic Caustic Shockwave (Emitted with each beat)
            if (isInternetConnected && heartbeatFreqHz > 0.1f) {
                Box(
                    modifier = Modifier
                        .size(auraSize * shockwaveScale)
                        .blur(radius = 8.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    animatedAuraColor.copy(alpha = shockwaveAlpha * 0.6f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        } else {
            // FROZEN STATE: Cryo-Glass Frosted Matte Dispersion Shader
            Box(
                modifier = Modifier
                    .size(auraSize * 0.90f)
                    .blur(radius = 16.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFB2EBF2).copy(alpha = 0.14f),
                                Color(0xFF37474F).copy(alpha = 0.08f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Cold, motionless ice crystal perimeter
            Canvas(modifier = Modifier.size(auraSize * 0.85f)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                drawCircle(
                    color = Color(0xFFE0F7FA).copy(alpha = 0.22f),
                    radius = size.width * 0.42f,
                    center = center,
                    style = Stroke(width = 1.2.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
    }
}

/**
 * Hyper-Realistic Biologically Living & Freezing Heart Engine.
 *
 * Implements:
 * 1. Living State (Levels 1..15): Full physiological Lub-Dub contraction and vibrant liquid-glass aura with deep refraction.
 * 2. Straining State (Levels 16..27): Accelerated tachycardic tempo with irregular stress micro-tremors and dimmed aura.
 * 3. Frozen State (Levels 28..30): Flatlined, rigid shape with crystallizing ice-fractal glass shards and frost crystal overlay.
 */
@Composable
fun BiologicallyLivingAndFreezingHeart(
    percentage: Float,
    isInternetConnected: Boolean,
    appLanguage: AppLanguage,
    modifier: Modifier = Modifier,
    heartSize: Dp = 28.dp,
    auraSize: Dp = 80.dp,
    showTitle: Boolean = true
) {
    val isArabic = (appLanguage == AppLanguage.ARABIC)
    val heartParams = remember(percentage, isArabic) {
        BiologicalHeartCalculator.calculate(percentage, isArabic)
    }

    // Color transitions across consumption levels
    val animatedPrimaryColor by animateColorAsState(
        targetValue = heartParams.primaryColor,
        animationSpec = tween(durationMillis = 500),
        label = "bio_primary_color"
    )
    val animatedSecondaryColor by animateColorAsState(
        targetValue = heartParams.secondaryColor,
        animationSpec = tween(durationMillis = 500),
        label = "bio_secondary_color"
    )

    // Continuous cardiac phase accumulator (0f .. 1f) tied to display vsync
    var cardiacPhase by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isInternetConnected, heartParams.biologicalState, heartParams.cycleDurationMs) {
        var lastNanos = withFrameNanos { it }
        while (true) {
            val currentNanos = withFrameNanos { it }
            val dtSec = (currentNanos - lastNanos) / 1_000_000_000f
            lastNanos = currentNanos

            if (isInternetConnected && heartParams.biologicalState != BiologicalHeartState.FROZEN) {
                val cycleSec = (heartParams.cycleDurationMs / 1000f).coerceAtLeast(0.40f)
                cardiacPhase = (cardiacPhase + (dtSec / cycleSec)) % 1.0f
            }
        }
    }

    // Ambient shimmer for frost crystal facets when freezing
    val infiniteTransition = rememberInfiniteTransition(label = "frost_sparkle")
    val frostGlint by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.90f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "frost_glint_anim"
    )

    // Biological cardiac contraction calculation:
    // Ventricular systole ("Lub-Dub") -> elastic recoil -> diastolic resting pause
    val (cardiacScale, cardiacGlowAlpha) = remember(
        cardiacPhase,
        isInternetConnected,
        heartParams.biologicalState,
        heartParams.strainFactor,
        heartParams.freezeFactor
    ) {
        if (!isInternetConnected) {
            1.0f to 0.10f
        } else if (heartParams.biologicalState == BiologicalHeartState.FROZEN) {
            // Completely flatlined: rigid locked posture, zero beat pulsation
            1.0f to 0.04f
        } else {
            val p = cardiacPhase
            val strain = heartParams.strainFactor
            val strainTremor = if (strain > 0.35f) (sin(p * 28.0).toFloat() * 0.025f * strain) else 0f

            when {
                // 1. "Lub" First Ventricular Contraction
                p < 0.15f -> {
                    val t = p / 0.15f
                    val s = 1.00f + (0.16f + 0.08f * strain) * FastOutSlowInEasing.transform(t) + strainTremor
                    val g = 0.22f + 0.45f * FastOutSlowInEasing.transform(t)
                    s to g
                }
                // 2. Inter-systolic recoil
                p < 0.26f -> {
                    val t = (p - 0.15f) / 0.11f
                    val s = 1.16f - 0.11f * FastOutSlowInEasing.transform(t) + strainTremor
                    val g = 0.67f - 0.28f * FastOutSlowInEasing.transform(t)
                    s to g
                }
                // 3. "Dub" Second Major Contraction (Straining beats strike harder)
                p < 0.40f -> {
                    val t = (p - 0.26f) / 0.14f
                    val s = 1.05f + (0.22f + 0.10f * strain) * FastOutSlowInEasing.transform(t) + strainTremor
                    val g = 0.38f + 0.52f * FastOutSlowInEasing.transform(t)
                    s to g
                }
                // 4. Elastic recoil
                p < 0.54f -> {
                    val t = (p - 0.40f) / 0.14f
                    val s = 1.27f - 0.20f * FastOutSlowInEasing.transform(t) + strainTremor
                    val g = 0.90f - 0.48f * FastOutSlowInEasing.transform(t)
                    s to g
                }
                // 5. Diastolic relaxation
                p < 0.68f -> {
                    val t = (p - 0.54f) / 0.14f
                    val s = 1.07f - 0.07f * FastOutSlowInEasing.transform(t)
                    val g = 0.42f - 0.24f * FastOutSlowInEasing.transform(t)
                    s to g
                }
                // 6. Resting diastolic pause (Shortened drastically under strain)
                else -> {
                    1.00f to (0.18f - 0.08f * strain).coerceAtLeast(0.08f)
                }
            }
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // 1. Dynamic Liquid-Glass Refraction Aura Overlay (Reacts to Heartbeat Frequency)
        LiquidGlassRefractionAura(
            heartParams = heartParams,
            cardiacScale = cardiacScale,
            cardiacGlowAlpha = cardiacGlowAlpha,
            cardiacPhase = cardiacPhase,
            auraSize = auraSize,
            isInternetConnected = isInternetConnected
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 2. Beating / Freezing Heart Vector Canvas with Ice Crystal Overlay
            Box(
                modifier = Modifier.graphicsLayer {
                    scaleX = cardiacScale
                    scaleY = cardiacScale
                },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(heartSize)) {
                    val width = size.width
                    val height = size.height

                    // A. Anatomical Heart Base Path
                    val heartPath = Path().apply {
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

                    // Base Fill: Dynamic Living Gradient OR Frozen Crimson/Ice Shading
                    val fillBrush = if (heartParams.freezeFactor > 0.01f) {
                        val frozenColor = lerp(animatedPrimaryColor, Color(0xFF3A0D15), heartParams.freezeFactor)
                        val frostAccent = lerp(animatedSecondaryColor, Color(0xFF78909C), heartParams.freezeFactor)
                        Brush.verticalGradient(listOf(frozenColor, frostAccent))
                    } else {
                        Brush.verticalGradient(
                            listOf(
                                animatedPrimaryColor,
                                animatedSecondaryColor.copy(alpha = 0.85f)
                            )
                        )
                    }
                    drawPath(path = heartPath, brush = fillBrush)

                    // B. Freezing Ice Glass Overlay (Levels 28..30)
                    if (heartParams.freezeFactor > 0.05f) {
                        val frostAlpha = heartParams.freezeFactor.coerceIn(0f, 1f)
                        val iceLineColor = Color(0xFFE0F7FA).copy(alpha = (0.75f * frostAlpha * frostGlint).coerceIn(0f, 1f))
                        val iceShardFill = Color(0xFFFFFFFF).copy(alpha = (0.22f * frostAlpha).coerceIn(0f, 1f))

                        // Frosted Glass Crystalline Fractures & Crystal Shards
                        val crystalPath1 = Path().apply {
                            moveTo(width * 0.50f, height * 0.28f)
                            lineTo(width * 0.42f, height * 0.48f)
                            lineTo(width * 0.28f, height * 0.54f)
                            lineTo(width * 0.38f, height * 0.65f)
                            lineTo(width * 0.50f, height * 0.82f)
                        }
                        drawPath(
                            path = crystalPath1,
                            color = iceLineColor,
                            style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round)
                        )

                        val crystalPath2 = Path().apply {
                            moveTo(width * 0.42f, height * 0.48f)
                            lineTo(width * 0.62f, height * 0.40f)
                            lineTo(width * 0.72f, height * 0.58f)
                            lineTo(width * 0.50f, height * 0.70f)
                        }
                        drawPath(
                            path = crystalPath2,
                            color = iceLineColor.copy(alpha = iceLineColor.alpha * 0.80f),
                            style = Stroke(width = 1.1.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Top-left ice specular reflection facet
                        val iceFacet = Path().apply {
                            moveTo(width * 0.25f, height * 0.15f)
                            lineTo(width * 0.40f, height * 0.22f)
                            lineTo(width * 0.30f, height * 0.35f)
                            close()
                        }
                        drawPath(path = iceFacet, color = iceShardFill)

                        // Subtle frost edge perimeter
                        drawPath(
                            path = heartPath,
                            color = Color(0xFFB2EBF2).copy(alpha = 0.50f * frostAlpha),
                            style = Stroke(width = 1.2.dp.toPx())
                        )
                    }
                }
            }

            if (showTitle) {
                Spacer(modifier = Modifier.height(2.dp))

                // 3. Central "Pulse" Title with Dynamic Glow / Frost Glass Style
                val titleColor = if (heartParams.freezeFactor > 0.6f) {
                    lerp(animatedPrimaryColor, Color(0xFFCFD8DC), heartParams.freezeFactor)
                } else {
                    animatedPrimaryColor
                }

                Text(
                    text = if (heartParams.freezeFactor > 0.85f) "Frozen" else "Pulse",
                    color = titleColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

/**
 * Backward-compatible drop-in aliases.
 */
@Composable
fun PulseHeart(
    statusColor: Color = Color(0xFF00F5D4),
    isInternetConnected: Boolean = true,
    modifier: Modifier = Modifier,
    heartSize: Dp = 28.dp,
    auraSize: Dp = 80.dp,
    showTitle: Boolean = true
) {
    BiologicallyLivingAndFreezingHeart(
        percentage = 0f,
        isInternetConnected = isInternetConnected,
        appLanguage = AppLanguage.ARABIC,
        modifier = modifier,
        heartSize = heartSize,
        auraSize = auraSize,
        showTitle = showTitle
    )
}

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
    BiologicallyLivingAndFreezingHeart(
        percentage = percentage,
        isInternetConnected = isInternetConnected,
        appLanguage = appLanguage,
        modifier = modifier,
        heartSize = heartSize,
        auraSize = auraSize,
        showTitle = true
    )
}
