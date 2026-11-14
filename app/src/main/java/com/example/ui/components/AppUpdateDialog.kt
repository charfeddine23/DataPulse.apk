package com.example.ui.components

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppLanguage
import com.example.model.FormatUtils
import com.example.ui.theme.LocalLiquidGlass
import com.example.update.AppUpdateInfo
import com.example.update.AppUpdateUiState
import kotlin.math.PI
import kotlin.math.sin

/**
 * Compact, floating Liquid Glass In-App Update modal inspired by the requested rocket emblem
 * layout — dynamically adapted to the active Dark Liquid Glass / Pearl Light theme colors
 * with silky 60/120fps rocket thrust animations and spring-physics interactions.
 */
@Composable
fun AppUpdateDialog(
    state: AppUpdateUiState,
    appLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onStartUpdate: (AppUpdateInfo) -> Unit,
    onInstallNow: (AppUpdateUiState.ReadyToInstall) -> Unit,
    onRequestInstallPermission: () -> Unit
) {
    val activeInfo: AppUpdateInfo? = when (state) {
        is AppUpdateUiState.Available -> state.info
        is AppUpdateUiState.Downloading -> state.info
        is AppUpdateUiState.ReadyToInstall -> state.info
        is AppUpdateUiState.NeedsInstallPermission -> state.info
        is AppUpdateUiState.Error -> state.info
        else -> null
    }

    if (activeInfo == null && state !is AppUpdateUiState.Error) return

    val glassStyle = LocalLiquidGlass.current
    val haptics = rememberAppHaptics()
    val isArabic = (appLanguage == AppLanguage.ARABIC)

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val entranceScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.84f,
        animationSpec = spring(
            dampingRatio = 0.62f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "update_dialog_entrance_scale"
    )
    val entranceAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 240),
        label = "update_dialog_entrance_alpha"
    )

    val primaryAccent = glassStyle.accentPrimary
    val secondaryAccent = glassStyle.accentSecondary

    Dialog(
        onDismissRequest = {
            if (state !is AppUpdateUiState.Downloading) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = state !is AppUpdateUiState.Downloading,
            dismissOnClickOutside = state !is AppUpdateUiState.Downloading
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .widthIn(max = 368.dp)
                .graphicsLayer {
                    scaleX = entranceScale
                    scaleY = entranceScale
                    alpha = entranceAlpha
                }
        ) {
            GlassContainer(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                borderColor = primaryAccent.copy(alpha = 0.55f),
                enableBreathingGlow = true,
                glowColor = primaryAccent
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Bar: Crisp Dismiss X + Verified Source Chip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .tactileSpringPress(
                                    pressedScale = 0.88f,
                                    enabled = state !is AppUpdateUiState.Downloading,
                                    onClick = {
                                        haptics.performSegmentSwitch()
                                        onDismiss()
                                    }
                                )
                                .clip(CircleShape)
                                .background(glassStyle.cardSubtleBg)
                                .border(0.8.dp, glassStyle.borderColor, CircleShape)
                                .testTag("update_dialog_close_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = if (isArabic) "إغلاق" else "Close",
                                tint = glassStyle.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        if (activeInfo != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(primaryAccent.copy(alpha = if (glassStyle.isDark) 0.14f else 0.10f))
                                    .border(0.8.dp, primaryAccent.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 9.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = primaryAccent,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "v${activeInfo.versionName}",
                                    color = primaryAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Animated Rocket Emblem inside Concentric Breathing Glow Rings
                    AnimatedRocketEmblem(
                        primaryAccent = primaryAccent,
                        secondaryAccent = secondaryAccent,
                        isDark = glassStyle.isDark,
                        isDownloading = (state is AppUpdateUiState.Downloading),
                        modifier = Modifier.size(126.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Bold Headline ("تحديث جديد متاح" / "New Update Available")
                    Text(
                        text = if (isArabic) "تحديث جديد متاح" else "New Update Available",
                        color = glassStyle.textPrimary,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Accent Subheading ("تحديث جديد • v1.x.x")
                    Text(
                        text = if (isArabic) {
                            "تحديث جديد${activeInfo?.let { " • v${it.versionName}" } ?: ""}"
                        } else {
                            "New Release${activeInfo?.let { " • v${it.versionName}" } ?: ""}"
                        },
                        color = primaryAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Clean contextual status / size / notes
                    val subtitleText = when (state) {
                        is AppUpdateUiState.Downloading -> {
                            val pct = (state.progress * 100).toInt().coerceIn(0, 100)
                            val dlStr = FormatUtils.formatBytes(state.downloadedBytes)
                            val totStr = if (state.totalBytes > 0L) FormatUtils.formatBytes(state.totalBytes) else ""
                            if (isArabic) {
                                if (totStr.isNotEmpty()) "جاري التنزيل مباشرة داخل الجهاز... $pct% ($dlStr / $totStr)"
                                else "جاري التنزيل مباشرة داخل الجهاز... $dlStr"
                            } else {
                                if (totStr.isNotEmpty()) "Downloading directly in-app... $pct% ($dlStr / $totStr)"
                                else "Downloading directly in-app... $dlStr"
                            }
                        }
                        is AppUpdateUiState.ReadyToInstall -> {
                            if (isArabic) "اكتمل التحميل والتحقق بنجاح — جاهز للتثبيت الآن"
                            else "Download verified — ready to install directly"
                        }
                        is AppUpdateUiState.NeedsInstallPermission -> {
                            if (isArabic) "يرجى منح إذن تثبيت التطبيقات لإكمال التحديث من داخل الجهاز"
                            else "Allow app installation permission to complete direct update"
                        }
                        is AppUpdateUiState.Error -> {
                            if (isArabic) state.messageAr else state.messageEn
                        }
                        else -> {
                            val sizeSuffix = activeInfo?.apkSizeBytes?.takeIf { it > 0L }?.let {
                                " (${FormatUtils.formatBytes(it)})"
                            } ?: ""
                            if (isArabic) {
                                "تحديث تلقائي آمن مباشرة من داخل التطبيق$sizeSuffix"
                            } else {
                                "Direct in-app update ready to download & install$sizeSuffix"
                            }
                        }
                    }

                    Text(
                        text = subtitleText,
                        color = glassStyle.textSecondary,
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    if (activeInfo != null && activeInfo.releaseNotes.isNotBlank() && state is AppUpdateUiState.Available) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(glassStyle.cardSubtleBg)
                                .border(0.8.dp, glassStyle.borderColor.copy(alpha = 0.65f), RoundedCornerShape(14.dp))
                                .padding(horizontal = 12.dp, vertical = 9.dp)
                        ) {
                            Text(
                                text = activeInfo.releaseNotes,
                                color = glassStyle.textTertiary,
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Start
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Bottom Wide Pill CTA Button (With live animated download progress fill)
                    UpdateActionPillButton(
                        state = state,
                        activeInfo = activeInfo,
                        isArabic = isArabic,
                        primaryAccent = primaryAccent,
                        secondaryAccent = secondaryAccent,
                        isDark = glassStyle.isDark,
                        onStartUpdate = {
                            haptics.performConfirm()
                            activeInfo?.let(onStartUpdate)
                        },
                        onInstallNow = { ready ->
                            haptics.performConfirm()
                            onInstallNow(ready)
                        },
                        onRequestPermission = {
                            haptics.performSegmentSwitch()
                            onRequestInstallPermission()
                        }
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun UpdateActionPillButton(
    state: AppUpdateUiState,
    activeInfo: AppUpdateInfo?,
    isArabic: Boolean,
    primaryAccent: Color,
    secondaryAccent: Color,
    isDark: Boolean,
    onStartUpdate: () -> Unit,
    onInstallNow: (AppUpdateUiState.ReadyToInstall) -> Unit,
    onRequestPermission: () -> Unit
) {
    val isDownloading = (state is AppUpdateUiState.Downloading)
    val rawProgress = (state as? AppUpdateUiState.Downloading)?.progress ?: 0f
    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessLow),
        label = "update_download_progress"
    )

    val buttonEnabled = !isDownloading
    val ctaShape = RoundedCornerShape(28.dp)

    val labelText = when (state) {
        is AppUpdateUiState.Downloading -> {
            val pct = (animatedProgress * 100).toInt().coerceIn(0, 100)
            if (isArabic) "جاري التحميل  $pct%" else "Downloading  $pct%"
        }
        is AppUpdateUiState.ReadyToInstall -> if (isArabic) "تثبيت التحديث" else "Install Now"
        is AppUpdateUiState.NeedsInstallPermission -> if (isArabic) "تفعيل إذن التثبيت" else "Allow Install"
        is AppUpdateUiState.Error -> if (isArabic) "إعادة المحاولة" else "Retry Update"
        else -> if (isArabic) "تحديث" else "Update"
    }

    val ctaIcon = when (state) {
        is AppUpdateUiState.ReadyToInstall -> Icons.Default.InstallMobile
        is AppUpdateUiState.NeedsInstallPermission -> Icons.Default.Security
        is AppUpdateUiState.Downloading -> Icons.Default.Download
        else -> Icons.Default.CheckCircle
    }

    val onClickAction: () -> Unit = {
        when (state) {
            is AppUpdateUiState.ReadyToInstall -> onInstallNow(state)
            is AppUpdateUiState.NeedsInstallPermission -> onRequestPermission()
            else -> if (activeInfo != null) onStartUpdate()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .tactileSpringPress(
                pressedScale = 0.96f,
                enabled = buttonEnabled,
                onClick = onClickAction
            )
            .clip(ctaShape)
            .background(
                if (isDownloading) {
                    Color(0xFF0A1018).copy(alpha = if (isDark) 0.92f else 0.14f)
                } else {
                    primaryAccent
                }
            )
            .then(
                if (!isDownloading) {
                    Modifier.background(
                        Brush.horizontalGradient(
                            colors = listOf(primaryAccent, secondaryAccent)
                        )
                    )
                } else {
                    Modifier.border(1.dp, primaryAccent.copy(alpha = 0.45f), ctaShape)
                }
            )
            .testTag("update_dialog_action_button"),
        contentAlignment = Alignment.Center
    ) {
        // Live liquid progress fill inside button during download
        if (isDownloading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress.coerceIn(0.03f, 1f))
                    .fillMaxHeight()
                    .align(Alignment.CenterStart)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                primaryAccent.copy(alpha = 0.85f),
                                secondaryAccent.copy(alpha = 0.90f)
                            )
                        )
                    )
            )
        }

        AnimatedContent(
            targetState = labelText,
            transitionSpec = {
                (fadeIn(tween(180)) + scaleIn(initialScale = 0.94f)) togetherWith
                    (fadeOut(tween(140)) + scaleOut(targetScale = 0.94f))
            },
            label = "update_cta_label"
        ) { targetLabel ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                val ctaContentColor = if (isDownloading) {
                    if (isDark) Color.White else Color(0xFF0B1320)
                } else {
                    if (isDark) Color(0xFF061018) else Color.White
                }
                if (state !is AppUpdateUiState.Available) {
                    Icon(
                        imageVector = ctaIcon,
                        contentDescription = null,
                        tint = ctaContentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = targetLabel,
                    color = ctaContentColor,
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.3.sp
                )
            }
        }
    }
}

/**
 * Custom 60/120fps animated rocket launch emblem with concentric breathing rings,
 * flickering plasma exhaust flame, and orbiting stars matched dynamically to app theme colors.
 */
@Composable
private fun AnimatedRocketEmblem(
    primaryAccent: Color,
    secondaryAccent: Color,
    isDark: Boolean,
    isDownloading: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rocket_emblem_anim")

    val breathPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isDownloading) 950 else 2100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rocket_breath"
    )

    val flameFlicker by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rocket_flame_flicker"
    )

    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val rOuter = size.minDimension * 0.48f
        val rMid = size.minDimension * 0.39f
        val rInner = size.minDimension * 0.31f

        // 1. Concentric Breathing Radial Glow Rings
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    primaryAccent.copy(alpha = 0.24f + 0.12f * breathPhase),
                    secondaryAccent.copy(alpha = 0.10f * breathPhase),
                    Color.Transparent
                ),
                center = Offset(cx, cy),
                radius = rOuter * (0.95f + 0.08f * breathPhase)
            ),
            radius = rOuter * (0.95f + 0.08f * breathPhase),
            center = Offset(cx, cy)
        )

        drawCircle(
            color = primaryAccent.copy(alpha = 0.14f + 0.10f * breathPhase),
            radius = rMid,
            center = Offset(cx, cy)
        )

        drawCircle(
            color = primaryAccent.copy(alpha = 0.50f + 0.35f * breathPhase),
            radius = rMid,
            center = Offset(cx, cy),
            style = Stroke(width = 1.6.dp.toPx())
        )

        drawCircle(
            color = if (isDark) Color(0xFF0A101A) else Color(0xFFF0F6FF),
            radius = rInner,
            center = Offset(cx, cy)
        )

        // 2. Orbiting twinkling micro-stars
        val starAlpha1 = 0.45f + 0.55f * ((sin(flameFlicker.toDouble()).toFloat() + 1f) / 2f)
        val starAlpha2 = 0.45f + 0.55f * ((sin((flameFlicker + 2.1f).toDouble()).toFloat() + 1f) / 2f)
        val starAlpha3 = 0.45f + 0.55f * ((sin((flameFlicker + 4.2f).toDouble()).toFloat() + 1f) / 2f)

        drawCircle(
            color = Color.White.copy(alpha = if (isDark) starAlpha1 else 0.7f * starAlpha1),
            radius = 2.2.dp.toPx(),
            center = Offset(cx + rInner * 0.68f, cy - rInner * 0.48f)
        )
        drawCircle(
            color = primaryAccent.copy(alpha = starAlpha2),
            radius = 2.0.dp.toPx(),
            center = Offset(cx - rInner * 0.65f, cy - rInner * 0.35f)
        )
        drawCircle(
            color = secondaryAccent.copy(alpha = starAlpha3),
            radius = 2.0.dp.toPx(),
            center = Offset(cx + rInner * 0.58f, cy + rInner * 0.45f)
        )
        drawCircle(
            color = Color(0xFFFFD54F).copy(alpha = starAlpha1),
            radius = 1.8.dp.toPx(),
            center = Offset(cx - rInner * 0.58f, cy + rInner * 0.50f)
        )

        // 3. Gently Hovering Rocket Coordinates
        val hoverOffset = (breathPhase - 0.5f) * (if (isDownloading) 5.dp.toPx() else 3.dp.toPx())
        val rocketCy = cy - 2.dp.toPx() - hoverOffset
        val unit = size.minDimension * 0.01f

        // 3a. Animated Exhaust Thruster Flame
        val flameScale = 0.85f + 0.25f * ((sin(flameFlicker.toDouble()).toFloat() + 1f) / 2f)
        val flamePath = Path().apply {
            moveTo(cx - 5.2f * unit, rocketCy + 14.5f * unit)
            lineTo(cx, rocketCy + (26.5f * flameScale) * unit)
            lineTo(cx + 5.2f * unit, rocketCy + 14.5f * unit)
            close()
        }
        drawPath(
            path = flamePath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFFD54F), secondaryAccent, primaryAccent),
                startY = rocketCy + 14f * unit,
                endY = rocketCy + 28f * unit
            )
        )

        // 3b. Left & Right Stabilizer Fins
        val leftFin = Path().apply {
            moveTo(cx - 7.5f * unit, rocketCy + 4f * unit)
            lineTo(cx - 15.5f * unit, rocketCy + 14.5f * unit)
            lineTo(cx - 6.8f * unit, rocketCy + 13.5f * unit)
            close()
        }
        val rightFin = Path().apply {
            moveTo(cx + 7.5f * unit, rocketCy + 4f * unit)
            lineTo(cx + 15.5f * unit, rocketCy + 14.5f * unit)
            lineTo(cx + 6.8f * unit, rocketCy + 13.5f * unit)
            close()
        }
        drawPath(path = leftFin, color = primaryAccent)
        drawPath(path = rightFin, color = primaryAccent)

        // 3c. Sleek Rocket Fuselage Body
        val bodyPath = Path().apply {
            moveTo(cx, rocketCy - 21f * unit)
            quadraticTo(cx + 9.5f * unit, rocketCy - 8f * unit, cx + 7.2f * unit, rocketCy + 14f * unit)
            lineTo(cx - 7.2f * unit, rocketCy + 14f * unit)
            quadraticTo(cx - 9.5f * unit, rocketCy - 8f * unit, cx, rocketCy - 21f * unit)
            close()
        }
        drawPath(
            path = bodyPath,
            color = if (isDark) Color(0xFFF8FAFC) else Color(0xFFFFFFFF)
        )

        // 3d. Nose Cone Accent Cap
        val nosePath = Path().apply {
            moveTo(cx, rocketCy - 21f * unit)
            quadraticTo(cx + 6.2f * unit, rocketCy - 14.5f * unit, cx + 6.2f * unit, rocketCy - 10.5f * unit)
            lineTo(cx - 6.2f * unit, rocketCy - 10.5f * unit)
            quadraticTo(cx - 6.2f * unit, rocketCy - 14.5f * unit, cx, rocketCy - 21f * unit)
            close()
        }
        drawPath(
            path = nosePath,
            brush = Brush.horizontalGradient(listOf(primaryAccent, secondaryAccent))
        )

        // 3e. Porthole Window
        drawCircle(
            color = Color(0xFF0A1220),
            radius = 5.4f * unit,
            center = Offset(cx, rocketCy - 2.2f * unit)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White, primaryAccent),
                center = Offset(cx - 1.2f * unit, rocketCy - 3.4f * unit),
                radius = 5.0f * unit
            ),
            radius = 4.0f * unit,
            center = Offset(cx, rocketCy - 2.2f * unit)
        )

        // 3f. Thruster Nozzle
        drawRoundRect(
            color = secondaryAccent,
            topLeft = Offset(cx - 4.5f * unit, rocketCy + 13.5f * unit),
            size = androidx.compose.ui.geometry.Size(9f * unit, 2.2f * unit),
            cornerRadius = CornerRadius(2f * unit, 2f * unit)
        )
    }
}
