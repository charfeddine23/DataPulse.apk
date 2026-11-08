package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Speed
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppCustomLimitConfig
import com.example.model.AppUsageItem
import com.example.model.FormatUtils
import com.example.model.InterfaceFilter
import com.example.model.TimeRange
import com.example.ui.MainViewModel
import com.example.ui.components.GlassContainer
import com.example.ui.components.NeumorphicSegmentedPill
import com.example.ui.components.PerAppCustomLimitDialog
import com.example.ui.components.UsagePermissionCard
import com.example.ui.components.neumorphicDepth
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricBlue
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensions.screenPaddingHorizontal)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = com.example.model.AppStrings.analyticsTitle(appLanguage),
            color = glassStyle.textPrimary,
            fontSize = dimensions.textTitleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = com.example.model.AppStrings.analyticsSubtitle(appLanguage),
            color = glassStyle.textSecondary,
            fontSize = dimensions.textBodyMedium
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Quick tip for user discoverability
        Text(
            text = if (appLanguage == com.example.model.AppLanguage.ARABIC)
                "💡 اضغط مطولاً على أي تطبيق لتخصيص الحد اليومي وإشعاراته"
            else
                "💡 Long-press any app to customize its daily data limit & alerts",
            color = glassStyle.accentPrimary,
            fontSize = dimensions.textCaption,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (dimensions.isTablet) {
            // Adaptive Side-by-Side Filter Bar on Tablets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensions.gridSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimeRangeSelector(
                    currentRange = timeRange,
                    appLanguage = appLanguage,
                    onSelect = { viewModel.setTimeRange(it) },
                    modifier = Modifier.weight(1.15f)
                )

                InterfaceSelector(
                    currentFilter = interfaceFilter,
                    appLanguage = appLanguage,
                    onSelect = { viewModel.setInterfaceFilter(it) },
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            // Phone Stacked Filter Selectors
            TimeRangeSelector(
                currentRange = timeRange,
                appLanguage = appLanguage,
                onSelect = { viewModel.setTimeRange(it) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            InterfaceSelector(
                currentFilter = interfaceFilter,
                appLanguage = appLanguage,
                onSelect = { viewModel.setInterfaceFilter(it) }
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
}

@Composable
fun TimeRangeSelector(
    currentRange: TimeRange,
    appLanguage: com.example.model.AppLanguage,
    onSelect: (TimeRange) -> Unit,
    modifier: Modifier = Modifier
) {
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current
    val trayShape = RoundedCornerShape(16.dp)
    val haptics = com.example.ui.components.rememberAppHaptics()

    // Indented Neumorphic Tray
    Box(
        modifier = modifier
            .fillMaxWidth()
            .neumorphicDepth(
                shape = trayShape,
                lightColor = Color.Transparent,
                darkColor = glassStyle.neumorphicShadow,
                offsetDp = 3.dp,
                isPressed = true
            )
            .clip(trayShape)
            .background(
                Brush.linearGradient(
                    listOf(glassStyle.cardSubtleBg, glassStyle.neumorphicSurface)
                )
            )
            .border(1.dp, glassStyle.neumorphicBorderDark, trayShape)
            .padding(5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TimeRange.values().forEach { range ->
                val isSelected = (range == currentRange)
                val itemShape = RoundedCornerShape(12.dp)
                val label = when (range) {
                    TimeRange.TODAY -> com.example.model.AppStrings.timeToday(appLanguage)
                    TimeRange.WEEK -> com.example.model.AppStrings.timeWeek(appLanguage)
                    TimeRange.MONTH -> com.example.model.AppStrings.timeMonth(appLanguage)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .neumorphicDepth(
                            shape = itemShape,
                            lightColor = if (isSelected) Color.Transparent else glassStyle.neumorphicHighlight.copy(alpha = 0.4f),
                            darkColor = glassStyle.neumorphicShadow,
                            offsetDp = 2.dp,
                            isPressed = isSelected
                        )
                        .clip(itemShape)
                        .background(
                            if (isSelected) {
                                Brush.linearGradient(
                                    listOf(
                                        glassStyle.cardSubtleBg,
                                        glassStyle.accentPrimary.copy(alpha = 0.22f)
                                    )
                                )
                            } else {
                                Brush.linearGradient(
                                    listOf(
                                        glassStyle.neumorphicSurface.copy(alpha = 0.6f),
                                        glassStyle.neumorphicBase.copy(alpha = 0.6f)
                                    )
                                )
                            }
                        )
                        .border(
                            1.dp,
                            if (isSelected) {
                                Brush.linearGradient(listOf(glassStyle.accentPrimary.copy(alpha = 0.75f), glassStyle.neumorphicBorderDark))
                            } else {
                                Brush.linearGradient(listOf(glassStyle.neumorphicBorderLight.copy(alpha = 0.15f), glassStyle.neumorphicBorderDark))
                            },
                            itemShape
                        )
                        .clickable {
                            if (range != currentRange) {
                                haptics.performSegmentSwitch()
                                onSelect(range)
                            }
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) glassStyle.accentPrimary else glassStyle.textSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun InterfaceSelector(
    currentFilter: InterfaceFilter,
    appLanguage: com.example.model.AppLanguage,
    onSelect: (InterfaceFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InterfaceFilter.values().forEach { filter ->
            val isSelected = (filter == currentFilter)
            val label = when (filter) {
                InterfaceFilter.ALL -> com.example.model.AppStrings.filterAll(appLanguage)
                InterfaceFilter.MOBILE -> com.example.model.AppStrings.filterCellular(appLanguage)
                InterfaceFilter.WIFI -> com.example.model.AppStrings.filterWifi(appLanguage)
            }

            NeumorphicSegmentedPill(
                label = label,
                isSelected = isSelected,
                onClick = { onSelect(filter) },
                accentColor = glassStyle.accentSecondary,
                modifier = Modifier.weight(1f)
            )
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
