package com.mujapps.golfgarage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.mujapps.golfgarage.ui.theme.GolfGarageTheme
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

@Composable
@Preview
fun App() {
    remember {
        Napier.base(DebugAntilog())
    }

    var darkTheme by remember { mutableStateOf(false) }

    GolfGarageTheme(darkTheme = darkTheme) {

    }
}