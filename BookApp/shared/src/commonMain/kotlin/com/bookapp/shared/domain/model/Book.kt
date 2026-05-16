package com.bookapp.shared.domain.model

data class Book(
    val id: Int,
    val title: String,
    val description: String,
    val pageCount: Int,
    val excerpt: String,
    val publishDate: String
)
