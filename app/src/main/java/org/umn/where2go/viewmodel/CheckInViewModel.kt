package org.umn.where2go.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.umn.where2go.data.Place
import org.umn.where2go.data.SupabaseCheckInRepository
import org.umn.where2go.data.Vibe
import java.util.UUID

object CheckInOptions {
    val vibes = listOf("Tenang", "Ramai", "Fokus")
    val comforts = listOf("AC Dingin", "Kursi Nyaman", "Meja Luas", "Colokan Ada")
    val activities = listOf("Cocok nugas", "Nongkrong", "Meeting", "Solo Time", "Date")
    val prices = listOf("Affordable", "Mid-range", "Premium")
    const val MAX_COMMENT = 200
}

class CheckInViewModel : ViewModel() {
    var vibe by mutableStateOf<String?>(null); private set
    var comforts by mutableStateOf(emptySet<String>()); private set
    var activities by mutableStateOf(emptySet<String>()); private set
    var price by mutableStateOf<String?>(null); private set
    var placeRating by mutableStateOf<Int?>(null); private set
    var comment by mutableStateOf(""); private set
    var submitted by mutableStateOf(false); private set
    var isSubmitting by mutableStateOf(false); private set
    var showSuccess by mutableStateOf(false); private set
    var errorMessage by mutableStateOf<String?>(null); private set
    private var prefilled = false
    private var pendingRequestId: String? = null

    fun prefill(place: Place) {
        if (prefilled) return
        prefilled = true
        vibe = when (place.vibe) {
            Vibe.QUIET -> "Tenang"
            Vibe.LIVELY -> "Ramai"
            Vibe.STUDY -> "Fokus"
            Vibe.UNKNOWN -> null
        }
        price = when {
            place.minOrderK < 0 -> null
            place.minOrderK <= 15 -> "Affordable"
            place.minOrderK <= 35 -> "Mid-range"
            else -> "Premium"
        }
    }

    val vibeError: String?
        get() = if (submitted && vibe == null) "Pilih satu vibe" else null
    val comfortError: String?
        get() = if (submitted && comforts.isEmpty()) "Pilih minimal satu kenyamanan" else null
    val activityError: String?
        get() = if (submitted && activities.isEmpty()) "Pilih minimal satu aktivitas" else null
    val priceError: String?
        get() = if (submitted && price == null) "Pilih satu kategori harga" else null

    fun selectVibe(value: String) { pendingRequestId = null; vibe = value }

    fun toggleComfort(value: String) {
        pendingRequestId = null
        comforts = if (value in comforts) comforts - value else comforts + value
    }

    fun toggleActivity(value: String) {
        pendingRequestId = null
        activities = if (value in activities) activities - value else activities + value
    }

    fun selectPrice(value: String) { pendingRequestId = null; price = value }
    fun selectPlaceRating(value: Int) { pendingRequestId = null; placeRating = value }

    fun onComment(value: String) {
        if (value.length <= CheckInOptions.MAX_COMMENT) {
            pendingRequestId = null
            comment = value
        }
    }

    fun submit(context: Context, placeId: Long) {
        submitted = true
        val valid = vibe != null && comforts.isNotEmpty() && activities.isNotEmpty() && price != null
        if (!valid || isSubmitting) return

        val selectedVibe = vibe ?: return
        val selectedPrice = price ?: return
        val requestId = pendingRequestId ?: UUID.randomUUID().toString().also {
            pendingRequestId = it
        }
        val selectedComforts = comforts.toList()
        val selectedActivities = activities.toList()
        val selectedComment = comment
        val selectedRating = placeRating

        viewModelScope.launch {
            isSubmitting = true
            errorMessage = null
            showSuccess = false
            try {
                withContext(Dispatchers.IO) {
                    SupabaseCheckInRepository.submit(
                        context = context.applicationContext,
                        placeId = placeId,
                        vibe = selectedVibe.toDatabaseVibe(),
                        comfortTags = selectedComforts,
                        activityTags = selectedActivities,
                        priceLevel = selectedPrice.toDatabasePriceLevel(),
                        comment = selectedComment,
                        placeRating = selectedRating,
                        clientRequestId = requestId
                    )
                }
                showSuccess = true
                pendingRequestId = null
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                errorMessage = error.message ?: "Check-in gagal disimpan. Periksa koneksi lalu coba lagi."
            } finally {
                isSubmitting = false
            }
        }
    }

    fun dismissSuccess() {
        showSuccess = false
    }

    private fun String.toDatabaseVibe(): String = when (this) {
        "Tenang" -> "QUIET"
        "Ramai" -> "LIVELY"
        "Fokus" -> "STUDY"
        else -> error("Vibe tidak dikenal: $this")
    }

    private fun String.toDatabasePriceLevel(): String = when (this) {
        "Affordable" -> "LOW"
        "Mid-range" -> "MEDIUM"
        "Premium" -> "HIGH"
        else -> error("Kategori harga tidak dikenal: $this")
    }
}
