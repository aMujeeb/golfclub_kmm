package com.mujapps.domain.models

data class PlayerWithShots(
    val mPlayer: GolfPlayer,
    val mShots: List<GolfShot>
)
