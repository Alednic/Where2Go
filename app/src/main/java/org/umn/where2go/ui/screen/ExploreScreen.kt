@file:OptIn(ExperimentalMaterial3Api::class)

package org.umn.where2go.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.umn.where2go.ui.components.PlaceCard
import org.umn.where2go.ui.theme.*
import org.umn.where2go.viewmodel.ExploreFilter
import org.umn.where2go.viewmodel.ExploreViewModel

@Composable
fun ExploreScreen(
    vm: ExploreViewModel,
    onPlaceClick: (Int) -> Unit,
    bottomBar: @Composable () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = bottomBar
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            Text(
                "Explore", style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.statusBarsPadding().padding(start = 24.dp, top = 16.dp, end = 24.dp)
            )
            Spacer(Modifier.height(12.dp))
            SearchField(vm.query, vm::onQueryChange, Modifier.padding(horizontal = 24.dp))
            Spacer(Modifier.height(12.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ExploreFilter.entries.toList()) { f ->
                    FilterPill(f, selected = vm.filter == f, onClick = { vm.selectFilter(f) })
                }
            }
            Spacer(Modifier.height(12.dp))

            when {
                vm.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                vm.filteredPlaces.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tidak ada tempat yang cocok", style = MaterialTheme.typography.bodyLarge)
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 4.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(vm.filteredPlaces, key = { it.id }) { place ->
                        PlaceCard(place, onClick = { onPlaceClick(place.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchField(value: String, onChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value, onValueChange = onChange,
        placeholder = { Text("Cari tempat, vibe, area...") },
        leadingIcon = { Icon(Icons.Filled.Search, null) },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SearchBg, unfocusedContainerColor = SearchBg,
            focusedTextColor = Color(0xFF1B1B1B), unfocusedTextColor = Color(0xFF1B1B1B),
            unfocusedBorderColor = SearchBorder
        ),
        modifier = modifier.fillMaxWidth()
    )
}

private data class PillStyle(val accent: Color, val tint: Color, val icon: ImageVector?)

private fun ExploreFilter.style(primary: Color) = when (this) {
    ExploreFilter.ALL -> PillStyle(primary, Peach, null)
    ExploreFilter.QUIET -> PillStyle(ChipTeal, ChipTealBg, Icons.Filled.Eco)
    ExploreFilter.STUDY -> PillStyle(ChipSky, ChipSkyBg, Icons.Filled.MenuBook)
    ExploreFilter.BUDGET -> PillStyle(ChipAmber, ChipAmberBg, Icons.Filled.Savings)
}

@Composable
private fun FilterPill(filter: ExploreFilter, selected: Boolean, onClick: () -> Unit) {
    val s = filter.style(MaterialTheme.colorScheme.primary)
    Surface(
        onClick = onClick, shape = CircleShape,
        color = if (selected) s.accent else s.tint,
        border = BorderStroke(1.dp, s.accent)
    ) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                filter.label, fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) Color.White else Color(0xFF1B1B1B)
            )
            s.icon?.let {
                Icon(it, null, Modifier.size(16.dp), tint = if (selected) Color.White else s.accent)
            }
        }
    }
}
