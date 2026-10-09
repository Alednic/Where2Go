package com.umn.where2go.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umn.where2go.data.DummyData
import com.umn.where2go.data.Place
import com.umn.where2go.data.Vibe
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ExploreViewModel : ViewModel() {
    var isLoading by mutableStateOf(true); private set
    var places by mutableStateOf<List<Place>>(emptyList()); private set
    var query by mutableStateOf(""); private set
    var selectedVibe by mutableStateOf<Vibe?>(null); private set
    var onlyWifi by mutableStateOf(false); private set
    val favorites = mutableStateListOf<Int>()

    init {
        viewModelScope.launch {          // simulasi loading data
            delay(1200)
            places = DummyData.places
            isLoading = false
        }
    }

    val filteredPlaces: List<Place>
        get() = places.filter { p ->
            (selectedVibe == null || p.vibe == selectedVibe) &&
                    (!onlyWifi || p.wifiMbps > 0) &&
                    (query.isBlank() || p.name.contains(query, true) || p.address.contains(query, true))
        }.sortedBy { it.distanceKm }

    fun onQueryChange(v: String) { query = v }
    fun toggleVibe(v: Vibe) { selectedVibe = if (selectedVibe == v) null else v }
    fun toggleWifi() { onlyWifi = !onlyWifi }
    fun toggleFavorite(id: Int) { if (id in favorites) favorites.remove(id) else favorites.add(id) }
}