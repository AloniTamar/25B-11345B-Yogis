package com.tamara.a25b_11345b_yogis.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.tamara.a25b_11345b_yogis.data.model.UserProfile
import com.tamara.a25b_11345b_yogis.utils.IdUtils
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val database = FirebaseDatabase
        .getInstance("https://yogis-e26d1-default-rtdb.europe-west1.firebasedatabase.app/")
    private val usersRef = database.getReference("users")

    suspend fun createOrUpdateUser(
        uid: String,           // kept for payload, not used as key
        email: String,
        data: Map<String, Any?>
    ) {
        val db = FirebaseDatabase.getInstance(
            "https://yogis-e26d1-default-rtdb.europe-west1.firebasedatabase.app/"
        )
        val emailKey = IdUtils.emailKey(email) // e.g. "name,domain,com"

        val profile = HashMap<String, Any?>().apply {
            put("uid", uid)          // still stored inside object for reference
            put("email", email)
            putAll(data)             // username, yogaType, yearsExperience, timestamps, etc.
        }

        db.getReference("users")
            .child(emailKey)
            .setValue(profile)
            .await()
    }

    /**
     * Read a profile by email (keys are normalized email strings).
     */
    fun getUserByEmail(
        email: String,
        onLoaded: (UserProfile?) -> Unit,
        onError: (DatabaseError) -> Unit
    ) {
        val emailKey = IdUtils.emailKey(email)
        usersRef.child(emailKey)
            .addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    onLoaded(snapshot.getValue(UserProfile::class.java))
                }
                override fun onCancelled(error: DatabaseError) = onError(error)
            })
    }

    /**
     * Create or update a profile under /users/{emailKey}.
     * Uses profile.email to build the key.
     */
    fun saveUserByEmail(
        profile: UserProfile,
        onComplete: (DatabaseError?) -> Unit
    ) {
        require(profile.email.isNotBlank()) { "UserProfile.email must not be blank" }

        val now = System.currentTimeMillis()
        val payload = profile.copy(
            createdAt = if (profile.createdAt == 0L) now else profile.createdAt,
            updatedAt = now
        )

        val emailKey = IdUtils.emailKey(profile.email)
        usersRef.child(emailKey)
            .setValue(payload)
            .addOnCompleteListener { task ->
                onComplete(
                    if (task.isSuccessful) null
                    else task.exception?.let { DatabaseError.fromException(it) }
                )
            }
    }
}
