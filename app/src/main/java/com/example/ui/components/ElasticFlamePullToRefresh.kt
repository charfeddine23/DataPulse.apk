package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

/**
 * State machine representing the lifecycle of the Elastic Flame Pull-to-Refresh.
 */
enum class ElasticFlameRefreshState {
    IDLE,
    PULLING,
    SNAP_RELEASED,
    SYNCING,
    COMPLETED
}

/**
 * Custom, cinematic Elastic Flame Pull-to-Refresh container.
 * Features rubber-band tension physics, anchor-pinned teardrop stretching,
 * fluid bezier curve deformation, automatic spring retraction to 0f after sync,
 * and glowing "أفلت للمزامنة" Arabic text hint upon reaching the release threshold.
 */
@Composable
fun ElasticFlamePullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    refreshThreshold: Dp = 88.dp,
    maxDragDistance: Dp = 150.dp,
    restingSyncOffset: Dp = 68.dp,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val hapticFeedback = LocalHapticFeedback.current

    val thresholdPx = with(density) { refreshThreshold.toPx() }
    val maxDragPx = with(density) { maxDragDistance.toPx() }
    val restingSyncOffsetPx = with(density) { restingSyncOffset.toPx() }

    var refreshState by remember { mutableStateOf(ElasticFlameRefreshState.IDLE) }
    val pullDistanceAnim = remember { Animatable(0f) }
    var rawDragDistance by remember { mutableFloatStateOf(0f) }
    var hasTriggeredHaptic by remember { mutableStateOf(false) }

    // Unified release handler: handles finger release gracefully
    fun handleRelease() {
        if (refreshState == ElasticFlameRefreshState.SYNCING) {
            // Already syncing: make sure offset rests at restingSyncOffsetPx
            if (pullDistanceAnim.value > restingSyncOffsetPx) {
                coroutineScope.launch {
                    pullDistanceAnim.animateTo(
                        targetValue = restingSyncOffsetPx,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )
                }
            }
            return
        }

        if (refreshState == ElasticFlameRefreshState.COMPLETED) {
            return
        }

        val currentPull = pullDistanceAnim.value
        if (currentPull <= 1f) {
            refreshState = ElasticFlameRefreshState.IDLE
            rawDragDistance = 0f
            hasTriggeredHaptic = false
            return
        }

        if (currentPull >= thresholdPx) {
            // Reached or exceeded threshold: transition to SYNCING and trigger refresh
            refreshState = ElasticFlameRefreshState.SYNCING
            coroutineScope.launch {
                pullDistanceAnim.animateTo(
                    targetValue = restingSyncOffsetPx,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            }
            onRefresh()
        } else {
            // Released below threshold: smoothly retract flame back to 0f
            refreshState = ElasticFlameRefreshState.IDLE
            coroutineScope.launch {
                pullDistanceAnim.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
                rawDragDistance = 0f
                hasTriggeredHaptic = false
            }
        }
    }

    // Automatically retract the flame when synchronization finishes (isRefreshing becomes false)
    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) {
            if (refreshState == ElasticFlameRefreshState.SYNCING ||
                refreshState == ElasticFlameRefreshState.SNAP_RELEASED ||
                pullDistanceAnim.value > 0.5f
            ) {
                refreshState = ElasticFlameRefreshState.COMPLETED
                // Brief 100ms completion flare, then smoothly retract to 0f
                delay(100)
                pullDistanceAnim.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
                refreshState = ElasticFlameRefreshState.IDLE
                rawDragDistance = 0f
                hasTriggeredHaptic = false
            }
        } else {
            // If isRefreshing became true and user actually pulled the flame
            if (refreshState != ElasticFlameRefreshState.SYNCING && pullDistanceAnim.value > 10f) {
                refreshState = ElasticFlameRefreshState.SYNCING
                pullDistanceAnim.animateTo(
                    targetValue = restingSyncOffsetPx,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            }
        }
    }

    // Safety watchdog timer: If syncing takes longer than 3.5 seconds, automatically retract to 0f
    LaunchedEffect(refreshState) {
        if (refreshState == ElasticFlameRefreshState.SYNCING) {
            delay(3500)
            if (refreshState == ElasticFlameRefreshState.SYNCING) {
                pullDistanceAnim.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
                refreshState = ElasticFlameRefreshState.IDLE
                rawDragDistance = 0f
                hasTriggeredHaptic = false
            }
        }
    }

    // High-performance NestedScrollConnection to intercept overscroll pull gestures at top
    val nestedScrollConnection = remember(thresholdPx, maxDragPx, restingSyncOffsetPx) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // If user drags upwards while flame is extended, consume drag to retract flame
                if (available.y < 0f && pullDistanceAnim.value > 0f && refreshState != ElasticFlameRefreshState.SYNCING) {
                    val currentVal = pullDistanceAnim.value
                    val delta = available.y
                    val newTarget = (currentVal + delta).coerceAtLeast(0f)
                    val consumedY = newTarget - currentVal

                    coroutineScope.launch {
                        pullDistanceAnim.snapTo(newTarget)
                    }

                    rawDragDistance = (rawDragDistance + delta).coerceAtLeast(0f)
                    if (newTarget <= 0f && refreshState == ElasticFlameRefreshState.PULLING) {
                        refreshState = ElasticFlameRefreshState.IDLE
                        hasTriggeredHaptic = false
                    }
                    return Offset(0f, consumedY)
                }
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // When dragging down at top of scrollable content (available.y > 0)
                if (available.y > 0f && refreshState != ElasticFlameRefreshState.SYNCING) {
                    refreshState = ElasticFlameRefreshState.PULLING

                    // Calculate high-tension rubber-band resistance curve (~0.5s tension feel)
                    val dragFraction = (pullDistanceAnim.value / maxDragPx).coerceIn(0f, 1f)
                    val resistanceMultiplier = 0.55f * (1f - dragFraction).pow(1.8f).coerceAtLeast(0.12f)
                    val incrementalPull = available.y * resistanceMultiplier

                    rawDragDistance += available.y
                    val newDistance = (pullDistanceAnim.value + incrementalPull).coerceAtMost(maxDragPx)

                    // Trigger haptic tick as soon as threshold is crossed
                    if (newDistance >= thresholdPx && !hasTriggeredHaptic) {
                        hasTriggeredHaptic = true
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    } else if (newDistance < thresholdPx && hasTriggeredHaptic) {
                        hasTriggeredHaptic = false
                    }

                    coroutineScope.launch {
                        pullDistanceAnim.snapTo(newDistance)
                    }

                    return Offset(0f, available.y)
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                handleRelease()
                return Velocity.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                handleRelease()
                return Velocity.Zero
            }
        }
    }

    // Infinite animation loop driving organic flame licking, spark particles & turbulence
    val infiniteTransition = rememberInfiniteTransition(label = "flame_turbulence")
    val flamePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val corePulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "core_pulse"
    )

    val currentOffset = pullDistanceAnim.value
    val stretchProgress = (currentOffset / thresholdPx).coerceIn(0f, 1.8f)

    // "أفلت للمزامنة" appears ONLY when user reaches/exceeds the threshold
    val isPrimedForRelease = (stretchProgress >= 1.0f && refreshState == ElasticFlameRefreshState.PULLING)
    val primedAlpha by animateFloatAsState(
        targetValue = if (isPrimedForRelease) 1f else 0f,
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "primed_text_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
            // Multi-pointer detection with PointerEventPass.Initial ensures immediate release detection
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val down = awaitFirstDown(requireUnconsumed = false, pass = androidx.compose.ui.input.pointer.PointerEventPass.Initial)
                        val pointerId = down.id
                        while (true) {
                            val event = awaitPointerEvent(androidx.compose.ui.input.pointer.PointerEventPass.Initial)
                            val change = event.changes.firstOrNull { it.id == pointerId }
                            if (change == null || !change.pressed) {
                                handleRelease()
                                break
                            }
                        }
                    }
                }
            }
    ) {
        // Content smoothly translated down with GPU hardware layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationY = when (refreshState) {
                        ElasticFlameRefreshState.IDLE -> 0f
                        ElasticFlameRefreshState.PULLING -> currentOffset * 0.82f
                        ElasticFlameRefreshState.SNAP_RELEASED,
                        ElasticFlameRefreshState.SYNCING -> restingSyncOffsetPx * 0.82f
                        ElasticFlameRefreshState.COMPLETED -> currentOffset * 0.82f
                    }
                }
        ) {
            content()
        }

        // Custom Elastic Flame Canvas pinned strictly at top center
        // Only drawn when currentOffset > 1f, completely hidden when returned to 0
        if (currentOffset > 1f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(maxDragDistance + 40.dp)
                    .align(Alignment.TopCenter)
            ) {
                ElasticFlameCanvas(
                    currentOffsetPx = currentOffset,
                    stretchProgress = stretchProgress,
                    state = refreshState,
                    phase = flamePhase,
                    corePulse = corePulse,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Sleek Arabic Text Hint: ONLY "أفلت للمزامنة" when primed past the threshold
        if (primedAlpha > 0.01f) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .graphicsLayer {
                        val tipPos = currentOffset + with(density) { 14.dp.toPx() }
                        translationY = tipPos
                        alpha = primedAlpha
                    }
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xD9080D1A))
                    .border(
                        1.dp,
                        Brush.horizontalGradient(
                            colors = listOf(
                                CyanPulse.copy(alpha = 0.5f * primedAlpha),
                                Color(0xFFFF9100).copy(alpha = 0.7f * primedAlpha),
                                CyanPulse.copy(alpha = 0.5f * primedAlpha)
                            )
                        ),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "أفلت للمزامنة",
                    color = Color(0xFFF1F8FF),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(
                        shadow = Shadow(
                            color = CyanPulse.copy(alpha = 0.85f * primedAlpha),
                            offset = Offset(0f, 0f),
                            blurRadius = 12f
                        )
                    )
                )
            }
        }
    }
}

