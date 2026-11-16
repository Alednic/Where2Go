package org.umn.where2go.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.umn.where2go.data.Place
import org.umn.where2go.data.SupabasePlacesRepository
import org.umn.where2go.data.Vibe
import org.umn.where2go.data.Where2GoBackend
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

enum class ExploreFilter(val label: String) {
    ALL("Semua"), QUIET("Tenang"), STUDY("Nugas"), BUDGET("Budget")
}

class ExploreViewModel : ViewModel() {
    var isLoading by mutableStateOf(true); private set
    var places by mutableStateOf<List<Place>>(emptyList()); private set
    var errorMessage by mutableStateOf<String?>(null); private set
    var query by mutableStateOf(""); private set
    var filter by mutableStateOf(ExploreFilter.ALL); private set
    private var userLocation: Pair<Double, Double>? = null
    val favorites = mutableStateListOf<Int>()

    fun loadFavorites(context: Context) {
        viewModelScope.launch {
            runCatching { withContext(Dispatchers.IO) { Where2GoBackend.favoritePlaceIds(context) } }
                .onSuccess { saved -> favorites.clear(); favorites.addAll(saved) }
        }
    }

    fun toggleFavorite(context: Context, id: Int) {
        val wasFavorite = id in favorites
        if (wasFavorite) favorites.remove(id) else favorites.add(id)
        viewModelScope.launch {
            runCatching { withContext(Dispatchers.IO) { Where2GoBackend.setFavorite(context, id, !wasFavorite) } }
                .onFailure {
                    if (wasFavorite) favorites.add(id) else favorites.remove(id)
                }
        }
    }

    init {
        refreshPlaces()
    }

    fun updateUserLocation(latitude: Double, longitude: Double) {
        userLocation = latitude to longitude
        places = withDistances(places, latitude, longitude)
    }

    fun refreshPlaces() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val loadedPlaces = withContext(Dispatchers.IO) {
                    SupabasePlacesRepository.fetchPlaces()
                }
                places = userLocation?.let { (latitude, longitude) ->
                    withDistances(loadedPlaces, latitude, longitude)
                } ?: loadedPlaces
                if (places.isEmpty()) {
                    errorMessage = "Belum ada tempat di database."
                }
            } catch (error: Exception) {
                places = emptyList()
                errorMessage = when {
                    error.message?.contains("HTTP 401") == true ||
                        error.message?.contains("HTTP 403") == true ->
                        "Supabase menolak akses. Periksa izin SELECT dan kebijakan RLS tabel places."
                    else -> error.message ?: "Gagal memuat tempat dari Supabase."
                }
            } finally {
                isLoading = false
            }
        }
    }

    val filteredPlaces: List<Place>
        get() = places.filter { place ->
            val byFilter = when (filter) {
                ExploreFilter.ALL -> true
                ExploreFilter.QUIET -> place.vibe == Vibe.QUIET
                ExploreFilter.STUDY -> place.vibe == Vibe.STUDY
                ExploreFilter.BUDGET -> place.minOrderK in 1..15
            }
            val byQuery = query.isBlank() ||
                place.name.contains(query, true) ||
                place.address.contains(query, true) ||
                place.cardTags.any { it.contains(query, true) }
            byFilter && byQuery
        }.sortedWith(compareByDescending<Place> { it.id in favorites }
            .thenBy { it.distanceKm.takeIf { km -> km >= 0 } ?: Double.MAX_VALUE })

    fun onQueryChange(value: String) {
        query = value
    }

    fun selectFilter(selectedFilter: ExploreFilter) {
        filter = selectedFilter
    }

    private fun withDistances(
        places: List<Place>,
        userLatitude: Double,
        userLongitude: Double
    ): List<Place> = places.map { place ->
        val placeLatitude = place.latitude
        val placeLongitude = place.longitude
        val distance = if (placeLatitude != null && placeLongitude != null) {
            calculateDistanceKm(
                userLatitude,
                userLongitude,
                placeLatitude,
                placeLongitude
            )
        } else {
            -1.0
        }
        place.copy(distanceKm = distance)
    }

    private fun calculateDistanceKm(
        firstLatitude: Double,
        firstLongitude: Double,
        secondLatitude: Double,
        secondLongitude: Double
    ): Double {
        val earthRadiusKm = 6371.0
        val latitudeDelta = Math.toRadians(secondLatitude - firstLatitude)
        val longitudeDelta = Math.toRadians(secondLongitude - firstLongitude)
        val haversine = sin(latitudeDelta / 2) * sin(latitudeDelta / 2) +
            cos(Math.toRadians(firstLatitude)) *
            cos(Math.toRadians(secondLatitude)) *
            sin(longitudeDelta / 2) * sin(longitudeDelta / 2)
        return 2 * earthRadiusKm * asin(sqrt(haversine.coerceIn(0.0, 1.0)))
    }
}
