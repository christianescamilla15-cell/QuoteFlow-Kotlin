package com.christianhernandez.quoteflow.util

object Constants {
    const val DATABASE_NAME = "quoteflow_db"
    const val SWIPE_THRESHOLD_DP = 100
    const val DAILY_CHALLENGE_GOAL = 8
    const val FEED_PAGE_SIZE = 20
    const val PREFS_NAME = "quoteflow_prefs"
    const val KEY_LANGUAGE = "language"
    const val KEY_DARK_MODE = "dark_mode"
    const val KEY_SWIPE_COUNT = "swipe_count"
    const val KEY_STREAK = "streak"
    const val KEY_LAST_CHALLENGE_DATE = "last_challenge_date"
    const val KEY_DAILY_SWIPES = "daily_swipes"
}

enum class SwipeDirection {
    LEFT, RIGHT, UP, DOWN
}

enum class QuoteCategory(val displayName: String, val displayNameEs: String) {
    STOICISM("Stoicism", "Estoicismo"),
    PHILOSOPHY("Philosophy", "Filosofia"),
    DISCIPLINE("Discipline", "Disciplina"),
    REFLECTION("Reflection", "Reflexion");

    companion object {
        fun fromString(value: String): QuoteCategory {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
                ?: REFLECTION
        }
    }
}
