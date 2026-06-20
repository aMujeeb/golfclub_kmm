package com.mujapps.data.mappers

import com.mujapps.data.remote.models.GolfPlayerDto
import com.mujapps.data.remote.models.GolfShotDto
import com.mujapps.domain.models.GolfPlayer
import com.mujapps.domain.models.GolfShot
import com.mujapps.domain.models.PlayerWithShots
import kotlin.time.Instant

fun GolfPlayerDto.toDomain(): GolfPlayer = GolfPlayer(
    mId = id,
    mName = name,
    mProfPicUrl = profPicUrl,
    mPreferenceClub = preferenceClub,
    mAverageBallSpeed = averageBallSpeed,
    mAverageDistance = averageDistance
)

fun GolfShotDto.toDomain(): GolfShot = GolfShot(
    mId = id,
    mPlayerId = playerId,
    mClubName = clubName,
    mBallSpeed = ballSpeed,
    mLaunchAngle = launchAngle,
    mCarryDistance = carryDistance,
    mSpinRate = spinRate,
    mTimestamp = Instant.parse(createdAt)
)

fun GolfPlayer.toDto(): GolfPlayerDto = GolfPlayerDto(
    id = mId,
    name = mName,
    profPicUrl = mProfPicUrl,
    preferenceClub = mPreferenceClub,
    averageBallSpeed = mAverageBallSpeed,
    averageDistance = mAverageDistance,
)

fun GolfShot.toDto(): GolfShotDto = GolfShotDto(
    id = mId,
    playerId = mPlayerId,
    clubName = mClubName,
    ballSpeed = mBallSpeed,
    launchAngle = mLaunchAngle,
    carryDistance = mCarryDistance,
    spinRate = mSpinRate,
    createdAt = mTimestamp.toString()
)