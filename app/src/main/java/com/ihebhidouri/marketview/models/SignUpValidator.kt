package com.ihebhidouri.marketview.models

class SignUpValidator {

    data class ValidationResult(
        val usernameError: String? = null,
        val emailError: String? = null,
        val passwordError: String? = null
    ) {
        val isValid: Boolean
            get() = usernameError == null && emailError == null && passwordError == null
    }

    fun validate(username: String, email: String, password: String): ValidationResult {
        return ValidationResult(
            usernameError = validateUsername(username),
            emailError = validateEmail(email),
            passwordError = validatePassword(password)
        )
    }

    fun validateUsername(username: String): String? {
        return when {
            username.isBlank() -> "Username is required"
            username.length < 5 -> "Username must be at least 5 characters"
            !username.matches(Regex("^[a-zA-Z0-9_]+$")) -> "Only letters, numbers, and underscores"
            else -> null
        }
    }

    fun validateEmail(email: String): String? {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return when {
            email.isBlank() -> "Email is required"
            !emailRegex.matches(email) -> "Invalid email format"
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.length < 8 -> "Password must be at least 8 characters"
            !password.any { it.isUpperCase() } -> "Must contain at least one uppercase letter"
            !password.any { it.isLowerCase() } -> "Must contain at least one lowercase letter"
            !password.any { it.isDigit() } -> "Must contain at least one digit"
            !password.any { !it.isLetterOrDigit() } -> "Must contain at least one special character"
            else -> null
        }
    }
}