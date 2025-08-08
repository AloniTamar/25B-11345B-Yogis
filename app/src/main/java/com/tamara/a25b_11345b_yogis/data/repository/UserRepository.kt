package com.tamara.a25b_11345b_yogis.data.repository

import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.tamara.a25b_11345b_yogis.data.model.UserProfile

class UserRepository {
    private val database = FirebaseDatabase
        .getInstance("https://yogis-e26d1-default-rtdb.europe-west1.firebasedatabase.app/")
    private val usersRef = database.getReference("users")

    /**
     * Save or overwrite a UserProfile under /users/{uid}.
     */
    fun saveUser(
        profile: UserProfile,
        onComplete: (DatabaseError?) -> Unit
    ) {
        usersRef.child(profile.uid)
            .setValue(profile)
            .addOnCompleteListener { task ->
                onComplete(if (task.isSuccessful) null else task.exception?.let {
                    DatabaseError.fromException(it)
                })
            }
    }

    /**
     * (Optional) Load a single user profile once.
     */
    fun getUser(
        uid: String,
        onLoaded: (UserProfile?) -> Unit,
        onError: (DatabaseError) -> Unit
    ) {
        usersRef.child(uid)
            .addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                    onLoaded(snapshot.getValue(UserProfile::class.java))
                }
                override fun onCancelled(error: DatabaseError) = onError(error)
            })
    }
}
