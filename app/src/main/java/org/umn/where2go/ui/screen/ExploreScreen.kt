@file:OptIn(ExperimentalMaterial3Api::class)
package org.umn.where2go.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.umn.where2go.data.Vibe
import org.umn.where2go.ui.components.PlaceCard
import org.umn.where2go.viewmodel.ExploreViewModel

@Composable
fun ExploreScreen(vm: ExploreViewModel, onPlaceClick: (Int) -> Unit, bottomBar: @Composable () -> Unit) {
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Where2Go") }) },
        bottomBar = bottomBar
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                    value = vm.query, onValueChange = vm::onQueryChange,
                    placeholder = { Text("Cari kafe, perpustakaan, taman...") },
                    leadingIcon = { Icon(Icons.Filled.Search, null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(horizontal =
                        16.dp, vertical = 8.dp)
                )
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(Vibe.entries.toList()) { v ->
                            FilterChip(selected = vm.selectedVibe == v, onClick = {vm.toggleVibe(v) },
                                label = { Text(v.label) })
                        }
                        item { FilterChip(selected = vm.onlyWifi, onClick = vm::toggleWifi, label = { Text("Ada Wi-Fi") }) }
                    }
                    Spacer(Modifier.height(8.dp))
                    when {
                        vm.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                        CircularProgressIndicator()
                    }
                        vm.filteredPlaces.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Tidak ada tempat yang cocok dengan filter", style = MaterialTheme.typography.bodyLarge)
                        }
                        else -> {
                            Text("${vm.filteredPlaces.size} tempat ditemukan"
                                ,
                                style = MaterialTheme.typography.labelLarge, modifier
                                = Modifier.padding(horizontal = 16.dp))
                            LazyColumn(contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(vm.filteredPlaces, key = { it.id }) { place ->
                                    PlaceCard(place, isFavorite = place.id in
                                            vm.favorites,
                                        onClick = { onPlaceClick(place.id) })
                                }
                            }
                        }
                    }
                }
        }
    }


