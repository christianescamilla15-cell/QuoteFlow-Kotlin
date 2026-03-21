package com.christianhernandez.quoteflow.ui.reflection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.christianhernandez.quoteflow.data.repository.PhilosophyPoints
import com.christianhernandez.quoteflow.data.repository.PhilosophyRepository
import com.christianhernandez.quoteflow.data.repository.QuoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReflectionUiState(
    val reflectionText: String = "",
    val dominantCategory: String = "stoicism",
    val dateRange: String = "",
    val isLoading: Boolean = true,
    val points: PhilosophyPoints = PhilosophyPoints(),
)

class WeeklyReflectionViewModel(
    private val repository: QuoteRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReflectionUiState())
    val uiState: StateFlow<ReflectionUiState> = _uiState.asStateFlow()

    init {
        observePoints()
    }

    private fun observePoints() {
        viewModelScope.launch {
            PhilosophyRepository.points.collect { points ->
                _uiState.update { it.copy(points = points) }
            }
        }
    }

    fun loadReflection(lang: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Try API first
            var loaded = false
            try {
                val apiReflection = repository.getWeeklyInsight(lang)
                if (apiReflection != null) {
                    _uiState.update {
                        it.copy(
                            reflectionText = apiReflection,
                            isLoading = false,
                            dominantCategory = PhilosophyRepository.points.value.dominant(),
                            dateRange = calculateDateRange(),
                        )
                    }
                    loaded = true
                }
            } catch (_: Exception) {
                // Fallback to local
            }

            if (!loaded) {
                // Generate local reflection from PhilosophyRepository scores
                val points = PhilosophyRepository.points.value
                val reflection = generateLocalReflection(points, lang)
                _uiState.update {
                    it.copy(
                        reflectionText = reflection,
                        isLoading = false,
                        dominantCategory = points.dominant(),
                        dateRange = calculateDateRange(),
                    )
                }
            }
        }
    }

    private fun calculateDateRange(): String {
        val cal = java.util.Calendar.getInstance()
        val endDay = cal.get(java.util.Calendar.DAY_OF_MONTH)
        val endMonth = cal.getDisplayName(java.util.Calendar.MONTH, java.util.Calendar.SHORT, java.util.Locale.getDefault()) ?: ""
        val endYear = cal.get(java.util.Calendar.YEAR)
        cal.add(java.util.Calendar.DAY_OF_YEAR, -6)
        val startDay = cal.get(java.util.Calendar.DAY_OF_MONTH)
        return "$startDay-$endDay $endMonth $endYear"
    }

    companion object {
        fun generateLocalReflection(points: PhilosophyPoints, lang: String): String {
            val dominant = points.dominant()
            val isES = lang == "es"

            val reflections = mapOf(
                "stoicism" to mapOf(
                    "es" to listOf(
                        "Tu camino esta semana se ha inclinado hacia la sabiduria estoica. Los estoicos nos ensenan que no podemos controlar lo que sucede, pero si como respondemos. Reflexiona: en que momento de esta semana elegiste tu respuesta en lugar de reaccionar?",
                        "Marco Aurelio escribio: 'La felicidad de tu vida depende de la calidad de tus pensamientos.' Tu inclinacion hacia el estoicismo sugiere que buscas esa claridad mental. Que pensamiento quieres cultivar esta semana?",
                        "Seneca decia que sufrimos mas en la imaginacion que en la realidad. Tu exploracion estoica de esta semana te invita a preguntarte: que temor estas magnificando innecesariamente?",
                    ),
                    "en" to listOf(
                        "Your path this week has leaned toward Stoic wisdom. The Stoics teach us that we cannot control what happens, but we can control how we respond. Reflect: at what moment this week did you choose your response instead of reacting?",
                        "Marcus Aurelius wrote: 'The happiness of your life depends upon the quality of your thoughts.' Your Stoic inclination suggests you seek that mental clarity. What thought do you want to cultivate this week?",
                        "Seneca said we suffer more in imagination than in reality. Your Stoic exploration this week invites you to ask: what fear are you unnecessarily magnifying?",
                    ),
                ),
                "discipline" to mapOf(
                    "es" to listOf(
                        "La disciplina ha sido tu brujula esta semana. No se trata de rigidez, sino de libertad -- la libertad de elegir lo que importa sobre lo que es facil. Que habito pequeno puedes fortalecer manana?",
                        "Aristoteles dijo: 'Somos lo que hacemos repetidamente.' Tu enfoque en la disciplina revela un deseo de construir algo duradero. Que accion repetida esta definiendo quien eres?",
                    ),
                    "en" to listOf(
                        "Discipline has been your compass this week. It's not about rigidity, but freedom -- the freedom to choose what matters over what is easy. What small habit can you strengthen tomorrow?",
                        "Aristotle said: 'We are what we repeatedly do.' Your focus on discipline reveals a desire to build something lasting. What repeated action is defining who you are?",
                    ),
                ),
                "reflection" to mapOf(
                    "es" to listOf(
                        "Has dedicado tu atencion a la reflexion esta semana. En un mundo de ruido constante, elegir la introspeccion es un acto de valentia. Que descubriste sobre ti mismo que no sabias el lunes?",
                        "Socrates enseno que una vida sin examinar no vale la pena vivir. Tu camino reflexivo sugiere que estas buscando algo mas profundo. Que pregunta te persigue?",
                    ),
                    "en" to listOf(
                        "You have dedicated your attention to reflection this week. In a world of constant noise, choosing introspection is an act of courage. What did you discover about yourself that you didn't know on Monday?",
                        "Socrates taught that the unexamined life is not worth living. Your reflective path suggests you are seeking something deeper. What question haunts you?",
                    ),
                ),
                "philosophy" to mapOf(
                    "es" to listOf(
                        "La filosofia existencial ha llamado tu atencion esta semana. Camus nos recuerda que debemos imaginar a Sisifo feliz -- encontrar significado incluso en la repeticion. Donde encuentras significado en tu rutina diaria?",
                        "Nietzsche escribio: 'Quien tiene un porque para vivir puede soportar casi cualquier como.' Tu exploracion filosofica de esta semana te pregunta: cual es tu porque?",
                    ),
                    "en" to listOf(
                        "Existential philosophy has caught your attention this week. Camus reminds us that we must imagine Sisyphus happy -- finding meaning even in repetition. Where do you find meaning in your daily routine?",
                        "Nietzsche wrote: 'He who has a why to live can bear almost any how.' Your philosophical exploration this week asks you: what is your why?",
                    ),
                ),
            )

            val langKey = if (isES) "es" else "en"
            val options = reflections[dominant]?.get(langKey) ?: reflections["stoicism"]!![langKey]!!
            return options.random()
        }
    }

    class Factory(private val repository: QuoteRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WeeklyReflectionViewModel::class.java)) {
                return WeeklyReflectionViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
