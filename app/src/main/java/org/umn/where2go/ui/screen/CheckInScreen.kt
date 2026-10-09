@file:OptIn(ExperimentalMaterial3Api::class)

package org.umn.where2go.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.umn.where2go.data.DummyData
import org.umn.where2go.data.Vibe
import org.umn.where2go.ui.components.FormField
import org.umn.where2go.viewmodel.CheckInViewModel

@Composable
fun CheckInScreen(
    placeId: Int,
    onBack: () -> Unit,
    onDone: () -> Unit,
    vm: CheckInViewModel = viewModel()
) {
    val place = remember(placeId) { DummyData.places.find { it.id == placeId } }

    // pre-fill dari data dummy
    LaunchedEffect(placeId) { place?.let(vm::prefill) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Check-in") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                place?.name ?: "Tempat tidak ditemukan",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                "Bagikan kondisi tempat ini sekarang dan dapatkan +20 XP.",
                style = MaterialTheme.typography.bodyMedium
            )

            Text("Suasana saat ini *", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Vibe.entries.forEach { v ->
                    FilterChip(
                        selected = vm.vibe == v,
                        onClick = { vm.selectVibe(v) },
                        label = { Text(v.label) }
                    )
                }
            }
            vm.vibeError?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            FormField(
                vm.visitors, vm::onVisitors,
                "Perkiraan jumlah pengunjung", vm.visitorsError,
                keyboardType = KeyboardType.Number,
                helper = "Angka 1 - 500"
            )
            FormField(
                vm.wifi, vm::onWifi,
                "Kecepatan Wi-Fi (Mbps)", vm.wifiError,
                keyboardType = KeyboardType.Number,
                helper = "Terisi dari data tempat, ubah jika berbeda"
            )
            FormField(
                vm.review, vm::onReview,
                "Ulasan singkat", vm.reviewError,
                singleLine = false,
                minLines = 3,
                imeAction = ImeAction.Done,
                helper = "${vm.review.trim().length}/10 karakter minimum"
            )

            Button(
                onClick = vm::submit,
                enabled = !vm.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (vm.isSubmitting) {
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Kirim Check-in")
                }
            }
        }
    }

    if (vm.showSuccess) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Check-in berhasil!") },
            text = { Text("Terima kasih sudah berkontribusi. Kamu mendapat +20 XP.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.dismissSuccess()
                    onDone()
                }) { Text("OK") }
            }
        )
    }
}