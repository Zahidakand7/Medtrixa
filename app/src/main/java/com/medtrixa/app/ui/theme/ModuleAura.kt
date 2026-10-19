package com.medtrixa.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance

// Hero gradients built only from the app theme, darkened so white text stays readable.
@Composable
private fun deep(c: Color, light: Float): Color {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    return lerp(c, Color.Black, if (dark) maxOf(light, 0.55f) else light)
}

@Composable
fun atlasHeroBrush(): Brush {
    val cs = MaterialTheme.colorScheme
    return Brush.linearGradient(listOf(deep(cs.primary, 0.30f), deep(cs.tertiary, 0.20f)))
}

@Composable
fun dosingHeroBrush(): Brush {
    val cs = MaterialTheme.colorScheme
    return Brush.linearGradient(listOf(deep(cs.secondary, 0.55f), deep(cs.primary, 0.30f)))
}


// Six accents built only from the theme roles (primary, secondary, tertiary and mixes).
// forWhiteCard darkens them in dark mode, for screens whose cards are always white.
@Composable
fun auraAccent(slot: Int, forWhiteCard: Boolean = false): Color {
    val cs = MaterialTheme.colorScheme
    val base = when (slot % 6) {
        0 -> cs.primary
        1 -> cs.tertiary
        2 -> cs.secondary
        3 -> lerp(cs.primary, cs.tertiary, 0.5f)
        4 -> lerp(cs.secondary, cs.primary, 0.5f)
        else -> lerp(cs.secondary, cs.tertiary, 0.5f)
    }
    val dark = cs.background.luminance() < 0.5f
    return if (forWhiteCard && dark) lerp(base, Color.Black, 0.45f) else base
}


@Composable
fun pubmedHeroBrush(): Brush {
    val cs = MaterialTheme.colorScheme
    return Brush.linearGradient(listOf(deep(cs.primary, 0.12f), deep(cs.primary, 0.55f)))
}


// Dark panel shades for diagram cards (always dark with light text), built from the theme
@Composable
fun auraPanel(deepest: Boolean): Color {
    val cs = MaterialTheme.colorScheme
    // top of the panel: deep teal from primary; bottom: dark slate from secondary
    return if (deepest) lerp(cs.secondary, Color.Black, 0.72f) else lerp(cs.primary, Color.Black, 0.50f)
}

// Theme accents lightened so they stay readable on dark panels
@Composable
fun auraOnDark(slot: Int): Color {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    return lerp(auraAccent(slot), Color.White, if (dark) 0.10f else 0.45f)
}

@Composable
fun auraSubOnDark(): Color = lerp(MaterialTheme.colorScheme.secondary, Color.White, 0.55f)


@Composable
fun stewardshipHeroBrush(): Brush {
    val cs = MaterialTheme.colorScheme
    return Brush.linearGradient(listOf(deep(cs.tertiary, 0.35f), deep(cs.secondary, 0.45f)))
}
