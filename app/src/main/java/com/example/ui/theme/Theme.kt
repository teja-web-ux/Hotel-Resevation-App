package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = PureWhite,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = EmeraldDark,
    secondary = EmeraldPrimary,
    onSecondary = PureWhite,
    secondaryContainer = SurfaceLight,
    onSecondaryContainer = TextPrimary,
    background = PureWhite,
    onBackground = TextPrimary,
    surface = PureWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = TextSecondary,
    surfaceContainer = SurfaceCard,
    surfaceContainerHigh = SurfaceLight,
    outline = BorderLight,
    outlineVariant = BorderMedium,
    error = RedCancel,
    onError = PureWhite,
    errorContainer = RedCancelBg,
    onErrorContainer = RedCancel
)

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = PureWhite,
    primaryContainer = Color(0xFF0F3D20),
    onPrimaryContainer = EmeraldLight,
    secondary = EmeraldPrimary,
    onSecondary = PureWhite,
    secondaryContainer = DarkCard,
    onSecondaryContainer = DarkTextPrimary,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkCard,
    onSurfaceVariant = DarkTextSecondary,
    surfaceContainer = DarkCard,
    surfaceContainerHigh = DarkSurface,
    outline = DarkBorder,
    outlineVariant = DarkBorder,
    error = RedCancel,
    onError = PureWhite,
    errorContainer = Color(0xFF3E1A1A),
    onErrorContainer = PureWhite
)

@Composable
fun StarHotelTheme(
    darkTheme: Boolean = false, // Clean bright emerald theme as in the screenshot
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
