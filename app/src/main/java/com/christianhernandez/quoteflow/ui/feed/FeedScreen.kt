package com.christianhernandez.quoteflow.ui.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.SwipeLeft
import androidx.compose.material.icons.filled.SwipeRight
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.christianhernandez.quoteflow.ui.components.QuoteCard
import com.christianhernandez.quoteflow.ui.components.SwipeableCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
    language: String,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(language) {
        viewModel.loadFeed(language)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar
        TopAppBar(
            title = {
                Text(
                    text = "QuoteFlow",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            },
            actions = {
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
                                modifier = Modifier
                                    .alpha(0.5f)
                                    .padding(top = 16.dp),
                            )
                        }
                    }

                    // Current swipeable card
                    uiState.currentQuote?.let { quote ->
                        SwipeableCard(
                            onSwiped = { direction -> viewModel.onSwipe(direction) },
                        ) { offsetX, _ ->
                            QuoteCard(
                                quote = quote,
                                offsetX = offsetX,
                                onSaveClick = { viewModel.onSave(quote) },
                            )
                        }
                    }
                }
            }
        }

        // Swipe hints
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.SwipeLeft,
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp)
                    .alpha(0.4f),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (language == "es") "Desliza para explorar" else "Swipe to explore",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.SwipeRight,
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp)
                    .alpha(0.4f),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}
