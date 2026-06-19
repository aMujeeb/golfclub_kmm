package com.mujapps.domain.di

import com.mujapps.domain.repositories.IGolferRepository
import com.mujapps.domain.use_cases.GetAllPlayersUseCase
import com.mujapps.domain.use_cases.GetPlayerDetailsShotsUseCase
import org.koin.dsl.module

val domainModule = module {
    factory {
        GetAllPlayersUseCase(get<IGolferRepository>())
    }

    factory {
        GetPlayerDetailsShotsUseCase(get<IGolferRepository>())
    }
}

