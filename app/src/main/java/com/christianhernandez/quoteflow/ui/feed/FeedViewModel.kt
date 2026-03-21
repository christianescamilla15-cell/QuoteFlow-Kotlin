package com.christianhernandez.quoteflow.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.christianhernandez.quoteflow.data.model.Quote
import com.christianhernandez.quoteflow.data.repository.PhilosophyPoints
import com.christianhernandez.quoteflow.data.repository.PhilosophyRepository
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
    val showLikeAnimation: Boolean = false,
    val philosophyPoints: PhilosophyPoints = PhilosophyPoints(),
)

class FeedViewModel(private val repository: QuoteRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private var currentLang = if (java.util.Locale.getDefault().language == "es") "es" else "en"

    init {
        observeSavedCount()
        observePhilosophyPoints()
    }

    private fun observeSavedCount() {
        viewModelScope.launch {
            repository.getSavedCount().collect { count ->
                _uiState.update { it.copy(savedCount = count) }
            }
        }
    }

    /** Keep local UI state in sync with the shared PhilosophyRepository */
    private fun observePhilosophyPoints() {
        viewModelScope.launch {
            PhilosophyRepository.points.collect { points ->
                _uiState.update { it.copy(philosophyPoints = points) }
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
                    val balanced = balanceFeed(quotes)
                    val queue = balanced.toMutableList()
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

    /**
     * Balance the feed: ensure ~5 quotes per category so there is always
     * a quote for each swipe direction. Shuffled so categories are mixed.
     */
    private fun balanceFeed(quotes: List<Quote>): List<Quote> {
        val byCategory = quotes.groupBy { it.category }
        val categories = listOf("stoicism", "discipline", "reflection", "philosophy")
        val balanced = mutableListOf<Quote>()
        val perCategory = 5

        // Take up to 5 from each category
        for (cat in categories) {
            val catQuotes = byCategory[cat] ?: emptyList()
            balanced.addAll(catQuotes.take(perCategory))
        }

        // If we don't have enough from some categories, fill with remaining
        if (balanced.size < 20) {
            val usedIds = balanced.map { it.id }.toSet()
            val remaining = quotes.filter { it.id !in usedIds }
            balanced.addAll(remaining.take(20 - balanced.size))
        }

        // Shuffle to mix categories (not all stoicism then all discipline)
        return balanced.shuffled()
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
            // Swipe = 1 point for the quote's category (shared repository)
            PhilosophyRepository.addPoints(current.category, 1)

            // Record swipe to API (fire-and-forget)
            repository.recordSwipe(
                quoteId = current.id,
                direction = directionStr,
                category = current.category,
                dwellTimeMs = dwellTimeMs,
            )

            // Map direction to the category the user WANTS to see next
            val nextCategory = when (direction) {
                SwipeDirection.UP -> "stoicism"
                SwipeDirection.RIGHT -> "discipline"
                SwipeDirection.LEFT -> "reflection"
                SwipeDirection.DOWN -> "philosophy"
            }
            loadNextQuoteByCategory(nextCategory)
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
            // Vault save = 3 points (shared repository)
            if (updated.isSaved) {
                PhilosophyRepository.addPoints(quote.category, 3)
                _uiState.update {
                    it.copy(
                        currentQuote = if (it.currentQuote?.id == quote.id) updated else it.currentQuote,
                    )
                }
            } else {
                _uiState.update { state ->
                    if (state.currentQuote?.id == quote.id) state.copy(currentQuote = updated) else state
                }
            }
        }
    }

    // Double-tap = Like = 2 points
    fun onDoubleTapLike() {
        val current = _uiState.value.currentQuote ?: return
        PhilosophyRepository.addPoints(current.category, 2)
        _uiState.update {
            it.copy(showLikeAnimation = true)
        }
        // Hide animation after 800ms
        viewModelScope.launch {
            kotlinx.coroutines.delay(800)
            _uiState.update { it.copy(showLikeAnimation = false) }
        }
        // Record like to API
        viewModelScope.launch {
            try { repository.recordSwipe(current.id, "like", current.category, 0) } catch (_: Exception) {}
        }
    }

    // Share = 4 points
    fun onShare(quote: Quote) {
        PhilosophyRepository.addPoints(quote.category, 4)
    }

    fun retry() {
        loadFeed(currentLang)
    }

    fun dismissPaywall() {
        _uiState.update { it.copy(showPaywall = false) }
    }

    /**
     * Load next quote matching the requested category.
     * If no match in queue, falls back to any available quote.
     * The swipe direction determines what category appears next:
     * UP=stoicism, RIGHT=discipline, LEFT=reflection, DOWN=philosophy
     */
    private fun loadNextQuoteByCategory(category: String) {
        _uiState.update { state ->
            val queue = state.feedQueue.toMutableList()

            // Find a quote matching the requested category
            val matchIndex = queue.indexOfFirst { it.category == category }
            val newCurrent = if (matchIndex >= 0) {
                queue.removeAt(matchIndex)
            } else {
                // Fallback: take any quote from queue
                queue.removeFirstOrNull()
            }

            // For next preview, try to pick a different category for variety
            val newNext = queue.firstOrNull()

            // If queue is running low (< 5 quotes), fetch more
            if (queue.size < 5) {
                viewModelScope.launch {
                    try {
                        val moreQuotes = repository.getFeed(currentLang)
                        val balanced = balanceFeed(moreQuotes)
                        _uiState.update { current ->
                            // If currentQuote became null (empty queue), populate it
                            if (current.currentQuote == null && balanced.isNotEmpty()) {
                                val refillQueue = balanced.toMutableList()
                                val fillCurrent = refillQueue.removeFirstOrNull()
                                val fillNext = refillQueue.removeFirstOrNull()
                                current.copy(
                                    currentQuote = fillCurrent,
                                    nextQuote = fillNext,
                                    feedQueue = current.feedQueue + refillQueue,
                                    currentCardAppearedAt = System.currentTimeMillis(),
                                )
                            } else {
                                current.copy(feedQueue = current.feedQueue + balanced)
                            }
                        }
                    } catch (_: Exception) {
                        // Silently fail; user can keep swiping existing queue
                    }
                }
            }

            // If newCurrent is null (queue was exhausted), trigger a full reload
            if (newCurrent == null) {
                viewModelScope.launch { loadFeed(currentLang) }
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
