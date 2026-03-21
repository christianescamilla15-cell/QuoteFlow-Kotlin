package com.christianhernandez.quoteflow.data.repository

import com.christianhernandez.quoteflow.data.local.QuoteDao
import com.christianhernandez.quoteflow.data.model.Quote
import com.christianhernandez.quoteflow.data.remote.ApiService
import com.christianhernandez.quoteflow.data.remote.SaveVaultRequest
import com.christianhernandez.quoteflow.data.remote.SwipeRequest
import com.christianhernandez.quoteflow.data.remote.ChallengeResponse
import com.christianhernandez.quoteflow.data.remote.ProgressResponse
import com.christianhernandez.quoteflow.data.remote.ProfileResponse
import com.christianhernandez.quoteflow.data.remote.ProfileRequest
import com.christianhernandez.quoteflow.data.remote.PremiumStatusResponse
import com.christianhernandez.quoteflow.data.remote.MapResponse
import com.christianhernandez.quoteflow.data.remote.PacksResponse
import com.christianhernandez.quoteflow.data.remote.PackPreviewResponse
import com.christianhernandez.quoteflow.data.remote.PackFeedResponse
import com.christianhernandez.quoteflow.data.remote.SnapshotResponse
import com.christianhernandez.quoteflow.data.remote.VaultItemApi
import kotlinx.coroutines.flow.Flow

