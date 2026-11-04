package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.model.AppUsageItem
import com.example.model.FormatUtils
import com.example.model.InterfaceFilter
import com.example.model.TimeRange
import com.example.ui.MainViewModel
import com.example.ui.components.GlassContainer
import com.example.ui.components.UsagePermissionCard
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

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
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = com.example.model.AppStrings.analyticsTitle(appLanguage),
            color = glassStyle.textPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = com.example.model.AppStrings.analyticsSubtitle(appLanguage),
            color = glassStyle.textSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Time Range Selector Tabs
        TimeRangeSelector(
            currentRange = timeRange,
            appLanguage = appLanguage,
            onSelect = { viewModel.setTimeRange(it) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Interface filter chips (All, Cellular, Wi-Fi)
        InterfaceSelector(
            currentFilter = interfaceFilter,
            appLanguage = appLanguage,
            onSelect = { viewModel.setInterfaceFilter(it) }
        )

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

        // Apps List
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
                        fontSize = 13.sp
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
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(apps, key = { it.packageName }) { appItem ->
                    AppUsageCard(item = appItem, filter = interfaceFilter, appLanguage = appLanguage)
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
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

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(glassStyle.cardBackground)
            .border(1.dp, glassStyle.borderColor, RoundedCornerShape(14.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        TimeRange.values().forEach { range ->
            val isSelected = (range == currentRange)
            val label = when (range) {
                TimeRange.TODAY -> com.example.model.AppStrings.timeToday(appLanguage)
                TimeRange.WEEK -> com.example.model.AppStrings.timeWeek(appLanguage)
                TimeRange.MONTH -> com.example.model.AppStrings.timeMonth(appLanguage)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) glassStyle.accentPrimary else Color.Transparent)
                    .clickable { onSelect(range) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (isSelected) (if (glassStyle.isDark) Color.Black else Color.White) else glassStyle.textSecondary,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
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
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isSelected) glassStyle.accentSecondary.copy(alpha = if (glassStyle.isDark) 0.2f else 0.12f)
                        else glassStyle.cardBackground
                    )
                    .border(
                        1.dp,
                        if (isSelected) glassStyle.accentSecondary else glassStyle.borderColor,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onSelect(filter) }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = label,
                    color = if (isSelected) (if (glassStyle.isDark) glassStyle.textPrimary else glassStyle.accentSecondary) else glassStyle.textSecondary,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun AppUsageCard(
    item: AppUsageItem,
    filter: InterfaceFilter,
    appLanguage: com.example.model.AppLanguage,
    modifier: Modifier = Modifier
) {
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current

    GlassContainer(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = glassStyle.cardBackground,
        borderColor = glassStyle.borderColor
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
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = item.packageName,
                        color = glassStyle.textTertiary,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
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
                        fontSize = 15.sp,
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
                            fontSize = 10.sp
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
                        fontSize = 11.sp
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
                        fontSize = 11.sp
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
    val bitmap = remember(drawable) {
        drawableToBitmap(drawable)
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = appName,
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
        )
    } else {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(glassStyle.cardSubtleBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Apps,
                contentDescription = appName,
                tint = glassStyle.accentPrimary,
                modifier = Modifier.size(20.dp)
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
