package com.ihebhidouri.marketview.repository

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?
    val isLoggedIn: Boolean
    suspend fun login(email: String, password: String): Result<FirebaseUser>
    suspend fun signUp(email: String, password: String): Result<FirebaseUser>
    fun logout()
}