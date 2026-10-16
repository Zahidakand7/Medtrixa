package com.medtrixa.app.ui.screens

import android.app.Activity
import com.medtrixa.app.ui.components.PremiumBackButton
import android.content.Intent
import androidx.compose.material.icons.filled.Description
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import androidx.compose.foundation.text.contextmenu.builder.item
import androidx.compose.foundation.text.contextmenu.data.ProcessTextKey
import androidx.compose.foundation.text.contextmenu.data.TextContextMenuItem
import androidx.compose.foundation.text.contextmenu.data.TextContextMenuKeys
import androidx.compose.foundation.text.contextmenu.data.TextContextMenuSession
import androidx.compose.foundation.text.contextmenu.modifier.appendTextContextMenuComponents
import androidx.compose.foundation.text.contextmenu.modifier.filterTextContextMenuComponents
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import android.speech.RecognizerIntent
import androidx.compose.material.icons.filled.Mic
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.PictureAsPdf
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.ui.draw.scale
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.runtime.key
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.res.painterResource
import com.medtrixa.app.R
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlinx.coroutines.delay
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.input.VisualTransformation
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
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

private const val KEYSTORE_ALIAS = "medtrixa_ai_key_wrap"
private const val ENC_PREFIX = "enc1:"

private fun aiSecretKey(): javax.crypto.SecretKey {
    val ks = java.security.KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    (ks.getKey(KEYSTORE_ALIAS, null) as? javax.crypto.SecretKey)?.let { return it }
    val gen = javax.crypto.KeyGenerator.getInstance(
        android.security.keystore.KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
    )
    gen.init(
        android.security.keystore.KeyGenParameterSpec.Builder(
            KEYSTORE_ALIAS,
            android.security.keystore.KeyProperties.PURPOSE_ENCRYPT or
                android.security.keystore.KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(android.security.keystore.KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
    )
    return gen.generateKey()
}

private fun encryptApiKey(plain: String): String {
    val cipher = javax.crypto.Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, aiSecretKey())
    val data = cipher.doFinal(plain.toByteArray(Charsets.UTF_8))
    val flags = android.util.Base64.NO_WRAP
    return ENC_PREFIX + android.util.Base64.encodeToString(cipher.iv, flags) + ":" +
        android.util.Base64.encodeToString(data, flags)
}

private fun decryptApiKey(stored: String): String = try {
    val parts = stored.removePrefix(ENC_PREFIX).split(":")
    val flags = android.util.Base64.NO_WRAP
    val iv = android.util.Base64.decode(parts[0], flags)
    val data = android.util.Base64.decode(parts[1], flags)
    val cipher = javax.crypto.Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(
        javax.crypto.Cipher.DECRYPT_MODE,
        aiSecretKey(),
        javax.crypto.spec.GCMParameterSpec(128, iv)
    )
    String(cipher.doFinal(data), Charsets.UTF_8)
} catch (e: Exception) {
    ""
}

private fun getStoredApiKey(context: android.content.Context, provider: String): String {
    val prefs = context.getSharedPreferences(AI_SOURCE_PREFS, android.content.Context.MODE_PRIVATE)
    val stored = prefs.getString("key_$provider", "") ?: ""
    if (stored.isBlank()) return ""
    if (!stored.startsWith(ENC_PREFIX)) {
        // Older plain-text key: encrypt it now, then keep using it.
        saveApiKey(context, provider, stored)
        return stored
    }
    return decryptApiKey(stored)
}

private fun saveApiKey(context: android.content.Context, provider: String, key: String) {
    val prefs = context.getSharedPreferences(AI_SOURCE_PREFS, android.content.Context.MODE_PRIVATE)
    // Encrypted with an Android Keystore key; falls back to plain text only if the
    // device's Keystore fails, so saving never breaks.
    val value = try { encryptApiKey(key) } catch (e: Exception) { key }
    prefs.edit().putString("key_$provider", value).apply()
}

private fun removeApiKey(context: android.content.Context, provider: String) {
    val prefs = context.getSharedPreferences(AI_SOURCE_PREFS, android.content.Context.MODE_PRIVATE)
    prefs.edit().remove("key_$provider").apply()
}

// ───────────────────────── Chat history persistence ─────────────────────────
// Keeps the full conversation thread (every question + answer, or question +
// PubMed results) for each provider, so re-opening the screen after the app
// was closed shows the whole thread where you left off, not just the last turn.

private fun keyLink(provider: String): String = when (provider) {
    "Groq" -> "https://console.groq.com/keys"
    "Gemini" -> "https://aistudio.google.com/apikey"
    else -> "https://openrouter.ai/keys"
}

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
    val exchanges: List<ChatExchange> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

private fun sessionTitle(exchanges: List<ChatExchange>): String {
    val q = exchanges.firstOrNull()?.question?.trim().orEmpty()
    return when {
        q.isEmpty() -> "New chat"
        q.length > 40 -> q.take(40) + "…"
        else -> q
    }
}

private val sessionTimestampFormat = java.text.SimpleDateFormat("MMM d, h:mm a", java.util.Locale.getDefault())

private fun formatSessionTimestamp(timestamp: Long): String =
    sessionTimestampFormat.format(java.util.Date(timestamp))

private fun saveSessions(context: android.content.Context, provider: String, sessions: List<ChatSession>) {
    val prefs = context.getSharedPreferences(AI_HISTORY_PREFS, android.content.Context.MODE_PRIVATE)
    val arr = JSONArray().apply {
        sessions.forEach { session ->
            put(JSONObject().apply {
                put("id", session.id)
                put("title", session.title)
                put("timestamp", session.timestamp)
                put("exchanges", exchangesToJson(session.exchanges))
            })
        }
    }
    prefs.edit().putString("sessions_$provider", arr.toString()).apply()
}

private const val CHAT_RETENTION_MS = 30L * 24 * 60 * 60 * 1000

private fun loadSessions(context: android.content.Context, provider: String): List<ChatSession> {
    val prefs = context.getSharedPreferences(AI_HISTORY_PREFS, android.content.Context.MODE_PRIVATE)
    val raw = prefs.getString("sessions_$provider", null)
    if (raw != null) {
        return try {
            val arr = JSONArray(raw)
            val all = (0 until arr.length()).map { i ->
                val json = arr.getJSONObject(i)
                ChatSession(
                    id = json.optString("id", UUID.randomUUID().toString()),
                    title = json.optString("title", "New chat"),
                    timestamp = json.optLong("timestamp", System.currentTimeMillis()),
                    exchanges = jsonToExchanges(json.optJSONArray("exchanges") ?: JSONArray())
                )
            }
            // Chat history auto-expires 30 days after its last activity.
            // Saved Answers are stored separately and are never touched here.
            val cutoff = System.currentTimeMillis() - CHAT_RETENTION_MS
            val kept = all.filter { it.timestamp >= cutoff }
            if (kept.size != all.size) saveSessions(context, provider, kept)
            kept
        } catch (e: Exception) {
            emptyList()
        }
    }
    // One-time migration: earlier versions kept a single thread per provider.
    // Wrap any existing thread into the first chat session so nobody loses history.
    val legacy = loadStoredThread(context, provider)
    if (legacy.isNotEmpty()) {
        val migrated = listOf(ChatSession(id = UUID.randomUUID().toString(), title = sessionTitle(legacy), timestamp = System.currentTimeMillis(), exchanges = legacy))
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

private data class SavedAnswer(
    val id: String,
    val provider: String,
    val question: String,
    val answer: String,
    val timestamp: Long
)

private fun loadSavedAnswers(context: android.content.Context): List<SavedAnswer> {
    val prefs = context.getSharedPreferences(AI_HISTORY_PREFS, android.content.Context.MODE_PRIVATE)
    val raw = prefs.getString("saved_answers", null) ?: return emptyList()
    return try {
        val arr = JSONArray(raw)
        // One unreadable entry must never cost the user the rest of their list.
        (0 until arr.length()).mapNotNull { i ->
            try {
                val j = arr.getJSONObject(i)
                SavedAnswer(
                    id = j.optString("id", UUID.randomUUID().toString()),
                    provider = j.optString("provider", ""),
                    question = j.optString("q", ""),
                    answer = j.optString("a", ""),
                    timestamp = j.optLong("ts", 0L)
                )
            } catch (e: Exception) {
                null
            }
        }
    } catch (e: Exception) {
        // Unreadable list: keep the raw data aside so a later save can't erase it.
        prefs.edit().putString("saved_answers_unreadable", raw).apply()
        emptyList()
    }
}

private fun writeSavedAnswers(context: android.content.Context, list: List<SavedAnswer>) {
    val prefs = context.getSharedPreferences(AI_HISTORY_PREFS, android.content.Context.MODE_PRIVATE)
    val arr = JSONArray().apply {
        list.forEach { sa ->
            put(JSONObject().apply {
                put("id", sa.id)
                put("provider", sa.provider)
                put("q", sa.question)
                put("a", sa.answer)
                put("ts", sa.timestamp)
            })
        }
    }
    prefs.edit().putString("saved_answers", arr.toString()).apply()
}

private const val CLINICAL_SYSTEM_PROMPT =
    "You are a clinical reference assistant for healthcare professionals. " +
        "Provide concise, evidence-based answers using precise clinical terminology. " +
        "Rely on medical knowledge reasoning and clinical safety guidelines. " +
        "Answer concisely and precisely, using clinical terminology, and note " +
        "when a question needs the clinician's own judgment or a primary source. " +
        "Organize the answer into several clearly titled sections using '## ' headings, " +
        "with bullet points under each, so it is thorough and easy to scan. " +
        "Do not add a separate Sources section. Instead, end each section with one short " +
        "final line that starts with 'Source:' and names the guideline, trial or consensus " +
        "statement (organization or study, and year) that section relies on. " +
        "Only name sources you are confident exist; if you are not sure, write " +
        "'Source: No specific guideline cited' instead of guessing."

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
        401 -> "API key rejected\n$label didn't accept your key. Update it and try again."
        403 -> "Access not authorized\n$label blocked this request. Check your key's permissions."
        429 -> "$label is busy\nToo many requests right now. Wait a minute, then try again."
        500, 502, 503 -> "$label is unavailable\nTheir servers are having trouble. Try again shortly, or switch source."
        else -> "Something went wrong\n$label returned an error ($code). Please try again."
    }
}

/** Turns a low-level network failure (no HTTP response at all) into a clean message. */
private fun friendlyNetworkError(label: String, e: Exception): String {
    Log.w("AiInsights", "$label network error", e)
    return when (e) {
        is java.net.SocketTimeoutException -> "$label is taking too long\nThe request timed out. Try again, or switch source."
        is java.net.UnknownHostException, is java.io.IOException -> "You're offline\nCheck your internet connection, then try again."
        else -> "Something went wrong\n$label couldn't complete the request. Please try again."
    }
}

// ───────────────────────── API key check ─────────────────────────

private enum class KeyCheck { Idle, Checking, Verified, Unverified, Rejected }

/**
 * Checks a key with a lightweight read-only request (list models / key info).
 * It generates no text, so it uses none of the free answer quota.
 *  - Verified:   the provider accepted the key.
 *  - Rejected:   the provider said the key is invalid or not allowed.
 *  - Unverified: couldn't tell (offline, timeout, busy). The key is still saved.
 */
private suspend fun verifyApiKey(provider: String, key: String): KeyCheck =
    withContext(Dispatchers.IO) {
        try {
            val url = when (provider) {
                "Groq" -> "https://api.groq.com/openai/v1/models"
                "Gemini" -> "https://generativelanguage.googleapis.com/v1beta/models?pageSize=1"
                else -> "https://openrouter.ai/api/v1/auth/key"
            }
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 10000
            conn.readTimeout = 10000
            if (provider == "Gemini") conn.setRequestProperty("x-goog-api-key", key)
            else conn.setRequestProperty("Authorization", "Bearer $key")
            val code = conn.responseCode
            val body = (if (code in 200..299) conn.inputStream else conn.errorStream)
                ?.bufferedReader()?.readText().orEmpty()
            conn.disconnect()
            when {
                code in 200..299 -> KeyCheck.Verified
                code == 401 || code == 403 -> KeyCheck.Rejected
                code == 400 && (body.contains("API_KEY_INVALID") || body.contains("API key not valid", ignoreCase = true)) -> KeyCheck.Rejected
                else -> KeyCheck.Unverified
            }
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            KeyCheck.Unverified
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
/** A clean, already user-facing failure while streaming. Never retried. */
private class StreamFailure(message: String) : Exception(message)

private fun askOpenAiCompatible(
    endpoint: String,
    apiKey: String,
    model: String,
    history: List<ChatExchange>,
    label: String,
    onPartial: (String) -> Unit = {},
    cancelled: () -> Boolean = { false }
): String {
    val maxAttempts = 3
    var lastError: Exception = Exception("$label: Something went wrong. Please try again.")
    // Once text has started arriving there is no silent retry: that would restart the answer.
    var started = false

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
                put("stream", true)
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
            if (code in 200..299) {
                val answer = StringBuilder()
                var lastEmit = 0L
                try {
                    conn.inputStream.bufferedReader().use { reader ->
                        while (true) {
                            if (cancelled()) throw kotlinx.coroutines.CancellationException("Stopped")
                            val line = reader.readLine() ?: break
                            if (!line.startsWith("data:")) continue
                            val payload = line.removePrefix("data:").trim()
                            if (payload.isEmpty()) continue
                            if (payload == "[DONE]") break
                            val chunk = try { JSONObject(payload) } catch (e: Exception) { continue }
                            chunk.optJSONObject("error")?.let { err ->
                                throw StreamFailure("$label: ${err.optString("message", "The answer was interrupted. Please try again.")}")
                            }
                            val delta = chunk.optJSONArray("choices")?.optJSONObject(0)?.optJSONObject("delta")
                            if (delta != null && !delta.isNull("content")) {
                                val piece = delta.getString("content")
                                if (piece.isNotEmpty()) {
                                    started = true
                                    answer.append(piece)
                                    val now = System.currentTimeMillis()
                                    if (now - lastEmit >= 60) {
                                        lastEmit = now
                                        onPartial(answer.toString())
                                    }
                                }
                            }
                        }
                    }
                } finally {
                    conn.disconnect()
                }
                if (answer.isBlank()) throw StreamFailure("$label: The answer came back empty. Please try again.")
                onPartial(answer.toString())
                return answer.toString()
            }

            val text = conn.errorStream?.bufferedReader()?.readText().orEmpty()
            conn.disconnect()

            val message = friendlyApiError(label, code, text)
            lastError = Exception(message)
            val retryable = code in RETRYABLE_HTTP_CODES
            if (!retryable || attempt == maxAttempts - 1) throw lastError
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: StreamFailure) {
            throw e
        } catch (e: Exception) {
            if (e === lastError) {
                // Already-classified HTTP error from above; only sleep-and-retry
                // when there are attempts left, otherwise propagate.
                if (attempt == maxAttempts - 1) throw e
            } else {
                // Low-level network failure (timeout, DNS, connection reset, etc).
                lastError = Exception(friendlyNetworkError(label, e))
                if (started || attempt == maxAttempts - 1) throw lastError
            }
        }
        Thread.sleep(2000L * (1L shl attempt)) // 2s, 4s, 8s
    }
    throw lastError
}

private fun askGroq(
    apiKey: String,
    history: List<ChatExchange>,
    onPartial: (String) -> Unit = {},
    cancelled: () -> Boolean = { false }
): String =
    askOpenAiCompatible("https://api.groq.com/openai/v1/chat/completions", apiKey, "openai/gpt-oss-120b", history, "Groq", onPartial, cancelled)

private fun askOpenRouter(
    apiKey: String,
    history: List<ChatExchange>,
    onPartial: (String) -> Unit = {},
    cancelled: () -> Boolean = { false }
): String =
    askOpenAiCompatible(
        "https://openrouter.ai/api/v1/chat/completions",
        apiKey,
        // Free-tagged route: no cost, but rate-limited and can be slower/queued
        // versus paid routes for the same underlying model.
        "inclusionai/ling-3.0-flash-sante:free",
        history,
        "OpenRouter",
        onPartial,
        cancelled
    )

/** Carries the HTTP status and raw body so the retry wrapper can classify the failure. */
private class GeminiHttpException(val code: Int, val rawBody: String) : Exception()

/** A clean, already user-facing message (blocked / empty answer). Never retried. */
private class GeminiContentException(message: String) : Exception(message)

// Tried in order. If a model fails for ANY reason other than a rejected API key
// (busy, quota, unknown/retired model ID, unsupported field, empty answer), the next
// one is used automatically. Quotas are per model, so each has its own free allowance.
// To swap models later, just edit this list.
private val GEMINI_MODELS = listOf(
    "gemini-3.6-flash",
    "gemini-3.5-flash-lite",
    "gemini-3.1-flash-lite"
)

// Longest we'll silently wait on a 429 before moving on to the fallback model.
private const val GEMINI_MAX_WAIT_MS = 15_000L

/** Reads RetryInfo.retryDelay (e.g. "34s") from a Gemini error body; null if absent. */
private fun geminiRetryDelayMs(rawBody: String): Long? = try {
    val details = JSONObject(rawBody).getJSONObject("error").optJSONArray("details")
    var ms: Long? = null
    if (details != null) {
        for (i in 0 until details.length()) {
            val d = details.getJSONObject(i)
            if (d.optString("@type").endsWith("RetryInfo")) {
                ms = (d.optString("retryDelay").removeSuffix("s").toDouble() * 1000).toLong()
            }
        }
    }
    ms
} catch (_: Exception) {
    null
}

/** True when the 429 is a per-day quota (quotaId contains "PerDay"): waiting won't help. */
private fun geminiIsDailyQuota(rawBody: String): Boolean =
    rawBody.contains("PerDay", ignoreCase = true)

/**
 * Gemini caller with quota-aware retry and automatic model fallback:
 *  - 401 (key rejected) fails immediately: every model would reject it too.
 *  - 429 per-minute: wait the server's own retryDelay (capped), retry once.
 *  - 500/502/503: one short retry with jitter.
 *  - Anything else that fails (429 per-day, 400/403/404 bad or retired model ID,
 *    blocked/empty answer, still busy after retry): move on to the next model in
 *    GEMINI_MODELS.
 *  - All models failed: show the most useful error (busy/unavailable beats a
 *    technical one).
 * Suspend + delay() so the Stop button cancels it instantly.
 */
private suspend fun askGeminiWithRetry(
    apiKey: String,
    history: List<ChatExchange>,
    onPartial: (String) -> Unit = {}
): String {
    var lastError: Exception = Exception("Gemini is temporarily unavailable. Please try again.")
    var busyError: Exception? = null
    for (model in GEMINI_MODELS) {
        var attempt = 0
        while (attempt < 2) {
            var waitMs = 0L
            try {
                return askGemini(apiKey, model, history, onPartial)
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: GeminiContentException) {
                lastError = e
                break // try next model
            } catch (e: GeminiHttpException) {
                lastError = Exception(friendlyApiError("Gemini", e.code, e.rawBody))
                if (e.code == 401) throw lastError
                if (e.code !in RETRYABLE_HTTP_CODES) break // bad/retired model etc: next model
                if (busyError == null) busyError = lastError
                if (e.code == 429 && geminiIsDailyQuota(e.rawBody)) break // next model
                waitMs = (if (e.code == 429) geminiRetryDelayMs(e.rawBody) else null)
                    ?: (1500L * (attempt + 1))
                if (waitMs > GEMINI_MAX_WAIT_MS) break // too long to wait: next model
            } catch (e: Exception) {
                lastError = Exception(friendlyNetworkError("Gemini", e))
                if (e is java.net.UnknownHostException) throw lastError // offline: retry is pointless
                waitMs = 1500L
            }
            if (attempt == 1) break
            delay(waitMs + kotlin.random.Random.nextLong(0, 400))
            attempt++
        }
    }
    throw busyError ?: lastError
}

private suspend fun askGemini(
    apiKey: String,
    model: String,
    history: List<ChatExchange>,
    onPartial: (String) -> Unit = {}
): String =
    withContext(Dispatchers.IO) {
        val conn = URL(
            "https://generativelanguage.googleapis.com/v1beta/models/$model:streamGenerateContent?alt=sse"
        ).openConnection() as HttpURLConnection
        // If the coroutine is cancelled (Stop button), close the socket so the
        // blocking read ends now instead of finishing in the background.
        val watcher = launch {
            try {
                kotlinx.coroutines.awaitCancellation()
            } finally {
                conn.disconnect()
            }
        }
        try {
            conn.requestMethod = "POST"
            conn.setRequestProperty("x-goog-api-key", apiKey)
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 45000
            conn.readTimeout = 45000

            val generationConfig = JSONObject().put("maxOutputTokens", 4096)
            // thinkingLevel is only sent to the main model; the fallback may not accept it.
            if (model == GEMINI_MODELS.first()) {
                generationConfig.put("thinkingConfig", JSONObject().put("thinkingLevel", "low"))
            }
            val body = JSONObject().apply {
                put(
                    "systemInstruction",
                    JSONObject().put(
                        "parts",
                        JSONArray().put(JSONObject().put("text", CLINICAL_SYSTEM_PROMPT))
                    )
                )
                put("generationConfig", generationConfig)
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
            if (code !in 200..299) {
                val errText = conn.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
                throw GeminiHttpException(code, errText)
            }

            val answer = StringBuilder()
            var lastEmit = 0L
            var blockReason = ""
            var finishReason = ""
            conn.inputStream.bufferedReader().use { reader ->
                while (true) {
                    val line = reader.readLine() ?: break
                    if (!line.startsWith("data:")) continue
                    val payload = line.removePrefix("data:").trim()
                    if (payload.isEmpty()) continue
                    val root = try { JSONObject(payload) } catch (e: Exception) { continue }
                    val candidate = root.optJSONArray("candidates")?.optJSONObject(0)
                    if (candidate == null) {
                        blockReason = root.optJSONObject("promptFeedback")?.optString("blockReason").orEmpty()
                        continue
                    }
                    finishReason = candidate.optString("finishReason", finishReason)
                    val parts = candidate.optJSONObject("content")?.optJSONArray("parts")
                    if (parts != null) {
                        for (i in 0 until parts.length()) {
                            val part = parts.getJSONObject(i)
                            if (part.optBoolean("thought", false)) continue
                            val piece = part.optString("text")
                            if (piece.isNotEmpty()) {
                                answer.append(piece)
                                val now = System.currentTimeMillis()
                                if (now - lastEmit >= 60) {
                                    lastEmit = now
                                    onPartial(answer.toString())
                                }
                            }
                        }
                    }
                }
            }
            if (answer.isBlank()) {
                Log.w("AiInsights", "Gemini empty answer. blockReason=$blockReason finishReason=$finishReason")
                if (blockReason.isNotEmpty()) {
                    throw GeminiContentException(
                        "Answer blocked\nGemini couldn't answer this question. Try rephrasing it."
                    )
                }
                throw GeminiContentException(
                    "No answer returned\nGemini sent an empty response. Try again or rephrase."
                )
            }
            onPartial(answer.toString())
            answer.toString()
        } finally {
            watcher.cancel()
            conn.disconnect()
        }
    }

// ───────────────────────── Markdown → structured cards ─────────────────────────

private sealed interface InsightBlock {
    data class Para(val text: String) : InsightBlock
    data class Bullet(val text: String) : InsightBlock
    data class Step(val n: String, val text: String) : InsightBlock
}

private data class InsightSection(val title: String?, val blocks: List<InsightBlock>)

private val numberedRegex = Regex("^(\\d+)[.)]\\s+(.*)$")

private val sourceLineRegex = Regex("^[*_\\s]*sources?\\s*:", RegexOption.IGNORE_CASE)

private fun isSourceLine(text: String): Boolean = sourceLineRegex.containsMatchIn(text)

/** Turns simple LaTeX the model sometimes emits into plain readable text. */
private fun cleanLatex(md: String): String {
    val math = Regex("\\$\\$?([^$]+?)\\$\\$?")
    return math.replace(md) { m ->
        var t = m.groupValues[1]
        t = Regex("\\\\text\\{([^{}]*)\\}").replace(t) { it.groupValues[1] }
        t = Regex("\\\\frac\\{([^{}]*)\\}\\{([^{}]*)\\}").replace(t) { "(${it.groupValues[1]}) / (${it.groupValues[2]})" }
        t = t.replace("\\left", "").replace("\\right", "")
            .replace("\\geq", "≥").replace("\\ge", "≥").replace("\\leq", "≤").replace("\\le", "≤")
            .replace("\\times", "×").replace("\\cdot", "·").replace("\\approx", "≈")
            .replace("\\%", "%")
            .replace("^{-1}", "⁻¹").replace("^{2}", "²").replace("^{3}", "³")
            .replace("^2", "²").replace("^3", "³")
        t.replace("{", "").replace("}", "").replace("\\", "").replace(Regex("\\s+"), " ").trim()
    }
}

private fun parseInsight(rawMd: String): List<InsightSection> {
    val md = cleanLatex(rawMd)
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

// Doses, lab values and thresholds ("5 mg/kg", "140 mmol/L", "30%") get picked
// out in the card's accent colour so key numbers stand out in long answers.
private val doseRegex = Regex(
    "\\b\\d+(?:[.,]\\d+)?(?:\\s?[-–]\\s?\\d+(?:[.,]\\d+)?)?\\s?" +
        "(?:mg/kg/day|mg/kg|mcg/kg|µg/kg|mg/dL|mmol/L|mEq/L|mL/kg|mL|mg|mcg|µg|IU|units|mmHg|bpm|kg|g|%)(?![A-Za-z])"
)

private fun String.withBold(highlight: Color? = null): AnnotatedString = buildAnnotatedString {
    split("**").forEachIndexed { i, part ->
        if (i % 2 == 1) {
            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append(part) }
        } else if (highlight == null) {
            append(part)
        } else {
            var last = 0
            doseRegex.findAll(part).forEach { m ->
                append(part.substring(last, m.range.first))
                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = highlight)) { append(m.value) }
                last = m.range.last + 1
            }
            append(part.substring(last))
        }
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
// A provider's chip icon is either a Material glyph (recolorable, used for
// providers we don't have a real logo asset for) or the provider's own
// drawable resource (its actual brand mark, rendered in its own colors).
private sealed class ProviderIcon {
    data class Vector(val icon: ImageVector) : ProviderIcon()
    data class Drawable(@androidx.annotation.DrawableRes val resId: Int) : ProviderIcon()
}

private data class ProviderStyle(
    val icon: ProviderIcon,
    val shape: Shape,
    val gradient: List<Color>? = null,
    val bordered: Boolean = false,
    // True when `icon` is a full wordmark (spells out the provider's name,
    // e.g. Groq's actual logo) rather than a compact symbol. The chip skips
    // its separate text label in that case, since the wordmark already reads
    // as the name — showing both would repeat it.
    val isWordmark: Boolean = false,
    // A small glyph shown before a wordmark icon (e.g. Groq's bolt next to
    // its logo). Unused for non-wordmark providers.
    val leadingIcon: ImageVector? = null
)

private fun providerStyle(provider: String): ProviderStyle = when (provider) {
    // Groq has no standalone symbol mark — their brand asset (from
    // console.groq.com/groq-logo.svg) is the wordmark itself, paired here
    // with a bolt glyph (speed/inference) in front of it.
    "Groq" -> ProviderStyle(
        icon = ProviderIcon.Drawable(R.drawable.ic_provider_groq_wordmark),
        shape = RoundedCornerShape(10.dp),
        isWordmark = true,
        leadingIcon = Icons.Filled.Bolt
    )
    "Gemini" -> ProviderStyle(
        icon = ProviderIcon.Vector(Icons.Filled.AutoAwesome),
        shape = RoundedCornerShape(50),
        gradient = listOf(Color(0xFF4285F4), Color(0xFF9B72CB), Color(0xFFD96570))
    )
    // OpenRouter's actual logo mark, dropped in as a drawable — see
    // ic_provider_openrouter.png in res/drawable.
    "OpenRouter" -> ProviderStyle(icon = ProviderIcon.Drawable(R.drawable.ic_provider_openrouter), shape = RoundedCornerShape(10.dp))
    else -> ProviderStyle(icon = ProviderIcon.Vector(Icons.Filled.Circle), shape = RoundedCornerShape(50))
}

@Composable
private fun ProviderChip(
    provider: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
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
    // Each chip takes an equal share of the row's width (via the weight
    // modifier passed in from the caller) and fills it, so all three chips
    // are always the same size and the row always fits the screen exactly —
    // no fixed dp values to guess, and no scrolling ever needed.
    val chipPadding = PaddingValues(start = 10.dp, end = 12.dp, top = 9.dp, bottom = 9.dp)

    Row(
        modifier = modifier
            .clip(style.shape)
            .then(backgroundModifier)
            .then(borderModifier)
            .clickable(enabled = enabled, onClick = onClick)
            .fillMaxWidth()
            // Fixed height so every chip is exactly the same height
            // regardless of whether its content is an icon+text (taller,
            // due to text line-height) or Groq's icon+logo (shorter).
            .height(38.dp)
            .padding(chipPadding),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        style.leadingIcon?.let { leading ->
            Icon(
                leading,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        when (val ic = style.icon) {
            is ProviderIcon.Vector -> Icon(
                ic.icon,
                contentDescription = provider,
                tint = contentColor,
                modifier = Modifier.size(17.dp)
            )
            is ProviderIcon.Drawable -> Icon(
                painter = painterResource(ic.resId),
                contentDescription = provider,
                tint = contentColor,
                modifier = if (style.isWordmark) {
                    // Size by the wordmark's own aspect ratio (Groq's is
                    // 40:15) rather than the fixed square used by symbol
                    // icons, so the lettering isn't squashed or clipped.
                    // Shrunk a bit smaller than the other providers' icons,
                    // since the wordmark otherwise reads much larger than
                    // Gemini/OpenRouter's icon+text at the same height.
                    Modifier.height(13.dp).aspectRatio(40f / 15f)
                } else {
                    Modifier.size(17.dp)
                }
            )
        }
        if (!style.isWordmark) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                provider,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (enabled) contentColor else contentColor.copy(alpha = 0.5f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
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

// ───────────────────────── Links to the app's own calculators ─────────────────────────
// When an answer mentions a score the app has a calculator for, offer a button
// that opens it. Ids match the screen names used by MainActivity's navigation.

private data class ScoreLink(val id: String, val label: String)

private fun detectScoreLinks(text: String): List<ScoreLink> {
    val t = text.lowercase()
    val out = mutableListOf<ScoreLink>()
    fun has(vararg keys: String) = keys.any { it in t }
    fun word(re: String) = Regex(re).containsMatchIn(t)

    if (has("curb-65", "curb65", "curb 65")) out += ScoreLink("curb65", "CURB-65")
    if (has("heart score")) out += ScoreLink("heart_score", "HEART Score")
    if (has("child-pugh", "child pugh")) out += ScoreLink("child_pugh", "Child-Pugh")
    if (has("centor")) out += ScoreLink("centor", "Centor Score")
    if (word("\\bmeld\\b")) out += ScoreLink("meld30", "MELD")
    if (has("apgar")) out += ScoreLink("apgar", "APGAR")
    if (has("glasgow coma") || word("\\bgcs\\b")) out += ScoreLink("gcs", "Glasgow Coma Scale")
    if (has("qsofa")) out += ScoreLink("qsofa", "qSOFA")
    if (has("cha2ds2", "cha₂ds₂")) out += ScoreLink("cha2ds2vasc", "CHA₂DS₂-VASc")
    if (has("has-bled", "hasbled")) out += ScoreLink("hasbled", "HAS-BLED")
    if (has("wells")) {
        if (has("dvt", "deep vein")) out += ScoreLink("wells_dvt", "Wells (DVT)")
        if (has("pulmonary embol")) out += ScoreLink("wells_pe", "Wells (PE)")
    }
    return out
}

@Composable
private fun ScoreLinkChip(label: String, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.clip(RoundedCornerShape(50)).clickable(onClick = onClick),
        shape = RoundedCornerShape(50),
        color = cs.primaryContainer
    ) {
        Text(
            "Open $label calculator →",
            style = MaterialTheme.typography.labelLarge,
            color = cs.onPrimaryContainer,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun InsightCard(section: InsightSection, highlighted: Boolean = false) {
    val ts = toneStyle(toneFor(section.title))
    // While being read aloud the card gets a strong outline and is kept in view.
    val bringIntoView = remember { BringIntoViewRequester() }
    LaunchedEffect(highlighted) {
        if (highlighted) bringIntoView.bringIntoView()
    }
    val cardClipboard = LocalClipboardManager.current
    val cardHaptic = LocalHapticFeedback.current
    var cardCopied by remember { mutableStateOf(false) }
    LaunchedEffect(cardCopied) {
        if (cardCopied) {
            delay(2000)
            cardCopied = false
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth().bringIntoViewRequester(bringIntoView),
        shape = RoundedCornerShape(22.dp),
        color = ts.container,
        border = BorderStroke(
            if (highlighted) 2.dp else 1.dp,
            if (highlighted) ts.accent else ts.accent.copy(alpha = 0.14f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            section.title?.let { title ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                    IconButton(
                        onClick = {
                            cardClipboard.setText(AnnotatedString(sectionPlain(section)))
                            cardCopied = true
                            cardHaptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            if (cardCopied) Icons.Filled.Check else Icons.Filled.ContentCopy,
                            contentDescription = if (cardCopied) "Copied" else "Copy section",
                            tint = if (cardCopied) Color(0xFF2E7D32) else ts.onContainer.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                section.blocks.forEach { b ->
                    when (b) {
                        is InsightBlock.Para -> if (isSourceLine(b.text)) {
                            Text(
                                b.text.replace("**", "").trim('*', '_', ' '),
                                style = MaterialTheme.typography.labelMedium,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = ts.onContainer.copy(alpha = 0.7f),
                                lineHeight = 18.sp
                            )
                        } else Text(
                            b.text.withBold(ts.accent),
                            style = if (section.title == null) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
                            color = ts.onContainer,
                            lineHeight = 22.sp
                        )
                        is InsightBlock.Bullet -> if (isSourceLine(b.text)) {
                            // The model sometimes writes the source as a bullet; show it as the source line.
                            Text(
                                b.text.replace("**", "").trim('*', '_', ' '),
                                style = MaterialTheme.typography.labelMedium,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = ts.onContainer.copy(alpha = 0.7f),
                                lineHeight = 18.sp
                            )
                        } else Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier.padding(top = 9.dp).size(6.dp).background(ts.accent, CircleShape)
                            )
                            Text(
                                b.text.withBold(ts.accent),
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
                                b.text.withBold(ts.accent),
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

private fun plainAnswer(answer: String): String =
    cleanLatex(answer).replace("**", "")
        .replace(Regex("(?m)^#+\\s*"), "")
        .replace(Regex("(?m)^-{3,}$"), "")
        .trim()

private fun toSpeakable(text: String): String =
    text.replace(Regex("(?m)^\\s*[-*•]\\s+"), "")
        .replace("*", "")
        .replace("→", " to ")

private fun sectionPlain(section: InsightSection): String = buildString {
    section.title?.let { appendLine(it) }
    section.blocks.forEach { b ->
        when (b) {
            is InsightBlock.Para -> appendLine(b.text)
            is InsightBlock.Bullet -> appendLine("• " + b.text)
            is InsightBlock.Step -> appendLine(b.n + ". " + b.text)
        }
    }
}.replace("**", "").trim()

// Google's recognizer often leaves end punctuation off. If the spoken text opens like
// a question ("what is...", "can you...", "is X better than Y"), end it with "?";
// anything else without end punctuation gets a full stop.
private val questionStarters = setOf(
    "what", "whats", "why", "how", "when", "where", "which", "who", "whom", "whose",
    "is", "are", "was", "were", "am", "can", "could", "do", "does", "did",
    "should", "would", "will", "shall", "may", "might", "has", "have", "had"
)

private fun capitalizeSentences(text: String, startsSentence: Boolean): String {
    var t = text
    if (startsSentence && t.isNotEmpty() && t[0].isLowerCase()) t = t[0].uppercaseChar() + t.substring(1)
    // A lower-case letter right after ". ", "? " or "! " starts a new sentence ("e.g. the" is left alone).
    return Regex("(?<=\\w{2}[.?!] )[a-z]").replace(t) { it.value.uppercase() }
}

private fun withQuestionMark(spoken: String): String {
    val text = spoken.trim()
    if (text.isEmpty() || text.endsWith("?") || text.endsWith("!")) return text
    val first = text.substringBefore(' ').lowercase().trim(',', '.', '\'', '’')
    val isQuestion = first.removeSuffix("s") in questionStarters || first in questionStarters
    return if (isQuestion) text.trimEnd('.', ',', ' ') + "?" else text.trimEnd(',', ' ').trimEnd('.') + "."
}

// ───────────────────────── Read aloud ─────────────────────────

private class SpeechChunk(val section: Int, val text: String)

/** Rewrites written clinical text so a speech engine says it the way a person would. */
private fun cleanForSpeech(raw: String): String {
    var t = raw
        .replace("**", "").replace("*", "").replace("`", "").replace("#", "")
        .replace("e.g.", "for example").replace("i.e.", "that is").replace("vs.", "versus")
        .replace("mg/kg/day", "milligrams per kilogram per day")
        .replace("mg/kg", "milligrams per kilogram")
        .replace("mcg/kg", "micrograms per kilogram")
        .replace("µg/kg", "micrograms per kilogram")
        .replace("mL/kg", "milliliters per kilogram")
        .replace("mg/dL", "milligrams per deciliter")
        .replace("mmol/L", "millimoles per liter")
        .replace("mEq/L", "milliequivalents per liter")
        .replace("mmHg", "millimeters of mercury")
        .replace("kg/m²", "kilograms per square meter")
        .replace("kg/m^2", "kilograms per square meter")
        .replace("≥", " at least ").replace("≤", " at most ")
        .replace("×", " times ").replace("→", " to ").replace("±", " plus or minus ")
        .replace("•", "")
    t = Regex("(\\d)\\s?(?:–|-)\\s?(\\d)").replace(t) { "${it.groupValues[1]} to ${it.groupValues[2]}" }
    t = Regex("(\\d)\\s?%").replace(t) { "${it.groupValues[1]} percent" }
    t = Regex("(\\d)\\s?mg\\b").replace(t) { "${it.groupValues[1]} milligrams" }
    t = Regex("(\\d)\\s?(?:mcg|µg)\\b").replace(t) { "${it.groupValues[1]} micrograms" }
    t = Regex("(\\d)\\s?mL\\b").replace(t) { "${it.groupValues[1]} milliliters" }
    t = Regex("(\\d)\\s?kg\\b").replace(t) { "${it.groupValues[1]} kilograms" }
    t = Regex("(\\d)\\s?bpm\\b").replace(t) { "${it.groupValues[1]} beats per minute" }
    return t.replace(Regex("\\s+"), " ").trim()
}

/** Splits an answer into short spoken pieces, each tagged with the card it belongs to. */
private fun buildSpeechChunks(answer: String): List<SpeechChunk> {
    val out = mutableListOf<SpeechChunk>()
    parseInsight(answer).forEachIndexed { si, section ->
        val pieces = mutableListOf<String>()
        section.title?.let { pieces += cleanForSpeech(it).trimEnd('.', ':') + "." }
        section.blocks.forEach { b ->
            val raw = when (b) {
                is InsightBlock.Para -> b.text
                is InsightBlock.Bullet -> b.text
                is InsightBlock.Step -> b.text
            }
            if (isSourceLine(raw)) return@forEach   // sources are not read out
            val t = cleanForSpeech(raw)
            if (t.isNotBlank()) pieces += if (t.last() in ".!?:;") t else "$t."
        }
        var buf = StringBuilder()
        for (piece in pieces) {
            for (sentence in piece.split(Regex("(?<=[.!?])\\s+"))) {
                if (buf.isNotEmpty() && buf.length + sentence.length > 320) {
                    out += SpeechChunk(si, buf.toString().trim())
                    buf = StringBuilder()
                }
                buf.append(sentence).append(' ')
            }
        }
        if (buf.isNotBlank()) out += SpeechChunk(si, buf.toString().trim())
    }
    return out
}

// ───────────────────────── Selection menu: Medtrixa's "Read aloud" ─────────────────────────

private val nativeReadAloudLabel = Regex("read|aloud|speak|listen", RegexOption.IGNORE_CASE)

/**
 * Lets answer text be selected using Android's own menu (Copy, Select all, ...), but swaps
 * the phone's built-in "Read aloud" item for Medtrixa's, in the same place in the menu.
 */
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun ReadAloudSelection(onReadAloud: (String) -> Unit, content: @Composable () -> Unit) {
    val clipboard = LocalClipboardManager.current
    // The menu's own Copy action is kept, so Medtrixa can read what is selected.
    val copyAction = remember { arrayOfNulls<(TextContextMenuSession) -> Unit>(1) }
    Box(
        modifier = Modifier
            .filterTextContextMenuComponents { component ->
                if (component is TextContextMenuItem && component.key == TextContextMenuKeys.CopyKey) {
                    copyAction[0] = component.onClick
                }
                // true = keep. Only the phone's own read-aloud entry is dropped.
                !(component is TextContextMenuItem &&
                    component.key is ProcessTextKey &&
                    nativeReadAloudLabel.containsMatchIn(component.label))
            }
            .appendTextContextMenuComponents {
                val action: TextContextMenuSession.() -> Unit = {
                    // Copy the selection, read it, then put back whatever was on the clipboard.
                    val previous = clipboard.getText()
                    copyAction[0]?.invoke(this)
                    val selected = clipboard.getText()?.text.orEmpty()
                    if (previous != null) clipboard.setText(previous)
                    close()
                    if (selected.isNotBlank()) onReadAloud(selected)
                }
                item("medtrixa_read_aloud", "Read aloud", 0, action)
            }
    ) {
        SelectionContainer { content() }
    }
}

/** The mini player shown above the question box while an answer is being read. */
@Composable
private fun ReaderBar(
    position: Int,
    total: Int,
    paused: Boolean,
    rate: Float,
    onPauseResume: () -> Unit,
    onRate: () -> Unit,
    onStop: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val fraction = if (total > 0) (position.toFloat() / total).coerceIn(0f, 1f) else 0f
    val rateLabel = when {
        rate < 0.95f -> "0.8×"
        rate > 1.1f -> "1.25×"
        else -> "1×"
    }
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        color = cs.primaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPauseResume) {
                Icon(
                    if (paused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                    contentDescription = if (paused) "Resume reading" else "Pause reading",
                    tint = cs.onPrimaryContainer
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    if (paused) "Paused · section $position of $total" else "Reading · section $position of $total",
                    style = MaterialTheme.typography.labelLarge,
                    color = cs.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(cs.onPrimaryContainer.copy(alpha = 0.18f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction)
                            .fillMaxHeight()
                            .background(cs.onPrimaryContainer)
                    )
                }
            }
            TextButton(onClick = onRate) {
                Text(rateLabel, color = cs.onPrimaryContainer, fontWeight = FontWeight.SemiBold)
            }
            IconButton(onClick = onStop) {
                Icon(Icons.Filled.Close, contentDescription = "Stop reading", tint = cs.onPrimaryContainer)
            }
        }
    }
}

// ───────────────────────── Word (.docx) export ─────────────────────────

private fun xmlEscape(text: String): String =
    text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")

/** One Word paragraph. "**bold**" in the text becomes bold runs. */
private fun docxParagraph(
    text: String,
    halfPoints: Int,
    bold: Boolean = false,
    italic: Boolean = false,
    color: String? = null,
    indentLeft: Int = 0,
    hanging: Int = 0,
    afterTwips: Int = 100
): String {
    val sb = StringBuilder()
    sb.append("<w:p><w:pPr><w:spacing w:after=\"").append(afterTwips).append("\"/>")
    if (indentLeft > 0) {
        sb.append("<w:ind w:left=\"").append(indentLeft).append("\" w:hanging=\"").append(hanging).append("\"/>")
    }
    sb.append("</w:pPr>")
    text.split("**").forEachIndexed { i, part ->
        if (part.isEmpty()) return@forEachIndexed
        val isBold = bold || i % 2 == 1
        sb.append("<w:r><w:rPr><w:rFonts w:ascii=\"Calibri\" w:hAnsi=\"Calibri\" w:cs=\"Calibri\"/>")
        if (isBold) sb.append("<w:b/>")
        if (italic) sb.append("<w:i/>")
        if (color != null) sb.append("<w:color w:val=\"").append(color).append("\"/>")
        sb.append("<w:sz w:val=\"").append(halfPoints).append("\"/></w:rPr>")
        sb.append("<w:t xml:space=\"preserve\">").append(xmlEscape(part)).append("</w:t></w:r>")
    }
    sb.append("</w:p>")
    return sb.toString()
}

/** Writes one answer as a Word document (.docx), built directly with no extra library. */
private fun buildAnswerDocx(question: String, provider: String, answer: String, out: java.io.OutputStream) {
    val gray = "6E6E78"
    val blue = "1A56A0"
    val body = StringBuilder()
    body.append(docxParagraph("Medtrixa · AI Insights", 18, color = gray, afterTwips = 120))
    body.append(docxParagraph(question, 32, bold = true, afterTwips = 60))
    body.append(
        docxParagraph(
            provider + " · " + formatSessionTimestamp(System.currentTimeMillis()),
            18, color = gray, afterTwips = 280
        )
    )
    for (section in parseInsight(answer)) {
        section.title?.let { body.append(docxParagraph(it, 25, bold = true, color = blue, afterTwips = 80)) }
        for (b in section.blocks) {
            when (b) {
                is InsightBlock.Para ->
                    if (isSourceLine(b.text)) {
                        body.append(docxParagraph(b.text.replace("**", "").trim('*', '_', ' '), 18, italic = true, color = gray, afterTwips = 120))
                    } else {
                        body.append(docxParagraph(b.text, 21, afterTwips = 100))
                    }
                is InsightBlock.Bullet ->
                    if (isSourceLine(b.text)) {
                        body.append(docxParagraph(b.text.replace("**", "").trim('*', '_', ' '), 18, italic = true, color = gray, afterTwips = 120))
                    } else {
                        body.append(docxParagraph("•  " + b.text, 21, indentLeft = 360, hanging = 260, afterTwips = 60))
                    }
                is InsightBlock.Step ->
                    body.append(docxParagraph(b.n + ".  " + b.text, 21, indentLeft = 360, hanging = 300, afterTwips = 60))
            }
        }
        body.append(docxParagraph("", 12, afterTwips = 120))
    }
    body.append(docxParagraph("AI-generated. Verify against current clinical guidelines.", 17, italic = true, color = gray, afterTwips = 0))

    val documentXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
        "<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\"><w:body>" +
        body +
        "<w:sectPr><w:pgSz w:w=\"11906\" w:h=\"16838\"/>" +
        "<w:pgMar w:top=\"1134\" w:right=\"1134\" w:bottom=\"1134\" w:left=\"1134\" w:header=\"708\" w:footer=\"708\" w:gutter=\"0\"/>" +
        "</w:sectPr></w:body></w:document>"
    val contentTypes = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
        "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">" +
        "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>" +
        "<Default Extension=\"xml\" ContentType=\"application/xml\"/>" +
        "<Override PartName=\"/word/document.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml\"/>" +
        "</Types>"
    val rels = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
        "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">" +
        "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"word/document.xml\"/>" +
        "</Relationships>"

    ZipOutputStream(out).use { zip ->
        fun put(name: String, content: String) {
            zip.putNextEntry(ZipEntry(name))
            zip.write(content.toByteArray(Charsets.UTF_8))
            zip.closeEntry()
        }
        put("[Content_Types].xml", contentTypes)
        put("_rels/.rels", rels)
        put("word/document.xml", documentXml)
    }
}

// ───────────────────────── PDF export ─────────────────────────

/** Writes one answer (question + section cards + disclaimer) as an A4 PDF. */
private fun buildAnswerPdf(question: String, provider: String, answer: String, out: java.io.OutputStream) {
    val doc = android.graphics.pdf.PdfDocument()
    val pageW = 595
    val pageH = 842
    val margin = 40
    val contentW = pageW - margin * 2
    val bottom = pageH - margin
    val gray = android.graphics.Color.rgb(110, 110, 120)

    fun paint(size: Float, bold: Boolean = false, italic: Boolean = false, color: Int = android.graphics.Color.rgb(25, 25, 30)): android.text.TextPaint {
        val tp = android.text.TextPaint(android.graphics.Paint.ANTI_ALIAS_FLAG)
        tp.textSize = size
        tp.color = color
        val style = when {
            bold && italic -> android.graphics.Typeface.BOLD_ITALIC
            bold -> android.graphics.Typeface.BOLD
            italic -> android.graphics.Typeface.ITALIC
            else -> android.graphics.Typeface.NORMAL
        }
        tp.typeface = android.graphics.Typeface.create(android.graphics.Typeface.SANS_SERIF, style)
        return tp
    }

    var pageNo = 0
    var page: android.graphics.pdf.PdfDocument.Page? = null
    var canvas: android.graphics.Canvas? = null
    var y = margin.toFloat()

    fun newPage() {
        page?.let { doc.finishPage(it) }
        pageNo++
        val p = doc.startPage(android.graphics.pdf.PdfDocument.PageInfo.Builder(pageW, pageH, pageNo).create())
        page = p
        canvas = p.canvas
        y = margin.toFloat()
    }

    fun styled(text: String): CharSequence {
        val sb = android.text.SpannableStringBuilder()
        text.split("**").forEachIndexed { i, part ->
            val start = sb.length
            sb.append(part)
            if (i % 2 == 1) {
                sb.setSpan(
                    android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                    start, sb.length, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        return sb
    }

    fun drawText(text: CharSequence, tp: android.text.TextPaint, indent: Int = 0, after: Float = 6f) {
        val width = contentW - indent
        val layout = android.text.StaticLayout.Builder.obtain(text, 0, text.length, tp, width)
            .setLineSpacing(2f, 1f)
            .build()
        for (i in 0 until layout.lineCount) {
            val top = layout.getLineTop(i)
            val bot = layout.getLineBottom(i)
            if (y + (bot - top) > bottom) newPage()
            val c = canvas ?: return
            c.save()
            c.translate((margin + indent).toFloat(), y - top)
            c.clipRect(0f, top.toFloat(), width.toFloat(), bot.toFloat())
            layout.draw(c)
            c.restore()
            y += (bot - top)
        }
        y += after
    }

    newPage()
    drawText("Medtrixa · AI Insights", paint(9f, color = gray), after = 10f)
    drawText(question, paint(16f, bold = true), after = 4f)
    drawText("$provider · ${formatSessionTimestamp(System.currentTimeMillis())}", paint(9f, color = gray), after = 14f)

    for (section in parseInsight(answer)) {
        section.title?.let {
            drawText(it, paint(12.5f, bold = true, color = android.graphics.Color.rgb(26, 86, 160)), after = 4f)
        }
        for (b in section.blocks) {
            when (b) {
                is InsightBlock.Para ->
                    if (isSourceLine(b.text)) {
                        drawText(b.text.replace("**", "").trim('*', '_', ' '), paint(9f, italic = true, color = gray), after = 6f)
                    } else {
                        drawText(styled(b.text), paint(10.5f), after = 5f)
                    }
                is InsightBlock.Bullet ->
                    if (isSourceLine(b.text)) {
                        drawText(b.text.replace("**", "").trim('*', '_', ' '), paint(9f, italic = true, color = gray), after = 6f)
                    } else {
                        drawText(styled("•  " + b.text), paint(10.5f), indent = 10, after = 3f)
                    }
                is InsightBlock.Step -> drawText(styled(b.n + ".  " + b.text), paint(10.5f), indent = 10, after = 3f)
            }
        }
        y += 8f
    }
    y += 6f
    drawText("AI-generated. Verify against current clinical guidelines.", paint(8.5f, italic = true, color = gray), after = 0f)

    page?.let { doc.finishPage(it) }
    doc.writeTo(out)
    doc.close()
}

@Composable
private fun AnswerActions(
    answer: String,
    provider: String,
    question: String,
    saved: Boolean,
    onToggleSave: () -> Unit,
    onRegenerate: (() -> Unit)? = null,
    onPdf: (() -> Unit)? = null,
    onDocx: (() -> Unit)? = null,
    reading: Boolean = false,
    onListen: () -> Unit = {}
) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val cs = MaterialTheme.colorScheme
    val green = Color(0xFF2E7D32)
    val amber = Color(0xFFF9A825)
    var copied by remember(answer) { mutableStateOf(false) }
    // Star pops when an answer is newly saved (not when an already-saved one is shown).
    var prevSaved by remember { mutableStateOf(saved) }
    var starBump by remember { mutableStateOf(false) }
    val starScale by animateFloatAsState(
        targetValue = if (starBump) 1.4f else 1f,
        animationSpec = spring(dampingRatio = 0.35f),
        label = "starPop"
    )
    LaunchedEffect(saved) {
        if (saved && !prevSaved) {
            starBump = true
            delay(160)
            starBump = false
        }
        prevSaved = saved
    }
    LaunchedEffect(copied) {
        if (copied) {
            delay(2000)
            copied = false
        }
    }
    val plain = remember(answer) { plainAnswer(answer) }

    val emoji = when (provider) {
        "Groq" -> "⚡"
        "Gemini" -> "✨"
        "OpenRouter" -> "🧭"
        else -> ""
    }
    val line1Rest = when (provider) {
        "Groq" -> "Powered by Groq AI • Responses are Ai-generated."
        "Gemini" -> "Powered by Gemini AI • Responses are Ai-generated."
        "OpenRouter" -> "Powered by OpenRouter AI • Responses are Ai-generated."
        else -> "Responses are Ai-generated."
    }
    val line2 = "Verify against Current Clinical Guidelines."
    val footerStyle = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp)
    val footerColor = MaterialTheme.colorScheme.onSurfaceVariant
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (copied) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = green.copy(alpha = 0.14f),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.height(40.dp).padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = green, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Copied",
                            style = MaterialTheme.typography.labelLarge,
                            color = green,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            } else {
                IconButton(onClick = {
                    clipboard.setText(AnnotatedString(plain))
                    copied = true
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", tint = cs.primary, modifier = Modifier.size(22.dp))
                }
            }
            IconButton(onClick = {
                val send = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, plain)
                }
                context.startActivity(Intent.createChooser(send, "Share answer"))
            }) {
                Icon(Icons.Filled.Share, contentDescription = "Share", tint = cs.primary, modifier = Modifier.size(22.dp))
            }
            if (onPdf != null) {
                IconButton(onClick = onPdf) {
                    Icon(Icons.Filled.PictureAsPdf, contentDescription = "Save as PDF", tint = cs.primary, modifier = Modifier.size(22.dp))
                }
            }
            if (onDocx != null) {
                IconButton(onClick = onDocx) {
                    Icon(Icons.Filled.Description, contentDescription = "Save as Word document", tint = cs.primary, modifier = Modifier.size(22.dp))
                }
            }
            if (onRegenerate != null) {
                IconButton(onClick = onRegenerate) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Regenerate", tint = cs.primary, modifier = Modifier.size(22.dp))
                }
            }
            IconButton(onClick = onToggleSave) {
                Icon(
                    if (saved) Icons.Filled.Star else Icons.Filled.StarBorder,
                    contentDescription = if (saved) "Remove from saved" else "Save answer",
                    tint = if (saved) amber else cs.primary,
                    modifier = Modifier.size(22.dp).scale(starScale)
                )
            }
            IconButton(onClick = onListen) {
                Icon(
                    if (reading) Icons.Filled.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = if (reading) "Stop reading" else "Read aloud",
                    tint = cs.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
            Text("$emoji $line1Rest", style = footerStyle, color = footerColor)
            Row {
                // Invisible copy of "<emoji> " reserves exactly the width the
                // real prefix occupies on line 1, so line 2 lines up under the
                // "P" in "Powered" based on actual measured layout.
                Text("$emoji ", style = footerStyle, color = Color.Transparent)
                Text(line2, style = footerStyle, color = footerColor)
            }
        }
    }
}

@Composable
private fun ErrorCard(message: String, actionLabel: String, onAction: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val title = message.substringBefore("\n")
    val body = message.substringAfter("\n", "")
    val offline = title.startsWith("You're offline")
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = cs.errorContainer
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (offline) Icons.Filled.WifiOff else Icons.Filled.ErrorOutline,
                    contentDescription = null,
                    tint = cs.onErrorContainer,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    title,
                    color = cs.onErrorContainer,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            if (body.isNotEmpty()) {
                Text(
                    body,
                    color = cs.onErrorContainer.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 30.dp)
                )
            }
            androidx.compose.material3.Button(
                onClick = onAction,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = cs.onErrorContainer,
                    contentColor = cs.errorContainer
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                modifier = Modifier.padding(start = 30.dp, top = 6.dp)
            ) {
                if (actionLabel == "Try again") {
                    Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(actionLabel, fontWeight = FontWeight.SemiBold)
            }
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
    provider: String,
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    loading: Boolean,
    onSend: () -> Unit,
    onStop: () -> Unit,
    onMic: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    // The box keeps its own cursor. When the text is changed from outside (voice input,
    // or cleared after sending) the cursor is placed at the end so typing can continue.
    var field by remember { mutableStateOf(TextFieldValue(query, TextRange(query.length))) }
    val shownField = if (field.text == query) field else TextFieldValue(query, TextRange(query.length))
    LaunchedEffect(query) {
        if (field.text != query) field = TextFieldValue(query, TextRange(query.length))
    }
    val canSend = query.isNotBlank() && !loading
    Surface(color = cs.surface, tonalElevation = 3.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = shownField,
                onValueChange = {
                    field = it
                    onQueryChange(it.text)
                },
                singleLine = true,
                placeholder = { Text(placeholder) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = onMic, enabled = !loading) {
                        Icon(Icons.Filled.Mic, contentDescription = "Ask by voice", tint = cs.primary)
                    }
                },
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
                    .background(
                        if (canSend) Brush.linearGradient(
                            // Same colours as the provider's chip: Gemini keeps its
                            // gradient, Groq and OpenRouter use their own solid accent.
                            providerStyle(provider).gradient
                                ?: providerColor(provider).let { listOf(it, it) }
                        ) else Brush.linearGradient(
                            listOf(cs.onSurface.copy(alpha = 0.12f), cs.onSurface.copy(alpha = 0.12f))
                        )
                    )
                    .clickable(enabled = canSend || loading, onClick = if (loading) onStop else onSend),
                contentAlignment = Alignment.Center
            ) {
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(40.dp), strokeWidth = 2.dp)
                    Icon(Icons.Filled.Stop, contentDescription = "Stop", tint = cs.primary, modifier = Modifier.size(18.dp))
                } else {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (canSend) Color.White else cs.onSurface.copy(alpha = 0.38f)
                    )
                }
            }
        }
    }
}

// ───────────────────────── Screen ─────────────────────────

@Composable
private fun KeyBanner(provider: String, onAdd: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onAdd),
        shape = RoundedCornerShape(14.dp),
        color = cs.tertiaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Info, contentDescription = null, tint = cs.onTertiaryContainer, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                "Add your $provider key to start asking",
                style = MaterialTheme.typography.bodyMedium,
                color = cs.onTertiaryContainer,
                modifier = Modifier.weight(1f)
            )
            Text("Add", fontWeight = FontWeight.SemiBold, color = cs.onTertiaryContainer)
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
fun AiInsightsScreen(
    onBackClick: () -> Unit,
    resumeLast: Boolean = false,
    onOpenScore: (String) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // PDF export: the system "save as" picker lets the user choose where the file goes,
    // so no storage permission is needed.
    var pdfPending by remember { mutableStateOf<Triple<String, String, String>?>(null) }
    val pdfLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        val job = pdfPending
        pdfPending = null
        if (uri != null && job != null) {
            scope.launch {
                val ok = withContext(Dispatchers.IO) {
                    try {
                        context.contentResolver.openOutputStream(uri)?.use {
                            buildAnswerPdf(job.first, job.second, job.third, it)
                        }
                        true
                    } catch (e: Exception) {
                        false
                    }
                }
                Toast.makeText(context, if (ok) "PDF saved" else "Couldn't save the PDF", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun exportPdf(question: String, answerProvider: String, answer: String) {
        pdfPending = Triple(question, answerProvider, answer)
        pdfLauncher.launch("Medtrixa-answer.pdf")
    }

    // Word export works the same way: the system "save as" screen picks the location.
    var docxPending by remember { mutableStateOf<Triple<String, String, String>?>(null) }
    val docxLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    ) { uri ->
        val job = docxPending
        docxPending = null
        if (uri != null && job != null) {
            scope.launch {
                val ok = withContext(Dispatchers.IO) {
                    try {
                        context.contentResolver.openOutputStream(uri)?.use {
                            buildAnswerDocx(job.first, job.second, job.third, it)
                        }
                        true
                    } catch (e: Exception) {
                        false
                    }
                }
                Toast.makeText(context, if (ok) "Word document saved" else "Couldn't save the document", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun exportDocx(question: String, answerProvider: String, answer: String) {
        docxPending = Triple(question, answerProvider, answer)
        docxLauncher.launch("Medtrixa-answer.docx")
    }

    // ── Read aloud: one reader for the whole screen, with a mini player ──
    var readingKey by remember { mutableStateOf<String?>(null) }
    var readerSection by remember { mutableStateOf(0) }
    var readerPaused by remember { mutableStateOf(false) }
    var readerRate by remember { mutableStateOf(1.0f) }
    val readerChunks = remember { arrayOf<List<SpeechChunk>>(emptyList()) }
    val readerSections = remember { arrayOf<List<Int>>(emptyList()) }
    val readerChunkIndex = remember { intArrayOf(0) }
    val readerGen = remember { intArrayOf(0) }
    val ttsHolder = remember { arrayOfNulls<TextToSpeech>(1) }
    val ttsReady = remember { booleanArrayOf(false) }
    DisposableEffect(Unit) {
        onDispose {
            ttsHolder[0]?.stop()
            ttsHolder[0]?.shutdown()
            ttsHolder[0] = null
        }
    }

    fun resetReader() {
        readingKey = null
        readerPaused = false
        readerSection = 0
        readerChunkIndex[0] = 0
    }

    fun speakFrom(startChunk: Int) {
        val engine = ttsHolder[0] ?: return
        val chunks = readerChunks[0]
        if (startChunk >= chunks.size) {
            resetReader()
            return
        }
        // Stop first, then register the new listener. Every utterance carries a "generation"
        // number so late callbacks from an interrupted run can be told apart and ignored.
        engine.stop()
        val gen = ++readerGen[0]
        engine.setLanguage(java.util.Locale.getDefault())
        engine.setSpeechRate(readerRate)
        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            private fun parse(id: String?): Int? {
                val m = id?.removePrefix("g") ?: return null
                val parts = m.split("c")
                if (parts.size != 2 || parts[0].toIntOrNull() != gen) return null
                return parts[1].toIntOrNull()
            }
            override fun onStart(utteranceId: String?) {
                val j = parse(utteranceId) ?: return
                readerChunkIndex[0] = j
                readerSection = chunks.getOrNull(j)?.section ?: 0
            }
            override fun onDone(utteranceId: String?) {
                val j = parse(utteranceId) ?: return
                if (j == chunks.size - 1) resetReader()
            }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                if (parse(utteranceId) != null) resetReader()
            }
        })
        for (j in startChunk until chunks.size) {
            engine.speak(chunks[j].text, TextToSpeech.QUEUE_ADD, null, "g${gen}c$j")
        }
    }

    fun stopReading() {
        readerGen[0]++
        ttsHolder[0]?.stop()
        resetReader()
    }

    fun startReading(key: String, answer: String) {
        val chunks = buildSpeechChunks(answer)
        if (chunks.isEmpty()) return
        readerChunks[0] = chunks
        readerSections[0] = chunks.map { it.section }.distinct()
        readerChunkIndex[0] = 0
        readerSection = chunks.first().section
        readerPaused = false
        readingKey = key
        if (ttsHolder[0] != null && ttsReady[0]) {
            speakFrom(0)
        } else if (ttsHolder[0] == null) {
            var engine: TextToSpeech? = null
            engine = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    ttsReady[0] = true
                    speakFrom(readerChunkIndex[0])
                } else {
                    resetReader()
                    Toast.makeText(context, "Read aloud isn't available on this phone", Toast.LENGTH_SHORT).show()
                }
            }
            ttsHolder[0] = engine
        }
    }

    fun toggleListen(key: String, answer: String) {
        if (readingKey == key) stopReading() else startReading(key, answer)
    }

    fun pauseResumeReading() {
        if (readerPaused) {
            readerPaused = false
            speakFrom(readerChunkIndex[0])
        } else {
            readerGen[0]++
            ttsHolder[0]?.stop()
            readerPaused = true
        }
    }

    fun cycleReadingSpeed() {
        readerRate = when {
            readerRate < 0.95f -> 1.0f
            readerRate < 1.1f -> 1.25f
            else -> 0.8f
        }
        if (readingKey != null && !readerPaused) speakFrom(readerChunkIndex[0])
    }
    val listState = rememberLazyListState()

    var query by remember { mutableStateOf("") }
    var pendingQuestion by remember { mutableStateOf("") }
    var provider by remember { mutableStateOf("Groq") }
    var loading by remember { mutableStateOf(false) }
    var streamingText by remember { mutableStateOf("") }
    // What is actually on screen. It follows streamingText at a steady pace, so text that
    // arrives in bursts still appears smoothly, like typing, instead of in jumps.
    var displayedText by remember { mutableStateOf("") }
    // True once the provider has sent the whole answer; the rest is then shown at once.
    var streamDone by remember { mutableStateOf(false) }
    LaunchedEffect(loading) {
        var shown = 0
        var budget = 0f   // characters we are allowed to reveal, built up tick by tick
        while (loading) {
            val target = streamingText
            if (target.isEmpty()) {
                shown = 0
                budget = 0f
                if (displayedText.isNotEmpty()) displayedText = ""
            } else {
                if (target.length < shown) shown = target.length
                val backlog = target.length - shown
                if (streamDone && backlog > 0) {
                    // The answer is complete: show whatever is left immediately.
                    shown = target.length
                    budget = 0f
                    displayedText = target
                } else if (backlog > 0) {
                    // Dynamic pace: about 140 characters a second while keeping up, faster
                    // when the answer is getting ahead, fastest when far behind.
                    val charsPerSecond = when {
                        backlog > 600 -> 450f
                        backlog > 150 -> 250f
                        else -> 140f
                    }
                    budget = minOf(budget + charsPerSecond * 0.04f, 40f)
                    var next = minOf(target.length, shown + budget.toInt())
                    if (next < target.length) {
                        // Reveal whole words only. A half-typed long word would otherwise
                        // jump to the next line the moment it no longer fits.
                        val boundary = target.lastIndexOfAny(charArrayOf(' ', '\n'), next - 1)
                        next = when {
                            boundary >= shown -> boundary + 1
                            backlog > 40 -> next   // very long token: don't stall
                            else -> shown          // wait for the word to finish
                        }
                    }
                    if (next != shown) {
                        budget = (budget - (next - shown)).coerceAtLeast(0f)
                        shown = next
                        displayedText = target.substring(0, shown)
                    }
                } else {
                    budget = 0f
                }
            }
            delay(40)
        }
        displayedText = ""
    }
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
    var showSavedDialog by remember { mutableStateOf(false) }
    var viewSavedId by remember { mutableStateOf<String?>(null) }
    var savedQuery by remember { mutableStateOf("") }
    var historyQuery by remember { mutableStateOf("") }
    val savedListState = rememberLazyListState()
    var savedAnswers by remember { mutableStateOf(loadSavedAnswers(context)) }
    var longPressedSessionId by remember { mutableStateOf<String?>(null) }

    var showAiSourceDialog by remember { mutableStateOf(false) }
    var keyDraft by remember { mutableStateOf("") }
    var searchJob by remember { mutableStateOf<Job?>(null) }
    var keyVersion by remember { mutableStateOf(0) }

    val providers = listOf("Groq", "Gemini", "OpenRouter")
    val hasKey = remember(provider, keyVersion) { getStoredApiKey(context, provider).isNotBlank() }

    // Freshly randomized per provider each time this screen is entered, so the
    // "try asking" suggestions differ on every app start rather than being static.
    val suggestionsByProvider = remember { providers.associateWith { pickSuggestions(it) } }

    // Loads a provider's saved sessions (for the Chat History list) and always
    // begins a fresh, unsaved conversation — the previous thread is never
    // auto-restored here. Every visit to a provider starts a new session; the
    // old ones stay reachable through Chat History rather than reappearing
    // on their own.
    fun startFreshFor(target: String) {
        sessions = loadSessions(context, target)
        activeSessionId = null
        thread = emptyList()
        sortMode = "Relevance"
        error = null
        errorNeedsKey = false
        pendingQuestion = ""
        saveActiveSessionId(context, target, null)
    }

    // Saves the current turn either into its existing session, or — for a
    // brand-new chat that hasn't been saved yet — creates that session now.
    fun persistThread(target: String, updated: List<ChatExchange>) {
        val currentId = activeSessionId
        if (currentId == null) {
            val newSession = ChatSession(id = UUID.randomUUID().toString(), title = sessionTitle(updated), timestamp = System.currentTimeMillis(), exchanges = updated)
            val updatedSessions = sessions + newSession
            sessions = updatedSessions
            activeSessionId = newSession.id
            saveSessions(context, target, updatedSessions)
            saveActiveSessionId(context, target, newSession.id)
        } else {
            val updatedSessions = sessions.map { if (it.id == currentId) it.copy(exchanges = updated, timestamp = System.currentTimeMillis()) else it }
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



    fun removeSession(session: ChatSession) {
        val updatedSessions = sessions.filterNot { it.id == session.id }
        sessions = updatedSessions
        saveSessions(context, provider, updatedSessions)
        if (session.id == activeSessionId) {
            activeSessionId = null
            thread = emptyList()
            pendingQuestion = ""
            error = null
            errorNeedsKey = false
            saveActiveSessionId(context, provider, null)
        }
    }

    fun shareSession(session: ChatSession) {
        val text = session.exchanges.joinToString("\n\n") { ex ->
            val ans = ex.answer?.replace("**", "")?.replace(Regex("(?m)^#+\\s*"), "")
            "Q: ${ex.question}" + (if (ans != null) "\n\n$ans" else "")
        }
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(send, "Share chat"))
    }

    fun toggleSaveAnswer(question: String, answer: String) {
        val existing = savedAnswers.firstOrNull { it.answer == answer && it.provider == provider }
        val updated = if (existing != null) {
            savedAnswers.filterNot { it.id == existing.id }
        } else {
            listOf(SavedAnswer(UUID.randomUUID().toString(), provider, question, answer, System.currentTimeMillis())) + savedAnswers
        }
        savedAnswers = updated
        writeSavedAnswers(context, updated)
    }

    // Every time this screen is opened it starts a new session — the previous
    // thread (if any) is not restored, only the saved history list is.
    LaunchedEffect(Unit) {
        // Coming back from a calculator opened out of an answer: reopen that chat
        // instead of starting a new one.
        if (resumeLast) {
            val prefs = context.getSharedPreferences(AI_HISTORY_PREFS, android.content.Context.MODE_PRIVATE)
            val resumeProvider = prefs.getString("resume_provider", null)
            if (resumeProvider != null && resumeProvider in providers) {
                val activeId = loadActiveSessionId(context, resumeProvider)
                provider = resumeProvider
                startFreshFor(resumeProvider)
                val session = sessions.firstOrNull { it.id == activeId }
                if (session != null) switchToSession(session)
                return@LaunchedEffect
            }
        }
        startFreshFor(provider)
    }

    fun openScore(id: String) {
        context.getSharedPreferences(AI_HISTORY_PREFS, android.content.Context.MODE_PRIVATE)
            .edit().putString("resume_provider", provider).apply()
        onOpenScore(id)
    }

    // Voice input: the phone's own speech recognizer (no microphone permission needed
    // by the app). The spoken question is placed in the box for review, not sent automatically.
    val voiceLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                val startsSentence = query.isBlank() || query.trimEnd().last() in ".?!"
                val fixed = capitalizeSentences(withQuestionMark(spoken), startsSentence)
                query = if (query.isBlank()) fixed else query.trimEnd() + " " + fixed
            }
        }
    }
    fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask a clinical question")
            // Asks the recognizer for punctuation where the phone supports it (Android 13+).
            putExtra("android.speech.extra.ENABLE_FORMATTING", "quality")
        }
        try {
            voiceLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Voice input isn't available on this phone", Toast.LENGTH_SHORT).show()
        }
    }

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
            error = "API key needed\nAdd your $source key to get answers."
            return
        }

        loading = true
        streamingText = ""
        streamDone = false
        // The new question is included as the last turn of the history sent to the
        // model, so each answer is aware of the whole conversation so far.
        val historyForCall = thread + ChatExchange(question)
        searchJob = scope.launch {
            val myJob = coroutineContext[Job]
            // Text is shown as it arrives; ignored once Stop has been tapped.
            val onPartial: (String) -> Unit = { text ->
                if (myJob?.isActive == true) streamingText = text
            }
            val cancelled: () -> Boolean = { myJob?.isActive != true }
            try {
                val answer = withContext(Dispatchers.IO) {
                    when (source) {
                        "Groq" -> askGroq(apiKey, historyForCall, onPartial, cancelled)
                        "OpenRouter" -> askOpenRouter(apiKey, historyForCall, onPartial, cancelled)
                        else -> askGeminiWithRetry(apiKey, historyForCall, onPartial)
                    }
                }
                // Saved to history straight away, so nothing is lost if you leave while the
                // text is still being revealed.
                val updated = thread + ChatExchange(question, answer = answer)
                persistThread(source, updated)
                // Let the on-screen text finish revealing before the final answer replaces it.
                streamingText = answer
                streamDone = true
                var waited = 0
                while (displayedText.length < answer.length && waited < 40000) {
                    delay(40)
                    waited += 40
                }
                thread = updated
                streamingText = ""
                displayedText = ""
                pendingQuestion = ""
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                // askOpenAiCompatible / askGeminiWithRetry already produce a clean,
                // labeled, user-facing message — no raw JSON or status-code noise.
                error = e.message ?: "Couldn't reach $source. Please try again."
            } finally {
                streamingText = ""
                displayedText = ""
                streamDone = false
                if (isActive) loading = false
            }
        }
    }

    fun stopSearch() {
        searchJob?.cancel()
        searchJob = null
        query = pendingQuestion
        pendingQuestion = ""
        streamingText = ""
        displayedText = ""
        streamDone = false
        loading = false
    }

    fun regenerateLast() {
        if (loading) return
        val last = thread.lastOrNull() ?: return
        thread = thread.dropLast(1)
        runSearch(last.question)
    }

    // With reverseLayout = true below, item index 0 is always the bottom-most (newest)
    // item on screen, so scrolling to 0 is a true "scroll to bottom" — including when
    // the keyboard opens and shrinks the viewport, the newest message stays flush
    // against the composer instead of getting stranded above the fold.
    val hasContent = thread.isNotEmpty() || loading || error != null
    LaunchedEffect(thread.size, loading, error) {
        if (hasContent) listState.animateScrollToItem(0)
    }

    // A new question, another chat, or a regenerated answer ends any reading in progress.
    LaunchedEffect(thread.size, activeSessionId, loading) { stopReading() }

    val imeVisible = WindowInsets.isImeVisible
    LaunchedEffect(imeVisible) {
        if (imeVisible && hasContent) listState.animateScrollToItem(0)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

        if (showAiSourceDialog) {
            val uriHandler = LocalUriHandler.current
            val clipboardMgr = LocalClipboardManager.current
            var keyVisible by remember { mutableStateOf(false) }
            var keySaved by remember { mutableStateOf(getStoredApiKey(context, provider).isNotBlank()) }
            val keyRejected = error?.startsWith("API key rejected") == true
            val savedGreen = Color(0xFF2E7D32)

            var keyCheck by remember { mutableStateOf(KeyCheck.Idle) }
            var checkJob by remember { mutableStateOf<Job?>(null) }
            // Only locked while the check runs. The dialog never closes by itself:
            // after a save it stays open until the user taps Close.
            val keyLocked = keyCheck == KeyCheck.Checking
            val storedKey = remember(keyVersion, provider) { getStoredApiKey(context, provider) }
            val keyUnchanged = keySaved && keyDraft.trim() == storedKey

            fun closeKeyDialog() {
                checkJob?.cancel()
                showAiSourceDialog = false
            }

            fun saveKey() {
                val cleaned = keyDraft.trim()
                if (cleaned.isEmpty() || keyLocked) return
                val rerun = (errorNeedsKey || keyRejected) && pendingQuestion.isNotBlank()
                keyCheck = KeyCheck.Checking
                checkJob = scope.launch {
                    val result = verifyApiKey(provider, cleaned)
                    if (result == KeyCheck.Rejected) {
                        // Wrong key: nothing is saved and the dialog stays open.
                        keyCheck = KeyCheck.Rejected
                        return@launch
                    }
                    saveApiKey(context, provider, cleaned)
                    keySaved = true
                    keyVersion++
                    keyCheck = result
                    if (rerun) runSearch(pendingQuestion)
                }
            }

            AlertDialog(
                onDismissRequest = { closeKeyDialog() },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("$provider API key", modifier = Modifier.weight(1f))
                        if (keySaved) {
                            Surface(shape = RoundedCornerShape(50), color = savedGreen.copy(alpha = 0.14f)) {
                                Text(
                                    "Saved",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = savedGreen,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                },
                text = {
                    Column {
                        Text(
                            "Stored only on this device, and stays saved until you remove it here.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = keyDraft,
                            onValueChange = {
                                keyDraft = it
                                if (keyCheck != KeyCheck.Checking) keyCheck = KeyCheck.Idle
                            },
                            label = { Text("$provider API key") },
                            singleLine = true,
                            readOnly = keyLocked,
                            isError = keyCheck == KeyCheck.Rejected,
                            visualTransformation = if (keyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { saveKey() }),
                            trailingIcon = {
                                Row {
                                    IconButton(onClick = { keyVisible = !keyVisible }, modifier = Modifier.size(36.dp)) {
                                        Icon(
                                            if (keyVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = if (keyVisible) "Hide key" else "Show key",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            if (!keyLocked) {
                                                clipboardMgr.getText()?.text?.let { keyDraft = it.trim() }
                                                keyCheck = KeyCheck.Idle
                                            }
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Filled.ContentPaste, contentDescription = "Paste", modifier = Modifier.size(20.dp))
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        when (keyCheck) {
                            KeyCheck.Checking -> Row(
                                modifier = Modifier.padding(top = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Checking key…", style = MaterialTheme.typography.bodySmall)
                            }
                            KeyCheck.Verified -> Row(
                                modifier = Modifier.padding(top = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Check, contentDescription = null, tint = savedGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Key verified and saved", style = MaterialTheme.typography.bodySmall, color = savedGreen)
                            }
                            KeyCheck.Unverified -> Row(
                                modifier = Modifier.padding(top = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Check, contentDescription = null, tint = savedGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Saved. Couldn't verify it right now (offline or busy).",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            KeyCheck.Rejected -> Text(
                                "This key was rejected. Check it and try again.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            KeyCheck.Idle -> {}
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (keySaved) {
                                Text(
                                    "Remove key",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.error.copy(alpha = if (keyLocked) 0.38f else 1f),
                                    modifier = Modifier
                                        .clickable(enabled = !keyLocked) {
                                            removeApiKey(context, provider)
                                            keyDraft = ""
                                            keySaved = false
                                            keyCheck = KeyCheck.Idle
                                            keyVersion++
                                        }
                                        .padding(vertical = 4.dp)
                                )
                            } else {
                                Spacer(modifier = Modifier.width(1.dp))
                            }
                            if (!keySaved || keyRejected) {
                                Text(
                                    if (keySaved) "Get a new key" else "Get a free key",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .clickable { uriHandler.openUri(keyLink(provider)) }
                                        .padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    if (keyUnchanged) {
                        // Key saved and nothing edited: nothing to cancel or save.
                        TextButton(onClick = { closeKeyDialog() }) { Text("Close") }
                    } else {
                        TextButton(onClick = { saveKey() }, enabled = keyDraft.isNotBlank() && !keyLocked) { Text("Save") }
                    }
                },
                dismissButton = if (keyUnchanged) null else ({
                    TextButton(onClick = { closeKeyDialog() }) { Text("Cancel") }
                })
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

        if (showSavedDialog) {
            AlertDialog(
                onDismissRequest = { showSavedDialog = false },
                title = {
                    Text(
                        "Saved Answers",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                text = {
                    if (savedAnswers.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Filled.StarBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Nothing saved yet. Tap the star under an answer to keep it here.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        val sq = savedQuery.trim()
                        val filteredSaved = savedAnswers.filter {
                            sq.isEmpty() || it.question.contains(sq, ignoreCase = true) ||
                                it.answer.contains(sq, ignoreCase = true)
                        }
                        Column {
                            OutlinedTextField(
                                value = savedQuery,
                                onValueChange = { savedQuery = it },
                                singleLine = true,
                                placeholder = { Text("Search Saved Answers") },
                                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                            )
                            if (filteredSaved.isEmpty()) {
                                Text("No matching answers.", style = MaterialTheme.typography.bodyMedium)
                            }
                            LazyColumn(
                                state = savedListState,
                                modifier = Modifier.heightIn(max = 520.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(filteredSaved, key = { it.id }) { item ->
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                // Opens the full-screen viewer; the list dialog
                                                // is restored (same scroll position) on Back.
                                                showSavedDialog = false
                                                viewSavedId = item.id
                                            },
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                item.question,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                "${item.provider} · ${formatSessionTimestamp(item.timestamp)}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSavedDialog = false }) { Text("Close") }
                }
            )
        }

        if (showHistoryDialog) {
            AlertDialog(
                onDismissRequest = { showHistoryDialog = false },
                title = {
                    Text(
                        "Chat History · $provider",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false
                    )
                },
                text = {
                    Column {
                        Text(
                            "Chats are deleted automatically after 30 days. Saved Answers are kept until you remove them.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        if (sessions.isEmpty()) {
                            Text("No saved chats yet.", style = MaterialTheme.typography.bodyMedium)
                        } else {
                            val hq = historyQuery.trim()
                            val sortedSessions = sessions.sortedByDescending { it.timestamp }.filter { sess ->
                                hq.isEmpty() || sess.title.contains(hq, ignoreCase = true) ||
                                    sess.exchanges.any { ex ->
                                        ex.question.contains(hq, ignoreCase = true) ||
                                            (ex.answer?.contains(hq, ignoreCase = true) == true)
                                    }
                            }
                            val visibleSessions = sortedSessions
                            OutlinedTextField(
                                value = historyQuery,
                                onValueChange = { historyQuery = it },
                                singleLine = true,
                                placeholder = { Text("Search Chats") },
                                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                            )
                            if (visibleSessions.isEmpty()) {
                                Text("No matching chats.", style = MaterialTheme.typography.bodyMedium)
                            }
                            LazyColumn(
                                modifier = Modifier.heightIn(max = 520.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Newest first, like Claude's own chat history list.
                                items(visibleSessions, key = { it.id }) { session ->
                                    val isActive = session.id == activeSessionId
                                    Box {
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .combinedClickable(
                                                    onClick = { switchToSession(session) },
                                                    onLongClick = { longPressedSessionId = session.id }
                                                ),
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (isActive) MaterialTheme.colorScheme.primaryContainer
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        session.title,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                    Text(
                                                        formatSessionTimestamp(session.timestamp),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                        DropdownMenu(
                                            expanded = longPressedSessionId == session.id,
                                            onDismissRequest = { longPressedSessionId = null }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("Remove") },
                                                onClick = { removeSession(session); longPressedSessionId = null }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Share") },
                                                onClick = { shareSession(session); longPressedSessionId = null }
                                            )
                                        }
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
                PremiumBackButton(onClick = onBackClick, edgeInset = 8.dp)
                Text(
                    "AI Insights",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f), fontSize = 20.sp, lineHeight = 26.sp
                )
                IconButton(onClick = {
                    keyDraft = getStoredApiKey(context, provider)
                    showAiSourceDialog = true
                }) {
                    Box {
                        Icon(Icons.Outlined.Settings, contentDescription = "AI Source")
                        if (!hasKey) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(8.dp)
                                    .background(MaterialTheme.colorScheme.error, CircleShape)
                            )
                        }
                    }
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
                        DropdownMenuItem(
                            text = { Text("Saved Answers") },
                            leadingIcon = { Icon(Icons.Filled.Star, contentDescription = null) },
                            onClick = {
                                showChatMenu = false
                                showSavedDialog = true
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                providers.forEach { option ->
                    // Gemini's name is short enough to need less room; giving
                    // it a smaller share and putting that space toward Groq
                    // and OpenRouter (kept equal to each other) stops
                    // "OpenRouter" from being cut off with an ellipsis.
                    val chipWeight = if (option == "Gemini") 0.8f else 1.1f
                    ProviderChip(
                        provider = option,
                        selected = provider == option,
                        enabled = !loading,
                        onClick = {
                            if (provider != option) {
                                provider = option
                                startFreshFor(option)
                            }
                        },
                        modifier = Modifier.weight(chipWeight)
                    )
                }
            }

            if (!hasKey && !hasContent) {
                KeyBanner(provider) {
                    keyDraft = ""
                    showAiSourceDialog = true
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
                                    actionLabel = if (errorNeedsKey) "Add API key" else if (msg.startsWith("API key rejected")) "Update API key" else "Try again"
                                ) {
                                    if (errorNeedsKey || msg.startsWith("API key rejected")) {
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
                                if (displayedText.isBlank()) {
                                    InsightSkeleton()
                                } else {
                                    // A half-typed **bold** would show raw asterisks; close it for now.
                                    val liveText = if (displayedText.split("**").size % 2 == 0) displayedText + "**" else displayedText
                                    val liveSections = remember(liveText) { parseInsight(liveText) }
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        liveSections.forEachIndexed { i, section ->
                                            key(i) { InsightCard(section) }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    itemsIndexed(thread.asReversed()) { index, exchange ->
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            QuestionBubble(exchange.question, provider)
                            exchange.answer?.let { ans ->
                                val sections = remember(ans) { parseInsight(ans) }
                                val readKey = "t${thread.size - 1 - index}"
                                ReadAloudSelection(onReadAloud = { text -> startReading("sel", text) }) {
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        sections.forEachIndexed { si, sec ->
                                            InsightCard(sec, highlighted = readingKey == readKey && readerSection == si)
                                        }
                                    }
                                }
                                val scoreLinks = remember(ans) { detectScoreLinks(ans) }
                                if (scoreLinks.isNotEmpty()) {
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        scoreLinks.forEach { link ->
                                            ScoreLinkChip(link.label) { openScore(link.id) }
                                        }
                                    }
                                }
                                AnswerActions(
                                    answer = ans,
                                    provider = provider,
                                    question = exchange.question,
                                    saved = savedAnswers.any { it.answer == ans && it.provider == provider },
                                    onToggleSave = { toggleSaveAnswer(exchange.question, ans) },
                                    onRegenerate = if (index == 0 && !loading) ({ regenerateLast() }) else null,
                                    onPdf = { exportPdf(exchange.question, provider, ans) },
                                    onDocx = { exportDocx(exchange.question, provider, ans) },
                                    reading = readingKey == readKey,
                                    onListen = { toggleListen(readKey, ans) }
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(visible = readingKey != null) {
                val sectionList = readerSections[0]
                ReaderBar(
                    position = (sectionList.indexOf(readerSection) + 1).coerceAtLeast(1),
                    total = sectionList.size,
                    paused = readerPaused,
                    rate = readerRate,
                    onPauseResume = { pauseResumeReading() },
                    onRate = { cycleReadingSpeed() },
                    onStop = { stopReading() }
                )
            }

            ComposerBar(
                provider = provider,
                query = query,
                onQueryChange = { query = it },
                placeholder = "Ask a Clinical Question",
                loading = loading,
                onSend = { runSearch() },
                onStop = { stopSearch() },
                onMic = { startVoiceInput() }
            )
        }

        // Full-screen viewer for one saved answer. Sits above the chat but is
        // completely separate from it: nothing in the open thread is touched.
        // Back (system gesture/button or the arrow) returns to the Saved Answers list.
        val viewing = viewSavedId?.let { id -> savedAnswers.firstOrNull { it.id == id } }
        // Keeps the last opened answer around so the slide-out animation has content.
        var lastViewed by remember { mutableStateOf<SavedAnswer?>(null) }
        LaunchedEffect(viewing) { if (viewing != null) lastViewed = viewing }
        val shown = viewing ?: lastViewed
        val viewerClipboard = LocalClipboardManager.current
        var confirmRemoveSaved by remember { mutableStateOf(false) }
        val closeViewer = {
            if (readingKey?.startsWith("s:") == true) stopReading()
            viewSavedId = null
            showSavedDialog = true
        }
        if (viewing != null) BackHandler { closeViewer() }

        AnimatedVisibility(
            visible = viewing != null,
            enter = slideInVertically(initialOffsetY = { it / 3 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it / 3 }) + fadeOut()
        ) {
            if (shown != null) {
                val viewerSections = remember(shown.id) { parseInsight(shown.answer) }
                if (confirmRemoveSaved) {
                    AlertDialog(
                        onDismissRequest = { confirmRemoveSaved = false },
                        title = { Text("Remove saved answer?") },
                        text = {
                            Text(
                                "This deletes the saved answer permanently.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                confirmRemoveSaved = false
                                val updated = savedAnswers.filterNot { it.id == shown.id }
                                savedAnswers = updated
                                writeSavedAnswers(context, updated)
                                closeViewer()
                            }) { Text("Remove") }
                        },
                        dismissButton = {
                            TextButton(onClick = { confirmRemoveSaved = false }) { Text("Cancel") }
                        }
                    )
                }
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .windowInsetsPadding(WindowInsets.navigationBars)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PremiumBackButton(onClick = { closeViewer() }, edgeInset = 8.dp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Saved Answer",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp
                                )
                                Text(
                                    "${shown.provider} · ${formatSessionTimestamp(shown.timestamp)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            val viewerReading = readingKey == "s:${shown.id}"
                            IconButton(onClick = { toggleListen("s:${shown.id}", shown.answer) }) {
                                Icon(
                                    if (viewerReading) Icons.Filled.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = if (viewerReading) "Stop reading" else "Read aloud",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item { QuestionBubble(shown.question, shown.provider) }
                            item {
                                ReadAloudSelection(onReadAloud = { text -> startReading("sel", text) }) {
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        viewerSections.forEachIndexed { si, sec ->
                                            InsightCard(sec, highlighted = readingKey == "s:${shown.id}" && readerSection == si)
                                        }
                                    }
                                }
                            }
                        }

                        AnimatedVisibility(visible = readingKey == "s:${shown.id}") {
                            val sectionList = readerSections[0]
                            ReaderBar(
                                position = (sectionList.indexOf(readerSection) + 1).coerceAtLeast(1),
                                total = sectionList.size,
                                paused = readerPaused,
                                rate = readerRate,
                                onPauseResume = { pauseResumeReading() },
                                onRate = { cycleReadingSpeed() },
                                onStop = { stopReading() }
                            )
                        }

                        Button(
                            onClick = {
                                // Starts a new chat that already contains this saved
                                // exchange, so the follow-up is answered with its context.
                                val target = if (shown.provider in providers) shown.provider else provider
                                provider = target
                                startFreshFor(target)
                                thread = listOf(ChatExchange(shown.question, answer = shown.answer))
                                viewSavedId = null
                                showSavedDialog = false
                            },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Ask follow-up")
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = {
                                viewerClipboard.setText(AnnotatedString(plainAnswer(shown.answer)))
                            }) {
                                Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Copy")
                            }
                            TextButton(onClick = {
                                val send = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "Q: ${shown.question}\n\n${plainAnswer(shown.answer)}")
                                }
                                context.startActivity(Intent.createChooser(send, "Share answer"))
                            }) {
                                Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Share")
                            }
                            // One Export button holds both formats so the row doesn't overflow.
                            var exportMenu by remember { mutableStateOf(false) }
                            Box {
                                TextButton(onClick = { exportMenu = true }) {
                                    Icon(Icons.Filled.Description, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Export")
                                }
                                DropdownMenu(expanded = exportMenu, onDismissRequest = { exportMenu = false }) {
                                    DropdownMenuItem(
                                        text = { Text("PDF") },
                                        onClick = {
                                            exportMenu = false
                                            exportPdf(shown.question, shown.provider, shown.answer)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Word (.docx)") },
                                        onClick = {
                                            exportMenu = false
                                            exportDocx(shown.question, shown.provider, shown.answer)
                                        }
                                    )
                                }
                            }
                            TextButton(
                                onClick = { confirmRemoveSaved = true },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Remove")
                            }
                        }
                    }
                }
            }
        }
    }
}
