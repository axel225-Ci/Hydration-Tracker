package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MyApplicationTheme
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {

  private val hydrationViewModel: HydrationViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = DarkBackground
        ) {
          HydrationScreen(viewModel = hydrationViewModel)
        }
      }
    }
  }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HydrationScreenPreview() {
  MyApplicationTheme {
    Surface(
      modifier = Modifier.fillMaxSize(),
      color = DarkBackground
    ) {
      HydrationContent(
        uiState = HydrationUiState(
          currentMl = 750,
          targetMl = 2000,
          history = listOf()
        )
      )
    }
  }
}