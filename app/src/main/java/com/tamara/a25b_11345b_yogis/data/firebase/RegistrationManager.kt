package com.tamara.a25b_11345b_yogis.data.firebase

/**
 * Temporarily holds all the sign-up fields until we submit them as one payload.
 */
object RegistrationManager {
    var email: String = ""
    var password: String = ""
    var username: String = ""

    var yogaType: String = ""
    var yearsExperience: Int = 0

    fun clear() {
        email = ""
        password = ""
        username = ""
        yogaType = ""
        yearsExperience = 0
    }
}