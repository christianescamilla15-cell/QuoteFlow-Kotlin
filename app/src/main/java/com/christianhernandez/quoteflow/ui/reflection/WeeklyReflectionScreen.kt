package com.christianhernandez.quoteflow.ui.reflection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.christianhernandez.quoteflow.data.repository.PhilosophyRepository
import com.christianhernandez.quoteflow.ui.theme.DisciplineColor
import com.christianhernandez.quoteflow.ui.theme.PhilosophyColor
import com.christianhernandez.quoteflow.ui.theme.ReflectionColor
import com.christianhernandez.quoteflow.ui.theme.StoicismColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyReflectionScreen(
    viewModel: WeeklyReflectionViewModel,
    language: String,
    onBack: () -> Unit,
    onShare: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(language) {
        viewModel.loadReflection(language)
    }

    // Trigger fade-in after loading completes
    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            isVisible = true
        }
    }

    val categoryColor = when (uiState.dominantCategory) {
        "stoicism" -> StoicismColor
        "discipline" -> DisciplineColor
        "reflection" -> ReflectionColor
        "philosophy" -> PhilosophyColor
        else -> StoicismColor
    }

    val categoryLabel = when (uiState.dominantCategory) {
        "stoicism" -> if (language == "es") "Sabiduria" else "Wisdom"
        "discipline" -> if (language == "es") "Disciplina" else "Discipline"
        "reflection" -> if (language == "es") "Reflexion" else "Reflection"
        "philosophy" -> if (language == "es") "Filosofia" else "Philosophy"
        else -> if (language == "es") "Sabiduria" else "Wisdom"
    }

    val gradientColors = listOf(
        categoryColor.copy(alpha = 0.08f),
        MaterialTheme.colorScheme.background,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors)),
    ) {
        TopAppBar(
            title = {
                Text(
                    text = if (language == "es") "Reflexion Semanal" else "Weekly Reflection",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = if (language == "es") "Volver" else "Back",
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
            ),
        )

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(800)),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Date range
                    Text(
                        text = uiState.dateRange,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dominant category badge
                    Box(
                        modifier = Modifier
                            .background(
                                categoryColor.copy(alpha = 0.15f),
                                RoundedCornerShape(16.dp),
                            )
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = if (language == "es") {
                                "Tu orientacion esta semana: $categoryLabel"
                            } else {
                                "Your orientation this week: $categoryLabel"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = categoryColor,
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Main reflection card
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            // Decorative quote mark
                            Text(
                                text = "\u201C",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = categoryColor.copy(alpha = 0.4f),
                                lineHeight = 48.sp,
                            )

                            // Reflection text in elegant style
                            Text(
                                text = uiState.reflectionText,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    lineHeight = 28.sp,
                                ),
                                fontStyle = FontStyle.Italic,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp),
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "\u201D",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = categoryColor.copy(alpha = 0.4f),
                                lineHeight = 48.sp,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Mini Philosophy Map preview
                    Text(
                        text = if (language == "es") "Tu Mapa Filosofico" else "Your Philosophy Map",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    MiniRadarChart(
                        scores = PhilosophyRepository.getScores(),
                        modifier = Modifier.size(140.dp),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Score labels
                    val scores = PhilosophyRepository.getScores()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        MiniScoreLabel(
                            label = if (language == "es") "Sab." else "Wis.",
                            value = scores.wisdom ?: 0,
                            color = StoicismColor,
                        )
                        MiniScoreLabel(
                            label = if (language == "es") "Disc." else "Disc.",
                            value = scores.discipline ?: 0,
                            color = DisciplineColor,
                        )
                        MiniScoreLabel(
                            label = if (language == "es") "Refl." else "Refl.",
                            value = scores.reflection ?: 0,
                            color = ReflectionColor,
                        )
                        MiniScoreLabel(
                            label = if (language == "es") "Fil." else "Phil.",
                            value = scores.philosophy ?: 0,
                            color = PhilosophyColor,
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Share button
                    Button(
                        onClick = { onShare(uiState.reflectionText) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = categoryColor,
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == "es") "Compartir reflexion" else "Share reflection",
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@Composable
private fun MiniScoreLabel(
    label: String,
    value: Int,
    color: Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$value",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun MiniRadarChart(
    scores: com.christianhernandez.quoteflow.data.remote.MapScores,
    modifier: Modifier = Modifier,
) {
    val wisdom by animateFloatAsState(
        targetValue = (scores.wisdom ?: 0).coerceIn(0, 100) / 100f,
        animationSpec = tween(600),
        label = "wisdom",
    )
    val discipline by animateFloatAsState(
        targetValue = (scores.discipline ?: 0).coerceIn(0, 100) / 100f,
        animationSpec = tween(600),
        label = "discipline",
    )
    val reflection by animateFloatAsState(
        targetValue = (scores.reflection ?: 0).coerceIn(0, 100) / 100f,
        animationSpec = tween(600),
        label = "reflection",
    )
    val philosophy by animateFloatAsState(
        targetValue = (scores.philosophy ?: 0).coerceIn(0, 100) / 100f,
        animationSpec = tween(600),
        label = "philosophy",
    )

    val gridColor = Color.Gray.copy(alpha = 0.2f)
    val fillColor = StoicismColor.copy(alpha = 0.15f)
    val strokeColor = StoicismColor.copy(alpha = 0.7f)

    Canvas(modifier = modifier) {
        val cx = size.width / 2
        val cy = size.height / 2
        val maxR = size.width / 2 - 8f

        // Grid
        for (scale in listOf(0.5f, 1.0f)) {
            val r = maxR * scale
            val path = Path().apply {
                moveTo(cx, cy - r)
                lineTo(cx + r, cy)
                lineTo(cx, cy + r)
                lineTo(cx - r, cy)
                close()
            }
            drawPath(path, gridColor, style = Stroke(width = 1f))
        }

        // Data
        val dataPath = Path().apply {
            moveTo(cx, cy - maxR * wisdom)
            lineTo(cx + maxR * discipline, cy)
            lineTo(cx, cy + maxR * philosophy)
            lineTo(cx - maxR * reflection, cy)
            close()
        }
        drawPath(dataPath, fillColor)
        drawPath(dataPath, strokeColor, style = Stroke(width = 2f))

        // Dots
        drawCircle(StoicismColor, 4f, Offset(cx, cy - maxR * wisdom))
        drawCircle(DisciplineColor, 4f, Offset(cx + maxR * discipline, cy))
        drawCircle(PhilosophyColor, 4f, Offset(cx, cy + maxR * philosophy))
        drawCircle(ReflectionColor, 4f, Offset(cx - maxR * reflection, cy))
    }
}
