package com.christianhernandez.quoteflow.data.repository

import com.christianhernandez.quoteflow.data.local.QuoteDao
import com.christianhernandez.quoteflow.data.model.Quote
import kotlinx.coroutines.flow.Flow

class QuoteRepository(private val quoteDao: QuoteDao) {

    fun getSavedQuotes(): Flow<List<Quote>> = quoteDao.getSavedQuotes()

    fun getSavedCount(): Flow<Int> = quoteDao.getSavedCount()

    suspend fun getFeed(lang: String, limit: Int = 20): List<Quote> {
        return quoteDao.getFeed(lang, limit)
    }

    suspend fun saveQuote(quote: Quote) {
        val updated = quote.copy(isSaved = true, savedAt = System.currentTimeMillis())
        quoteDao.updateQuote(updated)
    }

    suspend fun unsaveQuote(quote: Quote) {
        val updated = quote.copy(isSaved = false, savedAt = null)
        quoteDao.updateQuote(updated)
    }

    suspend fun toggleSave(quote: Quote): Quote {
        val updated = if (quote.isSaved) {
            quote.copy(isSaved = false, savedAt = null)
        } else {
            quote.copy(isSaved = true, savedAt = System.currentTimeMillis())
        }
        quoteDao.updateQuote(updated)
        return updated
    }

    suspend fun getQuoteCount(): Int = quoteDao.getCount()

    suspend fun getByCategory(category: String, lang: String): List<Quote> {
        return quoteDao.getByCategory(category, lang)
    }
}
