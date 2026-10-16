package com.medtrixa.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Standard back button: 48dp circle with a soft shadow.
 * [edgeInset] adds the missing start margin so the circle sits 16dp from the screen edge.
 * A fixed 14dp gap after the circle keeps every header title the same distance away.
 */
@Composable
fun PremiumBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    edgeInset: Dp = 0.dp
) {
    Box(modifier = modifier.padding(start = edgeInset, end = 14.dp)) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 3.dp,
            modifier = Modifier.size(48.dp)
        ) {
            IconButton(onClick = onClick) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
            }
        }
    }
}
