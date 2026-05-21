package com.example.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.SearchEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val searchEngine by viewModel.searchEngine.collectAsStateWithLifecycle()
    val adBlockEnabled by viewModel.adBlockEnabled.collectAsStateWithLifecycle()
    val customDnsEnabled by viewModel.customDnsEnabled.collectAsStateWithLifecycle()
    val videoToolsEnabled by viewModel.videoToolsEnabled.collectAsStateWithLifecycle()

    var showSearchEngineDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                SettingsItem(
                    title = "Search Engine",
                    subtitle = searchEngine,
                    onClick = { showSearchEngineDialog = true }
                )
                HorizontalDivider()
            }
            item {
                SettingsSwitchItem(
                    title = "Ad Blocker",
                    subtitle = "Block intrusive ads and trackers",
                    checked = adBlockEnabled,
                    onCheckedChange = { viewModel.setAdBlockEnabled(it) }
                )
                HorizontalDivider()
            }
            item {
                SettingsSwitchItem(
                    title = "Secure DNS",
                    subtitle = "Use DNS over HTTPS for privacy",
                    checked = customDnsEnabled,
                    onCheckedChange = { viewModel.setCustomDnsEnabled(it) }
                )
                HorizontalDivider()
            }
            item {
                SettingsSwitchItem(
                    title = "Video Tools",
                    subtitle = "Enable PiP and background play features",
                    checked = videoToolsEnabled,
                    onCheckedChange = { viewModel.setVideoToolsEnabled(it) }
                )
                HorizontalDivider()
            }
        }
    }

    if (showSearchEngineDialog) {
        AlertDialog(
            onDismissRequest = { showSearchEngineDialog = false },
            title = { Text("Search Engine") },
            text = {
                Column {
                    SearchEngine.values().forEach { engine ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setSearchEngine(engine.title)
                                    showSearchEngineDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = engine.title == searchEngine,
                                onClick = {
                                    viewModel.setSearchEngine(engine.title)
                                    showSearchEngineDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = engine.title)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSearchEngineDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun SettingsItem(title: String, subtitle: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun SettingsSwitchItem(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
