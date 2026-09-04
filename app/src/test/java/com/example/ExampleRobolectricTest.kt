package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
    assertEquals("Hydratation", appName)
  }

  @Test
  fun `test hydration viewmodel add water and reset`() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = HydrationViewModel(application)

    // Initial reset
    viewModel.reset()
    assertEquals(0, viewModel.uiState.value.currentMl)
    assertEquals(2000, viewModel.uiState.value.targetMl)
    assertFalse(viewModel.uiState.value.isGoalReached)

    // Add 250ml
    viewModel.addWater(250)
    assertEquals(250, viewModel.uiState.value.currentMl)
    assertEquals(1, viewModel.uiState.value.history.size)
    assertEquals(250, viewModel.uiState.value.history.first().amountMl)

    // Add another 1750ml to reach 2000ml (2L)
    viewModel.addWater(1750)
    assertEquals(2000, viewModel.uiState.value.currentMl)
    assertTrue(viewModel.uiState.value.isGoalReached)

    // Reset
    viewModel.reset()
    assertEquals(0, viewModel.uiState.value.currentMl)
    assertEquals(0, viewModel.uiState.value.history.size)
  }
}

