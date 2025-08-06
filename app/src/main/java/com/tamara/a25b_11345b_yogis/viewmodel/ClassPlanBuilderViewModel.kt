package com.tamara.a25b_11345b_yogis.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tamara.a25b_11345b_yogis.data.manager.ClassPlanBuilderManager
import com.tamara.a25b_11345b_yogis.data.model.ClassPlan
import com.tamara.a25b_11345b_yogis.data.model.ClassPlanElement
import com.tamara.a25b_11345b_yogis.data.model.Pose
import com.tamara.a25b_11345b_yogis.data.model.Flow

class ClassPlanBuilderViewModel : ViewModel() {

    private val manager = ClassPlanBuilderManager()

    // Expose the evolving list as LiveData
    private val _items = MutableLiveData<List<ClassPlanElement>>(emptyList())
    val items: LiveData<List<ClassPlanElement>> = _items

    // Expose canSave as LiveData if you like, or compute in the fragment
    val canSave: Boolean
        get() = manager.canSave()

    // Initial metadata
    fun setClassInfo(name: String, duration: Int, level: Pose.Level) {
        manager.setClassInfo(name, duration, level)
        // no change to items
    }

    // Add operations
    fun addPose(pose: Pose) {
        manager.addPose(pose)
        _items.value = manager.items
    }
    fun addFlow(flow: Flow) {
        manager.addFlow(flow)
        _items.value = manager.items
    }

    // Build or reset
    fun buildPlan(): ClassPlan = manager.buildPlan()
    fun resetAll() {
        manager.resetAll()
        _items.value = manager.items
    }
}
