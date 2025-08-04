package com.tamara.a25b_11345b_yogis.data.repository

import com.tamara.a25b_11345b_yogis.data.model.Flow
import com.tamara.a25b_11345b_yogis.data.model.Pose

object FlowRepository {

    // Use PoseRepository to get real/dummy poses
    private val samplePoses = PoseRepository.getAll().take(4) // or any number

    private val flows = listOf(
        Flow(
            flowId = "surya_a",
            flowName = "Surya Namaskar A",
            level = Pose.Level.beginner,
            numberOfPoses = samplePoses.size,
            recommendedRounds = 3,
            poses = samplePoses
        ),
        Flow(
            flowId = "surya_b",
            flowName = "Surya Namaskar B",
            level = Pose.Level.beginner,
            numberOfPoses = samplePoses.size,
            recommendedRounds = 2,
            poses = samplePoses
        ),
        Flow(
            flowId = "standing_seq",
            flowName = "Standing Sequence",
            level = Pose.Level.intermediate,
            numberOfPoses = samplePoses.size,
            recommendedRounds = 1,
            poses = samplePoses
        ),
        Flow(
            flowId = "primary_series",
            flowName = "Primary Series",
            level = Pose.Level.advanced,
            numberOfPoses = samplePoses.size,
            recommendedRounds = 1,
            poses = samplePoses
        )
    )

    fun getAll(): List<Flow> = flows
    fun getById(id: String): Flow? = flows.find { it.flowId == id }
}
