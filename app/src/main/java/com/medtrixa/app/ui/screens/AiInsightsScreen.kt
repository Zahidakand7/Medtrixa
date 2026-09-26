package com.medtrixa.app.ui.screens

import android.content.Intent
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
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
import java.util.UUID

// ───────────────────────── Data + network (unchanged logic) ─────────────────────────

private data class PubMedResult(
    val pmid: String,
    val title: String,
    val journal: String,
    val pubDate: String
)

private const val AI_SOURCE_PREFS = "medtrixa_ai_source"

private fun getStoredApiKey(context: android.content.Context, provider: String): String {
    val prefs = context.getSharedPreferences(AI_SOURCE_PREFS, android.content.Context.MODE_PRIVATE)
    return prefs.getString("key_$provider", "") ?: ""
}

private fun saveApiKey(context: android.content.Context, provider: String, key: String) {
    val prefs = context.getSharedPreferences(AI_SOURCE_PREFS, android.content.Context.MODE_PRIVATE)
    // Keys are written with apply() to a persistent, app-private SharedPreferences file.
    // They remain on disk across app restarts/updates until removeApiKey() is called.
    prefs.edit().putString("key_$provider", key).apply()
}

private fun removeApiKey(context: android.content.Context, provider: String) {
    val prefs = context.getSharedPreferences(AI_SOURCE_PREFS, android.content.Context.MODE_PRIVATE)
    prefs.edit().remove("key_$provider").apply()
}

// ───────────────────────── Chat history persistence ─────────────────────────
// Keeps the full conversation thread (every question + answer, or question +
// PubMed results) for each provider, so re-opening the screen after the app
// was closed shows the whole thread where you left off, not just the last turn.

private const val AI_HISTORY_PREFS = "medtrixa_ai_history"

private data class ChatExchange(
    val question: String,
    val answer: String? = null,
    val results: List<PubMedResult> = emptyList(),
    val sortMode: String = "Relevance"
)

private fun exchangesToJson(thread: List<ChatExchange>): JSONArray =
    JSONArray().apply {
        thread.forEach { exchange ->
            put(JSONObject().apply {
                put("q", exchange.question)
                exchange.answer?.let { put("a", it) }
                put("sort", exchange.sortMode)
                put("results", JSONArray().apply {
                    exchange.results.forEach { r ->
                        put(JSONObject().apply {
                            put("pmid", r.pmid)
                            put("title", r.title)
                            put("journal", r.journal)
                            put("pubDate", r.pubDate)
                        })
                    }
                })
            })
        }
    }

private fun jsonToExchanges(arr: JSONArray): List<ChatExchange> {
    val results = mutableListOf<ChatExchange>()
    for (i in 0 until arr.length()) {
        val json = arr.getJSONObject(i)
        val resultsArr = json.optJSONArray("results")
        val pubMedResults = mutableListOf<PubMedResult>()
        if (resultsArr != null) {
            for (j in 0 until resultsArr.length()) {
                val item = resultsArr.getJSONObject(j)
                pubMedResults.add(
                    PubMedResult(
                        pmid = item.optString("pmid", ""),
                        title = item.optString("title", ""),
                        journal = item.optString("journal", ""),
                        pubDate = item.optString("pubDate", "")
                    )
                )
            }
        }
        results.add(
            ChatExchange(
                question = json.optString("q", ""),
                answer = if (json.has("a")) json.optString("a") else null,
                results = pubMedResults,
                sortMode = json.optString("sort", "Relevance")
            )
        )
    }
    return results
}

private fun saveStoredThread(context: android.content.Context, provider: String, thread: List<ChatExchange>) {
    val prefs = context.getSharedPreferences(AI_HISTORY_PREFS, android.content.Context.MODE_PRIVATE)
    prefs.edit().putString("thread_$provider", exchangesToJson(thread).toString()).apply()
}

private fun loadStoredThread(context: android.content.Context, provider: String): List<ChatExchange> {
    val prefs = context.getSharedPreferences(AI_HISTORY_PREFS, android.content.Context.MODE_PRIVATE)
    val raw = prefs.getString("thread_$provider", null) ?: return emptyList()
    return try {
        jsonToExchanges(JSONArray(raw))
    } catch (e: Exception) {
        emptyList()
    }
}

