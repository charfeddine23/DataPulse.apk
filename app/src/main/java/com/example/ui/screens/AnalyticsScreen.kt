package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppCustomLimitConfig
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.model.AppUsageItem
import com.example.model.FormatUtils
import com.example.model.InterfaceFilter
import com.example.model.TimeRange
import com.example.ui.MainViewModel
import com.example.ui.components.GlassContainer
import com.example.ui.components.PerAppCustomLimitDialog
import com.example.ui.components.UsagePermissionCard
import com.example.ui.components.rememberAppHaptics
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnalyticsScreen(
    viewModel: MainViewModel,
    onRequestUsagePermission: () -> Unit,
    onOpenAppInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val apps by viewModel.filteredAppUsage.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshingApps.collectAsStateWithLifecycle()
    val timeRange by viewModel.selectedTimeRange.collectAsStateWithLifecycle()
    val interfaceFilter by viewModel.selectedInterface.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val hasUsagePerm by viewModel.hasUsagePermission.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val customAppLimits by viewModel.customAppLimits.collectAsStateWithLifecycle()
    val dailyAppQuotaGB by viewModel.dailyAppQuotaGB.collectAsStateWithLifecycle()
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current

    var selectedAppForCustomization by remember { mutableStateOf<AppUsageItem?>(null) }
    var showTipPopup by remember { mutableStateOf(false) }
    val dimensions = com.example.ui.theme.LocalAppDimensions.current
    val haptics = rememberAppHaptics()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensions.screenPaddingHorizontal)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle on the left, Dynamic Glowing Lightbulb on the right opposite to it
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = AppStrings.analyticsSubtitle(appLanguage),
                color = glassStyle.textSecondary,
                fontSize = dimensions.textBodyMedium,
                lineHeight = 17.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 10.dp)
            )

            DynamicGlowingLightbulbButton(
                onClick = {
                    haptics.performConfirm()
                    showTipPopup = true
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Side-by-Side Single Cycling Filter Buttons (Time Range + Interface Traffic Filter)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CyclingTimeRangeButton(
                currentRange = timeRange,
                appLanguage = appLanguage,
                onCycle = {
                    haptics.performSegmentSwitch()
                    val nextRange = when (timeRange) {
                        TimeRange.TODAY -> TimeRange.WEEK
                        TimeRange.WEEK -> TimeRange.MONTH
                        TimeRange.MONTH -> TimeRange.TODAY
                    }
                    viewModel.setTimeRange(nextRange)
                },
                modifier = Modifier.weight(1f)
            )

            CyclingInterfaceFilterButton(
                currentFilter = interfaceFilter,
                appLanguage = appLanguage,
                onCycle = {
                    haptics.performSegmentSwitch()
                    val nextFilter = when (interfaceFilter) {
                        InterfaceFilter.ALL -> InterfaceFilter.MOBILE
                        InterfaceFilter.MOBILE -> InterfaceFilter.WIFI
                        InterfaceFilter.WIFI -> InterfaceFilter.ALL
                    }
                    viewModel.setInterfaceFilter(nextFilter)
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text(com.example.model.AppStrings.searchApps(appLanguage), color = glassStyle.textTertiary, fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = glassStyle.textTertiary,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = glassStyle.textTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = glassStyle.accentPrimary,
                unfocusedBorderColor = glassStyle.borderColor,
                focusedContainerColor = glassStyle.cardBackground,
                unfocusedContainerColor = glassStyle.cardBackground,
                focusedTextColor = glassStyle.textPrimary,
                unfocusedTextColor = glassStyle.textPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("app_search_field")
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (!hasUsagePerm) {
            UsagePermissionCard(
                onRequestPermission = onRequestUsagePermission,
                onOpenAppInfo = onOpenAppInfo,
                appLanguage = appLanguage
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Apps List / Grid
        if (isRefreshing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = glassStyle.accentPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = com.example.model.AppStrings.analyzingNetwork(appLanguage),
                        color = glassStyle.textSecondary,
                        fontSize = dimensions.textBodyLarge
                    )
                }
            }
        } else if (apps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = "No apps",
                        tint = glassStyle.textTertiary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty()) com.example.model.AppStrings.noMatchingApps(appLanguage) else com.example.model.AppStrings.noAppTraffic(appLanguage),
                        color = glassStyle.textSecondary,
                        fontSize = dimensions.textBodyLarge
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimensions.gridSpacing)
            ) {
                if (dimensions.isTablet) {
                    // Adaptive 2-Column App Grid on Tablets & Foldables
                    items(apps.chunked(2), key = { chunk -> chunk.first().packageName }) { rowApps ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(dimensions.gridSpacing)
                        ) {
                            for (appItem in rowApps) {
                                val sanitizedPkg = appItem.packageName.replace('.', '_')
                                val config = customAppLimits[sanitizedPkg]
                                AppUsageCard(
                                    item = appItem,
                                    filter = interfaceFilter,
                                    appLanguage = appLanguage,
                                    customConfig = config,
                                    onLongClick = { selectedAppForCustomization = appItem },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowApps.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                } else {
                    // Phone 1-Column List
                    items(apps, key = { it.packageName }) { appItem ->
                        val sanitizedPkg = appItem.packageName.replace('.', '_')
                        val config = customAppLimits[sanitizedPkg]
                        AppUsageCard(
                            item = appItem,
                            filter = interfaceFilter,
                            appLanguage = appLanguage,
                            customConfig = config,
                            onLongClick = { selectedAppForCustomization = appItem }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Custom Per-App Dialog triggered by long-press
    if (selectedAppForCustomization != null) {
        val targetApp = selectedAppForCustomization!!
        val sanitizedPkg = targetApp.packageName.replace('.', '_')
        val existingConfig = customAppLimits[sanitizedPkg]
        val initialLimit = existingConfig?.dailyLimitGB ?: dailyAppQuotaGB
        val initialAlert = existingConfig?.alertEnabled ?: true

        PerAppCustomLimitDialog(
            appItem = targetApp,
            initialDailyLimitGB = initialLimit,
            initialAlertEnabled = initialAlert,
            appLanguage = appLanguage,
            onDismiss = { selectedAppForCustomization = null },
            onSave = { limit, alertEnabled ->
                viewModel.saveAppCustomLimit(targetApp.packageName, limit, alertEnabled)
            }
        )
    }

    // Luxurious Liquid Glass Tip Popup Dialog triggered by Lightbulb
    if (showTipPopup) {
        AnalyticsTipPopupDialog(
            appLanguage = appLanguage,
            onDismiss = { showTipPopup = false }
        )
    }
}

/**
 * Larger, dynamically animated glowing Lightbulb button positioned opposite to the analytics subtitle.
 */
@Composable
fun DynamicGlowingLightbulbButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current
    val warmGold = if (glassStyle.isDark) Color(0xFFFFD54F) else Color(0xFFD97706)
    val warmAmber = if (glassStyle.isDark) Color(0xFFFF9100) else Color(0xFFF59E0B)

    val infiniteTransition = rememberInfiniteTransition(label = "lightbulb_dynamics")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.42f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bulb_glow_alpha"
    )
    val bulbScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bulb_scale"
    )
    val rayRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bulb_ray_rotation"
    )

    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .testTag("analytics_tip_lightbulb"),
        contentAlignment = Alignment.Center
    ) {
        // Animated outer aura + rotating shimmer rays behind bulb
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val auraRadius = size.minDimension * 0.48f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        warmGold.copy(alpha = 0.34f * glowAlpha),
                        warmAmber.copy(alpha = 0.14f * glowAlpha),
                        Color.Transparent
                    ),
                    center = center,
                    radius = auraRadius
                ),
                radius = auraRadius,
                center = center
            )

            // Delicate rotating energy rays
            val rayCount = 6
            val innerR = size.minDimension * 0.36f
            val outerR = size.minDimension * 0.45f
            for (i in 0 until rayCount) {
                val angleDeg = rayRotation + i * (360f / rayCount)
                val angleRad = (angleDeg * PI / 180.0).toFloat()
                val start = Offset(
                    center.x + cos(angleRad) * innerR,
                    center.y + sin(angleRad) * innerR
                )
                val end = Offset(
                    center.x + cos(angleRad) * outerR,
                    center.y + sin(angleRad) * outerR
                )
                drawLine(
                    color = warmGold.copy(alpha = 0.55f * glowAlpha),
                    start = start,
                    end = end,
                    strokeWidth = 1.6.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        // Elevated frosted glass pill enclosing the enlarged lightbulb
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            warmGold.copy(alpha = if (glassStyle.isDark) 0.24f else 0.16f),
                            glassStyle.cardSubtleBg
                        )
                    )
                )
                .border(
                    width = 1.2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            warmGold.copy(alpha = 0.85f * glowAlpha),
                            glassStyle.accentPrimary.copy(alpha = 0.45f)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = "Analytics Tip",
                tint = warmGold,
                modifier = Modifier
                    .size(22.dp)
                    .graphicsLayer {
                        scaleX = bulbScale
                        scaleY = bulbScale
                    }
            )
        }
    }
}

