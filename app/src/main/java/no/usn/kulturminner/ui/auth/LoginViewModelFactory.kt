package no.usn.kulturminner.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import no.usn.kulturminner.data.local.TokenStorage

class LoginViewModelFactory(
    private val tokenStorage: TokenStorage
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return LoginViewModel(tokenStorage) as T
    }
}