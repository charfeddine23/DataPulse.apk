package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.model.AppUsageItem
import com.example.model.FormatUtils
import com.example.ui.MainViewModel
import com.example.ui.screens.AppIconImage
import com.example.ui.theme.LocalLiquidGlass

/**
 * Liquid Glass dialog for customizing per-app daily quota limits and notification alerts.
 * Triggered on long-pressing an app item in the usage list.
 */
@Composable
fun PerAppQuotaCustomizationDialog(
    item: AppUsageItem,
    viewModel: MainViewModel,
    appLanguage: AppLanguage,
    onDismiss: () -> Unit
) {
    val glassStyle = LocalLiquidGlass.current
    val savedSettings by viewModel.getPerAppSettingsFlow(item.packageName)
        .collectAsStateWithLifecycle(initialValue = null)

    var dailyLimitGB by remember { mutableFloatStateOf(3.0f) }
    var alertEnabled by remember { mutableStateOf(true) }
    var initialized by remember { mutableStateOf(false) }

    // Synchronize initial state once from DataStore
    LaunchedEffect(savedSettings) {
        savedSettings?.let {
            if (!initialized) {
                dailyLimitGB = it.dailyLimitGB
                alertEnabled = it.alertEnabled
                initialized = true
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .padding(20.dp)
                .widthIn(max = 460.dp)
                .fillMaxWidth()
        ) {
            GlassContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("per_app_quota_dialog"),
                shape = RoundedCornerShape(26.dp),
                backgroundColor = glassStyle.cardBackground,
                borderColor = glassStyle.borderColor
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Header Row: App Icon + Name + Dismiss Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIconImage(drawable = item.icon, appName = item.appName)

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.appName,
                                color = glassStyle.textPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = item.packageName,
                                color = glassStyle.textTertiary,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(glassStyle.cardSubtleBg)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = glassStyle.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title & Description Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(glassStyle.accentPrimary.copy(alpha = 0.12f))
                            .border(1.dp, glassStyle.accentPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = glassStyle.accentPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.perAppDialogTitle(appLanguage),
                            color = glassStyle.textPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Current Usage summary card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(glassStyle.cardSubtleBg)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = AppStrings.perAppCurrentUsage(appLanguage),
                                    color = glassStyle.textTertiary,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = FormatUtils.formatBytes(item.totalBytes),
                                    color = glassStyle.textPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            val consumedGB = item.totalBytes / (1024f * 1024f * 1024f)
                            val pct = (consumedGB / dailyLimitGB.coerceAtLeast(0.1f)).coerceIn(0f, 1f)
                            val pctText = "%.1f%%".format(pct * 100f)

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "$pctText / ${"%.1f".format(dailyLimitGB)} GB",
                                    color = if (pct >= 1f) Color(0xFFEF4444) else glassStyle.accentSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(90.dp)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(Color.White.copy(alpha = 0.1f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(pct)
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(
                                                if (pct >= 1f) Brush.horizontalGradient(listOf(Color(0xFFEF4444), Color(0xFFDC2626)))
                                                else Brush.horizontalGradient(listOf(glassStyle.accentSecondary, glassStyle.accentPrimary))
                                            )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stepper: Daily Limit (GB)
                    Text(
                        text = AppStrings.perAppDailyLimit(appLanguage),
                        color = glassStyle.textSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(glassStyle.cardSubtleBg)
                            .border(1.dp, glassStyle.borderColor, RoundedCornerShape(16.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Decrement Button (- 0.5 GB)
                        IconButton(
                            onClick = {
                                dailyLimitGB = (dailyLimitGB - 0.5f).coerceAtLeast(0.5f)
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(glassStyle.accentPrimary.copy(alpha = 0.18f))
                                .testTag("per_app_limit_decrement")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease",
                                tint = glassStyle.accentPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Display Value
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "%.1f GB".format(dailyLimitGB),
                                color = glassStyle.accentPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = AppStrings.perAppDefaultIndicator(appLanguage),
                                color = glassStyle.textTertiary,
                                fontSize = 10.sp
                            )
                        }

                        // Increment Button (+ 0.5 GB)
                        IconButton(
                            onClick = {
                                dailyLimitGB = (dailyLimitGB + 0.5f).coerceAtMost(50.0f)
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(glassStyle.accentPrimary.copy(alpha = 0.18f))
                                .testTag("per_app_limit_increment")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase",
                                tint = glassStyle.accentPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset chips (1G, 2G, 3G, 5G, 10G)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val presets = listOf(1.0f, 2.0f, 3.0f, 5.0f, 10.0f)
                        presets.forEach { preset ->
                            val isSelected = kotlin.math.abs(dailyLimitGB - preset) < 0.05f
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) glassStyle.accentPrimary
                                        else glassStyle.cardSubtleBg
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) glassStyle.accentPrimary else glassStyle.borderColor,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { dailyLimitGB = preset }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${preset.toInt()}G",
                                    color = if (isSelected) (if (glassStyle.isDark) Color.Black else Color.White) else glassStyle.textSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Alert Toggle Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(glassStyle.cardSubtleBg)
                            .border(1.dp, glassStyle.borderColor, RoundedCornerShape(14.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = if (alertEnabled) glassStyle.accentSecondary else glassStyle.textTertiary,
                            modifier = Modifier.size(22.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = AppStrings.perAppAlertToggle(appLanguage),
                                color = glassStyle.textPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = AppStrings.perAppAlertDesc(appLanguage),
                                color = glassStyle.textTertiary,
                                fontSize = 10.sp,
                                lineHeight = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Switch(
                            checked = alertEnabled,
                            onCheckedChange = { alertEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = if (glassStyle.isDark) Color.Black else Color.White,
                                checkedTrackColor = glassStyle.accentPrimary,
                                uncheckedThumbColor = glassStyle.textTertiary,
                                uncheckedTrackColor = glassStyle.cardBackground
                            ),
                            modifier = Modifier.testTag("per_app_alert_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Buttons (Cancel / Save)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, glassStyle.borderColor),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = glassStyle.textSecondary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("per_app_quota_cancel_button")
                        ) {
                            Text(
                                text = AppStrings.perAppCancelBtn(appLanguage),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.savePerAppSettings(
                                    packageName = item.packageName,
                                    dailyLimitGB = dailyLimitGB,
                                    alertEnabled = alertEnabled
                                )
                                onDismiss()
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = glassStyle.accentPrimary,
                                contentColor = if (glassStyle.isDark) Color.Black else Color.White
                            ),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(46.dp)
                                .testTag("save_per_app_quota_button")
                        ) {
                            Text(
                                text = AppStrings.perAppSaveBtn(appLanguage),
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
