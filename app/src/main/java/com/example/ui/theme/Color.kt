package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// Atmospheric Command Palette (Dark Mode)
val NavyAbyss = Color(0xFF060E1A)
val NavySurface = Color(0xFF0D1B2E)
val NavyElevated = Color(0xFF152740)
val NavyCardBorder = Color(0xFF203A5E)

// Daylight Meteorological Palette (Light Mode)
val LightSkyBackground = Color(0xFFF2F7FC)
val LightCardSurface = Color(0xFFFFFFFF)
val LightElevatedSurface = Color(0xFFE6F0FA)
val LightCardBorder = Color(0xFFC5D8EC)

// Primary Accents (High-contrast across Dark and Light themes)
val RadarCyan = Color(0xFF00B8D4)
val MonsoonTeal = Color(0xFF00897B)

// Alert & Hydrology Status Colors
val AlertRed = Color(0xFFE53935)
val WarningAmber = Color(0xFFF57C00)
val AdvisoryYellow = Color(0xFFFBC02D)
val SafeEmerald = Color(0xFF00A859)

// Typography & Neutral Tones
val CloudWhite = Color(0xFFF4F8FC)
val MistSilver = Color(0xFF9AB0C7)
val SlateMuted = Color(0xFF5C7490)
val DeepInk = Color(0xFF0A192F)

// Theme-aware adaptive color accessors for screens and components
val AdaptiveAppBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalIsDarkTheme.current) NavyAbyss else LightSkyBackground

val AdaptiveSurfaceColor: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalIsDarkTheme.current) NavySurface else LightCardSurface

val AdaptiveElevatedColor: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalIsDarkTheme.current) NavyElevated else LightElevatedSurface

val AdaptiveBorderColor: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalIsDarkTheme.current) NavyCardBorder else LightCardBorder

val AdaptivePrimaryText: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalIsDarkTheme.current) CloudWhite else DeepInk

val AdaptiveSecondaryText: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalIsDarkTheme.current) MistSilver else SlateMuted

val AdaptiveCyanAccent: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalIsDarkTheme.current) Color(0xFF00E5FF) else Color(0xFF007799)

val AdaptiveTealAccent: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalIsDarkTheme.current) Color(0xFF1DE9B6) else Color(0xFF00695C)
