package com.mujapps.golfgarage.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColors(
    val success: Color,
    val warning: Color,
    val primarySoft: Color,
    val shadow: Color,
)

val LightExtendedColors = ExtendedColors(
    success = LightSuccess,
    warning = LightWarning,
    primarySoft = LightPrimarySoft,
    shadow = LightShadow,
)

val DarkExtendedColors = ExtendedColors(
    success = DarkSuccess,
    warning = DarkWarning,
    primarySoft = DarkPrimarySoft,
    shadow = DarkShadow,
)

val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

object ExtendedTheme {
    val colors: ExtendedColors
        @Composable get() = LocalExtendedColors.current
}
