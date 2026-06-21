package com.mujapps.data.remote

import com.mujapps.data.remote.models.GolfPlayerDto
import com.mujapps.data.remote.models.GolfShotDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.github.aakira.napier.Napier

class RemoteDataSource(private val httpClient: HttpClient, private val baseUrl: String) {

    suspend fun getGolfPlayers(page: Int, limit: Int, search: String? = null): Result<List<GolfPlayerDto>> {
        return try {
            val url = if (search.isNullOrBlank()) {
                "$baseUrl/players?page=$page&limit=$limit"
            } else {
                "$baseUrl/players?page=$page&limit=$limit&search=$search"
            }
            val response = httpClient.get(url)
            Result.success(response.body())
        } catch (ex: Exception) {
            Napier.e("Failed to fetch golf players for page=$page, limit=$limit, search=$search", ex)
            Result.failure(ex)
        }
    }

    suspend fun getGolfPlayerShots(playerId: String): Result<List<GolfShotDto>> {
        return try {
            val response = httpClient.get("$baseUrl/Shot?playerId=$playerId")
            Result.success(response.body())
        } catch (ex: Exception) {
            Napier.e("Failed to fetch golf player shots for playerId=$playerId", ex)
            Result.failure(ex)
        }
    }
}