package com.medtrixa.app.ui.screens.pulse

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medtrixa.app.pulse.PpgCapture
import com.medtrixa.app.pulse.PpgStatus

@Composable
fun PulseScreen() {
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    val capture = remember { PpgCapture(context.applicationContext) }
    val result by capture.result.collectAsStateWithLifecycle()
    val progress by capture.progress.collectAsStateWithLifecycle()

    var granted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted = it }
    LaunchedEffect(Unit) { if (!granted) launcher.launch(Manifest.permission.CAMERA) }
    DisposableEffect(granted) {
        if (granted) capture.start(owner)
        onDispose { capture.stop() }
    }

    val shown = minOf(result.filtered.size, 150)
    val from = result.filtered.size - shown
    val signal = result.filtered.copyOfRange(from, result.filtered.size)
    val peaks = result.peakIndices.filter { it >= from }.map { it - from }

    Column(
        Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Pulse Assessment", style = MaterialTheme.typography.titleLarge)
        Text(
            "Camera pulse estimate (research)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (!granted) {
            Text(
                "Camera permission is needed to measure your pulse.",
                textAlign = TextAlign.Center
            )
            return@Column
        }

        PulseWaveform(signal, peaks)

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SignalQualityRing(result.quality)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val bpm = result.bpm
                if (bpm != null) {
                    Text("$bpm", style = MaterialTheme.typography.displaySmall)
                    Text("bpm, estimate", style = MaterialTheme.typography.bodySmall)
                } else {
                    Text(
                        result.status.userMessage(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (result.status == PpgStatus.TOO_SHORT && progress > 0f) {
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
        }

        result.sdnnMs?.let { sd ->
            Text(
                "Pulse rate variability (research): SDNN ${sd.toInt()} ms, " +
                    "RMSSD ${(result.rmssdMs ?: 0.0).toInt()} ms",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.weight(1f))
        Text(
            "Not for diagnosis. This does not measure blood pressure. " +
                "Record blood pressure from a validated cuff.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
    }
}
