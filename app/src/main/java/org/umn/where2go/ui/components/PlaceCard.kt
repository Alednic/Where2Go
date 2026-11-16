package org.umn.where2go.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.unit.dp
import org.umn.where2go.data.Place
import org.umn.where2go.data.distanceLabel
import org.umn.where2go.ui.theme.*

@Composable
fun PlaceCard(
    place: Place,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            Box(Modifier.fillMaxWidth().height(124.dp)) {
                PhotoBox(
                    place.imageRes,
                    Modifier.fillMaxSize(),
                    fallback = place.vibe.color(),
                    imageUrl = place.imageUrl
                )
                Box(
                    Modifier.fillMaxSize().background(
                        Brush.verticalGradient(listOf(Color.Transparent, Color(0xAA000000)))
                    )
                )
                Row(
                    Modifier.align(Alignment.TopEnd).padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(color = ChartBar, shape = CircleShape, modifier = Modifier.size(30.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(place.vibe.icon(), null, Modifier.size(16.dp), tint = DaypartIcon)
                        }
                    }
                    Surface(color = Color.White.copy(alpha = 0.95f), shape = CircleShape) {
                        IconButton(onClick = onFavoriteClick, modifier = Modifier.size(34.dp)) {
                            Icon(
                                if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (isFavorite) "Hapus dari favorit" else "Tambah ke favorit",
                                tint = if (isFavorite) MaterialTheme.colorScheme.primary else Color.DarkGray,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }
                Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                    Text(
                        place.name, color = Color.White, fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocationOn, null, Modifier.size(14.dp), tint = Color.White)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            place.distanceLabel(), color = Color.White,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
            Row(
                Modifier.fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(StripTop, StripBottom)))
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                place.cardTags.forEach { tag ->
                    Surface(color = Color.White, shape = CircleShape) {
                        Text(
                            tag, color = Color(0xFF1B1B1B), fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1, softWrap = false,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

