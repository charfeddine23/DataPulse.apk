package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WarningAmber
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.ThemeMode
import com.example.ui.MainViewModel
import com.example.ui.components.GlassContainer
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.RedAlert
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onRequestUsagePermission: () -> Unit,
    onOpenAppInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quota by viewModel.dataPlanQuota.collectAsStateWithLifecycle()
    val hasUsagePerm by viewModel.hasUsagePermission.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val powerSavingMode by viewModel.powerSavingMode.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

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

        Text(
            text = "Quotas & Preferences",
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Customize your theme, data limits up to 1000GB, and smart alerts",
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Theme Selection Card (Light Mode, Dark Mode, Follow System)
        GlassContainer(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Theme",
                        tint = CyanPulse,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "THEME & APPEARANCE",
                        color = TextPrimary,
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
                    ThemeOptionButton(
                        title = "System",
                        subtitle = "تتبع النظام",
                        icon = Icons.Default.BrightnessAuto,
                        isSelected = themeMode == ThemeMode.SYSTEM,
                        onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) },
                        modifier = Modifier.weight(1f)
                    )

                    ThemeOptionButton(
                        title = "Light",
                        subtitle = "الوضع النهاري",
                        icon = Icons.Default.LightMode,
                        isSelected = themeMode == ThemeMode.LIGHT,
                        onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) },
                        modifier = Modifier.weight(1f)
                    )

                    ThemeOptionButton(
                        title = "Dark",
                        subtitle = "الوضع الليلي",
                        icon = Icons.Default.DarkMode,
                        isSelected = themeMode == ThemeMode.DARK,
                        onClick = { viewModel.setThemeMode(ThemeMode.DARK) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Monthly Data Plan Limit Card (Freeform up to 1000GB)
        GlassContainer(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PieChart,
                        contentDescription = "Plan Quota",
                        tint = CyanPulse,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MONTHLY DATA PLAN LIMIT",
                        color = TextPrimary,
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
                        text = "Plan Cap (up to 1000 GB):",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "%.1f GB".format(quota.planLimitGB),
                        color = CyanPulse,
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
                            // Allow numbers and decimal point
                            if (newVal.all { it.isDigit() || it == '.' }) {
                                customLimitInput = newVal
                                val parsed = newVal.toFloatOrNull()
                                if (parsed != null && parsed in 0.1f..10000f) {
                                    viewModel.updatePlanLimit(parsed)
                                }
                            }
                        },
                        label = { Text("Custom Limit (GB)", color = TextTertiary, fontSize = 11.sp) },
                        trailingIcon = {
                            Text(
                                text = "GB",
                                color = CyanPulse,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPulse,
                            unfocusedBorderColor = Color(0x33FFFFFF),
                            focusedContainerColor = Color(0xFF0F1728),
                            unfocusedContainerColor = Color(0xFF0F1728),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
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
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPulse),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Text("Set", color = Color.Black, fontWeight = FontWeight.Bold)
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
                        thumbColor = CyanPulse,
                        activeTrackColor = CyanPulse,
                        inactiveTrackColor = Color(0xFF162238)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quota_slider")
                )

                // Quick Presets including high caps (50GB, 100GB, 250GB, 500GB, 1000GB)
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
                                .background(if (isSelected) CyanPulse else Color(0xFF0F1728))
                                .border(
                                    1.dp,
                                    if (isSelected) CyanPulse else Color(0x22FFFFFF),
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
                                color = if (isSelected) Color.Black else TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Billing Cycle Reset Day
        GlassContainer(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Billing Cycle Reset Day",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Day ${quota.billingCycleStartDay} of every month",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.updateBillingCycleDay(quota.billingCycleStartDay - 1) },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0F1728))
                        ) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = CyanPulse)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "${quota.billingCycleStartDay}",
                            color = CyanPulse,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        IconButton(
                            onClick = { viewModel.updateBillingCycleDay(quota.billingCycleStartDay + 1) },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0F1728))
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = CyanPulse)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Smart Alert Thresholds Card
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
                        text = "SMART QUOTA ALERTS",
                        color = TextPrimary,
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
                            text = "Warning Alert at 80%",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Notifies when 80% of data plan is consumed",
                            color = TextTertiary,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = quota.alertThreshold80,
                        onCheckedChange = { viewModel.toggleAlert80(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AmberAlert,
                            checkedTrackColor = AmberAlert.copy(alpha = 0.4f),
                            uncheckedThumbColor = TextTertiary,
                            uncheckedTrackColor = Color(0xFF162238)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 95% Alert Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Critical Alert at 95%",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Urgent alert before hitting plan cap",
                            color = TextTertiary,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = quota.alertThreshold95,
                        onCheckedChange = { viewModel.toggleAlert95(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = RedAlert,
                            checkedTrackColor = RedAlert.copy(alpha = 0.4f),
                            uncheckedThumbColor = TextTertiary,
                            uncheckedTrackColor = Color(0xFF162238)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 5. Power Saving & Battery Card
        GlassContainer(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = "Power Saving",
                        tint = CyanPulse,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "POWER SAVING & BATTERY",
                        color = TextPrimary,
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
                            text = "Power Saving Mode",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (powerSavingMode)
                                "Eco Mode Active: Network polling reduced to 3s to save battery"
                            else
                                "High Accuracy: Real-time 1s network polling active",
                            color = if (powerSavingMode) CyanPulse else TextTertiary,
                            fontSize = 11.sp
                        )
                    }

                    Switch(
                        checked = powerSavingMode,
                        onCheckedChange = { viewModel.togglePowerSavingMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyanPulse,
                            checkedTrackColor = CyanPulse.copy(alpha = 0.4f),
                            uncheckedThumbColor = TextTertiary,
                            uncheckedTrackColor = Color(0xFF162238)
                        ),
                        modifier = Modifier.testTag("power_saving_switch")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 6. Permissions Status Card (Cleaned - without PACKAGE_USAGE_STATS)
        GlassContainer(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Permissions",
                        tint = CyanPulse,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SYSTEM PERMISSIONS",
                        color = TextPrimary,
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
                            text = "Usage Access",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (hasUsagePerm) "Granted - Full historical analytics active" else "Not Granted - Real-time session only",
                            color = if (hasUsagePerm) CyanPulse else AmberAlert,
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
                            Text("Grant", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Granted",
                            tint = CyanPulse,
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
                            .background(Color(0xFF131D30))
                            .clickable { showGuide = true }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Setting disabled or restricted on Android 13+?",
                            color = CyanPulse,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "View Guide →",
                            color = CyanPulse,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (showGuide) {
                        com.example.ui.components.RestrictedSettingsGuideDialog(
                            onDismiss = { showGuide = false },
                            onOpenAppInfo = onOpenAppInfo,
                            onOpenUsageSettings = onRequestUsagePermission
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 6. Developer Branding & App Version Card
        GlassContainer(
            modifier = Modifier.fillMaxWidth(),
            borderColor = NeonPurple.copy(alpha = 0.4f)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NeonPurple.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = "Developer",
                            tint = NeonPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Data Pulse",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Developed by Charfeddine Ka",
                            color = CyanPulse,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Version 1.0.0 (v1.0.0) | Last Updated November 2026",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Crafted with Modern Dark Mode & Light Mode Liquid Glass Aesthetics, Jetpack Compose, NetworkStatsManager, and Dual SIM readiness. Guaranteed seamless update installation over previous versions.",
                    color = TextTertiary,
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
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) CyanPulse.copy(alpha = 0.18f) else Color(0xFF0F1728))
            .border(
                1.dp,
                if (isSelected) CyanPulse else Color(0x22FFFFFF),
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
                tint = if (isSelected) CyanPulse else TextTertiary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = if (isSelected) TextPrimary else TextSecondary,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = if (isSelected) CyanPulse else TextTertiary,
                fontSize = 9.sp
            )
        }
    }
}
