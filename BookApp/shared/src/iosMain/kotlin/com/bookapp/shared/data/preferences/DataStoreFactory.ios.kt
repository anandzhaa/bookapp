package com.bookapp.shared.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual fun createDataStore(): DataStore<Preferences> {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )
    val path = requireNotNull(documentDirectory).path + "/$DATA_STORE_FILE_NAME"
    return preferencesDataStore(path).value
}
