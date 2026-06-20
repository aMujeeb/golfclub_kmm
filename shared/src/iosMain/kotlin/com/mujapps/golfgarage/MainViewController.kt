package com.mujapps.golfgarage

import androidx.compose.ui.window.ComposeUIViewController
import com.mujapps.golfgarage.di.initKoin

fun MainViewController() = ComposeUIViewController { App() }

class IOSKoinInitializer {
    fun initialize() {
        initKoin()
    }
}