package org.umn.where2go.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

private data class OSMPlace(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)

@Composable
fun MapScreen(bottomBar: @Composable () -> Unit) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val mapView = remember { createOpenStreetMapView(context) }
    val locationOverlay = remember(mapView) {
        MyLocationNewOverlay(GpsMyLocationProvider(context), mapView).apply {
            setDrawAccuracyEnabled(true)
        }
    }

    var query by rememberSaveable { mutableStateOf("") }
    var results by remember { mutableStateOf<List<OSMPlace>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var searchMessage by remember { mutableStateOf<String?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            locationOverlay.enableMyLocation()
            locationOverlay.enableFollowLocation()
        } else {
            searchMessage = "Izin lokasi tidak diberikan."
        }
    }

    DisposableEffect(mapView, locationOverlay) {
        mapView.overlays.add(locationOverlay)
        mapView.onResume()
        onDispose {
            locationOverlay.disableFollowLocation()
            locationOverlay.disableMyLocation()
            mapView.onPause()
            mapView.onDetach()
        }
    }

    LaunchedEffect(results) {
        val oldMarkers = mapView.overlays.filterIsInstance<Marker>()
        mapView.overlays.removeAll(oldMarkers)
        results.forEach { place ->
            mapView.overlays.add(
                Marker(mapView).apply {
                    position = GeoPoint(place.latitude, place.longitude)
                    title = place.name
                    snippet = place.address
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
            )
        }
        if (results.isNotEmpty()) {
            val first = results.first()
            mapView.controller.animateTo(GeoPoint(first.latitude, first.longitude))
            mapView.controller.setZoom(16.0)
        }
        mapView.invalidate()
    }

    fun submitSearch() {
        val searchText = query.trim()
        if (searchText.isBlank() || isSearching) return

        keyboardController?.hide()
        isSearching = true
        searchMessage = null
        scope.launch {
            try {
                results = withContext(Dispatchers.IO) {
                    searchOpenStreetMap(searchText)
                }
                searchMessage = if (results.isEmpty()) "Tempat tidak ditemukan." else null
            } catch (_: Exception) {
                searchMessage = "Pencarian gagal. Periksa koneksi internet lalu coba lagi."
                results = emptyList()
            } finally {
                isSearching = false
            }
        }
    }

    fun showUserLocation() {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            locationOverlay.enableMyLocation()
            locationOverlay.enableFollowLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = bottomBar
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AndroidView(
                factory = { mapView },
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp)
                            .padding(start = 16.dp, end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Cari tempat",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        BasicTextField(
                            value = query,
                            onValueChange = { query = it },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { submitSearch() }),
                            decorationBox = { innerTextField ->
                                Box {
                                    if (query.isBlank()) {
                                        Text(
                                            text = "Cari tempat atau alamat",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                        if (isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(12.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            IconButton(onClick = { submitSearch() }) {
                                Icon(Icons.Filled.Search, contentDescription = "Mulai pencarian")
                            }
                        }
                        IconButton(onClick = { showUserLocation() }) {
                            Icon(
                                Icons.Filled.MyLocation,
                                contentDescription = "Tampilkan lokasi saya",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                if (searchMessage != null || results.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 6.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 280.dp)
                                .padding(12.dp)
                        ) {
                            searchMessage?.let { message ->
                                Text(
                                    text = message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            results.take(5).forEach { place ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            mapView.controller.animateTo(
                                                GeoPoint(place.latitude, place.longitude)
                                            )
                                            mapView.controller.setZoom(17.0)
                                        }
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Filled.Place,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 10.dp)
                                    ) {
                                        Text(
                                            text = place.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = place.address,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 2
                                        )
                                    }
                                    IconButton(onClick = {
                                        results = emptyList()
                                        searchMessage = null
                                    }) {
                                        Icon(Icons.Filled.Close, contentDescription = "Tutup hasil")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp),
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            ) {
                Text(
                    text = "© OpenStreetMap contributors",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun createOpenStreetMapView(context: Context): MapView {
    val cacheDirectory = File(context.cacheDir, "osmdroid")
    val tileDirectory = File(cacheDirectory, "tiles")
    cacheDirectory.mkdirs()
    tileDirectory.mkdirs()

    Configuration.getInstance().apply {
        userAgentValue = "${context.packageName}/1.0 (Where2Go Android app)"
        osmdroidBasePath = cacheDirectory
        osmdroidTileCache = tileDirectory
    }

    return MapView(context).apply {
        setTileSource(TileSourceFactory.MAPNIK)
        setMultiTouchControls(true)
        setBuiltInZoomControls(false)
        controller.setZoom(14.0)
        controller.setCenter(GeoPoint(-6.256, 106.618))
    }
}

private var lastPhotonRequestAt = 0L

private suspend fun searchOpenStreetMap(query: String): List<OSMPlace> {
    val waitMs = 1000L - (System.currentTimeMillis() - lastPhotonRequestAt)
    if (waitMs > 0) delay(waitMs)
    lastPhotonRequestAt = System.currentTimeMillis()

    val encodedQuery = URLEncoder.encode(query, Charsets.UTF_8.name())
    val url = URL("https://photon.komoot.io/api/?q=$encodedQuery&limit=8")
    val connection = (url.openConnection() as HttpURLConnection).apply {
        connectTimeout = 10_000
        readTimeout = 10_000
        requestMethod = "GET"
        setRequestProperty("User-Agent", "Where2Go/1.0 (org.umn.where2go)")
        setRequestProperty("Accept", "application/geo+json, application/json")
    }

    return try {
        val response = connection.inputStream.bufferedReader().use { it.readText() }
        val features = JSONObject(response).optJSONArray("features") ?: return emptyList()
        buildList {
            for (index in 0 until features.length()) {
                val feature = features.optJSONObject(index) ?: continue
                val geometry = feature.optJSONObject("geometry") ?: continue
                val coordinates = geometry.optJSONArray("coordinates") ?: continue
                if (coordinates.length() < 2) continue

                val properties = feature.optJSONObject("properties") ?: JSONObject()
                val name = properties.optString("name").ifBlank {
                    properties.optString("street").ifBlank {
                        properties.optString("city").ifBlank { "Tempat tanpa nama" }
                    }
                }
                val address = listOf(
                    properties.optString("street"),
                    properties.optString("city"),
                    properties.optString("state"),
                    properties.optString("country")
                ).filter { it.isNotBlank() && it != "null" }
                    .distinct()
                    .joinToString(", ")

                add(
                    OSMPlace(
                        name = name,
                        address = address,
                        longitude = coordinates.optDouble(0),
                        latitude = coordinates.optDouble(1)
                    )
                )
            }
        }
    } finally {
        connection.disconnect()
    }
}
