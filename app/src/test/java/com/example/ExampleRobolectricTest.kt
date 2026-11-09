package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.FormatUtils
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
  fun `verify byte formatting utility`() {
    assertEquals("0 B", FormatUtils.formatBytes(0L))
    assertEquals("1.0 KB", FormatUtils.formatBytes(1024L))
    assertEquals("1.0 MB", FormatUtils.formatBytes(1024L * 1024L))
    assertEquals("1.00 GB", FormatUtils.formatBytes(1024L * 1024L * 1024L))
  }
}
