package com.mujapps.data.mappers

import com.mujapps.data.local.entities.GolfShotEntity
import com.mujapps.data.local.entities.PlayerEntity
import com.mujapps.data.remote.models.GolfPlayerDto
import com.mujapps.data.remote.models.GolfShotDto
import com.mujapps.domain.models.GolfPlayer
import com.mujapps.domain.models.GolfShot
import kotlin.time.Instant
import com.mujapps.data.local.entities.PlayerWithShots as EntityPlayerWithShots
import com.mujapps.domain.models.PlayerWithShots as DomainPlayerWithShots

fun GolfPlayerDto.toEntity(): PlayerEntity = PlayerEntity(
    id = id,
    name = name,
    profPicUrl = profPicUrl,
    preferenceClub = preferenceClub,
    averageBallSpeed = averageBallSpeed,
    averageDistance = averageDistance
)

fun GolfShotDto.toEntity(): GolfShotEntity = GolfShotEntity(
    id = id,
    playerId = playerId,
    clubName = clubName,
    ballSpeed = ballSpeed,
    launchAngle = launchAngle,
    carryDistance = carryDistance,
    spinRate = spinRate,
    timestamp = Instant.parse(createdAt).toEpochMilliseconds()
)
fun PlayerEntity.toDomain(): GolfPlayer = GolfPlayer(
    mId = id,
    mName = name,
    mProfPicUrl = profPicUrl,
    mPreferenceClub = preferenceClub,
    mAverageBallSpeed = averageBallSpeed,
    mAverageDistance = averageDistance
)

fun GolfShotEntity.toDomain(): GolfShot = GolfShot(
    mId = id,
    mPlayerId = playerId,
    mClubName = clubName,
    mBallSpeed = ballSpeed,
    mLaunchAngle = launchAngle,
    mCarryDistance = carryDistance,
    mSpinRate = spinRate,
    mTimestamp = Instant.fromEpochMilliseconds(timestamp)
)

fun EntityPlayerWithShots.toDomain(): DomainPlayerWithShots = DomainPlayerWithShots(
    mPlayer = player.toDomain(),
    mShots = shots.map { it.toDomain() }
)
