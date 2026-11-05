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

val Context.perAppDataStore: DataStore<Preferences> by preferencesDataStore(name = "data_pulse_per_app_prefs")

data class PerAppQuotaSettings(
    val packageName: String,
    val dailyLimitGB: Float = 3.0f,
    val alertEnabled: Boolean = true
)

/**
 * Persists and observes custom per-app daily quota limits and notification toggles
 * using dynamic keys per package name in Jetpack Preferences DataStore.
 */
class PerAppPreferences(private val context: Context) {

    private fun limitKey(packageName: String) =
        floatPreferencesKey("limit_gb_${packageName.replace('.', '_')}")

    private fun alertKey(packageName: String) =
        booleanPreferencesKey("alert_en_${packageName.replace('.', '_')}")

    /**
     * Observes settings for a specific package name as a Flow.
     * Defaults to defaultLimitGB (default 3.0f) and true.
     */
    fun getSettingsFlow(packageName: String, defaultLimitGB: Float = 3.0f): Flow<PerAppQuotaSettings> {
        return context.perAppDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val limit = preferences[limitKey(packageName)] ?: defaultLimitGB
                val alert = preferences[alertKey(packageName)] ?: true
                PerAppQuotaSettings(
                    packageName = packageName,
                    dailyLimitGB = limit,
                    alertEnabled = alert
                )
            }
    }

    /**
     * Reads current settings directly from Preferences snapshot.
     */
    suspend fun getSettingsSnapshot(packageName: String, defaultLimitGB: Float = 3.0f): PerAppQuotaSettings {
        var result = PerAppQuotaSettings(packageName, defaultLimitGB, true)
        context.perAppDataStore.edit { preferences ->
            val limit = preferences[limitKey(packageName)] ?: defaultLimitGB
            val alert = preferences[alertKey(packageName)] ?: true
            result = PerAppQuotaSettings(packageName, limit, alert)
        }
        return result
    }

    /**
     * Saves custom settings for a specific application.
     */
    suspend fun saveSettings(packageName: String, dailyLimitGB: Float, alertEnabled: Boolean) {
        context.perAppDataStore.edit { preferences ->
            preferences[limitKey(packageName)] = dailyLimitGB.coerceIn(0.1f, 50.0f)
            preferences[alertKey(packageName)] = alertEnabled
        }
    }
}
