package com.medtrixa.app.ui.screens.pulse

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.view.WindowManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.rememberCoroutineScope
import com.medtrixa.app.pulse.PpgCapture
import com.medtrixa.app.pulse.PpgStatus
import com.medtrixa.app.ui.components.PremiumBackButton
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private val PulseGreen = Color(0xFF2EB67D)
private val PulseAmber = Color(0xFFE8A33D)
private val PulseRed = Color(0xFFD9534F)

// Clinical pulse counts use 30 s (x2) for a regular rhythm and a full 60 s when the
// rhythm is irregular. The same windows are used here. The 8 bpm spread limit is a
// practical app heuristic, not a published guideline value.
private const val FINAL_SECONDS = 30
private const val EXTENDED_SECONDS = 60
private const val MAX_SPREAD_BPM = 8

private data class FinalReading(
    val bpm: Int,
    val seconds: Int,
    val varied: Boolean,
    val quality: Float,
    val sdnnMs: Double?,
    val rmssdMs: Double?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PulseScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    DisposableEffect(view) {
        val window = (view.context as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
    val owner = LocalLifecycleOwner.current
    val capture = remember { PpgCapture(context.applicationContext) }

    val result by capture.result.collectAsStateWithLifecycle()
    val progress by capture.progress.collectAsStateWithLifecycle()
    val lensCount by capture.lensCount.collectAsStateWithLifecycle()
    val lensIndex by capture.lensIndex.collectAsStateWithLifecycle()

    var granted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted = it }

    // The camera and flashlight run only while a measurement is active.
    var measuring by remember { mutableStateOf(false) }
    var timedOut by remember { mutableStateOf(false) }

    // Bumped by "Try again" to restart capture and the wait timer
    var attempt by remember { mutableIntStateOf(0) }

    DisposableEffect(granted, measuring, attempt) {
        if (granted && measuring) capture.start(owner)
        onDispose { capture.stop() }
    }

    // Leaving the app (Home, recents, screen off) ends the measurement
    DisposableEffect(owner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) measuring = false
        }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer) }
    }

    // Seconds spent without a stable estimate; drives the "taking longer" hints
    var waitedSec by remember { mutableIntStateOf(0) }
    LaunchedEffect(granted, measuring, attempt, result.status) {
        if (!granted || !measuring || result.status == PpgStatus.OK) {
            waitedSec = 0
            return@LaunchedEffect
        }
        while (true) {
            delay(1000)
            waitedSec++
            if (waitedSec >= 60) {
                // Give up: turn the flashlight off instead of waiting forever
                timedOut = true
                measuring = false
                waitedSec = 0
                break
            }
        }
    }

    val primary = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val muted = MaterialTheme.colorScheme.onSurfaceVariant
    val surface = MaterialTheme.colorScheme.surface
    val cardShape = RoundedCornerShape(22.dp)

    // Stable reading: mean of per-second estimates over a 30 s window (60 s if they vary)
    var finalReading by remember { mutableStateOf<FinalReading?>(null) }
    var goodSec by remember { mutableIntStateOf(0) }
    val samples = remember { mutableStateListOf<Int>() }
    val latest by rememberUpdatedState(result)

    LaunchedEffect(measuring, attempt) {
        if (!measuring) return@LaunchedEffect
        var badSec = 0
        while (true) {
            delay(1000)
            val r = latest
            val b = r.bpm
            if (r.status == PpgStatus.OK && b != null) {
                badSec = 0
                samples.add(b)
                goodSec++
            } else {
                badSec++
                if (badSec >= 5) {
                    // Contact lost for too long: restart the window
                    samples.clear()
                    goodSec = 0
                }
            }
            if (goodSec >= FINAL_SECONDS) {
                val spread = (samples.maxOrNull() ?: 0) - (samples.minOrNull() ?: 0)
                val varied = spread > MAX_SPREAD_BPM
                if (!varied || goodSec >= EXTENDED_SECONDS) {
                    finalReading = FinalReading(
                        bpm = samples.average().roundToInt(),
                        seconds = goodSec,
                        varied = varied,
                        quality = r.quality,
                        sdnnMs = r.sdnnMs,
                        rmssdMs = r.rmssdMs
                    )
                    measuring = false
                    break
                }
            }
        }
    }

    val fr = finalReading
    val liveBpm: Int? = if (measuring && samples.size >= 3) {
        samples.takeLast(8).sorted().let { it[it.size / 2] }
    } else null
    val displayBpm: Int? = fr?.bpm ?: liveBpm
    val done = fr != null && !measuring

    // Capture now reports NO_FINGER reliably; show nothing instead of auto-scaled noise
    val hasContact = measuring && result.status != PpgStatus.NO_FINGER
    val shownQuality = when {
        !measuring -> fr?.quality ?: 0f
        hasContact -> result.quality
        else -> 0f
    }

    val shown = if (hasContact) minOf(result.filtered.size, 150) else 0
    val from = result.filtered.size - shown
    val signal = result.filtered.copyOfRange(from, result.filtered.size)
    val peaks = if (hasContact) {
        result.peakIndices.filter { it >= from }.map { it - from }
    } else emptyList()

    val qualityColor = when {
        done -> PulseGreen
        !measuring || result.status == PpgStatus.TOO_SHORT -> Color(0xFF94A3B8)
        !hasContact -> PulseRed
        result.quality >= 0.55f -> PulseGreen
        result.quality >= 0.35f -> PulseAmber
        else -> PulseRed
    }

    val qualityLabel = when {
        done -> "Reading Complete"
        !measuring -> "Ready"
        !hasContact -> "No Finger Detected"
        result.status == PpgStatus.TOO_SHORT -> "Measuring…"
        result.quality >= 0.55f -> "Good Signal"
        result.quality >= 0.35f -> "Fair Signal"
        else -> "Weak Signal"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        Text(
                            "Pulse Assessment",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Camera-Based Pulse Estimate",
                            style = MaterialTheme.typography.labelSmall,
                            color = muted
                        )
                    }
                },
                navigationIcon = {
                    PremiumBackButton(
                        onClick = onBackClick,
                        edgeInset = 12.dp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Your Pulse, Visualised",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        HeartbeatIcon(bpm = displayBpm)
                    }
                    Surface(
                        color = primary.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            "Research",
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 4.dp
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    "Cover the flash and the camera beside it. Two fingers side by side works best.",
                    style = MaterialTheme.typography.bodySmall,
                    color = muted
                )
            }

            if (!granted) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = cardShape,
                    colors = CardDefaults.cardColors(
                        containerColor = surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Camera Access Required",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Camera access is used to analyse fingertip colour changes. " +
                                "The estimate may be inaccurate and is not a clinical measurement.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = muted,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = {
                                launcher.launch(Manifest.permission.CAMERA)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Allow Camera Access")
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = cardShape,
                    colors = CardDefaults.cardColors(
                        containerColor = surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            when {
                                fr != null -> "ESTIMATED PULSE RATE · ${fr.seconds} SEC MEAN"
                                displayBpm != null -> "LIVE ESTIMATE"
                                else -> "PULSE RATE"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = muted,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.2.sp
                        )

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                displayBpm?.toString() ?: "—",
                                style = MaterialTheme.typography.displayLarge,
                                color = primary,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 64.sp
                            )
                            Text(
                                " bpm",
                                modifier = Modifier.padding(
                                    start = 5.dp,
                                    bottom = 10.dp
                                ),
                                style = MaterialTheme.typography.titleMedium,
                                color = muted
                            )
                        }

                        Surface(
                            color = qualityColor.copy(alpha = 0.10f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 7.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(7.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(
                                            qualityColor,
                                            CircleShape
                                        )
                                )
                                Text(
                                    qualityLabel,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = qualityColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        if (!measuring) {
                            Text(
                                when {
                                    fr != null ->
                                        "Mean of ${fr.seconds} seconds of stable signal." +
                                            if (fr.varied) {
                                                " Readings varied, so the window was extended to " +
                                                    "60 seconds. Rest and repeat if needed."
                                            } else {
                                                " Rest and measure again whenever you like."
                                            }
                                    timedOut ->
                                        "Couldn't get a stable reading. Check finger placement, " +
                                            "rest your hand and try again."
                                    else ->
                                        "Press Start, then cover the flash and the camera beside it. " +
                                            "The flashlight turns on when you start."
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = muted,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = {
                                    timedOut = false
                                    waitedSec = 0
                                    goodSec = 0
                                    samples.clear()
                                    finalReading = null
                                    measuring = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            ) {
                                Text(
                                    when {
                                        fr != null -> "Measure Again"
                                        timedOut -> "Try Again"
                                        else -> "Start"
                                    }
                                )
                            }
                        } else if (result.status != PpgStatus.OK) {
                            Text(
                                result.status.userMessage(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = muted,
                                textAlign = TextAlign.Center
                            )
                        }

                        if (measuring && result.status == PpgStatus.TOO_SHORT && progress > 0f) {
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp)
                            )
                        }

                        if (measuring && goodSec > 0) {
                            // Two clear phases so the bar never jumps backwards:
                            // 0-30 s, then (only if readings varied) a fresh 30 s extension.
                            val extending = goodSec >= FINAL_SECONDS
                            val phaseLen = EXTENDED_SECONDS - FINAL_SECONDS
                            val phaseSec = if (extending) goodSec - FINAL_SECONDS else goodSec
                            val phaseTotal = if (extending) phaseLen else FINAL_SECONDS
                            Text(
                                if (extending) {
                                    "Readings varied. Extending: $phaseSec / $phaseTotal sec"
                                } else {
                                    "Stabilising: $phaseSec / $phaseTotal sec"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = if (extending) PulseAmber else muted
                            )
                            LinearProgressIndicator(
                                progress = { (phaseSec.toFloat() / phaseTotal).coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (measuring) {
                            OutlinedButton(
                                onClick = { measuring = false },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            ) {
                                Text("Stop")
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = cardShape,
                    colors = CardDefaults.cardColors(
                        containerColor = surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Pulse Waveform",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                when {
                                    hasContact && signal.size >= 2 -> "LIVE SIGNAL"
                                    !measuring -> "READY"
                                    else -> "WAITING FOR CONTACT"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF38BDF8),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        PulseWaveform(
                            signal = signal,
                            peaks = peaks,
                            idleMessage = if (measuring) {
                                "Place your fingertip over the flash and the camera next to it"
                            } else if (fr != null) {
                                "Measurement complete"
                            } else {
                                "Press Start when you are ready"
                            }
                        )

                        Text(
                            "The trace represents camera-derived signal data; " +
                                "it is not an ECG.",
                            style = MaterialTheme.typography.bodySmall,
                            color = muted
                        )
                    }
                }

                if (measuring && result.status != PpgStatus.OK && waitedSec >= 10) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = PulseAmber.copy(alpha = 0.12f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Taking Longer than Usual",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Phones have several rear lenses and the flash sits beside them. " +
                                    "Cover the flash and the lens next to it together, using two " +
                                    "fingers side by side. Rest your hand, press gently and stay still.",
                                modifier = Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.bodySmall,
                                color = muted,
                                textAlign = TextAlign.Justify
                            )
                            if (lensCount > 1) {
                                OutlinedButton(
                                    onClick = {
                                        waitedSec = 0
                                        capture.nextLens()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Try Another Lens (${lensIndex + 1} of $lensCount)")
                                }
                            }
                            if (waitedSec >= 30) {
                                Button(
                                    onClick = { attempt++ },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Try Again")
                                }
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = cardShape,
                    colors = CardDefaults.cardColors(
                        containerColor = surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SignalQualityRing(shownQuality)

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                "Signal Quality",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Quality Score: ${(shownQuality.coerceIn(0f, 1f) * 100).toInt()} / 100",
                                style = MaterialTheme.typography.bodyMedium,
                                color = muted
                            )
                            Text(
                                "For a steadier signal, rest your hand and apply gentle, consistent contact.",
                                style = MaterialTheme.typography.bodySmall,
                                color = muted
                            )
                        }
                    }
                }

                if (
                    fr != null &&
                    fr.sdnnMs != null &&
                    fr.rmssdMs != null
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Experimental Variability Metrics",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                VariabilityMetric(
                                    title = "SDNN",
                                    value = "${fr.sdnnMs!!.toInt()} ms",
                                    modifier = Modifier.weight(1f)
                                )
                                VariabilityMetric(
                                    title = "RMSSD",
                                    value = "${fr.rmssdMs!!.toInt()} ms",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Text(
                                "Experimental estimates only. Do not use these values to diagnose or exclude disease.",
                                style = MaterialTheme.typography.bodySmall,
                                color = muted
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                        alpha = 0.65f
                    )
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        "Important Clinical Limitation",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "This experimental feature is not validated for clinical decisions. " +
                            "It does not measure blood pressure, diagnose arrhythmias, " +
                            "or replace a validated pulse oximeter or clinical assessment. " +
                            "Use a validated cuff to measure blood pressure.",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodySmall,
                        color = muted,
                        textAlign = TextAlign.Justify
                    )
                }
            }

            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun VariabilityMetric(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                RoundedCornerShape(14.dp)
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun HeartbeatIcon(bpm: Int?, modifier: Modifier = Modifier) {
    val color = Color(0xFFE24B4A)
    val beat = remember { Animatable(1f) }
    val current by rememberUpdatedState(bpm)

    LaunchedEffect(bpm != null) {
        if (bpm == null) {
            beat.snapTo(1f)
            return@LaunchedEffect
        }
        while (true) {
            val period = 60_000f / (current ?: 72).coerceIn(40, 200)
            beat.animateTo(1.25f, tween((period * 0.12f).toInt()))
            beat.animateTo(1f, tween((period * 0.14f).toInt()))
            beat.animateTo(1.15f, tween((period * 0.10f).toInt()))
            beat.animateTo(1f, tween((period * 0.18f).toInt()))
            delay((period * 0.46f).toLong())
        }
    }

    val heartPath = remember {
        PathParser().parsePathString(
            "M19.5 13.572l-7.5 7.428l-2.896 -2.868m-6.117 -8.104a5 5 0 0 1 9.013 -3.022a5 5 0 1 1 7.5 6.572"
        ).toPath()
    }
    val linePath = remember {
        PathParser().parsePathString("M3 13h2l2 3l2 -6l1 3h3").toPath()
    }

    Canvas(
        modifier
            .size(22.dp)
            .graphicsLayer {
                scaleX = beat.value
                scaleY = beat.value
            }
    ) {
        val k = size.width / 24f
        val stroke = Stroke(1.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        withTransform({ scale(k, k, Offset.Zero) }) {
            drawPath(heartPath, color, style = stroke)
            drawPath(linePath, color, style = stroke)
        }
    }
}
