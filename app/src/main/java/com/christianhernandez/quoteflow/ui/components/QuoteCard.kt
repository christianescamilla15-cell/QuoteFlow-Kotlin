package com.christianhernandez.quoteflow.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.christianhernandez.quoteflow.data.model.Quote
import com.christianhernandez.quoteflow.ui.theme.DisciplineColor
import com.christianhernandez.quoteflow.ui.theme.PhilosophyColor
import com.christianhernandez.quoteflow.ui.theme.ReflectionColor
import com.christianhernandez.quoteflow.ui.theme.SaveGreen
import com.christianhernandez.quoteflow.ui.theme.StoicismColor
import com.christianhernandez.quoteflow.util.AuthorPortraits
import com.christianhernandez.quoteflow.util.QuoteCategory
import kotlin.math.abs

@Composable
fun QuoteCard(
    quote: Quote,
    offsetX: Float = 0f,
    offsetY: Float = 0f,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit = {},
    onAuthorClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val categoryColor = when (QuoteCategory.fromString(quote.category)) {
        QuoteCategory.STOICISM -> StoicismColor
        QuoteCategory.PHILOSOPHY -> PhilosophyColor
        QuoteCategory.DISCIPLINE -> DisciplineColor
        QuoteCategory.REFLECTION -> ReflectionColor
    }

    // Determine swipe overlay color based on dominant direction
    val absX = abs(offsetX)
    val absY = abs(offsetY)
    val swipeColor = when {
        absX > absY && offsetX > 0 -> SaveGreen // right = save/discipline
        absX > absY && offsetX < 0 -> ReflectionColor // left = reflection
        absY > absX && offsetY < 0 -> StoicismColor // up = stoicism
        absY > absX && offsetY > 0 -> PhilosophyColor // down = philosophy
        else -> SaveGreen
    }
    val maxOffset = maxOf(absX, absY)
    val swipeIndicatorAlpha = (maxOffset / 300f).coerceIn(0f, 0.3f)

    // Shimmer animation for card border on first appearance
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer_offset",
    )

    // Author portrait URL
    val portraitUrl = AuthorPortraits.getPortraitUrl(quote.author)
    val authorInitial = if (quote.author.isNotEmpty()) quote.author[0].uppercase() else "?"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .drawBehind {
                // Subtle gradient shimmer on the border
                val shimmerWidth = size.width * 0.4f
                val shimmerStart = size.width * shimmerOffset
                val shimmerEnd = shimmerStart + shimmerWidth
                val brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        categoryColor.copy(alpha = 0.15f),
                        categoryColor.copy(alpha = 0.3f),
                        categoryColor.copy(alpha = 0.15f),
                        Color.Transparent,
                    ),
                    start = Offset(shimmerStart, 0f),
                    end = Offset(shimmerEnd, size.height),
                )
                drawRoundRect(
                    brush = brush,
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx()),
                )
            },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Box {
            // Swipe color overlay
            if (maxOffset > 20f) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .alpha(swipeIndicatorAlpha)
                        .background(swipeColor, RoundedCornerShape(24.dp))
                )
            }

            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Category badge
                Box(
                    modifier = Modifier
                        .background(
                            categoryColor.copy(alpha = 0.15f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = quote.category.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall,
                        color = categoryColor,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Quote icon
                Icon(
                    imageVector = Icons.Default.FormatQuote,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .alpha(0.3f),
                    tint = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quote text
                Text(
                    text = quote.text,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Author row with photo
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .then(
                            if (onAuthorClick != null) {
                                Modifier.clickable(onClick = onAuthorClick)
                            } else {
                                Modifier
                            }
                        ),
                ) {
                    // Author photo (circular, 36dp)
                    if (portraitUrl != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(portraitUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = quote.author,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape),
                        )
                    } else {
                        // Fallback: initial letter in colored circle
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    categoryColor.copy(alpha = 0.12f),
                                    CircleShape,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = authorInitial,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = categoryColor,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = quote.author,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons: Save + Share
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    IconButton(onClick = onSaveClick) {
                        Icon(
                            imageVector = if (quote.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (quote.isSaved) "Unsave" else "Save",
                            tint = if (quote.isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = onShareClick) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
            }
        }
    }
}
