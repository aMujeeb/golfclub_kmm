package com.mujapps.presentation.features.player_details

import app.cash.turbine.test
import com.mujapps.domain.models.GolfPlayer
import com.mujapps.domain.models.PlayerWithShots
import com.mujapps.domain.use_cases.GetPlayerDetailsShotsUseCase
import com.mujapps.presentation.features.MockGolfRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerDetailsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val mockRepository = MockGolfRepository()
    private val useCase = GetPlayerDetailsShotsUseCase(mockRepository)
    private lateinit var viewModel: PlayerDetailsViewModel

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

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun detailsUiState_emitsInitialStateAndCombinesDbData() = runTest {
        val dbFlow = MutableSharedFlow<PlayerWithShots?>()
        mockRepository.playerShotsResultFlow = dbFlow

        viewModel = PlayerDetailsViewModel(useCase, "player_123")

        viewModel.mDetailsUiState.test {
            // Initial combined state: loading = true, playerDetail = null (from stateIn initialValue or first emission)
            val initialItem = awaitItem()
            assertTrue(initialItem.mIsLoading)
            assertNull(initialItem.mPlayerDetail)

            // Emit data from DB
            dbFlow.emit(testPlayerWithShots)

            val updatedItem = awaitItem()
            assertEquals(testPlayerWithShots, updatedItem.mPlayerDetail)
        }
    }

    @Test
    fun getUserDetails_success_updatesLoadingStates() = runTest {
        mockRepository.playerShotsResultFlow = flowOf(testPlayerWithShots)
        viewModel = PlayerDetailsViewModel(useCase, "player_123")

        viewModel.mDetailsUiState.test {
            // First item: combined flow starts. Since DB flow of testPlayerWithShots emits immediately,
            // we get the combined state from the initial _syncState (loading = false) and DB data.
            val initial = awaitItem()
            assertFalse(initial.mIsLoading)
            assertEquals(testPlayerWithShots, initial.mPlayerDetail)

            // Trigger sync
            viewModel.getUserDetails()

            // getUserDetails updates _syncState.mIsLoading to true
            val loadingState = awaitItem()
            assertTrue(loadingState.mIsLoading)

            // getUserDetails finally block updates _syncState.mIsLoading to false
            val successState = awaitItem()
            assertFalse(successState.mIsLoading)
            assertNull(successState.mErrorMessage)
            assertEquals(testPlayerWithShots, successState.mPlayerDetail)
            assertTrue(mockRepository.syncPlayerShotsCalled)
        }
    }

    @Test
    fun getUserDetails_failure_emitsErrorMessage() = runTest {
        mockRepository.playerShotsResultFlow = flowOf(testPlayerWithShots)
        mockRepository.syncPlayerShotsError = RuntimeException("Network Error")
        viewModel = PlayerDetailsViewModel(useCase, "player_123")

        viewModel.mDetailsUiState.test {
            // First item: loading = false, playerDetail = testPlayerWithShots
            val initial = awaitItem()
            assertFalse(initial.mIsLoading)

            // Trigger sync
            viewModel.getUserDetails()

            // 1. getUserDetails updates _syncState.mIsLoading to true
            val loadingState = awaitItem()
            assertTrue(loadingState.mIsLoading)
            assertNull(loadingState.mErrorMessage)

            // 2. getUserDetails fails, catches error, and sets mErrorMessage (mIsLoading is still true)
            val intermediateErrorState = awaitItem()
            assertTrue(intermediateErrorState.mIsLoading)
            assertNotNull(intermediateErrorState.mErrorMessage)
            assertTrue(intermediateErrorState.mErrorMessage.contains("Background sync failed: Network Error"))

            // 3. finally block runs and sets mIsLoading to false
            val finalErrorState = awaitItem()
            assertFalse(finalErrorState.mIsLoading)
            assertNotNull(finalErrorState.mErrorMessage)
            assertTrue(finalErrorState.mErrorMessage.contains("Background sync failed: Network Error"))
        }
    }
}
