package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "browser_settings")

class SettingsRepository(private val context: Context) {
    companion object {
        val SEARCH_ENGINE = stringPreferencesKey("search_engine")
        val AD_BLOCK = booleanPreferencesKey("ad_block")
        val CUSTOM_DNS = booleanPreferencesKey("custom_dns")
        val VIDEO_TOOLS = booleanPreferencesKey("video_tools")
    }

    val searchEngineFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[SEARCH_ENGINE] ?: "Google"
        }

    val adBlockFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[AD_BLOCK] ?: true
        }

    val customDnsFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[CUSTOM_DNS] ?: false
        }
        
    val videoToolsFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[VIDEO_TOOLS] ?: true
        }

    suspend fun saveSearchEngine(engine: String) {
        context.dataStore.edit { preferences ->
            preferences[SEARCH_ENGINE] = engine
        }
    }

    suspend fun saveAdBlock(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AD_BLOCK] = enabled
        }
    }

    suspend fun saveCustomDns(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[CUSTOM_DNS] = enabled
        }
    }
    
    suspend fun saveVideoTools(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[VIDEO_TOOLS] = enabled
        }
    }
}
