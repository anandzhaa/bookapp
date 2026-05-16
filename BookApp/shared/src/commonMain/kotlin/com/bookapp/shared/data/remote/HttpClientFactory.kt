package com.bookapp.shared.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createHttpClient(): HttpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = false
        })
    }
    install(Logging) {
        logger = Logger.SIMPLE
        level = LogLevel.INFO
    }
}
