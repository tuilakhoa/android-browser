package com.example.ui.browser

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.SettingsRepository
import com.example.domain.getSearchUrl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BrowserViewModel(private val repository: SettingsRepository) : ViewModel() {

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
    
    val currentUrl = MutableStateFlow("https://www.google.com")
    val currentTitle = MutableStateFlow("Super Browser")
    val isLoading = MutableStateFlow(false)
    val loadProgress = MutableStateFlow(0f)

    fun loadUrl(queryOrUrl: String) {
        if (queryOrUrl.isBlank()) {
            val homeUrl = when(searchEngine.value) {
                "Google" -> "https://www.google.com"
                "Bing" -> "https://www.bing.com"
                "DuckDuckGo" -> "https://duckduckgo.com"
                else -> "https://www.google.com"
            }
            currentUrl.value = homeUrl
            return
        }
    
        val url = if (queryOrUrl.startsWith("http://") || queryOrUrl.startsWith("https://")) {
            queryOrUrl
        } else if (queryOrUrl.contains(".") && !queryOrUrl.contains(" ")) {
            "https://$queryOrUrl"
        } else {
            getSearchUrl(searchEngine.value, queryOrUrl)
        }
        currentUrl.value = url
    }
}

class BrowserViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BrowserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BrowserViewModel(SettingsRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
