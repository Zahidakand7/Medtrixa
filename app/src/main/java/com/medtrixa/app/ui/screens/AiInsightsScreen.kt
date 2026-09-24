package com.medtrixa.app.ui.screens

import android.content.Intent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

// ───────────────────────── Data + network (unchanged logic) ─────────────────────────

private data class PubMedResult(
    val pmid: String,
    val title: String,
    val journal: String,
    val pubDate: String
)

private fun searchPubMed(query: String, sortMode: String): List<PubMedResult> {
    val encoded = URLEncoder.encode(query, "UTF-8")
    val sortParam = if (sortMode == "Newest") "pub_date" else "relevance"
    val searchUrl = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=$encoded&retmax=8&sort=$sortParam&retmode=json"

    val searchConn = URL(searchUrl).openConnection() as HttpURLConnection
    searchConn.connectTimeout = 10000
    searchConn.readTimeout = 10000
    val searchJson = JSONObject(searchConn.inputStream.bufferedReader().readText())
    searchConn.disconnect()

    val idArray = searchJson.getJSONObject("esearchresult").getJSONArray("idlist")
    if (idArray.length() == 0) return emptyList()

    val ids = (0 until idArray.length()).joinToString(",") { idArray.getString(it) }
    val summaryUrl = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&id=$ids&retmode=json"

    val summaryConn = URL(summaryUrl).openConnection() as HttpURLConnection
    summaryConn.connectTimeout = 10000
    summaryConn.readTimeout = 10000
    val summaryJson = JSONObject(summaryConn.inputStream.bufferedReader().readText())
    summaryConn.disconnect()

    val resultObj = summaryJson.getJSONObject("result")
    val results = mutableListOf<PubMedResult>()
    for (i in 0 until idArray.length()) {
        val pmid = idArray.getString(i)
        val item = resultObj.optJSONObject(pmid) ?: continue
        results.add(
            PubMedResult(
                pmid = pmid,
                title = item.optString("title", ""),
                journal = item.optString("fulljournalname", ""),
                pubDate = item.optString("pubdate", "")
            )
        )
    }
    return results
}

private const val AI_SOURCE_PREFS = "medtrixa_ai_source"

private fun getStoredApiKey(context: android.content.Context, provider: String): String {
    val prefs = context.getSharedPreferences(AI_SOURCE_PREFS, android.content.Context.MODE_PRIVATE)
    return prefs.getString("key_$provider", "") ?: ""
}

private fun saveApiKey(context: android.content.Context, provider: String, key: String) {
    val prefs = context.getSharedPreferences(AI_SOURCE_PREFS, android.content.Context.MODE_PRIVATE)
    prefs.edit().putString("key_$provider", key).apply()
}

private fun askChatGpt(apiKey: String, question: String): String {
    val conn = URL("https://api.openai.com/v1/chat/completions").openConnection() as HttpURLConnection
    conn.requestMethod = "POST"
    conn.setRequestProperty("Authorization", "Bearer $apiKey")
    conn.setRequestProperty("Content-Type", "application/json")
    conn.doOutput = true
    conn.connectTimeout = 60000
    conn.readTimeout = 60000

    val body = JSONObject().apply {
        put("model", "gpt-4o-mini")
        put("messages", JSONArray().apply {
            put(JSONObject().apply {
                put("role", "system")
                put(
                    "content",
                    "You are a clinical reference assistant for healthcare professionals. " +
                        "Answer concisely and precisely, using clinical terminology, and note " +
                        "when a question needs the clinician's own judgment or a primary source."
                )
            })
            put(JSONObject().apply {
                put("role", "user")
                put("content", question)
            })
        })
    }
    conn.outputStream.use { it.write(body.toString().toByteArray()) }

    val code = conn.responseCode
    val stream = if (code in 200..299) conn.inputStream else conn.errorStream
    val text = stream.bufferedReader().readText()
    conn.disconnect()

    if (code !in 200..299) throw Exception("ChatGPT error ($code): $text")

    return JSONObject(text)
        .getJSONArray("choices")
        .getJSONObject(0)
        .getJSONObject("message")
        .getString("content")
}

