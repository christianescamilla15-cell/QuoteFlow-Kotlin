package com.christianhernandez.quoteflow.ui.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.SwipeRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.christianhernandez.quoteflow.data.remote.MapScores
import com.christianhernandez.quoteflow.ui.theme.DisciplineColor
import com.christianhernandez.quoteflow.ui.theme.PhilosophyColor
import com.christianhernandez.quoteflow.ui.theme.ReflectionColor
import com.christianhernandez.quoteflow.ui.theme.StoicismColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    swipeCount: Int,
    language: String,
    onLanguageChange: (String) -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onLogout: () -> Unit = {},
    onNavigateToReflection: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(swipeCount) {
        viewModel.updateSwipeCount(swipeCount)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        TopAppBar(
            title = {
                Text(
                    text = if (language == "es") "Perfil" else "Profile",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
        )

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
        ) {
            // Stats section
            Text(
                text = if (language == "es") "Estadisticas" else "Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatCard(
                    icon = Icons.Default.SwipeRight,
                    label = if (language == "es") "Deslizadas" else "Swipes",
                    value = uiState.totalSwipes.toString(),
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    icon = Icons.Default.Bookmark,
                    label = if (language == "es") "Guardadas" else "Saved",
                    value = uiState.savedCount.toString(),
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    icon = Icons.Default.FormatQuote,
                    label = if (language == "es") "Total" else "Total",
                    value = uiState.totalQuotes.toString(),
                    modifier = Modifier.weight(1f),
                )
            }

            // Philosophy Map section with animated radar chart
            // Always show if we have map scores (local or API)
            if (uiState.mapScores != null) {
                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = if (language == "es") "Mapa Filosofico" else "Philosophy Map",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp),
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // Animated radar chart
                        val scores = uiState.mapScores!!
                        val delta = uiState.mapDelta
                        AnimatedPhilosophyRadarChart(scores = scores)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Score rows with delta arrows
                        ScoreRowWithDelta(
                            label = if (language == "es") "Sabiduria" else "Wisdom",
                            score = scores.wisdom ?: 0,
                            delta = delta?.wisdom,
                            color = StoicismColor,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        ScoreRowWithDelta(
                            label = if (language == "es") "Disciplina" else "Discipline",
                            score = scores.discipline ?: 0,
                            delta = delta?.discipline,
                            color = DisciplineColor,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        ScoreRowWithDelta(
                            label = if (language == "es") "Reflexion" else "Reflection",
                            score = scores.reflection ?: 0,
                            delta = delta?.reflection,
                            color = ReflectionColor,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        ScoreRowWithDelta(
                            label = if (language == "es") "Filosofia" else "Philosophy",
                            score = scores.philosophy ?: 0,
                            delta = delta?.philosophy,
                            color = PhilosophyColor,
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Save snapshot button
                        OutlinedButton(
                            onClick = { viewModel.saveSnapshot() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !uiState.isSavingSnapshot,
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (uiState.snapshotSaved) {
                                    if (language == "es") "Snapshot guardado!" else "Snapshot saved!"
                                } else {
                                    if (language == "es") "Guardar snapshot" else "Save snapshot"
                                }
                            )
                        }
                    }
                }
            }

            // Weekly Reflection card
            Spacer(modifier = Modifier.height(28.dp))

            Card(
                onClick = onNavigateToReflection,
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (language == "es") "Reflexion Semanal" else "Weekly Reflection",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = if (language == "es") {
                                "Descubre tu reflexion personalizada basada en tu actividad"
                            } else {
                                "Discover your personalized reflection based on your activity"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Settings section
            Text(
                text = if (language == "es") "Configuracion" else "Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Language picker
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (language == "es") "Idioma" else "Language",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = language == "en",
                                onClick = {
                                    onLanguageChange("en")
                                    viewModel.updateProfile(preferredLanguage = "en")
                                },
                                label = { Text("EN") },
                            )
                            FilterChip(
                                selected = language == "es",
                                onClick = {
                                    onLanguageChange("es")
                                    viewModel.updateProfile(preferredLanguage = "es")
                                },
                                label = { Text("ES") },
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    // Theme toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (language == "es") "Modo oscuro" else "Dark Mode",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                        )
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = onToggleDarkMode,
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    // Clear cache
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (language == "es") "Limpiar cache" else "Clear Cache",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                        )
                        OutlinedButton(
                            onClick = { viewModel.clearCache() },
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text(
                                text = if (uiState.cacheCleared) {
                                    if (language == "es") "Limpiado!" else "Cleared!"
                                } else {
                                    if (language == "es") "Limpiar" else "Clear"
                                },
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    // Logout / Reset device ID
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.error,
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (language == "es") "Cerrar sesion" else "Logout",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                        )
                        OutlinedButton(
                            onClick = onLogout,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error,
                            ),
                        ) {
                            Text(
                                text = if (language == "es") "Salir" else "Logout",
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // About section
            Text(
                text = if (language == "es") "Acerca de" else "About",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "QuoteFlow v1.0",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (language == "es") {
                                "Una app minimalista de frases inspiradoras con tarjetas deslizables, favoritos y retos diarios. Construida con Kotlin + Jetpack Compose."
                            } else {
                                "A minimalist inspirational quote app with swipeable cards, favorites, and daily challenges. Built with Kotlin + Jetpack Compose."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Christian Hernandez",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "github.com/christianhernandez",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

/**
 * Animated radar chart that smoothly transitions when scores change.
 * Uses animateFloatAsState for each axis.
 */
@Composable
private fun AnimatedPhilosophyRadarChart(scores: MapScores) {
    val wisdomColor = StoicismColor
    val disciplineColor = DisciplineColor
    val reflectionColor = ReflectionColor
    val philosophyColor = PhilosophyColor

    val wisdom by animateFloatAsState(
        targetValue = (scores.wisdom ?: 0).coerceIn(0, 100) / 100f,
        animationSpec = tween(durationMillis = 600),
        label = "wisdom_anim",
    )
    val discipline by animateFloatAsState(
        targetValue = (scores.discipline ?: 0).coerceIn(0, 100) / 100f,
        animationSpec = tween(durationMillis = 600),
        label = "discipline_anim",
    )
    val reflection by animateFloatAsState(
        targetValue = (scores.reflection ?: 0).coerceIn(0, 100) / 100f,
        animationSpec = tween(durationMillis = 600),
        label = "reflection_anim",
    )
    val philosophy by animateFloatAsState(
        targetValue = (scores.philosophy ?: 0).coerceIn(0, 100) / 100f,
        animationSpec = tween(durationMillis = 600),
        label = "philosophy_anim",
    )

    val gridColor = Color.Gray.copy(alpha = 0.2f)
    val fillColor = StoicismColor.copy(alpha = 0.15f)
    val strokeColor = StoicismColor.copy(alpha = 0.7f)

    Canvas(modifier = Modifier.size(200.dp)) {
        val cx = size.width / 2
        val cy = size.height / 2
        val maxR = size.width / 2 - 16f

        // Draw grid lines (25%, 50%, 75%, 100%)
        for (scale in listOf(0.25f, 0.5f, 0.75f, 1.0f)) {
            val r = maxR * scale
            val path = Path().apply {
                moveTo(cx, cy - r)          // top (wisdom)
                lineTo(cx + r, cy)          // right (discipline)
                lineTo(cx, cy + r)          // bottom (philosophy)
                lineTo(cx - r, cy)          // left (reflection)
                close()
            }
            drawPath(path, gridColor, style = Stroke(width = 1f))
        }

        // Draw axes
        drawLine(gridColor, Offset(cx, cy - maxR), Offset(cx, cy + maxR), strokeWidth = 1f)
        drawLine(gridColor, Offset(cx - maxR, cy), Offset(cx + maxR, cy), strokeWidth = 1f)

        // Draw data diamond (animated)
        val topY = cy - maxR * wisdom       // wisdom (up)
        val rightX = cx + maxR * discipline  // discipline (right)
        val bottomY = cy + maxR * philosophy // philosophy (down)
        val leftX = cx - maxR * reflection   // reflection (left)

        val dataPath = Path().apply {
            moveTo(cx, topY)
            lineTo(rightX, cy)
            lineTo(cx, bottomY)
            lineTo(leftX, cy)
            close()
        }
        drawPath(dataPath, fillColor)
        drawPath(dataPath, strokeColor, style = Stroke(width = 3f))

        // Draw corner dots
        val dotRadius = 5f
        drawCircle(wisdomColor, dotRadius, Offset(cx, topY))
        drawCircle(disciplineColor, dotRadius, Offset(rightX, cy))
        drawCircle(philosophyColor, dotRadius, Offset(cx, bottomY))
        drawCircle(reflectionColor, dotRadius, Offset(leftX, cy))
    }
}

@Composable
private fun ScoreRowWithDelta(
    label: String,
    score: Int,
    delta: Int?,
    color: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$score",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = color,
            )
            if (delta != null && delta != 0) {
                Spacer(modifier = Modifier.width(4.dp))
                val arrow = if (delta > 0) "+" else ""
                val deltaColor = if (delta > 0) Color(0xFF4CAF50) else Color(0xFFEF5350)
                Text(
                    text = "$arrow$delta",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = deltaColor,
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
