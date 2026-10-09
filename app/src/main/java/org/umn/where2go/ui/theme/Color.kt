package org.umn.where2go.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val LightColors = lightColorScheme(
    primary = Color(0xFF0B6E6E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBFEFEA),
    onPrimaryContainer = Color(0xFF00201F),
    secondary = Color(0xFFE5654A),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDAD2),
    onSecondaryContainer = Color(0xFF3B0A02),
    tertiary = Color(0xFF8A6D00),
    tertiaryContainer = Color(0xFFFFE08A),
    background = Color(0xFFFAFDFC),
    onBackground = Color(0xFF191C1C),
    surface = Color(0xFFFAFDFC),
    onSurface = Color(0xFF191C1C),
    surfaceVariant = Color(0xFFDAE5E3),
    onSurfaceVariant = Color(0xFF3F4948),
    outline = Color(0xFF6F7978) )

val DarkColors = darkColorScheme(
    primary = Color(0xFF7FD6D2),
    onPrimary = Color(0xFF003735),
    primaryContainer = Color(0xFF00504E),
    onPrimaryContainer = Color(0xFFBFEFEA),
    secondary = Color(0xFFFFB4A3),
    onSecondary = Color(0xFF601500),
    secondaryContainer = Color(0xFF862A14),
    onSecondaryContainer = Color(0xFFFFDAD2),
    tertiary = Color(0xFFE9C349),
    tertiaryContainer = Color(0xFF695300),
    background = Color(0xFF0E1514),
    onBackground = Color(0xFFDDE4E2),
    surface = Color(0xFF0E1514),
    onSurface = Color(0xFFDDE4E2),
    surfaceVariant = Color(0xFF3F4948),
    onSurfaceVariant = Color(0xFFBEC9C7),
    outline = Color(0xFF899392)
)

// Warna semantik untuk indikator suasana tempat
val VibeQuietColor = Color(0xFF2E9E6B)
val VibeStudyColor = Color(0xFF3B7DD8)
val VibeLivelyColor = Color(0xFFE5654A)