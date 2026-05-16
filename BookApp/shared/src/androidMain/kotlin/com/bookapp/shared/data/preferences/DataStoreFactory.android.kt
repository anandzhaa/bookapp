package com.bookapp.shared.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = DATA_STORE_FILE_NAME
)

// Initialized by the DI module on Android
lateinit var appContext: Context

actual fun createDataStore(): DataStore<Preferences> = appContext.dataStore
