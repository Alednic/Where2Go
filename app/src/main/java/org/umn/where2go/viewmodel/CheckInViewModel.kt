package org.umn.where2go.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.umn.where2go.data.Place
import org.umn.where2go.data.Vibe
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object CheckInOptions {
    val vibes = listOf("Tenang", "Ramai", "Cozy", "Good Vibes", "Fokus")
    val comforts = listOf("AC Dingin", "Kursi Nyaman", "Meja Luas", "Colokan Ada")
    val activities = listOf("Cocok nugas", "Nongkrong", "Meeting", "Solo Time", "Date")
    val prices = listOf("Affordable", "Mid-range", "Premium")
    const val MAX_COMMENT = 200
}

class CheckInViewModel : ViewModel() {
    var vibes by mutableStateOf(emptySet<String>()); private set
    var comforts by mutableStateOf(emptySet<String>()); private set
    var activities by mutableStateOf(emptySet<String>()); private set
    var price by mutableStateOf<String?>(null); private set
    var comment by mutableStateOf(""); private set
    var submitted by mutableStateOf(false); private set
    var isSubmitting by mutableStateOf(false); private set
    var showSuccess by mutableStateOf(false); private set
    private var prefilled = false

    fun prefill(place: Place) {
        if (prefilled) return
        prefilled = true
        vibes = setOf(
            when (place.vibe) {
                Vibe.QUIET -> "Tenang"
                Vibe.LIVELY -> "Ramai"
                Vibe.STUDY -> "Fokus"
            }
        )
        price = when {
            place.minOrderK <= 15 -> "Affordable"
            place.minOrderK <= 35 -> "Mid-range"
            else -> "Premium"
        }
    }

    val vibeError: String?
        get() =
            if (submitted && vibes.isEmpty()) "Pilih minimal satu vibe" else null
    val comfortError: String?
        get() =
            if (submitted && comforts.isEmpty()) "Pilih minimal satu kenyamanan" else null
    val activityError: String?
        get() =
            if (submitted && activities.isEmpty()) "Pilih minimal satu aktivitas" else null
    val priceError: String?
        get() =
            if (submitted && price == null) "Pilih satu kategori harga" else null

    fun toggleVibe(v: String) {
        vibes = if (v in vibes) vibes - v else vibes + v
    }

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

    fun submit() {
        submitted = true
        val valid = vibes.isNotEmpty() && comforts.isNotEmpty() &&
                activities.isNotEmpty() && price != null
        if (!valid) return
        viewModelScope.launch {
            isSubmitting = true
            delay(1000)
            isSubmitting = false
            showSuccess = true
        }
    }

    fun dismissSuccess() {
        showSuccess = false
    }
}