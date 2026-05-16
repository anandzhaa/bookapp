package com.bookapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightPrimary = Color(0xFF1565C0)
private val LightOnPrimary = Color(0xFFFFFFFF)
private val LightPrimaryContainer = Color(0xFFD6E4FF)
private val LightSecondary = Color(0xFF0288D1)
private val LightBackground = Color(0xFFF8F9FF)
private val LightSurface = Color(0xFFFFFFFF)
private val LightOnBackground = Color(0xFF1A1C2E)

private val DarkPrimary = Color(0xFF90CAF9)
private val DarkOnPrimary = Color(0xFF003258)
private val DarkPrimaryContainer = Color(0xFF00497D)
private val DarkSecondary = Color(0xFF81D4FA)
private val DarkBackground = Color(0xFF0F1117)
private val DarkSurface = Color(0xFF1A1D2E)
private val DarkOnBackground = Color(0xFFE3E8FF)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    secondary = LightSecondary,
    background = LightBackground,
    surface = LightSurface,
    onBackground = LightOnBackground
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    secondary = DarkSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkOnBackground
)

@Composable
fun BookAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
