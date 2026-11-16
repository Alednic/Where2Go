package org.umn.where2go.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import org.umn.where2go.data.DeviceLocationProvider

@Composable
fun PermissionScreen(
    onContinue: () -> Unit,
    onLocationFound: (Double, Double) -> Unit
) {
    val context = LocalContext.current
    var detecting by remember { mutableStateOf(false) }

    fun continueWithLocation() {
        detecting = true
        DeviceLocationProvider.requestCurrentLocation(context) { location ->
            location?.let { onLocationFound(it.latitude, it.longitude) }
            detecting = false
            onContinue()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) continueWithLocation() else onContinue()
    }

    Scaffold { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(120.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            Text(
                "Izinkan Akses Lokasi",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Where2Go memakai lokasimu untuk menghitung jarak ke tempat di Explore dan menampilkan posisimu di peta.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))
            if (detecting) {
                CircularProgressIndicator()
                Spacer(Modifier.height(12.dp))
                Text("Mendeteksi lokasi…", style = MaterialTheme.typography.bodyMedium)
            } else {
                Button(
                    onClick = {
                        val alreadyGranted =
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED

                        if (alreadyGranted) {
                            continueWithLocation()
                        } else {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                )
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Izinkan")
                }
                TextButton(onClick = onContinue) {
                    Text("Lewati untuk sekarang")
                }
            }
        }
    }
}
