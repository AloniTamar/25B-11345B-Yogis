package com.tamara.a25b_11345b_yogis.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tamara.a25b_11345b_yogis.data.model.Pose
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

object PoseRepository {
    // 1. Point to your “poses” node
    private val posesRef = FirebaseDatabase
        .getInstance("https://yogis-e26d1-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("poses")

    // 2. Backing LiveData
    private val _poses = MutableLiveData<List<Pose>>(emptyList())
    val poses: LiveData<List<Pose>> = _poses

    init {
        // 3. Fire up the listener once, for app-lifetime
        posesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _poses.postValue(
                    snapshot.children
                        .mapNotNull { it.getValue(Pose::class.java) }
                )
            }
            override fun onCancelled(error: DatabaseError) {
                // you can log or ignore
            }
        })
    }

    /** Legacy API — now returns a LiveData that stays up-to-date */
    fun getAll(): List<Pose> = runBlocking {
        val snapshot = posesRef.get().await()
        snapshot.children
            .mapNotNull { it.getValue(Pose::class.java) }
    }

    /**
     * Save or update a Pose in RTDB.
     * @param pose the Pose to save. If pose.id is null or blank we generate a new key.
     * @param callback invoked with null on success, or with the exception on failure.
     */
    fun savePose(pose: Pose, callback: (Exception?) -> Unit) {
        val key: String = pose.id
            .takeUnless { it.isBlank() }
            ?: posesRef.push().key!!
                .also { generated -> pose.id = generated }

        posesRef
            .child(key)
            .setValue(pose)
            .addOnSuccessListener { callback(null) }
            .addOnFailureListener { ex -> callback(ex) }
    }
}
