package com.christianhernandez.quoteflow

import com.christianhernandez.quoteflow.data.local.QuoteDao
import com.christianhernandez.quoteflow.data.model.Quote
import com.christianhernandez.quoteflow.data.repository.QuoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class QuoteRepositoryTest {

    private lateinit var fakeDao: FakeQuoteDao
    private lateinit var repository: QuoteRepository

    @Before
    fun setup() {
        fakeDao = FakeQuoteDao()
        repository = QuoteRepository(fakeDao, null)
    }

    @Test
    fun `getFeed returns quotes for given language`() = runTest {
        fakeDao.insertAll(
            listOf(
                createQuote("1", lang = "en"),
                createQuote("2", lang = "en"),
                createQuote("3", lang = "es"),
            )
        )

        val feed = repository.getFeed("en")
        assertEquals(2, feed.size)
        assertTrue(feed.all { it.lang == "en" })
    }

    @Test
    fun `getFeed returns empty list when no quotes match language`() = runTest {
        fakeDao.insertAll(listOf(createQuote("1", lang = "en")))

        val feed = repository.getFeed("es")
        assertTrue(feed.isEmpty())
    }

    @Test
    fun `saveQuote marks quote as saved with timestamp`() = runTest {
        val quote = createQuote("1")
        fakeDao.insertAll(listOf(quote))

        repository.saveQuote(quote)

        val saved = fakeDao.getQuoteById("1")
        assertNotNull(saved)
        assertTrue(saved!!.isSaved)
        assertNotNull(saved.savedAt)
    }

    @Test
    fun `unsaveQuote removes saved status`() = runTest {
        val quote = createQuote("1", isSaved = true, savedAt = 123L)
        fakeDao.insertAll(listOf(quote))

        repository.unsaveQuote(quote)

        val unsaved = fakeDao.getQuoteById("1")
        assertNotNull(unsaved)
        assertFalse(unsaved!!.isSaved)
        assertNull(unsaved.savedAt)
    }

    @Test
    fun `toggleSave saves an unsaved quote`() = runTest {
        val quote = createQuote("1", isSaved = false)
        fakeDao.insertAll(listOf(quote))

        val result = repository.toggleSave(quote)

        assertTrue(result.isSaved)
        assertNotNull(result.savedAt)
    }

    @Test
    fun `toggleSave unsaves a saved quote`() = runTest {
        val quote = createQuote("1", isSaved = true, savedAt = 100L)
        fakeDao.insertAll(listOf(quote))

        val result = repository.toggleSave(quote)

        assertFalse(result.isSaved)
        assertNull(result.savedAt)
    }

    @Test
    fun `getSavedCount returns correct count`() = runTest {
        fakeDao.insertAll(
            listOf(
                createQuote("1", isSaved = true, savedAt = 1L),
                createQuote("2", isSaved = false),
                createQuote("3", isSaved = true, savedAt = 2L),
            )
        )

        val count = repository.getSavedCount().first()
        assertEquals(2, count)
    }

    @Test
    fun `getQuoteCount returns total count`() = runTest {
        fakeDao.insertAll(
            listOf(
                createQuote("1"),
                createQuote("2"),
                createQuote("3"),
            )
        )

        val count = repository.getQuoteCount()
        assertEquals(3, count)
    }

    private fun createQuote(
        id: String,
        text: String = "Test quote $id",
        author: String = "Author $id",
        category: String = "stoicism",
        lang: String = "en",
        isSaved: Boolean = false,
        savedAt: Long? = null,
    ) = Quote(id, text, author, category, lang, isSaved, savedAt)
}

/**
 * Fake DAO implementation for testing without Room database.
 */
class FakeQuoteDao : QuoteDao {
    private val quotes = mutableListOf<Quote>()
    private val quotesFlow = MutableStateFlow<List<Quote>>(emptyList())

    private fun emitUpdate() {
        quotesFlow.value = quotes.toList()
    }

    override suspend fun getFeed(lang: String, limit: Int): List<Quote> {
        return quotes.filter { it.lang == lang }.take(limit)
    }

    override fun getSavedQuotes(): Flow<List<Quote>> {
        return quotesFlow.map { list ->
            list.filter { it.isSaved }.sortedByDescending { it.savedAt }
        }
    }

    override fun getSavedCount(): Flow<Int> {
        return quotesFlow.map { list -> list.count { it.isSaved } }
    }

    override suspend fun updateQuote(quote: Quote) {
        val index = quotes.indexOfFirst { it.id == quote.id }
        if (index >= 0) {
            quotes[index] = quote
            emitUpdate()
        }
    }

    override suspend fun insertAll(quotes: List<Quote>) {
        quotes.forEach { quote ->
            val index = this.quotes.indexOfFirst { it.id == quote.id }
            if (index >= 0) {
                this.quotes[index] = quote
            } else {
                this.quotes.add(quote)
            }
        }
        emitUpdate()
    }

    override suspend fun getCount(): Int = quotes.size

    override suspend fun getQuoteById(id: String): Quote? {
        return quotes.find { it.id == id }
    }

    override suspend fun getByCategory(category: String, lang: String, limit: Int): List<Quote> {
        return quotes.filter { it.category == category && it.lang == lang }.take(limit)
    }

    override suspend fun deleteAllNonSaved() {
        quotes.removeAll { !it.isSaved }
        emitUpdate()
    }
}
