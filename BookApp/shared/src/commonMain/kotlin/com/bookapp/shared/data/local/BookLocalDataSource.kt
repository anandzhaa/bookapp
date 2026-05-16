package com.bookapp.shared.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.bookapp.shared.data.local.db.BookDatabase
import com.bookapp.shared.data.local.db.BookEntity
import com.bookapp.shared.domain.model.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class BookLocalDataSource(database: BookDatabase) {

    private val queries = database.bookEntityQueries

    fun getAllBooks(): Flow<List<Book>> {
        return queries.getAllBooks()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toBook() } }
    }

    suspend fun getBookById(id: Int): Book? = withContext(Dispatchers.IO) {
        queries.getBookById(id.toLong()).executeAsOneOrNull()?.toBook()
    }

    suspend fun insertBook(book: Book) = withContext(Dispatchers.IO) {
        queries.insertBook(
            id = book.id.toLong(),
            title = book.title,
            description = book.description,
            page_count = book.pageCount.toLong(),
            excerpt = book.excerpt,
            publish_date = book.publishDate,
            created_at = getCurrentTimeMillis()
        )
    }

    suspend fun insertBooks(books: List<Book>) = withContext(Dispatchers.IO) {
        queries.transaction {
            books.forEach { book ->
                queries.insertBook(
                    id = book.id.toLong(),
                    title = book.title,
                    description = book.description,
                    page_count = book.pageCount.toLong(),
                    excerpt = book.excerpt,
                    publish_date = book.publishDate,
                    created_at = getCurrentTimeMillis()
                )
            }
        }
    }

    suspend fun deleteBook(id: Int) = withContext(Dispatchers.IO) {
        queries.deleteBook(id.toLong())
    }

    suspend fun deleteAllBooks() = withContext(Dispatchers.IO) {
        queries.deleteAllBooks()
    }

    private fun BookEntity.toBook(): Book = Book(
        id = id.toInt(),
        title = title,
        description = description,
        pageCount = page_count.toInt(),
        excerpt = excerpt,
        publishDate = publish_date
    )

    private fun getCurrentTimeMillis(): Long {
        return kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
    }
}
