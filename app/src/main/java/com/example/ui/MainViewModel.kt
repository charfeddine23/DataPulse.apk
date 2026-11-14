package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DataPlanPreferences
import com.example.data.NetworkStatsRepository
import com.example.model.AppUsageItem
import com.example.model.DataPlanQuota
import com.example.model.InterfaceFilter
import com.example.model.LiveSpeedSample
import com.example.model.NetworkUsageSummary
import com.example.model.PrivateDnsPreset
import com.example.model.QuotaStatus
import com.example.model.SimCardInfo
import com.example.model.TimeRange
import com.example.util.LiveSpeedMonitor
import com.example.util.SmartAlertNotifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NetworkStatsRepository(application)
    private val preferences = DataPlanPreferences(application)
    private val themePreferences = com.example.data.ThemePreferences(application)
    private val appQuotaPreferences = com.example.data.AppQuotaPreferences(application)
    private val speedMonitor = LiveSpeedMonitor(application, viewModelScope)
    private val alertNotifier = SmartAlertNotifier(application)

    val speedSample: StateFlow<LiveSpeedSample> = speedMonitor.speedSample
    val speedHistory: StateFlow<List<Float>> = speedMonitor.speedHistory

    // Observable map of per-app custom limits backed by DataStore
    val customAppLimits: StateFlow<Map<String, com.example.data.AppCustomLimitConfig>> =
        appQuotaPreferences.allCustomLimitsFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    private val _usageSummary = MutableStateFlow(NetworkUsageSummary())
    val usageSummary: StateFlow<NetworkUsageSummary> = _usageSummary.asStateFlow()

    private val _isDashboardRefreshing = MutableStateFlow(false)
    val isDashboardRefreshing: StateFlow<Boolean> = _isDashboardRefreshing.asStateFlow()

    private val _rawAppUsageList = MutableStateFlow<List<AppUsageItem>>(emptyList())
    private val _isRefreshingApps = MutableStateFlow(false)
    val isRefreshingApps: StateFlow<Boolean> = _isRefreshingApps.asStateFlow()

    private val _selectedTimeRange = MutableStateFlow(preferences.savedTimeRange)
    val selectedTimeRange: StateFlow<TimeRange> = _selectedTimeRange.asStateFlow()

    private val _selectedInterface = MutableStateFlow(InterfaceFilter.ALL)
    val selectedInterface: StateFlow<InterfaceFilter> = _selectedInterface.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _simCards = MutableStateFlow<List<SimCardInfo>>(emptyList())
    val simCards: StateFlow<List<SimCardInfo>> = _simCards.asStateFlow()

    private val _selectedSimIndex = MutableStateFlow(preferences.selectedSimIndex)
    val selectedSimIndex: StateFlow<Int> = _selectedSimIndex.asStateFlow()

    private var cachedSim1Summary = NetworkUsageSummary()
    private var cachedSim2Summary = NetworkUsageSummary()
    private var cachedSim1Quota = DataPlanQuota()
    private var cachedSim2Quota = DataPlanQuota()
    private var cachedSim1Forecast = com.example.model.SmartDataForecast()
    private var cachedSim2Forecast = com.example.model.SmartDataForecast()

    private val _dataPlanQuota = MutableStateFlow(DataPlanQuota())
    val dataPlanQuota: StateFlow<DataPlanQuota> = _dataPlanQuota.asStateFlow()

    private val _targetNavDestination = MutableStateFlow<String?>(null)
    val targetNavDestination: StateFlow<String?> = _targetNavDestination.asStateFlow()

    fun setTargetNavDestination(destination: String?) {
        _targetNavDestination.value = destination
    }

    private val _dataForecast = MutableStateFlow(com.example.model.SmartDataForecast())
    val dataForecast: StateFlow<com.example.model.SmartDataForecast> = _dataForecast.asStateFlow()

    private val _allTimeRankedApps = MutableStateFlow<List<AppUsageItem>>(emptyList())
    val allTimeRankedApps: StateFlow<List<AppUsageItem>> = _allTimeRankedApps.asStateFlow()

    private val _isRankRefreshing = MutableStateFlow(false)
    val isRankRefreshing: StateFlow<Boolean> = _isRankRefreshing.asStateFlow()

    private val _hasUsagePermission = MutableStateFlow(repository.hasUsageStatsPermission())
    val hasUsagePermission: StateFlow<Boolean> = _hasUsagePermission.asStateFlow()

    // Observable AppThemeMode backed by Jetpack DataStore
    val appThemeMode: StateFlow<com.example.model.AppThemeMode> = themePreferences.themeModeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = com.example.model.AppThemeMode.LIQUID_GLASS_DEFAULT
        )

    private val _themeMode = MutableStateFlow(
        try {
            com.example.model.ThemeMode.valueOf(preferences.themeMode)
        } catch (_: Exception) {
            com.example.model.ThemeMode.DARK
        }
    )
    val themeMode: StateFlow<com.example.model.ThemeMode> = _themeMode.asStateFlow()

    private val _powerSavingMode = MutableStateFlow(preferences.powerSavingMode)
    val powerSavingMode: StateFlow<Boolean> = _powerSavingMode.asStateFlow()

    private val _dailyAppQuotaGB = MutableStateFlow(preferences.dailyAppQuotaGB)
    val dailyAppQuotaGB: StateFlow<Float> = _dailyAppQuotaGB.asStateFlow()

    private val _dailyAppAlertEnabled = MutableStateFlow(preferences.dailyAppAlertEnabled)
    val dailyAppAlertEnabled: StateFlow<Boolean> = _dailyAppAlertEnabled.asStateFlow()

    private val _appLanguage = MutableStateFlow(
        if (preferences.appLanguage == "EN") com.example.model.AppLanguage.ENGLISH else com.example.model.AppLanguage.ARABIC
    )
    val appLanguage: StateFlow<com.example.model.AppLanguage> = _appLanguage.asStateFlow()

    private val _privateDnsPreset = MutableStateFlow(
        try {
            PrivateDnsPreset.valueOf(preferences.privateDnsPreset)
        } catch (_: Exception) {
            PrivateDnsPreset.OFF
        }
    )
    val privateDnsPreset: StateFlow<PrivateDnsPreset> = _privateDnsPreset.asStateFlow()

    private val _privateDnsCustomHostname = MutableStateFlow(preferences.privateDnsCustomHostname)
    val privateDnsCustomHostname: StateFlow<String> = _privateDnsCustomHostname.asStateFlow()

    // Filtered app usage list based on interface & search
    val filteredAppUsage: StateFlow<List<AppUsageItem>> = combine(
        _rawAppUsageList,
        _selectedInterface,
        _searchQuery
    ) { apps, iface, query ->
        apps.filter { app ->
            val matchesQuery = query.isBlank() ||
                    app.appName.contains(query, ignoreCase = true) ||
                    app.packageName.contains(query, ignoreCase = true)

            val matchesInterface = when (iface) {
                InterfaceFilter.ALL -> app.totalBytes > 0
                InterfaceFilter.MOBILE -> app.mobileBytes > 0
                InterfaceFilter.WIFI -> app.wifiBytes > 0
            }
            matchesQuery && matchesInterface
        }.sortedByDescending { app ->
            when (iface) {
                InterfaceFilter.ALL -> app.totalBytes
                InterfaceFilter.MOBILE -> app.mobileBytes
                InterfaceFilter.WIFI -> app.wifiBytes
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val appUpdateManager = com.example.update.AppUpdateManager(application)
    private val _appUpdateState = MutableStateFlow<com.example.update.AppUpdateUiState>(
        com.example.update.AppUpdateUiState.Hidden
    )
    val appUpdateState: StateFlow<com.example.update.AppUpdateUiState> = _appUpdateState.asStateFlow()

    init {
        speedMonitor.setPowerSaving(preferences.powerSavingMode)
        loadSimCards()
        refreshAll()
        checkForAppUpdate(manual = false)
    }

    fun loadSimCards() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getSimCards()
            _simCards.value = list
        }
    }

    fun refreshAll() {
        val perm = repository.hasUsageStatsPermission()
        _hasUsagePermission.value = perm

        viewModelScope.launch {
            _isDashboardRefreshing.value = true
            try {
                val startMs = System.currentTimeMillis()

                // 1. Fetch & compute stats for SIM 1 (Slot 0)
                val summary1 = repository.fetchUsageSummary(preferences.billingCycleStartDay, slotIndex = 0)
                cachedSim1Summary = summary1
                cachedSim1Quota = computeQuotaForSim(slotIndex = 0, usedMobileBytes = summary1.mobileMonthBytes, mobileTodayBytes = summary1.mobileTodayBytes)
                cachedSim1Forecast = com.example.model.DataForecastCalculator.calculate(
                    usedMobileBytes = summary1.mobileMonthBytes,
                    planLimitGB = preferences.getPlanLimitGB(0),
                    billingCycleStartDay = preferences.billingCycleStartDay,
                    daysRemainingInCycle = repository.getDaysRemainingInCycle(preferences.billingCycleStartDay),
                    mobileTodayBytes = summary1.mobileTodayBytes
                )

                // 2. Fetch & compute stats for SIM 2 (Slot 1)
                val summary2 = repository.fetchUsageSummary(preferences.billingCycleStartDay, slotIndex = 1)
                cachedSim2Summary = summary2
                cachedSim2Quota = computeQuotaForSim(slotIndex = 1, usedMobileBytes = summary2.mobileMonthBytes, mobileTodayBytes = summary2.mobileTodayBytes)
                cachedSim2Forecast = com.example.model.DataForecastCalculator.calculate(
                    usedMobileBytes = summary2.mobileMonthBytes,
                    planLimitGB = preferences.getPlanLimitGB(1),
                    billingCycleStartDay = preferences.billingCycleStartDay,
                    daysRemainingInCycle = repository.getDaysRemainingInCycle(preferences.billingCycleStartDay),
                    mobileTodayBytes = summary2.mobileTodayBytes
                )

                // 3. Apply active SIM data to public StateFlows
                applyActiveSimStats(_selectedSimIndex.value)

                loadAppUsage()
                refreshRankings()
                val elapsed = System.currentTimeMillis() - startMs
                if (elapsed < 1100L) {
                    kotlinx.coroutines.delay(1100L - elapsed)
                }
            } catch (_: Exception) {
                // Ensure resilient recovery
            } finally {
                _isDashboardRefreshing.value = false
            }
        }
    }

    fun setTimeRange(range: TimeRange) {
        _selectedTimeRange.value = range
        preferences.savedTimeRange = range
        loadAppUsage()
    }

    fun setInterfaceFilter(filter: InterfaceFilter) {
        _selectedInterface.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSimIndex(index: Int) {
        val validIndex = index.coerceIn(0, 1)
        _selectedSimIndex.value = validIndex
        preferences.selectedSimIndex = validIndex
        applyActiveSimStats(validIndex)
    }

    private var lastForecastNotifiedTimeMs = 0L

    private fun checkForecastAlert(forecast: com.example.model.SmartDataForecast) {
        val now = System.currentTimeMillis()
        // Alert at most once every 6 hours for critical burn rate depletion
        if (now - lastForecastNotifiedTimeMs < 6 * 60 * 60 * 1000L) return

        if (forecast.willDepleteBeforeCycleEnd && forecast.predictedDaysRemaining in 1..3) {
            lastForecastNotifiedTimeMs = now
            val isArabic = _appLanguage.value == com.example.model.AppLanguage.ARABIC
            alertNotifier.notifySmartDepletionWarning(
                daysRemaining = forecast.predictedDaysRemaining,
                planLimitGB = forecast.planLimitGB,
                burnRateFormatted = forecast.dailyBurnRateFormatted,
                isArabic = isArabic
            )
        }
    }

    private fun applyActiveSimStats(slotIndex: Int) {
        val (summary, quota, forecast) = if (slotIndex == 1) {
            Triple(cachedSim2Summary, cachedSim2Quota, cachedSim2Forecast)
        } else {
            Triple(cachedSim1Summary, cachedSim1Quota, cachedSim1Forecast)
        }
        _usageSummary.value = summary
        _dataPlanQuota.value = quota
        _dataForecast.value = forecast

        checkForecastAlert(forecast)
    }

    fun sendTestNotification() {
        val isArabic = _appLanguage.value == com.example.model.AppLanguage.ARABIC
        alertNotifier.sendTestHeadsUpNotification(isArabic)
    }

    private val notifiedAppsToday = mutableSetOf<String>()

    private fun loadAppUsage() {
        viewModelScope.launch {
            _isRefreshingApps.value = true
            val apps = repository.fetchAppUsage(_selectedTimeRange.value, preferences.billingCycleStartDay)
            _rawAppUsageList.value = apps
            _isRefreshingApps.value = false

            // Check if any app exceeded 3 GB today
            checkAndAlertDailyAppQuota(apps)
        }
    }

    fun refreshRankings() {
        viewModelScope.launch {
            _isRankRefreshing.value = true
            try {
                val startMs = System.currentTimeMillis()
                val ranked = repository.fetchAllTimeRankedAppUsage()
                _allTimeRankedApps.value = ranked
                val elapsed = System.currentTimeMillis() - startMs
                if (elapsed < 1100L) {
                    kotlinx.coroutines.delay(1100L - elapsed)
                }
            } catch (_: Exception) {
            } finally {
                _isRankRefreshing.value = false
            }
        }
    }

    private fun checkAndAlertDailyAppQuota(apps: List<AppUsageItem>) {
        val globalDefaultGB = _dailyAppQuotaGB.value
        val isGlobalEnabled = _dailyAppAlertEnabled.value
        val isArabic = _appLanguage.value == com.example.model.AppLanguage.ARABIC
        val perAppConfigs = customAppLimits.value

        viewModelScope.launch(Dispatchers.IO) {
            // Query today's usage specifically to ensure accurate daily telemetry
            val todayApps = if (_selectedTimeRange.value == TimeRange.TODAY) {
                apps
            } else {
                repository.fetchAppUsage(TimeRange.TODAY, preferences.billingCycleStartDay)
            }

            for (app in todayApps) {
                val sanitizedPkg = app.packageName.replace('.', '_')
                val customConfig = perAppConfigs[sanitizedPkg]

                val isAlertEnabled = customConfig?.alertEnabled ?: isGlobalEnabled
                if (!isAlertEnabled) continue

                val appThresholdGB = customConfig?.dailyLimitGB ?: globalDefaultGB
                val thresholdBytes = (appThresholdGB * 1024L * 1024L * 1024L).toLong()

                if (app.totalBytes >= thresholdBytes) {
                    val alertKey = "${app.packageName}_${appThresholdGB}"
                    if (!notifiedAppsToday.contains(alertKey)) {
                        notifiedAppsToday.add(alertKey)
                        alertNotifier.notifyAppDailyQuotaExceeded(
                            appName = app.appName,
                            usedBytes = app.totalBytes,
                            thresholdBytes = thresholdBytes,
                            isArabic = isArabic
                        )
                    }
                }
            }
        }
    }

    fun saveAppCustomLimit(packageName: String, dailyLimitGB: Float, alertEnabled: Boolean) {
        viewModelScope.launch {
            appQuotaPreferences.saveAppCustomLimit(packageName, dailyLimitGB, alertEnabled)
            notifiedAppsToday.removeIf { it.startsWith(packageName) }
            loadAppUsage()
        }
    }

    private fun computeQuotaForSim(slotIndex: Int, usedMobileBytes: Long, mobileTodayBytes: Long): DataPlanQuota {
        val limitGB = preferences.getPlanLimitGB(slotIndex)
        val limitBytes = (limitGB * 1024L * 1024L * 1024L).toLong()
        val remainingBytes = (limitBytes - usedMobileBytes).coerceAtLeast(0L)
        val percent = if (limitBytes > 0) ((usedMobileBytes.toDouble() / limitBytes.toDouble()) * 100.0).toFloat() else 0f
        val daysRemaining = repository.getDaysRemainingInCycle(preferences.billingCycleStartDay)

        val status = when {
            percent >= 100f -> QuotaStatus.EXCEEDED
            percent >= 95f -> QuotaStatus.CRITICAL
            percent >= 80f -> QuotaStatus.WARNING
            else -> QuotaStatus.SAFE
        }

        if (slotIndex == _selectedSimIndex.value) {
            triggerAlertIfNeeded(status, usedMobileBytes, limitBytes, percent)
        }

        return DataPlanQuota(
            planLimitGB = limitGB,
            billingCycleStartDay = preferences.billingCycleStartDay,
            alertThreshold80 = preferences.alert80Enabled,
            alertThreshold95 = preferences.alert95Enabled,
            usedBytes = usedMobileBytes,
            remainingBytes = remainingBytes,
            usagePercent = percent,
            daysRemaining = daysRemaining,
            status = status
        )
    }

    private fun triggerAlertIfNeeded(status: QuotaStatus, used: Long, limit: Long, percent: Float) {
        val lastNotified = preferences.lastNotifiedThreshold
        if (status == QuotaStatus.CRITICAL && preferences.alert95Enabled && lastNotified < 95) {
            preferences.lastNotifiedThreshold = 95
            alertNotifier.notifyQuotaThreshold(status, used, limit, percent)
        } else if (status == QuotaStatus.WARNING && preferences.alert80Enabled && lastNotified < 80) {
            preferences.lastNotifiedThreshold = 80
            alertNotifier.notifyQuotaThreshold(status, used, limit, percent)
        } else if (status == QuotaStatus.SAFE) {
            preferences.lastNotifiedThreshold = 0
        }
    }

    fun updatePlanLimit(limitGB: Float) {
        val activeSlot = _selectedSimIndex.value
        val clamped = limitGB.coerceIn(0.1f, 10000f)
        preferences.setPlanLimitGB(activeSlot, clamped)
        val currentSummary = if (activeSlot == 1) cachedSim2Summary else cachedSim1Summary
        val updatedQuota = computeQuotaForSim(activeSlot, currentSummary.mobileMonthBytes, currentSummary.mobileTodayBytes)
        val updatedForecast = com.example.model.DataForecastCalculator.calculate(
            usedMobileBytes = currentSummary.mobileMonthBytes,
            planLimitGB = clamped,
            billingCycleStartDay = preferences.billingCycleStartDay,
            daysRemainingInCycle = repository.getDaysRemainingInCycle(preferences.billingCycleStartDay),
            mobileTodayBytes = currentSummary.mobileTodayBytes
        )
        if (activeSlot == 1) {
            cachedSim2Quota = updatedQuota
            cachedSim2Forecast = updatedForecast
        } else {
            cachedSim1Quota = updatedQuota
            cachedSim1Forecast = updatedForecast
        }
        _dataPlanQuota.value = updatedQuota
        _dataForecast.value = updatedForecast
    }

    fun setThemeMode(mode: com.example.model.ThemeMode) {
        preferences.themeMode = mode.name
        _themeMode.value = mode
    }

    fun setAppThemeMode(themeMode: com.example.model.AppThemeMode) {
        viewModelScope.launch {
            themePreferences.setThemeMode(themeMode)
            val legacy = if (themeMode == com.example.model.AppThemeMode.LIGHT_MODE) {
                com.example.model.ThemeMode.LIGHT
            } else {
                com.example.model.ThemeMode.DARK
            }
            preferences.themeMode = legacy.name
            _themeMode.value = legacy
        }
    }

    fun updateBillingCycleDay(day: Int) {
        preferences.billingCycleStartDay = day.coerceIn(1, 31)
        refreshAll()
    }

    fun toggleAlert80(enabled: Boolean) {
        preferences.alert80Enabled = enabled
        _dataPlanQuota.value = _dataPlanQuota.value.copy(alertThreshold80 = enabled)
    }

    fun toggleAlert95(enabled: Boolean) {
        preferences.alert95Enabled = enabled
        _dataPlanQuota.value = _dataPlanQuota.value.copy(alertThreshold95 = enabled)
    }

    fun updateDailyAppQuotaGB(limitGB: Float) {
        val clamped = limitGB.coerceIn(0.5f, 50.0f)
        preferences.dailyAppQuotaGB = clamped
        _dailyAppQuotaGB.value = clamped
        notifiedAppsToday.clear() // Reset so newly set threshold is evaluated
        loadAppUsage()
    }

    fun toggleDailyAppAlert(enabled: Boolean) {
        preferences.dailyAppAlertEnabled = enabled
        _dailyAppAlertEnabled.value = enabled
        if (enabled) {
            loadAppUsage()
        }
    }

    fun togglePowerSavingMode(enabled: Boolean) {
        preferences.powerSavingMode = enabled
        _powerSavingMode.value = enabled
        speedMonitor.setPowerSaving(enabled)
    }

    fun setAppLanguage(lang: com.example.model.AppLanguage) {
        preferences.appLanguage = lang.code
        _appLanguage.value = lang
    }

    val dnsVpnActive: StateFlow<Boolean> = com.example.dns.DnsVpnState.isActive
    val dnsVpnHost: StateFlow<String> = com.example.dns.DnsVpnState.activeHostname
    val dnsQueriesForwarded: StateFlow<Long> = com.example.dns.DnsVpnState.queriesForwarded

    private val _dnsLatencies = MutableStateFlow<Map<PrivateDnsPreset, Int?>>(emptyMap())
    val dnsLatencies: StateFlow<Map<PrivateDnsPreset, Int?>> = _dnsLatencies.asStateFlow()

    private val _isPingingDns = MutableStateFlow(false)
    val isPingingDns: StateFlow<Boolean> = _isPingingDns.asStateFlow()

    fun refreshDnsLatencies() {
        if (_isPingingDns.value) return
        viewModelScope.launch {
            _isPingingDns.value = true
            try {
                val results = withContext(Dispatchers.IO) {
                    coroutineScope {
                        PrivateDnsPreset.integratedServers.map { preset ->
                            async {
                                preset to (preset.primaryIp?.let { measureDnsUdpLatencyMs(it) })
                            }
                        }.awaitAll().toMap()
                    }
                }
                _dnsLatencies.value = results
            } finally {
                _isPingingDns.value = false
            }
        }
    }

    private fun measureDnsUdpLatencyMs(ipAddress: String): Int? {
        var socket: DatagramSocket? = null
        return try {
            val dnsQueryPacket = byteArrayOf(
                0x24, 0x71, // Transaction ID
                0x01, 0x00, // Flags: standard query, recursion desired
                0x00, 0x01, // Questions: 1
                0x00, 0x00, // Answer RRs: 0
                0x00, 0x00, // Authority RRs: 0
                0x00, 0x00, // Additional RRs: 0
                0x07, 'e'.code.toByte(), 'x'.code.toByte(), 'a'.code.toByte(), 'm'.code.toByte(),
                'p'.code.toByte(), 'l'.code.toByte(), 'e'.code.toByte(),
                0x03, 'c'.code.toByte(), 'o'.code.toByte(), 'm'.code.toByte(),
                0x00,       // End of QNAME
                0x00, 0x01, // QTYPE = A
                0x00, 0x01  // QCLASS = IN
            )
            socket = DatagramSocket().apply {
                soTimeout = 1500
            }
            val address = InetAddress.getByName(ipAddress)
            val request = DatagramPacket(dnsQueryPacket, dnsQueryPacket.size, address, 53)
            val responseBuffer = ByteArray(512)
            val response = DatagramPacket(responseBuffer, responseBuffer.size)

            val startNs = System.nanoTime()
            socket.send(request)
            socket.receive(response)
            val elapsedMs = ((System.nanoTime() - startNs) / 1_000_000L).toInt()
            elapsedMs.coerceAtLeast(1)
        } catch (_: Exception) {
            null
        } finally {
            try {
                socket?.close()
            } catch (_: Exception) {
            }
        }
    }

    fun startLocalDnsVpn() {
        if (_privateDnsPreset.value == PrivateDnsPreset.OFF) {
            setPrivateDnsPreset(PrivateDnsPreset.ADGUARD)
        }
        com.example.dns.DnsVpnService.start(getApplication())
    }

    fun stopLocalDnsVpn() {
        com.example.dns.DnsVpnService.stop(getApplication())
    }

    fun setPrivateDnsPreset(preset: PrivateDnsPreset) {
        preferences.privateDnsPreset = preset.name
        _privateDnsPreset.value = preset
        if (dnsVpnActive.value) {
            if (preset == PrivateDnsPreset.OFF) {
                stopLocalDnsVpn()
            } else {
                com.example.dns.DnsVpnService.start(getApplication())
            }
        }
    }

    fun setPrivateDnsCustomHostname(hostname: String) {
        val trimmed = hostname.trim()
        preferences.privateDnsCustomHostname = trimmed
        _privateDnsCustomHostname.value = trimmed
        if (dnsVpnActive.value && _privateDnsPreset.value == PrivateDnsPreset.CUSTOM && trimmed.isNotBlank()) {
            com.example.dns.DnsVpnService.start(getApplication())
        }
    }

    fun checkForAppUpdate(manual: Boolean = true) {
        viewModelScope.launch {
            if (manual) {
                _appUpdateState.value = com.example.update.AppUpdateUiState.Checking
            }
            val latest = try {
                appUpdateManager.fetchLatestRelease()
            } catch (_: Exception) {
                null
            }
            if (latest != null) {
                _appUpdateState.value = com.example.update.AppUpdateUiState.Available(latest)
            } else if (manual) {
                _appUpdateState.value = com.example.update.AppUpdateUiState.UpToDate
            }
        }
    }

    fun startInAppUpdateDownload(
        callerContext: android.content.Context,
        info: com.example.update.AppUpdateInfo
    ) {
        if (_appUpdateState.value is com.example.update.AppUpdateUiState.Downloading) return
        viewModelScope.launch {
            _appUpdateState.value = com.example.update.AppUpdateUiState.Downloading(
                info = info,
                progress = 0f,
                downloadedBytes = 0L,
                totalBytes = info.apkSizeBytes
            )
            try {
                val apkFile = appUpdateManager.downloadApk(info) { progress, downloaded, total ->
                    _appUpdateState.value = com.example.update.AppUpdateUiState.Downloading(
                        info = info,
                        progress = progress,
                        downloadedBytes = downloaded,
                        totalBytes = total
                    )
                }
                if (appUpdateManager.canInstallPackages()) {
                    _appUpdateState.value = com.example.update.AppUpdateUiState.ReadyToInstall(info, apkFile)
                    appUpdateManager.triggerSystemApkInstaller(callerContext, apkFile)
                } else {
                    _appUpdateState.value = com.example.update.AppUpdateUiState.NeedsInstallPermission(info, apkFile)
                }
            } catch (_: Exception) {
                _appUpdateState.value = com.example.update.AppUpdateUiState.Error(
                    messageAr = "تعذر تنزيل التحديث. تحقق من اتصالك بالإنترنت وحاول مرة أخرى.",
                    messageEn = "Unable to download update. Check your connection and retry.",
                    info = info
                )
            }
        }
    }

    fun installDownloadedUpdate(
        callerContext: android.content.Context,
        readyState: com.example.update.AppUpdateUiState.ReadyToInstall
    ) {
        if (!appUpdateManager.canInstallPackages()) {
            _appUpdateState.value = com.example.update.AppUpdateUiState.NeedsInstallPermission(
                info = readyState.info,
                apkFile = readyState.apkFile
            )
            return
        }
        appUpdateManager.triggerSystemApkInstaller(callerContext, readyState.apkFile)
    }

    fun requestInstallPermission(callerContext: android.content.Context) {
        appUpdateManager.launchInstallPermissionSettings(callerContext)
    }

    fun resumePendingInstallIfPermitted(callerContext: android.content.Context) {
        val current = _appUpdateState.value
        if (current is com.example.update.AppUpdateUiState.NeedsInstallPermission &&
            appUpdateManager.canInstallPackages()
        ) {
            val ready = com.example.update.AppUpdateUiState.ReadyToInstall(current.info, current.apkFile)
            _appUpdateState.value = ready
            appUpdateManager.triggerSystemApkInstaller(callerContext, current.apkFile)
        }
    }

    fun dismissUpdateDialog() {
        _appUpdateState.value = com.example.update.AppUpdateUiState.Hidden
    }
}
