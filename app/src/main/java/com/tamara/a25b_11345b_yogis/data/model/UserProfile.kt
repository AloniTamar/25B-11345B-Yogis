package com.tamara.a25b_11345b_yogis.data.model
import com.google.firebase.database.IgnoreExtraProperties

/**
 * Mirror of your user schema in RealtimeDB under /users/{uid}.
 */
@IgnoreExtraProperties
data class UserProfile(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val yogaType: String = "",
    val yearsExperience: Int = 0,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)