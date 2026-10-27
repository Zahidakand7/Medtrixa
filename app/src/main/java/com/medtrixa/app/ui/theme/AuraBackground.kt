package com.medtrixa.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.compositeOver

@Composable
fun medtrixaAuraBrush(): Brush {
    val scheme = MaterialTheme.colorScheme
    return Brush.verticalGradient(
        listOf(
            scheme.primary.copy(alpha = 0.06f).compositeOver(scheme.background),
            scheme.background
        )
    )
}
