package com.medtrixa.app.ui.screens.more

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun FitHeaderText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = false,
    maxLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    val baseSp = when {
        fontSize != TextUnit.Unspecified -> fontSize.value
        style.fontSize != TextUnit.Unspecified -> style.fontSize.value
        else -> 20f
    }
    val floorSp = (baseSp * 0.5f).coerceAtLeast(10f)
    var sizeSp by remember(text, baseSp) { mutableStateOf(baseSp) }
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = sizeSp.sp,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = (sizeSp * 1.25f).sp,
        overflow = TextOverflow.Clip,
        softWrap = false,
        maxLines = 1,
        onTextLayout = { r ->
            onTextLayout(r)
            if (r.didOverflowWidth && sizeSp > floorSp) {
                val needed = r.multiParagraph.maxIntrinsicWidth
                val avail = r.size.width.toFloat()
                val ratio = if (needed > 0f) avail / needed else 0.95f
                sizeSp = (sizeSp * ratio * 0.98f).coerceAtLeast(floorSp)
            }
        },
        style = style
    )
}
