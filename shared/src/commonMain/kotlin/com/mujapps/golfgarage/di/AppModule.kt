package com.mujapps.golfgarage.di

import com.mujapps.data.di.dataModule
import com.mujapps.domain.di.domainModule
import com.mujapps.presentation.di.presentationModule

val appModule = listOf(
    dataModule,
    domainModule,
    presentationModule
)   