package com.example.ui.navigation

import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.AnalyticsTipPopupDialog
import com.example.ui.components.AppUpdateDialog
import com.example.ui.components.LocalRootFlameOverlay
import com.example.ui.components.PersistentTopAppBar
import com.example.ui.components.RootFlameOverlayCanvas
import com.example.ui.components.RootFlameOverlayController
import com.example.ui.components.tactileSpringPress
import com.example.update.AppUpdateUiState
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DnsControlScreen
import com.example.ui.screens.RankScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SmartForecastScreen
import com.example.ui.screens.SpeedMeterScreen
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.TextPrimary
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.ui.theme.TextTertiary

enum class NavDestination(val icon: ImageVector, val tag: String) {
    DASHBOARD(Icons.Default.GraphicEq, "nav_dashboard"),
    USAGE(Icons.Default.Equalizer, "nav_usage"),
    SPEED(Icons.Default.Speed, "nav_speed"),
    DNS(Icons.Default.Shield, "nav_dns"),
    FORECAST(Icons.Default.AutoAwesome, "nav_forecast"),
    SETTINGS(Icons.Default.Settings, "nav_settings"),
    RANK(Icons.Default.EmojiEvents, "nav_rank")
}

fun NavDestination.getLabel(lang: AppLanguage): String = when (this) {
    NavDestination.DASHBOARD -> AppStrings.navPulse(lang)
    NavDestination.USAGE -> AppStrings.navUsage(lang)
    NavDestination.SPEED -> AppStrings.navSpeed(lang)
    NavDestination.DNS -> AppStrings.navDns(lang)
    NavDestination.FORECAST -> AppStrings.navForecast(lang)
    NavDestination.SETTINGS -> AppStrings.navSettings(lang)
    NavDestination.RANK -> AppStrings.navRank(lang)
}

