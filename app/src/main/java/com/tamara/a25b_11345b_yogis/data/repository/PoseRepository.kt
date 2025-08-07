package com.tamara.a25b_11345b_yogis.data.repository

import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.tamara.a25b_11345b_yogis.data.model.Pose

/**
 * For now this just returns an empty list (or you can hard-code a few
 * Pose(...) instances), later we’ll load from your JSON.
 */
object PoseRepository {

    private val database = FirebaseDatabase
        .getInstance("https://yogis-e26d1-default-rtdb.europe-west1.firebasedatabase.app/")
    private val posesRef = database.getReference("poses")

    fun savePose(
        pose: Pose,
        onComplete: (DatabaseError?) -> Unit
    ) {
        posesRef.child(pose.id)
            .setValue(pose)
            .addOnCompleteListener { task ->
                onComplete(if (task.isSuccessful) null else task.exception
                    ?.let { DatabaseError.fromException(it) })
            }
    }

    fun getAllOnce(
        onPoses: (List<Pose>) -> Unit,
        onError: (DatabaseError) -> Unit
    ) {
        posesRef.addListenerForSingleValueEvent(object :
            com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val list = snapshot.children.mapNotNull {
                    it.getValue(Pose::class.java)
                }
                onPoses(list)
            }

            override fun onCancelled(error: DatabaseError) = onError(error)
        })
    }

    fun getAll(): List<Pose> {
        // TODO: Replace this with Firebase fetch later
        return listOf(
            // EXAMPLE POSE
            Pose(
                id = "adho_mukha",
                name = "Adho Mukha Shvanasana",
                level = Pose.Level.beginner,
                category = Pose.Category.forwardBends,
                duration = 2,
                description = "Start on all fours, tuck your toes, lift hips up and back...",
                notes = "Keep feet hip-width apart.",
                image = "https://.../dogpose.jpg"
            ),
            // ADD ANOTHER
            Pose(
                id = "utkatasana",
                name = "Utkatasana",
                level = Pose.Level.intermediate,
                category = Pose.Category.standingPoses,
                repetitions = 8,
                description = "Stand tall, bend knees as if sitting, raise arms overhead...",
                notes = "Keep weight in heels.",
                image = "https://.../utkatasana.jpg"
            )
        )
    }
}
