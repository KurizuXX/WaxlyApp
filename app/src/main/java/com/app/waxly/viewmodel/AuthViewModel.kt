package com.app.waxly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.waxly.model.entities.User
import com.app.waxly.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: User? = null
)

class AuthViewModel(
    private val repo: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun reset() {
        _uiState.value = AuthUiState()
    }

    fun login(email: String, password: String) {
        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            val res = repo.login(email, password)
            _uiState.value = res.fold(
                onSuccess = { user -> AuthUiState(user = user) },
                onFailure = { e -> AuthUiState(error = e.message ?: "Error al iniciar sesión") }
            )
        }
    }

    fun register(name: String, email: String, password: String) {
        _uiState.value = AuthUiState(isLoading = true)

        viewModelScope.launch {
            val reg = repo.register(name, email, password)
            if (reg.isFailure) {
                _uiState.value = AuthUiState(error = reg.exceptionOrNull()?.message ?: "Error al registrarse")
                return@launch
            }

            // Registro ok -> login automático para obtener el User
            val login = repo.login(email, password)
            _uiState.value = login.fold(
                onSuccess = { user -> AuthUiState(user = user) },
                onFailure = { e -> AuthUiState(error = e.message ?: "Registrado, pero falló el login") }
            )
        }
    }
}