@Composable
fun DataPulseApp(
    viewModel: MainViewModel,
    onRequestUsagePermission: () -> Unit,
    onOpenAppInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentDestination by remember { mutableStateOf(NavDestination.DASHBOARD) }
    var previousDestination by remember { mutableStateOf(NavDestination.DASHBOARD) }
    var showUsageTipPopup by remember { mutableStateOf(false) }
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val speedSample by viewModel.speedSample.collectAsStateWithLifecycle()
    val targetNavDestination by viewModel.targetNavDestination.collectAsStateWithLifecycle()
    val appUpdateState by viewModel.appUpdateState.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    androidx.compose.runtime.LaunchedEffect(targetNavDestination) {
        targetNavDestination?.let { destString ->
            when (destString.uppercase()) {
                "FORECAST" -> {
                    if (currentDestination != NavDestination.FORECAST) {
                        previousDestination = currentDestination
                        currentDestination = NavDestination.FORECAST
                    }
                }
                "USAGE" -> {
                    if (currentDestination != NavDestination.USAGE) {
                        previousDestination = currentDestination
                        currentDestination = NavDestination.USAGE
                    }
                }
                "DASHBOARD" -> {
                    if (currentDestination != NavDestination.DASHBOARD) {
                        previousDestination = currentDestination
                        currentDestination = NavDestination.DASHBOARD
                    }
                }
                "SPEED" -> {
                    if (currentDestination != NavDestination.SPEED) {
                        previousDestination = currentDestination
                        currentDestination = NavDestination.SPEED
                    }
                }
                "DNS" -> {
                    if (currentDestination != NavDestination.DNS) {
                        previousDestination = currentDestination
                        currentDestination = NavDestination.DNS
                    }
                }
                "SETTINGS" -> {
                    if (currentDestination != NavDestination.SETTINGS) {
                        previousDestination = currentDestination
                        currentDestination = NavDestination.SETTINGS
                    }
                }
                "RANK" -> {
                    if (currentDestination != NavDestination.RANK) {
                        previousDestination = currentDestination
                        currentDestination = NavDestination.RANK
                    }
                }
            }
            viewModel.setTargetNavDestination(null)
        }
    }

    fun navigateTo(dest: NavDestination) {
        if (dest != currentDestination) {
            previousDestination = currentDestination
            currentDestination = dest
        }
    }

    fun navigateBack() {
        currentDestination = previousDestination
    }

    val dimensions = com.example.ui.theme.LocalAppDimensions.current
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current
    val rootFlameController = remember { RootFlameOverlayController() }
    var topBarHeightPx by remember { mutableFloatStateOf(0f) }

    CompositionLocalProvider(LocalRootFlameOverlay provides rootFlameController) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(brush = glassStyle.backdropBrush)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    PersistentTopAppBar(
                        speedSample = speedSample,
                        appLanguage = appLanguage,
                        isSettingsActive = (currentDestination == NavDestination.SETTINGS),
                        isForecastActive = (currentDestination == NavDestination.FORECAST),
                        title = when (currentDestination) {
                            NavDestination.USAGE -> "Usage"
                            NavDestination.SETTINGS -> AppStrings.navSettings(appLanguage)
                            else -> "Data Pulse"
                        },
                        onUsageTipClick = if (currentDestination == NavDestination.USAGE) {
                            { showUsageTipPopup = true }
                        } else {
                            null
                        },
                        onOpenForecast = {
                            if (currentDestination == NavDestination.FORECAST) {
                                navigateBack()
                            } else {
                                navigateTo(NavDestination.FORECAST)
                            }
                        },
                        onOpenSettings = {
                            if (currentDestination == NavDestination.SETTINGS) {
                                navigateBack()
                            } else {
                                navigateTo(NavDestination.SETTINGS)
                            }
                        },
                        modifier = Modifier.onSizeChanged { size ->
                            topBarHeightPx = size.height.toFloat()
                        }
                    )
                },
                bottomBar = {
                    LiquidGlassBottomNav(
                        currentDestination = currentDestination,
                        appLanguage = appLanguage,
                        onSelectDestination = { navigateTo(it) }
                    )
                }
            ) { innerPadding ->
                com.example.ui.theme.AdaptiveScreenContainer(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) { appDimensions ->
                    Crossfade(targetState = currentDestination, label = "screen_crossfade") { dest ->
                        when (dest) {
                            NavDestination.DASHBOARD -> DashboardScreen(
                                viewModel = viewModel,
                                onRequestUsagePermission = onRequestUsagePermission,
                                onOpenAppInfo = onOpenAppInfo,
                                onNavigateToSettings = { navigateTo(NavDestination.SETTINGS) },
                                onNavigateToAnalytics = { navigateTo(NavDestination.USAGE) }
                            )
                            NavDestination.USAGE -> AnalyticsScreen(
                                viewModel = viewModel,
                                onRequestUsagePermission = onRequestUsagePermission,
                                onOpenAppInfo = onOpenAppInfo
                            )
                            NavDestination.SPEED -> SpeedMeterScreen(
                                viewModel = viewModel
                            )
                            NavDestination.DNS -> DnsControlScreen(
                                viewModel = viewModel
                            )
                            NavDestination.FORECAST -> SmartForecastScreen(
                                viewModel = viewModel,
                                onNavigateToSettings = { navigateTo(NavDestination.SETTINGS) },
                                onNavigateBack = { navigateBack() }
                            )
                            NavDestination.SETTINGS -> SettingsScreen(
                                viewModel = viewModel,
                                onRequestUsagePermission = onRequestUsagePermission,
                                onOpenAppInfo = onOpenAppInfo,
                                onNavigateBack = { navigateBack() },
                                onNavigateToDns = { navigateTo(NavDestination.DNS) }
                            )
                            NavDestination.RANK -> RankScreen(
                                viewModel = viewModel,
                                onRequestUsagePermission = onRequestUsagePermission,
                                onOpenAppInfo = onOpenAppInfo
                            )
                        }
                    }
                }
            }

            // Screen-top flame overlay for Pulse pull-to-refresh (emerges from very top edge of screen)
            if (currentDestination == NavDestination.DASHBOARD) {
                RootFlameOverlayCanvas(
                    controller = rootFlameController,
                    topBarHeightPx = topBarHeightPx,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }

    if (showUsageTipPopup) {
        AnalyticsTipPopupDialog(
            appLanguage = appLanguage,
            onDismiss = { showUsageTipPopup = false }
        )
    }

    if (appUpdateState is AppUpdateUiState.Available ||
        appUpdateState is AppUpdateUiState.Downloading ||
        appUpdateState is AppUpdateUiState.ReadyToInstall ||
        appUpdateState is AppUpdateUiState.NeedsInstallPermission ||
        (appUpdateState is AppUpdateUiState.Error && (appUpdateState as AppUpdateUiState.Error).info != null)
    ) {
        AppUpdateDialog(
            state = appUpdateState,
            appLanguage = appLanguage,
            onDismiss = { viewModel.dismissUpdateDialog() },
            onStartUpdate = { info -> viewModel.startInAppUpdateDownload(context, info) },
            onInstallNow = { ready -> viewModel.installDownloadedUpdate(context, ready) },
            onRequestInstallPermission = { viewModel.requestInstallPermission(context) }
        )
    }
}

