package com.bookapp.shared.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

expect fun createDataStore(): DataStore<Preferences>

internal const val DATA_STORE_FILE_NAME = "book_app_prefs.preferences_pb"
