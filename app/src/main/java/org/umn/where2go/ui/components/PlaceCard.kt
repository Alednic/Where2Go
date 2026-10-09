@file:OptIn(ExperimentalMaterial3Api::class)
package org.umn.where2go.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.umn.where2go.data.Place

@Composable
fun PlaceCard(place: Place, isFavorite: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(Modifier.height(IntrinsicSize.Min)) {
        
        Box(Modifier.width(8.dp).fillMaxHeight().background(place.vibe.color()))
            Column(Modifier.weight(1f).padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(place.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    if (isFavorite) Icon(Icons.Filled.Favorite, "Favorit",
                        tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                }
                Text(place.address, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    VibeBadge(place.vibe)
                    Pill("%.1f km".format(place.distanceKm))
                    Pill(if (place.wifiMbps > 0) "${place.wifiMbps} Mbps" else "Tanpa Wi-Fi")
                }
            }
        }
    }
}