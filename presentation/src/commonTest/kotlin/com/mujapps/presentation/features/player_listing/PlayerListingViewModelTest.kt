package com.mujapps.presentation.features.player_listing

import androidx.paging.PagingData
import app.cash.turbine.test
import com.mujapps.domain.models.GolfPlayer
import com.mujapps.domain.use_cases.GetAllPlayersUseCase
import com.mujapps.presentation.features.MockGolfRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerListingViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val mockRepository = MockGolfRepository()
    private val useCase = GetAllPlayersUseCase(mockRepository)
    private lateinit var viewModel: PlayerListingViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun playersPagingFlow_emitsRepositoryData() = runTest {
        val testPlayer = GolfPlayer(
            mId = "123",
            mName = "Test",
            mProfPicUrl = "",
            mPreferenceClub = "",
            mAverageBallSpeed = 0.0,
            mAverageDistance = 0.0
        )
        val mockPagingData = PagingData.from(listOf(testPlayer))
        mockRepository.playersPagingFlowResult = flowOf(mockPagingData)

        viewModel = PlayerListingViewModel(useCase)

        viewModel.mPlayersPagingFlow.test {
            val item = awaitItem()
            assertNotNull(item)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onSearchQueryChanged_updatesPagingFlow() = runTest {
        val testPlayer = GolfPlayer(
            mId = "123",
            mName = "Test",
            mProfPicUrl = "",
            mPreferenceClub = "",
            mAverageBallSpeed = 0.0,
            mAverageDistance = 0.0
        )
        val mockPagingData = PagingData.from(listOf(testPlayer))
        mockRepository.playersPagingFlowResult = flowOf(mockPagingData)

        viewModel = PlayerListingViewModel(useCase)
        viewModel.onSearchQueryChanged("New Query")

        viewModel.mPlayersPagingFlow.test {
            val item = awaitItem()
            assertNotNull(item)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onClubFilterToggled_updatesPagingFlow() = runTest {
        val testPlayer = GolfPlayer(
            mId = "123",
            mName = "Test",
            mProfPicUrl = "",
            mPreferenceClub = "Driver",
            mAverageBallSpeed = 0.0,
            mAverageDistance = 0.0
        )
        val mockPagingData = PagingData.from(listOf(testPlayer))
        mockRepository.playersPagingFlowResult = flowOf(mockPagingData)

        viewModel = PlayerListingViewModel(useCase)
        viewModel.onClubFilterToggled("Driver")

        viewModel.mPlayersPagingFlow.test {
            val item = awaitItem()
            assertNotNull(item)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
