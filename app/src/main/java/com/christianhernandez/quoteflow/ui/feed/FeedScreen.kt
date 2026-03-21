package com.christianhernandez.quoteflow.ui.feed

import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwipeLeft
import androidx.compose.material.icons.filled.SwipeRight
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.christianhernandez.quoteflow.ui.components.QuoteCard
import com.christianhernandez.quoteflow.ui.components.SwipeableCard

private const val SOFT_PAYWALL_THRESHOLD = 20

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
    language: String,
    isPremium: Boolean = false,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(language) {
        viewModel.loadFeed(language)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar with swipe counter
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "QuoteFlow",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    // Swipe counter
                    if (uiState.swipeCount > 0) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                        ) {
                            Text(
                                text = "${uiState.swipeCount}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            },
            actions = {
                com.christianhernandez.quoteflow.ui.ambient.AmbientAudioButton()
                BadgedBox(
                    badge = {
                        if (uiState.savedCount > 0) {
                            Badge {
                                Text(text = uiState.savedCount.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Saved quotes",
                        modifier = Modifier.padding(end = 16.dp),
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
        )

        // Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            when {
                // Soft paywall check
                !isPremium && uiState.swipeCount >= SOFT_PAYWALL_THRESHOLD && uiState.showPaywall -> {
                    PaywallCard(
                        language = language,
                        onDismiss = { viewModel.dismissPaywall() },
                    )
                }

                // Cold start / connecting state
                uiState.isConnecting -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (language == "es") {
                                "Conectando al servidor..."
                            } else {
                                "Connecting to server..."
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (language == "es") {
                                "(primera conexion puede tardar hasta 30 segundos)"
                            } else {
                                "(first connection may take up to 30 seconds)"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp),
                        )
                    }
                }

                // Error state
                uiState.errorMessage != null && uiState.currentQuote == null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (language == "es") "Error de conexion" else "Connection error",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.retry() }) {
                            Text(if (language == "es") "Reintentar" else "Retry")
                        }
                    }
                }

                // Loading (non-connecting, e.g. refetch)
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }

                uiState.currentQuote == null -> {
                    Text(
                        text = if (language == "es") "No hay frases disponibles" else "No quotes available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                else -> {
                    // Next card preview (behind)
                    androidx.compose.animation.AnimatedVisibility(
                        visible = uiState.nextQuote != null,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        uiState.nextQuote?.let { nextQuote ->
                            QuoteCard(
                                quote = nextQuote,
                                onSaveClick = { },
                                onShareClick = { },
                                modifier = Modifier
                                    .alpha(0.5f)
                                    .padding(top = 16.dp),
                            )
                        }
                    }

                    // Current swipeable card
                    uiState.currentQuote?.let { quote ->
                        Box {
                            SwipeableCard(
                                onSwiped = { direction -> viewModel.onSwipe(direction) },
                                onDoubleTap = { viewModel.onDoubleTapLike() },
                            ) { offsetX, offsetY ->
                                QuoteCard(
                                    quote = quote,
                                    offsetX = offsetX,
                                    offsetY = offsetY,
                                    onSaveClick = { viewModel.onSave(quote) },
                                    onShareClick = {
                                        viewModel.onShare(quote)
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "\"${quote.text}\" -- ${quote.author}\n\nVia QuoteFlow"
                                            )
                                            type = "text/plain"
                                        }
                                        context.startActivity(
                                            Intent.createChooser(
                                                sendIntent,
                                                if (language == "es") "Compartir frase" else "Share quote"
                                            )
                                        )
                                    },
                                )
                            }
                            // Like animation on double-tap
                            androidx.compose.animation.AnimatedVisibility(
                                visible = uiState.showLikeAnimation,
                                enter = fadeIn(),
                                exit = fadeOut(),
                                modifier = Modifier.align(Alignment.Center),
                            ) {
                                Text(
                                    text = "❤️",
                                    style = MaterialTheme.typography.displayLarge,
                                    modifier = Modifier.alpha(0.9f),
                                )
                            }
                        }
                    }
                }
            }
        }

        // Swipe direction hints
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Left hint — Reflection
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "←", style = MaterialTheme.typography.labelMedium, color = Color(0xFFF97316).copy(alpha = 0.6f))
                Text(
                    text = if (language == "es") "Reflexión" else "Reflection",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFF97316).copy(alpha = 0.5f),
                )
            }
            // Center — Up (Stoicism) + Down (Philosophy)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "↑", style = MaterialTheme.typography.labelMedium, color = Color(0xFF3B82F6).copy(alpha = 0.6f))
                Text(
                    text = if (language == "es") "Sabiduría" else "Wisdom",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF3B82F6).copy(alpha = 0.5f),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (language == "es") "Desliza en 4 direcciones" else "Swipe in 4 directions",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (language == "es") "Filosofía" else "Philosophy",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF8B5CF6).copy(alpha = 0.5f),
                )
                Text(text = "↓", style = MaterialTheme.typography.labelMedium, color = Color(0xFF8B5CF6).copy(alpha = 0.6f))
            }
            // Right hint — Discipline
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "→", style = MaterialTheme.typography.labelMedium, color = Color(0xFF10B981).copy(alpha = 0.6f))
                Text(
                    text = if (language == "es") "Disciplina" else "Discipline",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF10B981).copy(alpha = 0.5f),
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun PaywallCard(
    language: String,
    onDismiss: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (language == "es") "Sigue explorando!" else "Keep exploring!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (language == "es") {
                    "Has deslizado 20 frases hoy. Desbloquea deslizadas ilimitadas con MindScrolling Inside."
                } else {
                    "You've swiped 20 quotes today. Unlock unlimited swipes with MindScrolling Inside."
                },
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "$4.99 / " + if (language == "es") "mes" else "month",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(if (language == "es") "Continuar gratis" else "Continue free")
            }
        }
    }
}
