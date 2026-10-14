package org.umn.where2go.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.umn.where2go.R

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    // pindah ke layar berikutnya setelah 2 detik
    LaunchedEffect(Unit) { delay(2000); onFinished() }

    Box(Modifier.fillMaxSize()) {
        // Lapisan 1: foto latar
        Image(
            painterResource(R.drawable.splash_bg), null,
            Modifier.fillMaxSize(), contentScale = ContentScale.Crop
        )
        // Lapisan 2: warna oranye di atas foto
        Box(
            Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.82f))
        )
        // Lapisan 3 sampai 5: konten
        Column(
            Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(0.8f))

            Text(
                "Where2Go", color = Color.White,
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(Modifier.height(32.dp))

            // maskot + pin lokasi di kiri atas
            Box(Modifier.fillMaxWidth(0.72f)) {
                Icon(
                    Icons.Filled.LocationOn, null,
                    tint = Color(0xFFFF9B6B),
                    modifier = Modifier.size(44.dp).align(Alignment.TopStart).offset(y = 24.dp)
                )
                Image(
                    painterResource(R.drawable.maskot_where2go_peta), "Maskot Where2Go",
                    Modifier.fillMaxWidth(0.8f).align(Alignment.Center),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(Modifier.height(28.dp))

            Text(
                "Find your vibe. Find your space.", color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.weight(1.2f))
        }
    }
}