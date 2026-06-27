package com.ihebhidouri.marketview.viewmodels

data class AuthUiState(
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val displayName: String? = null,
    val usernameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null
)