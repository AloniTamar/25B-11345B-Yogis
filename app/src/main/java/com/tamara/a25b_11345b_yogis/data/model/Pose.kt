@file:Suppress("unused")

package com.tamara.a25b_11345b_yogis.data.model
import com.google.firebase.database.IgnoreExtraProperties

/**
 * Mirrors poseMetadata.json:
 * – id, name, description, image are required
 * – either duration or repetitions must be non-null
 */
@IgnoreExtraProperties
data class Pose(
    var id: String = "",
    val name: String = "",
    val level: Level = Level.beginner,
    val category: Category = Category.standingPoses,
    val duration: Int? = null,
    val description: String = "",
    val notes: String? = null,
    val image: String? = ""
) {
    enum class Level { beginner, intermediate, advanced }
    enum class Category {
        standingPoses,
        forwardBends,
        backendArches,
        twists,
        inversions,
        seatedPoses,
        boatPoses,
        balancingPoses
    }
}