private fun clearStoredThread(context: android.content.Context, provider: String) {
    val prefs = context.getSharedPreferences(AI_HISTORY_PREFS, android.content.Context.MODE_PRIVATE)
    prefs.edit().remove("thread_$provider").apply()
}

// ───────────────────────── Chat sessions (New Chat / Clear Chat / history) ─────────────────────────
// Each provider can now hold several saved conversations ("chat sessions") instead
// of just one. "New Chat" archives the current conversation into this list and
// starts a blank one; "Clear Chat" drops the current conversation without saving
// it; the chat-history list lets you switch back to any saved session later.

private data class ChatSession(
    val id: String,
    val title: String,
    val exchanges: List<ChatExchange> = emptyList()
)

private fun sessionTitle(exchanges: List<ChatExchange>): String {
    val q = exchanges.firstOrNull()?.question?.trim().orEmpty()
    return when {
        q.isEmpty() -> "New chat"
        q.length > 40 -> q.take(40) + "…"
        else -> q
    }
}

private fun saveSessions(context: android.content.Context, provider: String, sessions: List<ChatSession>) {
    val prefs = context.getSharedPreferences(AI_HISTORY_PREFS, android.content.Context.MODE_PRIVATE)
    val arr = JSONArray().apply {
        sessions.forEach { session ->
            put(JSONObject().apply {
                put("id", session.id)
                put("title", session.title)
                put("exchanges", exchangesToJson(session.exchanges))
            })
        }
    }
    prefs.edit().putString("sessions_$provider", arr.toString()).apply()
}

