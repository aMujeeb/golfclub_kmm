package com.mujapps.domain.models

data class GolfPlayer(
    val mId: String,
    val mName: String,
    val mProfPicUrl: String,
    val mPreferenceClub: String,
    val mAverageBallSpeed: Double,
    val mAverageDistance: Double
)
