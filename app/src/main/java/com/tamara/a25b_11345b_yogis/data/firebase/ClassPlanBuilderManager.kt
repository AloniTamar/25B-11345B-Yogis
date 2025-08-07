package com.tamara.a25b_11345b_yogis.data.firebase

import com.google.firebase.database.DatabaseError
import com.tamara.a25b_11345b_yogis.data.model.ClassPlan
import com.tamara.a25b_11345b_yogis.data.model.ClassPlanElement
import com.tamara.a25b_11345b_yogis.data.model.Flow
import com.tamara.a25b_11345b_yogis.data.model.Pose
import com.tamara.a25b_11345b_yogis.data.model.TeachingSession
import com.tamara.a25b_11345b_yogis.data.repository.ClassPlanRepository
import java.util.UUID

/**
 * In-memory builder for a ClassPlan, with Firebase hooks.
 */
class ClassPlanBuilderManager(
    private val repository: ClassPlanRepository = ClassPlanRepository()
) {

    // ———————————————————————————
    // 1) Draft metadata
    // ———————————————————————————
    private var planId: String = ""
    private var className: String = ""
    private var durationMinutes: Int = 0
    private var level: String = ""

    var latestSession: TeachingSession? = null
        private set

    // ———————————————————————————
    // 2) Draft elements
    // ———————————————————————————
    private val _items = mutableListOf<ClassPlanElement>()
    val items: List<ClassPlanElement> get() = _items

    // ———————————————————————————
    // Step 1: set the basic info
    // ———————————————————————————
    fun setClassInfo(name: String, durationMinutes: Int, level: Pose.Level) {
        this.className = name
        this.durationMinutes = durationMinutes
        this.level = level.name
    }

    // ———————————————————————————
    // Step 2: add/remove/reorder elements
    // ———————————————————————————
    fun addPose(pose: Pose) {
        _items.add(ClassPlanElement.PoseElement(pose))
    }

    fun addFlow(flow: Flow) {
        _items.add(ClassPlanElement.FlowElement(flow))
    }

    // ———————————————————————————
    // Finalize in-memory plan
    // ———————————————————————————
    fun buildPlan(): ClassPlan {
        require(className.isNotBlank())        { "Class name must be set" }
        require(durationMinutes > 0)           { "Duration must be > 0" }
        require(_items.isNotEmpty())           { "Add at least one element before saving" }

        // generate or reuse planId
        if (planId.isBlank()) {
            planId = UUID.randomUUID().toString()
        }

        val user = AuthManager.currentUser()
            ?: throw IllegalStateException("No authenticated user; cannot save ClassPlan")

        return ClassPlan(
            planId = planId,
            planName = className,
            userId = user.uid,
            level = level,
            duration = durationMinutes,
            elements = _items.toList() as MutableList<ClassPlanElement>
        )
    }
    // ———————————————————————————
    // Firebase integration
    // ———————————————————————————

    /**
     * Save the current draft plan to Firebase.
     * Builds the plan (assigning planId if needed).
     */
    fun savePlan(onComplete: (DatabaseError?) -> Unit) {
        val draft = buildPlan()
        repository.savePlan(draft.planId, draft) { err ->
            onComplete(err)
        }
    }
}