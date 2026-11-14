package com.example

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.navigation.DataPulseApp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val pendingTileVpnConsent = kotlinx.coroutines.flow.MutableStateFlow(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleNotificationIntent(intent)

        setContent {
            val appThemeMode by viewModel.appThemeMode.collectAsStateWithLifecycle()
            val shouldPromptVpnConsent by pendingTileVpnConsent.collectAsStateWithLifecycle()

            com.example.ui.theme.DataPulseTheme(appThemeMode = appThemeMode) {
                // Request Phone State (for Dual SIM carrier resolution) & Notification permissions
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) {
                    viewModel.loadSimCards()
                }

                val tileVpnConsentLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == RESULT_OK) {
                        viewModel.startLocalDnsVpn()
                    }
                }

                LaunchedEffect(Unit) {
                    val permissionsToRequest = mutableListOf<String>()
                    permissionsToRequest.add(Manifest.permission.READ_PHONE_STATE)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    permissionLauncher.launch(permissionsToRequest.toTypedArray())
                }

                LaunchedEffect(shouldPromptVpnConsent) {
                    if (shouldPromptVpnConsent) {
                        pendingTileVpnConsent.value = false
                        val prepareIntent = android.net.VpnService.prepare(this@MainActivity)
                        if (prepareIntent == null) {
                            viewModel.startLocalDnsVpn()
                        } else {
                            tileVpnConsentLauncher.launch(prepareIntent)
                        }
                    }
                }

                DataPulseApp(
                    viewModel = viewModel,
                    onRequestUsagePermission = { openUsageAccessSettings() },
                    onOpenAppInfo = { openAppInfoSettings() }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        val dest = intent?.getStringExtra(com.example.util.SmartAlertNotifier.EXTRA_NAV_DESTINATION)
        if (!dest.isNullOrBlank()) {
            viewModel.setTargetNavDestination(dest)
        }
        val requestVpn = intent?.getBooleanExtra(com.example.dns.DnsQuickSettingsTileService.EXTRA_REQUEST_VPN_CONSENT, false) == true
        if (requestVpn) {
            val prepareIntent = android.net.VpnService.prepare(this)
            if (prepareIntent == null) {
                viewModel.startLocalDnsVpn()
            } else {
                pendingTileVpnConsent.value = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh stats automatically when returning from Settings or background
        viewModel.refreshAll()
        viewModel.resumePendingInstallIfPermitted(this)
    }

    private fun openUsageAccessSettings() {
        // Many Android versions and custom ROMs (MIUI, HyperOS, ColorOS, Samsung) crash or ignore package: URI on ACTION_USAGE_ACCESS_SETTINGS
        try {
            val generalIntent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(generalIntent)
        } catch (_: Exception) {
            try {
                val packageIntent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                    data = Uri.parse("package:$packageName")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(packageIntent)
            } catch (_: Exception) {
                openAppInfoSettings()
            }
        }
    }

    private fun openAppInfoSettings() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
            startActivity(intent)
        } catch (_: Exception) {
            try {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            } catch (_: Exception) {
                // Ignore fallback
            }
        }
    }
}
