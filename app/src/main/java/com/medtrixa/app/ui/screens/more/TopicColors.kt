package com.medtrixa.app.ui.screens.more

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import com.medtrixa.app.ui.theme.auraAccent

@Composable
fun topicAccentColor(a: TopicAccent): Color = when (a) {
    TopicAccent.BLUE, TopicAccent.RED -> auraAccent(0)
    TopicAccent.VIOLET -> auraAccent(1)
    TopicAccent.GREEN -> auraAccent(2)
    TopicAccent.AMBER -> auraAccent(3)
    TopicAccent.TEAL -> auraAccent(4)
    TopicAccent.ROSE -> auraAccent(5)
}
