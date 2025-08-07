package com.tamara.a25b_11345b_yogis.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseError
import com.tamara.a25b_11345b_yogis.data.firebase.ClassPlanBuilderManager
import com.tamara.a25b_11345b_yogis.data.model.ClassPlanElement
import com.tamara.a25b_11345b_yogis.data.model.Flow
import com.tamara.a25b_11345b_yogis.data.model.Pose

class TeachingModeViewModel(
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
}
