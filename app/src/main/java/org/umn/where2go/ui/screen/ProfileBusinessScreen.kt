package org.umn.where2go.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.viewinterop.AndroidView
import coil3.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.umn.where2go.data.Where2GoBackend
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(bottomBar: @Composable () -> Unit, onSignedOut: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var profile by remember { mutableStateOf<Where2GoBackend.Profile?>(null) }
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var avatarUrl by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var preferences by remember { mutableStateOf(setOf<String>()) }
    var loading by remember { mutableStateOf(true) }
    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var showBusiness by remember { mutableStateOf(false) }
    val avatarPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { photoUri = it }
    LaunchedEffect(Unit) {
        try {
            val p = withContext(Dispatchers.IO) { Where2GoBackend.profile(context) }
            profile = p; name = p?.name.orEmpty(); username = p?.username.orEmpty(); bio = p?.bio.orEmpty()
            avatarUrl = p?.avatarUrl.orEmpty()
            preferences = withContext(Dispatchers.IO) { Where2GoBackend.preferences(context) }
        } catch (e: Exception) { message = e.message }
        loading = false
    }
    if (showBusiness) {
        BusinessRegistrationScreen(bottomBar = bottomBar, onBack = { showBusiness = false })
        return
    }
    Scaffold(containerColor = MaterialTheme.colorScheme.background, bottomBar = bottomBar) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                Text("Profil", style = MaterialTheme.typography.displayMedium)
                Text("Kelola akun dan tempat usahamu", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (loading) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
            item {
                Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                    Column(Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        if (photoUri != null || avatarUrl.isNotBlank()) {
                            AsyncImage(
                                model = photoUri ?: avatarUrl,
                                contentDescription = "Foto profil",
                                modifier = Modifier.size(88.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Person, null, Modifier.size(68.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                        Text(profile?.email.orEmpty(), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            item {
                OutlinedButton(onClick = { avatarPicker.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (photoUri == null) "Pilih Foto Profil" else "Ganti Foto Profil")
                }
            }
            item {
                Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text("Poin Where2Go", fontWeight = FontWeight.SemiBold)
                        Text("${profile?.points ?: 0} poin", fontWeight = FontWeight.Bold)
                    }
                }
            }
            item { OutlinedTextField(name, { name = it }, label = { Text("Nama tampilan") }, modifier = Modifier.fillMaxWidth(), singleLine = true) }
            item { OutlinedTextField(username, { username = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth(), singleLine = true) }
            item { OutlinedTextField(bio, { bio = it }, label = { Text("Bio") }, modifier = Modifier.fillMaxWidth(), minLines = 3) }
            item { Text("Vibe dan preferensi", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
            item {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("Date", "Ngopi", "Ramai", "Kreatif", "Tenang", "Nyaman", "Outdoor", "Nightlife", "Hangout",
                        "Premium", "Affordable", "Cocok Nugas", "Colokan", "Wi-Fi", "Nongkrong").forEach { tag ->
                        FilterChip(selected = tag in preferences, onClick = {
                            preferences = if (tag in preferences) preferences - tag else preferences + tag
                        }, label = { Text(tag) })
                    }
                }
            }
            item {
                Button(enabled = !busy, onClick = {
                    scope.launch { busy = true; message = null
                        try {
                            avatarUrl = withContext(Dispatchers.IO) {
                                val uploadedAvatar = photoUri?.let { Where2GoBackend.uploadProfilePhoto(context, it) } ?: avatarUrl
                                Where2GoBackend.saveProfile(context, name, username, bio, uploadedAvatar)
                                Where2GoBackend.savePreferences(context, preferences)
                                uploadedAvatar
                            }
                            photoUri = null
                            message = "Profil berhasil disimpan."
                        }
                        catch (e: Exception) { message = e.message ?: "Profil gagal disimpan." }
                        busy = false
                    }
                }, modifier = Modifier.fillMaxWidth()) { Text(if (busy) "Menyimpan…" else "Simpan Profil") }
            }
            item {
                OutlinedButton(onClick = { showBusiness = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.AddBusiness, null); Spacer(Modifier.width(8.dp)); Text("Daftarkan Tempat Usaha")
                }
            }
            if (profile?.role == "admin") item { AdminReviewPanel(context) }
            item {
                TextButton(onClick = { scope.launch { withContext(Dispatchers.IO) { Where2GoBackend.signOut(context) }; onSignedOut() } },
                    modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Logout, null); Spacer(Modifier.width(8.dp)); Text("Keluar")
                }
            }
            message?.let { msg -> item { Text(msg, color = if (msg.contains("berhasil")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error) } }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun BusinessRegistrationScreen(bottomBar: @Composable () -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var opening by remember { mutableStateOf("07:00") }
    var closing by remember { mutableStateOf("22:00") }
    var minOrder by remember { mutableStateOf("0") }
    var facilities by remember { mutableStateOf(setOf<String>()) }
    var mapTags by remember { mutableStateOf(setOf<String>()) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Kafe") }
    var expanded by remember { mutableStateOf(false) }
    var showLocationPicker by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var listRefresh by remember { mutableIntStateOf(0) }
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { photoUri = it }
    if (showLocationPicker) {
        OpenStreetMapLocationPicker(
            initialLatitude = latitude.toDoubleOrNull(),
            initialLongitude = longitude.toDoubleOrNull(),
            onBack = { showLocationPicker = false },
            onLocationSelected = { lat, lon, foundAddress ->
                latitude = lat.toString()
                longitude = lon.toString()
                if (foundAddress.isNotBlank()) address = foundAddress
                showLocationPicker = false
            }
        )
        return
    }
    Scaffold(containerColor = MaterialTheme.colorScheme.background, bottomBar = bottomBar,
        topBar = { TopAppBar(title = { Text("Daftarkan Tempat") }, navigationIcon = {
            TextButton(onClick = onBack) { Text("Kembali") }
        }) }) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Text("Daftarkan tempat usahamu", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
            item { Text("Tempat akan ditinjau sebelum tampil di Where2Go.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            item { OutlinedTextField(name, { name = it }, label = { Text("Nama tempat") }, modifier = Modifier.fillMaxWidth(), singleLine = true) }
            item {
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(category, {}, readOnly = true, label = { Text("Kategori") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }, modifier = Modifier.fillMaxWidth().menuAnchor())
                    ExposedDropdownMenu(expanded, { expanded = false }) {
                        listOf("Kafe", "Restoran", "Ruang kerja", "Tempat rekreasi", "Lainnya").forEach { option ->
                            DropdownMenuItem(text = { Text(option) }, onClick = { category = option; expanded = false })
                        }
                    }
                }
            }
            item { OutlinedTextField(address, { address = it }, label = { Text("Alamat lengkap") }, modifier = Modifier.fillMaxWidth(), minLines = 2) }
            item { OutlinedTextField(phone, { phone = it }, label = { Text("Nomor telepon") }, modifier = Modifier.fillMaxWidth(), singleLine = true) }
            item { Text("Jam Operasional", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(opening, { opening = it }, label = { Text("Buka") }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(closing, { closing = it }, label = { Text("Tutup") }, modifier = Modifier.weight(1f), singleLine = true)
                }
            }
            item { Text("Fasilitas yang tersedia", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
            item {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    listOf("Wi-Fi", "Colokan", "AC", "Parkir", "No Rokok", "Pet Friendly").forEach { facility ->
                        FilterChip(selected = facility in facilities, onClick = {
                            facilities = if (facility in facilities) facilities - facility else facilities + facility
                        }, label = { Text(facility) })
                    }
                }
            }
            item { Text("Tag di Map dan Explore", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
            item {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    listOf("Tenang", "Nugas", "Ramai", "Budget", "Date", "Nongkrong", "Outdoor")
                        .forEach { tag ->
                            FilterChip(selected = tag in mapTags, onClick = {
                                mapTags = if (tag in mapTags) mapTags - tag else mapTags + tag
                            }, label = { Text(tag) })
                        }
                }
            }
            item { OutlinedTextField(minOrder, { minOrder = it.filter(Char::isDigit) }, label = { Text("Minimum order (Rp)") }, modifier = Modifier.fillMaxWidth(), singleLine = true) }
            item { OutlinedTextField(description, { description = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth(), minLines = 4) }
            item { Text("Foto Tempat", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
            item {
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    photoUri?.let { uri -> AsyncImage(model = uri, contentDescription = "Foto tempat",
                        modifier = Modifier.fillMaxWidth().height(180.dp), contentScale = ContentScale.Crop) }
                    OutlinedButton(onClick = { photoPicker.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (photoUri == null) "Pilih foto JPG/PNG (maks. 5 MB)" else "Ganti Foto")
                    }
                }
            }
            item { Text("Lokasi tempat", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold) }
            item {
                OutlinedButton(onClick = { showLocationPicker = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (latitude.isNotBlank() && longitude.isNotBlank()) "Ubah Lokasi di Peta" else "Pilih Lokasi di Peta")
                }
            }
            if (latitude.isNotBlank() && longitude.isNotBlank()) {
                item { Text("Titik terpilih: $latitude, $longitude", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            } else {
                item { Text("Ketuk titik tempat usahamu di peta. Lokasi akan disimpan bersama pengajuan.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            item {
                Button(enabled = !busy && name.isNotBlank() && address.isNotBlank(), onClick = {
                    scope.launch { busy = true; message = null
                        val lat = latitude.toDoubleOrNull()
                        val lon = longitude.toDoubleOrNull()
                        try {
                            require((latitude.isBlank() && longitude.isBlank()) || (lat != null && lon != null)) {
                                "Isi latitude dan longitude dengan angka yang benar, atau kosongkan keduanya."
                            }
                            require(lat == null || lat in -90.0..90.0) { "Latitude harus di antara -90 dan 90." }
                            require(lon == null || lon in -180.0..180.0) { "Longitude harus di antara -180 dan 180." }
                            val parsedMinOrder = minOrder.toIntOrNull() ?: 0
                            require(opening.matches(Regex("\\d{2}:\\d{2}")) && closing.matches(Regex("\\d{2}:\\d{2}"))) {
                                "Jam operasional harus memakai format HH:mm."
                            }
                            val uploadedPhoto = photoUri?.let { uri ->
                                withContext(Dispatchers.IO) { Where2GoBackend.uploadBusinessPhoto(context, uri) }
                            }
                            withContext(Dispatchers.IO) { Where2GoBackend.submitBusiness(context, name, category, address,
                                phone, description, "$opening-$closing", facilities, mapTags, parsedMinOrder, lat, lon, uploadedPhoto) }
                            message = "Pendaftaran terkirim dan menunggu peninjauan."; listRefresh++
                        } catch (e: Exception) { message = e.message ?: "Pendaftaran gagal." }
                        busy = false
                    }
                }, modifier = Modifier.fillMaxWidth()) { Text(if (busy) "Mengirim…" else "Kirim Pendaftaran") }
            }
            message?.let { item { Text(it, color = if (it.startsWith("Pendaftaran terkirim")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error) } }
            item { Text("Tempat Usahaku", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
            item { MyBusinessList(context, listRefresh) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OpenStreetMapLocationPicker(
    initialLatitude: Double?,
    initialLongitude: Double?,
    onBack: () -> Unit,
    onLocationSelected: (Double, Double, String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val mapView = remember(context) { createBusinessLocationMap(context) }
    var selectedPoint by remember {
        mutableStateOf(
            if (initialLatitude != null && initialLongitude != null) {
                GeoPoint(initialLatitude, initialLongitude)
            } else null
        )
    }
    var query by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<BusinessMapResult>>(emptyList()) }
    var searching by remember { mutableStateOf(false) }
    var selectedAddress by remember { mutableStateOf("") }
    val currentOnLocationSelected by rememberUpdatedState(onLocationSelected)

    DisposableEffect(mapView) {
        val tapOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(point: GeoPoint?): Boolean {
                point ?: return false
                selectedPoint = GeoPoint(point.latitude, point.longitude)
                selectedAddress = ""
                return true
            }

            override fun longPressHelper(point: GeoPoint?): Boolean {
                point ?: return false
                selectedPoint = GeoPoint(point.latitude, point.longitude)
                selectedAddress = ""
                return true
            }
        })
        mapView.overlays.add(tapOverlay)
        mapView.onResume()
        onDispose {
            mapView.onPause()
            mapView.onDetach()
        }
    }

    LaunchedEffect(mapView, selectedPoint) {
        mapView.overlays.removeAll(
            mapView.overlays.filterIsInstance<Marker>().filter { it.title == "Lokasi terpilih" }
        )
        selectedPoint?.let { point ->
            mapView.overlays.add(Marker(mapView).apply {
                position = point
                title = "Lokasi terpilih"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            })
            mapView.controller.animateTo(point)
        }
        mapView.invalidate()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pilih Lokasi") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Kembali") } }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())
            Surface(
                modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth().padding(12.dp),
                shape = RoundedCornerShape(18.dp),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Cari nama tempat atau alamat") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = {
                                if (query.isNotBlank() && !searching) {
                                    searching = true
                                    scope.launch {
                                        searchResults = runCatching {
                                            withContext(Dispatchers.IO) { searchBusinessMap(query) }
                                        }.getOrDefault(emptyList())
                                        searching = false
                                    }
                                }
                            }),
                            trailingIcon = {
                                IconButton(onClick = {
                                    if (query.isNotBlank() && !searching) {
                                        searching = true
                                        scope.launch {
                                            searchResults = runCatching {
                                                withContext(Dispatchers.IO) { searchBusinessMap(query) }
                                            }.getOrDefault(emptyList())
                                            searching = false
                                        }
                                    }
                                }) {
                                    if (searching) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                                    else Icon(Icons.Filled.Search, contentDescription = "Cari lokasi")
                                }
                            }
                        )
                    }
                    searchResults.take(5).forEach { result ->
                        TextButton(onClick = {
                            selectedPoint = GeoPoint(result.latitude, result.longitude)
                            selectedAddress = result.address
                            query = result.name
                            searchResults = emptyList()
                            mapView.controller.animateTo(GeoPoint(result.latitude, result.longitude))
                            mapView.controller.setZoom(17.0)
                        }, modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.fillMaxWidth()) {
                                Text(result.name, fontWeight = FontWeight.SemiBold)
                                Text(result.address, style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        selectedPoint?.let {
                            if (selectedAddress.isNotBlank()) selectedAddress
                            else "Lokasi: %.6f, %.6f".format(it.latitude, it.longitude)
                        } ?: "Ketuk peta untuk memilih titik tempat",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text("Peta © OpenStreetMap contributors", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Button(
                        enabled = selectedPoint != null,
                        onClick = {
                            selectedPoint?.let { currentOnLocationSelected(it.latitude, it.longitude, selectedAddress) }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Gunakan Lokasi Ini") }
                }
            }
        }
    }
}

private data class BusinessMapResult(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)

private fun searchBusinessMap(query: String): List<BusinessMapResult> {
    val encoded = URLEncoder.encode(query, Charsets.UTF_8.name())
    val connection = (URL("https://photon.komoot.io/api/?q=$encoded&limit=6").openConnection() as HttpURLConnection).apply {
        connectTimeout = 10_000
        readTimeout = 10_000
        requestMethod = "GET"
        setRequestProperty("User-Agent", "Where2Go/1.0 (Android app)")
        setRequestProperty("Accept", "application/geo+json, application/json")
    }
    return try {
        val body = connection.inputStream.bufferedReader().use { it.readText() }
        val features = JSONObject(body).optJSONArray("features") ?: return emptyList()
        buildList {
            for (index in 0 until features.length()) {
                val feature = features.optJSONObject(index) ?: continue
                val coordinates = feature.optJSONObject("geometry")?.optJSONArray("coordinates") ?: continue
                if (coordinates.length() < 2) continue
                val properties = feature.optJSONObject("properties") ?: JSONObject()
                val name = properties.optString("name").ifBlank {
                    properties.optString("street").ifBlank { properties.optString("city", "Lokasi") }
                }
                val address = listOf("street", "housenumber", "district", "city", "state")
                    .map { properties.optString(it) }
                    .filter { it.isNotBlank() && it != "null" }
                    .distinct().joinToString(", ").ifBlank { name }
                add(BusinessMapResult(name, address, coordinates.optDouble(1), coordinates.optDouble(0)))
            }
        }
    } finally {
        connection.disconnect()
    }
}

private fun createBusinessLocationMap(context: android.content.Context): MapView {
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
        setBuiltInZoomControls(true)
        controller.setZoom(15.0)
        controller.setCenter(GeoPoint(-6.256, 106.618))
    }
}

@Composable
private fun MyBusinessList(context: android.content.Context, refreshKey: Int) {
    var items by remember { mutableStateOf<List<Where2GoBackend.PlaceSubmission>>(emptyList()) }
    var stats by remember { mutableStateOf<List<Where2GoBackend.BusinessStats>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(refreshKey) {
        try { items = withContext(Dispatchers.IO) { Where2GoBackend.myBusinesses(context) } }
        catch (e: Exception) { error = e.message }
        runCatching { withContext(Dispatchers.IO) { Where2GoBackend.businessStats(context) } }
            .onSuccess { stats = it }.onFailure { if (error == null) error = it.message }
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (stats.isNotEmpty()) {
            Text("Ringkasan Usaha", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            stats.forEach { stat ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(stat.name, fontWeight = FontWeight.Bold)
                        Text("${stat.checkIns} check-in · Vibe terbanyak: ${stat.leadingVibe}")
                    }
                }
            }
        }
        when {
            error != null -> Text(error!!, color = MaterialTheme.colorScheme.error)
            items.isEmpty() -> Text("Belum ada pendaftaran tempat.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            else -> {
            items.forEach { business ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp)) {
                        Text(business.name, fontWeight = FontWeight.Bold)
                        Text("${business.category} · ${business.address}", style = MaterialTheme.typography.bodySmall)
                        Text(when (business.status) { "APPROVED" -> "Disetujui"; "REJECTED" -> "Perlu perbaikan"; else -> "Menunggu peninjauan" },
                            color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun AdminReviewPanel(context: android.content.Context) {
    val scope = rememberCoroutineScope()
    var entries by remember { mutableStateOf<List<Where2GoBackend.PlaceSubmission>>(emptyList()) }
    var status by remember { mutableStateOf<String?>(null) }
    var refresh by remember { mutableIntStateOf(0) }
    var busyId by remember { mutableStateOf<Long?>(null) }
    LaunchedEffect(refresh) {
        try { entries = withContext(Dispatchers.IO) { Where2GoBackend.pendingBusinesses(context) }; status = null }
        catch (e: Exception) { status = e.message ?: "Daftar admin gagal dimuat." }
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Peninjauan Admin", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        status?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        if (entries.isEmpty() && status == null) Text("Tidak ada pendaftaran yang menunggu.")
        entries.forEach { entry ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(entry.name, fontWeight = FontWeight.Bold)
                    Text("${entry.category} · ${entry.address}", style = MaterialTheme.typography.bodySmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(enabled = busyId == null, onClick = {
                            scope.launch { busyId = entry.id
                                try { withContext(Dispatchers.IO) { Where2GoBackend.reviewBusiness(context, entry.id, true) }; refresh++ }
                                catch (e: Exception) { status = e.message ?: "Gagal menyetujui." }
                                busyId = null
                            }
                        }) { Text("Setujui") }
                        OutlinedButton(enabled = busyId == null, onClick = {
                            scope.launch { busyId = entry.id
                                try { withContext(Dispatchers.IO) { Where2GoBackend.reviewBusiness(context, entry.id, false) }; refresh++ }
                                catch (e: Exception) { status = e.message ?: "Gagal menolak." }
                                busyId = null
                            }
                        }) { Text("Tolak") }
                    }
                }
            }
        }
    }
}
