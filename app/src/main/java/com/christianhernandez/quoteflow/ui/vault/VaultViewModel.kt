package com.christianhernandez.quoteflow.ui.vault

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.christianhernandez.quoteflow.data.model.Quote
import com.christianhernandez.quoteflow.data.repository.QuoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VaultUiState(
    val savedQuotes: List<Quote> = emptyList(),
    val isLoading: Boolean = true,
)

class VaultViewModel(private val repository: QuoteRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(VaultUiState())
    val uiState: StateFlow<VaultUiState> = _uiState.asStateFlow()

    init {
        observeSavedQuotes()
    }

    private fun observeSavedQuotes() {
        viewModelScope.launch {
            repository.getSavedQuotes().collect { quotes ->
                _uiState.update {
                    it.copy(savedQuotes = quotes, isLoading = false)
                }
            }
        }
    }

    fun unsaveQuote(quote: Quote) {
        viewModelScope.launch {
            repository.unsaveQuote(quote)
        }
    }

    class Factory(private val repository: QuoteRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(VaultViewModel::class.java)) {
                return VaultViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
