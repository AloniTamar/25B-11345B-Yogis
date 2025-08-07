package com.tamara.a25b_11345b_yogis.data.firebase

/**
 * Temporarily holds all the sign-up fields until we submit them as one payload.
 */
object RegistrationManager {
    // Step 1 (general info)
    var email: String = ""
    var password: String = ""
    var username: String = ""

    // Step 2 (professional info)
    var yogatype: String = ""
    var yearsExperience: Int = 0

    fun clear() {
        email = ""
        password = ""
        username = ""
        yogatype = ""
        yearsExperience = 0
    }
}