private fun loadSessions(context: android.content.Context, provider: String): List<ChatSession> {
    val prefs = context.getSharedPreferences(AI_HISTORY_PREFS, android.content.Context.MODE_PRIVATE)
    val raw = prefs.getString("sessions_$provider", null)
    if (raw != null) {
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val json = arr.getJSONObject(i)
                ChatSession(
                    id = json.optString("id", UUID.randomUUID().toString()),
                    title = json.optString("title", "New chat"),
                    exchanges = jsonToExchanges(json.optJSONArray("exchanges") ?: JSONArray())
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    // One-time migration: earlier versions kept a single thread per provider.
    // Wrap any existing thread into the first chat session so nobody loses history.
    val legacy = loadStoredThread(context, provider)
    if (legacy.isNotEmpty()) {
        val migrated = listOf(ChatSession(id = UUID.randomUUID().toString(), title = sessionTitle(legacy), exchanges = legacy))
        saveSessions(context, provider, migrated)
        clearStoredThread(context, provider)
        return migrated
    }
    return emptyList()
}

private fun loadActiveSessionId(context: android.content.Context, provider: String): String? {
    val prefs = context.getSharedPreferences(AI_HISTORY_PREFS, android.content.Context.MODE_PRIVATE)
    return prefs.getString("active_$provider", null)
}

private fun saveActiveSessionId(context: android.content.Context, provider: String, id: String?) {
    val prefs = context.getSharedPreferences(AI_HISTORY_PREFS, android.content.Context.MODE_PRIVATE)
    if (id == null) prefs.edit().remove("active_$provider").apply()
    else prefs.edit().putString("active_$provider", id).apply()
}

private const val CLINICAL_SYSTEM_PROMPT =
    "You are a clinical reference assistant for healthcare professionals. " +
        "Answer concisely and precisely, using clinical terminology, and note " +
        "when a question needs the clinician's own judgment or a primary source."

/** HTTP codes worth a short automatic retry with backoff before giving up. */
private val RETRYABLE_HTTP_CODES = setOf(429, 500, 502, 503)

/**
 * Turns a raw HTTP failure from a provider into a short, clinical-app-appropriate
 * message. The full raw body (which may contain provider-specific JSON) is only
 * ever written to Logcat for debugging — it is never shown to the user.
 */
private fun friendlyApiError(label: String, code: Int, rawBody: String): String {
    Log.w("AiInsights", "$label error ($code): $rawBody")
    return when (code) {
        401 -> "$label: API key is invalid."
        403 -> "$label: API access is not authorized."
        429 -> "$label is temporarily rate-limited. Please wait a moment and try again."
        500, 502, 503 -> "$label is temporarily unavailable. Please try again shortly."
        else -> "$label error ($code). Please try again."
    }
}

/** Turns a low-level network failure (no HTTP response at all) into a clean message. */
private fun friendlyNetworkError(label: String, e: Exception): String {
    Log.w("AiInsights", "$label network error", e)
    return when (e) {
        is java.net.SocketTimeoutException -> "$label: Request timed out. Please try again."
        is java.net.UnknownHostException -> "Check your internet connection."
        is java.io.IOException -> "Check your internet connection."
        else -> "$label: Something went wrong. Please try again."
    }
}

/**
 * Shared caller for OpenAI-compatible chat-completions endpoints.
 * Groq and OpenRouter both speak this same request/response shape.
 * `history` is the full thread so far, including the new question as the last
 * exchange (with a null answer) — this is what makes it a real multi-turn
 * conversation instead of a one-off question each time.
 *
 * Transient failures (429 rate limits, 500/502/503) are retried automatically with
 * exponential backoff (2s, 4s, 8s) before surfacing an error to the user.
 */
private fun askOpenAiCompatible(
    endpoint: String,
    apiKey: String,
    model: String,
    history: List<ChatExchange>,
    label: String
): String {
    val maxAttempts = 3
    var lastError: Exception = Exception("$label: Something went wrong. Please try again.")

    repeat(maxAttempts) { attempt ->
        try {
            val conn = URL(endpoint).openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Authorization", "Bearer $apiKey")
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 45000
            conn.readTimeout = 45000

            val body = JSONObject().apply {
                put("model", model)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", CLINICAL_SYSTEM_PROMPT)
                    })
                    history.forEach { exchange ->
                        put(JSONObject().apply {
                            put("role", "user")
                            put("content", exchange.question)
                        })
                        exchange.answer?.let { ans ->
                            put(JSONObject().apply {
                                put("role", "assistant")
                                put("content", ans)
                            })
                        }
                    }
                })
            }
            conn.outputStream.use { it.write(body.toString().toByteArray()) }

            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val text = stream.bufferedReader().readText()
            conn.disconnect()

            if (code in 200..299) {
                return JSONObject(text)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
            }

            val message = friendlyApiError(label, code, text)
            lastError = Exception(message)
            val retryable = code in RETRYABLE_HTTP_CODES
            if (!retryable || attempt == maxAttempts - 1) throw lastError
        } catch (e: Exception) {
            if (e === lastError) {
                // Already-classified HTTP error from above; only sleep-and-retry
                // when there are attempts left, otherwise propagate.
                if (attempt == maxAttempts - 1) throw e
            } else {
                // Low-level network failure (timeout, DNS, connection reset, etc).
                lastError = Exception(friendlyNetworkError(label, e))
                if (attempt == maxAttempts - 1) throw lastError
            }
        }
        Thread.sleep(2000L * (1L shl attempt)) // 2s, 4s, 8s
    }
    throw lastError
}

private fun askGroq(apiKey: String, history: List<ChatExchange>): String =
    askOpenAiCompatible("https://api.groq.com/openai/v1/chat/completions", apiKey, "openai/gpt-oss-120b", history, "Groq")

private fun askOpenRouter(apiKey: String, history: List<ChatExchange>): String =
    askOpenAiCompatible(
        "https://openrouter.ai/api/v1/chat/completions",
        apiKey,
        // Free-tagged route: no cost, but rate-limited and can be slower/queued
        // versus paid routes for the same underlying model.
        "meta-llama/llama-3.1-8b-instruct:free",
        history,
        "OpenRouter"
    )

