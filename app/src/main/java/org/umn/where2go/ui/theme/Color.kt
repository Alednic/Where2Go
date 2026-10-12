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

// ---- Warna komponen layar Place Detail (diukur dari mockup) ----
val TitleInk = Color(0xFF111827)        // judul "Kopi Kenangan"
val SubtleGray = Color(0xFFB8BCC3)      // alamat
val StatCardBg = Color(0xFFF0F0F0)      // kartu 4.7 / 248 / Rp. 12K
val ChartBar = Color(0xFFEEEDFE)        // batang "Jam Ramai" & kartu Pagi/Siang/Malam
val ChartAxisBg = Color(0xFFFFF5EE)     // strip label jam
val DaypartIcon = Color(0xFF3A30A0)
val TrackGray = Color(0xFFD9D9D9)       // track progress "Cocok untuk"
val BarNugas = Color(0xFF10B981)
val BarHangout = Color(0xFF7B6EF6)
val BarMeeting = Color(0xFFE59A12)
val LavenderButton = Color(0xFFCBC4FB)  // tombol di kartu Kuliner

// Chip tag (latar, teks)
val TagTenangBg = Color(0xFFEEF2FF);  val TagTenangFg = Color(0xFF6366F1)
val TagWifiBg = Color(0xFFDCFCE7);    val TagWifiFg = Color(0xFF16A34A)
val TagVibesBg = Color(0xFFFEF9C3);   val TagVibesFg = Color(0xFFCA8A04)
val TagFocusBg = Color(0xFFFAC0AC);   val TagFocusFg = Color(0xFFE8431A)
val TagPriceBg = Color(0xFFFFE9C2);   val TagPriceFg = Color(0xFFE59A12)
