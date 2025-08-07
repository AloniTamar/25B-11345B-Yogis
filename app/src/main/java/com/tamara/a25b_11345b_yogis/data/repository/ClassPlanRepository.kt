package com.tamara.a25b_11345b_yogis.data.repository

import com.google.firebase.database.*
import com.tamara.a25b_11345b_yogis.data.model.ClassPlan

/**
 * Simple wrapper around Firebase Realtime Database for ClassPlan objects.
 */
class ClassPlanRepository {

    private val database = FirebaseDatabase.getInstance()
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
                    // Map the snapshot into your Kotlin data class
                    val plan = snapshot.getValue(ClassPlan::class.java)
                    onPlanLoaded(plan)
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
}
