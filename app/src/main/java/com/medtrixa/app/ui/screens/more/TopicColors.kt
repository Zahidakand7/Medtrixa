package com.medtrixa.app.ui.screens.more

import androidx.compose.ui.graphics.Color

fun topicAccentColor(a: TopicAccent): Color = when (a) {
    TopicAccent.VIOLET -> Color(0xFF7C4DFF)
    TopicAccent.AMBER -> Color(0xFFE08600)
    TopicAccent.TEAL -> Color(0xFF0F9D8A)
    TopicAccent.ROSE -> Color(0xFFE0457B)
    TopicAccent.GREEN -> Color(0xFF2E9E5B)
    TopicAccent.BLUE -> Color(0xFF2F7DF6)
    TopicAccent.RED -> Color(0xFFD93025)
}
