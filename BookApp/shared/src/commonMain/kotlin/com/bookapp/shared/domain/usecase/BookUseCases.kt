package com.bookapp.shared.domain.usecase

import com.bookapp.shared.domain.model.Book
import com.bookapp.shared.domain.model.Resource
import com.bookapp.shared.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow

class GetBooksUseCase(private val repository: BookRepository) {
    operator fun invoke(): Flow<Resource<List<Book>>> = repository.getBooks()
}

class GetBookByIdUseCase(private val repository: BookRepository) {
    suspend operator fun invoke(id: Int): Resource<Book> = repository.getBookById(id)
}

class AddBookUseCase(private val repository: BookRepository) {
    suspend operator fun invoke(
        title: String,
        description: String,
        pageCount: Int,
        excerpt: String,
        publishDate: String
    ): Resource<Book> {
        if (title.isBlank()) return Resource.Error("Title cannot be empty")
        if (pageCount <= 0) return Resource.Error("Page count must be greater than zero")

        val book = Book(
            id = 0,
            title = title.trim(),
            description = description.trim(),
            pageCount = pageCount,
            excerpt = excerpt.trim(),
            publishDate = publishDate
        )
        return repository.addBook(book)
    }
}

class DeleteBookUseCase(private val repository: BookRepository) {
    suspend operator fun invoke(id: Int): Resource<Unit> = repository.deleteBook(id)
}

class RefreshBooksUseCase(private val repository: BookRepository) {
    suspend operator fun invoke(): Resource<Unit> = repository.refreshBooks()
}
