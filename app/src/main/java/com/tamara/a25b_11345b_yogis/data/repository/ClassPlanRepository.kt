package com.tamara.a25b_11345b_yogis.data.repository

import com.google.firebase.database.*
import com.tamara.a25b_11345b_yogis.data.model.*
import com.tamara.a25b_11345b_yogis.utils.IdUtils
import kotlinx.coroutines.tasks.await

/**
 * Simple wrapper around Firebase Realtime Database for ClassPlan objects.
 */
class ClassPlanRepository {

    private val database = FirebaseDatabase.getInstance(
        "https://yogis-e26d1-default-rtdb.europe-west1.firebasedatabase.app/"
    )
    private val plansRef = database.getReference("classPlans")

    /**
     * Listen for live updates to the ClassPlan with the given ID.
     *
     * @param planId       the node key under "classPlans"
     * @param onPlanLoaded callback with the latest ClassPlan (or null if missing)
     * @param onError      callback on failure
     */
    fun observePlan(
        planId: String,
        onPlanLoaded: (ClassPlan?) -> Unit,
        onError: (DatabaseError) -> Unit
    ) {
        plansRef.child(planId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        onPlanLoaded(null)
                        return
                    }
                    try {
                        val planName = snapshot.child("planName").getValue(String::class.java) ?: ""
                        val userId   = snapshot.child("userId").getValue(String::class.java) ?: ""
                        val level    = snapshot.child("level").getValue(String::class.java)   ?: ""
                        val duration = snapshot.child("duration").getValue(Int::class.java)   ?: 0

                        val elements = mutableListOf<ClassPlanElement>()
                        for (elemNode in snapshot.child("elements").children) {
                            val poseSnap = elemNode.child("pose")
                            if (poseSnap.exists()) {
                                poseSnap.getValue(Pose::class.java)
                                    ?.let { elements.add(ClassPlanElement.PoseElement(it)) }
                            } else {
                                val flowSnap = elemNode.child("flow")
                                if (flowSnap.exists()) {
                                    flowSnap.getValue(Flow::class.java)
                                        ?.let { elements.add(ClassPlanElement.FlowElement(it)) }
                                }
                            }
                        }

                        val plan = ClassPlan(
                            planId   = planId,
                            planName = planName,
                            userId   = userId,
                            level    = level,
                            duration = duration,
                            elements = elements
                        )
                        onPlanLoaded(plan)
                    } catch (e: Exception) {
                        onError(DatabaseError.fromException(e))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error)
                }
            })
    }

    /**
     * Save (create or overwrite) a ClassPlan under the given ID.
     */
    fun savePlan(
        planId: String,
        plan: ClassPlan,
        onComplete: (DatabaseError?) -> Unit
    ) {
        plansRef.child(planId)
            .setValue(plan)
            .addOnCompleteListener { task ->
                onComplete(if (task.isSuccessful) null else task.exception?.let {
                    DatabaseError.fromException(it)
                })
            }
    }

    suspend fun savePlanByName(plan: ClassPlan): String {
        val plansRef = database.getReference("classPlans")
        val base = IdUtils.slugify(plan.planName)
        val id   = IdUtils.nextAvailableId(plansRef, base)
        val final = plan.copy(planId = id)
        plansRef.child(id).setValue(final).await()
        return id
    }

    fun listForUser(
        uid: String,
        onLoaded: (List<ClassPlan>) -> Unit,
        onError: (DatabaseError) -> Unit
    ) {
        database
            .getReference("classPlans")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(sn: DataSnapshot) {
                    val list = sn.children.mapNotNull { snapshot ->

                        try {
                            val planId   = snapshot.key ?: return@mapNotNull null
                            val planName = snapshot.child("planName").getValue(String::class.java) ?: ""
                            val userId   = snapshot.child("userId").getValue(String::class.java) ?: ""
                            val level    = snapshot.child("level").getValue(String::class.java) ?: ""
                            val duration = snapshot.child("duration").getValue(Int::class.java) ?: 0

                            if (userId != uid) return@mapNotNull null

                            val elements = mutableListOf<ClassPlanElement>()
                            for (elemNode in snapshot.child("elements").children) {
                                val poseSnap = elemNode.child("pose")
                                if (poseSnap.exists()) {
                                    poseSnap.getValue(Pose::class.java)
                                        ?.let { elements.add(ClassPlanElement.PoseElement(it)) }
                                } else {
                                    val flowSnap = elemNode.child("flow")
                                    if (flowSnap.exists()) {
                                        flowSnap.getValue(Flow::class.java)
                                            ?.let { elements.add(ClassPlanElement.FlowElement(it)) }
                                    }
                                }
                            }

                            ClassPlan(
                                planId = planId,
                                planName = planName,
                                userId = userId,
                                level = level,
                                duration = duration,
                                elements = elements
                            )
                        } catch (_: Exception) { null }
                    }
                    onLoaded(list)
                }
                override fun onCancelled(err: DatabaseError) = onError(err)
            })
    }

}
