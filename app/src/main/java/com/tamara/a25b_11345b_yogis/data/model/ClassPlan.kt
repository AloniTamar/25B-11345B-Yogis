package com.tamara.a25b_11345b_yogis.data.model

/**
 * Represents a full class plan, as shown in the class plan builder and saved to Firebase.
 */
data class ClassPlan(
    val planId: String,
    val planName: String,
    val userId: String,
    val level: String,
    val duration: Int,
    val elements: MutableList<ClassPlanElement>
) {
    val numberOfElements: Int
        get() = elements.size
}
