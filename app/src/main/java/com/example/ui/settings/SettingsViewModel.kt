package com.example.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    val searchEngine: StateFlow<String> = repository.searchEngineFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Google"
    )

    val adBlockEnabled: StateFlow<Boolean> = repository.adBlockFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val customDnsEnabled: StateFlow<Boolean> = repository.customDnsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )
    
    val videoToolsEnabled: StateFlow<Boolean> = repository.videoToolsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    fun setSearchEngine(engine: String) {
        viewModelScope.launch {
            repository.saveSearchEngine(engine)
        }
    }

    fun setAdBlockEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveAdBlock(enabled)
        }
    }

    fun setCustomDnsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveCustomDns(enabled)
        }
    }
    
    fun setVideoToolsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveVideoTools(enabled)
        }
    }
}

class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(SettingsRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
