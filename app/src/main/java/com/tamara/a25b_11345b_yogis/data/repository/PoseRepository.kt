package com.tamara.a25b_11345b_yogis.data.repository

import com.tamara.a25b_11345b_yogis.data.model.Pose

/**
 * For now this just returns an empty list (or you can hard-code a few
 * Pose(...) instances), later we’ll load from your JSON.
 */
object PoseRepository {

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
