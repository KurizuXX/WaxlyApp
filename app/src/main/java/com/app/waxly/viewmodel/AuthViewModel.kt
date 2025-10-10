package com.app.waxly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.waxly.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class AuthViewModel(private val repo: UserRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun register(name: String, email: String, password: String) {
        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            val r = repo.register(name, email, password)
            _uiState.value = r.fold(
                onSuccess = { AuthUiState(success = true) },
                onFailure = { AuthUiState(error = it.message) }
            )
        }
    }

    fun login(email: String, password: String) {
        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            val r = repo.login(email, password)
            _uiState.value = r.fold(
                onSuccess = { AuthUiState(success = true) },
                onFailure = { AuthUiState(error = it.message) }
            )
        }
    }
}
