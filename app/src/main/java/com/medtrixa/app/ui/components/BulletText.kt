package com.medtrixa.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** The app's standard bullet dot: 6 dp, primary colour, aligned to the first text line. */
@Composable
fun BulletDot(modifier: Modifier = Modifier) {
    Box(modifier.width(16.dp).padding(top = 7.dp)) {
        Box(
            Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

/** Bullet item: wrapped lines start in line with the first word. Left aligned by default. */
@Composable
fun BulletText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 13.sp,
    lineHeight: TextUnit = 19.sp,
    justify: Boolean = false
) {
    Row(modifier) {
        BulletDot()
        Text(
            text,
            fontSize = fontSize,
            lineHeight = lineHeight,
            textAlign = if (justify) TextAlign.Justify else TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
    }
}
