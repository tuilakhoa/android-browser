package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.browser.BrowserScreen
import com.example.ui.browser.BrowserViewModel
import com.example.ui.browser.BrowserViewModelFactory
import com.example.ui.extensions.ExtensionsScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.settings.SettingsViewModel
import com.example.ui.settings.SettingsViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            val navController = rememberNavController()
            val browserViewModel: BrowserViewModel = viewModel(factory = BrowserViewModelFactory(applicationContext))
            val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(applicationContext))

            NavHost(navController = navController, startDestination = "browser") {
                composable("browser") {
                    BrowserScreen(
                        viewModel = browserViewModel,
                        onNavigateToSettings = { navController.navigate("settings") },
                        onNavigateToExtensions = { navController.navigate("extensions") }
                    )
                }
                composable("settings") {
                    SettingsScreen(
                        viewModel = settingsViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("extensions") {
                    ExtensionsScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
      }
    }
  }
}
