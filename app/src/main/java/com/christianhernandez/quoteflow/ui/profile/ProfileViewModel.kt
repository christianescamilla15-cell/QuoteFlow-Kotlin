package com.christianhernandez.quoteflow.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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
)

class ProfileViewModel(private val repository: QuoteRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeSavedCount()
        loadTotalQuotes()
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
