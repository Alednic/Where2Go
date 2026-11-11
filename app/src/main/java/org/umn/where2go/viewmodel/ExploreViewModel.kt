
package org.umn.where2go.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.umn.where2go.data.Place
import org.umn.where2go.data.Vibe
import org.umn.where2go.data.SupabasePlacesRepository

enum class ExploreFilter(val label: String) {
    ALL("Semua"), QUIET("Tenang"), STUDY("Nugas"), BUDGET("Budget")
}

class ExploreViewModel : ViewModel() {
    var isLoading by mutableStateOf(true); private set
    var places by mutableStateOf<List<Place>>(emptyList()); private set
    var errorMessage by mutableStateOf<String?>(null); private set
    var query by mutableStateOf(""); private set
    var filter by mutableStateOf(ExploreFilter.ALL); private set
    val favorites = mutableStateListOf<Int>()

    init {
        refreshPlaces()
    }

    fun refreshPlaces() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                places = SupabasePlacesRepository.fetchPlaces()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                errorMessage = error.message ?: "Gagal mengambil data tempat."
            } finally {
                isLoading = false
            }
        }
    }

    val filteredPlaces: List<Place>
        get() = places.filter { p ->
            val byFilter = when (filter) {
                ExploreFilter.ALL -> true
                ExploreFilter.QUIET -> p.vibe == Vibe.QUIET
                ExploreFilter.STUDY -> p.vibe == Vibe.STUDY
                ExploreFilter.BUDGET -> p.minOrderK in 1..15
            }
            val byQuery = query.isBlank() ||
                    p.name.contains(query, true) ||
                    p.address.contains(query, true) ||
                    p.cardTags.any { it.contains(query, true) }
            byFilter && byQuery
        }.sortedBy { it.distanceKm }

    fun onQueryChange(v: String) { query = v }
    fun selectFilter(f: ExploreFilter) { filter = f }
    fun toggleFavorite(id: Int) {
        if (id in favorites) favorites.remove(id) else favorites.add(id)
    }
}