class QuoteRepository(
    private val quoteDao: QuoteDao,
    private val apiService: ApiService? = null,
) {

    fun getSavedQuotes(): Flow<List<Quote>> = quoteDao.getSavedQuotes()

    fun getSavedCount(): Flow<Int> = quoteDao.getSavedCount()

    /**
     * Fetch feed from API, cache in Room. Falls back to Room on error.
     */
    suspend fun getFeed(lang: String, limit: Int = 20, cursor: String? = null): List<Quote> {
        if (apiService != null) {
            try {
                val response = apiService.getFeed(lang, limit, cursor)
                val quotes = response.data.map { apiQuote ->
                    Quote(
                        id = apiQuote.id,
                        text = apiQuote.text,
                        author = apiQuote.author,
                        category = apiQuote.category,
                        lang = apiQuote.lang,
                    )
                }
                // Cache in Room for offline use
                if (quotes.isNotEmpty()) {
                    quoteDao.insertAll(quotes)
                }
                return quotes
            } catch (_: Exception) {
                // Fallback to Room cache
            }
        }
        return quoteDao.getFeed(lang, limit)
    }

    /**
     * Record a swipe event to the API.
     */
    suspend fun recordSwipe(
        quoteId: String,
        direction: String,
        category: String,
        dwellTimeMs: Int,
    ) {
        if (apiService != null) {
            try {
                apiService.recordSwipe(
                    SwipeRequest(
                        quote_id = quoteId,
                        direction = direction,
                        category = category,
                        dwell_time_ms = dwellTimeMs,
                    )
                )
            } catch (_: Exception) {
                // Swipe recording is fire-and-forget; don't block the UI
            }
        }
    }

    /**
     * Save quote to API vault and mark locally.
     */
    suspend fun saveQuote(quote: Quote) {
        val updated = quote.copy(isSaved = true, savedAt = System.currentTimeMillis())
        quoteDao.updateQuote(updated)
        if (apiService != null) {
            try {
                apiService.saveToVault(SaveVaultRequest(quote_id = quote.id))
            } catch (_: Exception) {
                // Saved locally even if API fails
            }
        }
    }

    /**
     * Remove quote from API vault and unmark locally.
     */
    suspend fun unsaveQuote(quote: Quote) {
        val updated = quote.copy(isSaved = false, savedAt = null)
        quoteDao.updateQuote(updated)
        if (apiService != null) {
            try {
                apiService.removeFromVault(quote.id)
            } catch (_: Exception) {
                // Unsaved locally even if API fails
            }
        }
    }

    suspend fun toggleSave(quote: Quote): Quote {
        val updated = if (quote.isSaved) {
            quote.copy(isSaved = false, savedAt = null)
        } else {
            quote.copy(isSaved = true, savedAt = System.currentTimeMillis())
        }
        quoteDao.updateQuote(updated)
        if (apiService != null) {
            try {
                if (updated.isSaved) {
                    apiService.saveToVault(SaveVaultRequest(quote_id = quote.id))
                } else {
                    apiService.removeFromVault(quote.id)
                }
            } catch (_: Exception) {
                // Local state is source of truth
            }
        }
        return updated
    }

    /**
     * Fetch vault from API and sync with local Room database.
     */
    suspend fun syncVaultFromApi(): List<Quote> {
        if (apiService != null) {
            try {
                val response = apiService.getVault()
                val vaultQuotes = response.data.map { item ->
                    Quote(
                        id = item.quote_id,
                        text = item.text,
                        author = item.author,
                        category = item.category ?: "reflection",
                        lang = "en",
                        isSaved = true,
                        savedAt = System.currentTimeMillis(),
                    )
                }
                if (vaultQuotes.isNotEmpty()) {
                    quoteDao.insertAll(vaultQuotes)
                }
                return vaultQuotes
            } catch (_: Exception) {
                // Fallback to local
            }
        }
        return emptyList()
    }

    /**
     * Get today's challenge from API.
     */
    suspend fun getTodayChallenge(lang: String = "en"): ChallengeResponse? {
        if (apiService != null) {
            try {
                return apiService.getTodayChallenge(lang)
            } catch (_: Exception) {
                // Fallback handled by caller
            }
        }
        return null
    }

    /**
     * Increment challenge progress on the API.
     */
    suspend fun incrementChallengeProgress(challengeId: String): ProgressResponse? {
        if (apiService != null) {
            try {
                return apiService.incrementProgress(challengeId)
            } catch (_: Exception) {
                // Handled by caller
            }
        }
        return null
    }

    /**
     * Get user profile from API.
     */
    suspend fun getProfile(): ProfileResponse? {
        if (apiService != null) {
            try {
                return apiService.getProfile()
            } catch (_: Exception) {
                // Fallback
            }
        }
        return null
    }

    /**
     * Update user profile on API.
     */
    suspend fun updateProfile(request: ProfileRequest): Boolean {
        if (apiService != null) {
            try {
                val response = apiService.updateProfile(request)
                return response.isSuccessful
            } catch (_: Exception) {
                // Fallback
            }
        }
        return false
    }

    /**
     * Get premium status from API.
     */
    suspend fun getPremiumStatus(): PremiumStatusResponse? {
        if (apiService != null) {
            try {
                return apiService.getPremiumStatus()
            } catch (_: Exception) {
                // Fallback
            }
        }
        return null
    }

    /**
     * Get philosophy map scores from API.
     */
    suspend fun getPhilosophyMap(): MapResponse? {
        if (apiService != null) {
            try {
                return apiService.getPhilosophyMap()
            } catch (_: Exception) {
                // Fallback
            }
        }
        return null
    }

    suspend fun getQuoteCount(): Int = quoteDao.getCount()

    suspend fun getByCategory(category: String, lang: String): List<Quote> {
        return quoteDao.getByCategory(category, lang)
    }

    /**
     * Save a philosophy map snapshot via API.
     */
    suspend fun saveMapSnapshot(): SnapshotResponse? {
        if (apiService != null) {
            try {
                return apiService.saveMapSnapshot()
            } catch (_: Exception) {
                // Fallback
            }
        }
        return null
    }

    /**
     * Get available packs from API.
     */
    suspend fun getPacks(): PacksResponse? {
        if (apiService != null) {
            try {
                return apiService.getPacks()
            } catch (_: Exception) {
                // Fallback
            }
        }
        return null
    }

    /**
     * Get preview quotes for a pack.
     */
    suspend fun getPackPreview(packId: String, lang: String = "en"): PackPreviewResponse? {
        if (apiService != null) {
            try {
                return apiService.getPackPreview(packId, lang)
            } catch (_: Exception) {
                // Fallback
            }
        }
        return null
    }

    /**
     * Get full feed for an entitled pack.
     */
    suspend fun getPackFeed(packId: String, cursor: String? = null): PackFeedResponse? {
        if (apiService != null) {
            try {
                return apiService.getPackFeed(packId, cursor)
            } catch (_: Exception) {
                // Fallback
            }
        }
        return null
    }

    /**
     * Clear all cached quotes from Room database.
     */
    suspend fun clearCache() {
        quoteDao.deleteAllNonSaved()
    }

    /**
     * Get weekly insight/reflection text from API.
     */
    suspend fun getWeeklyInsight(lang: String = "en"): String? {
        if (apiService != null) {
            try {
                val response = apiService.getWeeklyInsight(lang)
                return response.reflection
            } catch (_: Exception) {
                // Fallback to local generation handled by caller
            }
        }
        return null
    }
}
