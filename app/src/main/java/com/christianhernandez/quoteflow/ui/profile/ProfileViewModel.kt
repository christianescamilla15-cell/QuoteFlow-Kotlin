package com.christianhernandez.quoteflow.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.christianhernandez.quoteflow.data.remote.MapScores
import com.christianhernandez.quoteflow.data.remote.ProfileRequest
import com.christianhernandez.quoteflow.data.repository.PhilosophyRepository
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
    val mapDelta: MapScores? = null,
    val isLoadingProfile: Boolean = false,
    val isSavingSnapshot: Boolean = false,
    val snapshotSaved: Boolean = false,
    val cacheCleared: Boolean = false,
)

class ProfileViewModel(private val repository: QuoteRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // Store API scores separately so we can merge with local
    private var apiScores: MapScores? = null
    private var apiDelta: MapScores? = null

    init {
        observeSavedCount()
        loadTotalQuotes()
        loadProfile()
        loadPremiumStatus()
        loadPhilosophyMap()
        observeLocalPhilosophyPoints()
    }

    private fun observeSavedCount() {
        viewModelScope.launch {
            repository.getSavedCount().collect { count ->
                _uiState.update { it.copy(savedCount = count) }
            }
        }
    }

    /**
     * Observe local PhilosophyRepository points in real-time
     * and merge with API scores for the radar chart.
     */
    private fun observeLocalPhilosophyPoints() {
        viewModelScope.launch {
            PhilosophyRepository.points.collect { localPoints ->
                val merged = mergeScores(apiScores, localPoints)
                _uiState.update { it.copy(mapScores = merged) }
            }
        }
    }

    /**
     * Merge API scores with local PhilosophyRepository points.
     * Local points are converted to 0-100 scale and averaged with API scores.
     */
    private fun mergeScores(
        api: MapScores?,
        local: com.christianhernandez.quoteflow.data.repository.PhilosophyPoints,
    ): MapScores {
        val localScores = PhilosophyRepository.getScores()
        if (api == null) {
            // No API data: use local scores only
            return localScores
        }
        // Merge: average of API and local (weighted toward whichever has more data)
        val localTotal = local.total()
        if (localTotal == 0) return api

        // Weighted merge: local gets more weight as user interacts more
        val localWeight = (localTotal.coerceAtMost(50) / 50f).coerceIn(0f, 0.6f)
        val apiWeight = 1f - localWeight

        return MapScores(
            wisdom = ((api.wisdom ?: 0) * apiWeight + (localScores.wisdom ?: 0) * localWeight).toInt(),
            discipline = ((api.discipline ?: 0) * apiWeight + (localScores.discipline ?: 0) * localWeight).toInt(),
            reflection = ((api.reflection ?: 0) * apiWeight + (localScores.reflection ?: 0) * localWeight).toInt(),
            philosophy = ((api.philosophy ?: 0) * apiWeight + (localScores.philosophy ?: 0) * localWeight).toInt(),
        )
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
                if (map != null) {
                    apiScores = map.current
                    apiDelta = map.delta
                    // Merge with current local points
                    val merged = mergeScores(map.current, PhilosophyRepository.points.value)
                    _uiState.update {
                        it.copy(
                            mapScores = merged,
                            mapDelta = map.delta,
                        )
                    }
                } else {
                    // No API data; use local scores if available
                    val localScores = PhilosophyRepository.getScores()
                    val hasLocal = PhilosophyRepository.points.value.total() > 0
                    if (hasLocal) {
                        _uiState.update { it.copy(mapScores = localScores) }
                    }
                }
            } catch (_: Exception) {
                // Use local scores as fallback
                val localScores = PhilosophyRepository.getScores()
                val hasLocal = PhilosophyRepository.points.value.total() > 0
                if (hasLocal) {
                    _uiState.update { it.copy(mapScores = localScores) }
                }
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

    fun saveSnapshot() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingSnapshot = true, snapshotSaved = false) }
            try {
                val result = repository.saveMapSnapshot()
                _uiState.update {
                    it.copy(
                        isSavingSnapshot = false,
                        snapshotSaved = result?.saved == true || result != null,
                    )
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(isSavingSnapshot = false) }
            }
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            try {
                repository.clearCache()
                _uiState.update { it.copy(cacheCleared = true) }
            } catch (_: Exception) {
                // Silently fail
            }
        }
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
