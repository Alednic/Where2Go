package com.umn.where2go.data

enum class Vibe(val label: String) {
    QUIET("Tenang"), STUDY("Cocok Nugas"), LIVELY("Ramai")
}

data class Place(
    val id: Int,
    val name: String,
    val address: String,
    val distanceKm: Double,
    val vibe: Vibe,
    val quietness: Int,      // 0..100
    val wifiMbps: Int,       // 0 = tidak ada Wi-Fi
    val hasOutlet: Boolean,
    val rating: Double,
    val openHours: String,
    val description: String,
    val facilities: List<String>
)

data class Review(
    val id: Int, val placeId: Int, val author: String,
    val vibe: Vibe, val comment: String, val timeAgo: String
)

data class Contributor(val rank: Int, val name: String, val level: String, val xp: Int)