@file:OptIn(ExperimentalMaterial3Api::class)

package org.umn.where2go.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.umn.where2go.data.DummyData
import org.umn.where2go.data.Place
import org.umn.where2go.ui.components.Pill
import org.umn.where2go.ui.components.VibeBadge
import org.umn.where2go.ui.components.color
import org.umn.where2go.viewmodel.ExploreViewModel

@Composable
fun PlaceDetailScreen(
    placeId: Int,
    vm: ExploreViewModel,
    onBack: () -> Unit,
    onCheckIn: () -> Unit
) {
    val place = remember(placeId) { DummyData.places.find { it.id == placeId } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(place?.name ?: "Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                },
                actions = {
                    if (place != null) {
                        val fav = place.id in vm.favorites
                        IconButton(onClick = { vm.toggleFavorite(place.id) }) {
                            Icon(
                                if (fav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                "Favorit",
                                tint = if (fav) MaterialTheme.colorScheme.secondary
                                else LocalContentColor.current
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (place != null) {
                Surface(tonalElevation = 3.dp) {
                    Button(
                        onClick = onCheckIn,
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        Text("Check-in di sini")
                    }
                }
            }
        }
    ) { padding ->
        if (place == null) {
            Box(
                Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Tempat tidak ditemukan")
            }
        } else {
            DetailContent(place, Modifier.padding(padding))
        }
    }
}

@Composable
private fun DetailContent(place: Place, modifier: Modifier = Modifier) {
    val reviews = remember(place.id) {
        DummyData.reviews.filter { it.placeId == place.id }
    }

    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(place.vibe.color().copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Place, null,
                Modifier.size(64.dp),
                tint = place.vibe.color()
            )
        }

        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(place.name, style = MaterialTheme.typography.headlineMedium)

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Star, null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(18.dp)
                )
                Text("${place.rating}", style = MaterialTheme.typography.titleSmall)
            }

            Text(
                "Tingkat ketenangan: ${place.quietness}%",
                style = MaterialTheme.typography.titleSmall
            )
            LinearProgressIndicator(
                progress = { place.quietness / 100f },
                color = place.vibe.color(),
                modifier = Modifier.fillMaxWidth().height(8.dp)
            )

            Text(place.description, style = MaterialTheme.typography.bodyLarge)

            InfoRow(
                Icons.Filled.LocationOn,
                "${place.address} (${"%.1f".format(place.distanceKm)} km)"
            )
            InfoRow(Icons.Filled.Schedule, place.openHours)
            InfoRow(
                Icons.Filled.Wifi,
                if (place.wifiMbps > 0) "${place.wifiMbps} Mbps" else "Tidak ada Wi-Fi"
            )
            InfoRow(
                Icons.Filled.Power,
                if (place.hasOutlet) "Stopkontak tersedia" else "Stopkontak terbatas"
            )

            Text("Fasilitas", style = MaterialTheme.typography.titleMedium)
            Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                place.facilities.forEach { Pill(it) }
            }

            Text("Ulasan komunitas", style = MaterialTheme.typography.titleMedium)
            if (reviews.isEmpty()) {
                Text("Belum ada ulasan.", style = MaterialTheme.typography.bodyMedium)
            }
            reviews.forEach { r ->
                Card(Modifier.fillMaxWidth()) {
                    Column(
                        Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(r.author, style = MaterialTheme.typography.titleSmall)
                            VibeBadge(r.vibe)
                            Spacer(Modifier.weight(1f))
                            Text(r.timeAgo, style = MaterialTheme.typography.labelSmall)
                        }
                        Text(r.comment, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}