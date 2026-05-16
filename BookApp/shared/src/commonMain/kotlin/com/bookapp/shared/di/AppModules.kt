package com.bookapp.shared.di

import com.bookapp.shared.data.local.BookLocalDataSource
import com.bookapp.shared.data.local.DatabaseDriverFactory
import com.bookapp.shared.data.local.db.BookDatabase
import com.bookapp.shared.data.preferences.AppPreferences
import com.bookapp.shared.data.preferences.createDataStore
import com.bookapp.shared.data.remote.BookApiService
import com.bookapp.shared.data.remote.createHttpClient
import com.bookapp.shared.data.repository.BookRepositoryImpl
import com.bookapp.shared.domain.repository.BookRepository
import com.bookapp.shared.domain.usecase.AddBookUseCase
import com.bookapp.shared.domain.usecase.DeleteBookUseCase
import com.bookapp.shared.domain.usecase.GetBookByIdUseCase
import com.bookapp.shared.domain.usecase.GetBooksUseCase
import com.bookapp.shared.domain.usecase.RefreshBooksUseCase
import com.bookapp.shared.presentation.addbook.AddBookViewModel
import com.bookapp.shared.presentation.bookdetail.BookDetailViewModel
import com.bookapp.shared.presentation.booklist.BookListViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    single { createHttpClient() }
    single { BookApiService(get()) }
}

val databaseModule = module {
    single { get<DatabaseDriverFactory>().createDriver() }
    single { BookDatabase(get()) }
    single { BookLocalDataSource(get()) }
}

val preferencesModule = module {
    single { createDataStore() }
    single { AppPreferences(get()) }
}

val repositoryModule = module {
    singleOf(::BookRepositoryImpl) bind BookRepository::class
}

val useCaseModule = module {
    factoryOf(::GetBooksUseCase)
    factoryOf(::GetBookByIdUseCase)
    factoryOf(::AddBookUseCase)
    factoryOf(::DeleteBookUseCase)
    factoryOf(::RefreshBooksUseCase)
}

val viewModelModule = module {
    factoryOf(::BookListViewModel)
    factoryOf(::AddBookViewModel)
    factoryOf(::BookDetailViewModel)
}

val sharedModules = listOf(
    networkModule,
    databaseModule,
    preferencesModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)
