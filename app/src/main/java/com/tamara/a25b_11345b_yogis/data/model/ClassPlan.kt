package com.tamara.a25b_11345b_yogis.data.model
import com.google.firebase.database.IgnoreExtraProperties
/**
 * Represents a full class plan, as shown in the class plan builder and saved to Firebase.
 */
@IgnoreExtraProperties
data class ClassPlan(
    val planId: String = "",
    val planName: String = "",
    val userId: String = "",
    val level: String = "",
    val duration: Int = 0,
    val elements: MutableList<ClassPlanElement> = mutableListOf()
)