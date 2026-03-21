package com.christianhernandez.quoteflow.ui.feed

import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
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
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.christianhernandez.quoteflow.ui.components.AuthorDetailSheet
import com.christianhernandez.quoteflow.ui.components.QuoteCard
import com.christianhernandez.quoteflow.ui.components.SwipeableCard
import com.christianhernandez.quoteflow.util.HapticService

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
    var currentDragX by remember { mutableFloatStateOf(0f) }
    var currentDragY by remember { mutableFloatStateOf(0f) }

    // Author detail bottom sheet state
    var showAuthorSheet by remember { mutableStateOf(false) }
    var selectedAuthorName by remember { mutableStateOf("") }

    // Track swipe count for haptic on swipe completed
    var lastSwipeCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(language) {
        viewModel.loadFeed(language)
    }

    // Haptic: swipe completed (mediumTap)
    LaunchedEffect(uiState.swipeCount) {
        if (uiState.swipeCount > lastSwipeCount) {
            HapticService.mediumTap(context)
        }
        lastSwipeCount = uiState.swipeCount
    }

    // Haptic: double-tap like (success)
    LaunchedEffect(uiState.showLikeAnimation) {
        if (uiState.showLikeAnimation) {
            HapticService.success(context)
        }
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
                    // Calculate drag progress for next card scale animation
                    val dragProgress = (maxOf(
                        kotlin.math.abs(currentDragX),
                        kotlin.math.abs(currentDragY)
                    ) / 300f).coerceIn(0f, 1f)
                    val nextCardScale by animateFloatAsState(
                        targetValue = 0.95f + (0.05f * dragProgress),
                        animationSpec = tween(100),
                        label = "next_card_scale",
                    )
                    val nextCardAlpha by animateFloatAsState(
                        targetValue = 0.5f + (0.5f * dragProgress),
                        animationSpec = tween(100),
                        label = "next_card_alpha",
                    )

                    // Next card preview (behind) with scale animation
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
                                    .scale(nextCardScale)
                                    .alpha(nextCardAlpha)
                                    .padding(top = 16.dp),
                            )
                        }
                    }

                    // Current swipeable card
                    uiState.currentQuote?.let { quote ->
                        Box(modifier = Modifier.fillMaxSize()) {
                            SwipeableCard(
                                onSwiped = { direction -> viewModel.onSwipe(direction) },
                                onDoubleTap = { viewModel.onDoubleTapLike() },
                                onDragUpdate = { dx, dy ->
                                    currentDragX = dx
                                    currentDragY = dy
                                },
                            ) { offsetX, offsetY ->
                                QuoteCard(
                                    quote = quote,
                                    offsetX = offsetX,
                                    offsetY = offsetY,
                                    onSaveClick = {
                                        viewModel.onSave(quote)
                                        HapticService.lightTap(context)
                                    },
                                    onAuthorClick = {
                                        selectedAuthorName = quote.author
                                        showAuthorSheet = true
                                    },
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

                            // Dynamic direction overlays based on drag
                            val threshold = 100f
                            val absX = kotlin.math.abs(currentDragX)
                            val absY = kotlin.math.abs(currentDragY)

                            // UP indicator — Wisdom / Sabiduria
                            if (currentDragY < -20f && absY > absX) {
                                val alpha = (kotlin.math.abs(currentDragY) / threshold).coerceIn(0f, 1f)
                                DirectionOverlay(
                                    text = if (language == "es") "Sabiduria" else "Wisdom",
                                    arrow = "\u2191",
                                    color = Color(0xFF3B82F6),
                                    alpha = alpha,
                                    alignment = Alignment.TopCenter,
                                )
                            }

                            // DOWN indicator — Philosophy / Filosofia
                            if (currentDragY > 20f && absY > absX) {
                                val alpha = (currentDragY / threshold).coerceIn(0f, 1f)
                                DirectionOverlay(
                                    text = if (language == "es") "Filosofia" else "Philosophy",
                                    arrow = "\u2193",
                                    color = Color(0xFF8B5CF6),
                                    alpha = alpha,
                                    alignment = Alignment.BottomCenter,
                                )
                            }

                            // RIGHT indicator — Discipline / Disciplina
                            if (currentDragX > 20f && absX > absY) {
                                val alpha = (currentDragX / threshold).coerceIn(0f, 1f)
                                DirectionOverlay(
                                    text = if (language == "es") "Disciplina" else "Discipline",
                                    arrow = "\u2192",
                                    color = Color(0xFF10B981),
                                    alpha = alpha,
                                    alignment = Alignment.CenterEnd,
                                )
                            }

                            // LEFT indicator — Reflection / Reflexion
                            if (currentDragX < -20f && absX > absY) {
                                val alpha = (kotlin.math.abs(currentDragX) / threshold).coerceIn(0f, 1f)
                                DirectionOverlay(
                                    text = if (language == "es") "Reflexion" else "Reflection",
                                    arrow = "\u2190",
                                    color = Color(0xFFF97316),
                                    alpha = alpha,
                                    alignment = Alignment.CenterStart,
                                )
                            }

                            // Like animation on double-tap
                            androidx.compose.animation.AnimatedVisibility(
                                visible = uiState.showLikeAnimation,
                                enter = fadeIn() + scaleIn(
                                    initialScale = 0.5f,
                                    animationSpec = tween(200),
                                ),
                                exit = fadeOut() + scaleOut(
                                    targetScale = 1.5f,
                                    animationSpec = tween(300),
                                ),
                                modifier = Modifier.align(Alignment.Center),
                            ) {
                                Text(
                                    text = "\u2764\uFE0F",
                                    style = MaterialTheme.typography.displayLarge,
                                    modifier = Modifier.alpha(0.9f),
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }

    // Author detail bottom sheet
    if (showAuthorSheet && selectedAuthorName.isNotEmpty()) {
        // Collect quotes by this author from the feed queue + current
        val authorQuotes = buildList {
            uiState.currentQuote?.let { if (it.author == selectedAuthorName) add(it) }
            uiState.nextQuote?.let { if (it.author == selectedAuthorName) add(it) }
            addAll(uiState.feedQueue.filter { it.author == selectedAuthorName })
        }.distinctBy { it.id }

        AuthorDetailSheet(
            authorName = selectedAuthorName,
            authorQuotes = authorQuotes,
            language = language,
            onDismiss = { showAuthorSheet = false },
        )
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

@Composable
private fun DirectionOverlay(
    text: String,
    arrow: String,
    color: Color,
    alpha: Float,
    alignment: Alignment,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = alignment,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alpha),
        ) {
            Text(
                text = arrow,
                fontSize = 48.sp,
                color = color,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = text,
                fontSize = 20.sp,
                color = color,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
