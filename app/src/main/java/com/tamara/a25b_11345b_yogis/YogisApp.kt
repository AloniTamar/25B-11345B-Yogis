package com.tamara.a25b_11345b_yogis

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class YogisApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Enable disk persistence for Realtime Database
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}
