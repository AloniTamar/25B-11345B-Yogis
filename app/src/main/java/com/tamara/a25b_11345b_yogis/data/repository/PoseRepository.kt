package com.tamara.a25b_11345b_yogis.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.tamara.a25b_11345b_yogis.data.model.Pose
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import android.util.Log

object PoseRepository {
    private const val TAG = "PoseRepository"

    private val posesRef = FirebaseDatabase
        .getInstance("https://yogis-e26d1-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("poses")

    private val _poses = MutableLiveData<List<Pose>>(emptyList())
    val poses: LiveData<List<Pose>> = _poses

    // Raw DTO to avoid enum-mapping crashes
    private data class PoseRaw(
        var id: String? = null,
        var name: String? = null,
        var level: String? = null,
        var category: String? = null,
        var duration: Int? = null,
        var repetitions: Int? = null,
        var description: String? = null,
        var notes: String? = null,
        var image: String? = null
    )

    private fun parseCategory(s: String?): Pose.Category? {
        if (s.isNullOrBlank()) return null
        val key = s.lowercase()
            .replace(" ", "")
            .replace("_", "")
            .replace("-", "")
        return when (key) {
            "standingposes"  -> Pose.Category.standingPoses
            "forwardbends"   -> Pose.Category.forwardBends
            "backendarches"  -> Pose.Category.backendArches
            "twists"         -> Pose.Category.twists
            "inversions"     -> Pose.Category.inversions
            "seatedposes"    -> Pose.Category.seatedPoses
            "boatposes"      -> Pose.Category.boatPoses
            "balancingposes" -> Pose.Category.balancingPoses
            else -> null
        }
    }

    private fun parseLevel(s: String?): Pose.Level? {
        if (s.isNullOrBlank()) return null
        // Your enum names are already lowercase (you used valueOf(lowercase()) before)
        return try { Pose.Level.valueOf(s) } catch (_: IllegalArgumentException) { null }
    }

    private fun PoseRaw.toPoseOrNull(snapshotKey: String?): Pose? {
        val levelEnum = parseLevel(level) ?: return null
        val categoryEnum = parseCategory(category)
        if (categoryEnum == null) {
            Log.w(TAG, "Unknown category '$category' for pose id=$snapshotKey; defaulting to standingPoses")
        }
        return Pose(
            id = id ?: snapshotKey.orEmpty(),
            name = name.orEmpty(),
            level = levelEnum,
            category = categoryEnum ?: Pose.Category.standingPoses,
            duration = duration,
            repetitions = repetitions,
            description = description.orEmpty(),
            notes = notes,
            image = image
        )
    }

    init {
        posesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { snap ->
                    val raw = snap.getValue(PoseRaw::class.java)
                    raw?.toPoseOrNull(snap.key)
                }
                _poses.postValue(list)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "poses listener cancelled: ${error.message}")
            }
        })
    }

    /** Legacy API — now resilient */
    fun getAll(): List<Pose> = runBlocking {
        val snapshot = posesRef.get().await()
        snapshot.children.mapNotNull { snap ->
            val raw = snap.getValue(PoseRaw::class.java)
            raw?.toPoseOrNull(snap.key)
        }
    }

    fun savePose(pose: Pose, callback: (Exception?) -> Unit) {
        val key: String = pose.id.takeUnless { it.isBlank() }
            ?: posesRef.push().key!!.also { generated -> pose.id = generated }

        posesRef.child(key).setValue(pose)
            .addOnSuccessListener { callback(null) }
            .addOnFailureListener { ex -> callback(ex) }
    }
}
