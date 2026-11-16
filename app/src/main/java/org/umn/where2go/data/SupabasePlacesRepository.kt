package org.umn.where2go.data

import org.json.JSONArray
import org.json.JSONObject
import org.umn.where2go.BuildConfig
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object SupabasePlacesRepository {
    fun fetchPlaces(): List<Place> {
        val projectUrl = BuildConfig.SUPABASE_URL.trimEnd('/')
        val publishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY

        require(projectUrl.startsWith("https://")) {
            "URL Supabase belum diatur."
        }
        require(publishableKey.isNotBlank()) {
            "Publishable key Supabase belum diatur."
        }

        val url = URL(
            "$projectUrl/rest/v1/places" +
                "?select=id,name,address,latitude,longitude,description,opening_hours,image_url,facilities,card_tags,vibe,rating,min_order,check_in_count" +
                "&order=id.asc"
        )
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15_000
            readTimeout = 15_000
            setRequestProperty("apikey", publishableKey)
            if (!publishableKey.startsWith("sb_publishable_")) {
                setRequestProperty("Authorization", "Bearer " + publishableKey)
            }
            setRequestProperty("Accept", "application/json")
        }

        try {
            val statusCode = connection.responseCode
            if (statusCode !in 200..299) {
                throw IOException("Supabase mengembalikan HTTP $statusCode.")
            }

            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val rows = JSONArray(body)
            return buildList {
                for (index in 0 until rows.length()) {
                    val row = rows.optJSONObject(index) ?: continue
                    val cardTags = row.stringList("card_tags")
                    val facilities = row.stringList("facilities")
                    val latitude = row.optDouble("latitude", Double.NaN)
                        .takeUnless { it.isNaN() }
                    val longitude = row.optDouble("longitude", Double.NaN)
                        .takeUnless { it.isNaN() }

                    add(
                        Place(
                            id = row.optInt("id"),
                            name = row.optString("name").takeUnless { it == "null" }.orEmpty(),
                            address = row.optString("address").takeUnless { it == "null" }.orEmpty(),
                            distanceKm = -1.0,
                            vibe = row.optString("vibe").toVibe(),
                            quietness = row.optInt("quietness"),
                            wifiMbps = row.optInt("wifi_mbps"),
                            hasOutlet = row.optBoolean("has_outlet"),
                            rating = row.optDouble("rating", 0.0),
                            checkIns = row.optInt("check_in_count", 0),
                            openHours = row.optString("opening_hours")
                                .takeUnless { it.isBlank() || it == "null" }
                                ?: "Jam operasional belum tersedia",
                            description = row.optString("description")
                                .takeUnless { it.isBlank() || it == "null" }
                                ?: "Informasi tempat belum tersedia.",
                            facilities = facilities,
                            minOrderK = row.optInt("min_order", 0) / 1000,
                            tags = cardTags,
                            cardTags = cardTags,
                            latitude = latitude,
                            longitude = longitude,
                            imageUrl = row.optString("image_url")
                                .takeUnless { it.isBlank() || it == "null" }
                        )
                    )
                }
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun JSONObject.stringList(key: String): List<String> {
        val values = optJSONArray(key) ?: return emptyList()
        return buildList {
            for (index in 0 until values.length()) {
                values.optString(index)
                    .takeUnless { it.isBlank() || it == "null" }
                    ?.let(::add)
            }
        }
    }

    private fun String.toVibe(): Vibe = when (uppercase()) {
        "QUIET", "TENANG" -> Vibe.QUIET
        "STUDY", "NUGAS" -> Vibe.STUDY
        "LIVELY", "RAMAI" -> Vibe.LIVELY
        else -> Vibe.UNKNOWN
    }
}


