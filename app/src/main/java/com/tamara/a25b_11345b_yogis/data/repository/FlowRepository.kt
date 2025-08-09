package com.tamara.a25b_11345b_yogis.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.tamara.a25b_11345b_yogis.data.model.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

object FlowRepository {
    private val flowsRef = FirebaseDatabase
        .getInstance("https://yogis-e26d1-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("flows")

    /**
     * Blocks and returns the full list of flows.
     */
    fun getAll(): List<Flow> = runBlocking(Dispatchers.IO) {
        flowsRef.get().await()
            .children
            .mapNotNull { it.getValue(Flow::class.java) }
    }

    /**
     * New! Fetch a single Flow by its ID (the key under /flows).
     * Returns null if not found or on parse failure.
     */
    fun getById(id: String): Flow? = runBlocking(Dispatchers.IO) {
        flowsRef.child(id)
            .get()
            .await()
            .getValue(Flow::class.java)
    }
}
