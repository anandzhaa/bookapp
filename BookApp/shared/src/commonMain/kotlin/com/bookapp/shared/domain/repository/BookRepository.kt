package com.bookapp.shared.domain.repository

import com.bookapp.shared.domain.model.Book
import com.bookapp.shared.domain.model.Resource
import kotlinx.coroutines.flow.Flow

interface BookRepository {

    /** Emits cached books immediately, then syncs from network. */
    fun getBooks(): Flow<Resource<List<Book>>>

    suspend fun getBookById(id: Int): Resource<Book>

    suspend fun addBook(book: Book): Resource<Book>

    suspend fun deleteBook(id: Int): Resource<Unit>

    /** Triggers a fresh network fetch and updates the local cache. */
    suspend fun refreshBooks(): Resource<Unit>
}
