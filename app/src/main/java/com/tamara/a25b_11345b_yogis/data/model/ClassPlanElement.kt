package com.tamara.a25b_11345b_yogis.data.model

/**
 * Represents one item in a class plan: either a Pose or a Flow.
 * Use this as the element type for the class plan’s elements list.
 */
sealed class ClassPlanElement {
    data class PoseElement(val pose: Pose) : ClassPlanElement()
    data class FlowElement(val flow: Flow) : ClassPlanElement()
}