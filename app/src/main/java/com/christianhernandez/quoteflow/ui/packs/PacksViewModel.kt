package com.christianhernandez.quoteflow.ui.packs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.christianhernandez.quoteflow.data.model.Quote
import com.christianhernandez.quoteflow.data.remote.PackApi
import com.christianhernandez.quoteflow.data.repository.QuoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PacksUiState(
    val packs: List<PackApi> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isPremium: Boolean = false,
    val ownedPacks: List<String> = emptyList(),
    val previewQuotes: List<Quote> = emptyList(),
    val previewPackId: String? = null,
    val isLoadingPreview: Boolean = false,
)

class PacksViewModel(
    private val repository: QuoteRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PacksUiState())
    val uiState: StateFlow<PacksUiState> = _uiState.asStateFlow()

    init {
        loadPacks()
        loadPremiumStatus()
    }

    private fun loadPacks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = repository.getPacks()
                if (response != null) {
                    _uiState.update {
                        it.copy(packs = response.data, isLoading = false)
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Error loading packs")
                }
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
                            ownedPacks = status.owned_packs ?: emptyList(),
                        )
                    }
                }
            } catch (_: Exception) {
                // Silently fail
            }
        }
    }

    fun loadPreview(packId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingPreview = true, previewPackId = packId) }
            try {
                val response = repository.getPackPreview(packId)
                val quotes = response?.data?.map { apiQuote ->
                    Quote(
                        id = apiQuote.id,
                        text = apiQuote.text,
                        author = apiQuote.author,
                        category = apiQuote.category,
                        lang = apiQuote.lang,
                    )
                } ?: emptyList()
                _uiState.update {
                    it.copy(previewQuotes = quotes, isLoadingPreview = false)
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoadingPreview = false) }
            }
        }
    }

    fun dismissPreview() {
        _uiState.update { it.copy(previewQuotes = emptyList(), previewPackId = null) }
    }

    fun retry() {
        loadPacks()
    }

    class Factory(private val repository: QuoteRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PacksViewModel::class.java)) {
                return PacksViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
