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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AppLanguage
import com.example.model.AppUsageItem
import com.example.ui.screens.AppIconImage
import com.example.ui.theme.LocalLiquidGlass

/**
 * Modern Neumorphic (Soft UI) Dialog for customizing per-app daily limit and alert settings on long-press.
 */
@Composable
fun PerAppCustomLimitDialog(
    appItem: AppUsageItem,
    initialDailyLimitGB: Float,
    initialAlertEnabled: Boolean,
    appLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (dailyLimitGB: Float, alertEnabled: Boolean) -> Unit
) {
    val glassStyle = LocalLiquidGlass.current
    val dialogShape = RoundedCornerShape(26.dp)

    var dailyLimit by remember(initialDailyLimitGB) { mutableStateOf(initialDailyLimitGB) }
    var alertEnabled by remember(initialAlertEnabled) { mutableStateOf(initialAlertEnabled) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .neumorphicDepth(
                    shape = dialogShape,
                    lightColor = glassStyle.neumorphicHighlight,
                    darkColor = glassStyle.neumorphicShadow,
                    offsetDp = 6.dp
                )
                .clip(dialogShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(glassStyle.neumorphicSurface, glassStyle.neumorphicBase)
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            glassStyle.neumorphicBorderLight,
                            glassStyle.accentPrimary.copy(alpha = 0.4f),
                            glassStyle.neumorphicBorderDark
                        )
                    ),
                    shape = dialogShape
                )
                .padding(22.dp)
        ) {
            Column {
                // Header with App Icon and Close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIconImage(drawable = appItem.icon, appName = appItem.appName)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = appItem.appName,
                                color = glassStyle.textPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = appItem.packageName,
                                color = glassStyle.textTertiary,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }

                    NeumorphicIconButton(
                        icon = Icons.Default.Close,
                        contentDescription = "Close",
                        onClick = onDismiss,
                        sizeDp = 32.dp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Custom Daily Limit Section in an indented neumorphic card
                val sectionShape = RoundedCornerShape(18.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .neumorphicDepth(
                            shape = sectionShape,
                            lightColor = Color.Transparent,
                            darkColor = glassStyle.neumorphicShadow,
                            offsetDp = 3.dp,
                            isPressed = true
                        )
                        .clip(sectionShape)
                        .background(
                            Brush.linearGradient(
                                listOf(glassStyle.cardSubtleBg, glassStyle.neumorphicSurface)
                            )
                        )
                        .border(1.dp, glassStyle.neumorphicBorderDark, sectionShape)
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (appLanguage == AppLanguage.ARABIC) "الحد اليومي المخصص" else "Custom Daily Limit",
                                color = glassStyle.textSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                NeumorphicIconButton(
                                    icon = Icons.Default.Remove,
                                    contentDescription = "Decrease",
                                    onClick = { dailyLimit = (dailyLimit - 0.5f).coerceAtLeast(0.5f) },
                                    tint = glassStyle.accentPrimary,
                                    sizeDp = 32.dp
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                val formattedLimit = if (dailyLimit % 1f == 0f) "${dailyLimit.toInt()} GB" else "%.1f GB".format(dailyLimit)
                                Text(
                                    text = formattedLimit,
                                    color = glassStyle.accentPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                NeumorphicIconButton(
                                    icon = Icons.Default.Add,
                                    contentDescription = "Increase",
                                    onClick = { dailyLimit = (dailyLimit + 0.5f).coerceAtMost(50f) },
                                    tint = glassStyle.accentPrimary,
                                    sizeDp = 32.dp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick Presets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(1.0f, 2.0f, 3.0f, 5.0f).forEach { preset ->
                                val isSelected = (kotlin.math.abs(dailyLimit - preset) < 0.05f)
                                NeumorphicSegmentedPill(
                                    label = "${preset.toInt()} GB",
                                    isSelected = isSelected,
                                    onClick = { dailyLimit = preset },
                                    accentColor = glassStyle.accentPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Toggle Notification for this App (Indented Neumorphic Card with NeumorphicSwitch)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .neumorphicDepth(
                            shape = sectionShape,
                            lightColor = Color.Transparent,
                            darkColor = glassStyle.neumorphicShadow,
                            offsetDp = 3.dp,
                            isPressed = true
                        )
                        .clip(sectionShape)
                        .background(
                            Brush.linearGradient(
                                listOf(glassStyle.cardSubtleBg, glassStyle.neumorphicSurface)
                            )
                        )
                        .border(1.dp, glassStyle.neumorphicBorderDark, sectionShape)
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (appLanguage == AppLanguage.ARABIC) "تفعيل إشعار التجاوز للتطبيق" else "Enable Alerts for this App",
                                color = glassStyle.textPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (appLanguage == AppLanguage.ARABIC)
                                    "إرسال تنبيه فوري عند بلوغ هذا التطبيق حده اليومي"
                                else
                                    "Send instant alert when this app consumes custom quota",
                                color = glassStyle.textTertiary,
                                fontSize = 10.sp
                            )
                        }

                        NeumorphicSwitch(
                            checked = alertEnabled,
                            onCheckedChange = { alertEnabled = it },
                            accentColor = glassStyle.accentPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons (Cancel / Save) using tactile NeumorphicButton
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NeumorphicButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (appLanguage == AppLanguage.ARABIC) "إلغاء" else "Cancel",
                            color = glassStyle.textSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    NeumorphicButton(
                        onClick = {
                            onSave(dailyLimit, alertEnabled)
                            onDismiss()
                        },
                        accentColor = glassStyle.accentPrimary,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = glassStyle.accentPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (appLanguage == AppLanguage.ARABIC) "حفظ التخصيص" else "Save Limit",
                                color = glassStyle.accentPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
