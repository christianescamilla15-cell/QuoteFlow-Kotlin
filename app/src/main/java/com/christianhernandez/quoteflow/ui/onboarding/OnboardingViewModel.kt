package com.christianhernandez.quoteflow.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.christianhernandez.quoteflow.data.remote.ProfileRequest
import com.christianhernandez.quoteflow.data.repository.QuoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val currentStep: Int = 0,
    val ageRange: String? = null,
    val interest: String? = null,
    val goal: String? = null,
    val preferredLanguage: String? = null,
    val isSubmitting: Boolean = false,
    val isComplete: Boolean = false,
    val needsOnboarding: Boolean? = null, // null = still checking
)

class OnboardingViewModel(
    private val repository: QuoteRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun checkIfOnboardingNeeded() {
        viewModelScope.launch {
            try {
                val profile = repository.getProfile()
                val needsIt = profile == null ||
                    (profile.age_range == null && profile.interest == null && profile.goal == null)
                _uiState.update { it.copy(needsOnboarding = needsIt) }
            } catch (_: Exception) {
                // If we can't reach the server, skip onboarding for now
                _uiState.update { it.copy(needsOnboarding = false) }
            }
        }
    }

    fun selectAgeRange(value: String) {
        _uiState.update { it.copy(ageRange = value, currentStep = 1) }
    }

    fun selectInterest(value: String) {
        _uiState.update { it.copy(interest = value, currentStep = 2) }
    }

    fun selectGoal(value: String) {
        _uiState.update { it.copy(goal = value, currentStep = 3) }
    }

    fun selectLanguage(value: String) {
        _uiState.update { it.copy(preferredLanguage = value) }
        submitProfile()
    }

    private fun submitProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val state = _uiState.value
            val request = ProfileRequest(
                age_range = state.ageRange,
                interest = state.interest,
                goal = state.goal,
                preferred_language = state.preferredLanguage,
            )
            try {
                repository.updateProfile(request)
            } catch (_: Exception) {
                // Continue even if API fails
            }
            _uiState.update { it.copy(isSubmitting = false, isComplete = true, needsOnboarding = false) }
        }
    }

    fun goBack() {
        val step = _uiState.value.currentStep
        if (step > 0) {
            _uiState.update { it.copy(currentStep = step - 1) }
        }
    }

    class Factory(private val repository: QuoteRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {
                return OnboardingViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
