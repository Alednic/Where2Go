package org.umn.where2go.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.umn.where2go.viewmodel.AuthViewModel
import org.umn.where2go.ui.components.FormField

@Composable
fun AuthScreen(onAuthenticated: () -> Unit, vm: AuthViewModel = viewModel())
{
    Scaffold { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize()
                .verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            Text("Where2Go", style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary)
            Text(if (vm.isRegister) "Buat akun baru" else "Masuk ke akunmu", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(16.dp))
            if (vm.isRegister) FormField(vm.name, vm::onName, "Nama lengkap", vm.nameError)
            FormField(vm.email, vm::onEmail, label = "Email", error = vm.emailError, keyboardType = KeyboardType.Email)
            FormField(
                value = vm.password, onValueChange = vm::onPassword, label = "Password",
                error = vm.passwordError, keyboardType = KeyboardType.Password, imeAction = ImeAction.Done,
                visualTransformation = if (vm.showPassword) VisualTransformation.None
                    else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = vm::togglePassword) {
                        Icon(if (vm.showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            "Tampilkan password")
                    }
                }
            )
            Button(onClick = { vm.submit(onAuthenticated) }, enabled = !vm.isLoading,
                modifier = Modifier.fillMaxWidth()) {
                if (vm.isLoading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text(if (vm.isRegister) "Daftar" else "Masuk")
            }
            TextButton(onClick = vm::toggleMode, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(if (vm.isRegister) "Sudah punya akun? Masuk" else "Belum punya akun? Daftar")
            }
        }
    }
}