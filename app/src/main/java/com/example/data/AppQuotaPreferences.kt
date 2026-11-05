package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Singleton DataStore instance for per-app custom quotas
val Context.appQuotaDataStore: DataStore<Preferences> by preferencesDataStore(name = "data_pulse_app_quotas")

/**
 * Data model representing custom per-app daily limit and alert configuration
 */
data class AppCustomLimitConfig(
    val packageName: String,
    val dailyLimitGB: Float = 3.0f,
    val alertEnabled: Boolean = true
)

/**
 * Manages per-app daily data limits and alerts persistently using Jetpack Preferences DataStore
 * with dynamic keys per package name.
 */
class AppQuotaPreferences(private val context: Context) {

    private fun limitKey(packageName: String) = floatPreferencesKey("app_limit_gb_${packageName.replace('.', '_')}")
    private fun alertKey(packageName: String) = booleanPreferencesKey("app_alert_enabled_${packageName.replace('.', '_')}")

    /**
     * Reads custom limit configuration for a specific package as a reactive Flow.
     * Defaults to 3.0 GB daily limit and alerts enabled.
     */
    fun getAppLimitConfigFlow(packageName: String): Flow<AppCustomLimitConfig> =
        context.appQuotaDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val limit = preferences[limitKey(packageName)] ?: 3.0f
                val alert = preferences[alertKey(packageName)] ?: true
                AppCustomLimitConfig(
                    packageName = packageName,
                    dailyLimitGB = limit,
                    alertEnabled = alert
                )
            }

    /**
     * Reads all customized app limits in a single reactive Flow map (packageName -> AppCustomLimitConfig).
     */
    val allCustomLimitsFlow: Flow<Map<String, AppCustomLimitConfig>> =
        context.appQuotaDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val result = mutableMapOf<String, AppCustomLimitConfig>()
                preferences.asMap().forEach { (prefKey, value) ->
                    val keyName = prefKey.name
                    if (keyName.startsWith("app_limit_gb_")) {
                        val sanitizedPkg = keyName.removePrefix("app_limit_gb_")
                        val limit = value as? Float ?: 3.0f
                        val alert = preferences[booleanPreferencesKey("app_alert_enabled_$sanitizedPkg")] ?: true
                        result[sanitizedPkg] = AppCustomLimitConfig(
                            packageName = sanitizedPkg,
                            dailyLimitGB = limit,
                            alertEnabled = alert
                        )
                    }
                }
                result
            }

    /**
     * Persistently saves customized daily limit and alert toggle for an application.
     */
    suspend fun saveAppCustomLimit(packageName: String, dailyLimitGB: Float, alertEnabled: Boolean) {
        val clampedLimit = dailyLimitGB.coerceIn(0.2f, 100.0f)
        context.appQuotaDataStore.edit { preferences ->
            preferences[limitKey(packageName)] = clampedLimit
            preferences[alertKey(packageName)] = alertEnabled
        }
    }
}
