package com.christianhernandez.quoteflow

import com.christianhernandez.quoteflow.data.repository.PhilosophyPoints
import org.junit.Assert.assertEquals
import org.junit.Test

class PhilosophyPointsTest {

    @Test
    fun `addPoints increases correct category`() {
        val points = PhilosophyPoints()
        val updated = points.addPoints("stoicism", 5)
        assertEquals(5, updated.stoicism)
        assertEquals(0, updated.discipline)
        assertEquals(0, updated.reflection)
        assertEquals(0, updated.philosophy)
    }

    @Test
    fun `dominant returns highest category`() {
        val points = PhilosophyPoints(stoicism = 2, discipline = 5, reflection = 1, philosophy = 3)
        assertEquals("discipline", points.dominant())
    }

    @Test
    fun `swipe adds 1 point`() {
        val points = PhilosophyPoints()
        val updated = points.addPoints("stoicism", 1)
        assertEquals(1, updated.stoicism)
    }

    @Test
    fun `like adds 2 points`() {
        val points = PhilosophyPoints()
        val updated = points.addPoints("reflection", 2)
        assertEquals(2, updated.reflection)
    }

    @Test
    fun `vault save adds 3 points`() {
        val points = PhilosophyPoints()
        val updated = points.addPoints("philosophy", 3)
        assertEquals(3, updated.philosophy)
    }

    @Test
    fun `share adds 4 points`() {
        val points = PhilosophyPoints()
        val updated = points.addPoints("discipline", 4)
        assertEquals(4, updated.discipline)
    }

    @Test
    fun `multiple actions accumulate correctly`() {
        var points = PhilosophyPoints()
        // Swipe stoicism (+1)
        points = points.addPoints("stoicism", 1)
        // Like stoicism (+2)
        points = points.addPoints("stoicism", 2)
        // Vault save discipline (+3)
        points = points.addPoints("discipline", 3)
        // Share reflection (+4)
        points = points.addPoints("reflection", 4)
        // Another swipe philosophy (+1)
        points = points.addPoints("philosophy", 1)

        assertEquals(3, points.stoicism)
        assertEquals(3, points.discipline)
        assertEquals(4, points.reflection)
        assertEquals(1, points.philosophy)
    }

    @Test
    fun `dominant with tie returns first category`() {
        // When stoicism and discipline are tied, stoicism comes first in the when check
        val points = PhilosophyPoints(stoicism = 5, discipline = 5, reflection = 0, philosophy = 0)
        assertEquals("stoicism", points.dominant())
    }

    @Test
    fun `addPoints with unknown category returns unchanged`() {
        val points = PhilosophyPoints(stoicism = 1)
        val updated = points.addPoints("unknown", 10)
        assertEquals(points, updated)
    }

    @Test
    fun `default PhilosophyPoints has all zeros`() {
        val points = PhilosophyPoints()
        assertEquals(0, points.stoicism)
        assertEquals(0, points.discipline)
        assertEquals(0, points.reflection)
        assertEquals(0, points.philosophy)
    }
}