/**
 * Procedural Flame Graphics Renderer using cubic and quadratic Bezier curves,
 * multi-layered fiery plasma gradients, glowing outer bloom, and dynamic spark embers.
 */
@Composable
private fun ElasticFlameCanvas(
    currentOffsetPx: Float,
    stretchProgress: Float,
    state: ElasticFlameRefreshState,
    phase: Float,
    corePulse: Float,
    modifier: Modifier = Modifier
) {
    val flameRuby = Color(0xFFFF1744)
    val flameOrange = Color(0xFFFF6D00)
    val flameAmber = Color(0xFFFFD600)
    val plasmaCyan = CyanPulse
    val plasmaElectric = ElectricBlue
    val incandescentWhite = Color(0xFFFFFFFF)

    Canvas(modifier = modifier) {
        val centerX = size.width / 2f
        val topAnchorY = 0f // Strictly stuck to top boundary

        if (state == ElasticFlameRefreshState.SYNCING || state == ElasticFlameRefreshState.COMPLETED) {
            // =========================================================================
            // ACTIVE SYNCING STATE: Upright Hovering Plasma Flame & Energy Vortex
            // =========================================================================
            val restingY = currentOffsetPx.coerceAtLeast(40.dp.toPx())
            val flameHeight = 46.dp.toPx() + 6.dp.toPx() * sin(phase * 2f)
            val flameBaseY = restingY
            val flameTipY = flameBaseY - flameHeight

            // 1. Ambient Bloom Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        flameOrange.copy(alpha = 0.35f * corePulse),
                        plasmaCyan.copy(alpha = 0.15f * corePulse),
                        Color.Transparent
                    ),
                    center = Offset(centerX, flameBaseY - flameHeight * 0.45f),
                    radius = flameHeight * 1.1f
                ),
                radius = flameHeight * 1.1f,
                center = Offset(centerX, flameBaseY - flameHeight * 0.45f)
            )

            // 2. Main Upright Flickering Flame Tongue
            val mainFlamePath = Path().apply {
                val halfBase = 18.dp.toPx()
                moveTo(centerX - halfBase, flameBaseY)

                // Left curvature to tip with living flicker
                val tipX = centerX + sin(phase * 3f) * 3.5.dp.toPx()
                cubicTo(
                    centerX - halfBase * 1.2f, flameBaseY - flameHeight * 0.4f,
                    centerX - halfBase * 0.5f + cos(phase * 2f) * 3.dp.toPx(), flameBaseY - flameHeight * 0.75f,
                    tipX, flameTipY
                )

                // Right curvature back to base
                cubicTo(
                    centerX + halfBase * 0.5f - cos(phase * 2f) * 3.dp.toPx(), flameBaseY - flameHeight * 0.75f,
                    centerX + halfBase * 1.2f, flameBaseY - flameHeight * 0.4f,
                    centerX + halfBase, flameBaseY
                )

                // Bottom rounded base
                quadraticBezierTo(
                    centerX, flameBaseY + 6.dp.toPx(),
                    centerX - halfBase, flameBaseY
                )
                close()
            }

            drawPath(
                path = mainFlamePath,
                brush = Brush.verticalGradient(
                    colors = listOf(flameAmber, flameOrange, flameRuby),
                    startY = flameTipY,
                    endY = flameBaseY
                )
            )

            // 3. Inner Neon Cyan Plasma Core (Hottest center nucleus)
            val coreHeight = flameHeight * 0.55f * corePulse
            val coreTipY = flameBaseY - coreHeight
            val coreHalfBase = 10.dp.toPx()
            val corePath = Path().apply {
                moveTo(centerX - coreHalfBase, flameBaseY)
                cubicTo(
                    centerX - coreHalfBase * 0.9f, flameBaseY - coreHeight * 0.4f,
                    centerX - coreHalfBase * 0.3f, flameBaseY - coreHeight * 0.75f,
                    centerX + sin(phase * 4f) * 2.dp.toPx(), coreTipY
                )
                cubicTo(
                    centerX + coreHalfBase * 0.3f, flameBaseY - coreHeight * 0.75f,
                    centerX + coreHalfBase * 0.9f, flameBaseY - coreHeight * 0.4f,
                    centerX + coreHalfBase, flameBaseY
                )
                quadraticBezierTo(centerX, flameBaseY + 3.dp.toPx(), centerX - coreHalfBase, flameBaseY)
                close()
            }

            drawPath(
                path = corePath,
                brush = Brush.verticalGradient(
                    colors = listOf(incandescentWhite, plasmaCyan, plasmaElectric),
                    startY = coreTipY,
                    endY = flameBaseY
                )
            )

            // 4. Floating Plasma Embers (Ascending spark particles)
            drawSyncingEmbers(
                centerX = centerX,
                baseY = flameBaseY,
                phase = phase,
                sparkColor = flameAmber,
                cyanColor = plasmaCyan
            )

            // 5. Completion Radiant Flare
            if (state == ElasticFlameRefreshState.COMPLETED) {
                drawCircle(
                    color = incandescentWhite.copy(alpha = 0.75f),
                    radius = 16.dp.toPx(),
                    center = Offset(centerX, flameBaseY - flameHeight * 0.4f)
                )
            }

        } else {
            // =========================================================================
            // PULLING & STRETCHING STATE: Anchor-Pinned Elastic Rubber-Band Deformation
            // =========================================================================
            val stretchL = currentOffsetPx.coerceAtLeast(4.dp.toPx())
            val progressClamped = stretchProgress.coerceIn(0.05f, 1.8f)

            // Top anchor strictly tethered to ceiling
            val tetherHalfWidth = (16.dp.toPx() * (1f - 0.22f * progressClamped.coerceAtMost(1f))).coerceAtLeast(7.dp.toPx())
            val anchorLeft = Offset(centerX - tetherHalfWidth, topAnchorY)
            val anchorRight = Offset(centerX + tetherHalfWidth, topAnchorY)

            // Bottom tip of the stretching flame teardrop
            val tipY = topAnchorY + stretchL
            val tipX = centerX + sin(phase * 4f) * (2.dp.toPx() * progressClamped.coerceAtMost(1f))
            val tipOffset = Offset(tipX, tipY)

            // Elastic waist (narrows as pull distance elongates - volume conservation)
            val waistY = topAnchorY + stretchL * 0.36f
            val waistHalfWidth = (14.dp.toPx() * (1f - 0.48f * progressClamped.coerceAtMost(1f))).coerceAtLeast(4.5.dp.toPx())

            // Lower bulb (droplet belly)
            val bulbY = topAnchorY + stretchL * 0.73f
            val bulbHalfWidth = (19.dp.toPx() * (1f - 0.16f * progressClamped.coerceAtMost(1f))).coerceAtLeast(8.dp.toPx())

            // 1. Soft Outer Bloom Aura
            val glowPath = Path().apply {
                val gTether = tetherHalfWidth * 1.35f
                val gWaist = waistHalfWidth * 1.45f
                val gBulb = bulbHalfWidth * 1.4f
                val gTipY = tipY + 6.dp.toPx()

                moveTo(centerX - gTether, topAnchorY)
                cubicTo(
                    centerX - gTether * 0.95f, topAnchorY + stretchL * 0.14f,
                    centerX - gWaist * 1.15f, waistY - stretchL * 0.08f,
                    centerX - gWaist, waistY
                )
                cubicTo(
                    centerX - gWaist * 0.85f, waistY + (bulbY - waistY) * 0.38f,
                    centerX - gBulb * 1.05f, bulbY - (bulbY - waistY) * 0.25f,
                    centerX - gBulb, bulbY
                )
                cubicTo(
                    centerX - gBulb * 0.9f, bulbY + (gTipY - bulbY) * 0.55f,
                    tipX - 6.dp.toPx(), gTipY - (gTipY - bulbY) * 0.18f,
                    tipX, gTipY
                )
                cubicTo(
                    tipX + 6.dp.toPx(), gTipY - (gTipY - bulbY) * 0.18f,
                    centerX + gBulb * 0.9f, bulbY + (gTipY - bulbY) * 0.55f,
                    centerX + gBulb, bulbY
                )
                cubicTo(
                    centerX + gBulb * 1.05f, bulbY - (bulbY - waistY) * 0.25f,
                    centerX + gWaist * 0.85f, waistY + (bulbY - waistY) * 0.38f,
                    centerX + gWaist, waistY
                )
                cubicTo(
                    centerX + gWaist * 1.15f, waistY - stretchL * 0.08f,
                    centerX + gTether * 0.95f, topAnchorY + stretchL * 0.14f,
                    centerX + gTether, topAnchorY
                )
                close()
            }

            drawPath(
                path = glowPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        flameRuby.copy(alpha = 0.30f * progressClamped.coerceAtMost(1f)),
                        flameOrange.copy(alpha = 0.25f * progressClamped.coerceAtMost(1f)),
                        plasmaCyan.copy(alpha = 0.15f * progressClamped.coerceAtMost(1f)),
                        Color.Transparent
                    ),
                    startY = topAnchorY,
                    endY = tipY + 12.dp.toPx()
                )
            )

            // 2. Primary Elastic Flame Body (Stretching rubber plasma path)
            val elasticFlamePath = Path().apply {
                moveTo(anchorLeft.x, anchorLeft.y)

                // Left top-to-waist
                cubicTo(
                    anchorLeft.x + 1.dp.toPx(), topAnchorY + stretchL * 0.15f,
                    centerX - waistHalfWidth * 1.1f, waistY - stretchL * 0.08f,
                    centerX - waistHalfWidth, waistY
                )

                // Left waist-to-bulb
                cubicTo(
                    centerX - waistHalfWidth * 0.88f, waistY + (bulbY - waistY) * 0.4f,
                    centerX - bulbHalfWidth * 1.05f, bulbY - (bulbY - waistY) * 0.25f,
                    centerX - bulbHalfWidth, bulbY
                )

                // Left bulb-to-tip
                cubicTo(
                    centerX - bulbHalfWidth * 0.92f, bulbY + (tipY - bulbY) * 0.55f,
                    tipX - 3.5.dp.toPx(), tipY - (tipY - bulbY) * 0.18f,
                    tipX, tipY
                )

                // Right tip-to-bulb
                cubicTo(
                    tipX + 3.5.dp.toPx(), tipY - (tipY - bulbY) * 0.18f,
                    centerX + bulbHalfWidth * 0.92f, bulbY + (tipY - bulbY) * 0.55f,
                    centerX + bulbHalfWidth, bulbY
                )

                // Right bulb-to-waist
                cubicTo(
                    centerX + bulbHalfWidth * 1.05f, bulbY - (bulbY - waistY) * 0.25f,
                    centerX + waistHalfWidth * 0.88f, waistY + (bulbY - waistY) * 0.4f,
                    centerX + waistHalfWidth, waistY
                )

                // Right waist-to-anchor
                cubicTo(
                    centerX + waistHalfWidth * 1.1f, waistY - stretchL * 0.08f,
                    anchorRight.x - 1.dp.toPx(), topAnchorY + stretchL * 0.15f,
                    anchorRight.x, anchorRight.y
                )

                // Ceiling bridge line
                lineTo(anchorLeft.x, anchorLeft.y)
                close()
            }

            drawPath(
                path = elasticFlamePath,
                brush = Brush.verticalGradient(
                    colors = listOf(flameRuby, flameOrange, flameAmber),
                    startY = topAnchorY,
                    endY = tipY
                )
            )

            // 3. Inner Incandescent Neon Cyan Core (High-temperature plasma core)
            if (stretchL > 16.dp.toPx()) {
                val coreTopY = topAnchorY + 4.dp.toPx()
                val coreStretchL = (stretchL - 10.dp.toPx()) * 0.72f
                val coreWaistW = (waistHalfWidth * 0.45f).coerceAtLeast(2.5.dp.toPx())
                val coreBulbW = (bulbHalfWidth * 0.52f).coerceAtLeast(4.5.dp.toPx())
                val coreBulbY = coreTopY + coreStretchL * 0.72f
                val coreTipY = coreTopY + coreStretchL

                val corePath = Path().apply {
                    val cTether = tetherHalfWidth * 0.42f
                    moveTo(centerX - cTether, coreTopY)

                    cubicTo(
                        centerX - cTether, coreTopY + coreStretchL * 0.2f,
                        centerX - coreWaistW * 1.1f, coreTopY + coreStretchL * 0.35f,
                        centerX - coreWaistW, coreTopY + coreStretchL * 0.38f
                    )
                    cubicTo(
                        centerX - coreWaistW, coreTopY + coreStretchL * 0.5f,
                        centerX - coreBulbW * 1.05f, coreBulbY - 4.dp.toPx(),
                        centerX - coreBulbW, coreBulbY
                    )
                    cubicTo(
                        centerX - coreBulbW * 0.85f, coreBulbY + (coreTipY - coreBulbY) * 0.55f,
                        tipX - 2.dp.toPx(), coreTipY - 3.dp.toPx(),
                        tipX, coreTipY
                    )
                    cubicTo(
                        tipX + 2.dp.toPx(), coreTipY - 3.dp.toPx(),
                        centerX + coreBulbW * 0.85f, coreBulbY + (coreTipY - coreBulbY) * 0.55f,
                        centerX + coreBulbW, coreBulbY
                    )
                    cubicTo(
                        centerX + coreBulbW * 1.05f, coreBulbY - 4.dp.toPx(),
                        centerX + coreWaistW, coreTopY + coreStretchL * 0.5f,
                        centerX + coreWaistW, coreTopY + coreStretchL * 0.38f
                    )
                    cubicTo(
                        centerX + coreWaistW * 1.1f, coreTopY + coreStretchL * 0.35f,
                        centerX + cTether, coreTopY + coreStretchL * 0.2f,
                        centerX + cTether, coreTopY
                    )
                    close()
                }

                drawPath(
                    path = corePath,
                    brush = Brush.verticalGradient(
                        colors = listOf(incandescentWhite, plasmaCyan, plasmaElectric),
                        startY = coreTopY,
                        endY = coreTipY
                    )
                )
            }

            // 4. Stretched Droplet Rim Arc Highlight
            drawArc(
                brush = Brush.horizontalGradient(
                    colors = listOf(plasmaCyan.copy(alpha = 0.6f), incandescentWhite, flameAmber)
                ),
                startAngle = 20f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(tipX - bulbHalfWidth * 0.7f, tipY - bulbHalfWidth * 0.8f),
                size = androidx.compose.ui.geometry.Size(bulbHalfWidth * 1.4f, bulbHalfWidth * 1.1f),
                style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
            )

            // 5. Tension Threshold Primed Spark (when stretch >= 1.0)
            if (progressClamped >= 1.0f) {
                val sparkRadius = 3.5.dp.toPx() * (1f + 0.3f * sin(phase * 8f))
                drawCircle(
                    color = incandescentWhite,
                    radius = sparkRadius,
                    center = tipOffset
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(plasmaCyan.copy(alpha = 0.8f), Color.Transparent),
                        center = tipOffset,
                        radius = sparkRadius * 2.8f
                    ),
                    radius = sparkRadius * 2.8f,
                    center = tipOffset
                )
            }
        }
    }
}

/**
 * Renders floating micro plasma spark particles during syncing state.
 */
private fun DrawScope.drawSyncingEmbers(
    centerX: Float,
    baseY: Float,
    phase: Float,
    sparkColor: Color,
    cyanColor: Color
) {
    val emberCount = 6
    for (i in 0 until emberCount) {
        val emberOffsetPhase = (phase + i * (2f * PI.toFloat() / emberCount)) % (2f * PI.toFloat())
        val progress = emberOffsetPhase / (2f * PI.toFloat())

        // Particles float upward and drift outward
        val driftX = centerX + sin(emberOffsetPhase * 2f + i) * (14.dp.toPx() + i * 2.5.dp.toPx())
        val riseY = baseY - progress * (55.dp.toPx() + (i % 3) * 10.dp.toPx())
        val alpha = (1f - progress).pow(1.5f).coerceIn(0f, 1f)
        val radius = (1.8.dp.toPx() + (i % 2) * 1.2.dp.toPx()) * (1f - progress * 0.4f)
        val color = if (i % 2 == 0) sparkColor.copy(alpha = alpha) else cyanColor.copy(alpha = alpha)

        drawCircle(
            color = color,
            radius = radius,
            center = Offset(driftX, riseY)
        )
    }
}
