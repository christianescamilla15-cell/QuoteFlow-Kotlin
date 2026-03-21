package com.christianhernandez.quoteflow.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    language: String,
    onComplete: (String) -> Unit, // passes selected language
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isComplete) {
        onComplete(uiState.preferredLanguage ?: language)
        return
    }

    if (uiState.isSubmitting) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Progress dots
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 32.dp),
        ) {
            repeat(4) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == uiState.currentStep) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (index <= uiState.currentStep) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                )
            }
        }

        AnimatedContent(
            targetState = uiState.currentStep,
            transitionSpec = {
                (slideInHorizontally { it } + fadeIn()) togetherWith
                    (slideOutHorizontally { -it } + fadeOut())
            },
            label = "onboarding_step",
        ) { step ->
            when (step) {
                0 -> OnboardingStep(
                    title = if (language == "es") "Cual es tu rango de edad?" else "What is your age range?",
                    options = listOf(
                        "18-24" to "18-24",
                        "25-34" to "25-34",
                        "35-44" to "35-44",
                        "45+" to "45+",
                    ),
                    onSelect = { viewModel.selectAgeRange(it) },
                )
                1 -> OnboardingStep(
                    title = if (language == "es") "Que te interesa mas?" else "What interests you most?",
                    options = listOf(
                        (if (language == "es") "Filosofia" else "Philosophy") to "philosophy",
                        (if (language == "es") "Estoicismo" else "Stoicism") to "stoicism",
                        (if (language == "es") "Crecimiento personal" else "Personal Growth") to "personal_growth",
                        "Mindfulness" to "mindfulness",
                        (if (language == "es") "Curiosidad" else "Curiosity") to "curiosity",
                    ),
                    onSelect = { viewModel.selectInterest(it) },
                )
                2 -> OnboardingStep(
                    title = if (language == "es") "Cual es tu objetivo?" else "What is your goal?",
                    options = listOf(
                        (if (language == "es") "Mente tranquila" else "Calm Mind") to "calm_mind",
                        (if (language == "es") "Disciplina" else "Discipline") to "discipline",
                        (if (language == "es") "Proposito" else "Meaning") to "meaning",
                        (if (language == "es") "Claridad emocional" else "Emotional Clarity") to "emotional_clarity",
                    ),
                    onSelect = { viewModel.selectGoal(it) },
                )
                3 -> OnboardingStep(
                    title = if (language == "es") "Idioma preferido?" else "Preferred language?",
                    options = listOf(
                        "Espanol" to "es",
                        "English" to "en",
                    ),
                    onSelect = { viewModel.selectLanguage(it) },
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Back button (visible on steps > 0)
        if (uiState.currentStep > 0) {
            OutlinedButton(
                onClick = { viewModel.goBack() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(if (language == "es") "Atras" else "Back")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun OnboardingStep(
    title: String,
    options: List<Pair<String, String>>,
    onSelect: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 32.dp),
        )

        options.forEach { (label, value) ->
            Button(
                onClick = { onSelect(value) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}
