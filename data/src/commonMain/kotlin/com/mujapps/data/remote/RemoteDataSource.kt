package com.mujapps.data.remote

import com.mujapps.data.remote.models.GolfPlayerDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class RemoteDataSource(private val httpClient: HttpClient, private val baseUrl: String) {

    suspend fun getAllGolfPlayers(): Result<List<GolfPlayerDto>> {
        return try {
            val response = httpClient.get("$baseUrl/players")
            Result.success(response.body())
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    suspend fun getGolfPlayerDetailsWithShots(playerId: String): Result<GolfPlayerDto> {
        return try {
            val response = httpClient.get("$baseUrl/Shot?playerId=$playerId")
            Result.success(response.body())
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}