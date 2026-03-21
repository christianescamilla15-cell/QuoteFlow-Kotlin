package com.christianhernandez.quoteflow.ui.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.christianhernandez.quoteflow.data.model.Challenge
import com.christianhernandez.quoteflow.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ChallengeUiState(
    val challenge: Challenge = Challenge.dailyChallenge(0, 0),
    val showCelebration: Boolean = false,
)

class ChallengeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ChallengeUiState())
    val uiState: StateFlow<ChallengeUiState> = _uiState.asStateFlow()

    private var streak = 0
    private var dailySwipes = 0

    fun updateProgress(totalSwipes: Int) {
        dailySwipes = totalSwipes
        val wasComplete = _uiState.value.challenge.isComplete
        val challenge = Challenge.dailyChallenge(
            progress = dailySwipes,
            streak = streak,
            goal = Constants.DAILY_CHALLENGE_GOAL,
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
        }
    }

    fun dismissCelebration() {
        _uiState.update { it.copy(showCelebration = false) }
    }

    fun getStreak(): Int = streak

    fun getDailySwipes(): Int = dailySwipes

    fun isComplete(): Boolean = _uiState.value.challenge.isComplete

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChallengeViewModel::class.java)) {
                return ChallengeViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
