package com.bookapp.shared.data.remote

import com.bookapp.shared.data.remote.dto.BookDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class BookApiService(private val client: HttpClient) {

    companion object {
        private const val BASE_URL = "https://fakerestapi.azurewebsites.net/api/v1"
        private const val BOOKS_ENDPOINT = "$BASE_URL/Books"
    }

    suspend fun getBooks(): List<BookDto> {
        return client.get(BOOKS_ENDPOINT) {
            header(HttpHeaders.Accept, "text/plain; v=1.0")
        }.body()
    }

    suspend fun getBookById(id: Int): BookDto {
        return client.get("$BOOKS_ENDPOINT/$id") {
            header(HttpHeaders.Accept, "text/plain; v=1.0")
        }.body()
    }

    suspend fun addBook(book: BookDto): BookDto {
        return client.post(BOOKS_ENDPOINT) {
            contentType(ContentType.Application.Json)
            setBody(book)
        }.body()
    }

    suspend fun deleteBook(id: Int) {
        client.delete("$BOOKS_ENDPOINT/$id")
    }
}
