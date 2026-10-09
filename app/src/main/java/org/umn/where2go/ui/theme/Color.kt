package org.umn.where2go.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/*
 * Palet diambil dari mockup Where2Go (PROJECT_MAP_KELOMPOK_6):
 *  - Vermilion (merah-oranye) #E8451D : tombol utama, judul, tab aktif, header Check-in
 *  - Peach #FFE7DD                    : latar slide / permukaan lembut
 *  - Lavender #C6C5FF                 : label, kartu, aksen sekunder
 *  - Ungu #7B6EF6                     : ikon lokasi, tombol Check-in tengah (FAB)
 */
val Vermilion = Color(0xFFE8451D)
val VermilionDark = Color(0xFFB8300F)
val Peach = Color(0xFFFFE7DD)
val Coral = Color(0xFFF6AE99)
val Lavender = Color(0xFFC6C5FF)
val LavenderSoft = Color(0xFFE4E4FC)
val Violet = Color(0xFF7B6EF6)
val Gold = Color(0xFFFFD27A)

val LightColors = lightColorScheme(
    primary = Vermilion, onPrimary = Color.White,
    primaryContainer = Peach, onPrimaryContainer = Color(0xFF5A1708),
    secondary = Violet, onSecondary = Color.White,
    secondaryContainer = Lavender, onSecondaryContainer = Color(0xFF241E6B),
    tertiary = Color(0xFFB8860B), onTertiary = Color.White,
    tertiaryContainer = Gold, onTertiaryContainer = Color(0xFF3D2A00),
    background = Color.White, onBackground = Color(0xFF1C1B1B),
    surface = Color.White, onSurface = Color(0xFF1C1B1B),
    surfaceVariant = Color(0xFFFFF1EB), onSurfaceVariant = Color(0xFF6B5A55),
    outline = Color(0xFFD8C6BF),
    error = Color(0xFFBA1A1A)
)

val DarkColors = darkColorScheme(
    primary = Color(0xFFFF8A68), onPrimary = Color(0xFF5A1708),
    primaryContainer = VermilionDark, onPrimaryContainer = Peach,
    secondary = Color(0xFFB9B2FF), onSecondary = Color(0xFF241E6B),
    secondaryContainer = Color(0xFF3F3896), onSecondaryContainer = Lavender,
    tertiary = Color(0xFFFFD27A), onTertiary = Color(0xFF3D2A00),
    tertiaryContainer = Color(0xFF5C4300), onTertiaryContainer = Gold,
    background = Color(0xFF1A1210), onBackground = Color(0xFFF3E3DD),
    surface = Color(0xFF1A1210), onSurface = Color(0xFFF3E3DD),
    surfaceVariant = Color(0xFF3A2B27), onSurfaceVariant = Color(0xFFD8C6BF),
    outline = Color(0xFF9C8A84)
)

// Warna "vibe" (diambil dari pin peta dan bar "Cocok untuk" di mockup)
val VibeQuietColor = Color(0xFF0F9D8A)   // teal  -> Tenang
val VibeStudyColor = Violet              // ungu  -> Cocok Nugas
val VibeLivelyColor = Color(0xFFE69B00)  // amber -> Ramai