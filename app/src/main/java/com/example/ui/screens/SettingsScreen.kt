package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.VpnService
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInNew
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PrivateDnsPreset
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.model.ThemeMode
import com.example.ui.MainViewModel
import com.example.ui.components.GlassContainer
import com.example.ui.components.NeumorphicSwitch
import com.example.ui.components.rememberAppHaptics
import com.example.ui.components.tactileSpringPress
import com.example.update.AppUpdateUiState
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.LocalAppDimensions
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.RedAlert

/**
 * Safely launches the system's Private DNS or Wireless settings activity across all Android versions.
 */
fun openPrivateDnsSettings(context: Context) {
    val intents = listOf(
        Intent("android.settings.NETWORK_PROVIDER_SETTINGS"),
        Intent(Settings.ACTION_WIRELESS_SETTINGS),
        Intent("android.settings.PRIVATE_DNS_SETTINGS"),
        Intent(Settings.ACTION_SETTINGS)
    )
    for (intent in intents) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            return
        } catch (_: Exception) {
            // Fallback to next candidate
        }
    }
}

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
    val privateDnsPreset by viewModel.privateDnsPreset.collectAsStateWithLifecycle()
    val privateDnsCustomHostname by viewModel.privateDnsCustomHostname.collectAsStateWithLifecycle()
    val dnsVpnActive by viewModel.dnsVpnActive.collectAsStateWithLifecycle()
    val dnsVpnHost by viewModel.dnsVpnHost.collectAsStateWithLifecycle()
    val dnsQueriesForwarded by viewModel.dnsQueriesForwarded.collectAsStateWithLifecycle()
    val appUpdateState by viewModel.appUpdateState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current
    val dimensions = LocalAppDimensions.current
    val appHaptics = rememberAppHaptics()

    val vpnConsentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.startLocalDnsVpn()
        }
    }

    val toggleLocalDnsVpn: (Boolean) -> Unit = { enable ->
        if (enable) {
            val prepareIntent = VpnService.prepare(context)
            if (prepareIntent == null) {
                viewModel.startLocalDnsVpn()
            } else {
                vpnConsentLauncher.launch(prepareIntent)
            }
        } else {
            viewModel.stopLocalDnsVpn()
        }
    }

    var customDnsInput by remember(privateDnsCustomHostname) { mutableStateOf(privateDnsCustomHostname) }
    var showCopiedNotice by remember { mutableStateOf(false) }
    var copiedHostnameText by remember { mutableStateOf("") }

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

                Spacer(modifier = Modifier.height(10.dp))

                // Sleek, luxurious frosted segmented pill selector
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(glassStyle.cardSubtleBg)
                        .border(0.8.dp, glassStyle.borderColor, RoundedCornerShape(14.dp))
                        .padding(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val arabicLabel = "العربية"
                        val englishLabel = if (appLanguage == AppLanguage.ARABIC) "الإنغليزية" else "English"

                        LanguagePillOption(
                            label = arabicLabel,
                            isSelected = appLanguage == AppLanguage.ARABIC,
                            onClick = {
                                appHaptics.performSegmentSwitch()
                                viewModel.setAppLanguage(AppLanguage.ARABIC)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("language_option_arabic")
                        )

                        LanguagePillOption(
                            label = englishLabel,
                            isSelected = appLanguage == AppLanguage.ENGLISH,
                            onClick = {
                                appHaptics.performSegmentSwitch()
                                viewModel.setAppLanguage(AppLanguage.ENGLISH)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("language_option_english")
                        )
                    }
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

    // 3. Private DNS Engine & Local VPN Shield Card with 1-Click AdGuard Preset & Quick Settings Tile Sync
    val privateDnsCard: @Composable () -> Unit = {
        val emeraldAdGuard = Color(0xFF00E676)
        val cloudflareBlue = Color(0xFF00E5FF)

        GlassContainer(
            modifier = Modifier.fillMaxWidth(),
            borderColor = if (dnsVpnActive || privateDnsPreset == PrivateDnsPreset.ADGUARD) {
                emeraldAdGuard.copy(alpha = if (glassStyle.isDark) 0.65f else 0.45f)
            } else {
                glassStyle.borderColor
            },
            enableBreathingGlow = (dnsVpnActive || privateDnsPreset == PrivateDnsPreset.ADGUARD),
            glowColor = emeraldAdGuard
        ) {
            Column {
                // Header with DNS icon and active status chip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(
                                    if (privateDnsPreset == PrivateDnsPreset.ADGUARD) {
                                        emeraldAdGuard.copy(alpha = if (glassStyle.isDark) 0.22f else 0.15f)
                                    } else {
                                        glassStyle.accentPrimary.copy(alpha = if (glassStyle.isDark) 0.18f else 0.12f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (privateDnsPreset == PrivateDnsPreset.ADGUARD) Icons.Default.Shield else Icons.Default.Dns,
                                contentDescription = "Private DNS",
                                tint = if (privateDnsPreset == PrivateDnsPreset.ADGUARD) emeraldAdGuard else glassStyle.accentPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(7.dp))
                        Column {
                            Text(
                                text = AppStrings.privateDnsTitle(appLanguage),
                                color = glassStyle.textPrimary,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = AppStrings.privateDnsSubtitle(appLanguage),
                                color = glassStyle.textSecondary,
                                fontSize = 9.5.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Active badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (privateDnsPreset == PrivateDnsPreset.ADGUARD) {
                                    emeraldAdGuard.copy(alpha = if (glassStyle.isDark) 0.22f else 0.14f)
                                } else {
                                    glassStyle.cardSubtleBg
                                }
                            )
                            .border(
                                0.8.dp,
                                if (privateDnsPreset == PrivateDnsPreset.ADGUARD) emeraldAdGuard.copy(alpha = 0.6f) else glassStyle.borderColor,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 7.dp, vertical = 2.5.dp)
                    ) {
                        Text(
                            text = when (privateDnsPreset) {
                                PrivateDnsPreset.ADGUARD -> "AdGuard"
                                PrivateDnsPreset.CLOUDFLARE -> "Cloudflare"
                                PrivateDnsPreset.OFF -> if (appLanguage == AppLanguage.ARABIC) "معطل" else "Off"
                                PrivateDnsPreset.CUSTOM -> if (privateDnsCustomHostname.isNotBlank()) privateDnsCustomHostname else (if (appLanguage == AppLanguage.ARABIC) "مخصص" else "Custom")
                            },
                            color = if (privateDnsPreset == PrivateDnsPreset.ADGUARD) emeraldAdGuard else glassStyle.accentPrimary,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // =========================================================================
                // 0. ONE-TAP LOCAL VPN DNS SHIELD (IN-APP + QUICK SETTINGS TILE SYNC)
                // =========================================================================
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (dnsVpnActive) {
                                emeraldAdGuard.copy(alpha = if (glassStyle.isDark) 0.16f else 0.10f)
                            } else {
                                glassStyle.cardSubtleBg
                            }
                        )
                        .border(
                            width = if (dnsVpnActive) 1.3.dp else 0.9.dp,
                            color = if (dnsVpnActive) emeraldAdGuard.copy(alpha = 0.7f) else glassStyle.borderColor,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = AppStrings.dnsVpnShieldTitle(appLanguage),
                                    color = glassStyle.textPrimary,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (dnsVpnActive) {
                                        AppStrings.dnsVpnActiveLabel(dnsVpnHost, appLanguage)
                                    } else {
                                        AppStrings.dnsVpnInactiveLabel(appLanguage)
                                    },
                                    color = if (dnsVpnActive) emeraldAdGuard else glassStyle.textSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = if (dnsVpnActive) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            NeumorphicSwitch(
                                checked = dnsVpnActive,
                                onCheckedChange = { checked ->
                                    appHaptics.performConfirm()
                                    toggleLocalDnsVpn(checked)
                                },
                                accentColor = emeraldAdGuard,
                                modifier = Modifier.testTag("local_dns_vpn_switch")
                            )
                        }

                        if (dnsVpnActive || dnsQueriesForwarded > 0L) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = AppStrings.dnsVpnQueriesCount(dnsQueriesForwarded, appLanguage),
                                    color = glassStyle.accentPrimary,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = AppStrings.dnsQuickTileHint(appLanguage),
                                    color = glassStyle.textTertiary,
                                    fontSize = 9.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = AppStrings.dnsQuickTileHint(appLanguage),
                                color = glassStyle.textTertiary,
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // =========================================================================
                // 1. DEDICATED ONE-CLICK ADGUARD PRESET CARD (HIGHLIGHTED AS RECOMMENDED)
                // =========================================================================
                val isAdGuardSelected = (privateDnsPreset == PrivateDnsPreset.ADGUARD)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .tactileSpringPress(
                            pressedScale = 0.97f,
                            onClick = {
                                appHaptics.performConfirm()
                                viewModel.setPrivateDnsPreset(PrivateDnsPreset.ADGUARD)
                                clipboardManager.setText(AnnotatedString("dns.adguard.com"))
                                copiedHostnameText = "dns.adguard.com"
                                showCopiedNotice = true
                            }
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isAdGuardSelected) {
                                emeraldAdGuard.copy(alpha = if (glassStyle.isDark) 0.18f else 0.12f)
                            } else {
                                glassStyle.cardSubtleBg
                            }
                        )
                        .border(
                            width = if (isAdGuardSelected) 1.5.dp else 1.dp,
                            color = if (isAdGuardSelected) emeraldAdGuard else emeraldAdGuard.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(11.dp)
                ) {
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
                                        .clip(CircleShape)
                                        .background(emeraldAdGuard.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "AdGuard",
                                        tint = emeraldAdGuard,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(7.dp))
                                Text(
                                    text = "AdGuard DNS",
                                    color = if (isAdGuardSelected) {
                                        if (glassStyle.isDark) Color.White else glassStyle.textPrimary
                                    } else {
                                        glassStyle.textPrimary
                                    },
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                // Highlighted recommended badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(emeraldAdGuard)
                                        .padding(horizontal = 5.dp, vertical = 1.5.dp)
                                ) {
                                    Text(
                                        text = AppStrings.privateDnsAdGuardBadge(appLanguage),
                                        color = Color.Black,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.4.sp
                                    )
                                }
                            }

                            if (isAdGuardSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = emeraldAdGuard,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = AppStrings.privateDnsAdGuardDesc(appLanguage),
                                color = glassStyle.textSecondary,
                                fontSize = 10.sp,
                                lineHeight = 13.5.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // Hostname Pill with copy hint
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.Black.copy(alpha = 0.25f))
                                    .border(0.6.dp, emeraldAdGuard.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = emeraldAdGuard,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "dns.adguard.com",
                                        color = emeraldAdGuard,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // =========================================================================
                // 2. OTHER PRESETS ROW: System Default (Off), Cloudflare, Custom Hostname
                // =========================================================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Option 1: System Default (Off)
                    val isOffSelected = (privateDnsPreset == PrivateDnsPreset.OFF)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isOffSelected) glassStyle.accentPrimary.copy(alpha = if (glassStyle.isDark) 0.18f else 0.12f)
                                else glassStyle.cardSubtleBg
                            )
                            .border(
                                width = if (isOffSelected) 1.2.dp else 0.8.dp,
                                color = if (isOffSelected) glassStyle.accentPrimary else glassStyle.borderColor,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                appHaptics.performSegmentSwitch()
                                viewModel.setPrivateDnsPreset(PrivateDnsPreset.OFF)
                                showCopiedNotice = false
                            }
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Dns,
                                    contentDescription = "Off",
                                    tint = if (isOffSelected) glassStyle.accentPrimary else glassStyle.textTertiary,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = AppStrings.privateDnsOff(appLanguage),
                                    color = if (isOffSelected) glassStyle.textPrimary else glassStyle.textSecondary,
                                    fontSize = 9.5.sp,
                                    fontWeight = if (isOffSelected) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = if (appLanguage == AppLanguage.ARABIC) "تلقائي من الشبكة" else "ISP Default",
                                color = glassStyle.textTertiary,
                                fontSize = 8.sp,
                                maxLines = 1
                            )
                        }
                    }

                    // Option 2: Cloudflare (1.1.1.1)
                    val isCloudflareSelected = (privateDnsPreset == PrivateDnsPreset.CLOUDFLARE)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isCloudflareSelected) cloudflareBlue.copy(alpha = if (glassStyle.isDark) 0.20f else 0.12f)
                                else glassStyle.cardSubtleBg
                            )
                            .border(
                                width = if (isCloudflareSelected) 1.2.dp else 0.8.dp,
                                color = if (isCloudflareSelected) cloudflareBlue else glassStyle.borderColor,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                appHaptics.performSegmentSwitch()
                                viewModel.setPrivateDnsPreset(PrivateDnsPreset.CLOUDFLARE)
                                clipboardManager.setText(AnnotatedString("one.one.one.one"))
                                copiedHostnameText = "one.one.one.one"
                                showCopiedNotice = true
                            }
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = "Cloudflare",
                                    tint = if (isCloudflareSelected) cloudflareBlue else glassStyle.textTertiary,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Cloudflare",
                                    color = if (isCloudflareSelected) glassStyle.textPrimary else glassStyle.textSecondary,
                                    fontSize = 9.5.sp,
                                    fontWeight = if (isCloudflareSelected) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1
                                )
                            }
                            Text(
                                text = "one.one.one.one",
                                color = if (isCloudflareSelected) cloudflareBlue else glassStyle.textTertiary,
                                fontSize = 8.sp,
                                maxLines = 1
                            )
                        }
                    }

                    // Option 3: Custom Hostname
                    val isCustomSelected = (privateDnsPreset == PrivateDnsPreset.CUSTOM)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isCustomSelected) glassStyle.accentPrimary.copy(alpha = if (glassStyle.isDark) 0.18f else 0.12f)
                                else glassStyle.cardSubtleBg
                            )
                            .border(
                                width = if (isCustomSelected) 1.2.dp else 0.8.dp,
                                color = if (isCustomSelected) glassStyle.accentPrimary else glassStyle.borderColor,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                appHaptics.performSegmentSwitch()
                                viewModel.setPrivateDnsPreset(PrivateDnsPreset.CUSTOM)
                                if (customDnsInput.isNotBlank()) {
                                    clipboardManager.setText(AnnotatedString(customDnsInput))
                                    copiedHostnameText = customDnsInput
                                    showCopiedNotice = true
                                }
                            }
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Custom",
                                    tint = if (isCustomSelected) glassStyle.accentPrimary else glassStyle.textTertiary,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = AppStrings.privateDnsCustom(appLanguage),
                                    color = if (isCustomSelected) glassStyle.textPrimary else glassStyle.textSecondary,
                                    fontSize = 9.5.sp,
                                    fontWeight = if (isCustomSelected) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = if (customDnsInput.isNotBlank()) customDnsInput else (if (appLanguage == AppLanguage.ARABIC) "مخصص" else "Custom DoT"),
                                color = glassStyle.textTertiary,
                                fontSize = 8.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Custom Hostname TextField input (when Custom is selected)
                if (privateDnsPreset == PrivateDnsPreset.CUSTOM) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = customDnsInput,
                            onValueChange = {
                                customDnsInput = it
                                viewModel.setPrivateDnsCustomHostname(it)
                            },
                            placeholder = {
                                Text(
                                    AppStrings.privateDnsCustomPlaceholder(appLanguage),
                                    fontSize = 11.sp,
                                    color = glassStyle.textTertiary
                                )
                            },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = glassStyle.textPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = glassStyle.accentPrimary,
                                unfocusedBorderColor = glassStyle.borderColor,
                                focusedContainerColor = glassStyle.cardSubtleBg,
                                unfocusedContainerColor = glassStyle.cardSubtleBg
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                appHaptics.performConfirm()
                                viewModel.setPrivateDnsCustomHostname(customDnsInput)
                                if (customDnsInput.isNotBlank()) {
                                    clipboardManager.setText(AnnotatedString(customDnsInput))
                                    copiedHostnameText = customDnsInput
                                    showCopiedNotice = true
                                }
                            },
                            modifier = Modifier.height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = glassStyle.accentPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = AppStrings.privateDnsApplyBtn(appLanguage),
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // =========================================================================
                // 3. INTENT APPLICATION / SYSTEM SETTINGS PROMPT BUTTON
                // =========================================================================
                Spacer(modifier = Modifier.height(10.dp))

                val activeHostname = when (privateDnsPreset) {
                    PrivateDnsPreset.ADGUARD -> "dns.adguard.com"
                    PrivateDnsPreset.CLOUDFLARE -> "one.one.one.one"
                    PrivateDnsPreset.CUSTOM -> customDnsInput.ifBlank { privateDnsCustomHostname }
                    PrivateDnsPreset.OFF -> null
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (privateDnsPreset == PrivateDnsPreset.ADGUARD) {
                                emeraldAdGuard.copy(alpha = if (glassStyle.isDark) 0.12f else 0.08f)
                            } else {
                                glassStyle.cardSubtleBg
                            }
                        )
                        .border(
                            0.8.dp,
                            if (privateDnsPreset == PrivateDnsPreset.ADGUARD) emeraldAdGuard.copy(alpha = 0.35f) else glassStyle.borderColor,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(9.dp)
                ) {
                    if (showCopiedNotice && copiedHostnameText.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = emeraldAdGuard,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = AppStrings.privateDnsCopiedNotice(copiedHostnameText, appLanguage),
                                color = emeraldAdGuard,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Text(
                        text = AppStrings.privateDnsGuide(appLanguage),
                        color = glassStyle.textSecondary,
                        fontSize = 9.5.sp,
                        lineHeight = 13.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Clean 1-Click System Settings Trigger
                    Button(
                        onClick = {
                            appHaptics.performConfirm()
                            if (activeHostname != null) {
                                clipboardManager.setText(AnnotatedString(activeHostname))
                                copiedHostnameText = activeHostname
                                showCopiedNotice = true
                            }
                            openPrivateDnsSettings(context)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (privateDnsPreset == PrivateDnsPreset.ADGUARD) emeraldAdGuard else glassStyle.accentPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = "Open Settings",
                                tint = Color.Black,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = AppStrings.privateDnsOpenSettingsBtn(appLanguage),
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
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
                        modifier = Modifier.size(dimensions.headerIconBoxSize),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_brand_logo),
                            contentDescription = "Data Pulse",
                            modifier = Modifier.size(dimensions.headerIconSize)
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

                Spacer(modifier = Modifier.height(12.dp))

                // In-Device Automatic Update Check Trigger
                val isCheckingUpdate = (appUpdateState is AppUpdateUiState.Checking)
                val isUpToDate = (appUpdateState is AppUpdateUiState.UpToDate)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .tactileSpringPress(
                            pressedScale = 0.965f,
                            enabled = !isCheckingUpdate,
                            onClick = {
                                appHaptics.performConfirm()
                                viewModel.checkForAppUpdate(manual = true)
                            }
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            glassStyle.accentPrimary.copy(alpha = if (glassStyle.isDark) 0.14f else 0.10f)
                        )
                        .border(
                            1.dp,
                            glassStyle.accentPrimary.copy(alpha = 0.45f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 9.dp)
                        .testTag("settings_check_update_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isUpToDate) Icons.Default.CheckCircle else Icons.Default.SystemUpdate,
                            contentDescription = null,
                            tint = glassStyle.accentPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when {
                                isCheckingUpdate -> if (appLanguage == AppLanguage.ARABIC) "جاري التحقق من التحديثات..." else "Checking for updates..."
                                isUpToDate -> if (appLanguage == AppLanguage.ARABIC) "أنت تستخدم أحدث إصدار ✓" else "You're on the latest version ✓"
                                else -> if (appLanguage == AppLanguage.ARABIC) "التحقق من التحديثات التلقائية" else "Check for In-App Updates"
                            },
                            color = glassStyle.accentPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
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
                    privateDnsCard()
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
                privateDnsCard()
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
fun LanguagePillOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassStyle = com.example.ui.theme.LocalLiquidGlass.current
    val shape = RoundedCornerShape(11.dp)

    val animatedBgTop by animateColorAsState(
        targetValue = when {
            isSelected && glassStyle.isDark -> glassStyle.accentPrimary.copy(alpha = 0.24f)
            isSelected -> Color.White
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 220),
        label = "lang_bg_top"
    )
    val animatedBgBottom by animateColorAsState(
        targetValue = when {
            isSelected && glassStyle.isDark -> glassStyle.accentSecondary.copy(alpha = 0.14f)
            isSelected -> glassStyle.accentPrimary.copy(alpha = 0.08f)
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 220),
        label = "lang_bg_bottom"
    )
    val animatedBorderStart by animateColorAsState(
        targetValue = if (isSelected) glassStyle.accentPrimary else Color.Transparent,
        animationSpec = tween(durationMillis = 220),
        label = "lang_border_start"
    )
    val animatedBorderEnd by animateColorAsState(
        targetValue = if (isSelected) glassStyle.accentSecondary.copy(alpha = 0.65f) else Color.Transparent,
        animationSpec = tween(durationMillis = 220),
        label = "lang_border_end"
    )
    val animatedTextColor by animateColorAsState(
        targetValue = when {
            isSelected && glassStyle.isDark -> Color.White
            isSelected -> glassStyle.textPrimary
            else -> glassStyle.textSecondary
        },
        animationSpec = tween(durationMillis = 200),
        label = "lang_text"
    )

    Box(
        modifier = modifier
            .height(44.dp)
            .tactileSpringPress(pressedScale = 0.95f, onClick = onClick)
            .then(
                if (isSelected && !glassStyle.isDark) {
                    Modifier.shadow(
                        elevation = 3.dp,
                        shape = shape,
                        spotColor = Color(0x140F172A),
                        ambientColor = Color(0x0A0F172A)
                    )
                } else Modifier
            )
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(animatedBgTop, animatedBgBottom)
                )
            )
            .border(
                width = if (isSelected) 1.2.dp else 0.dp,
                brush = Brush.linearGradient(
                    colors = listOf(animatedBorderStart, animatedBorderEnd)
                ),
                shape = shape
            )
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = label,
            transitionSpec = {
                fadeIn(tween(180)) togetherWith fadeOut(tween(140))
            },
            label = "language_pill_text"
        ) { targetText ->
            Text(
                text = targetText,
                color = animatedTextColor,
                fontSize = 13.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                letterSpacing = 0.25.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
