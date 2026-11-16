package org.umn.where2go.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import org.umn.where2go.BuildConfig
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/** Membuat sesi anonim Supabase dan menyimpan check-in melalui Data API. */
object SupabaseCheckInRepository {
    private const val PREFS = "where2go_supabase_session"
    private const val CONNECT_TIMEOUT_MS = 15_000
    private const val READ_TIMEOUT_MS = 20_000

    data class Session(val accessToken: String, val userId: String)

    @Synchronized
    fun submit(
        context: Context,
        placeId: Long,
        vibe: String,
        comfortTags: List<String>,
        activityTags: List<String>,
        priceLevel: String,
        comment: String,
        placeRating: Int?,
        clientRequestId: String
    ) {
        val appContext = context.applicationContext
        val session = getOrCreateSession(appContext)
        val body = JSONObject()
            .put("place_id", placeId)
            .put("user_id", session.userId)
            .put("vibe", vibe)
            // JSONObject perlu menerima JSONArray; List Kotlin langsung berubah menjadi string.
            .put("comfort_tags", JSONArray(comfortTags))
            .put("activity_tags", JSONArray(activityTags))
            .put("price_level", priceLevel)
            .put("place_rating", placeRating ?: JSONObject.NULL)
            .put("comment", comment.trim().takeIf { it.isNotEmpty() } ?: JSONObject.NULL)
            .put("client_request_id", clientRequestId)

        val response = request(
            path = "/rest/v1/check_ins",
            method = "POST",
            body = body,
            accessToken = session.accessToken,
            prefer = "return=minimal"
        )
        if (response.statusCode in 200..299) return

        // Jika server sempat menyimpan tetapi responsnya hilang, request ID mencegah baris ganda.
        if (response.statusCode == 409 && requestAlreadySaved(session, clientRequestId)) return
        throw IOException(response.errorMessage("Check-in gagal disimpan."))
    }

    private fun getOrCreateSession(context: Context): Session {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val accessToken = prefs.getString("access_token", null)
        val refreshToken = prefs.getString("refresh_token", null)
        val userId = prefs.getString("user_id", null)
        val expiresAt = prefs.getLong("expires_at", 0L)

        if (!accessToken.isNullOrBlank() && !userId.isNullOrBlank() &&
            expiresAt > System.currentTimeMillis() + 60_000L
        ) {
            return Session(accessToken, userId)
        }

        if (!refreshToken.isNullOrBlank()) {
            val refreshed = request(
                path = "/auth/v1/token?grant_type=refresh_token",
                method = "POST",
                body = JSONObject().put("refresh_token", refreshToken),
                accessToken = null
            )
            if (refreshed.statusCode in 200..299) {
                return saveSession(prefs, refreshed.body)
            }
            throw IOException(refreshed.errorMessage("Sesi Supabase tidak dapat diperbarui. Coba lagi."))
        }

        val created = request(
            path = "/auth/v1/signup",
            method = "POST",
            body = JSONObject().put("data", JSONObject().put("source", "where2go-android")),
            accessToken = null
        )
        if (created.statusCode !in 200..299) {
            throw IOException(
                created.errorMessage(
                    "Gagal membuat sesi tamu. Aktifkan Anonymous Sign-Ins di Supabase Auth."
                )
            )
        }
        return saveSession(prefs, created.body)
    }

    private fun saveSession(
        prefs: android.content.SharedPreferences,
        responseBody: String
    ): Session {
        val json = JSONObject(responseBody)
        val user = json.optJSONObject("user")
            ?: throw IOException("Respons autentikasi Supabase tidak berisi user.")
        val accessToken = json.optString("access_token")
        val refreshToken = json.optString("refresh_token")
        val userId = user.optString("id")
        if (accessToken.isBlank() || refreshToken.isBlank() || userId.isBlank()) {
            throw IOException("Supabase tidak mengirim sesi lengkap untuk pengguna.")
        }
        val expiresAt = json.optLong("expires_at").takeIf { it > 0L }
            ?: (System.currentTimeMillis() / 1000L + json.optLong("expires_in", 3600L))

        val saved = prefs.edit()
            .putString("access_token", accessToken)
            .putString("refresh_token", refreshToken)
            .putString("user_id", userId)
            .putLong("expires_at", expiresAt * 1000L)
            .commit()
        if (!saved) throw IOException("Sesi pengguna tidak dapat disimpan di perangkat.")
        return Session(accessToken, userId)
    }

    private fun requestAlreadySaved(session: Session, requestId: String): Boolean {
        val encodedId = URLEncoder.encode(requestId, Charsets.UTF_8.name())
        val encodedUserId = URLEncoder.encode(session.userId, Charsets.UTF_8.name())
        val response = request(
            path = "/rest/v1/check_ins?select=id&user_id=eq.$encodedUserId" +
                "&client_request_id=eq.$encodedId&limit=1",
            method = "GET",
            body = null,
            accessToken = session.accessToken
        )
        return response.statusCode in 200..299 && response.body.trim().let {
            it.startsWith("[") && it != "[]"
        }
    }

    private data class HttpResult(val statusCode: Int, val body: String) {
        fun errorMessage(fallback: String): String = runCatching {
            val json = JSONObject(body)
            listOf("message", "error_description", "details", "hint")
                .firstNotNullOfOrNull { key -> json.optString(key).takeIf(String::isNotBlank) }
        }.getOrNull() ?: fallback
    }

    private fun request(
        path: String,
        method: String,
        body: JSONObject?,
        accessToken: String?,
        prefer: String? = null
    ): HttpResult {
        val baseUrl = BuildConfig.SUPABASE_URL.trimEnd('/')
        val apiKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY
        require(baseUrl.startsWith("https://") && apiKey.isNotBlank()) {
            "URL atau publishable key Supabase belum diatur."
        }

        val connection = (URL(baseUrl + path).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = CONNECT_TIMEOUT_MS
            readTimeout = READ_TIMEOUT_MS
            setRequestProperty("apikey", apiKey)
            setRequestProperty("Accept", "application/json")
            if (accessToken != null) setRequestProperty("Authorization", "Bearer $accessToken")
            if (body != null) {
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
            }
            if (prefer != null) setRequestProperty("Prefer", prefer)
        }

        try {
            body?.let { payload ->
                connection.outputStream.use { stream ->
                    stream.write(payload.toString().toByteArray(Charsets.UTF_8))
                }
            }
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val responseBody = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            return HttpResult(status, responseBody)
        } finally {
            connection.disconnect()
        }
    }
}
