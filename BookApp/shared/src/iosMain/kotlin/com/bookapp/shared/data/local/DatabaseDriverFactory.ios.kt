package com.bookapp.shared.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.bookapp.shared.data.local.db.BookDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(BookDatabase.Schema, "book_app.db")
    }
}