@Composable
fun LiquidGlassBottomNav(
    currentDestination: NavDestination,
    appLanguage: AppLanguage,
    onSelectDestination: (NavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current
    val dimensions = com.example.ui.theme.LocalAppDimensions.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(
                horizontal = dimensions.screenPaddingHorizontal,
                vertical = dimensions.screenPaddingVertical
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = dimensions.bottomNavMaxWidth)
                .then(
                    if (!glassStyle.isDark) {
                        Modifier.shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(24.dp),
                            spotColor = Color(0x140F172A),
                            ambientColor = Color(0x0A0F172A)
                        )
                    } else Modifier
                )
                .clip(RoundedCornerShape(24.dp))
                .background(brush = glassStyle.backdropBrush)
                .border(
                    1.dp,
                    if (glassStyle.isDark) glassStyle.rimLightBrush else Brush.linearGradient(listOf(Color(0x80FFFFFF), glassStyle.borderColor, Color(0x40FFFFFF))),
                    RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sequence displayed on screen (Left to Right):
                // 1. DASHBOARD (النبض - أقصى اليسار / Far Left)
                // 2. USAGE (الاستهلاك / Apps Usage)
                // 3. SPEED (عداد السرعة)
                // 4. DNS (درع DNS الذكي - صفحة منفردة)
                // 5. RANK (الرتبة / Rank - أقصى اليمين / Far Right)
                val navigationItems = listOf(
                    NavDestination.DASHBOARD,
                    NavDestination.USAGE,
                    NavDestination.SPEED,
                    NavDestination.DNS,
                    NavDestination.RANK
                )

            navigationItems.forEach { destination ->
                val isSelected = (destination == currentDestination)
                val destLabel = destination.getLabel(appLanguage)

                Box(
                    modifier = Modifier
                        .tactileSpringPress(
                            pressedScale = 0.91f,
                            onClick = { onSelectDestination(destination) }
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) {
                                glassStyle.accentPrimary.copy(alpha = if (glassStyle.isDark) 0.20f else 0.12f)
                            } else Color.Transparent
                        )
                        .border(
                            width = if (isSelected) 0.9.dp else 0.dp,
                            color = if (isSelected) glassStyle.accentPrimary.copy(alpha = if (glassStyle.isDark) 0.45f else 0.35f) else Color.Transparent,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag(destination.tag),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = destLabel,
                            tint = if (isSelected) glassStyle.accentPrimary else glassStyle.textTertiary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = destLabel,
                            color = if (isSelected) glassStyle.textPrimary else glassStyle.textTertiary,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
        }
        }
    }
}




