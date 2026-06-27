package com.ihebhidouri.marketview.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ihebhidouri.marketview.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.ihebhidouri.marketview.models.SignUpValidator



class AuthViewModel(
    private val authRepository: AuthRepository,
    private val validator: SignUpValidator = SignUpValidator()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        _uiState.value = AuthUiState(
            isLoggedIn = authRepository.isLoggedIn,
            displayName = authRepository.currentUser?.displayName
        )
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.login(email, password)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(isLoggedIn = true, isLoading = false, displayName = authRepository.currentUser?.displayName)
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Login failed"
                )
            }
        }
    }

    fun signUp(username: String, email: String, password: String) {
        val validation = validator.validate(username, email, password)

        if (!validation.isValid) {
            _uiState.value = _uiState.value.copy(
                usernameError = validation.usernameError,
                emailError = validation.emailError,
                passwordError = validation.passwordError
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                usernameError = null,
                emailError = null,
                passwordError = null
            )

            if (authRepository.isUsernameTaken(username)) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    usernameError = "Username is already taken"
                )
                return@launch
            }

            val result = authRepository.signUp(username, email, password)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isLoggedIn = true,
                    isLoading = false,
                    displayName = username
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Sign up failed"
                )
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _uiState.value = AuthUiState(isLoggedIn = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            error = null,
            usernameError = null,
            emailError = null,
            passwordError = null
        )
    }
}