package com.ihebhidouri.marketview.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = MarketPrimary,
    secondary = MarketSecondary,
    tertiary = MarketTertiary,
    background = MarketBackground,
    surface = MarketSurface,
    error = MarketError,
    onPrimary = MarketBackground,
    onSecondary = MarketBackground,
    onTertiary = MarketBackground,
    onBackground = MarketTextPrimary,
    onSurface = MarketTextPrimary,
    onError = MarketTextPrimary ,
    surfaceVariant = MarketCard ,
    onSurfaceVariant = MarketTextSecondary,
    errorContainer = MarketErrorSoft

)

private val LightColorScheme = lightColorScheme(
    primary = MarketPrimary,
    secondary = MarketSecondary,
    tertiary = MarketTertiary,
    background = MarketLightBackground,
    surface = MarketLightSurface,
    error = MarketError,
    onPrimary = MarketBackground,
    onSecondary = MarketBackground,
    onTertiary = MarketBackground,
    onBackground = MarketLightTextPrimary,
    onSurface = MarketLightTextPrimary,
    onError = MarketTextPrimary ,
    surfaceVariant = MarketLightCard,
    onSurfaceVariant = MarketLightTextSecondary,
    errorContainer = MarketErrorSoft,

)
@Composable
fun MarketViewTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme =  DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}