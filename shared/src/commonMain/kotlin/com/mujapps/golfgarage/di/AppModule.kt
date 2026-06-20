package com.mujapps.golfgarage.di

import com.mujapps.data.di.dataModule
import com.mujapps.domain.di.domainModule
import com.mujapps.presentation.di.presentationModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

val appModule = listOf(
    dataModule,
    domainModule,
    presentationModule
)

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(appModule)
    }   