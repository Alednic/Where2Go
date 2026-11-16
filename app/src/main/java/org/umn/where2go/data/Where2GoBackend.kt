package org.umn.where2go.data

import android.content.Context
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject
import org.umn.where2go.BuildConfig
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.UUID

/** Minimal Supabase Auth and PostgREST client. Uses only the app's public publishable key. */
object Where2GoBackend {
    private const val PREFS = "where2go_supabase_session"
    private const val CONNECT_TIMEOUT = 15_000
    private const val READ_TIMEOUT = 20_000

    data class Session(val accessToken: String, val refreshToken: String, val userId: String, val expiresAt: Long, val isAnonymous: Boolean = false)
    data class Profile(val id: String, val name: String, val email: String, val username: String, val bio: String, val role: String, val avatarUrl: String = "", val points: Int = 0)
    data class PlaceSubmission(val id: Long, val name: String, val category: String, val address: String, val status: String)
    data class Rank(val name: String, val score: Int, val place: String = "")
    data class BusinessStats(val placeId: Long, val name: String, val checkIns: Int, val leadingVibe: String)

    fun signIn(context: Context, email: String, password: String): Session {
        val result = request("/auth/v1/token?grant_type=password", "POST", JSONObject()
            .put("email", email.trim()).put("password", password), null)
        return parseAndSave(context, result)
    }

    fun signUp(context: Context, name: String, email: String, password: String): Session? {
        val result = request("/auth/v1/signup", "POST", JSONObject()
            .put("email", email.trim()).put("password", password)
            .put("data", JSONObject().put("display_name", name.trim())), null)
        val json = parseSuccess(result)
        // Supabase may require email confirmation; in that case it returns a user but no tokens.
        if (json.optString("access_token").isBlank()) return null
        return saveSession(context, json)
    }

    fun signOut(context: Context) {
        val session = currentSession(context)
        if (session != null) runCatching { request("/auth/v1/logout", "POST", null, session.accessToken) }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply()
    }

    fun currentSession(context: Context): Session? {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val token = p.getString("access_token", null) ?: return null
        val refresh = p.getString("refresh_token", null) ?: return null
        val id = p.getString("user_id", null) ?: return null
        val expiration = p.getLong("expires_at", 0L)
        if (expiration > System.currentTimeMillis() + 60_000) {
            return Session(token, refresh, id, expiration, p.getBoolean("is_anonymous", true))
        }
        return runCatching {
            val result = request("/auth/v1/token?grant_type=refresh_token", "POST",
                JSONObject().put("refresh_token", refresh), null)
            parseAndSave(context, result)
        }.getOrNull()
    }

    fun saveProfile(context: Context, name: String, username: String, bio: String, avatarUrl: String? = null) {
        val s = requireSession(context)
        val body = JSONObject().put("id", s.userId).put("display_name", name.trim())
            .put("username", username.trim()).put("bio", bio.trim())
        avatarUrl?.let { body.put("avatar_url", it) }
        val response = request("/rest/v1/profiles?on_conflict=id", "POST", body, s.accessToken,
            "resolution=merge-duplicates,return=minimal")
        ensureSuccess(response, "Profil gagal disimpan.")
    }

    fun preferences(context: Context): Set<String> {
        val s = requireSession(context)
        val response = request("/rest/v1/user_preferences?select=tags&user_id=eq.${s.userId}&limit=1", "GET", null, s.accessToken)
        ensureSuccess(response, "Preferensi gagal dimuat.")
        return JSONArray(response.body).optJSONObject(0)?.optJSONArray("tags")?.let { a ->
            (0 until a.length()).mapNotNull { a.optString(it).takeIf(String::isNotBlank) }.toSet()
        } ?: emptySet()
    }

    fun savePreferences(context: Context, tags: Set<String>) {
        val s = requireSession(context)
        val body = JSONObject().put("user_id", s.userId).put("tags", JSONArray(tags.toList()))
        val response = request("/rest/v1/user_preferences?on_conflict=user_id", "POST", body, s.accessToken,
            "resolution=merge-duplicates,return=minimal")
        ensureSuccess(response, "Preferensi gagal disimpan.")
    }

