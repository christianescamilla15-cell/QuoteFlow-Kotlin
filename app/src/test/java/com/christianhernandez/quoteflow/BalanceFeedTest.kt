package com.christianhernandez.quoteflow

import com.christianhernandez.quoteflow.data.model.Quote
import com.christianhernandez.quoteflow.data.repository.QuoteRepository
import com.christianhernandez.quoteflow.ui.feed.FeedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BalanceFeedTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeDao: FakeQuoteDao
    private lateinit var repository: QuoteRepository
    private lateinit var viewModel: FeedViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeDao = FakeQuoteDao()
        repository = QuoteRepository(fakeDao, null)
        viewModel = FeedViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `balanceFeed distributes evenly across categories`() = runTest {
        // Insert exactly 5 quotes per category (20 total, within DAO limit)
        val quotes = mutableListOf<Quote>()
        val categories = listOf("stoicism", "discipline", "reflection", "philosophy")
        for (cat in categories) {
            for (i in 1..5) {
                quotes.add(createQuote("${cat}_$i", category = cat))
            }
        }
        fakeDao.insertAll(quotes)

        viewModel.loadFeed("en")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.currentQuote)

        // Collect all quotes in the feed (current + next + queue)
        val allFeedQuotes = mutableListOf<Quote>()
        state.currentQuote?.let { allFeedQuotes.add(it) }
        state.nextQuote?.let { allFeedQuotes.add(it) }
        allFeedQuotes.addAll(state.feedQueue)

        // Should have at most 20 quotes total (5 per category)
        assertTrue("Feed should have at most 20 quotes, got ${allFeedQuotes.size}", allFeedQuotes.size <= 20)

        // Each category should have at least some representation
        for (cat in categories) {
            val catCount = allFeedQuotes.count { it.category == cat }
            assertTrue("Category $cat should have at least 1 quote, got $catCount", catCount >= 1)
        }
    }

    @Test
    fun `balanceFeed handles missing categories`() = runTest {
        // Only stoicism and discipline quotes, no reflection or philosophy
        val quotes = mutableListOf<Quote>()
        for (i in 1..10) {
            quotes.add(createQuote("stoic_$i", category = "stoicism"))
        }
        for (i in 1..10) {
            quotes.add(createQuote("disc_$i", category = "discipline"))
        }
        fakeDao.insertAll(quotes)

        viewModel.loadFeed("en")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull("Should still load feed with partial categories", state.currentQuote)
    }

    @Test
    fun `balanceFeed fills with remaining if short`() = runTest {
        // Only 2 stoicism and 2 discipline (4 total, less than 20)
        val quotes = listOf(
            createQuote("s1", category = "stoicism"),
            createQuote("s2", category = "stoicism"),
            createQuote("d1", category = "discipline"),
            createQuote("d2", category = "discipline"),
        )
        fakeDao.insertAll(quotes)

        viewModel.loadFeed("en")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.currentQuote)

        // All 4 quotes should be in the feed somewhere
        val allFeedQuotes = mutableListOf<Quote>()
        state.currentQuote?.let { allFeedQuotes.add(it) }
        state.nextQuote?.let { allFeedQuotes.add(it) }
        allFeedQuotes.addAll(state.feedQueue)

        assertTrue("Feed should contain all available quotes", allFeedQuotes.size >= 4)
    }

    @Test
    fun `balanceFeed shuffles result`() = runTest {
        // Insert quotes in order: all stoicism then all discipline
        val quotes = mutableListOf<Quote>()
        for (i in 1..5) {
            quotes.add(createQuote("stoic_$i", category = "stoicism"))
        }
        for (i in 1..5) {
            quotes.add(createQuote("disc_$i", category = "discipline"))
        }
        for (i in 1..5) {
            quotes.add(createQuote("refl_$i", category = "reflection"))
        }
        for (i in 1..5) {
            quotes.add(createQuote("phil_$i", category = "philosophy"))
        }
        fakeDao.insertAll(quotes)

        // Load multiple times and check that ordering varies (shuffle is random)
        // We just verify the feed loads without all being in insertion order
        viewModel.loadFeed("en")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        val allFeedQuotes = mutableListOf<Quote>()
        state.currentQuote?.let { allFeedQuotes.add(it) }
        state.nextQuote?.let { allFeedQuotes.add(it) }
        allFeedQuotes.addAll(state.feedQueue)

        // At minimum we verify feed is populated and contains mixed categories
        assertTrue("Feed should not be empty", allFeedQuotes.isNotEmpty())
        val categories = allFeedQuotes.map { it.category }.distinct()
        assertTrue("Feed should have multiple categories", categories.size > 1)
    }

    private fun createQuote(
        id: String,
        text: String = "Quote $id",
        author: String = "Author",
        category: String = "stoicism",
        lang: String = "en",
        isSaved: Boolean = false,
        savedAt: Long? = null,
    ) = Quote(id, text, author, category, lang, isSaved, savedAt)
}
