package com.mujapps.data.remote

import com.mujapps.data.remote.models.GolfPlayerDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.github.aakira.napier.Napier

class RemoteDataSource(private val httpClient: HttpClient, private val baseUrl: String) {

    suspend fun getGolfPlayers(page: Int, limit: Int): Result<List<GolfPlayerDto>> {
        return try {
            val response = httpClient.get("$baseUrl/players?page=$page&limit=$limit")
            Result.success(response.body())
        } catch (ex: Exception) {
            Napier.e("Failed to fetch golf players for page=$page, limit=$limit", ex)
            Result.failure(ex)
        }
    }

    suspend fun getGolfPlayerDetailsWithShots(playerId: String): Result<GolfPlayerDto> {
        return try {
            val response = httpClient.get("$baseUrl/Shot?playerId=$playerId")
            Result.success(response.body())
        } catch (ex: Exception) {
            Napier.e("Failed to fetch golf player details with shots for playerId=$playerId", ex)
            Result.failure(ex)
        }
    }
}