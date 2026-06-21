package com.mujapps.data.mappers

import com.mujapps.data.local.entities.GolfShotEntity
import com.mujapps.data.local.entities.PlayerEntity
import com.mujapps.data.local.entities.PlayerWithShots as EntityPlayerWithShots
import com.mujapps.data.remote.models.GolfPlayerDto
import com.mujapps.data.remote.models.GolfShotDto
import com.mujapps.domain.models.GolfPlayer
import com.mujapps.domain.models.GolfShot
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class MappersTest {

    private val testPlayerDto = GolfPlayerDto(
        id = "player_123",
        name = "John Doe",
        profPicUrl = "https://example.com/pic.png",
        preferenceClub = "Driver",
        averageBallSpeed = 165.4,
        averageDistance = 285.2
    )

    private val testPlayerDomain = GolfPlayer(
        mId = "player_123",
        mName = "John Doe",
        mProfPicUrl = "https://example.com/pic.png",
        mPreferenceClub = "Driver",
        mAverageBallSpeed = 165.4,
        mAverageDistance = 285.2
    )

    private val testPlayerEntity = PlayerEntity(
        id = "player_123",
        name = "John Doe",
        profPicUrl = "https://example.com/pic.png",
        preferenceClub = "Driver",
        averageBallSpeed = 165.4,
        averageDistance = 285.2
    )

    private val testShotDto = GolfShotDto(
        id = "shot_1",
        playerId = "player_123",
        clubName = "Driver",
        ballSpeed = 180.0,
        launchAngle = 10.5,
        carryDistance = 300.0,
        spinRate = 2200.0,
        createdAt = "2026-06-21T10:00:00Z"
    )

    private val testShotDomain = GolfShot(
        mId = "shot_1",
        mPlayerId = "player_123",
        mClubName = "Driver",
        mBallSpeed = 180.0,
        mLaunchAngle = 10.5,
        mCarryDistance = 300.0,
        mSpinRate = 2200.0,
        mTimestamp = Instant.parse("2026-06-21T10:00:00Z")
    )

    private val testShotEntity = GolfShotEntity(
        id = "shot_1",
        playerId = "player_123",
        clubName = "Driver",
        ballSpeed = 180.0,
        launchAngle = 10.5,
        carryDistance = 300.0,
        spinRate = 2200.0,
        timestamp = Instant.parse("2026-06-21T10:00:00Z").toEpochMilliseconds()
    )

    // DtoMappers Tests

    @Test
    fun playerDto_toDomain_mapsCorrectly() {
        val result = testPlayerDto.toDomain()
        assertEquals(testPlayerDomain, result)
    }

    @Test
    fun playerDomain_toDto_mapsCorrectly() {
        val result = testPlayerDomain.toDto()
        assertEquals(testPlayerDto, result)
    }

    @Test
    fun shotDto_toDomain_mapsCorrectly() {
        val result = testShotDto.toDomain()
        assertEquals(testShotDomain, result)
    }

    @Test
    fun shotDomain_toDto_mapsCorrectly() {
        val result = testShotDomain.toDto()
        assertEquals(testShotDto, result)
    }

    // EntityMappers Tests

    @Test
    fun playerDto_toEntity_mapsCorrectly() {
        val result = testPlayerDto.toEntity()
        assertEquals(testPlayerEntity, result)
    }

    @Test
    fun shotDto_toEntity_mapsCorrectly() {
        val result = testShotDto.toEntity()
        assertEquals(testShotEntity, result)
    }

    @Test
    fun playerEntity_toDomain_mapsCorrectly() {
        val result = testPlayerEntity.toDomain()
        assertEquals(testPlayerDomain, result)
    }

    @Test
    fun shotEntity_toDomain_mapsCorrectly() {
        val result = testShotEntity.toDomain()
        assertEquals(testShotDomain, result)
    }

    @Test
    fun playerWithShotsEntity_toDomain_mapsCorrectly() {
        val entityPlayerWithShots = EntityPlayerWithShots(
            player = testPlayerEntity,
            shots = listOf(testShotEntity)
        )
        val result = entityPlayerWithShots.toDomain()
        assertEquals(testPlayerDomain, result.mPlayer)
        assertEquals(1, result.mShots.size)
        assertEquals(testShotDomain, result.mShots[0])
    }
}
