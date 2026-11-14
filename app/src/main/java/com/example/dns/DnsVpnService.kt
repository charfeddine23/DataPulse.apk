package com.example.dns

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import com.example.MainActivity
import com.example.data.DataPlanPreferences
import com.example.model.PrivateDnsPreset
import com.example.util.SmartAlertNotifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicLong

/**
 * Shared observable state for [DnsVpnService], allowing both Jetpack Compose screens
 * and [DnsQuickSettingsTileService] to stay synchronized at 60/120fps without polling.
 */
object DnsVpnState {
    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

    private val _activeHostname = MutableStateFlow("dns.adguard.com")
    val activeHostname: StateFlow<String> = _activeHostname.asStateFlow()

    private val _queriesForwarded = MutableStateFlow(0L)
    val queriesForwarded: StateFlow<Long> = _queriesForwarded.asStateFlow()

    private val queryCounter = AtomicLong(0L)

    internal fun setRunning(hostname: String) {
        _activeHostname.value = hostname
        _isActive.value = true
    }

    internal fun setStopped() {
        _isActive.value = false
    }

    internal fun incrementQueries() {
        _queriesForwarded.value = queryCounter.incrementAndGet()
    }
}

/**
 * Lightweight local DNS-only VPN Service.
 *
 * Routes ONLY DNS server IP addresses through a virtual TUN interface (no global 0.0.0.0/0
 * capture), preserving full native bandwidth for regular traffic while forwarding UDP DNS
 * queries via protected sockets to the configured resolver (AdGuard, Cloudflare, or Custom).
 */
class DnsVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var serviceScope: CoroutineScope? = null
    private var forwardJob: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopVpnTunnel()
            stopSelf()
            return START_NOT_STICKY
        }

        val prefs = DataPlanPreferences(applicationContext)
        val preset = PrivateDnsPreset.fromName(prefs.privateDnsPreset)
        val effectivePreset = if (preset == PrivateDnsPreset.OFF) PrivateDnsPreset.ADGUARD else preset
        val customHost = prefs.privateDnsCustomHostname

        startVpnTunnel(effectivePreset, customHost)
        return START_STICKY
    }

    private fun startVpnTunnel(preset: PrivateDnsPreset, customHostname: String) {
        stopVpnTunnel()

        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        serviceScope = scope

        forwardJob = scope.launch {
            val (primaryIp, secondaryIp, displayHost) = resolveUpstreamServers(preset, customHostname)

            val launchIntent = Intent(applicationContext, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                putExtra(SmartAlertNotifier.EXTRA_NAV_DESTINATION, "SETTINGS")
            }
            val configurePending = PendingIntent.getActivity(
                applicationContext,
                REQUEST_CODE_CONFIGURE,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = Builder()
                .setSession("Data Pulse DNS ($displayHost)")
                .setConfigureIntent(configurePending)
                .setMtu(1500)
                .addAddress(VIRTUAL_TUN_ADDRESS, 32)
                .addDnsServer(primaryIp)
                .addDnsServer(secondaryIp)
                .addRoute(primaryIp, 32)
                .addRoute(secondaryIp, 32)
                .setBlocking(true)

            try {
                builder.addDisallowedApplication(packageName)
            } catch (_: Exception) {
                // Ignore if disallowed self fails on custom ROMs
            }

            val tun = try {
                builder.establish()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to establish DNS VPN TUN interface", e)
                null
            }

            if (tun == null) {
                DnsVpnState.setStopped()
                DnsQuickSettingsTileService.requestTileSync(applicationContext)
                stopSelf()
                return@launch
            }

            vpnInterface = tun
            DnsVpnState.setRunning(displayHost)
            DnsQuickSettingsTileService.requestTileSync(applicationContext)

            runPacketForwardLoop(tun, primaryIp, secondaryIp)
        }
    }

    private fun resolveUpstreamServers(
        preset: PrivateDnsPreset,
        customHostname: String
    ): Triple<String, String, String> {
        return when (preset) {
            PrivateDnsPreset.ADGUARD, PrivateDnsPreset.OFF ->
                Triple(ADGUARD_PRIMARY_IPV4, ADGUARD_SECONDARY_IPV4, "dns.adguard.com")

            PrivateDnsPreset.CLOUDFLARE ->
                Triple(CLOUDFLARE_PRIMARY_IPV4, CLOUDFLARE_SECONDARY_IPV4, "one.one.one.one")

            PrivateDnsPreset.CUSTOM -> {
                val host = customHostname.trim().ifBlank { "dns.adguard.com" }
                val resolvedIp = try {
                    InetAddress.getAllByName(host)
                        .firstOrNull { it.address.size == 4 }
                        ?.hostAddress
                } catch (_: Exception) {
                    null
                }
                if (!resolvedIp.isNullOrBlank()) {
                    Triple(resolvedIp, ADGUARD_PRIMARY_IPV4, host)
                } else {
                    Triple(ADGUARD_PRIMARY_IPV4, ADGUARD_SECONDARY_IPV4, host)
                }
            }
        }
    }

    private fun runPacketForwardLoop(
        tun: ParcelFileDescriptor,
        primaryUpstreamIp: String,
        secondaryUpstreamIp: String
    ) {
        val input = FileInputStream(tun.fileDescriptor)
        val output = FileOutputStream(tun.fileDescriptor)
        val packetBuf = ByteArray(32767)
        val fallbackUpstream = try {
            InetAddress.getByName(primaryUpstreamIp)
        } catch (_: Exception) {
            null
        }

        while (serviceScope?.isActive == true && vpnInterface != null) {
            val length = try {
                input.read(packetBuf)
            } catch (_: Exception) {
                break
            }
            if (length <= 0) continue

            // Parse IPv4 header
            if (length < 28) continue
            val version = (packetBuf[0].toInt() ushr 4) and 0x0F
            if (version != 4) continue

            val ihl = (packetBuf[0].toInt() and 0x0F) * 4
            val protocol = packetBuf[9].toInt() and 0xFF
            if (protocol != UDP_PROTOCOL || length < ihl + 8) continue

            val dstPort = ((packetBuf[ihl + 2].toInt() and 0xFF) shl 8) or
                (packetBuf[ihl + 3].toInt() and 0xFF)
            if (dstPort != DNS_PORT) continue

            val srcIpBytes = packetBuf.copyOfRange(12, 16)
            val dstIpBytes = packetBuf.copyOfRange(16, 20)
            val srcPort = ((packetBuf[ihl].toInt() and 0xFF) shl 8) or
                (packetBuf[ihl + 1].toInt() and 0xFF)
            val udpOffset = ihl
            val udpLength = ((packetBuf[udpOffset + 4].toInt() and 0xFF) shl 8) or
                (packetBuf[udpOffset + 5].toInt() and 0xFF)
            val dnsPayloadOffset = udpOffset + 8
            val dnsPayloadLen = (udpLength - 8).coerceAtMost(length - dnsPayloadOffset)
            if (dnsPayloadLen <= 0) continue

            val dnsQuery = packetBuf.copyOfRange(dnsPayloadOffset, dnsPayloadOffset + dnsPayloadLen)
            val targetIpStr = try {
                InetAddress.getByAddress(dstIpBytes).hostAddress
            } catch (_: Exception) {
                primaryUpstreamIp
            }

            // Forward query asynchronously so concurrent DNS lookups do not block the TUN reader
            serviceScope?.launch {
                val upstreamAddress = try {
                    when (targetIpStr) {
                        primaryUpstreamIp, secondaryUpstreamIp -> InetAddress.getByAddress(dstIpBytes)
                        else -> fallbackUpstream
                    }
                } catch (_: Exception) {
                    fallbackUpstream
                } ?: return@launch

                val dnsResponse = forwardUdpDnsQuery(dnsQuery, upstreamAddress) ?: return@launch
                DnsVpnState.incrementQueries()

                val replyPacket = buildIpv4UdpReplyPacket(
                    srcIp = dstIpBytes,
                    dstIp = srcIpBytes,
                    srcPort = dstPort,
                    dstPort = srcPort,
                    payload = dnsResponse
                )

                synchronized(output) {
                    try {
                        output.write(replyPacket)
                    } catch (_: Exception) {
                        // TUN closed concurrently
                    }
                }
            }
        }
    }

    private fun forwardUdpDnsQuery(query: ByteArray, upstream: InetAddress): ByteArray? {
        var socket: DatagramSocket? = null
        return try {
            socket = DatagramSocket().apply {
                soTimeout = 3000
            }
            if (!protect(socket)) {
                return null
            }
            val outPacket = DatagramPacket(query, query.size, upstream, DNS_PORT)
            socket.send(outPacket)

            val recvBuf = ByteArray(4096)
            val inPacket = DatagramPacket(recvBuf, recvBuf.size)
            socket.receive(inPacket)
            recvBuf.copyOfRange(0, inPacket.length)
        } catch (_: Exception) {
            null
        } finally {
            try {
                socket?.close()
            } catch (_: Exception) {
            }
        }
    }

    private fun buildIpv4UdpReplyPacket(
        srcIp: ByteArray,
        dstIp: ByteArray,
        srcPort: Int,
        dstPort: Int,
        payload: ByteArray
    ): ByteArray {
        val ipHeaderLen = 20
        val udpHeaderLen = 8
        val totalLen = ipHeaderLen + udpHeaderLen + payload.size
        val packet = ByteArray(totalLen)
        val buffer = ByteBuffer.wrap(packet)

        // IPv4 Header
        buffer.put(0x45.toByte()) // Version 4, IHL 5 (20 bytes)
        buffer.put(0x00.toByte()) // TOS
        buffer.putShort(totalLen.toShort())
        buffer.putShort(0.toShort()) // Identification
        buffer.putShort(0x4000.toShort()) // Flags: Don't Fragment
        buffer.put(64.toByte()) // TTL
        buffer.put(UDP_PROTOCOL.toByte())
        buffer.putShort(0.toShort()) // IP Checksum placeholder
        buffer.put(srcIp)
        buffer.put(dstIp)

        // Compute IPv4 Header Checksum
        val ipChecksum = computeChecksum(packet, 0, ipHeaderLen)
        packet[10] = ((ipChecksum ushr 8) and 0xFF).toByte()
        packet[11] = (ipChecksum and 0xFF).toByte()

        // UDP Header
        val udpLen = udpHeaderLen + payload.size
        buffer.putShort(srcPort.toShort())
        buffer.putShort(dstPort.toShort())
        buffer.putShort(udpLen.toShort())
        buffer.putShort(0.toShort()) // UDP Checksum placeholder
        buffer.put(payload)

        // Compute UDP Checksum (IPv4 pseudo-header + UDP header + payload)
        val udpChecksum = computeUdpChecksum(srcIp, dstIp, packet, ipHeaderLen, udpLen)
        packet[ipHeaderLen + 6] = ((udpChecksum ushr 8) and 0xFF).toByte()
        packet[ipHeaderLen + 7] = (udpChecksum and 0xFF).toByte()

        return packet
    }

    private fun computeChecksum(data: ByteArray, offset: Int, length: Int): Int {
        var sum = 0L
        var i = offset
        while (i < offset + length - 1) {
            val word = ((data[i].toInt() and 0xFF) shl 8) or (data[i + 1].toInt() and 0xFF)
            sum += word
            i += 2
        }
        if ((length and 1) != 0) {
            sum += (data[offset + length - 1].toInt() and 0xFF) shl 8
        }
        while ((sum ushr 16) != 0L) {
            sum = (sum and 0xFFFFL) + (sum ushr 16)
        }
        return (sum.inv() and 0xFFFFL).toInt()
    }

    private fun computeUdpChecksum(
        srcIp: ByteArray,
        dstIp: ByteArray,
        packet: ByteArray,
        udpOffset: Int,
        udpLen: Int
    ): Int {
        var sum = 0L
        for (i in 0 until 4 step 2) {
            sum += ((srcIp[i].toInt() and 0xFF) shl 8) or (srcIp[i + 1].toInt() and 0xFF)
            sum += ((dstIp[i].toInt() and 0xFF) shl 8) or (dstIp[i + 1].toInt() and 0xFF)
        }
        sum += UDP_PROTOCOL.toLong()
        sum += udpLen.toLong()

        var i = udpOffset
        val end = udpOffset + udpLen
        while (i < end - 1) {
            val word = ((packet[i].toInt() and 0xFF) shl 8) or (packet[i + 1].toInt() and 0xFF)
            sum += word
            i += 2
        }
        if ((udpLen and 1) != 0) {
            sum += (packet[end - 1].toInt() and 0xFF) shl 8
        }
        while ((sum ushr 16) != 0L) {
            sum = (sum and 0xFFFFL) + (sum ushr 16)
        }
        val result = (sum.inv() and 0xFFFFL).toInt()
        return if (result == 0) 0xFFFF else result
    }

    private fun stopVpnTunnel() {
        forwardJob?.cancel()
        forwardJob = null
        serviceScope?.cancel()
        serviceScope = null
        try {
            vpnInterface?.close()
        } catch (_: Exception) {
        }
        vpnInterface = null
        DnsVpnState.setStopped()
        DnsQuickSettingsTileService.requestTileSync(applicationContext)
    }

    override fun onRevoke() {
        super.onRevoke()
        stopVpnTunnel()
        stopSelf()
    }

    override fun onDestroy() {
        stopVpnTunnel()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "DnsVpnService"
        private const val REQUEST_CODE_CONFIGURE = 4091
        private const val VIRTUAL_TUN_ADDRESS = "10.111.222.1"
        private const val ADGUARD_PRIMARY_IPV4 = "94.140.14.14"
        private const val ADGUARD_SECONDARY_IPV4 = "94.140.15.15"
        private const val CLOUDFLARE_PRIMARY_IPV4 = "1.1.1.1"
        private const val CLOUDFLARE_SECONDARY_IPV4 = "1.0.0.1"
        private const val UDP_PROTOCOL = 17
        private const val DNS_PORT = 53

        const val ACTION_START = "com.example.dns.ACTION_START_VPN"
        const val ACTION_STOP = "com.example.dns.ACTION_STOP_VPN"

        fun start(context: Context) {
            val intent = Intent(context, DnsVpnService::class.java).apply {
                action = ACTION_START
            }
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, DnsVpnService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
}
