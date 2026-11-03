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
import com.example.model.QuotaStatus
import com.example.model.SimCardInfo
import com.example.model.TimeRange
import com.example.util.LiveSpeedMonitor
import com.example.util.SmartAlertNotifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NetworkStatsRepository(application)
    private val preferences = DataPlanPreferences(application)
    private val speedMonitor = LiveSpeedMonitor(application, viewModelScope)
    private val alertNotifier = SmartAlertNotifier(application)

    val speedSample: StateFlow<LiveSpeedSample> = speedMonitor.speedSample
    val speedHistory: StateFlow<List<Float>> = speedMonitor.speedHistory

    private val _usageSummary = MutableStateFlow(NetworkUsageSummary())
    val usageSummary: StateFlow<NetworkUsageSummary> = _usageSummary.asStateFlow()

    private val _rawAppUsageList = MutableStateFlow<List<AppUsageItem>>(emptyList())
    private val _isRefreshingApps = MutableStateFlow(false)
    val isRefreshingApps: StateFlow<Boolean> = _isRefreshingApps.asStateFlow()

    private val _selectedTimeRange = MutableStateFlow(TimeRange.MONTH)
    val selectedTimeRange: StateFlow<TimeRange> = _selectedTimeRange.asStateFlow()

    private val _selectedInterface = MutableStateFlow(InterfaceFilter.ALL)
    val selectedInterface: StateFlow<InterfaceFilter> = _selectedInterface.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _simCards = MutableStateFlow<List<SimCardInfo>>(emptyList())
    val simCards: StateFlow<List<SimCardInfo>> = _simCards.asStateFlow()

    private val _selectedSimIndex = MutableStateFlow(0)
    val selectedSimIndex: StateFlow<Int> = _selectedSimIndex.asStateFlow()

    private val _dataPlanQuota = MutableStateFlow(DataPlanQuota())
    val dataPlanQuota: StateFlow<DataPlanQuota> = _dataPlanQuota.asStateFlow()

    private val _hasUsagePermission = MutableStateFlow(repository.hasUsageStatsPermission())
    val hasUsagePermission: StateFlow<Boolean> = _hasUsagePermission.asStateFlow()

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

    init {
        loadSimCards()
        refreshAll()
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
            val summary = repository.fetchUsageSummary(preferences.billingCycleStartDay)
            _usageSummary.value = summary
            computeQuota(summary.mobileMonthBytes)
            loadAppUsage()
        }
    }

    fun setTimeRange(range: TimeRange) {
        _selectedTimeRange.value = range
        loadAppUsage()
    }

    fun setInterfaceFilter(filter: InterfaceFilter) {
        _selectedInterface.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSimIndex(index: Int) {
        _selectedSimIndex.value = index
    }

    private fun loadAppUsage() {
        viewModelScope.launch {
            _isRefreshingApps.value = true
            val apps = repository.fetchAppUsage(_selectedTimeRange.value, preferences.billingCycleStartDay)
            _rawAppUsageList.value = apps
            _isRefreshingApps.value = false
        }
    }

    private fun computeQuota(usedMobileBytes: Long) {
        val limitGB = preferences.planLimitGB
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

        _dataPlanQuota.value = DataPlanQuota(
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

        // Check if smart alerts should fire
        triggerAlertIfNeeded(status, usedMobileBytes, limitBytes, percent)
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
        preferences.planLimitGB = limitGB.coerceAtLeast(0.5f)
        computeQuota(_usageSummary.value.mobileMonthBytes)
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
}
