package com.tamara.a25b_11345b_yogis.data.model

/**
 * Mirror of your user schema in RealtimeDB under /users/{uid}.
 */
data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val username: String = "",
    val yogaType: String = "",
    val yearsExperience: Int = 0
)
