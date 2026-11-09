package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Data Pulse", appName)
  }

  @Test
  fun `verify 30-level pulse engine tier mapping and tempo acceleration`() {
    val state0 = com.example.ui.components.PulseEngineCalculator.calculate(0f)
    assertEquals(1, state0.tier)
    assertEquals(2000, state0.tempoDurationMs)

    val state50 = com.example.ui.components.PulseEngineCalculator.calculate(50f)
    assertEquals(16, state50.tier)
    org.junit.Assert.assertTrue(state50.tempoDurationMs in 1150..1300)

    val state100 = com.example.ui.components.PulseEngineCalculator.calculate(100f)
    assertEquals(30, state100.tier)
    assertEquals(450, state100.tempoDurationMs)

    // Verify all 30 tiers are mapped without regression
    for (i in 0..100) {
      val state = com.example.ui.components.PulseEngineCalculator.calculate(i.toFloat())
      org.junit.Assert.assertTrue(state.tier in 1..30)
      org.junit.Assert.assertTrue(state.tempoDurationMs in 450..2000)
    }
  }
}
