package com.mujapps.data.remote

import com.mujapps.data.remote.models.GolfPlayerDto
import com.mujapps.data.remote.models.GolfShotDto
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RemoteDataSourceTest {

    private val jsonConfiguration = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
    }

    private fun createMockClient(
        status: HttpStatusCode,
        responseContent: String
    ): HttpClient {
        val mockEngine = MockEngine { request ->
            respond(
                content = responseContent,
                status = status,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        return HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(jsonConfiguration)
            }
        }
    }

    @Test
    fun getGolfPlayers_success_returnsPlayersList() = runTest {
        val mockJson = """
            [
                {
                    "id": "player_123",
                    "name": "John Doe",
                    "profPicUrl": "https://example.com/pic.png",
                    "preferenceClub": "Driver",
                    "averageBallSpeed": 165.4,
                    "averageDistance": 285.2
                }
            ]
        """.trimIndent()

        val client = createMockClient(HttpStatusCode.OK, mockJson)
        val dataSource = RemoteDataSource(client, "https://api.example.com")

        val result = dataSource.getGolfPlayers(page = 1, limit = 10)
        assertTrue(result.isSuccess)

        val players = result.getOrNull()
        assertEquals(1, players?.size)
        assertEquals("player_123", players?.first()?.id)
        assertEquals("John Doe", players?.first()?.name)
    }

    @Test
    fun getGolfPlayers_httpError_returnsFailure() = runTest {
        val client = createMockClient(HttpStatusCode.InternalServerError, "Error response body")
        val dataSource = RemoteDataSource(client, "https://api.example.com")

        val result = dataSource.getGolfPlayers(page = 1, limit = 10)
        assertTrue(result.isFailure)
    }

    @Test
    fun getGolfPlayerShots_success_returnsShotsList() = runTest {
        val mockJson = """
            [
                {
                    "id": "shot_1",
                    "playerId": "player_123",
                    "clubName": "Driver",
                    "ballSpeed": 180.0,
                    "launchAngle": 10.5,
                    "carryDistance": 300.0,
                    "spinRate": 2200.0,
                    "createdAt": "2026-06-21T10:00:00Z"
                }
            ]
        """.trimIndent()

        val client = createMockClient(HttpStatusCode.OK, mockJson)
        val dataSource = RemoteDataSource(client, "https://api.example.com")

        val result = dataSource.getGolfPlayerShots("player_123")
        assertTrue(result.isSuccess)

        val shots = result.getOrNull()
        assertEquals(1, shots?.size)
        assertEquals("shot_1", shots?.first()?.id)
        assertEquals(180.0, shots?.first()?.ballSpeed)
    }

    @Test
    fun getGolfPlayerShots_httpError_returnsFailure() = runTest {
        val client = createMockClient(HttpStatusCode.BadRequest, "Bad Request")
        val dataSource = RemoteDataSource(client, "https://api.example.com")

        val result = dataSource.getGolfPlayerShots("player_123")
        assertTrue(result.isFailure)
    }
}
