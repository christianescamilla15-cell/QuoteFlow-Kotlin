package com.christianhernandez.quoteflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FormatQuote
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.christianhernandez.quoteflow.data.model.Quote
import com.christianhernandez.quoteflow.ui.theme.DisciplineColor
import com.christianhernandez.quoteflow.ui.theme.PhilosophyColor
import com.christianhernandez.quoteflow.ui.theme.ReflectionColor
import com.christianhernandez.quoteflow.ui.theme.SaveGreen
import com.christianhernandez.quoteflow.ui.theme.SkipRed
import com.christianhernandez.quoteflow.ui.theme.StoicismColor
import com.christianhernandez.quoteflow.util.QuoteCategory
import kotlin.math.abs

@Composable
fun QuoteCard(
    quote: Quote,
    offsetX: Float = 0f,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val categoryColor = when (QuoteCategory.fromString(quote.category)) {
        QuoteCategory.STOICISM -> StoicismColor
        QuoteCategory.PHILOSOPHY -> PhilosophyColor
        QuoteCategory.DISCIPLINE -> DisciplineColor
        QuoteCategory.REFLECTION -> ReflectionColor
    }

    // Swipe indicators: show green tint for right (save), red tint for left (skip)
    val swipeIndicatorAlpha = (abs(offsetX) / 300f).coerceIn(0f, 0.3f)
    val swipeColor = if (offsetX > 0) SaveGreen else SkipRed

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Box {
            // Swipe color overlay
            if (abs(offsetX) > 20f) {
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

                // Author
                Text(
                    text = "-- ${quote.author}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Save button
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
                }
            }
        }
    }
}
