package com.medtrixa.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MedtrixaDarkColors = darkColorScheme(
    primary = MedtrixaDarkPrimary,
    secondary = MedtrixaDarkSecondary,
    tertiary = MedtrixaDarkAccent,
    background = MedtrixaDarkBackground,
    surface = MedtrixaDarkSurface,
    surfaceVariant = MedtrixaDarkSurfaceVariant,
    onPrimary = MedtrixaDarkBackground,
    onSecondary = MedtrixaDarkBackground,
    onTertiary = MedtrixaDarkBackground,
    onBackground = MedtrixaDarkText,
    onSurface = MedtrixaDarkText,
    onSurfaceVariant = MedtrixaDarkMuted
)

private val MedtrixaLightColors = lightColorScheme(
    primary = MedtrixaLightPrimary,
    secondary = MedtrixaLightSecondary,
    tertiary = MedtrixaLightAccent,
    background = MedtrixaLightBackground,
    surface = MedtrixaLightSurface,
    onBackground = MedtrixaLightText,
    onSurface = MedtrixaLightText,
    onSurfaceVariant = MedtrixaLightMuted
)

@Composable
fun MedtrixaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        MedtrixaDarkColors
    } else {
        MedtrixaLightColors
    }

    MaterialTheme(
        colorScheme = colors,
        typography = MedtrixaTypography,
        content = content
    )
}
