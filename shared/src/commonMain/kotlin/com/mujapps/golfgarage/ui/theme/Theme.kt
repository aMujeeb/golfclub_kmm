package com.mujapps.golfgarage.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import golfgarage.shared.generated.resources.Res
import golfgarage.shared.generated.resources.inter_bold
import golfgarage.shared.generated.resources.inter_medium
import golfgarage.shared.generated.resources.inter_regular
import golfgarage.shared.generated.resources.inter_semibold
import golfgarage.shared.generated.resources.roboto_mono_medium
import golfgarage.shared.generated.resources.roboto_mono_regular
import golfgarage.shared.generated.resources.roboto_mono_semibold
import org.jetbrains.compose.resources.Font

// Color Schemes
val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimary,
    secondary = LightSecondary,
    onSecondary = LightOnPrimary,
    tertiary = LightSecondary,
    onTertiary = LightOnPrimary,
    error = LightError,
    onError = LightOnPrimary,
    background = LightPageBg,
    onBackground = LightText,
    surface = LightCard,
    onSurface = LightText,
    surfaceVariant = LightBorder,
    onSurfaceVariant = LightText2,
)

val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkPrimary,
    secondary = DarkSecondary,
    onSecondary = DarkOnPrimary,
    tertiary = DarkSecondary,
    onTertiary = DarkOnPrimary,
    error = DarkError,
    onError = DarkOnPrimary,
    background = DarkPageBg,
    onBackground = DarkText,
    surface = DarkCard,
    onSurface = DarkText,
    surfaceVariant = DarkBorder,
    onSurfaceVariant = DarkText2,
)

// Typography
@Composable
private fun interFontFamily(): FontFamily = FontFamily(
    Font(Res.font.inter_regular, FontWeight.Normal),
    Font(Res.font.inter_medium, FontWeight.Medium),
    Font(Res.font.inter_semibold, FontWeight.SemiBold),
    Font(Res.font.inter_bold, FontWeight.Bold),
)

@Composable
private fun robotoMonoFontFamily(): FontFamily = FontFamily(
    Font(Res.font.roboto_mono_regular, FontWeight.Normal),
    Font(Res.font.roboto_mono_medium, FontWeight.Medium),
    Font(Res.font.roboto_mono_semibold, FontWeight.SemiBold),
)

@Composable
fun golfGarageTypography(): Typography {
    val inter = interFontFamily()
    val robotoMono = robotoMonoFontFamily()
    return Typography(
        displayLarge = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            lineHeight = 28.6.sp,
            letterSpacing = (-0.025).em
        ),
        headlineLarge = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 24.2.sp,
            letterSpacing = (-0.02).em
        ),
        headlineMedium = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            lineHeight = 20.4.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 20.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 21.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 19.5.sp
        ),
        labelLarge = TextStyle(
            fontFamily = robotoMono,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            lineHeight = 14.4.sp,
            letterSpacing = 0.08.em
        ),
        labelMedium = TextStyle(
            fontFamily = robotoMono,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            lineHeight = 13.2.sp
        ),
        labelSmall = TextStyle(
            fontFamily = robotoMono,
            fontWeight = FontWeight.Medium,
            fontSize = 9.sp,
            lineHeight = 9.sp,
            letterSpacing = 0.06.em
        ),
    )
}

@Composable
fun GolfGarageTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors
    val typography = golfGarageTypography()

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content
        )
    }
}
