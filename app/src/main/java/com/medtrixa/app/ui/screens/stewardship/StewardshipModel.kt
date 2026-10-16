package com.medtrixa.app.ui.screens.stewardship

abstract class SBlock

class SText(val text: String) : SBlock()

class SBullets(val items: List<String>) : SBlock()

enum class SKind { INFO }

class SCallout(val title: String, val body: String, val kind: SKind) : SBlock()

class SRow(val term: String, val full: String, val body: String, val note: String? = null)

class SRows(val rows: List<SRow>) : SBlock()

enum class SAccent { BLUE, ROSE, AMBER, RED, VIOLET, GREEN, TEAL }

class SSection(val title: String, val blocks: List<SBlock>, val accent: SAccent)
