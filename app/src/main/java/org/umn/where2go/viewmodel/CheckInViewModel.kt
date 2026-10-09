package com.umn.where2go.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umn.where2go.data.Place
import com.umn.where2go.data.Validators
import com.umn.where2go.data.Vibe
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CheckInViewModel : ViewModel() {
    var vibe by mutableStateOf<Vibe?>(null); private set
    var visitors by mutableStateOf(""); private set
    var wifi by mutableStateOf(""); private set
    var review by mutableStateOf(""); private set
    var submitted by mutableStateOf(false); private set
    var isSubmitting by mutableStateOf(false); private set
    var showSuccess by mutableStateOf(false); private set
    private var prefilled = false

    /** Pre-fill form dari data dummy tempat yang dipilih */
    fun prefill(place: Place) {
        if (prefilled) return
        prefilled = true
        vibe = place.vibe
        wifi = place.wifiMbps.toString()
    }

    val vibeError: String? get() = if (submitted && vibe == null) "Pilih suasana tempat ini" else null
    val visitorsError: String? get() =
        if (submitted || visitors.isNotEmpty()) Validators.number(visitors, "Jumlah pengunjung", 1, 500) else null
    val wifiError: String? get() =
        if (submitted || wifi.isNotEmpty()) Validators.number(wifi, "Kecepatan Wi-Fi", 0, 1000) else null
    val reviewError: String? get() =
        if (submitted || review.isNotEmpty()) Validators.minLength(review, "Ulasan", 10) else null

    fun selectVibe(v: Vibe) { vibe = v }
    // hanya menerima digit: input huruf ditolak
    fun onVisitors(v: String) { if (v.length <= 3 && v.all { it.isDigit() }) visitors = v }
    fun onWifi(v: String) { if (v.length <= 4 && v.all { it.isDigit() }) wifi = v }
    fun onReview(v: String) { review = v }

    fun submit() {
        submitted = true
        val valid = vibe != null &&
                Validators.number(visitors, "x", 1, 500) == null &&
                Validators.number(wifi, "x", 0, 1000) == null &&
                Validators.minLength(review, "x", 10) == null
        if (!valid) return
        viewModelScope.launch {
            isSubmitting = true
            delay(1000)
            isSubmitting = false
            showSuccess = true
        }
    }

    fun dismissSuccess() { showSuccess = false }
}