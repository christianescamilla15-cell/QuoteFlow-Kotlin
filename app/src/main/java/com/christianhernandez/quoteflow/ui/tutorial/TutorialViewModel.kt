package com.christianhernandez.quoteflow.ui.tutorial

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TutorialViewModel : ViewModel() {

    private val _currentStep = MutableStateFlow(0)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    private val _showTutorial = MutableStateFlow(false)
    val showTutorial: StateFlow<Boolean> = _showTutorial.asStateFlow()

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val completed = prefs?.getBoolean(KEY_TUTORIAL_COMPLETED, false) ?: false
        _showTutorial.value = !completed
    }

    fun nextStep() {
        val next = _currentStep.value + 1
        if (next >= TOTAL_STEPS) {
            completeTutorial()
        } else {
            _currentStep.value = next
        }
    }

    private fun completeTutorial() {
        prefs?.edit()?.putBoolean(KEY_TUTORIAL_COMPLETED, true)?.apply()
        _showTutorial.value = false
        _currentStep.value = 0
    }

    companion object {
        const val PREFS_NAME = "quoteflow_tutorial_prefs"
        const val KEY_TUTORIAL_COMPLETED = "tutorial_completed"
        const val TOTAL_STEPS = 10
    }
}