    fun profile(context: Context): Profile? {
        val s = currentSession(context) ?: return null
        val response = request("/rest/v1/profiles?select=id,display_name,email,username,bio,role,avatar_url,points&id=eq.${s.userId}&limit=1",
            "GET", null, s.accessToken)
        ensureSuccess(response, "Profil gagal dimuat.")
        val row = JSONArray(response.body).optJSONObject(0) ?: return null
        return Profile(row.optString("id"), row.optString("display_name", "Pengguna Where2Go"),
            row.optString("email"), row.optString("username"), row.optString("bio"), row.optString("role", "user"),
            row.optString("avatar_url"), row.optInt("points"))
    }

    fun favoritePlaceIds(context: Context): Set<Int> {
        val s = currentSession(context) ?: return emptySet()
        val response = request("/rest/v1/favorite_places?select=place_id&user_id=eq.${s.userId}", "GET", null, s.accessToken)
        ensureSuccess(response, "Favorit gagal dimuat.")
        val rows = JSONArray(response.body)
        return (0 until rows.length()).mapNotNull { rows.optJSONObject(it)?.optInt("place_id") }.toSet()
    }

    fun setFavorite(context: Context, placeId: Int, favorite: Boolean) {
        val s = requireSession(context)
        val response = if (favorite) {
            request("/rest/v1/favorite_places?on_conflict=user_id,place_id", "POST",
                JSONObject().put("user_id", s.userId).put("place_id", placeId), s.accessToken,
                "resolution=ignore-duplicates,return=minimal")
        } else {
            request("/rest/v1/favorite_places?user_id=eq.${s.userId}&place_id=eq.$placeId", "DELETE", null, s.accessToken)
        }
        ensureSuccess(response, "Favorit gagal disimpan.")
    }

    fun placeReviews(context: Context, placeId: Int): List<org.umn.where2go.data.Review> {
        val s = currentSession(context) ?: return emptyList()
        val response = request("/rest/v1/rpc/place_reviews", "POST",
            JSONObject().put("requested_place_id", placeId), s.accessToken)
        ensureSuccess(response, "Ulasan gagal dimuat.")
        val rows = JSONArray(response.body)
        return (0 until rows.length()).mapNotNull { index ->
            rows.optJSONObject(index)?.let { row ->
                val createdAt = row.optString("created_at")
                org.umn.where2go.data.Review(
                    id = row.optLong("review_id").toInt(), placeId = placeId,
                    author = row.optString("author", "Pengguna Where2Go"),
                    vibe = when (row.optString("vibe")) {
                        "QUIET" -> org.umn.where2go.data.Vibe.QUIET
                        "STUDY" -> org.umn.where2go.data.Vibe.STUDY
                        "LIVELY" -> org.umn.where2go.data.Vibe.LIVELY
                        else -> org.umn.where2go.data.Vibe.UNKNOWN
                    },
                    comment = row.optString("comment"), timeAgo = createdAt.take(10)
                )
            }
        }
    }

    fun uploadProfilePhoto(context: Context, uri: Uri): String = uploadPhoto(context, uri, "profile-photos")

    fun submitBusiness(context: Context, name: String, category: String, address: String,
                       phone: String, description: String, openingHours: String, facilities: Set<String>,
                       cardTags: Set<String>, minOrder: Int, latitude: Double?, longitude: Double?, imageUrl: String?) {
        val s = requireSession(context)
        val body = JSONObject().put("owner_id", s.userId).put("name", name.trim())
            .put("category", category).put("address", address.trim()).put("phone", phone.trim())
            .put("description", description.trim())
            .put("opening_hours", openingHours.trim())
            .put("facilities", JSONArray(facilities.toList()))
            .put("card_tags", JSONArray(cardTags.toList()))
            .put("min_order", minOrder)
            .put("image_url", imageUrl ?: JSONObject.NULL)
            .put("latitude", latitude ?: JSONObject.NULL).put("longitude", longitude ?: JSONObject.NULL)
            .put("status", "PENDING")
        val response = request("/rest/v1/place_submissions", "POST", body, s.accessToken, "return=minimal")
        ensureSuccess(response, "Pendaftaran tempat gagal disimpan.")
    }

