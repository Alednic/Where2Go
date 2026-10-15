@file:OptIn(ExperimentalMaterial3Api::class)

package org.umn.where2go.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.umn.where2go.data.*
import org.umn.where2go.ui.components.VibeBadge
import org.umn.where2go.ui.components.color
import org.umn.where2go.ui.theme.*
import org.umn.where2go.ui.components.icon
import org.umn.where2go.viewmodel.ExploreViewModel

private val GrayText = Color(0xFF6B7280)
private val GrayLight = Color(0xFF9CA3AF)

@Composable
fun PlaceDetailScreen(placeId: Int, vm: ExploreViewModel, onBack: () -> Unit, onCheckIn: () -> Unit) {
    val place = remember(placeId) { DummyData.places.find { it.id == placeId } }
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { if (place != null) CheckInBar(onCheckIn) }
    ) { padding ->
        if (place == null) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Tempat tidak ditemukan")
            }
        } else {
            DetailContent(
                place = place,
                isFavorite = place.id in vm.favorites,
                onBack = onBack,
                onToggleFavorite = { vm.toggleFavorite(place.id) },
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun CheckInBar(onCheckIn: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Button(
            onClick = onCheckIn,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp).height(52.dp)
        ) { Text("Check-in di sini", fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun DetailContent(
    place: Place, isFavorite: Boolean, onBack: () -> Unit, onToggleFavorite: () -> Unit, modifier: Modifier = Modifier
) {
    val reviews = remember(place.id) { DummyData.reviews.filter { it.placeId == place.id } }
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Hero(place, isFavorite, onBack, onToggleFavorite)

        Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Filled.LocationOn, null, Modifier.size(44.dp), tint = MaterialTheme.colorScheme.primary)
                Column {
                    Text(place.name, style = MaterialTheme.typography.headlineMedium)
                    Text("${place.address} \u00B7 ${place.distanceLabel()}",
                        style = MaterialTheme.typography.bodyMedium, color = SubtleGray)
                }
            }

            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                place.tags.forEach { TagChip(it) }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("${place.rating}", "Vibe Score", Modifier.weight(1f))
                StatCard("${place.checkIns}", "Check-ins", Modifier.weight(1f))
                StatCard(if (place.minOrderK > 0) "Rp. ${place.minOrderK}K" else "Gratis", "Min. Order", Modifier.weight(1f))
            }

            SectionTitle("Jam Ramai")
            PeakHoursChart(place.peakHours)

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                place.dayparts.forEach { DaypartCard(it, Modifier.weight(1f)) }
            }

            SectionTitle("Cocok untuk")
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SuitabilityRow("Nugas", place.suitability.nugas, BarNugas)
                SuitabilityRow("Hangout", place.suitability.hangout, BarHangout)
                SuitabilityRow("Meeting", place.suitability.meeting, BarMeeting)
            }

            SectionTitle("Kuliner")
            if (place.culinary.isEmpty()) Text("Belum ada data kuliner.", style = MaterialTheme.typography.bodyMedium)
            place.culinary.forEach { CulinaryCard(it) }

            SectionTitle("Ulasan komunitas")
            if (reviews.isEmpty()) Text("Belum ada ulasan.", style = MaterialTheme.typography.bodyMedium)
            reviews.forEach { r ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
private fun Hero(place: Place, isFavorite: Boolean, onBack: () -> Unit, onToggleFavorite: () -> Unit) {
    val img = place.imageRes
    Box(Modifier.fillMaxWidth().aspectRatio(2.0f)) {
        if (img != null) {
            Image(painterResource(img), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } else {
            Box(Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(place.vibe.color().copy(alpha = 0.55f), place.vibe.color().copy(alpha = 0.95f)))
            ))
        }
        Row(Modifier.fillMaxWidth().statusBarsPadding().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            RoundIconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = Color.White) }
            RoundIconButton(onToggleFavorite) {
                Icon(if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, "Favorit", tint = Color.White)
            }
        }
        Surface(
            color = TagWifiBg, shape = CircleShape,
            modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)
        ) {
            Text("${place.vibe.label} sekarang", color = TagWifiFg, fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
        }
    }
}

@Composable
private fun RoundIconButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.9f), CircleShape)
    ) { content() }
}

@Composable
private fun TagChip(label: String) {
    val (bg, fg) = when (label.lowercase()) {
        "tenang", "nugas" -> TagTenangBg to TagTenangFg
        "free wifi" -> TagWifiBg to TagWifiFg
        "good vibes" -> TagVibesBg to TagVibesFg
        "focus", "ramai" -> TagFocusBg to TagFocusFg
        "affordable" -> TagPriceBg to TagPriceFg
        else -> TagTenangBg to TagTenangFg
    }
    Surface(color = bg, shape = CircleShape) {
        Text(label, color = fg, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(color = StatCardBg, shape = MaterialTheme.shapes.medium, modifier = modifier) {
        Column(Modifier.padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = TitleInk, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(label, color = GrayText, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}

@Composable
private fun PeakHoursChart(values: List<Int>) {
    val hours = listOf(7, 9, 11, 13, 15, 17, 19, 21)
    val maxV = values.maxOrNull() ?: 0
    Column {
        Row(Modifier.fillMaxWidth().height(72.dp), verticalAlignment = Alignment.Bottom) {
            values.forEach { v ->
                Box(
                    Modifier.weight(1f).padding(horizontal = 8.dp)
                        .fillMaxHeight(v.coerceIn(8, 100) / 100f)
                        .background(
                            if (v == maxV) Lavender else ChartBar,
                            RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                        )
                )
            }
        }
        Row(Modifier.fillMaxWidth().background(ChartAxisBg).padding(vertical = 6.dp)) {
            hours.forEach { h ->
                Text("%02d".format(h), Modifier.weight(1f), textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall, color = GrayLight)
            }
        }
    }
}

@Composable
private fun DaypartCard(part: DayPart, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = ChartBar),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(part.label, color = TitleInk, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
            Icon(part.vibe.icon(), null, Modifier.size(22.dp), tint = DaypartIcon)
            Text(part.vibe.label, color = TitleInk, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun SuitabilityRow(label: String, value: Int, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        LinearProgressIndicator(
            progress = { value / 100f },
            color = color, trackColor = TrackGray,
            modifier = Modifier.weight(1f).height(8.dp).clip(CircleShape)
        )
        Text(label, Modifier.width(56.dp), style = MaterialTheme.typography.labelSmall, color = GrayText)
    }
}

@Composable
private fun CulinaryCard(item: Culinary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ChartBar),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Filled.Coffee, null, tint = DaypartIcon)
            Column(Modifier.weight(1f)) {
                Text(item.name, color = TitleInk, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Text(item.description, color = GrayText, style = MaterialTheme.typography.labelSmall)
            }
            Surface(color = LavenderButton, shape = CircleShape) {
                val priceLabel = if (item.price > 0) "Rp ${"%,d".format(item.price).replace(',', '.')}" else "Lihat"
                Text(priceLabel, color = DaypartIcon, fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp))
            }
        }
    }
}