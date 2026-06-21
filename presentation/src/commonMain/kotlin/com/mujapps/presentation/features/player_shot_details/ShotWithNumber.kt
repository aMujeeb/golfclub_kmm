package com.mujapps.presentation.features.player_shot_details

import com.mujapps.domain.models.GolfShot

data class ShotWithNumber(
    val shot: GolfShot,
    val number: Int
)
