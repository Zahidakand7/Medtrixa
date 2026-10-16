package com.medtrixa.app.ui.screens.more

enum class CalloutKind { INFO, WARNING }

enum class TopicAccent { VIOLET, AMBER, TEAL, ROSE, GREEN, BLUE, RED }

data class TopicEntry(val term: String, val meaning: String, val note: String? = null)

data class AbbrevRow(
    val term: String,
    val elaboration: String,
    val meaning: String,
    val note: String? = null,
    val avoid: Boolean = false,
    val instead: String? = null
)

sealed class TopicBlock {
    data class Text(val body: String) : TopicBlock()
    data class Bullets(val items: List<String>) : TopicBlock()
    data class Entries(val items: List<TopicEntry>) : TopicBlock()
    data class Abbreviations(val rows: List<AbbrevRow>) : TopicBlock()
    data class Callout(val title: String, val body: String, val kind: CalloutKind = CalloutKind.INFO) : TopicBlock()
    data class Example(val label: String, val original: String, val decoded: String) : TopicBlock()
}

data class TopicSection(val title: String, val blocks: List<TopicBlock>, val accent: TopicAccent = TopicAccent.BLUE)

data class MoreTopic(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val badge: String = "",
    val accent: TopicAccent = TopicAccent.BLUE,
    val keywords: List<String> = emptyList(),
    val sections: List<TopicSection>,
    val references: List<String>
)
