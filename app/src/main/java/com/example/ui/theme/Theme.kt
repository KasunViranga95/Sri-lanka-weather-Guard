package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalIsDarkTheme = staticCompositionLocalOf { true }

private val DarkWeatherColorScheme = darkColorScheme(
    primary = Color(0xFF00E5FF),
    onPrimary = Color(0xFF00262C),
    primaryContainer = NavyElevated,
    onPrimaryContainer = CloudWhite,
    secondary = Color(0xFF1DE9B6),
    onSecondary = Color(0xFF00281F),
    secondaryContainer = Color(0xFF0E3338),
    onSecondaryContainer = CloudWhite,
    tertiary = WarningAmber,
    onTertiary = Color(0xFF2B1700),
    error = AlertRed,
    onError = Color.White,
    background = NavyAbyss,
    onBackground = CloudWhite,
    surface = NavySurface,
    onSurface = CloudWhite,
    surfaceVariant = NavyElevated,
    onSurfaceVariant = MistSilver,
    outline = NavyCardBorder
)

private val LightWeatherColorScheme = lightColorScheme(
    primary = Color(0xFF007799),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6F3FA),
    onPrimaryContainer = Color(0xFF002633),
    secondary = Color(0xFF00796B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD0F2EC),
    onSecondaryContainer = Color(0xFF00251E),
    tertiary = Color(0xFFE65100),
    onTertiary = Color.White,
    error = Color(0xFFD32F2F),
    onError = Color.White,
    background = LightSkyBackground,
    onBackground = DeepInk,
    surface = LightCardSurface,
    onSurface = DeepInk,
    surfaceVariant = LightElevatedSurface,
    onSurfaceVariant = SlateMuted,
    outline = LightCardBorder
)

@Composable
fun LankaWeatherGuardTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkWeatherColorScheme else LightWeatherColorScheme

    CompositionLocalProvider(LocalIsDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
