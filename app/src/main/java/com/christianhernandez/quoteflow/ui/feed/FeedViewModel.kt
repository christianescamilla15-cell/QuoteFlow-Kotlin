package com.christianhernandez.quoteflow.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.christianhernandez.quoteflow.data.model.Quote
import com.christianhernandez.quoteflow.data.repository.QuoteRepository
import com.christianhernandez.quoteflow.util.SwipeDirection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FeedUiState(
    val currentQuote: Quote? = null,
    val nextQuote: Quote? = null,
    val isLoading: Boolean = true,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null,
    val swipeCount: Int = 0,
    val savedCount: Int = 0,
    val feedQueue: List<Quote> = emptyList(),
    val currentCardAppearedAt: Long = 0L,
    val showPaywall: Boolean = false,
)

class FeedViewModel(private val repository: QuoteRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private var currentLang = if (java.util.Locale.getDefault().language == "es") "es" else "en"

    init {
        observeSavedCount()
    }

    private fun observeSavedCount() {
        viewModelScope.launch {
            repository.getSavedCount().collect { count ->
                _uiState.update { it.copy(savedCount = count) }
            }
        }
    }

    fun loadFeed(lang: String) {
        currentLang = lang
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isConnecting = true, errorMessage = null) }
            try {
                val quotes = repository.getFeed(lang)
                if (quotes.isNotEmpty()) {
                    val queue = quotes.toMutableList()
                    val current = queue.removeFirstOrNull()
                    val next = queue.removeFirstOrNull()
                    _uiState.update {
                        it.copy(
                            currentQuote = current,
                            nextQuote = next,
                            feedQueue = queue,
                            isLoading = false,
                            isConnecting = false,
                            errorMessage = null,
                            currentCardAppearedAt = System.currentTimeMillis(),
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, isConnecting = false) }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isConnecting = false,
                        errorMessage = e.message ?: "Error loading feed",
                    )
                }
            }
        }
    }

    fun onSwipe(direction: SwipeDirection) {
        val state = _uiState.value
        val current = state.currentQuote ?: return

        // Calculate dwell time
        val dwellTimeMs = if (state.currentCardAppearedAt > 0) {
            (System.currentTimeMillis() - state.currentCardAppearedAt).toInt()
        } else {
            0
        }

        // Map swipe direction to category string
        val directionStr = when (direction) {
            SwipeDirection.UP -> "up"
            SwipeDirection.RIGHT -> "right"
            SwipeDirection.LEFT -> "left"
            SwipeDirection.DOWN -> "down"
        }

        viewModelScope.launch {
            // Right swipe = save
            if (direction == SwipeDirection.RIGHT && !current.isSaved) {
                repository.saveQuote(current)
            }

            // Record swipe to API (fire-and-forget)
            repository.recordSwipe(
                quoteId = current.id,
                direction = directionStr,
                category = current.category,
                dwellTimeMs = dwellTimeMs,
            )

            loadNextQuote()
            _uiState.update {
                val newCount = it.swipeCount + 1
                it.copy(
                    swipeCount = newCount,
                    currentCardAppearedAt = System.currentTimeMillis(),
                    showPaywall = newCount == 20 && !it.showPaywall,
                )
            }
        }
    }

    fun onSave(quote: Quote) {
        viewModelScope.launch {
            val updated = repository.toggleSave(quote)
            _uiState.update { state ->
                if (state.currentQuote?.id == quote.id) {
                    state.copy(currentQuote = updated)
                } else {
                    state
                }
            }
        }
    }

    fun retry() {
        loadFeed(currentLang)
    }

    fun dismissPaywall() {
        _uiState.update { it.copy(showPaywall = false) }
    }

    private fun loadNextQuote() {
        _uiState.update { state ->
            val queue = state.feedQueue.toMutableList()
            val newCurrent = state.nextQuote
            val newNext = queue.removeFirstOrNull()

            // If queue is running low, reload
            if (queue.size <= 2) {
                viewModelScope.launch {
                    try {
                        val moreQuotes = repository.getFeed(currentLang)
                        _uiState.update { it.copy(feedQueue = it.feedQueue + moreQuotes) }
                    } catch (_: Exception) {
                        // Silently fail; user can keep swiping existing queue
                    }
                }
            }

            state.copy(
                currentQuote = newCurrent,
                nextQuote = newNext,
                feedQueue = queue,
            )
        }
    }

    class Factory(private val repository: QuoteRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
                return FeedViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
