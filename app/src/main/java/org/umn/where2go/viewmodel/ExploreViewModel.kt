
package org.umn.where2go.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.umn.where2go.data.DummyData
import org.umn.where2go.data.Place
import org.umn.where2go.data.Vibe

class ExploreViewModel : ViewModel() {

    var isLoading by mutableStateOf(true)
        private set

    var places by mutableStateOf<List<Place>>(emptyList())
        private set

    var query by mutableStateOf("")
        private set

    var selectedVibe by mutableStateOf<Vibe?>(null)
        private set

    var onlyWifi by mutableStateOf(false)
        private set

    var onlyBudget by mutableStateOf(false)
        private set

    val favorites = mutableStateListOf<Int>()

    init {
        viewModelScope.launch {
            delay(1200)
            places = DummyData.places
            isLoading = false
        }
    }

    val filteredPlaces: List<Place>
        get() = places.filter { place ->
            val matchesVibe =
                selectedVibe == null || place.vibe == selectedVibe

            val matchesWifi =
                !onlyWifi || place.wifiMbps > 0

            val matchesBudget =
                !onlyBudget || place.minOrderK in 1..25

            val matchesQuery =
                query.isBlank() ||
                        place.name.contains(query, ignoreCase = true) ||
                        place.address.contains(query, ignoreCase = true) ||
                        place.facilities.any {
                            it.contains(query, ignoreCase = true)
                        } ||
                        place.tags.any {
                            it.contains(query, ignoreCase = true)
                        }

            matchesVibe &&
                    matchesWifi &&
                    matchesBudget &&
                    matchesQuery
        }.sortedBy { it.distanceKm }

    fun onQueryChange(value: String) {
        query = value
    }

    fun toggleVibe(vibe: Vibe) {
        selectedVibe =
            if (selectedVibe == vibe) null else vibe
    }

    fun toggleWifi() {
        onlyWifi = !onlyWifi
    }

    fun toggleBudget() {
        onlyBudget = !onlyBudget
    }

    fun clearFilters() {
        selectedVibe = null
        onlyWifi = false
        onlyBudget = false
    }

    fun toggleFavorite(id: Int) {
        if (id in favorites) {
            favorites.remove(id)
        } else {
            favorites.add(id)
        }
    }
}
