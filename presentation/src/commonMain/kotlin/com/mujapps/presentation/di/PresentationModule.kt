package com.mujapps.presentation.di

import com.mujapps.domain.use_cases.GetAllPlayersUseCase
import com.mujapps.presentation.features.player_listing.PlayerListingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel {
        PlayerListingViewModel(get<GetAllPlayersUseCase>())
    }
}