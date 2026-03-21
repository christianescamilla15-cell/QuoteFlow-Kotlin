package com.christianhernandez.quoteflow.data.model

data class Challenge(
    val id: String,
    val title: String,
    val titleEs: String,
    val description: String,
    val descriptionEs: String,
    val goal: Int,
    val progress: Int = 0,
    val isComplete: Boolean = false,
    val streak: Int = 0,
) {
    fun localizedTitle(lang: String): String = if (lang == "es") titleEs else title
    fun localizedDescription(lang: String): String = if (lang == "es") descriptionEs else description

    companion object {
        fun dailyChallenge(progress: Int, streak: Int, goal: Int = 8): Challenge {
            return Challenge(
                id = "daily_swipe",
                title = "Daily Reader",
                titleEs = "Lector Diario",
                description = "Swipe through $goal quotes today to complete the challenge.",
                descriptionEs = "Desliza $goal frases hoy para completar el reto.",
                goal = goal,
                progress = progress.coerceAtMost(goal),
                isComplete = progress >= goal,
                streak = streak,
            )
        }
    }
}
