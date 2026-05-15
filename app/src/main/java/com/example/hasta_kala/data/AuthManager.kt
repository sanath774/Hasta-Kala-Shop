package com.example.hasta_kala.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthManager {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "AuthManager"

    // 1. User Registration
    suspend fun registerUser(email: String, pass: String, profile: UserProfile): Result<Boolean> {
        return try {
            Log.d(TAG, "Starting registration for: $email")
            // Step 1: Create user in Auth
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val userId = result.user?.uid ?: return Result.failure(Exception("Failed to get user ID from Auth"))
            Log.d(TAG, "Auth successful. UID: $userId")

            // Step 2: Save profile in Firestore
            try {
                db.collection("users").document(userId).set(profile.copy(userId = userId)).await()
                Log.d(TAG, "Firestore profile saved successfully")
            } catch (firestoreError: Exception) {
                Log.e(TAG, "Firestore failed: ${firestoreError.message}", firestoreError)
                return Result.failure(Exception("Auth worked, but could not save profile. Check Firestore Rules. Error: ${firestoreError.localizedMessage}"))
            }

            Result.success(true)
        } catch (authError: Exception) {
            Log.e(TAG, "Auth failed: ${authError.message}", authError)
            Result.failure(authError)
        }
    }

    // Check if logged in
    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    // 2. Login
    suspend fun loginUser(email: String, pass: String): Result<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Login failed", e)
            Result.failure(e)
        }
    }

    // 3. Logout
    fun logout() {
        auth.signOut()
    }

    // Get Current User Profile
    suspend fun getUserProfile(): UserProfile? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            db.collection("users").document(uid).get().await().toObject(UserProfile::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
