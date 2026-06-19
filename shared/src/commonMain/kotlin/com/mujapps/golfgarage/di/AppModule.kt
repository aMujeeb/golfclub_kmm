package com.mujapps.golfgarage.di

import com.mujapps.data.di.dataModule
import com.mujapps.domain.di.domainModule

val appModule = listOf(
    dataModule,
    domainModule
)   