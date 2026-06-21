package com.mujapps.presentation.features.player_shot_details

import app.cash.turbine.test
import com.mujapps.domain.models.GolfPlayer
import com.mujapps.domain.models.GolfShot
import com.mujapps.domain.models.PlayerWithShots
import com.mujapps.domain.use_cases.GetPlayerDetailsShotsUseCase
import com.mujapps.presentation.features.MockGolfRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import kotlin.test.*
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerShotDetailsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val mockRepository = MockGolfRepository()
    private val useCase = GetPlayerDetailsShotsUseCase(mockRepository)
    private lateinit var viewModel: PlayerShotDetailsViewModel

    private val testPlayer = GolfPlayer(
        mId = "player_123",
        mName = "John Doe",
        mProfPicUrl = "https://example.com/pic.png",
        mPreferenceClub = "Driver",
        mAverageBallSpeed = 165.4,
        mAverageDistance = 285.2
    )

    private fun shot(id: String, club: String, timestamp: String) = GolfShot(
        mId = id,
        mPlayerId = "player_123",
        mClubName = club,
        mBallSpeed = 150.0,
        mLaunchAngle = 18.5,
        mCarryDistance = 240.0,
        mSpinRate = 2650.0,
        mTimestamp = Instant.parse(timestamp)
    )

    private val shot1 = shot("shot_1", "Driver", "2026-06-21T10:00:00Z")
    private val shot2 = shot("shot_2", "Iron", "2026-06-21T11:00:00Z")
    private val shot3 = shot("shot_3", "Driver", "2026-06-21T12:00:00Z")

    private val testPlayerWithShots = PlayerWithShots(
        mPlayer = testPlayer,
        mShots = listOf(shot1, shot2, shot3)
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
    fun shotsUiState_emitsLoadingInitially_whenPlayerDetailNotYetEmitted() = runTest {
        mockRepository.playerShotsResultFlow = MutableSharedFlow()
        viewModel = PlayerShotDetailsViewModel(useCase, "player_123")

        viewModel.mShotsUiState.test {
            val initialItem = awaitItem()
            assertTrue(initialItem.mIsLoading)
            assertEquals("", initialItem.mPlayerName)
            assertTrue(initialItem.mFilteredShots.isEmpty())
        }
    }

    @Test
    fun shotsUiState_combinesDbData_numbersChronologicallyAndSortsDescending() = runTest {
        mockRepository.playerShotsResultFlow = flowOf(testPlayerWithShots)
        viewModel = PlayerShotDetailsViewModel(useCase, "player_123")

        viewModel.mShotsUiState.test {
            val item = awaitItem()
            assertFalse(item.mIsLoading)
            assertEquals("John Doe", item.mPlayerName)
            assertEquals(listOf(shot1, shot2, shot3), item.mOriginalShots)

            // Newest shot first, but numbered by ascending chronological order (oldest = 1)
            assertEquals(listOf(shot3, shot2, shot1), item.mFilteredShots.map { it.shot })
            assertEquals(listOf(3, 2, 1), item.mFilteredShots.map { it.number })
        }
    }

    @Test
    fun onClubSelected_filtersShotsByClubName_caseInsensitive() = runTest {
        mockRepository.playerShotsResultFlow = flowOf(testPlayerWithShots)
        viewModel = PlayerShotDetailsViewModel(useCase, "player_123")

        viewModel.mShotsUiState.test {
            awaitItem() // initial "All clubs" emission

            viewModel.onClubSelected("driver")

            val filtered = awaitItem()
            assertEquals(listOf(shot3, shot1), filtered.mFilteredShots.map { it.shot })
            assertEquals(listOf(3, 1), filtered.mFilteredShots.map { it.number })
        }
    }

    @Test
    fun onClubSelected_backToAllClubs_returnsFullSortedList() = runTest {
        mockRepository.playerShotsResultFlow = flowOf(testPlayerWithShots)
        viewModel = PlayerShotDetailsViewModel(useCase, "player_123")

        viewModel.mShotsUiState.test {
            awaitItem() // initial "All clubs" emission

            viewModel.onClubSelected("Iron")
            val filtered = awaitItem()
            assertEquals(listOf(shot2), filtered.mFilteredShots.map { it.shot })

            viewModel.onClubSelected("All clubs")
            val restored = awaitItem()
            assertEquals(listOf(shot3, shot2, shot1), restored.mFilteredShots.map { it.shot })
        }
    }
}
