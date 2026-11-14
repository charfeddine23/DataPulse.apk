package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.components.tactileSpringPress
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

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
    val dimensions = com.example.ui.theme.LocalAppDimensions.current
    val haptics = rememberAppHaptics()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensions.screenPaddingHorizontal)
    ) {
        Spacer(modifier = Modifier.height(6.dp))

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

        Spacer(modifier = Modifier.height(10.dp))

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
            .tactileSpringPress(pressedScale = 0.955f, onClick = onCycle)
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
            .tactileSpringPress(pressedScale = 0.955f, onClick = onCycle)
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
