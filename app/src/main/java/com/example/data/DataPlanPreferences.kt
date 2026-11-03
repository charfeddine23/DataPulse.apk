package com.example.data

import android.content.Context
import android.content.SharedPreferences

class DataPlanPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("data_pulse_prefs", Context.MODE_PRIVATE)

    var planLimitGB: Float
        get() = prefs.getFloat(KEY_PLAN_LIMIT_GB, 10.0f)
        set(value) = prefs.edit().putFloat(KEY_PLAN_LIMIT_GB, value).apply()

    var billingCycleStartDay: Int
        get() = prefs.getInt(KEY_BILLING_CYCLE_DAY, 1)
        set(value) = prefs.edit().putInt(KEY_BILLING_CYCLE_DAY, value.coerceIn(1, 31)).apply()

    var alert80Enabled: Boolean
        get() = prefs.getBoolean(KEY_ALERT_80, true)
        set(value) = prefs.edit().putBoolean(KEY_ALERT_80, value).apply()

    var alert95Enabled: Boolean
        get() = prefs.getBoolean(KEY_ALERT_95, true)
        set(value) = prefs.edit().putBoolean(KEY_ALERT_95, value).apply()

    var selectedSimSubId: Int
        get() = prefs.getInt(KEY_SELECTED_SIM, -1)
        set(value) = prefs.edit().putInt(KEY_SELECTED_SIM, value).apply()

    var lastNotifiedThreshold: Int
        get() = prefs.getInt(KEY_LAST_NOTIFIED_THRESHOLD, 0)
        set(value) = prefs.edit().putInt(KEY_LAST_NOTIFIED_THRESHOLD, value).apply()

    var themeMode: String
        get() = prefs.getString(KEY_THEME_MODE, "SYSTEM") ?: "SYSTEM"
        set(value) = prefs.edit().putString(KEY_THEME_MODE, value).apply()

    var powerSavingMode: Boolean
        get() = prefs.getBoolean(KEY_POWER_SAVING, false)
        set(value) = prefs.edit().putBoolean(KEY_POWER_SAVING, value).apply()

    companion object {
        private const val KEY_PLAN_LIMIT_GB = "plan_limit_gb"
        private const val KEY_BILLING_CYCLE_DAY = "billing_cycle_day"
        private const val KEY_ALERT_80 = "alert_80_enabled"
        private const val KEY_ALERT_95 = "alert_95_enabled"
        private const val KEY_SELECTED_SIM = "selected_sim_sub_id"
        private const val KEY_LAST_NOTIFIED_THRESHOLD = "last_notified_threshold"
        private const val KEY_THEME_MODE = "theme_mode_preference"
        private const val KEY_POWER_SAVING = "power_saving_mode"
    }
}
