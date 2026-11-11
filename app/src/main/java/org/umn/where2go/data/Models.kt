package org.umn.where2go.data

import androidx.annotation.DrawableRes

enum class Vibe(val label: String) {
    QUIET("Tenang"), STUDY("Nugas"), LIVELY("Ramai"), UNKNOWN("Belum diketahui")
}

data class DayPart(val label: String, val vibe: Vibe)

data class Suitability(val nugas: Int, val hangout: Int, val meeting: Int)

data class Culinary(val name: String, val description: String, val price: Int = 0)

data class Place(
    val id: Int,
    val name: String,
    val address: String,
    val distanceKm: Double,
    val vibe: Vibe,
    val quietness: Int,
    val wifiMbps: Int,
    val hasOutlet: Boolean,
    val rating: Double,
    val openHours: String,
    val description: String,
    val facilities: List<String>,
    val checkIns: Int = 0,
    val minOrderK: Int = 0,
    val tags: List<String> = emptyList(),
    val peakHours: List<Int> = listOf(30, 20, 28, 28, 35, 42, 70, 55),
    val dayparts: List<DayPart> = listOf(
        DayPart("Pagi", Vibe.QUIET), DayPart("Siang", Vibe.LIVELY), DayPart("Malam", Vibe.STUDY)
    ),
    val suitability: Suitability = Suitability(60, 50, 40),
    val culinary: List<Culinary> = emptyList(),
    val cardTags: List<String> = emptyList(),
    @DrawableRes val imageRes: Int? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val imageUrl: String? = null
)

fun Place.distanceLabel(): String =
    if (distanceKm < 0) "Jarak tidak diketahui"
    else if (distanceKm < 1.0) "${(distanceKm * 1000).toInt()}m" else "%.1f km".format(distanceKm)

data class Review(
    val id: Int,
    val placeId: Int,
    val author: String,
    val vibe: Vibe,
    val comment: String,
    val timeAgo: String
)

data class Contributor(
    val rank: Int,
    val name: String,
    val level: String,
    val tags: Int,
    @DrawableRes val avatarRes: Int? = null
)

data class TopPlace(
    val rank: Int,
    val name: String,
    val area: String,
    val checkIns: Int,
    @DrawableRes val imageRes: Int? = null
)
