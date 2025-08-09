package com.tamara.a25b_11345b_yogis.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /** Returns the currently signed-in user, or null */
    fun currentUser(): FirebaseUser? = auth.currentUser

    /** Sign in existing user */
    fun signIn(
        email: String,
        password: String,
        onComplete: (success: Boolean, exception: Exception?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful, task.exception)
            }
    }

    /** Create new account */
    fun signUp(
        email: String,
        password: String,
        onComplete: (success: Boolean, exception: Exception?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful, task.exception)
            }
    }

    /** Send password-reset email */
    fun sendPasswordResetEmail(
        email: String,
        onComplete: (success: Boolean, exception: Exception?) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful, task.exception)
            }
    }

    /** Update the signed-in user’s password */
    fun updatePassword(
        newPassword: String,
        onComplete: (success: Boolean, exception: Exception?) -> Unit
    ) {
        auth.currentUser
            ?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->
                onComplete(task.isSuccessful, task.exception)
            }
            ?: onComplete(false, Exception("No user signed in"))
    }

    /** Sign out */
    fun signOut() {
        auth.signOut()
    }

    fun updatePassword(currentPassword: String, newPassword: String, callback: (Boolean, String?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email ?: return callback(false, "User not logged in")

        val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, currentPassword)
        user.reauthenticate(credential)
            .addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                callback(true, null)
                            } else {
                                callback(false, updateTask.exception?.localizedMessage)
                            }
                        }
                } else {
                    callback(false, "Current password is incorrect")
                }
            }
    }

}