private fun askGeminiWithRetry(apiKey: String, question: String): String {
    repeat(3) { attempt ->
        try {
            return askGemini(apiKey, question)
        } catch (e: Exception) {
            val busy = e.message?.contains("(503)") == true || e.message?.contains("(429)") == true
            if (!busy) throw e
            Thread.sleep(2000L * (attempt + 1))
        }
    }
    throw Exception("Gemini is busy right now. Try again in a moment.")
}

private fun askGemini(apiKey: String, question: String): String {
    val conn = URL(
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key=$apiKey"
    ).openConnection() as HttpURLConnection
    conn.requestMethod = "POST"
    conn.setRequestProperty("Content-Type", "application/json")
    conn.doOutput = true
    conn.connectTimeout = 60000
    conn.readTimeout = 60000

    val body = JSONObject().apply {
        put("generationConfig", JSONObject().put("thinkingConfig", JSONObject().put("thinkingLevel", "low")))
        put("contents", JSONArray().put(
            JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().put("text", question)))
            }
        ))
    }
    conn.outputStream.use { it.write(body.toString().toByteArray()) }

    val code = conn.responseCode
    val stream = if (code in 200..299) conn.inputStream else conn.errorStream
    val text = stream.bufferedReader().readText()
    conn.disconnect()

    if (code !in 200..299) throw Exception("Gemini error ($code): $text")

    return JSONObject(text)
        .getJSONArray("candidates")
        .getJSONObject(0)
        .getJSONObject("content")
        .getJSONArray("parts")
        .getJSONObject(0)
        .getString("text")
}

// ───────────────────────── Markdown → structured cards ─────────────────────────

private sealed interface InsightBlock {
    data class Para(val text: String) : InsightBlock
    data class Bullet(val text: String) : InsightBlock
    data class Step(val n: String, val text: String) : InsightBlock
}

private data class InsightSection(val title: String?, val blocks: List<InsightBlock>)

private val numberedRegex = Regex("^(\\d+)[.)]\\s+(.*)$")

private fun parseInsight(md: String): List<InsightSection> {
    val out = mutableListOf<InsightSection>()
    var title: String? = null
    var blocks = mutableListOf<InsightBlock>()
    fun flush() {
        if (title != null || blocks.isNotEmpty()) out += InsightSection(title, blocks.toList())
        blocks = mutableListOf()
    }
    md.lines().map { it.trim() }.forEach { line ->
        val numbered = numberedRegex.matchEntire(line)
        when {
            line.isEmpty() -> Unit
            line.length >= 3 && line.all { it in "-*_|: " } -> Unit
            line.startsWith("#") -> {
                flush()
                title = line.trimStart('#').trim().replace("**", "")
            }
            line.startsWith("|") -> {
                val cells = line.split("|").map { it.trim() }.filter { it.isNotEmpty() }
                if (cells.isNotEmpty()) blocks += InsightBlock.Bullet(cells.joinToString(" — "))
            }
            line.startsWith("* ") || line.startsWith("- ") || line.startsWith("• ") ->
                blocks += InsightBlock.Bullet(line.drop(2).trim())
            numbered != null ->
                blocks += InsightBlock.Step(numbered.groupValues[1], numbered.groupValues[2])
            else -> blocks += InsightBlock.Para(line)
        }
    }
    flush()
    return out
}

private fun String.withBold(): AnnotatedString = buildAnnotatedString {
    split("**").forEachIndexed { i, part ->
        if (i % 2 == 1) withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append(part) }
        else append(part)
    }
}

private enum class Tone { Intro, Info, Good, Caution, Urgent }

private fun toneFor(title: String?): Tone {
    if (title == null) return Tone.Intro
    val t = title.lowercase()
    return when {
        listOf("urgent", "emergency", "red flag", "warning", "danger", "seek").any { it in t } -> Tone.Urgent
        listOf("treat", "manage", "therapy", "dosing", "medication", "prevent", "tip").any { it in t } -> Tone.Good
        listOf("cause", "risk", "complication", "side effect", "contraindic").any { it in t } -> Tone.Caution
        else -> Tone.Info
    }
}

private data class ToneStyle(
    val container: Color,
    val accent: Color,
    val onContainer: Color,
    val icon: ImageVector
)

