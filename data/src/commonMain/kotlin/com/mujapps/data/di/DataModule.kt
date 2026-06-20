package com.mujapps.data.di

import com.mujapps.data.local.database.GolfDatabase
import com.mujapps.data.remote.RemoteDataSource
import com.mujapps.data.repositories.GolfersRepository
import com.mujapps.domain.repositories.IGolferRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.bind
import org.koin.dsl.module
import io.github.aakira.napier.Napier


val dataModule = module {
    single<HttpClient> {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }

            install(Logging) {
                level = LogLevel.ALL //At production level this should be set to NONE
                logger = object : Logger {
                    override fun log(message: String) {
                        Napier.d(tag = "HttpClient", message = message)
                    }
                }
            }

            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }
    }

    single { get<GolfDatabase>().playerDao() }
    single { get<GolfDatabase>().shotDao() }

    single<RemoteDataSource> {
        RemoteDataSource(get(), "https://6a2e5885c9776ca6c0c4846c.mockapi.io/api/v1")
    }

    single {
        GolfersRepository(get(), get(), get())
    } bind IGolferRepository::class
}