package com.christianhernandez.quoteflow

import com.christianhernandez.quoteflow.data.model.Quote
import com.christianhernandez.quoteflow.data.repository.QuoteRepository
import com.christianhernandez.quoteflow.ui.feed.FeedViewModel
import com.christianhernandez.quoteflow.util.SwipeDirection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeDao: FakeQuoteDao
    private lateinit var repository: QuoteRepository
    private lateinit var viewModel: FeedViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeDao = FakeQuoteDao()
        repository = QuoteRepository(fakeDao)
        viewModel = FeedViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() {
        val state = viewModel.uiState.value
        assertTrue(state.isLoading)
        assertNull(state.currentQuote)
        assertEquals(0, state.swipeCount)
    }

    @Test
    fun `loadFeed populates current and next quote`() = runTest {
        fakeDao.insertAll(
            listOf(
                createQuote("1"),
                createQuote("2"),
                createQuote("3"),
            )
        )

        viewModel.loadFeed("en")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.currentQuote)
        assertNotNull(state.nextQuote)
    }

    @Test
    fun `loadFeed with no quotes sets loading to false`() = runTest {
        viewModel.loadFeed("en")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.currentQuote)
    }

    @Test
    fun `onSwipe increments swipe count`() = runTest {
        fakeDao.insertAll(
            listOf(
                createQuote("1"),
                createQuote("2"),
                createQuote("3"),
            )
        )

        viewModel.loadFeed("en")
        advanceUntilIdle()

        viewModel.onSwipe(SwipeDirection.LEFT)
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.swipeCount)
    }

    @Test
    fun `onSwipe LEFT advances to next quote`() = runTest {
        fakeDao.insertAll(
            listOf(
                createQuote("1"),
                createQuote("2"),
                createQuote("3"),
            )
        )

        viewModel.loadFeed("en")
        advanceUntilIdle()

        val firstQuote = viewModel.uiState.value.currentQuote
        val secondQuote = viewModel.uiState.value.nextQuote

        viewModel.onSwipe(SwipeDirection.LEFT)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(secondQuote?.id, state.currentQuote?.id)
    }

    @Test
    fun `onSwipe RIGHT saves current quote`() = runTest {
        val quote = createQuote("1", isSaved = false)
        fakeDao.insertAll(listOf(quote, createQuote("2"), createQuote("3")))

        viewModel.loadFeed("en")
        advanceUntilIdle()

        viewModel.onSwipe(SwipeDirection.RIGHT)
        advanceUntilIdle()

        val savedQuote = fakeDao.getQuoteById("1")
        assertTrue(savedQuote?.isSaved == true)
    }

    @Test
    fun `onSave toggles save state`() = runTest {
        val quote = createQuote("1", isSaved = false)
        fakeDao.insertAll(listOf(quote, createQuote("2")))

        viewModel.loadFeed("en")
        advanceUntilIdle()

        val current = viewModel.uiState.value.currentQuote!!
        viewModel.onSave(current)
        advanceUntilIdle()

        val updated = fakeDao.getQuoteById(current.id)
        assertTrue(updated?.isSaved == true)
    }

    @Test
    fun `swipe count starts at zero`() {
        assertEquals(0, viewModel.uiState.value.swipeCount)
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
