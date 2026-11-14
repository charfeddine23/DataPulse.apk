package com.example.dns

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.net.VpnService
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.example.MainActivity
import com.example.R
import com.example.data.DataPlanPreferences
import com.example.model.PrivateDnsPreset
import com.example.util.SmartAlertNotifier

/**
 * Android Quick Settings Tile allowing 1-tap toggling of Data Pulse's AdGuard / Local DNS Shield
 * directly from the system notification shade.
 */
@RequiresApi(Build.VERSION_CODES.N)
class DnsQuickSettingsTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        syncTileVisualState()
    }

    override fun onTileAdded() {
        super.onTileAdded()
        syncTileVisualState()
    }

    override fun onClick() {
        super.onClick()
        if (DnsVpnState.isActive.value) {
            DnsVpnService.stop(applicationContext)
            syncTileVisualState(forceActive = false)
        } else {
            val prepareIntent = VpnService.prepare(applicationContext)
            if (prepareIntent == null) {
                val prefs = DataPlanPreferences(applicationContext)
                if (prefs.privateDnsPreset == PrivateDnsPreset.OFF.name) {
                    prefs.privateDnsPreset = PrivateDnsPreset.ADGUARD.name
                }
                DnsVpnService.start(applicationContext)
                syncTileVisualState(forceActive = true)
            } else {
                // First-time VPN consent requires launching MainActivity so user can confirm the OS dialog
                val activityIntent = Intent(applicationContext, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra(SmartAlertNotifier.EXTRA_NAV_DESTINATION, "DNS")
                    putExtra(EXTRA_REQUEST_VPN_CONSENT, true)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    val pendingIntent = PendingIntent.getActivity(
                        applicationContext,
                        REQUEST_CODE_TILE_CONSENT,
                        activityIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    startActivityAndCollapse(pendingIntent)
                } else {
                    @Suppress("DEPRECATION")
                    startActivityAndCollapse(activityIntent)
                }
            }
        }
    }

    private fun syncTileVisualState(forceActive: Boolean? = null) {
        val tile = qsTile ?: return
        val isActive = forceActive ?: DnsVpnState.isActive.value
        val prefs = DataPlanPreferences(applicationContext)
        val preset = PrivateDnsPreset.fromName(prefs.privateDnsPreset)
        val host = when (preset) {
            PrivateDnsPreset.CUSTOM -> prefs.privateDnsCustomHostname.ifBlank { "dns.adguard.com" }
            PrivateDnsPreset.OFF -> "dns.adguard.com"
            else -> preset.hostname ?: "dns.adguard.com"
        }

        tile.state = if (isActive) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.label = getString(R.string.dns_tile_label)
        tile.icon = Icon.createWithResource(applicationContext, R.drawable.ic_notification_pulse)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = if (isActive) host else "Tap to protect"
        }
        tile.updateTile()
    }

    companion object {
        const val EXTRA_REQUEST_VPN_CONSENT = "com.example.dns.EXTRA_REQUEST_VPN_CONSENT"
        private const val REQUEST_CODE_TILE_CONSENT = 5120

        fun requestTileSync(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    requestListeningState(
                        context,
                        ComponentName(context, DnsQuickSettingsTileService::class.java)
                    )
                } catch (_: Exception) {
                }
            }
        }
    }
}
