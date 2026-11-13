package org.umn.where2go.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import org.umn.where2go.data.Place
import org.umn.where2go.data.Vibe

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
        get() = if (submitted && vibe == null) "Pilih satu vibe" else null
    val comfortError: String?
        get() = if (submitted && comforts.isEmpty()) "Pilih minimal satu kenyamanan" else null
    val activityError: String?
        get() = if (submitted && activities.isEmpty()) "Pilih minimal satu aktivitas" else null
    val priceError: String?
        get() = if (submitted && price == null) "Pilih satu kategori harga" else null

    fun selectVibe(value: String) { vibe = value }

    fun toggleComfort(value: String) {
        comforts = if (value in comforts) comforts - value else comforts + value
    }

    fun toggleActivity(value: String) {
        activities = if (value in activities) activities - value else activities + value
    }

    fun selectPrice(value: String) { price = value }

    fun onComment(value: String) {
        if (value.length <= CheckInOptions.MAX_COMMENT) comment = value
    }

    @Suppress("UNUSED_PARAMETER")
    fun submit(context: Context, placeId: Long) {
        submitted = true
        val valid = vibe != null && comforts.isNotEmpty() && activities.isNotEmpty() && price != null
        if (!valid) return

        // Simulasi UI sementara: data belum dikirim atau disimpan ke database.
        errorMessage = null
        isSubmitting = false
        showSuccess = true
    }

    fun dismissSuccess() {
        showSuccess = false
    }
}
