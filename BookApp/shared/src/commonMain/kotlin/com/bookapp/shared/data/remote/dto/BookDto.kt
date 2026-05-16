package com.bookapp.shared.data.remote.dto

import com.bookapp.shared.domain.model.Book
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookDto(
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("pageCount") val pageCount: Int,
    @SerialName("excerpt") val excerpt: String,
    @SerialName("publishDate") val publishDate: String
)

fun BookDto.toDomain(): Book = Book(
    id = id,
    title = title,
    description = description,
    pageCount = pageCount,
    excerpt = excerpt,
    publishDate = publishDate
)

fun Book.toDto(): BookDto = BookDto(
    id = id,
    title = title,
    description = description,
    pageCount = pageCount,
    excerpt = excerpt,
    publishDate = publishDate
)
