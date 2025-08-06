package com.tamara.a25b_11345b_yogis.data.manager

import com.tamara.a25b_11345b_yogis.data.model.ClassPlan
import com.tamara.a25b_11345b_yogis.data.model.ClassPlanElement
import com.tamara.a25b_11345b_yogis.data.model.Pose
import com.tamara.a25b_11345b_yogis.data.model.Flow
import java.util.UUID

/**
 * In-memory builder for a ClassPlan.
 * 1. Collects initial class info (name, duration, level)
 * 2. Accepts only additions of Pose or Flow
 * 3. Knows when it's valid to save (at least one item)
 * 4. Builds a ClassPlan for upload
 */
class ClassPlanBuilderManager {

    // Step 1: initial class metadata
    private var className: String = ""
    private var durationMinutes: Int = 0
    private var level: String = ""

    // Step 2: the growing list of poses/flows (mixed)
    private val _items = mutableListOf<ClassPlanElement>()
    val items: List<ClassPlanElement>
        get() = _items

    /** — Step 1: set the basic info once user fills name, duration & level — */
    fun setClassInfo(name: String, durationMinutes: Int, level: Pose.Level) {
        this.className = name
        this.durationMinutes = durationMinutes
        this.level = level.toString()
    }

    /** — Step 2.1: add a new Pose to the plan — */
    fun addPose(pose: Pose) {
        _items.add(ClassPlanElement.PoseElement(pose))
    }

    /** — Step 2.2: add a new Flow to the plan — */
    fun addFlow(flow: Flow) {
        _items.add(ClassPlanElement.FlowElement(flow))
    }

    /** — Step 3: can only save when there’s at least one element — */
    fun canSave(): Boolean = _items.isNotEmpty()

    /** — Finalize: build an immutable ClassPlan for Firebase upload — */
    fun buildPlan(): ClassPlan {
        require(className.isNotBlank()) { "Class name must be set" }
        require(durationMinutes > 0) { "Duration must be > 0" }
        val chosenLevel = level
        require(canSave()) { "Add at least one element before saving" }

        return ClassPlan(
            planId = UUID.randomUUID().toString(),
            planName = className,
            level = chosenLevel,
            duration = durationMinutes,
            elements = _items.toMutableList()
        )
    }

    /** — If you ever need to start over completely (e.g. user discarded all changes) — */
    fun resetAll() {
        className = ""
        durationMinutes = 0
        level = ""
        _items.clear()
    }
}
