package com.example.ui.screens

import android.app.Activity
import android.net.VpnService
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.model.PrivateDnsPreset
import com.example.ui.MainViewModel
import com.example.ui.components.GlassContainer
import com.example.ui.components.NeumorphicSwitch
import com.example.ui.components.neumorphicDepth
import com.example.ui.components.rememberAppHaptics
import com.example.ui.components.tactileSpringPress
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.LocalAppDimensions
import com.example.ui.theme.LocalLiquidGlass
import com.example.ui.theme.NeumorphicColors

/**
 * Dedicated standalone DNS Control & Local VPN Shield screen crafted in the
 * Dark Liquid Glass & Neumorphic Depth aesthetic with 60/120fps radar orbital animations.
 */
@Composable
fun DnsControlScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val privateDnsPreset by viewModel.privateDnsPreset.collectAsStateWithLifecycle()
    val privateDnsCustomHostname by viewModel.privateDnsCustomHostname.collectAsStateWithLifecycle()
    val dnsVpnActive by viewModel.dnsVpnActive.collectAsStateWithLifecycle()
    val dnsVpnHost by viewModel.dnsVpnHost.collectAsStateWithLifecycle()
    val dnsQueriesForwarded by viewModel.dnsQueriesForwarded.collectAsStateWithLifecycle()
    val dnsLatencies by viewModel.dnsLatencies.collectAsStateWithLifecycle()
    val isPingingDns by viewModel.isPingingDns.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refreshDnsLatencies()
    }

    val glassStyle = LocalLiquidGlass.current
    val dimensions = LocalAppDimensions.current
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val focusManager = LocalFocusManager.current
    val haptics = rememberAppHaptics()

    var customDnsInput by remember(privateDnsCustomHostname) { mutableStateOf(privateDnsCustomHostname) }
    var showCopiedNotice by remember { mutableStateOf(false) }
    var copiedHostnameText by remember { mutableStateOf("") }

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

    val emeraldAdGuard = if (glassStyle.isNeumorphic) NeumorphicColors.AccentMint else Color(0xFF00E676)
    val electricCyan = if (glassStyle.isNeumorphic) NeumorphicColors.AccentBlue else Color(0xFF00E5FF)
    val googleBlue = if (glassStyle.isNeumorphic) NeumorphicColors.AccentBlue else Color(0xFF4285F4)
    val quad9Violet = if (glassStyle.isNeumorphic) Color(0xFF7C3AED) else Color(0xFFB388FF)
    val openDnsAmber = if (glassStyle.isNeumorphic) Color(0xFFD97706) else Color(0xFFFFB300)

    val presetAccent: (PrivateDnsPreset) -> Color = { preset ->
        when (preset) {
            PrivateDnsPreset.ADGUARD -> emeraldAdGuard
            PrivateDnsPreset.CLOUDFLARE -> electricCyan
            PrivateDnsPreset.GOOGLE -> googleBlue
            PrivateDnsPreset.QUAD9 -> quad9Violet
            PrivateDnsPreset.OPENDNS -> openDnsAmber
            PrivateDnsPreset.CUSTOM -> glassStyle.accentSecondary
            PrivateDnsPreset.OFF -> glassStyle.accentPrimary
        }
    }

    val activeAccent by animateColorAsState(
        targetValue = when {
            dnsVpnActive -> presetAccent(privateDnsPreset)
            privateDnsPreset != PrivateDnsPreset.OFF -> presetAccent(privateDnsPreset)
            else -> glassStyle.accentPrimary
        },
        animationSpec = tween(420),
        label = "dns_active_accent"
    )

    val resolvedDisplayHost = when {
        dnsVpnActive -> dnsVpnHost
        privateDnsPreset == PrivateDnsPreset.CUSTOM -> customDnsInput.ifBlank { "dns.adguard.com" }
        privateDnsPreset.hostname != null -> privateDnsPreset.hostname!!
        else -> if (appLanguage == AppLanguage.ARABIC) "افتراضي الشبكة" else "ISP Default"
    }

    val filterModeLabel = when (privateDnsPreset) {
        PrivateDnsPreset.ADGUARD -> AppStrings.dnsAdFilterMode(appLanguage)
        PrivateDnsPreset.CLOUDFLARE -> AppStrings.dnsFastPrivacyMode(appLanguage)
        PrivateDnsPreset.GOOGLE -> AppStrings.dnsGlobalSpeedMode(appLanguage)
        PrivateDnsPreset.QUAD9 -> AppStrings.dnsThreatGuardMode(appLanguage)
        PrivateDnsPreset.OPENDNS -> AppStrings.dnsOpenDnsMode(appLanguage)
        PrivateDnsPreset.CUSTOM -> AppStrings.dnsCustomMode(appLanguage)
        PrivateDnsPreset.OFF -> if (dnsVpnActive) AppStrings.dnsAdFilterMode(appLanguage) else AppStrings.dnsIspMode(appLanguage)
    }

    val heroShieldSection: @Composable () -> Unit = {
        DnsShieldHeroCard(
            isActive = dnsVpnActive,
            activeHost = resolvedDisplayHost,
            queriesSecured = dnsQueriesForwarded,
            accentColor = activeAccent,
            appLanguage = appLanguage,
            onToggleVpn = { enable ->
                haptics.performConfirm()
                toggleLocalDnsVpn(enable)
            }
        )
    }

    val telemetryStripSection: @Composable () -> Unit = {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensions.gridSpacing)
        ) {
            DnsTelemetryMetricCard(
                icon = Icons.Default.Security,
                title = if (appLanguage == AppLanguage.ARABIC) "الاستعلامات المؤمّنة" else "Queries Secured",
                value = dnsQueriesForwarded.toString(),
                subtitle = if (dnsVpnActive) {
                    if (appLanguage == AppLanguage.ARABIC) "تصفية فورية نشطة" else "Live tunnel active"
                } else {
                    if (appLanguage == AppLanguage.ARABIC) "إجمالي الجلسة" else "Session total"
                },
                accentColor = if (dnsVpnActive) emeraldAdGuard else glassStyle.accentPrimary,
                isLive = dnsVpnActive,
                modifier = Modifier.weight(1f)
            )

            DnsTelemetryMetricCard(
                icon = Icons.Default.Bolt,
                title = AppStrings.dnsFilterEngineLabel(appLanguage),
                value = when (privateDnsPreset) {
                    PrivateDnsPreset.ADGUARD -> "AdGuard"
                    PrivateDnsPreset.CLOUDFLARE -> "1.1.1.1"
                    PrivateDnsPreset.GOOGLE -> "8.8.8.8"
                    PrivateDnsPreset.QUAD9 -> "Quad9"
                    PrivateDnsPreset.OPENDNS -> "OpenDNS"
                    PrivateDnsPreset.CUSTOM -> if (customDnsInput.isNotBlank()) customDnsInput else "Custom"
                    PrivateDnsPreset.OFF -> if (dnsVpnActive) "AdGuard" else "Auto"
                },
                subtitle = filterModeLabel,
                accentColor = activeAccent,
                isLive = (privateDnsPreset != PrivateDnsPreset.OFF || dnsVpnActive),
                modifier = Modifier.weight(1f)
            )
        }
    }

    val providersSection: @Composable () -> Unit = {
        val pingInfiniteTransition = rememberInfiniteTransition(label = "dns_ping_spin")
        val pingIconRotation by pingInfiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 900, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "ping_rotation"
        )

        GlassContainer(
            modifier = Modifier.fillMaxWidth(),
            borderColor = activeAccent.copy(alpha = if (glassStyle.isDark) 0.45f else 0.25f)
        ) {
            Column {
                // Header: Integrated Servers Title + Live Latency Ping Trigger
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = null,
                            tint = activeAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.dnsProvidersSection(appLanguage),
                            color = glassStyle.textPrimary,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .tactileSpringPress(
                                pressedScale = 0.92f,
                                enabled = !isPingingDns,
                                onClick = {
                                    haptics.performSegmentSwitch()
                                    viewModel.refreshDnsLatencies()
                                }
                            )
                            .clip(RoundedCornerShape(10.dp))
                            .background(AmberAlert.copy(alpha = if (glassStyle.isDark) 0.16f else 0.12f))
                            .border(0.9.dp, AmberAlert.copy(alpha = 0.65f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("dns_ping_refresh_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = AppStrings.dnsPingLatencyBtn(appLanguage),
                                tint = AmberAlert,
                                modifier = Modifier
                                    .size(13.dp)
                                    .graphicsLayer {
                                        rotationZ = if (isPingingDns) pingIconRotation else 0f
                                    }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = AppStrings.dnsPingLatencyBtn(appLanguage),
                                color = AmberAlert,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 5 Integrated DNS Server Cards: AdGuard, Cloudflare, Google, Quad9, Cisco OpenDNS
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    PrivateDnsPreset.integratedServers.forEach { preset ->
                        val isSelected = (privateDnsPreset == preset)
                        val cardAccent = presetAccent(preset)
                        val latencyMs = dnsLatencies[preset]
                        val providerIcon = when (preset) {
                            PrivateDnsPreset.ADGUARD -> Icons.Default.Shield
                            PrivateDnsPreset.CLOUDFLARE -> Icons.Default.Speed
                            PrivateDnsPreset.GOOGLE -> Icons.Default.Public
                            PrivateDnsPreset.QUAD9 -> Icons.Default.VerifiedUser
                            PrivateDnsPreset.OPENDNS -> Icons.Default.Cloud
                            else -> Icons.Default.Dns
                        }

                        DnsServerProviderCard(
                            name = preset.getDisplayName(appLanguage),
                            primaryIp = preset.primaryIp ?: "",
                            secondaryIp = preset.secondaryIp ?: "",
                            latencyMs = latencyMs,
                            isPinging = isPingingDns && !dnsLatencies.containsKey(preset),
                            isSelected = isSelected,
                            isRecommended = preset.isRecommended,
                            recommendedLabel = AppStrings.privateDnsAdGuardBadge(appLanguage),
                            accentColor = cardAccent,
                            icon = providerIcon,
                            onClick = {
                                haptics.performConfirm()
                                viewModel.setPrivateDnsPreset(preset)
                                preset.hostname?.let { host ->
                                    clipboardManager.setText(AnnotatedString(host))
                                    copiedHostnameText = host
                                    showCopiedNotice = true
                                }
                            },
                            modifier = Modifier.testTag("dns_provider_${preset.id.lowercase()}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Secondary Controls Row: System Default (Off) & Custom Hostname
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DnsPresetChip(
                        icon = Icons.Default.Dns,
                        title = AppStrings.privateDnsOff(appLanguage),
                        subtitle = if (appLanguage == AppLanguage.ARABIC) "تلقائي" else "ISP Auto",
                        isSelected = (privateDnsPreset == PrivateDnsPreset.OFF),
                        accentColor = glassStyle.accentPrimary,
                        onClick = {
                            haptics.performSegmentSwitch()
                            viewModel.setPrivateDnsPreset(PrivateDnsPreset.OFF)
                            showCopiedNotice = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dns_provider_off")
                    )

                    DnsPresetChip(
                        icon = Icons.Default.Tune,
                        title = AppStrings.privateDnsCustom(appLanguage),
                        subtitle = customDnsInput.ifBlank { if (appLanguage == AppLanguage.ARABIC) "مخصص" else "Custom DoT" },
                        isSelected = (privateDnsPreset == PrivateDnsPreset.CUSTOM),
                        accentColor = glassStyle.accentSecondary,
                        onClick = {
                            haptics.performSegmentSwitch()
                            viewModel.setPrivateDnsPreset(PrivateDnsPreset.CUSTOM)
                            if (customDnsInput.isNotBlank()) {
                                clipboardManager.setText(AnnotatedString(customDnsInput))
                                copiedHostnameText = customDnsInput
                                showCopiedNotice = true
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dns_provider_custom")
                    )
                }

                // Expandable Custom Hostname Input
                AnimatedVisibility(
                    visible = (privateDnsPreset == PrivateDnsPreset.CUSTOM),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = customDnsInput,
                                onValueChange = {
                                    customDnsInput = it
                                    viewModel.setPrivateDnsCustomHostname(it)
                                },
                                placeholder = {
                                    Text(
                                        text = AppStrings.privateDnsCustomPlaceholder(appLanguage),
                                        fontSize = 11.5.sp,
                                        color = glassStyle.textTertiary
                                    )
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        viewModel.setPrivateDnsCustomHostname(customDnsInput)
                                    }
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    color = glassStyle.textPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = electricCyan,
                                    unfocusedBorderColor = glassStyle.borderColor,
                                    focusedContainerColor = glassStyle.cardSubtleBg,
                                    unfocusedContainerColor = glassStyle.cardSubtleBg
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    haptics.performConfirm()
                                    viewModel.setPrivateDnsCustomHostname(customDnsInput)
                                    if (customDnsInput.isNotBlank()) {
                                        clipboardManager.setText(AnnotatedString(customDnsInput))
                                        copiedHostnameText = customDnsInput
                                        showCopiedNotice = true
                                    }
                                },
                                modifier = Modifier.height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = electricCyan),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = AppStrings.privateDnsApplyBtn(appLanguage),
                                    color = if (glassStyle.isNeumorphic) Color.White else Color(0xFF061018),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    val systemDotSyncSection: @Composable () -> Unit = {
        val copyableHost = when (privateDnsPreset) {
            PrivateDnsPreset.CUSTOM -> customDnsInput.ifBlank { privateDnsCustomHostname }
            PrivateDnsPreset.OFF -> null
            else -> privateDnsPreset.hostname
        }

        GlassContainer(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(activeAccent.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = activeAccent,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.dnsSystemDotTitle(appLanguage),
                        color = glassStyle.textPrimary,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                AnimatedVisibility(visible = showCopiedNotice && copiedHostnameText.isNotBlank()) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = emeraldAdGuard,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = AppStrings.privateDnsCopiedNotice(copiedHostnameText, appLanguage),
                                color = emeraldAdGuard,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }

                Text(
                    text = AppStrings.privateDnsGuide(appLanguage),
                    color = glassStyle.textSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = AppStrings.dnsQuickTileHint(appLanguage),
                    color = glassStyle.textTertiary,
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        haptics.performConfirm()
                        if (!copyableHost.isNullOrBlank()) {
                            clipboardManager.setText(AnnotatedString(copyableHost))
                            copiedHostnameText = copyableHost
                            showCopiedNotice = true
                        }
                        openPrivateDnsSettings(context)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("dns_open_system_settings_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (privateDnsPreset == PrivateDnsPreset.ADGUARD) emeraldAdGuard else glassStyle.accentPrimary
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    val btnFg = if (glassStyle.isNeumorphic) Color.White else Color(0xFF061018)
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        tint = btnFg,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.privateDnsOpenSettingsBtn(appLanguage),
                        color = btnFg,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensions.screenPaddingHorizontal),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(dimensions.sectionSpacing)
    ) {
        // Screen Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(dimensions.headerIconBoxSize)
                            .clip(RoundedCornerShape(12.dp))
                            .background(activeAccent.copy(alpha = 0.16f))
                            .border(1.dp, activeAccent.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = activeAccent,
                            modifier = Modifier.size(dimensions.headerIconSize * 0.56f)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = AppStrings.dnsScreenTitle(appLanguage),
                            color = glassStyle.textPrimary,
                            fontSize = dimensions.textTitleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = AppStrings.dnsScreenSubtitle(appLanguage),
                            color = glassStyle.textSecondary,
                            fontSize = dimensions.textCaption,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        if (dimensions.isTablet) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimensions.gridSpacing)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(dimensions.sectionSpacing)
                    ) {
                        heroShieldSection()
                        telemetryStripSection()
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(dimensions.sectionSpacing)
                    ) {
                        providersSection()
                        systemDotSyncSection()
                    }
                }
            }
        } else {
            item { heroShieldSection() }
            item { telemetryStripSection() }
            item { providersSection() }
            item { systemDotSyncSection() }
        }
    }
}

@Composable
private fun DnsShieldHeroCard(
    isActive: Boolean,
    activeHost: String,
    queriesSecured: Long,
    accentColor: Color,
    appLanguage: AppLanguage,
    onToggleVpn: (Boolean) -> Unit
) {
    val glassStyle = LocalLiquidGlass.current
    val infiniteTransition = rememberInfiniteTransition(label = "dns_hero_orbital")

    val radarAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isActive) 4200 else 11000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dns_radar_rotation"
    )

    val pulsePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isActive) 1800 else 2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dns_shield_pulse"
    )

    val orbScale by animateFloatAsState(
        targetValue = if (isActive) 1.04f else 0.96f,
        animationSpec = spring(dampingRatio = 0.60f, stiffness = Spring.StiffnessMediumLow),
        label = "dns_orb_scale"
    )

    GlassContainer(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        borderColor = if (isActive) accentColor.copy(alpha = 0.65f) else glassStyle.borderColor,
        enableBreathingGlow = isActive,
        glowColor = accentColor
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Status Row: Active/Standby Chip + Quick NeumorphicSwitch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isActive) accentColor.copy(alpha = if (glassStyle.isDark) 0.18f else 0.12f)
                            else glassStyle.cardSubtleBg
                        )
                        .border(
                            0.9.dp,
                            if (isActive) accentColor.copy(alpha = 0.55f) else glassStyle.borderColor,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isActive) accentColor else glassStyle.textTertiary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isActive) AppStrings.dnsShieldActiveBadge(appLanguage)
                        else AppStrings.dnsShieldStandbyBadge(appLanguage),
                        color = if (isActive) accentColor else glassStyle.textSecondary,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }

                NeumorphicSwitch(
                    checked = isActive,
                    onCheckedChange = onToggleVpn,
                    accentColor = accentColor,
                    modifier = Modifier.testTag("dns_screen_vpn_switch")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Central Interactive Radar Shield Core
            Box(
                modifier = Modifier
                    .size(164.dp)
                    .tactileSpringPress(
                        pressedScale = 0.92f,
                        onClick = { onToggleVpn(!isActive) }
                    )
                    .testTag("dns_shield_orb_button"),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val outerRadius = size.minDimension * 0.46f
                    val midRadius = size.minDimension * 0.37f
                    val innerRadius = size.minDimension * 0.28f

                    // Expanding shockwave halo when active
                    val waveAlpha = if (isActive) 0.26f * (1f - pulsePhase * 0.65f) else 0.08f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                accentColor.copy(alpha = waveAlpha),
                                Color.Transparent
                            ),
                            center = Offset(cx, cy),
                            radius = outerRadius * (0.92f + 0.14f * pulsePhase)
                        ),
                        radius = outerRadius * (0.92f + 0.14f * pulsePhase),
                        center = Offset(cx, cy)
                    )

                    // Outer dashed / sweeping radar ring
                    rotate(degrees = radarAngle, pivot = Offset(cx, cy)) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    accentColor.copy(alpha = if (isActive) 0.85f else 0.28f),
                                    Color(0xFF00E5FF).copy(alpha = if (isActive) 0.65f else 0.18f),
                                    Color.Transparent
                                )
                            ),
                            startAngle = 0f,
                            sweepAngle = 210f,
                            useCenter = false,
                            topLeft = Offset(cx - outerRadius, cy - outerRadius),
                            size = Size(outerRadius * 2f, outerRadius * 2f),
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    // Counter-rotating inner orbital arc
                    rotate(degrees = -radarAngle * 1.35f, pivot = Offset(cx, cy)) {
                        drawArc(
                            color = accentColor.copy(alpha = if (isActive) 0.55f else 0.20f),
                            startAngle = 40f,
                            sweepAngle = 140f,
                            useCenter = false,
                            topLeft = Offset(cx - midRadius, cy - midRadius),
                            size = Size(midRadius * 2f, midRadius * 2f),
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    // Subtle static core ring
                    drawCircle(
                        color = when {
                            glassStyle.isNeumorphic -> NeumorphicColors.SurfaceSubtle
                            glassStyle.isDark -> Color(0xFF0B111C)
                            else -> Color(0xFFF1F5F9)
                        },
                        radius = innerRadius,
                        center = Offset(cx, cy)
                    )
                    drawCircle(
                        color = accentColor.copy(alpha = if (isActive) 0.65f else 0.30f),
                        radius = innerRadius,
                        center = Offset(cx, cy),
                        style = Stroke(width = 1.6.dp.toPx())
                    )
                }

                // Inner Glowing Shield Emblem
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.graphicsLayer {
                        scaleX = orbScale
                        scaleY = orbScale
                    }
                ) {
                    Icon(
                        imageVector = if (isActive) Icons.Default.Shield else Icons.Default.PowerSettingsNew,
                        contentDescription = AppStrings.dnsVpnShieldTitle(appLanguage),
                        tint = if (isActive) accentColor else glassStyle.textSecondary,
                        modifier = Modifier.size(34.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isActive) "ON" else "OFF",
                        color = if (isActive) accentColor else glassStyle.textTertiary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            AnimatedContent(
                targetState = activeHost,
                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(150)) },
                label = "dns_active_host_text"
            ) { hostLabel ->
                Text(
                    text = hostLabel,
                    color = if (isActive) accentColor else glassStyle.textPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = AppStrings.dnsTapOrbHint(appLanguage, isActive),
                color = glassStyle.textSecondary,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )

            if (isActive && queriesSecured > 0L) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = if (glassStyle.isDark) 0.14f else 0.10f))
                        .border(0.8.dp, accentColor.copy(alpha = 0.40f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "UDP-53 Split Tunnel • ${AppStrings.dnsVpnQueriesCount(queriesSecured, appLanguage)}",
                        color = accentColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DnsTelemetryMetricCard(
    icon: ImageVector,
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    isLive: Boolean,
    modifier: Modifier = Modifier
) {
    val glassStyle = LocalLiquidGlass.current

    GlassContainer(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        borderColor = if (isLive) accentColor.copy(alpha = 0.45f) else glassStyle.borderColor
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = if (glassStyle.isDark) 0.18f else 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = title,
                    color = glassStyle.textSecondary,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                color = glassStyle.textPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                color = accentColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DnsPresetChip(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassStyle = LocalLiquidGlass.current

    Box(
        modifier = modifier
            .height(58.dp)
            .tactileSpringPress(pressedScale = 0.94f, onClick = onClick)
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isSelected) accentColor.copy(alpha = if (glassStyle.isDark) 0.20f else 0.12f)
                else glassStyle.cardSubtleBg
            )
            .border(
                width = if (isSelected) 1.3.dp else 0.8.dp,
                color = if (isSelected) accentColor else glassStyle.borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isSelected) accentColor else glassStyle.textTertiary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    color = if (isSelected) glassStyle.textPrimary else glassStyle.textSecondary,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = if (isSelected) accentColor else glassStyle.textTertiary,
                fontSize = 8.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Integrated DNS Server Provider card in the Dark Liquid Glass (#161B22) style:
 * Displays provider name, primary & secondary IP addresses in smaller monospace text,
 * luminous neon selection border, tactile spring press feedback, and a glowing
 * gold/amber live latency badge ("40 ms", "-- ms").
 */
@Composable
private fun DnsServerProviderCard(
    name: String,
    primaryIp: String,
    secondaryIp: String,
    latencyMs: Int?,
    isPinging: Boolean,
    isSelected: Boolean,
    isRecommended: Boolean,
    recommendedLabel: String,
    accentColor: Color,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassStyle = LocalLiquidGlass.current
    val cardBaseColor = if (glassStyle.isDark) Color(0xFF161B22) else glassStyle.surfaceColor
    val goldBorder = Color(0xFFFFB300)
    val goldBright = Color(0xFFFFD54F)

    val infiniteTransition = rememberInfiniteTransition(label = "dns_card_glow")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dns_card_glow_alpha"
    )

    val borderBrush = when {
        glassStyle.isNeumorphic && isSelected -> Brush.linearGradient(
            colors = listOf(
                accentColor.copy(alpha = 0.85f),
                glassStyle.neumorphicBorderLight,
                accentColor.copy(alpha = 0.50f)
            )
        )
        glassStyle.isNeumorphic -> Brush.linearGradient(
            colors = listOf(
                glassStyle.neumorphicBorderLight,
                glassStyle.neumorphicBorderDark
            )
        )
        isSelected -> Brush.linearGradient(
            colors = listOf(
                accentColor.copy(alpha = 0.95f),
                Color(0xFF00E5FF).copy(alpha = 0.65f * glowPulse),
                accentColor.copy(alpha = 0.80f)
            )
        )
        else -> Brush.linearGradient(
            colors = listOf(
                if (glassStyle.isDark) Color(0xFF2B3442) else glassStyle.borderColor,
                if (glassStyle.isDark) Color(0xFF1E2633) else glassStyle.borderColor
            )
        )
    }

    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .tactileSpringPress(pressedScale = 0.97f, onClick = onClick)
            .then(
                if (glassStyle.isNeumorphic) {
                    Modifier.neumorphicDepth(
                        shape = shape,
                        lightColor = glassStyle.neumorphicHighlight,
                        darkColor = glassStyle.neumorphicShadow,
                        offsetDp = if (isSelected) 4.dp else 6.dp,
                        isPressed = false,
                        surfaceColor = cardBaseColor,
                        blurDp = 12.dp
                    )
                } else Modifier
            )
            .clip(shape)
            .background(
                brush = when {
                    glassStyle.isNeumorphic && isSelected -> Brush.horizontalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.08f),
                            cardBaseColor
                        )
                    )
                    glassStyle.isNeumorphic -> Brush.verticalGradient(
                        colors = listOf(cardBaseColor, glassStyle.neumorphicBase)
                    )
                    isSelected -> Brush.horizontalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = if (glassStyle.isDark) 0.16f else 0.10f),
                            cardBaseColor
                        )
                    )
                    else -> Brush.verticalGradient(
                        colors = listOf(
                            cardBaseColor,
                            if (glassStyle.isDark) Color(0xFF12171E) else cardBaseColor
                        )
                    )
                }
            )
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                brush = borderBrush,
                shape = shape
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Icon + Name + Primary/Secondary IPv4
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = if (glassStyle.isDark) 0.18f else 0.12f))
                        .border(
                            0.9.dp,
                            accentColor.copy(alpha = if (isSelected) 0.70f else 0.30f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = name,
                        tint = accentColor,
                        modifier = Modifier.size(19.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = name,
                            color = glassStyle.textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = accentColor,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    if (isRecommended) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(accentColor.copy(alpha = if (glassStyle.isDark) 0.22f else 0.16f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = recommendedLabel,
                                color = accentColor,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "$primaryIp  •  $secondaryIp",
                        color = glassStyle.textSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Right: Live Latency Badge with Glowing Gold/Amber Border
            val latencyText = when {
                isPinging -> "... ms"
                latencyMs != null -> "$latencyMs ms"
                else -> "-- ms"
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                goldBorder.copy(alpha = if (glassStyle.isDark) 0.16f else 0.12f),
                                goldBorder.copy(alpha = if (glassStyle.isDark) 0.06f else 0.05f)
                            )
                        )
                    )
                    .border(
                        width = 1.1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                goldBright.copy(alpha = 0.90f * glowPulse),
                                goldBorder.copy(alpha = 0.65f),
                                goldBright.copy(alpha = 0.85f)
                            )
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = latencyText,
                    color = if (glassStyle.isDark) goldBright else Color(0xFFB45309),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1
                )
            }
        }
    }
}

