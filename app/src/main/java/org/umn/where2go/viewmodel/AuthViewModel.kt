package org.umn.where2go.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.umn.where2go.data.Validators
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    var isRegister by mutableStateOf(false); private set
    var name by mutableStateOf(""); private set
    var email by mutableStateOf(""); private set
    var password by mutableStateOf(""); private set
    var showPassword by mutableStateOf(false); private set
    var submitted by mutableStateOf(false); private set
    var isLoading by mutableStateOf(false); private set

    private val nameErr: String? get() = if (isRegister) Validators.required(name, "Nama") else null
    private val emailErr: String? get() = Validators.email(email)
    private val passErr: String? get() = Validators.password(password)

    // error baru tampil setelah user mengetik atau menekan tombol submit
    val nameError: String? get() = if (submitted || name.isNotEmpty()) nameErr else null
    val emailError: String? get() = if (submitted || email.isNotEmpty()) emailErr else null
    val passwordError: String? get() = if (submitted || password.isNotEmpty()) passErr else null

    fun onName(v: String) { name = v }
    fun onEmail(v: String) { email = v }
    fun onPassword(v: String) { password = v }
    fun togglePassword() { showPassword = !showPassword }
    fun toggleMode() { isRegister = !isRegister; submitted = false }

    fun submit(onSuccess: () -> Unit) {
        submitted = true
        if (nameErr != null || emailErr != null || passErr != null) return   // diblokir jika belum valid
        viewModelScope.launch {
            isLoading = true
            delay(1000)
            isLoading = false
            onSuccess()
        }
    }
}