    fun uploadBusinessPhoto(context: Context, uri: Uri): String {
        return uploadPhoto(context, uri, "place-photos")
    }

    private fun uploadPhoto(context: Context, uri: Uri, bucket: String): String {
        val session = requireSession(context)
        val contentType = context.contentResolver.getType(uri)?.takeIf { it == "image/jpeg" || it == "image/png" }
            ?: throw IOException("Foto harus berformat JPG atau PNG.")
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: throw IOException("Foto tidak dapat dibaca.")
        if (bytes.size > 5 * 1024 * 1024) throw IOException("Ukuran foto maksimal 5 MB.")
        val ext = if (contentType == "image/png") "png" else "jpg"
        val objectPath = "${session.userId}/${UUID.randomUUID()}.$ext"
        val base = BuildConfig.SUPABASE_URL.trimEnd('/')
        val conn = (URL("$base/storage/v1/object/$bucket/$objectPath").openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"; connectTimeout = CONNECT_TIMEOUT; readTimeout = READ_TIMEOUT; doOutput = true
            setRequestProperty("apikey", BuildConfig.SUPABASE_PUBLISHABLE_KEY)
            setRequestProperty("Authorization", "Bearer ${session.accessToken}")
            setRequestProperty("Content-Type", contentType)
            setRequestProperty("x-upsert", "false")
        }
        try {
            conn.outputStream.use { it.write(bytes) }
            val code = conn.responseCode
            if (code !in 200..299) {
                val error = conn.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
                val detail = runCatching { JSONObject(error).optString("message") }.getOrNull().orEmpty()
                throw IOException(detail.ifBlank { "Upload foto gagal (HTTP $code)." })
            }
        } finally { conn.disconnect() }
        return "$base/storage/v1/object/public/$bucket/$objectPath"
    }

    fun myBusinesses(context: Context): List<PlaceSubmission> {
        val s = currentSession(context) ?: return emptyList()
        val response = request("/rest/v1/place_submissions?select=id,name,category,address,status&owner_id=eq.${s.userId}&order=created_at.desc",
            "GET", null, s.accessToken)
        ensureSuccess(response, "Daftar usaha gagal dimuat.")
        val rows = JSONArray(response.body)
        return (0 until rows.length()).mapNotNull { i -> rows.optJSONObject(i)?.let { PlaceSubmission(
            it.optLong("id"), it.optString("name"), it.optString("category"), it.optString("address"), it.optString("status")) } }
    }

    fun pendingBusinesses(context: Context): List<PlaceSubmission> {
        val s = requireSession(context)
        val response = request("/rest/v1/place_submissions?select=id,name,category,address,status&status=eq.PENDING&order=created_at.asc",
            "GET", null, s.accessToken)
        ensureSuccess(response, "Daftar peninjauan gagal dimuat.")
        val rows = JSONArray(response.body)
        return (0 until rows.length()).mapNotNull { i -> rows.optJSONObject(i)?.let { PlaceSubmission(
            it.optLong("id"), it.optString("name"), it.optString("category"), it.optString("address"), it.optString("status")) } }
    }

    fun reviewBusiness(context: Context, id: Long, approve: Boolean) {
        val s = requireSession(context)
        val result = request("/rest/v1/rpc/review_place_submission", "POST",
            JSONObject().put("submission_id", id).put("approved", approve), s.accessToken)
        ensureSuccess(result, "Perubahan status tidak berhasil disimpan.")
    }

    fun businessStats(context: Context): List<BusinessStats> {
        val s = requireSession(context)
        val result = request("/rest/v1/rpc/owner_dashboard_stats", "POST", JSONObject(), s.accessToken)
        ensureSuccess(result, "Statistik usaha gagal dimuat.")
        val rows = JSONArray(result.body)
        return (0 until rows.length()).mapNotNull { i -> rows.optJSONObject(i)?.let { row ->
            BusinessStats(row.optLong("place_id"), row.optString("name"), row.optInt("check_ins"), row.optString("leading_vibe", "Belum ada"))
        } }
    }

