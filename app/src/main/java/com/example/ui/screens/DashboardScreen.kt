package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.FormatUtils
import com.example.model.SimCardInfo
import com.example.ui.MainViewModel
import com.example.ui.components.CircularQuotaProgressGauge
import com.example.ui.components.ElasticFlamePullToRefreshBox
import com.example.ui.components.GlassContainer
import com.example.ui.components.LiveSpeedCard
import com.example.ui.components.QuotaProgressGauge
import com.example.ui.components.UsagePermissionCard
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onRequestUsagePermission: () -> Unit,
    onOpenAppInfo: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    modifier: Modifier = Modifier
) {
    val speedSample by viewModel.speedSample.collectAsStateWithLifecycle()
    val speedHistory by viewModel.speedHistory.collectAsStateWithLifecycle()
    val usageSummary by viewModel.usageSummary.collectAsStateWithLifecycle()
    val quota by viewModel.dataPlanQuota.collectAsStateWithLifecycle()
    val simCards by viewModel.simCards.collectAsStateWithLifecycle()
    val selectedSimIndex by viewModel.selectedSimIndex.collectAsStateWithLifecycle()
    val hasUsagePerm by viewModel.hasUsagePermission.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isDashboardRefreshing.collectAsStateWithLifecycle()
    var showEditCapDialog by remember { mutableStateOf(false) }

    val dimensions = com.example.ui.theme.LocalAppDimensions.current

    if (showEditCapDialog) {
        EditDataCapDialog(
            currentLimitGB = quota.planLimitGB,
            usedBytes = quota.usedBytes,
            appLanguage = appLanguage,
            onDismiss = { showEditCapDialog = false },
            onSaveLimit = { newLimit ->
                viewModel.updatePlanLimit(newLimit)
                showEditCapDialog = false
            }
        )
    }

    ElasticFlamePullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refreshAll() },
        useScreenTopOverlay = true,
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimensions.screenPaddingHorizontal),
            contentPadding = PaddingValues(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(dimensions.sectionSpacing)
        ) {
            // Usage Permission Warning if not granted
            if (!hasUsagePerm) {
                item {
                    UsagePermissionCard(
                        onRequestPermission = onRequestUsagePermission,
                        onOpenAppInfo = onOpenAppInfo,
                        appLanguage = appLanguage
                    )
                }
            }

            if (dimensions.isTablet) {
                // Adaptive Tablet / Foldable 2-Column Dashboard Layout
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(dimensions.gridSpacing)
                    ) {
                        // Left Column (50%): Circular Quota Gauge & Dual SIM
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(dimensions.gridSpacing)
                        ) {
                            val limitBytes = (quota.planLimitGB * 1024L * 1024L * 1024L).toLong()
                            val isInternetConnected = (speedSample.connectionType != com.example.model.NetworkType.OFFLINE)
                            CircularQuotaProgressGauge(
                                usedBytes = quota.usedBytes,
                                limitBytes = limitBytes,
                                percentage = quota.usagePercent,
                                status = quota.status,
                                daysRemaining = quota.daysRemaining,
                                appLanguage = appLanguage,
                                isInternetConnected = isInternetConnected,
                                onConfigureClick = { showEditCapDialog = true }
                            )

                            if (simCards.isNotEmpty()) {
                                DualSimSelector(
                                    simCards = simCards,
                                    selectedIndex = selectedSimIndex,
                                    onSelect = { viewModel.setSimIndex(it) }
                                )
                            }
                        }

                        // Right Column (50%): Data Breakdown Summary Grid
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(dimensions.gridSpacing)
                        ) {
                            val glassStyle = com.example.ui.theme.LocalLiquidGlass.current
                            Text(
                                text = com.example.model.AppStrings.dataBreakdownTitle(appLanguage),
                                color = glassStyle.textSecondary,
                                fontSize = dimensions.textBodyLarge,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            DataBreakdownGrid(
                                usageSummary = usageSummary,
                                appLanguage = appLanguage
                            )
                        }
                    }
                }
            } else {
                // Phone Single-Column Stack
                if (simCards.isNotEmpty()) {
                    item {
                        DualSimSelector(
                            simCards = simCards,
                            selectedIndex = selectedSimIndex,
                            onSelect = { viewModel.setSimIndex(it) }
                        )
                    }
                }

                item {
                    val limitBytes = (quota.planLimitGB * 1024L * 1024L * 1024L).toLong()
                    val isInternetConnected = (speedSample.connectionType != com.example.model.NetworkType.OFFLINE)
                    CircularQuotaProgressGauge(
                        usedBytes = quota.usedBytes,
                        limitBytes = limitBytes,
                        percentage = quota.usagePercent,
                        status = quota.status,
                        daysRemaining = quota.daysRemaining,
                        appLanguage = appLanguage,
                        isInternetConnected = isInternetConnected,
                        onConfigureClick = { showEditCapDialog = true }
                    )
                }

                item {
                    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current
                    Text(
                        text = com.example.model.AppStrings.dataBreakdownTitle(appLanguage),
                        color = glassStyle.textSecondary,
                        fontSize = dimensions.textBodyLarge,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DataBreakdownGrid(
                        usageSummary = usageSummary,
                        appLanguage = appLanguage
                    )
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun LiquidGlassDualSimSlider(
    simCards: List<SimCardInfo>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    // Ensure we have 2 distinct SIM items for the dual slider
    val sim1 = simCards.getOrNull(0) ?: SimCardInfo(
        subId = 1,
        slotIndex = 0,
        carrierName = "SIM 1 (Primary)",
        displayName = "SIM 1",
        isDefaultData = true
    )
    val sim2 = simCards.getOrNull(1) ?: SimCardInfo(
        subId = 2,
        slotIndex = 1,
        carrierName = "SIM 2 (Secondary)",
        displayName = "SIM 2",
        isDefaultData = false
    )

    val progressAnim = remember { Animatable(selectedIndex.toFloat()) }
    var isDragging by remember { mutableStateOf(false) }
    val haptics = com.example.ui.components.rememberAppHaptics()

    LaunchedEffect(selectedIndex) {
        if (!isDragging && kotlin.math.abs(progressAnim.value - selectedIndex.toFloat()) > 0.01f) {
            progressAnim.animateTo(
                targetValue = selectedIndex.toFloat(),
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }

    val glowAlpha by animateFloatAsState(
        targetValue = if (isDragging) 0.88f else 0.52f,
        animationSpec = tween(150),
        label = "slider_glow"
    )

    val stretchX by animateFloatAsState(
        targetValue = if (isDragging) 1.05f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "stretch_x"
    )

    val squashY by animateFloatAsState(
        targetValue = if (isDragging) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "squash_y"
    )

    // Force LTR layout for intuitive left-to-right gesture coordinates
    androidx.compose.runtime.CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(glassStyle.cardBackground)
                .border(
                    1.dp,
                    Brush.horizontalGradient(
                        listOf(
                            glassStyle.borderColor.copy(alpha = 0.5f),
                            glassStyle.accentPrimary.copy(alpha = 0.25f),
                            glassStyle.borderColor.copy(alpha = 0.5f)
                        )
                    ),
                    RoundedCornerShape(22.dp)
                )
                .pointerInput(Unit) {
                    val trackW = size.width.toFloat()
                    val pad = with(density) { 4.dp.toPx() }
                    val halfW = (trackW - pad * 2f) / 2f
                    var crossedThreshold = selectedIndex > 0
                    detectHorizontalDragGestures(
                        onDragStart = {
                            isDragging = true
                            crossedThreshold = progressAnim.value > 0.5f
                        },
                        onDragEnd = {
                            isDragging = false
                            val target = if (progressAnim.value > 0.5f) 1 else 0
                            if (target != selectedIndex) {
                                haptics.performSegmentSwitch()
                            }
                            scope.launch {
                                progressAnim.animateTo(
                                    targetValue = target.toFloat(),
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                            }
                            onSelect(target)
                        },
                        onDragCancel = {
                            isDragging = false
                            scope.launch {
                                progressAnim.animateTo(
                                    targetValue = selectedIndex.toFloat(),
                                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                                )
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            val deltaProgress = dragAmount / halfW
                            val nextProgress = (progressAnim.value + deltaProgress).coerceIn(-0.06f, 1.06f)
                            val isAbove = nextProgress > 0.5f
                            if (isAbove != crossedThreshold) {
                                crossedThreshold = isAbove
                                haptics.performSegmentSwitch()
                            }
                            scope.launch {
                                progressAnim.snapTo(nextProgress)
                            }
                        }
                    )
                }
                .pointerInput(Unit) {
                    val trackW = size.width.toFloat()
                    detectTapGestures { offset ->
                        val target = if (offset.x > trackW / 2f) 1 else 0
                        if (target != selectedIndex) {
                            haptics.performSegmentSwitch()
                        }
                        scope.launch {
                            progressAnim.animateTo(
                                targetValue = target.toFloat(),
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }
                        onSelect(target)
                    }
                }
                .padding(4.dp)
                .testTag("dual_sim_slider"),
            contentAlignment = Alignment.CenterStart
        ) {
            val totalWidthPx = constraints.maxWidth.toFloat()
            val totalHeightPx = constraints.maxHeight.toFloat()
            val paddingPx = with(density) { 4.dp.toPx() }
            val thumbWidthPx = (totalWidthPx - paddingPx * 2f) / 2f
            val thumbWidthDp = with(density) { thumbWidthPx.toDp() }
            val thumbHeightDp = with(density) { (totalHeightPx - paddingPx * 2f).toDp() }

            val currentProgress = progressAnim.value
            val thumbOffsetXPx = (currentProgress * thumbWidthPx)

            // 1. Neon Glowing Plasma Backdrop behind Thumb
            Box(
                modifier = Modifier
                    .offset { IntOffset(thumbOffsetXPx.roundToInt(), 0) }
                    .size(width = thumbWidthDp, height = thumbHeightDp)
                    .graphicsLayer {
                        scaleX = stretchX
                        scaleY = squashY
                    }
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                CyanPulse.copy(alpha = glowAlpha * 0.35f),
                                ElectricBlue.copy(alpha = glowAlpha * 0.20f),
                                Color.Transparent
                            )
                        ),
                        RoundedCornerShape(18.dp)
                    )
            )

            // 2. The Liquid Glass Thumb Pill
            Box(
                modifier = Modifier
                    .offset { IntOffset(thumbOffsetXPx.roundToInt(), 0) }
                    .size(width = thumbWidthDp, height = thumbHeightDp)
                    .graphicsLayer {
                        scaleX = stretchX
                        scaleY = squashY
                    }
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                CyanPulse.copy(alpha = if (glassStyle.isDark) 0.28f else 0.20f),
                                ElectricBlue.copy(alpha = if (glassStyle.isDark) 0.18f else 0.12f)
                            )
                        )
                    )
                    .border(
                        1.5.dp,
                        Brush.horizontalGradient(
                            colors = listOf(
                                CyanPulse.copy(alpha = 0.85f),
                                ElectricBlue.copy(alpha = 0.65f),
                                CyanPulse.copy(alpha = 0.45f)
                            )
                        ),
                        RoundedCornerShape(18.dp)
                    )
            )

            // 3. Foreground Row containing Slot 0 (SIM 1) and Slot 1 (SIM 2)
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Slot 0: SIM 1
                val proximity0 = (1f - currentProgress).coerceIn(0f, 1f)
                SimSlotItem(
                    sim = sim1,
                    proximity = proximity0,
                    modifier = Modifier.weight(1f)
                )

                // Slot 1: SIM 2
                val proximity1 = currentProgress.coerceIn(0f, 1f)
                SimSlotItem(
                    sim = sim2,
                    proximity = proximity1,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SimSlotItem(
    sim: SimCardInfo,
    proximity: Float,
    modifier: Modifier = Modifier
) {
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current
    val activeColor = CyanPulse
    val inactiveColor = glassStyle.textSecondary
    val iconTint = if (proximity > 0.5f) activeColor else glassStyle.textTertiary

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // SIM Card Chip Icon
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (proximity > 0.5f)
                        activeColor.copy(alpha = 0.18f * proximity)
                    else
                        glassStyle.cardSubtleBg
                )
                .border(
                    1.dp,
                    if (proximity > 0.5f)
                        activeColor.copy(alpha = 0.45f * proximity)
                    else
                        glassStyle.borderColor.copy(alpha = 0.2f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SimCard,
                contentDescription = sim.displayName,
                tint = iconTint,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = sim.displayName,
                    color = if (proximity > 0.5f) glassStyle.textPrimary else inactiveColor,
                    fontSize = 13.sp,
                    fontWeight = if (proximity > 0.5f) FontWeight.ExtraBold else FontWeight.Medium
                )
                if (proximity > 0.5f) {
                    Spacer(modifier = Modifier.width(5.dp))
                    // Pulsing active dot
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(CyanPulse)
                    )
                }
            }

            Text(
                text = sim.carrierName,
                color = if (proximity > 0.5f) CyanPulse else glassStyle.textTertiary,
                fontSize = 10.5.sp,
                fontWeight = if (proximity > 0.5f) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}

// Backward-compatible alias
@Composable
fun DualSimSelector(
    simCards: List<SimCardInfo>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LiquidGlassDualSimSlider(
        simCards = simCards,
        selectedIndex = selectedIndex,
        onSelect = onSelect,
        modifier = modifier
    )
}

@Composable
fun DataBreakdownGrid(
    usageSummary: com.example.model.NetworkUsageSummary,
    appLanguage: com.example.model.AppLanguage,
    modifier: Modifier = Modifier,
    onViewFullAnalytics: (() -> Unit)? = null
) {
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BreakdownMetricCard(
                title = com.example.model.AppStrings.todayCellular(appLanguage),
                value = FormatUtils.formatBytes(usageSummary.mobileTodayBytes),
                icon = Icons.Default.SignalCellularAlt,
                tint = glassStyle.accentPrimary,
                modifier = Modifier.weight(1f)
            )
            BreakdownMetricCard(
                title = com.example.model.AppStrings.todayWifi(appLanguage),
                value = FormatUtils.formatBytes(usageSummary.wifiTodayBytes),
                icon = Icons.Default.Wifi,
                tint = ElectricBlue,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BreakdownMetricCard(
                title = com.example.model.AppStrings.monthlyCellular(appLanguage),
                value = FormatUtils.formatBytes(usageSummary.mobileMonthBytes),
                icon = Icons.Default.CellTower,
                tint = glassStyle.accentSecondary,
                modifier = Modifier.weight(1f)
            )
            BreakdownMetricCard(
                title = com.example.model.AppStrings.monthlyWifi(appLanguage),
                value = FormatUtils.formatBytes(usageSummary.wifiMonthBytes),
                icon = Icons.Default.TrendingUp,
                tint = glassStyle.accentPrimary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun BreakdownMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current
    val dimensions = com.example.ui.theme.LocalAppDimensions.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(glassStyle.cardBackground)
            .border(1.dp, if (glassStyle.isDark) tint.copy(alpha = 0.25f) else glassStyle.borderColor, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = tint,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    color = glassStyle.textSecondary,
                    fontSize = dimensions.textCaption,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = glassStyle.textPrimary,
                fontSize = dimensions.textMetricValue,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EditDataCapDialog(
    currentLimitGB: Float,
    usedBytes: Long,
    appLanguage: com.example.model.AppLanguage,
    onDismiss: () -> Unit,
    onSaveLimit: (Float) -> Unit
) {
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current
    val appHaptics = com.example.ui.components.rememberAppHaptics()
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current

    var limitValue by remember(currentLimitGB) { mutableStateOf(currentLimitGB.coerceIn(1f, 1000f)) }
    var textInput by remember(currentLimitGB) {
        mutableStateOf(if (currentLimitGB % 1f == 0f) currentLimitGB.toInt().toString() else "%.1f".format(currentLimitGB))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable { onDismiss() }
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 440.dp)
                    .clickable(enabled = false) { }
            ) {
                GlassContainer(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = glassStyle.accentPrimary.copy(alpha = 0.55f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(glassStyle.accentPrimary.copy(alpha = if (glassStyle.isDark) 0.20f else 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PieChart,
                                        contentDescription = "Edit Cap",
                                        tint = glassStyle.accentPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = com.example.model.AppStrings.editCap(appLanguage),
                                        color = glassStyle.textPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = com.example.model.AppStrings.planLimitSection(appLanguage),
                                        color = glassStyle.textSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = glassStyle.textTertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Current Usage vs Cap banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(glassStyle.cardSubtleBg)
                                .border(0.8.dp, glassStyle.borderColor, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (appLanguage == com.example.model.AppLanguage.ARABIC) "المستهلك حالياً" else "Currently Used",
                                        color = glassStyle.textSecondary,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = FormatUtils.formatBytes(usedBytes),
                                        color = glassStyle.textPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = com.example.model.AppStrings.planCapLabel(appLanguage),
                                        color = glassStyle.textSecondary,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "%.1f GB".format(limitValue),
                                        color = glassStyle.accentPrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Custom Input Field
                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { newVal ->
                                if (newVal.all { it.isDigit() || it == '.' }) {
                                    textInput = newVal
                                    val parsed = newVal.toFloatOrNull()
                                    if (parsed != null && parsed in 0.1f..10000f) {
                                        limitValue = parsed.coerceIn(1f, 1000f)
                                    }
                                }
                            },
                            label = { Text(com.example.model.AppStrings.customLimitLabel(appLanguage), color = glassStyle.textTertiary, fontSize = 11.sp) },
                            trailingIcon = {
                                Text(
                                    text = "GB",
                                    color = glassStyle.accentPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
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
                                .testTag("custom_quota_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Interactive Slider
                        Slider(
                            value = limitValue.coerceIn(1f, 1000f),
                            onValueChange = {
                                limitValue = it
                                textInput = if (it % 1f == 0f) it.toInt().toString() else "%.1f".format(it)
                            },
                            valueRange = 1f..1000f,
                            colors = SliderDefaults.colors(
                                thumbColor = glassStyle.accentPrimary,
                                activeTrackColor = glassStyle.accentPrimary,
                                inactiveTrackColor = if (glassStyle.isDark) Color(0xFF162238) else Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("quota_slider")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Presets chips: 10GB, 50GB, 100GB, 250GB, 500GB, 1000GB
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(10f, 50f, 100f, 250f, 500f, 1000f).forEach { preset ->
                                val isSelected = (limitValue.toInt() == preset.toInt())
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) glassStyle.accentPrimary else glassStyle.cardBackground)
                                        .border(
                                            0.8.dp,
                                            if (isSelected) glassStyle.accentPrimary else glassStyle.borderColor,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            appHaptics.performSegmentSwitch()
                                            limitValue = preset
                                            textInput = preset.toInt().toString()
                                        }
                                        .padding(vertical = 7.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${preset.toInt()}G",
                                        color = if (isSelected) (if (glassStyle.isDark) Color.Black else Color.White) else glassStyle.textSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Action Buttons: Cancel & Save
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            TextButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = if (appLanguage == com.example.model.AppLanguage.ARABIC) "إلغاء" else "Cancel",
                                    color = glassStyle.textSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Button(
                                onClick = {
                                    appHaptics.performConfirm()
                                    val parsed = textInput.toFloatOrNull() ?: limitValue
                                    onSaveLimit(parsed.coerceIn(0.1f, 10000f))
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = glassStyle.accentPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1.4f)
                                    .height(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = if (glassStyle.isDark) Color.Black else Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (appLanguage == com.example.model.AppLanguage.ARABIC) "حفظ السقف" else "Save Cap",
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
    }
}
