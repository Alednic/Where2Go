@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package org.umn.where2go.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.umn.where2go.data.DummyData
import org.umn.where2go.data.Place
import org.umn.where2go.ui.components.PhotoBox
import org.umn.where2go.ui.components.color
import org.umn.where2go.ui.theme.*
import org.umn.where2go.viewmodel.CheckInOptions
import org.umn.where2go.viewmodel.CheckInViewModel

@Composable
fun CheckInScreen(
    placeId: Int, onBack: () -> Unit, onDone: () -> Unit,
    vm: CheckInViewModel = viewModel()
) {
    val place = remember(placeId) { DummyData.places.find { it.id == placeId } }
    LaunchedEffect(placeId) { place?.let(vm::prefill) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        topBar = { CheckInHeader(onBack) },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.background) {
                Button(
                    onClick = vm::submit, enabled = !vm.isSubmitting,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding()
                        .padding(16.dp).height(56.dp)
                ) {
                    if (vm.isSubmitting) {
                        CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = Color.White)
                    } else {
                        Text("Kirim Check-in", fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            if (place != null) PlaceSummaryCard(place) else Text("Tempat tidak ditemukan")

            ChoiceGroup("\u2728", "VIBE", CheckInOptions.vibes, vm.vibes, vm::toggleVibe,
                ChipTeal, ChipTealBg, vm.vibeError)
            ChoiceGroup("\uD83D\uDECB\uFE0F", "KENYAMANAN", CheckInOptions.comforts, vm.comforts,
                vm::toggleComfort, ChipTeal, ChipTealBg, vm.comfortError)
            ChoiceGroup("\uD83C\uDFAF", "AKTIVITAS", CheckInOptions.activities, vm.activities,
                vm::toggleActivity, ChipSky, ChipSkyBg, vm.activityError)
            ChoiceGroup("\uD83D\uDCB0", "HARGA", CheckInOptions.prices, setOfNotNull(vm.price),
                vm::selectPrice, ChipAmber, ChipPriceSelBg, vm.priceError)

            // ---- Komentar (opsional) ----
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("\uD83D\uDCAC KOMENTAR", fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleSmall)
                OutlinedTextField(
                    value = vm.comment, onValueChange = vm::onComment,
                    placeholder = { Text("Tuliskan saran atau kesanmu") },
                    supportingText = { Text("${vm.comment.length}/${CheckInOptions.MAX_COMMENT}") },
                    minLines = 5,
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Default),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ChartBar, unfocusedContainerColor = ChartBar,
                        focusedTextColor = TitleInk, unfocusedTextColor = TitleInk,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (vm.showSuccess) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Check-in berhasil!") },
            text = { Text("Terima kasih sudah berbagi vibe. Kamu mendapat +20 XP.") },
            confirmButton = {
                TextButton(onClick = { vm.dismissSuccess(); onDone() }) { Text("OK") }
            }
        )
    }
}

@Composable
private fun CheckInHeader(onBack: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding().padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack, "Kembali",
                    tint = Color.White, modifier = Modifier.size(28.dp)
                )
            }
            Text("Check-in", color = Color.White, style = MaterialTheme.typography.displayMedium)
        }
        Spacer(Modifier.height(4.dp))
        Text("Bagikan vibe tempat ke komunitas", color = Color.White,
            style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun PlaceSummaryCard(place: Place) {
    Surface(color = ChartBar, shape = RoundedCornerShape(20.dp), shadowElevation = 6.dp) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            PhotoBox(place.imageRes, Modifier.size(60.dp), fallback = place.vibe.color(),
                shape = RoundedCornerShape(14.dp))
            Column {
                Text(place.name, color = TitleInk, fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium)
                Text(place.address, color = SubtleGray, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

/** Satu grup chip: judul + tanda wajib (*) + chip yang membungkus + pesan error. */
@Composable
private fun ChoiceGroup(
    emoji: String, title: String, options: List<String>, selected: Set<String>,
    onToggle: (String) -> Unit, accent: Color, selectedBg: Color, errorText: String?
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("$emoji ", style = MaterialTheme.typography.titleMedium)
            Text(title, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleSmall)
            Text(" *", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                ChoiceChip(option, option in selected, accent, selectedBg) { onToggle(option) }
            }
        }
        if (errorText != null) {
            Text(errorText, color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun ChoiceChip(
    label: String, selected: Boolean, accent: Color, selectedBg: Color, onClick: () -> Unit
) {
    Surface(
        onClick = onClick, shape = CircleShape,
        color = if (selected) selectedBg else ChipIdleBg,
        border = BorderStroke(1.dp, if (selected) accent else ChipIdleBorder)
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(label, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelLarge,
                color = if (selected) accent else Color(0xFF1B1B1B))
            if (selected) Icon(Icons.Filled.Check, null, Modifier.size(14.dp), tint = accent)
        }
    }
}