    fun rankings(context: Context, contributors: Boolean): List<Rank> {
        val session = currentSession(context) ?: return emptyList()
        val rpc = if (contributors) "weekly_top_contributors" else "weekly_top_places"
        val result = request("/rest/v1/rpc/$rpc", "POST", JSONObject(), session.accessToken)
        ensureSuccess(result, "Ranking gagal dimuat.")
        val rows = JSONArray(result.body)
        return (0 until rows.length()).mapNotNull { i -> rows.optJSONObject(i)?.let { row ->
            Rank(row.optString(if (contributors) "display_name" else "name", "Tempat"),
                row.optInt("check_ins"), row.optString("address"))
        } }
    }

    private fun requireSession(context: Context) = currentSession(context)
        ?: throw IOException("Sesi tidak tersedia. Silakan masuk kembali.")

    private fun parseAndSave(context: Context, result: HttpResult): Session = saveSession(context, parseSuccess(result))

    private fun saveSession(context: Context, json: JSONObject): Session {
        val user = json.optJSONObject("user") ?: throw IOException("Supabase tidak mengembalikan data akun.")
        val access = json.optString("access_token")
        val refresh = json.optString("refresh_token")
        val id = user.optString("id")
        if (access.isBlank() || refresh.isBlank() || id.isBlank()) throw IOException("Sesi masuk tidak lengkap.")
        val exp = json.optLong("expires_at").takeIf { it > 0 } ?: System.currentTimeMillis()/1000 + json.optLong("expires_in", 3600)
        val isAnonymous = user.optBoolean("is_anonymous") || user.optJSONObject("app_metadata")?.optString("provider") == "anonymous"
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString("access_token", access).putString("refresh_token", refresh)
            .putString("user_id", id).putBoolean("is_anonymous", isAnonymous)
            .putLong("expires_at", exp * 1000).commit()
        return Session(access, refresh, id, exp * 1000, isAnonymous)
    }

    private data class HttpResult(val status: Int, val body: String)
    private fun parseSuccess(result: HttpResult): JSONObject {
        ensureSuccess(result, "Autentikasi Supabase gagal.")
        return JSONObject(result.body)
    }
    private fun ensureSuccess(result: HttpResult, fallback: String) {
        if (result.status !in 200..299) {
            val message = runCatching {
                val json = JSONObject(result.body)
                json.optString("message")
                    .ifBlank { json.optString("msg") }
                    .ifBlank { json.optString("error_description") }
                    .ifBlank { json.optString("error") }
            }.getOrNull().orEmpty()
            throw IOException(message.ifBlank { fallback })
        }
    }
    private fun request(path: String, method: String, body: JSONObject?, token: String?, prefer: String? = null): HttpResult {
        val base = BuildConfig.SUPABASE_URL.trimEnd('/')
        val key = BuildConfig.SUPABASE_PUBLISHABLE_KEY
        require(base.startsWith("https://") && key.isNotBlank()) { "Konfigurasi Supabase belum tersedia." }
        val c = (URL(base + path).openConnection() as HttpURLConnection).apply {
            requestMethod = method; connectTimeout = CONNECT_TIMEOUT; readTimeout = READ_TIMEOUT
            setRequestProperty("apikey", key); setRequestProperty("Accept", "application/json")
            if (token != null) setRequestProperty("Authorization", "Bearer $token")
            if (prefer != null) setRequestProperty("Prefer", prefer)
            if (body != null) { doOutput = true; setRequestProperty("Content-Type", "application/json") }
        }
        try {
            body?.let { c.outputStream.use { out -> out.write(it.toString().toByteArray(Charsets.UTF_8)) } }
            val code = c.responseCode
            val stream = if (code in 200..299) c.inputStream else c.errorStream
            return HttpResult(code, stream?.bufferedReader()?.use { it.readText() }.orEmpty())
        } finally { c.disconnect() }
    }
}
