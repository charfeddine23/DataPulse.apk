package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
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
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.CyanPulse
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
    val dailyAppQuotaGB by viewModel.dailyAppQuotaGB.collectAsStateWithLifecycle()
    val dailyAppAlertEnabled by viewModel.dailyAppAlertEnabled.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current

    var customLimitInput by remember(quota.planLimitGB) {
        mutableStateOf(if (quota.planLimitGB % 1f == 0f) quota.planLimitGB.toInt().toString() else "%.1f".format(quota.planLimitGB))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onNavigateBack != null) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
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
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column {
                Text(
                    text = AppStrings.settingsTitle(appLanguage),
                    color = glassStyle.textPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = AppStrings.settingsSubtitle(appLanguage),
                    color = glassStyle.textSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Language Selection Card (العربية / English)
        GlassContainer(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = glassStyle.accentPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.appLanguageSection(appLanguage),
                        color = glassStyle.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
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

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Comprehensive Liquid Glass Theme Engine (3 Themes)
        GlassContainer(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Theme",
                        tint = glassStyle.accentPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (appLanguage == AppLanguage.ARABIC) "محرك المظهر والألوان (Liquid Glass)" else "Theme Engine (Liquid Glass)",
                        color = glassStyle.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 1. LIQUID_GLASS_DEFAULT
                    ThemeOptionButton(
                        title = com.example.model.AppThemeMode.LIQUID_GLASS_DEFAULT.getDisplayName(appLanguage),
                        subtitle = if (appLanguage == AppLanguage.ARABIC) "أزرق وبنفسجي" else "Blue & Violet",
                        icon = Icons.Default.DarkMode,
                        isSelected = appThemeMode == com.example.model.AppThemeMode.LIQUID_GLASS_DEFAULT,
                        onClick = { viewModel.setAppThemeMode(com.example.model.AppThemeMode.LIQUID_GLASS_DEFAULT) },
                        modifier = Modifier.weight(1f)
                    )

                    // 2. LIQUID_NEON_CYAN
                    ThemeOptionButton(
                        title = com.example.model.AppThemeMode.LIQUID_NEON_CYAN.getDisplayName(appLanguage),
                        subtitle = if (appLanguage == AppLanguage.ARABIC) "سيان وزمردي" else "Cyan & Teal",
                        icon = Icons.Default.BrightnessAuto,
                        isSelected = appThemeMode == com.example.model.AppThemeMode.LIQUID_NEON_CYAN,
                        onClick = { viewModel.setAppThemeMode(com.example.model.AppThemeMode.LIQUID_NEON_CYAN) },
                        modifier = Modifier.weight(1f)
                    )

                    // 3. AMOLED_PITCH_BLACK
                    ThemeOptionButton(
                        title = com.example.model.AppThemeMode.AMOLED_PITCH_BLACK.getDisplayName(appLanguage),
                        subtitle = if (appLanguage == AppLanguage.ARABIC) "توفير بطارية" else "#000000 OLED",
                        icon = Icons.Default.Eco,
                        isSelected = appThemeMode == com.example.model.AppThemeMode.AMOLED_PITCH_BLACK,
                        onClick = { viewModel.setAppThemeMode(com.example.model.AppThemeMode.AMOLED_PITCH_BLACK) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Monthly Data Plan Limit Card (Freeform up to 1000GB)
        GlassContainer(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PieChart,
                        contentDescription = "Plan Quota",
                        tint = glassStyle.accentPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.planLimitSection(appLanguage),
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
                    Text(
                        text = AppStrings.planCapLabel(appLanguage),
                        color = glassStyle.textSecondary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "%.1f GB".format(quota.planLimitGB),
                        color = glassStyle.accentPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Freeform input for custom GB limits
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = customLimitInput,
                        onValueChange = { newVal ->
                            if (newVal.all { it.isDigit() || it == '.' }) {
                                customLimitInput = newVal
                                val parsed = newVal.toFloatOrNull()
                                if (parsed != null && parsed in 0.1f..10000f) {
                                    viewModel.updatePlanLimit(parsed)
                                }
                            }
                        },
                        label = { Text(AppStrings.customLimitLabel(appLanguage), color = glassStyle.textTertiary, fontSize = 11.sp) },
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
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = glassStyle.accentPrimary,
                            unfocusedBorderColor = glassStyle.borderColor,
                            focusedContainerColor = glassStyle.cardBackground,
                            unfocusedContainerColor = glassStyle.cardBackground,
                            focusedTextColor = glassStyle.textPrimary,
                            unfocusedTextColor = glassStyle.textPrimary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("custom_quota_input")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            val parsed = customLimitInput.toFloatOrNull()
                            if (parsed != null && parsed > 0) {
                                viewModel.updatePlanLimit(parsed.coerceIn(0.1f, 10000f))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = glassStyle.accentPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Text(AppStrings.set(appLanguage), color = if (glassStyle.isDark) Color.Black else Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Dynamic Slider spanning up to 1000 GB
                Slider(
                    value = quota.planLimitGB.coerceIn(1f, 1000f),
                    onValueChange = {
                        viewModel.updatePlanLimit(it)
                        customLimitInput = if (it % 1f == 0f) it.toInt().toString() else "%.1f".format(it)
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

                // Quick Presets including high caps (10GB, 50GB, 100GB, 250GB, 500GB, 1000GB)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(10f, 50f, 100f, 250f, 500f, 1000f).forEach { preset ->
                        val isSelected = (quota.planLimitGB == preset)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) glassStyle.accentPrimary else glassStyle.cardBackground)
                                .border(
                                    1.dp,
                                    if (isSelected) glassStyle.accentPrimary else glassStyle.borderColor,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    viewModel.updatePlanLimit(preset)
                                    customLimitInput = preset.toInt().toString()
                                }
                                .padding(vertical = 8.dp),
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
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Billing Cycle Reset Day
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

        Spacer(modifier = Modifier.height(14.dp))

        // 5. Smart Alert Thresholds Card
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

                // Custom Daily App Usage Alert (Configurable 0.5 GB to 50 GB)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(glassStyle.cardSubtleBg)
                        .border(1.dp, glassStyle.accentPrimary.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (appLanguage == AppLanguage.ARABIC) "تنبيه استهلاك التطبيقات اليومي" else "Daily App Usage Alert",
                                    color = glassStyle.textPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (appLanguage == AppLanguage.ARABIC)
                                        "إشعار فوري عند تجاوز أي تطبيق لحد الاستهلاك المحدد في اليوم"
                                    else
                                        "Notify instantly when any app exceeds daily limit today",
                                    color = glassStyle.textTertiary,
                                    fontSize = 11.sp
                                )
                            }
                            Switch(
                                checked = dailyAppAlertEnabled,
                                onCheckedChange = { viewModel.toggleDailyAppAlert(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = glassStyle.accentPrimary,
                                    checkedTrackColor = glassStyle.accentPrimary.copy(alpha = 0.4f),
                                    uncheckedThumbColor = glassStyle.textTertiary,
                                    uncheckedTrackColor = if (glassStyle.isDark) Color(0xFF162238) else Color(0xFFCBD5E1)
                                )
                            )
                        }

                        if (dailyAppAlertEnabled) {
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (appLanguage == AppLanguage.ARABIC) "الحد اليومي المخصص لكل تطبيق:" else "Custom Daily Limit per App:",
                                    color = glassStyle.textSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            val next = (dailyAppQuotaGB - 0.5f).coerceAtLeast(0.5f)
                                            viewModel.updateDailyAppQuotaGB(next)
                                        },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(glassStyle.cardBackground)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Remove,
                                            contentDescription = "Decrease",
                                            tint = glassStyle.accentPrimary
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    val quotaDisplay = if (dailyAppQuotaGB % 1f == 0f) "${dailyAppQuotaGB.toInt()} GB" else "%.1f GB".format(dailyAppQuotaGB)
                                    Text(
                                        text = quotaDisplay,
                                        color = glassStyle.accentPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    IconButton(
                                        onClick = {
                                            val next = (dailyAppQuotaGB + 0.5f).coerceAtMost(50f)
                                            viewModel.updateDailyAppQuotaGB(next)
                                        },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(glassStyle.cardBackground)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Increase",
                                            tint = glassStyle.accentPrimary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Quick preset chips (1GB, 2GB, 3GB, 5GB, 10GB)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(1.0f, 2.0f, 3.0f, 5.0f, 10.0f).forEach { preset ->
                                    val isSelected = (kotlin.math.abs(dailyAppQuotaGB - preset) < 0.05f)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) glassStyle.accentPrimary else glassStyle.cardBackground)
                                            .clickable { viewModel.updateDailyAppQuotaGB(preset) }
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${preset.toInt()} GB",
                                            color = if (isSelected) (if (glassStyle.isDark) Color.Black else Color.White) else glassStyle.textSecondary,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 6. Power Saving & Battery Card
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

        Spacer(modifier = Modifier.height(14.dp))

        // 7. Permissions Status Card (Cleaned - without PACKAGE_USAGE_STATS)
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

        Spacer(modifier = Modifier.height(14.dp))

        // 8. Developer Branding & App Version Card
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

        Spacer(modifier = Modifier.height(24.dp))
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
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) glassStyle.accentPrimary.copy(alpha = if (glassStyle.isDark) 0.18f else 0.12f) else glassStyle.cardBackground)
            .border(
                1.dp,
                if (isSelected) glassStyle.accentPrimary else glassStyle.borderColor,
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) glassStyle.accentPrimary else glassStyle.textTertiary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = if (isSelected) glassStyle.textPrimary else glassStyle.textSecondary,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = if (isSelected) glassStyle.accentPrimary else glassStyle.textTertiary,
                fontSize = 9.sp
            )
        }
    }
}
