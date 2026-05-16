package com.bookapp

import android.app.Application
import com.bookapp.shared.data.local.DatabaseDriverFactory
import com.bookapp.shared.data.preferences.appContext
import com.bookapp.shared.di.sharedModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class BookApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this

        startKoin {
            androidContext(this@BookApplication)
            modules(
                sharedModules + module {
                    single { DatabaseDriverFactory(androidContext()) }
                }
            )
        }
    }
}
