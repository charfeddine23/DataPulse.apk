package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.AppThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Singleton DataStore instance for Theme Engine
val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "data_pulse_theme_prefs")

/**
 * Persists and observes the user's selected AppThemeMode asynchronously using Jetpack DataStore.
 */
class ThemePreferences(private val context: Context) {

    private object PreferencesKeys {
        val KEY_THEME_MODE = stringPreferencesKey("app_theme_mode")
    }

    /**
     * Reads the persisted AppThemeMode as a reactive Flow.
     * Defaults to LIQUID_GLASS_DEFAULT if unset or on error.
     */
    val themeModeFlow: Flow<AppThemeMode> = context.themeDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val themeString = preferences[PreferencesKeys.KEY_THEME_MODE]
            try {
                if (themeString != null) {
                    AppThemeMode.valueOf(themeString)
                } else {
                    AppThemeMode.LIQUID_GLASS_DEFAULT
                }
            } catch (_: IllegalArgumentException) {
                AppThemeMode.LIQUID_GLASS_DEFAULT
            }
        }

    /**
     * Asynchronously saves the selected theme mode to Preferences DataStore.
     */
    suspend fun setThemeMode(themeMode: AppThemeMode) {
        context.themeDataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_THEME_MODE] = themeMode.name
        }
    }
}
