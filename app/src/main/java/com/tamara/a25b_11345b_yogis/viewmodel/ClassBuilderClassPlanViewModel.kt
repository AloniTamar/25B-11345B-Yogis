package com.tamara.a25b_11345b_yogis.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tamara.a25b_11345b_yogis.data.model.ClassPlanElement
import com.tamara.a25b_11345b_yogis.data.model.Pose
import com.tamara.a25b_11345b_yogis.data.model.Flow
import com.tamara.a25b_11345b_yogis.data.model.Pose.Level

class ClassBuilderClassPlanViewModel : ViewModel() {

    val className = MutableLiveData<String>()
    val durationMinutes = MutableLiveData<Int>()
    private val _level = MutableLiveData<Level?>(null)
    val level: LiveData<Level?> = _level

    private val _elements = MutableLiveData<List<ClassPlanElement>>(emptyList())
    val elements: LiveData<List<ClassPlanElement>> = _elements

    fun addPose(pose: Pose) {
        _elements.value = _elements.value.orEmpty() + ClassPlanElement.PoseElement(pose)
    }

    fun addFlow(flow: Flow) {
        _elements.value = _elements.value.orEmpty() + ClassPlanElement.FlowElement(flow)
    }

    fun clearPlan() {
        _level.value = null
        _elements.value = emptyList()
    }

    fun setClassInfo(name: String, minutes: Int, lvl: Level) {
        className.value = name
        durationMinutes.value = minutes
        _level.value = lvl
    }

}
