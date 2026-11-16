package org.umn.where2go.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.umn.where2go.data.Validators
import org.umn.where2go.data.Where2GoBackend

class AuthViewModel : ViewModel() {
    var isRegister by mutableStateOf(false); private set
    var name by mutableStateOf(""); private set
    var email by mutableStateOf(""); private set
    var password by mutableStateOf(""); private set
    var showPassword by mutableStateOf(false); private set
    var submitted by mutableStateOf(false); private set
    var isLoading by mutableStateOf(false); private set
    var errorMessage by mutableStateOf<String?>(null); private set

    private val nameErr get() = if (isRegister) Validators.required(name, "Nama") else null
    private val emailErr get() = Validators.email(email)
    private val passErr get() = Validators.password(password)
    val nameError get() = if (submitted || name.isNotEmpty()) nameErr else null
    val emailError get() = if (submitted || email.isNotEmpty()) emailErr else null
    val passwordError get() = if (submitted || password.isNotEmpty()) passErr else null
    fun onName(v: String) { name = v; errorMessage = null }
    fun onEmail(v: String) { email = v; errorMessage = null }
    fun onPassword(v: String) { password = v; errorMessage = null }
    fun togglePassword() { showPassword = !showPassword }
    fun toggleMode() { isRegister = !isRegister; submitted = false; errorMessage = null }

    fun submit(context: Context, onSuccess: () -> Unit) {
        submitted = true
        if (nameErr != null || emailErr != null || passErr != null) return
        viewModelScope.launch {
            isLoading = true; errorMessage = null
            try {
                val message = withContext(Dispatchers.IO) {
                    if (isRegister) {
                        val session = Where2GoBackend.signUp(context, name, email, password)
                        if (session == null) "Akun dibuat. Cek email untuk konfirmasi, lalu masuk." else null
                    } else {
                        Where2GoBackend.signIn(context, email, password)
                        null
                    }
                }
                if (message == null) onSuccess() else errorMessage = message
            } catch (e: Exception) {
                errorMessage = e.message ?: "Tidak dapat terhubung ke Supabase."
            } finally { isLoading = false }
        }
    }
}
