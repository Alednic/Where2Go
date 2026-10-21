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
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LightMode
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
    bottomBar: @Composable () -> Unit,
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = bottomBar
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(start = 24.dp, top = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Explore",
                    style = MaterialTheme.typography.displayMedium
                )

                IconButton(
                    onClick = { onThemeChange(!darkTheme) }
                ) {
                    Icon(
                        imageVector = if (darkTheme) {
                            Icons.Filled.LightMode
                        } else {
                            Icons.Filled.DarkMode
                        },
                        contentDescription = if (darkTheme) {
                            "Ganti ke light mode"
                        } else {
                            "Ganti ke dark mode"
                        },
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            SearchField(
                vm.query,
                vm::onQueryChange,
                Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(12.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ExploreFilter.entries.toList()) { filter ->
                    FilterPill(
                        filter = filter,
                        selected = vm.filter == filter,
                        onClick = { vm.selectFilter(filter) }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            when {
                vm.isLoading -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                vm.filteredPlaces.isEmpty() -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Tidak ada tempat yang cocok",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = 24.dp,
                            end = 24.dp,
                            top = 4.dp,
                            bottom = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            vm.filteredPlaces,
                            key = { place -> place.id }
                        ) { place ->
                            PlaceCard(
                                place,
                                onClick = { onPlaceClick(place.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        placeholder = {
            Text("Cari tempat, vibe, area...")
        },
        leadingIcon = {
            Icon(Icons.Filled.Search, null)
        },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SearchBg,
            unfocusedContainerColor = SearchBg,
            focusedTextColor = Color(0xFF1B1B1B),
            unfocusedTextColor = Color(0xFF1B1B1B),
            unfocusedBorderColor = SearchBorder
        ),
        modifier = modifier.fillMaxWidth()
    )
}

private data class PillStyle(
    val accent: Color,
    val tint: Color,
    val icon: ImageVector?
)

private fun ExploreFilter.style(primary: Color) = when (this) {
    ExploreFilter.ALL ->
        PillStyle(primary, Peach, null)

    ExploreFilter.QUIET ->
        PillStyle(ChipTeal, ChipTealBg, Icons.Filled.Eco)

    ExploreFilter.STUDY ->
        PillStyle(ChipSky, ChipSkyBg, Icons.Filled.MenuBook)

    ExploreFilter.BUDGET ->
        PillStyle(ChipAmber, ChipAmberBg, Icons.Filled.Savings)
}

@Composable
private fun FilterPill(
    filter: ExploreFilter,
    selected: Boolean,
    onClick: () -> Unit
) {
    val style = filter.style(MaterialTheme.colorScheme.primary)

    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (selected) style.accent else style.tint,
        border = BorderStroke(1.dp, style.accent)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = filter.label,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) Color.White else Color(0xFF1B1B1B)
            )

            style.icon?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (selected) Color.White else style.accent
                )
            }
        }
    }
}