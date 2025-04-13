package com.example.chatroomhope.data


import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class UserRepository(private val auth: FirebaseAuth,
                     private val firestore: FirebaseFirestore
) {

    suspend fun signUp(email: String, password: String, firstName: String, lastName: String): Result<Boolean> =
        try {
            // ✅ Create user in Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password).await()

            // ✅ Create a Firestore document for the user in "users" collection
            val user = User(firstName, lastName, email)

            FirebaseFirestore.getInstance().collection("users")
                .document(email)  // Use email as document ID
                .set(user)
                .await()  // Ensures operation completes before proceeding

            Log.d("UserRepository", "✅ User added to Firestore: $user")

            Result.Success(true)
        } catch (e: Exception) {
            Log.e("UserRepository", "❌ Error during sign-up", e)
            Result.Error(e)
        }

    suspend fun login(email: String, password: String): Result<Boolean> = try {
        // Attempt to sign in with email and password
        auth.signInWithEmailAndPassword(email, password).await()

        // Check if the user exists in Firestore
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userRef = FirebaseFirestore.getInstance().collection("users").document(user.email!!)

            userRef.get().addOnSuccessListener { document ->
                // If user document does not exist, create it
                if (!document.exists()) {
                    val userData = hashMapOf(
                        "firstName" to user.displayName,
                        "lastName" to "", // You can add more fields here if needed
                        "email" to user.email
                    )
                    userRef.set(userData).addOnSuccessListener {
                        Log.d("Firestore", "New user document created for ${user.email}")
                    }
                } else {
                    Log.d("Firestore", "User document already exists for ${user.email}")
                }
            }.addOnFailureListener { e ->
                Log.e("Firestore", "Failed to check if user document exists", e)
            }
        }

        Result.Success(true)
    } catch (e: Exception) {
        Result.Error(e)
    }


    private suspend fun saveUserToFirestore(user: User) {
        firestore.collection("users").document(user.email).set(user).await()
    }

    suspend fun getCurrentUser(): Result<User> = try {
        Log.d("getCurrentUser", "Attempting to fetch current user...")

        val uid = auth.currentUser?.email
        Log.d("getCurrentUser", "Current user UID: $uid")

        if (uid != null) {
            Log.d("getCurrentUser", "Fetching user document from Firestore...")
            val userDocument = firestore.collection("users").document(uid).get().await()
            Log.d("getCurrentUser", "User document fetched: $userDocument")

            val user = userDocument.toObject(User::class.java)
            Log.d("getCurrentUser", "User object created: $user")

            if (user != null) {
                Log.d("getCurrentUser", "User data found: $user")
                Result.Success(user)
            } else {
                Log.e("getCurrentUser", "User data not found for UID: $uid")
                Result.Error(Exception("User data not found"))
            }
        } else {
            Log.e("getCurrentUser", "User not authenticated")
            Result.Error(Exception("User not authenticated"))
        }
    } catch (e: Exception) {
        Log.e("getCurrentUser", "Exception occurred: ${e.message}", e)
        Result.Error(e)
    }

}