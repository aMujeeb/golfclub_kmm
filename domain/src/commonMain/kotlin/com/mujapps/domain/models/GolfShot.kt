package com.mujapps.domain.models

import kotlin.time.Instant

data class GolfShot(
    val mId: String,
    val mPlayerId: String,
    val mClubName: String,
    val mBallSpeed: Double,
    val mLaunchAngle: Double,
    val mCarryDistance: Double,
    val mSpinRate: Double,
    val mTimestamp: Instant
)
