@file:Suppress("unused")

package com.tamara.a25b_11345b_yogis.data.model

/**
 * Mirrors poseMetadata.json:
 * – id, name, description, image are required
 * – either duration or repetitions must be non-null
 */
data class Pose(
    val id: String,
    val name: String,
    val level: Level,
    val category: Category,
    val duration: Int? = null,
    val repetitions: Int? = null,
    val description: String,
    val notes: String? = null,
    val image: String
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
