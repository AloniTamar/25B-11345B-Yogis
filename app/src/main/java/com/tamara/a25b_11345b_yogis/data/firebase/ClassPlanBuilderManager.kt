package com.tamara.a25b_11345b_yogis.data.firebase

import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.tamara.a25b_11345b_yogis.data.model.ClassPlan
import com.tamara.a25b_11345b_yogis.data.model.ClassPlanElement
import com.tamara.a25b_11345b_yogis.data.model.Flow
import com.tamara.a25b_11345b_yogis.data.model.Pose
import com.tamara.a25b_11345b_yogis.data.repository.ClassPlanRepository
import com.tamara.a25b_11345b_yogis.utils.IdUtils
import java.util.UUID

class ClassPlanBuilderManager(
    private val repository: ClassPlanRepository = ClassPlanRepository()
) {
    private var planId: String = ""
    private var className: String = ""
    private var durationMinutes: Int = 0
    private var level: String = ""
    private val _items = mutableListOf<ClassPlanElement>()
    val items: List<ClassPlanElement> get() = _items

    fun setClassInfo(name: String, durationMinutes: Int, level: Pose.Level) {
        this.className = name
        this.durationMinutes = durationMinutes
        this.level = level.name
    }

    fun addPose(pose: Pose) { _items.add(ClassPlanElement.PoseElement(pose)) }
    fun addFlow(flow: Flow) { _items.add(ClassPlanElement.FlowElement(flow)) }

    private fun buildPlan(): ClassPlan {
        require(className.isNotBlank())  { "Class name must be set" }
        require(durationMinutes > 0)     { "Duration must be > 0" }
        require(_items.isNotEmpty())     { "Add at least one element before saving" }

        if (planId.isBlank()) planId = UUID.randomUUID().toString()

        val user = AuthManager.currentUser()
            ?: throw IllegalStateException("No authenticated user; cannot save ClassPlan")

        val email = user.email ?: error("User has no email; cannot key plan by email")
        val emailKey = IdUtils.emailKey(email)

        return ClassPlan(
            planId   = planId,
            planName = className,
            userId   = emailKey,
            level    = level,
            duration = durationMinutes,
            elements = _items.toList() as MutableList<ClassPlanElement>
        )
    }

    fun savePlan(onComplete: (DatabaseError?) -> Unit) {
        if (planId.isNotBlank() && !looksLikeUuid(planId)) {
            val draft = buildPlan()
            repository.savePlan(draft.planId, draft, onComplete)
            return
        }

        if (className.isBlank()) {
            try {
                val draft = buildPlan()
                repository.savePlan(draft.planId, draft, onComplete)
            } catch (t: Throwable) {
                onComplete(DatabaseError.fromException(t))
            }
            return
        }

        val base = IdUtils.slugify(className)
        val db = FirebaseDatabase.getInstance(
            "https://yogis-e26d1-default-rtdb.europe-west1.firebasedatabase.app/"
        )
        val plansRef = db.getReference("classPlans")

        findUniqueId(plansRef, base, 1,
            onFound = { uniqueId ->
                planId = uniqueId
                try {
                    val draft = buildPlan()
                    repository.savePlan(draft.planId, draft, onComplete)
                } catch (t: Throwable) {
                    onComplete(DatabaseError.fromException(t))
                }
            },
            onError = { ex ->
                onComplete(DatabaseError.fromException(ex))
            }
        )
    }

    private fun looksLikeUuid(s: String): Boolean =
        s.count { it == '-' } == 4 && s.length in 32..40


    private fun findUniqueId(
        ref: DatabaseReference,
        base: String,
        attempt: Int,
        onFound: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val candidate = if (attempt == 1) base else "$base-$attempt"
        ref.child(candidate).get()
            .addOnSuccessListener { snap ->
                if (snap.exists()) {
                    findUniqueId(ref, base, attempt + 1, onFound, onError)
                } else {
                    onFound(candidate)
                }
            }
            .addOnFailureListener { ex -> onError(ex) }
    }
}
