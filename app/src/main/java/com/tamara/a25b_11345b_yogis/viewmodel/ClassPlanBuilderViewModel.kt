package com.tamara.a25b_11345b_yogis.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseError
import com.tamara.a25b_11345b_yogis.data.firebase.ClassPlanBuilderManager
import com.tamara.a25b_11345b_yogis.data.model.ClassPlanElement
import com.tamara.a25b_11345b_yogis.data.model.Flow
import com.tamara.a25b_11345b_yogis.data.model.Pose

class ClassPlanBuilderViewModel(
    private val manager: ClassPlanBuilderManager = ClassPlanBuilderManager()
) : ViewModel() {

    // 1) the current in-memory list of elements (poses + flows)
    private val _items = MutableLiveData<List<ClassPlanElement>>(manager.items)
    val items: LiveData<List<ClassPlanElement>> = _items

    // 2) loading / saving state
    private val _saving = MutableLiveData(false)

    // 3) any error message from save
    private val _saveError = MutableLiveData<String?>(null)

    /** Step 1: capture the class metadata from the UI */
    fun setClassInfo(name: String, durationMinutes: Int, level: Pose.Level) {
        manager.setClassInfo(name, durationMinutes, level)
    }

    /** Step 2a: add a pose (and update the LiveData) */
    fun addPose(pose: Pose) {
        manager.addPose(pose)
        _items.value = manager.items
    }

    /** Step 2b: add a flow (and update the LiveData) */
    fun addFlow(flow: Flow) {
        manager.addFlow(flow)
        _items.value = manager.items
    }

    /**
     * Step 3: save the plan and automatically spin up a TeachingSession.
     * Emits progress, errors, and the new session for the UI to react to.
     */
    fun savePlan() {
        _saving.value = true
        _saveError.value = null

        manager.savePlan { error: DatabaseError? ->
            _saving.postValue(false)
            if (error != null) {
                // bubble up the failure
                _saveError.postValue(error.message)
            }
        }
    }
}
