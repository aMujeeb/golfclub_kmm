package com.mujapps.presentation.di

import com.mujapps.domain.use_cases.GetAllPlayersUseCase
import com.mujapps.domain.use_cases.GetPlayerDetailsShotsUseCase
import com.mujapps.presentation.features.player_details.PlayerDetailsViewModel
import com.mujapps.presentation.features.player_listing.PlayerListingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel {
        PlayerListingViewModel(get<GetAllPlayersUseCase>())
    }

    viewModel { (playerId: String) ->
        PlayerDetailsViewModel(
            get<GetPlayerDetailsShotsUseCase>(),
            playerId
        )
    }
}