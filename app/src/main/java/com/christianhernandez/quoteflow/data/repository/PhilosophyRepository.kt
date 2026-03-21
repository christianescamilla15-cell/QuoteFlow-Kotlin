package com.christianhernandez.quoteflow.data.repository

import com.christianhernandez.quoteflow.data.remote.MapScores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Philosophy Map points per action:
 * Swipe: 1 point, Like (double-tap): 2 points, Vault save: 3 points, Share: 4 points
 */
data class PhilosophyPoints(
    val stoicism: Int = 0,
    val discipline: Int = 0,
    val reflection: Int = 0,
    val philosophy: Int = 0,
) {
    fun addPoints(category: String, points: Int): PhilosophyPoints {
        return when (category) {
            "stoicism" -> copy(stoicism = stoicism + points)
            "discipline" -> copy(discipline = discipline + points)
            "reflection" -> copy(reflection = reflection + points)
            "philosophy" -> copy(philosophy = philosophy + points)
            else -> this
        }
    }

    fun dominant(): String {
        val max = maxOf(stoicism, discipline, reflection, philosophy)
        return when (max) {
            stoicism -> "stoicism"
            discipline -> "discipline"
            reflection -> "reflection"
            else -> "philosophy"
        }
    }

    fun total(): Int = stoicism + discipline + reflection + philosophy
}

/**
 * Shared repository for Philosophy Map scores.
 * Both FeedViewModel (writes) and ProfileViewModel (reads) access this singleton.
 */
object PhilosophyRepository {
    private val _points = MutableStateFlow(PhilosophyPoints())
    val points: StateFlow<PhilosophyPoints> = _points.asStateFlow()

    fun addPoints(category: String, amount: Int) {
        _points.update { it.addPoints(category, amount) }
    }

    fun getScores(): MapScores {
        val p = _points.value
        val max = maxOf(p.stoicism, p.discipline, p.reflection, p.philosophy, 1)
        return MapScores(
            wisdom = (p.stoicism * 100) / max,
            discipline = (p.discipline * 100) / max,
            reflection = (p.reflection * 100) / max,
            philosophy = (p.philosophy * 100) / max,
        )
    }

    /** Reset for testing purposes */
    fun reset() {
        _points.value = PhilosophyPoints()
    }
}
