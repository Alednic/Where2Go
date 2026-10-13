
@file:OptIn(ExperimentalMaterial3Api::class)

package org.umn.where2go.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.umn.where2go.data.Vibe
import org.umn.where2go.ui.components.PlaceCard
import org.umn.where2go.ui.theme.*
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            Text(
                text = "Explore",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Vermilion,
                modifier = Modifier.padding(
                    start = 27.dp,
                    top = 14.dp,
                    bottom = 8.dp
                )
            )

            OutlinedTextField(
                value = vm.query,
                onValueChange = vm::onQueryChange,
                placeholder = {
                    Text(
                        "Cari tempat, vibe, area...",
                        fontSize = 12.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Cari tempat"
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Violet,
                    unfocusedBorderColor =
                        MaterialTheme.colorScheme.outline,
                    focusedContainerColor =
                        MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor =
                        MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 27.dp)
                    .height(54.dp)
            )

            Spacer(Modifier.height(14.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 27.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    ExploreFilter(
                        text = "Semua",
                        selected = vm.selectedVibe == null &&
                                !vm.onlyWifi && !vm.onlyBudget,
                        color = Vermilion,
                        onClick = vm::clearFilters
                    )
                }

                item {
                    ExploreFilter(
                        text = "Tenang",
                        selected = vm.selectedVibe == Vibe.QUIET,
                        color = VibeQuietColor,
                        onClick = {
                            vm.toggleVibe(Vibe.QUIET)
                        }
                    )
                }

                item {
                    ExploreFilter(
                        text = "Nugas",
                        selected = vm.selectedVibe == Vibe.STUDY,
                        color = VibeStudyColor,
                        onClick = {
                            vm.toggleVibe(Vibe.STUDY)
                        }
                    )
                }

                item {
                    ExploreFilter(
                        text = "Budget",
                        selected = vm.onlyBudget,
                        color = Gold,
                        onClick = vm::toggleBudget
                    )
                }

                item {
                    ExploreFilter(
                        text = "Ada Wi-Fi",
                        selected = vm.onlyWifi,
                        color = TagWifiFg,
                        onClick = vm::toggleWifi
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            when {
                vm.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Vermilion)
                    }
                }

                vm.filteredPlaces.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tidak ada tempat yang cocok dengan filter.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    Text(
                        text = "${vm.filteredPlaces.size} tempat ditemukan",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(
                            horizontal = 27.dp,
                            vertical = 4.dp
                        )
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 27.dp,
                            end = 27.dp,
                            top = 8.dp,
                            bottom = 20.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = vm.filteredPlaces,
                            key = { it.id }
                        ) { place ->
                            PlaceCard(
                                place = place,
                                isFavorite = place.id in vm.favorites,
                                onClick = {
                                    onPlaceClick(place.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExploreFilter(
    text: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (selected) color else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, color)
    ) {
        Text(
            text = text,
            color = if (selected) Color.White
            else MaterialTheme.colorScheme.onSurface,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(
                horizontal = 13.dp,
                vertical = 7.dp
            )
        )
    }
}
