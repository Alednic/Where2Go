package org.umn.where2go.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun PermissionScreen(onContinue: () -> Unit) {
    var detecting by remember { mutableStateOf(false) }
    LaunchedEffect(detecting) {
        if (detecting) { delay(1500); onContinue() }
    }
    Scaffold { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.primaryContainer, modifier
                = Modifier.size(120.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.LocationOn, null, Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(Modifier.height(24.dp))
            Text("Izinkan Akses Lokasi"
                , style =
                    MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text("Where2Go butuh lokasimu untuk menampilkan tempat tenang terdekat.",
                style = MaterialTheme.typography.bodyLarge, textAlign =
                    TextAlign.Center)
            Spacer(Modifier.height(32.dp))
            if (detecting) {
                CircularProgressIndicator()
                Spacer(Modifier.height(12.dp))
                Text("Mendeteksi lokasi..."
                    , style =
                        MaterialTheme.typography.bodyMedium)
            } else {
                Button(onClick = { detecting = true }, modifier =
                    Modifier.fillMaxWidth()) { Text("Izinkan") }
                TextButton(onClick = onContinue) { Text("Lewati untuk sekarang") }
                }
            }
        }
    }