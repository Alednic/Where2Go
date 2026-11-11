package org.umn.where2go.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.umn.where2go.data.Place
import org.umn.where2go.data.Vibe
import org.umn.where2go.data.SupabaseCheckInRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

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
    var comment by mutableStateOf(""); private set
    var submitted by mutableStateOf(false); private set
    var isSubmitting by mutableStateOf(false); private set
    var showSuccess by mutableStateOf(false); private set
    var errorMessage by mutableStateOf<String?>(null); private set
    private var prefilled = false

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
        get() =
            if (submitted && vibe == null) "Pilih satu vibe" else null
    val comfortError: String?
        get() =
            if (submitted && comforts.isEmpty()) "Pilih minimal satu kenyamanan" else null
    val activityError: String?
        get() =
            if (submitted && activities.isEmpty()) "Pilih minimal satu aktivitas" else null
    val priceError: String?
        get() =
            if (submitted && price == null) "Pilih satu kategori harga" else null

    fun selectVibe(v: String) { vibe = v }

    fun toggleComfort(v: String) {
        comforts = if (v in comforts) comforts - v else comforts + v
    }

    fun toggleActivity(v: String) {
        activities = if (v in activities) activities - v else activities + v
    }

    fun selectPrice(v: String) {
        price = v
    }

    fun onComment(v: String) {
        if (v.length <= CheckInOptions.MAX_COMMENT) comment = v
    }

    fun submit(context: Context, placeId: Long) {
        submitted = true
        val selectedVibe = vibe
        val selectedPrice = price
        val valid = selectedVibe != null && comforts.isNotEmpty() &&
                activities.isNotEmpty() && price != null
        if (!valid) return
        viewModelScope.launch {
            isSubmitting = true
            errorMessage = null
            try {
                SupabaseCheckInRepository.submit(
                    context = context.applicationContext,
                    placeId = placeId,
                    vibe = selectedVibe!!.toDatabaseVibe(),
                    comfortTags = comforts.toList(),
                    activityTags = activities.toList(),
                    priceLevel = selectedPrice!!.toDatabasePriceLevel(),
                    comment = comment
                )
                showSuccess = true
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
