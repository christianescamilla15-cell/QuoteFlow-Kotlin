package com.christianhernandez.quoteflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.christianhernandez.quoteflow.data.model.Quote
import com.christianhernandez.quoteflow.ui.theme.DisciplineColor
import com.christianhernandez.quoteflow.ui.theme.PhilosophyColor
import com.christianhernandez.quoteflow.ui.theme.ReflectionColor
import com.christianhernandez.quoteflow.ui.theme.SaveGreen
import com.christianhernandez.quoteflow.ui.theme.StoicismColor
import com.christianhernandez.quoteflow.util.AuthorPortraits
import com.christianhernandez.quoteflow.util.AuthorTranslations
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
    language: String = "en",
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
        absX > absY && offsetX > 0 -> SaveGreen
        absX > absY && offsetX < 0 -> ReflectionColor
        absY > absX && offsetY < 0 -> StoicismColor
        absY > absX && offsetY > 0 -> PhilosophyColor
        else -> SaveGreen
    }
    val maxOffset = maxOf(absX, absY)
    val swipeIndicatorAlpha = (maxOffset / 300f).coerceIn(0f, 0.3f)

    // Author portrait URL and translated name
    val portraitUrl = AuthorPortraits.getPortraitUrl(quote.author)
    val displayAuthor = AuthorTranslations.translate(quote.author, language)
    val authorInitial = if (displayAuthor.isNotEmpty()) displayAuthor[0].uppercase() else "?"

    // Card background gradient: subtle category color tint
    val cardBackground = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surface,
            categoryColor.copy(alpha = 0.05f),
            MaterialTheme.colorScheme.surface,
        )
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        ),
    ) {
        Box(
            modifier = Modifier.background(cardBackground),
        ) {
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
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Large author photo
                if (portraitUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(portraitUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = displayAuthor,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(3.dp, categoryColor, RoundedCornerShape(16.dp))
                            .then(
                                if (onAuthorClick != null) {
                                    Modifier.clickable(onClick = onAuthorClick)
                                } else {
                                    Modifier
                                }
                            ),
                    )
                } else {
                    // Fallback: initial letter in colored rounded rectangle
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(categoryColor.copy(alpha = 0.12f))
                            .border(3.dp, categoryColor, RoundedCornerShape(16.dp))
                            .then(
                                if (onAuthorClick != null) {
                                    Modifier.clickable(onClick = onAuthorClick)
                                } else {
                                    Modifier
                                }
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = authorInitial,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = categoryColor,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Author name (bold)
                Text(
                    text = displayAuthor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.then(
                        if (onAuthorClick != null) {
                            Modifier.clickable(onClick = onAuthorClick)
                        } else {
                            Modifier
                        }
                    ),
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Category badge (small pill)
                Box(
                    modifier = Modifier
                        .background(
                            categoryColor.copy(alpha = 0.15f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = quote.category.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall,
                        color = categoryColor,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Opening decorative quote mark
                Text(
                    text = "\u275D",
                    fontSize = 60.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                    lineHeight = 60.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(1f),
                    textAlign = TextAlign.Start,
                )

                // Quote text — serif style, centered
                Text(
                    text = quote.text,
                    fontFamily = FontFamily.Serif,
                    fontSize = 18.sp,
                    lineHeight = (18 * 1.6).sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )

                // Closing decorative quote mark
                Text(
                    text = "\u275E",
                    fontSize = 60.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                    lineHeight = 60.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(1f),
                    textAlign = TextAlign.End,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons row: like, save, share
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    // Like (heart) button — decorative for now
                    IconButton(onClick = { /* future like action */ }) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp),
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Save (bookmark) button
                    IconButton(onClick = onSaveClick) {
                        Icon(
                            imageVector = if (quote.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (quote.isSaved) "Unsave" else "Save",
                            tint = if (quote.isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp),
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Share button
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