/**
 * Luxurious Liquid Glass popup modal shown when the user taps the animated Lightbulb.
 */
@Composable
fun AnalyticsTipPopupDialog(
    appLanguage: AppLanguage,
    onDismiss: () -> Unit
) {
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current
    val warmGold = if (glassStyle.isDark) Color(0xFFFFD54F) else Color(0xFFD97706)
    val scaleAnim = remember { Animatable(0.86f) }

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = scaleAnim.value
                    scaleY = scaleAnim.value
                }
        ) {
            GlassContainer(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                borderColor = warmGold.copy(alpha = 0.6f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Glowing Emblem Header
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        warmGold.copy(alpha = 0.30f),
                                        glassStyle.accentPrimary.copy(alpha = 0.12f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .border(
                                1.5.dp,
                                Brush.linearGradient(listOf(warmGold, glassStyle.accentPrimary)),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = warmGold,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Badge Title
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(warmGold.copy(alpha = if (glassStyle.isDark) 0.18f else 0.12f))
                            .border(0.8.dp, warmGold.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (appLanguage == AppLanguage.ARABIC) "تخصيص ذكي للتطبيقات" else "SMART APP CONTROLS",
                            color = warmGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Exact requested message
                    Text(
                        text = if (appLanguage == AppLanguage.ARABIC) {
                            "اضغط مطولاً على أي تطبيق لتخصيص الحد اليومي وإشعاراته"
                        } else {
                            "Long-press any app to customize its daily data limit & alerts"
                        },
                        color = glassStyle.textPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("analytics_tip_dismiss_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = glassStyle.accentPrimary
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = if (appLanguage == AppLanguage.ARABIC) "حسناً، فهمت" else "Got it",
                            color = if (glassStyle.isDark) Color.Black else Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Single merged cycling button for Time Range (Today -> This Week -> This Month).
 */
@Composable
fun CyclingTimeRangeButton(
    currentRange: TimeRange,
    appLanguage: AppLanguage,
    onCycle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current
    val shape = RoundedCornerShape(15.dp)

    val label = when (currentRange) {
        TimeRange.TODAY -> AppStrings.timeToday(appLanguage)
        TimeRange.WEEK -> AppStrings.timeWeek(appLanguage)
        TimeRange.MONTH -> AppStrings.timeMonth(appLanguage)
    }

    val icon: ImageVector = when (currentRange) {
        TimeRange.TODAY -> Icons.Default.Today
        TimeRange.WEEK -> Icons.Default.DateRange
        TimeRange.MONTH -> Icons.Default.CalendarMonth
    }

    val activeIndex = when (currentRange) {
        TimeRange.TODAY -> 0
        TimeRange.WEEK -> 1
        TimeRange.MONTH -> 2
    }

    Box(
        modifier = modifier
            .height(48.dp)
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        glassStyle.cardBackground,
                        glassStyle.accentPrimary.copy(alpha = if (glassStyle.isDark) 0.14f else 0.08f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        glassStyle.accentPrimary.copy(alpha = 0.65f),
                        glassStyle.borderColor
                    )
                ),
                shape = shape
            )
            .clickable(onClick = onCycle)
            .padding(horizontal = 10.dp)
            .testTag("cycle_time_range_button"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(glassStyle.accentPrimary.copy(alpha = if (glassStyle.isDark) 0.20f else 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = glassStyle.accentPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                AnimatedContent(
                    targetState = label,
                    transitionSpec = {
                        (slideInVertically { it / 2 } + fadeIn(tween(180))) togetherWith
                            (slideOutVertically { -it / 2 } + fadeOut(tween(140)))
                    },
                    label = "time_range_cycle_label"
                ) { targetLabel ->
                    Text(
                        text = targetLabel,
                        color = glassStyle.textPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // 3-step pill indicator showing active filter index
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0..2) {
                    val isStepActive = (i == activeIndex)
                    Box(
                        modifier = Modifier
                            .width(if (isStepActive) 10.dp else 4.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(
                                if (isStepActive) glassStyle.accentPrimary else glassStyle.textTertiary.copy(alpha = 0.35f)
                            )
                    )
                }
            }
        }
    }
}

/**
 * Single merged cycling button for Interface Filter (All Traffic -> Mobile Data -> Wi-Fi).
 */
@Composable
fun CyclingInterfaceFilterButton(
    currentFilter: InterfaceFilter,
    appLanguage: AppLanguage,
    onCycle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current
    val shape = RoundedCornerShape(15.dp)

    val label = when (currentFilter) {
        InterfaceFilter.ALL -> AppStrings.filterAll(appLanguage)
        InterfaceFilter.MOBILE -> AppStrings.filterCellular(appLanguage)
        InterfaceFilter.WIFI -> AppStrings.filterWifi(appLanguage)
    }

    val icon: ImageVector = when (currentFilter) {
        InterfaceFilter.ALL -> Icons.Default.SwapVert
        InterfaceFilter.MOBILE -> Icons.Default.SignalCellularAlt
        InterfaceFilter.WIFI -> Icons.Default.Wifi
    }

    val targetTint = when (currentFilter) {
        InterfaceFilter.ALL -> glassStyle.accentSecondary
        InterfaceFilter.MOBILE -> ElectricBlue
        InterfaceFilter.WIFI -> EmeraldSafe
    }

    val animatedTint by animateColorAsState(
        targetValue = targetTint,
        animationSpec = tween(durationMillis = 220),
        label = "interface_tint"
    )

    val activeIndex = when (currentFilter) {
        InterfaceFilter.ALL -> 0
        InterfaceFilter.MOBILE -> 1
        InterfaceFilter.WIFI -> 2
    }

    Box(
        modifier = modifier
            .height(48.dp)
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        glassStyle.cardBackground,
                        animatedTint.copy(alpha = if (glassStyle.isDark) 0.14f else 0.08f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        animatedTint.copy(alpha = 0.65f),
                        glassStyle.borderColor
                    )
                ),
                shape = shape
            )
            .clickable(onClick = onCycle)
            .padding(horizontal = 10.dp)
            .testTag("cycle_interface_filter_button"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(animatedTint.copy(alpha = if (glassStyle.isDark) 0.20f else 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = animatedTint,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                AnimatedContent(
                    targetState = label,
                    transitionSpec = {
                        (slideInVertically { it / 2 } + fadeIn(tween(180))) togetherWith
                            (slideOutVertically { -it / 2 } + fadeOut(tween(140)))
                    },
                    label = "interface_filter_cycle_label"
                ) { targetLabel ->
                    Text(
                        text = targetLabel,
                        color = glassStyle.textPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // 3-step pill indicator showing active filter index
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0..2) {
                    val isStepActive = (i == activeIndex)
                    Box(
                        modifier = Modifier
                            .width(if (isStepActive) 10.dp else 4.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(
                                if (isStepActive) animatedTint else glassStyle.textTertiary.copy(alpha = 0.35f)
                            )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppUsageCard(
    item: AppUsageItem,
    filter: InterfaceFilter,
    appLanguage: com.example.model.AppLanguage,
    customConfig: AppCustomLimitConfig?,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current
    val dimensions = com.example.ui.theme.LocalAppDimensions.current

    GlassContainer(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .combinedClickable(
                onClick = { /* Tap */ },
                onLongClick = onLongClick
            ),
        backgroundColor = glassStyle.cardBackground,
        borderColor = if (customConfig != null) glassStyle.accentPrimary.copy(alpha = 0.55f) else glassStyle.borderColor
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App Icon
                AppIconImage(drawable = item.icon, appName = item.appName)

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.appName,
                        color = glassStyle.textPrimary,
                        fontSize = dimensions.textTitleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = item.packageName,
                        color = glassStyle.textTertiary,
                        fontSize = dimensions.textMicro,
                        maxLines = 1
                    )

                    if (customConfig != null) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(glassStyle.accentPrimary.copy(alpha = 0.16f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = if (customConfig.alertEnabled) Icons.Default.NotificationsActive else Icons.Default.Speed,
                                contentDescription = null,
                                tint = glassStyle.accentPrimary,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            val limitStr = if (customConfig.dailyLimitGB % 1f == 0f) "${customConfig.dailyLimitGB.toInt()} GB" else "%.1f GB".format(customConfig.dailyLimitGB)
                            Text(
                                text = if (appLanguage == com.example.model.AppLanguage.ARABIC) "مخصص: $limitStr / يوم" else "Custom: $limitStr / day",
                                color = glassStyle.accentPrimary,
                                fontSize = dimensions.textMicro,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Total display
                val primaryBytes = when (filter) {
                    InterfaceFilter.ALL -> item.totalBytes
                    InterfaceFilter.MOBILE -> item.mobileBytes
                    InterfaceFilter.WIFI -> item.wifiBytes
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = FormatUtils.formatBytes(primaryBytes),
                        color = glassStyle.accentPrimary,
                        fontSize = dimensions.textMetricValue,
                        fontWeight = FontWeight.ExtraBold
                    )
                    if (item.percentageOfTotal > 0f) {
                        val pctText = if (appLanguage == com.example.model.AppLanguage.ARABIC) {
                            "%.1f%% من الإجمالي".format(item.percentageOfTotal * 100f)
                        } else {
                            "%.1f%% of total".format(item.percentageOfTotal * 100f)
                        }
                        Text(
                            text = pctText,
                            color = glassStyle.textTertiary,
                            fontSize = dimensions.textMicro
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Cellular vs Wi-Fi sub-breakdown pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SignalCellularAlt,
                        contentDescription = com.example.model.AppStrings.filterCellular(appLanguage),
                        tint = glassStyle.accentSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${com.example.model.AppStrings.filterCellular(appLanguage)}: ${FormatUtils.formatBytes(item.mobileBytes)}",
                        color = glassStyle.textSecondary,
                        fontSize = dimensions.textCaption
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = com.example.model.AppStrings.filterWifi(appLanguage),
                        tint = ElectricBlue,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${com.example.model.AppStrings.filterWifi(appLanguage)}: ${FormatUtils.formatBytes(item.wifiBytes)}",
                        color = glassStyle.textSecondary,
                        fontSize = dimensions.textCaption
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Percentage track
            val barFraction = item.percentageOfTotal.coerceIn(0.01f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (glassStyle.isDark) Color(0xFF0B111E) else Color(0xFFE2E8F0))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(barFraction)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(glassStyle.accentSecondary, glassStyle.accentPrimary)
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun AppIconImage(drawable: Drawable?, appName: String) {
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current
    val dimensions = com.example.ui.theme.LocalAppDimensions.current
    val bitmap = remember(drawable) {
        drawableToBitmap(drawable)
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = appName,
            modifier = Modifier
                .size(dimensions.appItemIconSize)
                .clip(RoundedCornerShape(10.dp))
        )
    } else {
        Box(
            modifier = Modifier
                .size(dimensions.appItemIconSize)
                .clip(RoundedCornerShape(10.dp))
                .background(glassStyle.cardSubtleBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Apps,
                contentDescription = appName,
                tint = glassStyle.accentPrimary,
                modifier = Modifier.size(dimensions.appItemIconSize * 0.55f)
            )
        }
    }
}

fun drawableToBitmap(drawable: Drawable?): Bitmap? {
    if (drawable == null) return null
    if (drawable is BitmapDrawable && drawable.bitmap != null) {
        return drawable.bitmap
    }
    val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 72
    val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 72
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}
