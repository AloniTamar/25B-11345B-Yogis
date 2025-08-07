package com.tamara.a25b_11345b_yogis.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tamara.a25b_11345b_yogis.data.model.ClassPlanElement
import com.tamara.a25b_11345b_yogis.data.model.Pose
import com.tamara.a25b_11345b_yogis.data.model.Flow
import com.tamara.a25b_11345b_yogis.data.model.Pose.Level

class ClassBuilderClassPlanViewModel : ViewModel() {

    // Metadata for the class (set from UI)
    private val _level = MutableLiveData<Level?>(null)
    val level: LiveData<Level?> = _level

    // List of plan elements (poses and flows)
    private val _elements = MutableLiveData<List<ClassPlanElement>>(emptyList())
    val elements: LiveData<List<ClassPlanElement>> = _elements

    // Add pose
    fun addPose(pose: Pose) {
        _elements.value = _elements.value.orEmpty() + ClassPlanElement.PoseElement(pose)
    }

    // Add flow
    fun addFlow(flow: Flow) {
        _elements.value = _elements.value.orEmpty() + ClassPlanElement.FlowElement(flow)
    }

    // Remove an element by position
    fun removeElement(index: Int) {
        _elements.value = _elements.value.orEmpty().toMutableList().also { it.removeAt(index) }
    }

    // Clear plan (for reset/new class)
    fun clearPlan() {
        _level.value = null
        _elements.value = emptyList()
    }

}
