package com.medtrixa.app.ui.screens.drugtools

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.medtrixa.app.data.db.AppDatabase
import com.medtrixa.app.data.db.SearchHit

private fun crclBand(v: Double): Pair<String, Color> = when {
    v >= 90 -> "Normal or high (90 or above)" to Color(0xFF1F9D6B)
    v >= 60 -> "Mildly decreased (60-89)" to Color(0xFF6A9E1F)
    v >= 30 -> "Moderately decreased (30-59)" to Color(0xFFC27A00)
    v >= 15 -> "Severely decreased (15-29)" to Color(0xFFD9622B)
    else -> "Kidney failure range (below 15)" to Color(0xFFC0392B)
}

@Composable
fun RenalDoseAdjustmentScreen(onBackClick: () -> Unit) {
    BackHandler { onBackClick() }
    val context = LocalContext.current
    val dao = remember { AppDatabase.getInstance(context).drugDao() }
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var scr by remember { mutableStateOf("") }
    var isFemale by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<SearchHit>>(emptyList()) }
    var drug by remember { mutableStateOf<SearchHit?>(null) }
    var renalText by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(query) {
        val q = query.trim()
        suggestions = if (q.length < 2) emptyList() else dao.search(q)
    }
    LaunchedEffect(drug) {
        renalText = drug?.let { dao.getLabel(it.rxcui)?.renalHepaticDose }
    }

    val a = age.toDoubleOrNull()
    val w = weight.toDoubleOrNull()
    val c = scr.toDoubleOrNull()
    val crcl: Double? = if (a != null && w != null && c != null && a > 0 && w > 0 && c > 0) {
        val v = (140 - a) * w / (72 * c) * (if (isFemale) 0.85 else 1.0)
        if (v < 0) 0.0 else v
    } else null
    val decimal = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    val muted = MaterialTheme.colorScheme.onSurfaceVariant

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().imePadding(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(Modifier.width(4.dp))
                    Column {
                        Text("Renal Dose Adjustment", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Cockcroft-Gault CrCl and drug label dosing", style = MaterialTheme.typography.bodyMedium, color = muted)
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Age (years)") }, singleLine = true, keyboardOptions = decimal, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight (kg)") }, singleLine = true, keyboardOptions = decimal, modifier = Modifier.weight(1f))
                }
            }
            item {
                OutlinedTextField(value = scr, onValueChange = { scr = it }, label = { Text("Serum creatinine (mg/dL)") }, singleLine = true, keyboardOptions = decimal, modifier = Modifier.fillMaxWidth())
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Sex", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(4.dp))
                    SexPill("Male", !isFemale) { isFemale = false }
                    SexPill("Female", isFemale) { isFemale = true }
                }
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        if (crcl == null) {
                            Text("Enter age, weight and serum creatinine to calculate.", style = MaterialTheme.typography.bodyMedium, color = muted)
                        } else {
                            val (label, color) = crclBand(crcl)
                            Text("Creatinine clearance", style = MaterialTheme.typography.labelLarge)
                            Text("%.1f".format(crcl), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("mL/min", style = MaterialTheme.typography.bodyMedium, color = muted)
                            Spacer(Modifier.height(10.dp))
                            Text(
                                label,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                modifier = Modifier.background(color, RoundedCornerShape(20.dp)).padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                            if (crcl < 15) {
                                Spacer(Modifier.height(8.dp))
                                Text("Very low result. Check the entered values, especially serum creatinine and its unit.", style = MaterialTheme.typography.bodySmall, color = muted)
                            }
                        }
                    }
                }
            }
            item {
                Text("Drug", style = MaterialTheme.typography.labelLarge, color = muted)
            }
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) { Icon(Icons.Outlined.Close, contentDescription = "Clear") }
                        }
                    },
                    placeholder = { Text("Search a drug") }
                )
            }
            items(suggestions.take(6), key = { "s" + it.rxcui }) { hit ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { drug = hit; query = "" },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Text(hit.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(hit.drugClass ?: "Class not available", style = MaterialTheme.typography.bodySmall, color = muted)
                    }
                }
            }
            val sel = drug
            if (sel != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(sel.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text("Renal and hepatic dosing (drug label)", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            if (crcl != null) {
                                val (label, color) = crclBand(crcl)
                                Text("Your CrCl: ${"%.1f".format(crcl)} mL/min", fontWeight = FontWeight.SemiBold, color = color)
                                Text(label, style = MaterialTheme.typography.bodySmall, color = muted)
                                Spacer(Modifier.height(10.dp))
                            }
                            val t = renalText
                            Text(
                                if (t.isNullOrBlank()) "No renal or hepatic dosing text is stored for this drug." else t,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                item {
                    Text(
                        "Label text is shown as stored. Match your CrCl to the drug's own thresholds and verify with current prescribing information before dosing.",
                        style = MaterialTheme.typography.bodySmall,
                        color = muted
                    )
                }
            }
            item {
                Text(
                    "Cockcroft-Gault uses actual body weight and may be unreliable in obesity, extremes of age or muscle mass, and unstable kidney function.",
                    style = MaterialTheme.typography.bodySmall,
                    color = muted
                )
            }
        }
    }
}

@Composable
private fun SexPill(text: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
    Text(
        text,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .background(bg, RoundedCornerShape(10.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
