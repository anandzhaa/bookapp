package com.bookapp.shared.data.repository

import com.bookapp.shared.data.local.BookLocalDataSource
import com.bookapp.shared.data.remote.BookApiService
import com.bookapp.shared.data.remote.dto.toDomain
import com.bookapp.shared.data.remote.dto.toDto
import com.bookapp.shared.domain.model.Book
import com.bookapp.shared.domain.model.Resource
import com.bookapp.shared.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class BookRepositoryImpl(
    private val apiService: BookApiService,
    private val localDataSource: BookLocalDataSource
) : BookRepository {

    /**
     * Offline-first: emits cached data right away, then fetches from network
     * and updates the local cache so the UI refreshes automatically.
     */
    override fun getBooks(): Flow<Resource<List<Book>>> {
        return localDataSource.getAllBooks()
            .onStart {
                // Kick off a background sync without blocking the first emission
                runCatching {
                    val remote = apiService.getBooks().map { it.toDomain() }
                    localDataSource.insertBooks(remote)
                }
            }
            .map<List<Book>, Resource<List<Book>>> { Resource.Success(it) }
            .catch { e -> emit(Resource.Error(e.message ?: "Unknown error", e)) }
    }

    override suspend fun getBookById(id: Int): Resource<Book> {
        return runCatching {
            // Try local first
            localDataSource.getBookById(id)
                ?: apiService.getBookById(id).toDomain().also { localDataSource.insertBook(it) }
        }.fold(
            onSuccess = { Resource.Success(it) },
            onFailure = { Resource.Error(it.message ?: "Could not load book", it) }
        )
    }

    override suspend fun addBook(book: Book): Resource<Book> {
        return runCatching {
            val created = apiService.addBook(book.toDto()).toDomain()
            // The fake API returns id=0, so we store with a local negative id to avoid conflicts
            val stored = created.copy(id = if (created.id == 0) -System.generateId() else created.id)
            localDataSource.insertBook(stored)
            stored
        }.fold(
            onSuccess = { Resource.Success(it) },
            onFailure = { Resource.Error(it.message ?: "Could not add book", it) }
        )
    }

    override suspend fun deleteBook(id: Int): Resource<Unit> {
        return runCatching {
            if (id > 0) apiService.deleteBook(id)
            localDataSource.deleteBook(id)
        }.fold(
            onSuccess = { Resource.Success(Unit) },
            onFailure = { Resource.Error(it.message ?: "Could not delete book", it) }
        )
    }

    override suspend fun refreshBooks(): Resource<Unit> {
        return runCatching {
            val books = apiService.getBooks().map { it.toDomain() }
            localDataSource.deleteAllBooks()
            localDataSource.insertBooks(books)
        }.fold(
            onSuccess = { Resource.Success(Unit) },
            onFailure = { Resource.Error(it.message ?: "Refresh failed", it) }
        )
    }
}

/** Generates a simple unique negative id for optimistic local inserts. */
private object System {
    private var counter = 0
    fun generateId(): Int = ++counter
}
