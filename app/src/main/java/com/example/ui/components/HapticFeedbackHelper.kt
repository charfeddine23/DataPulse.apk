package com.example.ui.components

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView

/**
 * Premium haptic feedback controller providing subtle, crisp tactile responses
 * for interactive controls such as SIM sliders and time-period toggles.
 */
class AppHaptics(
    private val view: View,
    private val composeHaptics: HapticFeedback
) {
    /**
     * Subtle, tactile tick/click for switching segments, tabs, or sliders.
     */
    fun performSegmentSwitch() {
        var performed = false
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                performed = view.performHapticFeedback(HapticFeedbackConstants.SEGMENT_TICK)
            }
            if (!performed) {
                performed = view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            }
        } catch (_: Throwable) {}

        if (!performed) {
            try {
                composeHaptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            } catch (_: Throwable) {}
        }
    }

    /**
     * Crisp confirmation tick for completed actions or snap releases.
     */
    fun performConfirm() {
        var performed = false
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                performed = view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
            }
            if (!performed) {
                performed = view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            }
        } catch (_: Throwable) {}

        if (!performed) {
            try {
                composeHaptics.performHapticFeedback(HapticFeedbackType.LongPress)
            } catch (_: Throwable) {}
        }
    }
}

@Composable
fun rememberAppHaptics(): AppHaptics {
    val view = LocalView.current
    val composeHaptics = LocalHapticFeedback.current
    return remember(view, composeHaptics) {
        AppHaptics(view, composeHaptics)
    }
}
