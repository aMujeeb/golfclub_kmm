package com.mujapps.domain.use_cases

import androidx.paging.PagingData
import app.cash.turbine.test
import com.mujapps.domain.models.GolfPlayer
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetAllPlayersUseCaseTest {

    private val mockRepository = MockGolfRepository()
    private val useCase = GetAllPlayersUseCase(mockRepository)

    private val testPlayer = GolfPlayer(
        mId = "player_123",
        mName = "John Doe",
        mProfPicUrl = "https://example.com/pic.png",
        mPreferenceClub = "Driver",
        mAverageBallSpeed = 165.4,
        mAverageDistance = 285.2
    )

    @Test
    fun executePaging_emitsCorrectData() = runTest {
        val mockPagingData = PagingData.from(listOf(testPlayer))
        mockRepository.playersPagingFlowResult = flowOf(mockPagingData)

        useCase.executePaging().test {
            val item = awaitItem()
            assertNotNull(item)
            // Note: Since PagingData is an opaque container, we can't easily introspect the list
            // directly without paging adapters/presenters, but verifying non-null emission is sufficient
            // for the domain use-case layer's delegation test.
            assertEquals(mockPagingData, item)
            awaitComplete()
        }
    }
}
