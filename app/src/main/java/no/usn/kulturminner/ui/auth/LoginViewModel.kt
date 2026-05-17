package no.usn.kulturminner.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.usn.kulturminner.data.api.LoginApi
import no.usn.kulturminner.data.api.LoginRequest
import no.usn.kulturminner.data.local.TokenStorage

class LoginViewModel(private val tokenStorage: TokenStorage) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun updateEmail(email: String) = _uiState.update { it.copy(email = email, error = null) }
    fun updatePassword(password: String) = _uiState.update { it.copy(password = password, error = null) }

    fun login() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = "Fyll ut e-post og passord") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                LoginApi.service.login(LoginRequest(state.email, state.password))
            }.onSuccess { response ->
                tokenStorage.saveToken(response.token)
                tokenStorage.saveUserId(response.userId)
                _uiState.update { it.copy(isLoading = false, isLoginSuccess = true) }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false, error = "Feil e-post eller passord") }
            }
        }
    }
}