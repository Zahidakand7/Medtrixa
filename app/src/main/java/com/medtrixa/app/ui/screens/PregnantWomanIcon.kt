package com.medtrixa.app.ui.screens

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

val PregnantWomanIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "PregnantWomanProfile",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).addPath(
        pathData = PathParser().parsePathString(
            "M12.7,3.5 a2.1,2.1 0 1,1 -4.2,0 a2.1,2.1 0 1,1 4.2,0 z " +
            "M8.6,8.9 A2,2 0 0,1 10.6,6.9 A2,2 0 0,1 12.6,8.9 V15.5 H8.6 Z " +
            "M16.6,12.3 a3.6,3.6 0 1,1 -7.2,0 a3.6,3.6 0 1,1 7.2,0 z " +
            "M8.6,15 H15.2 L13.6,22.5 H11.8 L11.4,17 H10.9 L10.6,22.5 H8.8 Z"
        ).toNodes(),
        fill = SolidColor(Color.Black)
    ).build()
}
