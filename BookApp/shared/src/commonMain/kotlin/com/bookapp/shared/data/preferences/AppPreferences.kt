package com.bookapp.shared.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferences(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
        private val KEY_FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    }

    val isDarkMode: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_DARK_MODE] ?: false
    }

    val isFirstLaunch: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_FIRST_LAUNCH] ?: true
    }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { it[KEY_DARK_MODE] = enabled }
    }

    suspend fun markLaunched() {
        dataStore.edit { it[KEY_FIRST_LAUNCH] = false }
    }
}