@Composable
private fun toneStyle(tone: Tone): ToneStyle {
    val cs = MaterialTheme.colorScheme
    return when (tone) {
        Tone.Intro -> ToneStyle(cs.primaryContainer, cs.primary, cs.onPrimaryContainer, Icons.Outlined.Info)
        Tone.Info -> ToneStyle(cs.surfaceVariant.copy(alpha = 0.55f), cs.primary, cs.onSurface, Icons.Outlined.Info)
        Tone.Good -> ToneStyle(cs.secondaryContainer, cs.secondary, cs.onSecondaryContainer, Icons.Outlined.CheckCircle)
        Tone.Caution -> ToneStyle(cs.tertiaryContainer, cs.tertiary, cs.onTertiaryContainer, Icons.Outlined.Info)
        Tone.Urgent -> ToneStyle(cs.errorContainer, cs.error, cs.onErrorContainer, Icons.Outlined.Warning)
    }
}

// ───────────────────────── UI pieces ─────────────────────────

@Composable
private fun QuestionBubble(text: String) {
    val cs = MaterialTheme.colorScheme
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Surface(
            modifier = Modifier.widthIn(max = 300.dp),
            color = cs.primary,
            shape = RoundedCornerShape(20.dp, 20.dp, 6.dp, 20.dp)
        ) {
            Text(
                text,
                color = cs.onPrimary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun InsightCard(section: InsightSection) {
    val ts = toneStyle(toneFor(section.title))
    var expanded by remember { mutableStateOf(true) }

    Surface(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        shape = RoundedCornerShape(22.dp),
        color = ts.container
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            section.title?.let { title ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { expanded = !expanded },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(32.dp).background(ts.accent.copy(alpha = 0.16f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(ts.icon, contentDescription = null, tint = ts.accent, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = ts.onContainer,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = ts.onContainer.copy(alpha = 0.7f)
                    )
                }
            }

            if (expanded || section.title == null) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    section.blocks.forEach { b ->
                        when (b) {
                            is InsightBlock.Para -> Text(
                                b.text.withBold(),
                                style = if (section.title == null) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
                                color = ts.onContainer,
                                lineHeight = 22.sp
                            )
                            is InsightBlock.Bullet -> Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(
                                    modifier = Modifier.padding(top = 9.dp).size(6.dp).background(ts.accent, CircleShape)
                                )
                                Text(
                                    b.text.withBold(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ts.onContainer,
                                    lineHeight = 22.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            is InsightBlock.Step -> Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(
                                    modifier = Modifier.size(22.dp).background(ts.accent.copy(alpha = 0.16f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        b.n,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ts.accent,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    b.text.withBold(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ts.onContainer,
                                    lineHeight = 22.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InsightSkeleton() {
    val alpha by rememberInfiniteTransition(label = "sk").animateFloat(
        initialValue = 0.35f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "a"
    )
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha),
                        RoundedCornerShape(22.dp)
                    )
            )
        }
    }
}

@Composable
private fun AnswerActions(answer: String) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    var copied by remember(answer) { mutableStateOf(false) }
    val plain = remember(answer) {
        answer.replace("**", "")
            .replace(Regex("(?m)^#+\\s*"), "")
            .replace(Regex("(?m)^-{3,}$"), "")
            .trim()
    }
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = {
                clipboard.setText(AnnotatedString(plain))
                copied = true
            }) { Text(if (copied) "Copied" else "Copy", fontWeight = FontWeight.SemiBold) }
            TextButton(onClick = {
                val send = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, plain)
                }
                context.startActivity(Intent.createChooser(send, "Share answer"))
            }) { Text("Share", fontWeight = FontWeight.SemiBold) }
        }
        Text(
            "AI-generated. Verify against current clinical guidelines.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}

@Composable
private fun ErrorCard(message: String, actionLabel: String, onAction: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = cs.errorContainer
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(message, color = cs.onErrorContainer, style = MaterialTheme.typography.bodyMedium)
            TextButton(
                onClick = onAction,
                colors = ButtonDefaults.textButtonColors(contentColor = cs.onErrorContainer)
            ) { Text(actionLabel, fontWeight = FontWeight.SemiBold) }
        }
    }
}

@Composable
private fun PubMedCard(index: Int, r: PubMedResult, onOpen: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onOpen),
        shape = RoundedCornerShape(22.dp),
        color = cs.surfaceVariant.copy(alpha = 0.55f)
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier.size(28.dp).background(cs.primary.copy(alpha = 0.14f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "$index",
                    style = MaterialTheme.typography.labelMedium,
                    color = cs.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                Text(r.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                if (r.journal.isNotBlank() || r.pubDate.isNotBlank()) {
                    Text(
                        "${r.journal} · ${r.pubDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = cs.onSurfaceVariant
                    )
                }
                Text(
                    "Open in PubMed · PMID ${r.pmid}",
                    style = MaterialTheme.typography.labelMedium,
                    color = cs.primary
                )
            }
        }
    }
}

@Composable
private fun EmptyState(provider: String, onPick: (String) -> Unit) {
    val cs = MaterialTheme.colorScheme
    val suggestions = if (provider == "PubMed") {
        listOf("SGLT2 inhibitors in heart failure", "Sepsis fluid resuscitation", "Anticoagulation in atrial fibrillation")
    } else {
        listOf("Sepsis-3 criteria", "Metformin dosing in CKD", "Warfarin and antibiotic interactions", "Hyperkalemia management")
    }
    Column(modifier = Modifier.fillMaxWidth().padding(top = 20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("What do you need to know?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            if (provider == "PubMed") "Search medical literature. No API key needed."
            else "Ask a clinical question and get a structured, scannable answer.",
            style = MaterialTheme.typography.bodyMedium,
            color = cs.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            "TRY ASKING",
            style = MaterialTheme.typography.labelSmall,
            color = cs.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
        suggestions.forEach { s ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onPick(s) },
                shape = RoundedCornerShape(16.dp),
                color = cs.surfaceVariant.copy(alpha = 0.55f)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = cs.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(s, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun ComposerBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    loading: Boolean,
    onSend: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val canSend = query.isNotBlank() && !loading
    Surface(color = cs.surface, tonalElevation = 3.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                placeholder = { Text(placeholder) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                shape = RoundedCornerShape(28.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { if (canSend) onSend() }),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(if (canSend) cs.primary else cs.onSurface.copy(alpha = 0.12f))
                    .clickable(enabled = canSend, onClick = onSend),
                contentAlignment = Alignment.Center
            ) {
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (canSend) cs.onPrimary else cs.onSurface.copy(alpha = 0.38f)
                    )
                }
            }
        }
    }
}

// ───────────────────────── Screen ─────────────────────────

@Composable
@OptIn(ExperimentalFoundationApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
fun AiInsightsScreen(onBackClick: () -> Unit) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val listState = rememberLazyListState()

    var query by remember { mutableStateOf("") }
    var askedQuery by remember { mutableStateOf("") }
    var provider by remember { mutableStateOf("ChatGPT") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var errorNeedsKey by remember { mutableStateOf(false) }
    var results by remember { mutableStateOf<List<PubMedResult>>(emptyList()) }
    var aiAnswer by remember { mutableStateOf<String?>(null) }
    var sortMode by remember { mutableStateOf("Relevance") }

    var showAiSourceDialog by remember { mutableStateOf(false) }
    var keyDraft by remember { mutableStateOf("") }

    val providers = listOf("ChatGPT", "Gemini", "PubMed")
    val sections = remember(aiAnswer) { aiAnswer?.let { parseInsight(it) }.orEmpty() }

    fun runSearch(q: String = query) {
        val question = q.trim()
        if (question.isEmpty() || loading) return
        error = null
        errorNeedsKey = false
        results = emptyList()
        aiAnswer = null
        askedQuery = question
        query = ""
        val source = provider

        if (source == "PubMed") {
            loading = true
            scope.launch {
                try {
                    val r = withContext(Dispatchers.IO) { searchPubMed(question, sortMode) }
                    results = r
                    if (r.isEmpty()) error = "No results found for that query."
                } catch (e: Exception) {
                    error = "Couldn't reach PubMed: ${e.message}"
                } finally {
                    loading = false
                }
            }
            return
        }

        val apiKey = getStoredApiKey(context, source)
        if (apiKey.isBlank()) {
            errorNeedsKey = true
            error = "$source needs an API key before it can answer. Add yours, or switch to PubMed, which needs no key."
            return
        }

        loading = true
        scope.launch {
            try {
                val answer = withContext(Dispatchers.IO) {
                    if (source == "ChatGPT") askChatGpt(apiKey, question) else askGeminiWithRetry(apiKey, question)
                }
                aiAnswer = answer
            } catch (e: Exception) {
                error = "Couldn't reach $source: ${e.message}"
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(askedQuery, aiAnswer, results) { listState.animateScrollToItem(0) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

        if (showAiSourceDialog) {
            AlertDialog(
                onDismissRequest = { showAiSourceDialog = false },
                title = { Text("AI Source") },
                text = {
                    Column {
                        Text(
                            "Enter your own $provider API key. It's stored only on this device.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = keyDraft,
                            onValueChange = { keyDraft = it },
                            label = { Text("$provider API key") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        saveApiKey(context, provider, keyDraft.trim())
                        showAiSourceDialog = false
                        if (errorNeedsKey && askedQuery.isNotBlank() && keyDraft.isNotBlank()) {
                            runSearch(askedQuery)
                        }
                    }) { Text("Save") }
                },
                dismissButton = {
                    TextButton(onClick = { showAiSourceDialog = false }) { Text("Cancel") }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
                Text(
                    "AI Insights",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (provider != "PubMed") {
                    IconButton(onClick = {
                        keyDraft = getStoredApiKey(context, provider)
                        showAiSourceDialog = true
                    }) {
                        Icon(Icons.Outlined.Settings, contentDescription = "AI Source")
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                providers.forEach { option ->
                    FilterChip(
                        selected = provider == option,
                        enabled = !loading,
                        onClick = {
                            if (provider != option) {
                                provider = option
                                results = emptyList()
                                aiAnswer = null
                                error = null
                                errorNeedsKey = false
                                askedQuery = ""
                            }
                        },
                        label = { Text(option) }
                    )
                }
            }

            if (provider == "PubMed") {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Sort",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    listOf("Relevance", "Newest").forEach { mode ->
                        FilterChip(
                            selected = sortMode == mode,
                            enabled = !loading,
                            onClick = {
                                sortMode = mode
                                if (askedQuery.isNotBlank()) runSearch(askedQuery)
                            },
                            label = { Text(mode) }
                        )
                    }
                }
            }

            val hasContent = loading || error != null || aiAnswer != null || results.isNotEmpty()

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!hasContent) {
                    item { EmptyState(provider) { runSearch(it) } }
                } else {
                    item { QuestionBubble(askedQuery) }

                    if (loading) {
                        item {
                            if (provider == "PubMed") {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                    CircularProgressIndicator()
                                }
                            } else {
                                InsightSkeleton()
                            }
                        }
                    }

                    error?.let { msg ->
                        item {
                            ErrorCard(
                                message = msg,
                                actionLabel = if (errorNeedsKey) "Add API key" else "Try again"
                            ) {
                                if (errorNeedsKey) {
                                    keyDraft = getStoredApiKey(context, provider)
                                    showAiSourceDialog = true
                                } else {
                                    runSearch(askedQuery)
                                }
                            }
                        }
                    }

                    items(sections) { InsightCard(it) }

                    aiAnswer?.let { a -> item { AnswerActions(a) } }

                    if (results.isNotEmpty()) {
                        item {
                            Text(
                                "${results.size} results · ${sortMode.lowercase()}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        itemsIndexed(results) { i, r ->
                            PubMedCard(i + 1, r) {
                                uriHandler.openUri("https://pubmed.ncbi.nlm.nih.gov/${r.pmid}/")
                            }
                        }
                    }
                }
            }

            ComposerBar(
                query = query,
                onQueryChange = { query = it },
                placeholder = if (provider == "PubMed") "Search medical literature" else "Ask a clinical question",
                loading = loading,
                onSend = { runSearch() }
            )
        }
    }
}
