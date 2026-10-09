package org.umn.where2go.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) { delay(1800); onFinished() }
    val on = MaterialTheme.colorScheme.onPrimary
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Where2Go", style = MaterialTheme.typography.displayLarge, color = on)
            Spacer(Modifier.height(8.dp))
            Text("Find your vibe. Find your space.",
                style = MaterialTheme.typography.bodyLarge, color = on.copy(alpha = 0.85f))
            Spacer(Modifier.height(32.dp))
            CircularProgressIndicator(color = on)
        }
    }
}