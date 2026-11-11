package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.text.style.TextOverflow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.model.ThemeMode
import com.example.ui.MainViewModel
import com.example.ui.components.GlassContainer
import com.example.ui.components.rememberAppHaptics
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.LocalAppDimensions
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.RedAlert

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onRequestUsagePermission: () -> Unit,
    onOpenAppInfo: () -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (onNavigateBack != null) {
        BackHandler { onNavigateBack() }
    }

    val quota by viewModel.dataPlanQuota.collectAsStateWithLifecycle()
    val hasUsagePerm by viewModel.hasUsagePermission.collectAsStateWithLifecycle()
    val appThemeMode by viewModel.appThemeMode.collectAsStateWithLifecycle()
    val powerSavingMode by viewModel.powerSavingMode.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current
    val dimensions = LocalAppDimensions.current
    val appHaptics = rememberAppHaptics()

    // 1. Language Selection Card
    val languageCard: @Composable () -> Unit = {
        GlassContainer(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = glassStyle.accentPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = AppStrings.appLanguageSection(appLanguage),
                        color = glassStyle.textPrimary,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeOptionButton(
                        title = "العربية",
                        subtitle = "Arabic",
                        icon = Icons.Default.Translate,
                        isSelected = appLanguage == AppLanguage.ARABIC,
                        onClick = { viewModel.setAppLanguage(AppLanguage.ARABIC) },
                        modifier = Modifier.weight(1f)
                    )

                    ThemeOptionButton(
                        title = "English",
                        subtitle = "الإنجليزية",
                        icon = Icons.Default.Language,
                        isSelected = appLanguage == AppLanguage.ENGLISH,
                        onClick = { viewModel.setAppLanguage(AppLanguage.ENGLISH) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    // 2. Multi-Theme Engine Section (Small buttons, without explanation, mode name only)
    val multiThemeEngineCard: @Composable () -> Unit = {
        GlassContainer(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(glassStyle.accentPrimary.copy(alpha = if (glassStyle.isDark) 0.18f else 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Theme",
                                tint = glassStyle.accentPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Multi-Theme Engine",
                            color = glassStyle.textPrimary,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Active badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(glassStyle.cardSubtleBg)
                            .border(0.8.dp, glassStyle.borderColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = appThemeMode.getDisplayName(appLanguage),
                            color = glassStyle.accentPrimary,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 1: System Auto-Switch & Light Mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SmallThemeButton(
                        themeMode = com.example.model.AppThemeMode.SYSTEM,
                        icon = Icons.Default.BrightnessAuto,
                        accentColor = glassStyle.accentPrimary,
                        isSelected = appThemeMode == com.example.model.AppThemeMode.SYSTEM,
                        appLanguage = appLanguage,
                        onClick = {
                            appHaptics.performSegmentSwitch()
                            viewModel.setAppThemeMode(com.example.model.AppThemeMode.SYSTEM)
                        },
                        modifier = Modifier.weight(1f)
                    )

                    SmallThemeButton(
                        themeMode = com.example.model.AppThemeMode.LIGHT_MODE,
                        icon = Icons.Default.LightMode,
                        accentColor = com.example.ui.theme.LightModePrimary,
                        isSelected = appThemeMode == com.example.model.AppThemeMode.LIGHT_MODE,
                        appLanguage = appLanguage,
                        onClick = {
                            appHaptics.performSegmentSwitch()
                            viewModel.setAppThemeMode(com.example.model.AppThemeMode.LIGHT_MODE)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Row 2: Liquid Glass, Neon Cyan & AMOLED Black
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SmallThemeButton(
                        themeMode = com.example.model.AppThemeMode.LIQUID_GLASS_DEFAULT,
                        icon = Icons.Default.DarkMode,
                        accentColor = com.example.ui.theme.LiquidGlassDefaultPrimary,
                        isSelected = appThemeMode == com.example.model.AppThemeMode.LIQUID_GLASS_DEFAULT,
                        appLanguage = appLanguage,
                        onClick = {
                            appHaptics.performSegmentSwitch()
                            viewModel.setAppThemeMode(com.example.model.AppThemeMode.LIQUID_GLASS_DEFAULT)
                        },
                        modifier = Modifier.weight(1f)
                    )

                    SmallThemeButton(
                        themeMode = com.example.model.AppThemeMode.LIQUID_NEON_CYAN,
                        icon = Icons.Default.Palette,
                        accentColor = com.example.ui.theme.LiquidNeonCyanPrimary,
                        isSelected = appThemeMode == com.example.model.AppThemeMode.LIQUID_NEON_CYAN,
                        appLanguage = appLanguage,
                        onClick = {
                            appHaptics.performSegmentSwitch()
                            viewModel.setAppThemeMode(com.example.model.AppThemeMode.LIQUID_NEON_CYAN)
                        },
                        modifier = Modifier.weight(1f)
                    )

                    SmallThemeButton(
                        themeMode = com.example.model.AppThemeMode.AMOLED_PITCH_BLACK,
                        icon = Icons.Default.Eco,
                        accentColor = com.example.ui.theme.AmoledPitchBlackPrimary,
                        isSelected = appThemeMode == com.example.model.AppThemeMode.AMOLED_PITCH_BLACK,
                        appLanguage = appLanguage,
                        onClick = {
                            appHaptics.performSegmentSwitch()
                            viewModel.setAppThemeMode(com.example.model.AppThemeMode.AMOLED_PITCH_BLACK)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    // 4. Billing Cycle Reset Day
    val billingCycleCard: @Composable () -> Unit = {
        GlassContainer(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = AppStrings.billingCycleSection(appLanguage),
                            color = glassStyle.textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = AppStrings.billingCycleDay(quota.billingCycleStartDay, appLanguage),
                            color = glassStyle.textSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.updateBillingCycleDay(quota.billingCycleStartDay - 1) },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(glassStyle.cardSubtleBg)
                        ) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = glassStyle.accentPrimary)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "${quota.billingCycleStartDay}",
                            color = glassStyle.accentPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        IconButton(
                            onClick = { viewModel.updateBillingCycleDay(quota.billingCycleStartDay + 1) },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(glassStyle.cardSubtleBg)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = glassStyle.accentPrimary)
                        }
                    }
                }
            }
        }
    }

    // 5. Smart Alert Thresholds Card
    val smartAlertsCard: @Composable () -> Unit = {
        GlassContainer(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Alerts",
                        tint = AmberAlert,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.smartAlertsSection(appLanguage),
                        color = glassStyle.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 80% Alert Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = AppStrings.alert80Title(appLanguage),
                            color = glassStyle.textPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = AppStrings.alert80Desc(appLanguage),
                            color = glassStyle.textTertiary,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = quota.alertThreshold80,
                        onCheckedChange = { viewModel.toggleAlert80(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AmberAlert,
                            checkedTrackColor = AmberAlert.copy(alpha = 0.4f),
                            uncheckedThumbColor = glassStyle.textTertiary,
                            uncheckedTrackColor = if (glassStyle.isDark) Color(0xFF162238) else Color(0xFFCBD5E1)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 95% Alert Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = AppStrings.alert95Title(appLanguage),
                            color = glassStyle.textPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = AppStrings.alert95Desc(appLanguage),
                            color = glassStyle.textTertiary,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = quota.alertThreshold95,
                        onCheckedChange = { viewModel.toggleAlert95(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = RedAlert,
                            checkedTrackColor = RedAlert.copy(alpha = 0.4f),
                            uncheckedThumbColor = glassStyle.textTertiary,
                            uncheckedTrackColor = if (glassStyle.isDark) Color(0xFF162238) else Color(0xFFCBD5E1)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Test Heads-Up Notification Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(glassStyle.accentPrimary.copy(alpha = 0.12f))
                        .border(1.dp, glassStyle.accentPrimary.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                        .clickable { viewModel.sendTestNotification() }
                        .padding(horizontal = 14.dp, vertical = 11.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (appLanguage == com.example.model.AppLanguage.ARABIC) "اختبار الإشعار العائم (Heads-Up)" else "Test Heads-Up Alert Banner",
                            color = glassStyle.textPrimary,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (appLanguage == com.example.model.AppLanguage.ARABIC) "إرسال إشعار فوري للتأكد من ظهور البانر العائم في أعلى الشاشة" else "Simulate a live floating push banner with interactive buttons",
                            color = glassStyle.textTertiary,
                            fontSize = 10.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Test Notification",
                        tint = glassStyle.accentPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // 6. Power Saving & Battery Card
    val powerSavingCard: @Composable () -> Unit = {
        GlassContainer(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = "Power Saving",
                        tint = glassStyle.accentPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.powerSavingSection(appLanguage),
                        color = glassStyle.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = AppStrings.powerSavingTitle(appLanguage),
                            color = glassStyle.textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (powerSavingMode)
                                AppStrings.powerSavingActive(appLanguage)
                            else
                                AppStrings.powerSavingInactive(appLanguage),
                            color = if (powerSavingMode) glassStyle.accentPrimary else glassStyle.textTertiary,
                            fontSize = 11.sp
                        )
                    }

                    Switch(
                        checked = powerSavingMode,
                        onCheckedChange = { viewModel.togglePowerSavingMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = glassStyle.accentPrimary,
                            checkedTrackColor = glassStyle.accentPrimary.copy(alpha = 0.4f),
                            uncheckedThumbColor = glassStyle.textTertiary,
                            uncheckedTrackColor = if (glassStyle.isDark) Color(0xFF162238) else Color(0xFFCBD5E1)
                        ),
                        modifier = Modifier.testTag("power_saving_switch")
                    )
                }
            }
        }
    }

    // 7. Permissions Status Card (Cleaned - without PACKAGE_USAGE_STATS)
    val permissionsCard: @Composable () -> Unit = {
        GlassContainer(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Permissions",
                        tint = glassStyle.accentPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.permissionsSection(appLanguage),
                        color = glassStyle.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = AppStrings.usageAccess(appLanguage),
                            color = glassStyle.textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (hasUsagePerm) AppStrings.permGranted(appLanguage) else AppStrings.permNotGranted(appLanguage),
                            color = if (hasUsagePerm) glassStyle.accentPrimary else AmberAlert,
                            fontSize = 11.sp
                        )
                    }

                    if (!hasUsagePerm) {
                        Button(
                            onClick = onRequestUsagePermission,
                            colors = ButtonDefaults.buttonColors(containerColor = AmberAlert),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("settings_grant_permission_button")
                        ) {
                            Text(AppStrings.grantPerm(appLanguage), color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Granted",
                            tint = glassStyle.accentPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                if (!hasUsagePerm) {
                    Spacer(modifier = Modifier.height(10.dp))
                    var showGuide by remember { mutableStateOf(false) }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(glassStyle.cardSubtleBg)
                            .clickable { showGuide = true }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppStrings.restrictedGuideQ(appLanguage),
                            color = glassStyle.accentPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = AppStrings.viewGuide(appLanguage),
                            color = glassStyle.accentPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (showGuide) {
                        com.example.ui.components.RestrictedSettingsGuideDialog(
                            onDismiss = { showGuide = false },
                            onOpenAppInfo = onOpenAppInfo,
                            onOpenUsageSettings = onRequestUsagePermission,
                            appLanguage = appLanguage
                        )
                    }
                }
            }
        }
    }

    // 8. Developer Branding & App Version Card
    val developerCard: @Composable () -> Unit = {
        GlassContainer(
            modifier = Modifier.fillMaxWidth(),
            borderColor = glassStyle.accentSecondary.copy(alpha = 0.4f)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(glassStyle.cardSubtleBg)
                            .border(1.dp, glassStyle.accentSecondary.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Data Pulse",
                            modifier = Modifier.size(34.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Data Pulse",
                            color = glassStyle.textPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = AppStrings.developedBy(appLanguage),
                            color = glassStyle.accentPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = AppStrings.appVersionInfo(appLanguage),
                    color = glassStyle.textSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = AppStrings.appDescription(appLanguage),
                    color = glassStyle.textTertiary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = dimensions.screenPaddingHorizontal)
    ) {
        Spacer(modifier = Modifier.height(dimensions.screenPaddingVertical))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onNavigateBack != null) {
                Box(
                    modifier = Modifier
                        .size(dimensions.headerIconBoxSize)
                        .clip(RoundedCornerShape(12.dp))
                        .background(glassStyle.cardSubtleBg)
                        .border(1.dp, glassStyle.borderColor, RoundedCornerShape(12.dp))
                        .clickable { onNavigateBack() }
                        .testTag("settings_back_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = AppStrings.backToPrevious(appLanguage),
                        tint = glassStyle.accentPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column {
                Text(
                    text = AppStrings.settingsTitle(appLanguage),
                    color = glassStyle.textPrimary,
                    fontSize = dimensions.textTitleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = AppStrings.settingsSubtitle(appLanguage),
                    color = glassStyle.textSecondary,
                    fontSize = dimensions.textBodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(dimensions.sectionSpacing))

        if (dimensions.isTablet) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensions.sectionSpacing)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(dimensions.sectionSpacing)
                ) {
                    languageCard()
                    multiThemeEngineCard()
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(dimensions.sectionSpacing)
                ) {
                    billingCycleCard()
                    smartAlertsCard()
                    powerSavingCard()
                    permissionsCard()
                    developerCard()
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(dimensions.sectionSpacing)
            ) {
                languageCard()
                multiThemeEngineCard()
                billingCycleCard()
                smartAlertsCard()
                powerSavingCard()
                permissionsCard()
                developerCard()
            }
        }

        Spacer(modifier = Modifier.height(dimensions.sectionSpacing * 2))
    }
}

@Composable
fun ThemeOptionButton(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) glassStyle.accentPrimary.copy(alpha = if (glassStyle.isDark) 0.18f else 0.12f) else glassStyle.cardBackground)
            .border(
                1.dp,
                if (isSelected) glassStyle.accentPrimary else glassStyle.borderColor,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 7.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) glassStyle.accentPrimary else glassStyle.textTertiary,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = title,
                    color = if (isSelected) glassStyle.textPrimary else glassStyle.textSecondary,
                    fontSize = 11.5.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = if (isSelected) glassStyle.accentPrimary else glassStyle.textTertiary,
                    fontSize = 8.5.sp
                )
            }
        }
    }
}

@Composable
fun SmallThemeButton(
    themeMode: com.example.model.AppThemeMode,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    isSelected: Boolean,
    appLanguage: AppLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current

    val animatedBg by animateColorAsState(
        targetValue = if (isSelected) {
            accentColor.copy(alpha = if (glassStyle.isDark) 0.22f else 0.14f)
        } else {
            glassStyle.cardSubtleBg
        },
        animationSpec = tween(durationMillis = 200),
        label = "small_theme_bg"
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) accentColor else glassStyle.borderColor,
        animationSpec = tween(durationMillis = 200),
        label = "small_theme_border"
    )

    val animatedContentColor by animateColorAsState(
        targetValue = if (isSelected) {
            if (glassStyle.isDark) Color.White else glassStyle.textPrimary
        } else {
            glassStyle.textSecondary
        },
        animationSpec = tween(durationMillis = 200),
        label = "small_theme_text"
    )

    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(animatedBg)
            .border(
                width = if (isSelected) 1.2.dp else 0.8.dp,
                color = animatedBorderColor,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = themeMode.getDisplayName(appLanguage),
                tint = if (isSelected) accentColor else glassStyle.textTertiary,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = themeMode.getDisplayName(appLanguage),
                color = animatedContentColor,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
