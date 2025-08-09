package com.tamara.a25b_11345b_yogis.data.model
import com.google.firebase.database.IgnoreExtraProperties

/** A tiny model just to hold our placeholder flow name. */
@IgnoreExtraProperties
data class Flow(
    var flowId: String = "",
    val flowName: String = "",
    val level: Pose.Level = Pose.Level.beginner,
    val numberOfPoses: Int = 0,
    val recommendedRounds: Int = 0,
    val poses: List<Pose> = emptyList()
)