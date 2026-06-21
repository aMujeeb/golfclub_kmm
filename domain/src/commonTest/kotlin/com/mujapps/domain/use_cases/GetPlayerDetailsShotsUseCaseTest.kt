package com.mujapps.domain.use_cases

import app.cash.turbine.test
import com.mujapps.domain.models.GolfPlayer
import com.mujapps.domain.models.PlayerWithShots
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class GetPlayerDetailsShotsUseCaseTest {

    private val mockRepository = MockGolfRepository()
    private val useCase = GetPlayerDetailsShotsUseCase(mockRepository)

    private val testPlayer = GolfPlayer(
        mId = "player_123",
        mName = "John Doe",
        mProfPicUrl = "https://example.com/pic.png",
        mPreferenceClub = "Driver",
        mAverageBallSpeed = 165.4,
        mAverageDistance = 285.2
    )

    private val testPlayerWithShots = PlayerWithShots(
        mPlayer = testPlayer,
        mShots = emptyList()
    )

    @Test
    fun observePlayerShots_emitsCorrectData() = runTest {
        mockRepository.playerShotsResultFlow = flowOf(testPlayerWithShots)

        useCase.observePlayerShots("player_123").test {
            val item = awaitItem()
            assertEquals(testPlayerWithShots, item)
            awaitComplete()
        }
    }

    @Test
    fun observePlayerShots_emitsNullWhenNotFound() = runTest {
        mockRepository.playerShotsResultFlow = flowOf(null)

        useCase.observePlayerShots("player_123").test {
            val item = awaitItem()
            assertEquals(null, item)
            awaitComplete()
        }
    }

    @Test
    fun syncPlayerShots_callsRepositoryCorrectly() = runTest {
        useCase.syncPlayerShots("player_123")

        assertTrue(mockRepository.syncPlayerShotsCalled)
        assertEquals("player_123", mockRepository.lastSyncedPlayerId)
    }

    @Test
    fun syncPlayerShots_propagatesExceptions() = runTest {
        val expectedException = RuntimeException("Sync failed")
        mockRepository.syncPlayerShotsError = expectedException

        assertFailsWith<RuntimeException> {
            useCase.syncPlayerShots("player_123")
        }
    }
}
