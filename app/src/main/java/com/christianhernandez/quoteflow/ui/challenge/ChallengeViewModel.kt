package com.christianhernandez.quoteflow.ui.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.christianhernandez.quoteflow.data.model.Challenge
import com.christianhernandez.quoteflow.data.repository.QuoteRepository
import com.christianhernandez.quoteflow.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChallengeUiState(
    val challenge: Challenge = Challenge.dailyChallenge(0, 0),
    val showCelebration: Boolean = false,
    val isLoading: Boolean = false,
    val apiChallengeId: String? = null,
)

class ChallengeViewModel(
    private val repository: QuoteRepository? = null,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChallengeUiState())
    val uiState: StateFlow<ChallengeUiState> = _uiState.asStateFlow()

    private var streak = 0
    private var dailySwipes = 0

    init {
        loadTodayChallenge()
    }

    private fun loadTodayChallenge() {
        if (repository == null) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = repository.getTodayChallenge("en")
                if (response?.data != null) {
                    val apiChallenge = response.data
                    val progress = apiChallenge.progress ?: 0
                    val target = apiChallenge.target ?: Constants.DAILY_CHALLENGE_GOAL
                    _uiState.update {
                        it.copy(
                            challenge = Challenge(
                                id = apiChallenge.id,
                                title = apiChallenge.title,
                                titleEs = apiChallenge.title,
                                description = apiChallenge.description,
                                descriptionEs = apiChallenge.description,
                                goal = target,
                                progress = progress.coerceAtMost(target),
                                isComplete = apiChallenge.completed ?: (progress >= target),
                                streak = streak,
                            ),
                            apiChallengeId = apiChallenge.id,
                            isLoading = false,
                        )
                    }
                    dailySwipes = progress
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateProgress(totalSwipes: Int) {
        dailySwipes = totalSwipes
        val wasComplete = _uiState.value.challenge.isComplete
        val challenge = Challenge.dailyChallenge(
            progress = dailySwipes,
            streak = streak,
            goal = _uiState.value.challenge.goal.let { if (it > 0) it else Constants.DAILY_CHALLENGE_GOAL },
        )
        val justCompleted = challenge.isComplete && !wasComplete

        _uiState.update {
            it.copy(
                challenge = challenge,
                showCelebration = justCompleted,
            )
        }

        if (justCompleted) {
            streak++
            // Report progress to API
            val challengeId = _uiState.value.apiChallengeId
            if (challengeId != null && repository != null) {
                viewModelScope.launch {
                    try {
                        repository.incrementChallengeProgress(challengeId)
                    } catch (_: Exception) {
                        // Fire and forget
                    }
                }
            }
        }
    }

    /**
     * Report a single swipe to the API challenge progress.
     */
    fun reportSwipeToChallenge() {
        val challengeId = _uiState.value.apiChallengeId
        if (challengeId != null && repository != null) {
            viewModelScope.launch {
                try {
                    val result = repository.incrementChallengeProgress(challengeId)
                    if (result != null) {
                        val wasComplete = _uiState.value.challenge.isComplete
                        _uiState.update { state ->
                            state.copy(
                                challenge = state.challenge.copy(
                                    progress = result.progress.coerceAtMost(state.challenge.goal),
                                    isComplete = result.completed,
                                ),
                                showCelebration = result.completed && !wasComplete,
                            )
                        }
                        if (result.completed && !wasComplete) {
                            streak++
                        }
                    }
                } catch (_: Exception) {
                    // Silently fail
                }
            }
        }
    }

    fun dismissCelebration() {
        _uiState.update { it.copy(showCelebration = false) }
    }

    fun getStreak(): Int = streak

    fun getDailySwipes(): Int = dailySwipes

    fun isComplete(): Boolean = _uiState.value.challenge.isComplete

    class Factory(private val repository: QuoteRepository? = null) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChallengeViewModel::class.java)) {
                return ChallengeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
