package com.christianhernandez.quoteflow

import com.christianhernandez.quoteflow.data.model.Challenge
import com.christianhernandez.quoteflow.ui.challenge.ChallengeViewModel
import com.christianhernandez.quoteflow.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChallengeLogicTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- Challenge data class tests ---

    @Test
    fun `dailyChallenge with zero progress is not complete`() {
        val challenge = Challenge.dailyChallenge(progress = 0, streak = 0)
        assertFalse(challenge.isComplete)
        assertEquals(0, challenge.progress)
    }

    @Test
    fun `dailyChallenge with partial progress is not complete`() {
        val challenge = Challenge.dailyChallenge(progress = 5, streak = 2)
        assertFalse(challenge.isComplete)
        assertEquals(5, challenge.progress)
        assertEquals(2, challenge.streak)
    }

    @Test
    fun `dailyChallenge reaching goal is complete`() {
        val challenge = Challenge.dailyChallenge(progress = 8, streak = 3)
        assertTrue(challenge.isComplete)
        assertEquals(8, challenge.progress)
    }

    @Test
    fun `dailyChallenge exceeding goal caps progress at goal`() {
        val challenge = Challenge.dailyChallenge(progress = 12, streak = 1, goal = 8)
        assertTrue(challenge.isComplete)
        assertEquals(8, challenge.progress)
    }

    @Test
    fun `dailyChallenge default goal is DAILY_CHALLENGE_GOAL`() {
        val challenge = Challenge.dailyChallenge(progress = 0, streak = 0)
        assertEquals(Constants.DAILY_CHALLENGE_GOAL, challenge.goal)
    }

    @Test
    fun `dailyChallenge custom goal works`() {
        val challenge = Challenge.dailyChallenge(progress = 3, streak = 0, goal = 5)
        assertEquals(5, challenge.goal)
        assertFalse(challenge.isComplete)
    }

    @Test
    fun `localizedTitle returns English by default`() {
        val challenge = Challenge.dailyChallenge(0, 0)
        assertEquals("Daily Reader", challenge.localizedTitle("en"))
    }

    @Test
    fun `localizedTitle returns Spanish for es`() {
        val challenge = Challenge.dailyChallenge(0, 0)
        assertEquals("Lector Diario", challenge.localizedTitle("es"))
    }

    // --- ChallengeViewModel tests ---

    @Test
    fun `viewModel initial state has zero progress`() {
        val vm = ChallengeViewModel()
        val state = vm.uiState.value
        assertEquals(0, state.challenge.progress)
        assertFalse(state.challenge.isComplete)
        assertFalse(state.showCelebration)
    }

    @Test
    fun `updateProgress updates challenge progress`() {
        val vm = ChallengeViewModel()
        vm.updateProgress(5)
        assertEquals(5, vm.uiState.value.challenge.progress)
        assertFalse(vm.uiState.value.challenge.isComplete)
    }

    @Test
    fun `updateProgress reaching goal triggers celebration`() {
        val vm = ChallengeViewModel()
        vm.updateProgress(Constants.DAILY_CHALLENGE_GOAL)
        assertTrue(vm.uiState.value.challenge.isComplete)
        assertTrue(vm.uiState.value.showCelebration)
    }

    @Test
    fun `dismissCelebration hides celebration`() {
        val vm = ChallengeViewModel()
        vm.updateProgress(Constants.DAILY_CHALLENGE_GOAL)
        assertTrue(vm.uiState.value.showCelebration)

        vm.dismissCelebration()
        assertFalse(vm.uiState.value.showCelebration)
    }

    @Test
    fun `completing challenge increments streak`() {
        val vm = ChallengeViewModel()
        assertEquals(0, vm.getStreak())

        vm.updateProgress(Constants.DAILY_CHALLENGE_GOAL)
        assertEquals(1, vm.getStreak())
    }

    @Test
    fun `already complete challenge does not show celebration again`() {
        val vm = ChallengeViewModel()
        vm.updateProgress(Constants.DAILY_CHALLENGE_GOAL)
        assertTrue(vm.uiState.value.showCelebration)

        vm.dismissCelebration()
        // Update again with same or higher count
        vm.updateProgress(Constants.DAILY_CHALLENGE_GOAL + 2)
        assertFalse(vm.uiState.value.showCelebration)
    }

    @Test
    fun `isComplete reflects challenge state`() {
        val vm = ChallengeViewModel()
        assertFalse(vm.isComplete())

        vm.updateProgress(Constants.DAILY_CHALLENGE_GOAL)
        assertTrue(vm.isComplete())
    }

    @Test
    fun `getDailySwipes returns current swipe count`() {
        val vm = ChallengeViewModel()
        assertEquals(0, vm.getDailySwipes())

        vm.updateProgress(7)
        assertEquals(7, vm.getDailySwipes())
    }
}
