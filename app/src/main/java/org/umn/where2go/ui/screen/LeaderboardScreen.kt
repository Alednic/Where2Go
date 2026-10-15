@file:OptIn(ExperimentalMaterial3Api::class)

package org.umn.where2go.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.umn.where2go.data.Contributor
import org.umn.where2go.data.DummyData
import org.umn.where2go.data.TopPlace
import org.umn.where2go.ui.components.PhotoBox
import org.umn.where2go.ui.theme.*

@Composable
fun LeaderboardScreen(bottomBar: @Composable () -> Unit) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    val sorted = remember { DummyData.contributors.sortedByDescending { it.tags } }

    Scaffold(containerColor = MaterialTheme.colorScheme.background, bottomBar = bottomBar) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {

            LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(bottom = 8.dp)) {
                item { RankingHeader(tab) { tab = it } }

                if (tab == 0) {
                    item { Podium(sorted.take(3)) }
                    item {
                        Text("Lainnya minggu ini", fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 4.dp))
                    }
                    items(sorted.drop(3), key = { it.rank }) { ContributorRow(it) }
                } else {
                    items(DummyData.topPlaces, key = { it.rank }) { place ->
                        Box(Modifier.padding(horizontal = 24.dp, vertical = 6.dp)) { TopPlaceCard(place) }
                    }
                }
            }

            if (tab == 0) CurrentUserCard(DummyData.currentUser)
        }
    }
}

@Composable
private fun RankingHeader(tab: Int, onTab: (Int) -> Unit) {
    Column(Modifier.statusBarsPadding().padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text("Ranking", style = MaterialTheme.typography.displayMedium)
        Text("Kontributor dan spot terpopuler minggu ini", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(14.dp))
        SegmentedTabs(listOf("Top Kontributor", "Top Tempat"), tab, onTab)
        Spacer(Modifier.height(12.dp))
        Text(
            if (tab == 0) "Paling banyak nge-tag tempat" else "Paling banyak di-check-in",
            fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun SegmentedTabs(labels: List<String>, selected: Int, onSelect: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth().clip(CircleShape).background(SegmentTrack).padding(4.dp)) {
        labels.forEachIndexed { index, label ->
            val isSelected = index == selected
            Box(
                Modifier.weight(1f).clip(CircleShape)
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onSelect(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(label, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) Color.White else Color(0xFF8A8A8A))
            }
        }
    }
}

@Composable
private fun Podium(top3: List<Contributor>) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        PodiumColumn(top3[1], 2, 110.dp, PodiumSilver, 64.dp)
        PodiumColumn(top3[0], 1, 136.dp, PodiumGold, 74.dp, crown = true)
        PodiumColumn(top3[2], 3, 92.dp, PodiumBronze, 64.dp)
    }
}

@Composable
private fun PodiumColumn(
    c: Contributor, place: Int, blockHeight: Dp, color: Color, avatarSize: Dp, crown: Boolean = false
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (crown) Icon(Icons.Filled.WorkspacePremium, "Juara 1", Modifier.size(32.dp))
        PhotoBox(
            c.avatarRes, Modifier.size(avatarSize).border(1.dp, Color.Black, CircleShape),
            fallback = Violet, shape = CircleShape
        )
        Text(c.name, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
        Text("${c.tags} Tags", color = SubtleGray, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))
        Box(
            Modifier.width(84.dp).height(blockHeight)
                .background(color, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("$place", color = Color.Black, style = MaterialTheme.typography.displayMedium)
        }
    }
}

@Composable
private fun ContributorRow(c: Contributor) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("${c.rank}", color = SubtleGray, fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge, modifier = Modifier.width(24.dp))
            PhotoBox(c.avatarRes, Modifier.size(44.dp), fallback = Violet, shape = CircleShape)
            Column(Modifier.weight(1f)) {
                Text(c.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Text(c.level, color = SubtleGray, style = MaterialTheme.typography.labelSmall)
            }
            Text("${c.tags} Tags", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.onBackground))
    }
}

@Composable
private fun CurrentUserCard(me: Contributor) {
    Surface(
        color = RankCardBg,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "#${me.rank}", color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge
            )
            PhotoBox(
                me.avatarRes,
                Modifier.size(56.dp).border(3.dp, Color.White, CircleShape),
                fallback = Violet, shape = CircleShape
            )
            Column(Modifier.weight(1f)) {
                Text(me.name, color = TitleInk, fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium)
                Text(me.level, color = SubtleGray, style = MaterialTheme.typography.labelLarge)
            }
            Text("${me.tags} Tags", color = TitleInk, fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
private fun TopPlaceCard(p: TopPlace) {
    Surface(
        color = RankCardBg, shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.5.dp, RankCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            PhotoBox(p.imageRes, Modifier.size(62.dp), fallback = Violet, shape = RoundedCornerShape(16.dp))
            Column(Modifier.weight(1f)) {
                Text(p.name, color = TitleInk, fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium)
                Text("${p.area} - ${p.checkIns} check-ins", color = Color(0xFF6B7280),
                    style = MaterialTheme.typography.labelLarge)
            }
            Text("${p.rank}", color = TitleInk, fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(end = 8.dp))
        }
    }
}
