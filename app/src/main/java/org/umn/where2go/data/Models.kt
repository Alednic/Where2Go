package org.umn.where2go.data

import androidx.annotation.DrawableRes

enum class Vibe(val label: String) {
    QUIET("Tenang"), STUDY("Nugas"), LIVELY("Ramai")
}

/** Suasana pada waktu tertentu (kartu Pagi / Siang / Malam) */
data class DayPart(val label: String, val vibe: Vibe)

/** Skor kecocokan 0..100 (bar "Cocok untuk") */
data class Suitability(val nugas: Int, val hangout: Int, val meeting: Int)

/** Item di bagian "Kuliner" */
data class Culinary(val name: String, val description: String, val price: Int = 0)

data class Place(
    val id: Int,
    val name: String,
    val address: String,
    val distanceKm: Double,
    val vibe: Vibe,
    val quietness: Int,          // 0..100
    val wifiMbps: Int,           // 0 = tidak ada Wi-Fi
    val hasOutlet: Boolean,
    val rating: Double,          // "Vibe Score"
    val openHours: String,
    val description: String,
    val facilities: List<String>,
    // ---- field tambahan untuk layar Place Detail ----
    val checkIns: Int = 0,
    val minOrderK: Int = 0,      // dalam ribu rupiah (12 = Rp. 12K)
    val tags: List<String> = emptyList(),
    val peakHours: List<Int> = listOf(30, 20, 28, 28, 35, 42, 70, 55),   // jam 07,09,11,13,15,17,19,21
    val dayparts: List<DayPart> = listOf(
        DayPart("Pagi", Vibe.QUIET), DayPart("Siang", Vibe.LIVELY), DayPart("Malam", Vibe.STUDY)
    ),
    val suitability: Suitability = Suitability(60, 50, 40),
    val culinary: List<Culinary> = emptyList(),
    val cardTags: List<String> = emptyList(),   // chip putih di kartu Explore
    @DrawableRes val imageRes: Int? = null       // isi dengan R.drawable.nama_foto bila foto sudah ada
)

fun Place.distanceLabel(): String =
    if (distanceKm < 1.0) "${(distanceKm * 1000).toInt()}m" else "%.1f km".format(distanceKm)

data class Review(
    val id: Int,
    val placeId: Int,
    val author: String,
    val vibe: Vibe,
    val comment: String,
    val timeAgo: String
)

// (2) Contributor diperbarui (xp diganti tags, ditambah avatarRes)
data class Contributor(
    val rank: Int,
    val name: String,
    val level: String,
    val tags: Int,
    @DrawableRes val avatarRes: Int? = null
)

// (3) Model baru untuk tab "Top Tempat"
data class TopPlace(
    val rank: Int,
    val name: String,
    val area: String,
    val checkIns: Int,
    @DrawableRes val imageRes: Int? = null
)