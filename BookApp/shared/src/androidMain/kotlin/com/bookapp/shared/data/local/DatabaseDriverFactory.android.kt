package com.bookapp.shared.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.bookapp.shared.data.local.db.BookDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(BookDatabase.Schema, context, "book_app.db")
    }
}
