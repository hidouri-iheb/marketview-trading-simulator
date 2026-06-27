package com.ihebhidouri.marketview.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override val isLoggedIn: Boolean
        get() = auth.currentUser != null

    override suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(username: String, email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!!

            val profileUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build()
            user.updateProfile(profileUpdate).await()

            val db = FirebaseDatabase.getInstance(DB_URL)
            db.reference.child("usernames").child(username.lowercase()).setValue(user.uid).await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isUsernameTaken(username: String): Boolean {
        return try {
            val db = FirebaseDatabase.getInstance(DB_URL)
            val snapshot = db.reference.child("usernames").child(username.lowercase()).get().await()
            snapshot.exists()
        } catch (e: Exception) {
            false
        }
    }

    override fun logout() {
        auth.signOut()
    }
    companion object {
        private const val DB_URL = "https://marketview-251d0-default-rtdb.europe-west1.firebasedatabase.app"
    }
}