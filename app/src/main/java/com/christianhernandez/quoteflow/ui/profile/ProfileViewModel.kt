package com.christianhernandez.quoteflow.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.christianhernandez.quoteflow.data.remote.MapScores
import com.christianhernandez.quoteflow.data.remote.ProfileRequest
import com.christianhernandez.quoteflow.data.repository.QuoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val totalSwipes: Int = 0,
    val savedCount: Int = 0,
    val totalQuotes: Int = 0,
    val ageRange: String? = null,
    val interest: String? = null,
    val goal: String? = null,
    val preferredLanguage: String? = null,
    val isPremium: Boolean = false,
    val trialActive: Boolean? = null,
    val dailySwipesRemaining: Int? = null,
    val mapScores: MapScores? = null,
    val isLoadingProfile: Boolean = false,
)

class ProfileViewModel(private val repository: QuoteRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeSavedCount()
        loadTotalQuotes()
        loadProfile()
        loadPremiumStatus()
        loadPhilosophyMap()
    }

    private fun observeSavedCount() {
        viewModelScope.launch {
            repository.getSavedCount().collect { count ->
                _uiState.update { it.copy(savedCount = count) }
            }
        }
    }

    private fun loadTotalQuotes() {
        viewModelScope.launch {
            val count = repository.getQuoteCount()
            _uiState.update { it.copy(totalQuotes = count) }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingProfile = true) }
            try {
                val profile = repository.getProfile()
                if (profile != null) {
                    _uiState.update {
                        it.copy(
                            ageRange = profile.age_range,
                            interest = profile.interest,
                            goal = profile.goal,
                            preferredLanguage = profile.preferred_language,
                            isLoadingProfile = false,
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoadingProfile = false) }
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoadingProfile = false) }
            }
        }
    }

    private fun loadPremiumStatus() {
        viewModelScope.launch {
            try {
                val status = repository.getPremiumStatus()
                if (status != null) {
                    _uiState.update {
                        it.copy(
                            isPremium = status.is_premium,
                            trialActive = status.trial_active,
                            dailySwipesRemaining = status.daily_swipes_remaining,
                        )
                    }
                }
            } catch (_: Exception) {
                // Silently fail
            }
        }
    }

    private fun loadPhilosophyMap() {
        viewModelScope.launch {
            try {
                val map = repository.getPhilosophyMap()
                if (map?.current != null) {
                    _uiState.update { it.copy(mapScores = map.current) }
                }
            } catch (_: Exception) {
                // Silently fail
            }
        }
    }

    fun updateProfile(
        ageRange: String? = null,
        interest: String? = null,
        goal: String? = null,
        preferredLanguage: String? = null,
    ) {
        viewModelScope.launch {
            val request = ProfileRequest(
                age_range = ageRange ?: _uiState.value.ageRange,
                interest = interest ?: _uiState.value.interest,
                goal = goal ?: _uiState.value.goal,
                preferred_language = preferredLanguage ?: _uiState.value.preferredLanguage,
            )
            val success = repository.updateProfile(request)
            if (success) {
                _uiState.update {
                    it.copy(
                        ageRange = request.age_range,
                        interest = request.interest,
                        goal = request.goal,
                        preferredLanguage = request.preferred_language,
                    )
                }
            }
        }
    }

    fun updateSwipeCount(count: Int) {
        _uiState.update { it.copy(totalSwipes = count) }
    }

    class Factory(private val repository: QuoteRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                return ProfileViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
