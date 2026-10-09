@file:OptIn(ExperimentalMaterial3Api::class)

package org.umn.where2go.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.umn.where2go.data.DummyData

@Composable
fun LeaderboardScreen(bottomBar: @Composable () -> Unit) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    val topPlaces = remember { DummyData.places.sortedByDescending {
        it.rating } }
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Leaderboard") }) },
        bottomBar = bottomBar
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            TabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = {
                    Text("Top Kontributor") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = {
                    Text("Top Tempat") })
            }
            LazyColumn(contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (tab == 0) {
                    items(DummyData.contributors, key = { it.rank }) { c ->
                        RankRow(c.rank, c.name, c.level,
                            "${c.xp} XP")
                    }
                } else {
                    items(topPlaces.size) { i ->
                        val p = topPlaces[i]
                        RankRow(i + 1, p.name, p.vibe.label,
                            "★ ${p.rating}")
                    }
                }
            }
        }
    }
}

@Composable
private fun RankRow(rank: Int, title: String, subtitle: String, trailing:
String) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment =
            Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = if (rank <= 3)
                    MaterialTheme.colorScheme.tertiaryContainer
                else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(40.dp)
            ) { Box(contentAlignment = Alignment.Center) { Text("$rank"
                ,
                style = MaterialTheme.typography.titleMedium) } }
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(trailing, style = MaterialTheme.typography.titleSmall, color
            = MaterialTheme.colorScheme.primary)
        }
    }
}