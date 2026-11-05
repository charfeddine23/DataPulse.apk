package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AppLanguage
import com.example.model.AppUsageItem
import com.example.ui.screens.AppIconImage
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.LocalLiquidGlass

/**
 * Liquid Glass Dark Mode Dialog for customizing per-app daily limit and alert settings on long-press.
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

    var dailyLimit by remember(initialDailyLimitGB) { mutableStateOf(initialDailyLimitGB) }
    var alertEnabled by remember(initialAlertEnabled) { mutableStateOf(initialAlertEnabled) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFA111A2E),
                            Color(0xF00B0F19)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            glassStyle.accentPrimary.copy(alpha = 0.6f),
                            Color(0x22FFFFFF),
                            glassStyle.accentSecondary.copy(alpha = 0.5f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
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

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(glassStyle.cardSubtleBg)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = glassStyle.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Custom Daily Limit Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(glassStyle.cardSubtleBg)
                        .border(1.dp, glassStyle.borderColor, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (appLanguage == AppLanguage.ARABIC) "الحد اليومي المخصص للتطبيق" else "Custom Daily Data Limit",
                                color = glassStyle.textSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { dailyLimit = (dailyLimit - 0.5f).coerceAtLeast(0.5f) },
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

                                val formattedLimit = if (dailyLimit % 1f == 0f) "${dailyLimit.toInt()} GB" else "%.1f GB".format(dailyLimit)
                                Text(
                                    text = formattedLimit,
                                    color = glassStyle.accentPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                IconButton(
                                    onClick = { dailyLimit = (dailyLimit + 0.5f).coerceAtMost(50f) },
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

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Presets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(1.0f, 2.0f, 3.0f, 5.0f).forEach { preset ->
                                val isSelected = (kotlin.math.abs(dailyLimit - preset) < 0.05f)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) glassStyle.accentPrimary else glassStyle.cardBackground)
                                        .clickable { dailyLimit = preset }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${preset.toInt()} GB",
                                        color = if (isSelected) (if (glassStyle.isDark) Color.Black else Color.White) else glassStyle.textSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Toggle Notification for this App
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(glassStyle.cardSubtleBg)
                        .border(1.dp, glassStyle.borderColor, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (appLanguage == AppLanguage.ARABIC) "تفعيل إشعار التجاوز لهذا التطبيق" else "Enable Alerts for this App",
                                color = glassStyle.textPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (appLanguage == AppLanguage.ARABIC)
                                    "إرسال تنبيه فوري عند بلوغ الاستهلاك اليومي لهذا التطبيق الحد المحدد"
                                else
                                    "Send instant alert when this app consumes custom daily quota",
                                color = glassStyle.textTertiary,
                                fontSize = 10.sp
                            )
                        }

                        Switch(
                            checked = alertEnabled,
                            onCheckedChange = { alertEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = glassStyle.accentPrimary,
                                checkedTrackColor = glassStyle.accentPrimary.copy(alpha = 0.4f),
                                uncheckedThumbColor = glassStyle.textTertiary,
                                uncheckedTrackColor = if (glassStyle.isDark) Color(0xFF162238) else Color(0xFFCBD5E1)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons (Cancel / Save)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (appLanguage == AppLanguage.ARABIC) "إلغاء" else "Cancel",
                            color = glassStyle.textSecondary,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = {
                            onSave(dailyLimit, alertEnabled)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = glassStyle.accentPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = if (glassStyle.isDark) Color.Black else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (appLanguage == AppLanguage.ARABIC) "حفظ التخصيص" else "Save Limit",
                            color = if (glassStyle.isDark) Color.Black else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
