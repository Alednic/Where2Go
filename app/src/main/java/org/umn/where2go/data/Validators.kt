package org.umn.where2go.data

import android.util.Patterns

/** Mengembalikan pesan error, atau null jika valid. */
object Validators {
    fun required(value: String, label: String): String? =
        if (value.isBlank()) "$label wajib diisi" else null

    fun email(value: String): String? = when {
        value.isBlank() -> "Email wajib diisi"
        !Patterns.EMAIL_ADDRESS.matcher(value.trim()).matches() -> "Format email tidak valid"
        else -> null
    }

    fun password(value: String): String? = when {
        value.isBlank() -> "Password wajib diisi"
        value.length < 6 -> "Password minimal 6 karakter"
        else -> null
    }

    fun number(value: String, label: String, min: Int, max: Int): String? {
        if (value.isBlank()) return "$label wajib diisi"
        val n = value.toIntOrNull() ?: return "$label hanya boleh berisi angka"
        return if (n < min || n > max) "$label harus antara $min dan $max" else null
    }

    fun minLength(value: String, label: String, min: Int): String? = when {
        value.isBlank() -> "$label wajib diisi"
        value.trim().length < min -> "$label minimal $min karakter"
        else -> null
    }
}