private fun askGeminiWithRetry(apiKey: String, history: List<ChatExchange>): String {
    val maxAttempts = 3
    var lastError: Exception = Exception("Gemini is temporarily unavailable. Please try again.")
    repeat(maxAttempts) { attempt ->
        try {
            return askGemini(apiKey, history)
        } catch (e: GeminiHttpException) {
            lastError = Exception(friendlyApiError("Gemini", e.code, e.rawBody))
            val retryable = e.code in RETRYABLE_HTTP_CODES
            if (!retryable || attempt == maxAttempts - 1) throw lastError
        } catch (e: Exception) {
            lastError = Exception(friendlyNetworkError("Gemini", e))
            if (attempt == maxAttempts - 1) throw lastError
        }
        Thread.sleep(2000L * (1L shl attempt)) // 2s, 4s, 8s
    }
    throw lastError
}

/** Carries the HTTP status and raw body so the retry wrapper can classify the failure. */
private class GeminiHttpException(val code: Int, val rawBody: String) : Exception()

private fun askGemini(apiKey: String, history: List<ChatExchange>): String {
    val conn = URL(
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key=$apiKey"
    ).openConnection() as HttpURLConnection
    conn.requestMethod = "POST"
    conn.setRequestProperty("Content-Type", "application/json")
    conn.doOutput = true
    conn.connectTimeout = 45000
    conn.readTimeout = 45000

    val body = JSONObject().apply {
        put("generationConfig", JSONObject().put("thinkingConfig", JSONObject().put("thinkingLevel", "low")))
        put("contents", JSONArray().apply {
            history.forEach { exchange ->
                put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().put(JSONObject().put("text", exchange.question)))
                })
                exchange.answer?.let { ans ->
                    put(JSONObject().apply {
                        put("role", "model")
                        put("parts", JSONArray().put(JSONObject().put("text", ans)))
                    })
                }
            }
        })
    }
    conn.outputStream.use { it.write(body.toString().toByteArray()) }

    val code = conn.responseCode
    val stream = if (code in 200..299) conn.inputStream else conn.errorStream
    val text = stream.bufferedReader().readText()
    conn.disconnect()

    if (code !in 200..299) throw GeminiHttpException(code, text)

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

// Each AI/data source gets its own brand-ish accent color so the source picker
// reads at a glance instead of every option looking the same.
private fun providerColor(provider: String): Color = when (provider) {
    "Groq" -> Color(0xFFF55036)
    "Gemini" -> Color(0xFF4285F4)
    "OpenRouter" -> Color(0xFF6D28D9)
    else -> Color(0xFF6C63FF)
}

// Beyond color, each provider's chip ("chat head") gets its own icon and shape
// language so the row reads as genuinely different platforms, not one template
// recolored three times: Groq's is a sharp, low-radius badge (speed/inference),
// Gemini's is a full pill filled with its own multicolor gradient when active
// (Gemini's own brand treatment), and OpenRouter uses the same sharp,
// low-radius badge as Groq's, tinted in its own brand violet-purple.
private data class ProviderStyle(
    val icon: ImageVector,
    val shape: Shape,
    val gradient: List<Color>? = null,
    val bordered: Boolean = false
)

private fun providerStyle(provider: String): ProviderStyle = when (provider) {
    "Groq" -> ProviderStyle(icon = Icons.Filled.Bolt, shape = RoundedCornerShape(10.dp))
    "Gemini" -> ProviderStyle(
        icon = Icons.Filled.AutoAwesome,
        shape = RoundedCornerShape(50),
        gradient = listOf(Color(0xFF4285F4), Color(0xFF9B72CB), Color(0xFFD96570))
    )
    "OpenRouter" -> ProviderStyle(icon = Icons.Filled.AltRoute, shape = RoundedCornerShape(10.dp))
    else -> ProviderStyle(icon = Icons.Filled.Circle, shape = RoundedCornerShape(50))
}

