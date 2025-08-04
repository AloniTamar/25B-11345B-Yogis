package com.tamara.a25b_11345b_yogis.data.model

/** A tiny model just to hold our placeholder flow name. */
data class Flow(
    val flowId: String,
    val flowName: String,
    val level: Pose.Level,
    val numberOfPoses: Int,
    val recommendedRounds: Int,
    val poses: List<Pose>
)