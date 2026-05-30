package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.ui.screens.BabuuuuDashboardScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.BabuuuuViewModel
import com.example.ui.viewmodel.BabuuuuViewModelFactory

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    // Initialize our database-driven Babuuuu ViewModel with custom factory
    val viewModel = ViewModelProvider(
      this, 
      BabuuuuViewModelFactory(application)
    )[BabuuuuViewModel::class.java]

    setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = androidx.compose.ui.graphics.Color(0xFF060913) // Obsidian space BG
        ) {
          BabuuuuDashboardScreen(
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize()
          )
        }
      }
    }
  }
}