@Composable
private fun ProviderChip(
    provider: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val accent = providerColor(provider)
    val style = providerStyle(provider)
    val contentColor = if (selected) Color.White else accent

    val backgroundModifier = when {
        selected && style.gradient != null -> Modifier.background(Brush.linearGradient(style.gradient), style.shape)
        selected -> Modifier.background(accent, style.shape)
        else -> Modifier.background(accent.copy(alpha = 0.12f), style.shape)
    }
    val borderModifier = if (style.bordered) Modifier.border(1.2.dp, accent, style.shape) else Modifier

    Row(
        modifier = Modifier
            .clip(style.shape)
            .then(backgroundModifier)
            .then(borderModifier)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(start = 12.dp, end = 14.dp, top = 7.dp, bottom = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            style.icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(7.dp))
        Text(
            provider,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (enabled) contentColor else contentColor.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun QuestionBubble(text: String, provider: String? = null) {
    val cs = MaterialTheme.colorScheme
    val tailShape = RoundedCornerShape(20.dp, 20.dp, 6.dp, 20.dp)
    // Solid fill for most providers, tinted with their own accent color (same
    // accent as their ProviderChip). Gemini is the exception: its bubble uses
    // a gradient-outline treatment — the chip's blue-purple-pink gradient as a
    // thin border around a plain surface fill, fully rounded (no tail cut),
    // matching the approved style-4 mockup — rather than a solid block or the
    // tailed shape used by the other providers.
    val gemini = provider == "Gemini"
    val gradient = if (gemini) providerStyle("Gemini").gradient else null
    val shape = if (gemini) RoundedCornerShape(20.dp) else tailShape
    val backgroundColor = if (gemini) null else provider?.let { providerColor(it) } ?: cs.primary
    val textColor = if (gemini) cs.onSurface else Color.White

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        val outerModifier = if (gradient != null) {
            Modifier
                .widthIn(max = 300.dp)
                .background(Brush.linearGradient(gradient), shape)
                .padding(2.dp)
        } else {
            Modifier.widthIn(max = 300.dp)
        }
        Surface(
            modifier = outerModifier,
            color = backgroundColor ?: cs.surfaceVariant,
            shape = shape
        ) {
            Text(
                text,
                color = textColor,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 9.dp)
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

// Each provider gets its own larger pool of suggestions so they don't all show the
// same four questions; pickSuggestions() below draws a fresh random subset from the
// right pool every time the screen is opened, so they also change on every app start.
// Pools mix foundational, everyday questions spanning common domains (diabetes,
// cardiovascular disease, skin conditions, respiratory, GI, renal, neuro, infectious
// disease, mental health) with the sharper clinical-scenario questions, since both
// are things people reach for the app to check.
private val suggestionPools: Map<String, List<String>> = mapOf(
    "Groq" to listOf(
        "Acute coronary syndrome red flags",
        "Steroid tapering after prolonged use",
        "Community-acquired pneumonia antibiotics",
        "Approach to acute abdominal pain",
        "Beta-blocker choice in heart failure",
        "Management of acute asthma exacerbation",
        "How to read an ABG",
        "First-line treatment for hypertension",
        "Signs of dehydration in adults",
        "When to start antibiotics for a UTI",
        "Common causes of fatigue",
        "How to interpret a basic metabolic panel",
        "Type 1 vs type 2 diabetes basics",
        "Warning signs of a heart attack",
        "Difference between eczema and psoriasis",
        "Normal fasting blood sugar range",
        "Common triggers for migraines",
        "When a rash needs urgent care"
    ),
    "Gemini" to listOf(
        "Differential for acute chest pain",
        "Insulin sliding scale basics",
        "Stroke thrombolysis window",
        "Approach to unexplained weight loss",
        "Thyroid function test interpretation",
        "Managing status epilepticus",
        "How to calculate BMI and what it means",
        "Normal vital sign ranges by age",
        "Common drug interactions with warfarin",
        "Signs of an allergic reaction vs anaphylaxis",
        "How vaccines work",
        "Difference between viral and bacterial infection",
        "How HbA1c reflects blood sugar control",
        "Risk factors for coronary artery disease",
        "Common causes of chronic itchy skin",
        "Warning signs of a stroke",
        "How cholesterol numbers are interpreted",
        "Basics of managing anxiety symptoms"
    ),
    "OpenRouter" to listOf(
        "Empiric antibiotics for febrile neutropenia",
        "Approach to hyponatremia workup",
        "DKA management in adults",
        "Anticoagulation reversal strategies",
        "Approach to syncope evaluation",
        "Sepsis bundle first-hour actions",
        "How to take blood pressure correctly",
        "Common causes of a persistent cough",
        "When a headache needs urgent evaluation",
        "Basics of fluid resuscitation",
        "How antibiotics are chosen for an infection",
        "What a fever actually means",
        "Diabetic foot care basics",
        "Lifestyle changes to lower blood pressure",
        "Identifying a fungal vs bacterial skin infection",
        "Basics of managing acne",
        "Common early symptoms of kidney disease",
        "When shortness of breath is an emergency"
    )
)

private fun pickSuggestions(provider: String): List<String> =
    (suggestionPools[provider] ?: emptyList()).shuffled().take(4)

@Composable
private fun EmptyState(provider: String, suggestions: List<String>, onPick: (String) -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(modifier = Modifier.fillMaxWidth().padding(top = 20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("What do you need to know?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            "Ask a clinical question and get a structured, scannable answer.",
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
@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
fun AiInsightsScreen(onBackClick: () -> Unit) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val listState = rememberLazyListState()

    var query by remember { mutableStateOf("") }
    var pendingQuestion by remember { mutableStateOf("") }
    var provider by remember { mutableStateOf("Groq") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var errorNeedsKey by remember { mutableStateOf(false) }
    var thread by remember { mutableStateOf<List<ChatExchange>>(emptyList()) }
    var sortMode by remember { mutableStateOf("Relevance") }

    // Saved chat sessions for the current provider, plus which one (if any) is
    // currently open. A null activeSessionId means the current conversation
    // hasn't been saved as a session yet (a brand-new, still-empty chat).
    var sessions by remember { mutableStateOf<List<ChatSession>>(emptyList()) }
    var activeSessionId by remember { mutableStateOf<String?>(null) }
    var showChatMenu by remember { mutableStateOf(false) }
    var showClearConfirm by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }

    var showAiSourceDialog by remember { mutableStateOf(false) }
    var keyDraft by remember { mutableStateOf("") }

    val providers = listOf("Groq", "Gemini", "OpenRouter")

    // Freshly randomized per provider each time this screen is entered, so the
    // "try asking" suggestions differ on every app start rather than being static.
    val suggestionsByProvider = remember { providers.associateWith { pickSuggestions(it) } }

    fun loadHistoryFor(target: String) {
        sessions = loadSessions(context, target)
        val storedActiveId = loadActiveSessionId(context, target)
        val active = sessions.find { it.id == storedActiveId }
        activeSessionId = active?.id
        thread = active?.exchanges ?: emptyList()
        sortMode = thread.lastOrNull { it.results.isNotEmpty() }?.sortMode ?: "Relevance"
        error = null
        errorNeedsKey = false
        pendingQuestion = ""
    }

    // Saves the current turn either into its existing session, or — for a
    // brand-new chat that hasn't been saved yet — creates that session now.
    fun persistThread(target: String, updated: List<ChatExchange>) {
        val currentId = activeSessionId
        if (currentId == null) {
            val newSession = ChatSession(id = UUID.randomUUID().toString(), title = sessionTitle(updated), exchanges = updated)
            val updatedSessions = sessions + newSession
            sessions = updatedSessions
            activeSessionId = newSession.id
            saveSessions(context, target, updatedSessions)
            saveActiveSessionId(context, target, newSession.id)
        } else {
            val updatedSessions = sessions.map { if (it.id == currentId) it.copy(exchanges = updated) else it }
            sessions = updatedSessions
            saveSessions(context, target, updatedSessions)
        }
    }

    // "New Chat": keeps the current conversation saved in chat history and
    // starts a blank one.
    fun startNewChat() {
        activeSessionId = null
        thread = emptyList()
        pendingQuestion = ""
        error = null
        errorNeedsKey = false
        saveActiveSessionId(context, provider, null)
    }

    // "Clear Chat": drops the current conversation entirely, without keeping
    // it in chat history.
    fun clearChat() {
        val currentId = activeSessionId
        if (currentId != null) {
            val updatedSessions = sessions.filterNot { it.id == currentId }
            sessions = updatedSessions
            saveSessions(context, provider, updatedSessions)
        }
        activeSessionId = null
        thread = emptyList()
        pendingQuestion = ""
        error = null
        errorNeedsKey = false
        saveActiveSessionId(context, provider, null)
    }

    fun switchToSession(session: ChatSession) {
        activeSessionId = session.id
        thread = session.exchanges
        sortMode = thread.lastOrNull { it.results.isNotEmpty() }?.sortMode ?: "Relevance"
        pendingQuestion = ""
        error = null
        errorNeedsKey = false
        saveActiveSessionId(context, provider, session.id)
        showHistoryDialog = false
    }

    // Restore the full thread for the initially selected provider when the screen
    // (re)opens, so closing and reopening the app doesn't lose where you left off.
    LaunchedEffect(Unit) { loadHistoryFor(provider) }

    fun runSearch(q: String = query) {
        val question = q.trim()
        if (question.isEmpty() || loading) return
        error = null
        errorNeedsKey = false
        pendingQuestion = question
        query = ""
        val source = provider

        val apiKey = getStoredApiKey(context, source)
        if (apiKey.isBlank()) {
            errorNeedsKey = true
            error = "$source needs an API key before it can answer."
            return
        }

        loading = true
        // The new question is included as the last turn of the history sent to the
        // model, so each answer is aware of the whole conversation so far.
        val historyForCall = thread + ChatExchange(question)
        scope.launch {
            try {
                val answer = withContext(Dispatchers.IO) {
                    when (source) {
                        "Groq" -> askGroq(apiKey, historyForCall)
                        "OpenRouter" -> askOpenRouter(apiKey, historyForCall)
                        else -> askGeminiWithRetry(apiKey, historyForCall)
                    }
                }
                val updated = thread + ChatExchange(question, answer = answer)
                thread = updated
                persistThread(source, updated)
                pendingQuestion = ""
            } catch (e: Exception) {
                // askOpenAiCompatible / askGeminiWithRetry already produce a clean,
                // labeled, user-facing message — no raw JSON or status-code noise.
                error = e.message ?: "Couldn't reach $source. Please try again."
            } finally {
                loading = false
            }
        }
    }

    // With reverseLayout = true below, item index 0 is always the bottom-most (newest)
    // item on screen, so scrolling to 0 is a true "scroll to bottom" — including when
    // the keyboard opens and shrinks the viewport, the newest message stays flush
    // against the composer instead of getting stranded above the fold.
    val hasContent = thread.isNotEmpty() || loading || error != null
    LaunchedEffect(thread.size, loading, error) {
        if (hasContent) listState.animateScrollToItem(0)
    }

    val imeVisible = WindowInsets.isImeVisible
    LaunchedEffect(imeVisible) {
        if (imeVisible && hasContent) listState.animateScrollToItem(0)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

        if (showAiSourceDialog) {
            AlertDialog(
                onDismissRequest = { showAiSourceDialog = false },
                title = { Text("AI Source") },
                text = {
                    Column {
                        Text(
                            "Enter your own $provider API key. It's stored only on this device, and stays saved until you remove it here.",
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
                        if (errorNeedsKey && pendingQuestion.isNotBlank() && keyDraft.isNotBlank()) {
                            runSearch(pendingQuestion)
                        }
                    }) { Text("Save") }
                },
                dismissButton = {
                    Row {
                        if (keyDraft.isNotBlank()) {
                            TextButton(onClick = {
                                // Stay open after removing so the user can type a
                                // replacement key right away instead of having to
                                // reopen "AI Source" from scratch.
                                removeApiKey(context, provider)
                                keyDraft = ""
                            }) { Text("Remove key") }
                        }
                        TextButton(onClick = { showAiSourceDialog = false }) { Text("Cancel") }
                    }
                }
            )
        }

        if (showClearConfirm) {
            AlertDialog(
                onDismissRequest = { showClearConfirm = false },
                title = { Text("Clear this chat?") },
                text = {
                    Text(
                        "This removes the current $provider conversation. It won't be kept in chat history.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        showClearConfirm = false
                        clearChat()
                    }) { Text("Clear") }
                },
                dismissButton = {
                    TextButton(onClick = { showClearConfirm = false }) { Text("Cancel") }
                }
            )
        }

        if (showHistoryDialog) {
            AlertDialog(
                onDismissRequest = { showHistoryDialog = false },
                title = { Text("Chat History · $provider") },
                text = {
                    if (sessions.isEmpty()) {
                        Text("No saved chats yet.", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            sessions.asReversed().forEach { session ->
                                val isActive = session.id == activeSessionId
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { switchToSession(session) },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isActive) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            session.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            "${session.exchanges.size} message${if (session.exchanges.size == 1) "" else "s"}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showHistoryDialog = false }) { Text("Close") }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                // Union (not stack) the nav-bar and keyboard insets: stacking both
                // padding modifiers left a visible gap between the keyboard and the
                // composer, since the keyboard's own inset already covers the nav
                // bar's space. This keeps the composer flush against whichever is
                // taller, matching how Claude/ChatGPT's own composer behaves.
                .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
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
                IconButton(onClick = {
                    keyDraft = getStoredApiKey(context, provider)
                    showAiSourceDialog = true
                }) {
                    Icon(Icons.Outlined.Settings, contentDescription = "AI Source")
                }
                Box {
                    IconButton(onClick = { showChatMenu = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Chat options")
                    }
                    DropdownMenu(expanded = showChatMenu, onDismissRequest = { showChatMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("New Chat") },
                            leadingIcon = { Icon(Icons.Filled.Add, contentDescription = null) },
                            onClick = {
                                showChatMenu = false
                                startNewChat()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Clear Chat") },
                            leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null) },
                            enabled = thread.isNotEmpty(),
                            onClick = {
                                showChatMenu = false
                                showClearConfirm = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Chat History") },
                            leadingIcon = { Icon(Icons.Filled.History, contentDescription = null) },
                            enabled = sessions.isNotEmpty(),
                            onClick = {
                                showChatMenu = false
                                showHistoryDialog = true
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                providers.forEach { option ->
                    ProviderChip(
                        provider = option,
                        selected = provider == option,
                        enabled = !loading,
                        onClick = {
                            if (provider != option) {
                                provider = option
                                loadHistoryFor(option)
                            }
                        }
                    )
                }
            }

            LazyColumn(
                state = listState,
                reverseLayout = hasContent,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (!hasContent) {
                    item {
                        EmptyState(provider, suggestionsByProvider[provider].orEmpty()) { runSearch(it) }
                    }
                } else {
                    // Declared newest-first: with reverseLayout = true, the first item in
                    // this block is the one pinned to the bottom of the screen.
                    error?.let { msg ->
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                if (!loading && pendingQuestion.isNotBlank()) QuestionBubble(pendingQuestion, provider)
                                ErrorCard(
                                    message = msg,
                                    actionLabel = if (errorNeedsKey) "Add API key" else "Try again"
                                ) {
                                    if (errorNeedsKey) {
                                        keyDraft = getStoredApiKey(context, provider)
                                        showAiSourceDialog = true
                                    } else {
                                        runSearch(pendingQuestion)
                                    }
                                }
                            }
                        }
                    }

                    if (loading) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                QuestionBubble(pendingQuestion, provider)
                                InsightSkeleton()
                            }
                        }
                    }

                    itemsIndexed(thread.asReversed()) { _, exchange ->
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            QuestionBubble(exchange.question, provider)
                            exchange.answer?.let { ans ->
                                val sections = remember(ans) { parseInsight(ans) }
                                sections.forEach { InsightCard(it) }
                                AnswerActions(ans)
                            }
                        }
                    }
                }
            }

            ComposerBar(
                query = query,
                onQueryChange = { query = it },
                placeholder = "Ask a Clinical Question",
                loading = loading,
                onSend = { runSearch() }
            )
        }
    }
}
