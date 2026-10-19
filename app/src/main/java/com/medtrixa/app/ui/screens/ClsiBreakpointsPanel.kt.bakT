package com.medtrixa.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import com.medtrixa.app.ui.theme.dosingHeroBrush
import com.medtrixa.app.ui.theme.auraAccent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.data.repository.AntibioticBreakpointRepository
import com.medtrixa.app.data.repository.ClsiBreakpoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClsiBreakpointsPanel() {
    val context = LocalContext.current
    var loaded by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) { AntibioticBreakpointRepository.load(context) }
        loaded = true
    }
    var query by rememberSaveable { mutableStateOf("") }
    val results = remember(query, loaded) { AntibioticBreakpointRepository.search(query) }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(18.dp),
            placeholder = { Text("Search antibiotic") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true
        )
        if (results.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(if (loaded) "No matching antibiotics." else "Loading\u2026")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 28.dp)
            ) {
                item { BreakpointBanner() }
                items(results) { BreakpointCard(it) }
                item {
                    Text(
                        "Susceptible (S) breakpoints in \u00b5g/mL, as listed in the IDSA 2026 AMR Guidance (CLSI M100, 36th ed.). " +
                            "CLSI updates M100 annually; breakpoints may change in 2027. Always confirm with your laboratory's report.",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BreakpointBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(dosingHeroBrush())
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("CLSI 2026 \u00b7 SUSCEPTIBLE BREAKPOINTS", color = Color.White.copy(alpha = 0.85f),
                fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
            Text("MIC interpretation at a glance", color = Color.White, fontSize = 22.sp,
                lineHeight = 28.sp, fontWeight = FontWeight.Bold)
            Text("Enterobacterales, P. aeruginosa, CRAB and S. maltophilia. \u201c--\u201d means no breakpoint listed.",
                color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}

@Composable
private fun BreakpointCard(b: ClsiBreakpoint) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(b.antibiotic, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BpCell("Entero-\nbacterales", b.enterobacterales, auraAccent(0), Modifier.weight(1f))
                BpCell("P. aerug-\ninosa", b.pseudomonas, auraAccent(1), Modifier.weight(1f))
                BpCell("CRAB", b.crab, auraAccent(2), Modifier.weight(1f))
                BpCell("S. malto-\nphilia", b.stenotrophomonas, auraAccent(3), Modifier.weight(1f))
            }
            if (b.footnote.isNotBlank()) {
                Text(b.footnote, fontSize = 12.sp, lineHeight = 17.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun BpCell(label: String, value: String, accent: Color, modifier: Modifier) {
    val has = value.isNotBlank() && value != "--"
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (has) accent.copy(alpha = 0.12f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f))
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(label, fontSize = 9.sp, lineHeight = 11.sp, textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold, color = if (has) accent else MaterialTheme.colorScheme.onSurfaceVariant)
        Text(if (has) value else "--", fontSize = if (has) 14.sp else 13.sp,
            fontWeight = if (has) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            color = if (has) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
