package com.christianhernandez.quoteflow.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.christianhernandez.quoteflow.data.model.Quote
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao {

    @Query("SELECT * FROM quotes WHERE lang = :lang ORDER BY RANDOM() LIMIT :limit")
    suspend fun getFeed(lang: String, limit: Int = 20): List<Quote>

    @Query("SELECT * FROM quotes WHERE isSaved = 1 ORDER BY savedAt DESC")
    fun getSavedQuotes(): Flow<List<Quote>>

    @Query("SELECT COUNT(*) FROM quotes WHERE isSaved = 1")
    fun getSavedCount(): Flow<Int>

    @Update
    suspend fun updateQuote(quote: Quote)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(quotes: List<Quote>)

    @Query("SELECT COUNT(*) FROM quotes")
    suspend fun getCount(): Int

    @Query("SELECT * FROM quotes WHERE id = :id")
    suspend fun getQuoteById(id: String): Quote?

    @Query("SELECT * FROM quotes WHERE category = :category AND lang = :lang ORDER BY RANDOM() LIMIT :limit")
    suspend fun getByCategory(category: String, lang: String, limit: Int = 10): List<Quote>

    @Query("DELETE FROM quotes WHERE isSaved = 0")
    suspend fun deleteAllNonSaved()
}
