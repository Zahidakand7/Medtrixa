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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

private val PulseGreen = Color(0xFF2EB67D)
private val PulseAmber = Color(0xFFE8A33D)
private val PulseRed = Color(0xFFD9534F)

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

    // Capture now reports NO_FINGER reliably; show nothing instead of auto-scaled noise
    val hasContact = measuring && result.status != PpgStatus.NO_FINGER

    val shown = if (hasContact) minOf(result.filtered.size, 150) else 0
    val from = result.filtered.size - shown
    val signal = result.filtered.copyOfRange(from, result.filtered.size)
    val peaks = if (hasContact) {
        result.peakIndices.filter { it >= from }.map { it - from }
    } else emptyList()

    val qualityColor = when {
        !measuring || result.status == PpgStatus.TOO_SHORT -> Color(0xFF94A3B8)
        !hasContact -> PulseRed
        result.quality >= 0.55f -> PulseGreen
        result.quality >= 0.35f -> PulseAmber
        else -> PulseRed
    }

    val qualityLabel = when {
        !measuring -> "Ready"
        !hasContact -> "No Finger Detected"
        result.status == PpgStatus.TOO_SHORT -> "Measuring…"
        result.quality >= 0.55f -> "Good signal"
        result.quality >= 0.35f -> "Fair signal"
        else -> "Weak signal"
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
                            "Camera-based pulse estimate",
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Your Pulse, Visualised",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
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
                            "Camera access required",
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
                            Text("Allow camera access")
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
                            if (result.status == PpgStatus.OK && result.bpm != null) {
                                "ESTIMATED PULSE RATE"
                            } else {
                                "PULSE RATE"
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
                                if (result.status == PpgStatus.OK) {
                                    result.bpm?.toString() ?: "—"
                                } else {
                                    "—"
                                },
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
                                if (timedOut) {
                                    "Couldn't get a stable reading. Check finger placement, " +
                                        "rest your hand and try again."
                                } else {
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
                                    measuring = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            ) {
                                Text(if (timedOut) "Try Again" else "Start")
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
                                "Pulse waveform",
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
                                    Text("Try another lens (${lensIndex + 1} of $lensCount)")
                                }
                            }
                            if (waitedSec >= 30) {
                                Button(
                                    onClick = { attempt++ },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Try again")
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
                        SignalQualityRing(if (hasContact) result.quality else 0f)

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                "Signal quality",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Quality score: ${((if (hasContact) result.quality else 0f).coerceIn(0f, 1f) * 100).toInt()} / 100",
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
                    result.status == PpgStatus.OK &&
                    result.sdnnMs != null &&
                    result.rmssdMs != null
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
                                "Experimental variability metrics",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                VariabilityMetric(
                                    title = "SDNN",
                                    value = "${result.sdnnMs!!.toInt()} ms",
                                    modifier = Modifier.weight(1f)
                                )
                                VariabilityMetric(
                                    title = "RMSSD",
                                    value = "${result.rmssdMs!!.toInt()} ms",
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
