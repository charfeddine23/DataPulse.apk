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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.components.rememberAppHaptics
import com.example.ui.components.tactileSpringPress
import com.example.ui.theme.LocalAppDimensions
import com.example.ui.theme.LocalLiquidGlass

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

    val emeraldAdGuard = Color(0xFF00E676)
    val electricCyan = Color(0xFF00E5FF)
    val standBySlate = glassStyle.textTertiary

    val activeAccent by animateColorAsState(
        targetValue = when {
            dnsVpnActive && privateDnsPreset == PrivateDnsPreset.CLOUDFLARE -> electricCyan
            dnsVpnActive -> emeraldAdGuard
            privateDnsPreset == PrivateDnsPreset.ADGUARD -> emeraldAdGuard
            else -> glassStyle.accentPrimary
        },
        animationSpec = tween(420),
        label = "dns_active_accent"
    )

    val resolvedDisplayHost = when {
        dnsVpnActive -> dnsVpnHost
        privateDnsPreset == PrivateDnsPreset.ADGUARD -> "dns.adguard.com"
        privateDnsPreset == PrivateDnsPreset.CLOUDFLARE -> "one.one.one.one"
        privateDnsPreset == PrivateDnsPreset.CUSTOM -> customDnsInput.ifBlank { "dns.adguard.com" }
        else -> if (appLanguage == AppLanguage.ARABIC) "افتراضي الشبكة" else "ISP Default"
    }

    val filterModeLabel = when (privateDnsPreset) {
        PrivateDnsPreset.ADGUARD -> AppStrings.dnsAdFilterMode(appLanguage)
        PrivateDnsPreset.CLOUDFLARE -> AppStrings.dnsFastPrivacyMode(appLanguage)
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
        GlassContainer(
            modifier = Modifier.fillMaxWidth(),
            borderColor = activeAccent.copy(alpha = if (glassStyle.isDark) 0.40f else 0.25f)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                            letterSpacing = 0.6.sp
                        )
                    }

                    Text(
                        text = resolvedDisplayHost,
                        color = activeAccent,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Featured AdGuard DNS Card (Recommended)
                val isAdGuardSelected = (privateDnsPreset == PrivateDnsPreset.ADGUARD)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .tactileSpringPress(
                            pressedScale = 0.97f,
                            onClick = {
                                haptics.performConfirm()
                                viewModel.setPrivateDnsPreset(PrivateDnsPreset.ADGUARD)
                                clipboardManager.setText(AnnotatedString("dns.adguard.com"))
                                copiedHostnameText = "dns.adguard.com"
                                showCopiedNotice = true
                            }
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isAdGuardSelected) {
                                emeraldAdGuard.copy(alpha = if (glassStyle.isDark) 0.18f else 0.12f)
                            } else {
                                glassStyle.cardSubtleBg
                            }
                        )
                        .border(
                            width = if (isAdGuardSelected) 1.5.dp else 0.9.dp,
                            color = if (isAdGuardSelected) emeraldAdGuard else emeraldAdGuard.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(13.dp)
                        .testTag("dns_provider_adguard")
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(emeraldAdGuard.copy(alpha = 0.22f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "AdGuard DNS",
                                        tint = emeraldAdGuard,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(9.dp))
                                Text(
                                    text = "AdGuard DNS",
                                    color = glassStyle.textPrimary,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(emeraldAdGuard)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = AppStrings.privateDnsAdGuardBadge(appLanguage),
                                        color = Color.Black,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }

                            if (isAdGuardSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = emeraldAdGuard,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = AppStrings.privateDnsAdGuardDesc(appLanguage),
                                color = glassStyle.textSecondary,
                                fontSize = 10.5.sp,
                                lineHeight = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = if (glassStyle.isDark) 0.35f else 0.08f))
                                    .border(0.7.dp, emeraldAdGuard.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = emeraldAdGuard,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "dns.adguard.com",
                                        color = emeraldAdGuard,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Presets Row: System Default (Off) | Cloudflare | Custom Hostname
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
                        icon = Icons.Default.Speed,
                        title = "Cloudflare",
                        subtitle = "one.one.one.one",
                        isSelected = (privateDnsPreset == PrivateDnsPreset.CLOUDFLARE),
                        accentColor = electricCyan,
                        onClick = {
                            haptics.performSegmentSwitch()
                            viewModel.setPrivateDnsPreset(PrivateDnsPreset.CLOUDFLARE)
                            clipboardManager.setText(AnnotatedString("one.one.one.one"))
                            copiedHostnameText = "one.one.one.one"
                            showCopiedNotice = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dns_provider_cloudflare")
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
                                    color = Color(0xFF061018),
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
            PrivateDnsPreset.ADGUARD -> "dns.adguard.com"
            PrivateDnsPreset.CLOUDFLARE -> "one.one.one.one"
            PrivateDnsPreset.CUSTOM -> customDnsInput.ifBlank { privateDnsCustomHostname }
            PrivateDnsPreset.OFF -> null
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
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        tint = Color(0xFF061018),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.privateDnsOpenSettingsBtn(appLanguage),
                        color = Color(0xFF061018),
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
                        color = if (glassStyle.isDark) Color(0xFF0B111C) else Color(0xFFF1F5F9),
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
