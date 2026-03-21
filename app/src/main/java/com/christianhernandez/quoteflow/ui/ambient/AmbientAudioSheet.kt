package com.christianhernandez.quoteflow.ui.ambient

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbientAudioButton(
    modifier: Modifier = Modifier
) {
    var showSheet by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(AmbientAudioService.isCurrentlyPlaying()) }
    var currentTrackId by remember { mutableStateOf(AmbientAudioService.getCurrentTrackId()) }

    IconButton(
        onClick = { showSheet = true },
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = "Ambient Music",
            tint = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
        ) {
            AmbientAudioContent(
                isPlaying = isPlaying,
                currentTrackId = currentTrackId,
                onTrackSelected = { track ->
                    currentTrackId = track.id
                    isPlaying = true
                },
                onStop = {
                    isPlaying = false
                    currentTrackId = null
                },
                onToggle = {
                    isPlaying = !isPlaying
                },
            )
        }
    }
}

@Composable
fun AmbientAudioContent(
    isPlaying: Boolean,
    currentTrackId: String?,
    onTrackSelected: (AmbientTrack) -> Unit,
    onStop: () -> Unit,
    onToggle: () -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .padding(bottom = 32.dp),
    ) {
        Text(
            text = "Ambient Music",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Text(
            text = "Relax while you read",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 20.dp),
        )

        // Now playing controls
        if (currentTrackId != null) {
            val currentTrack = AmbientAudioService.tracks.find { it.id == currentTrackId }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Now Playing",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        )
                        Text(
                            text = currentTrack?.name ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                    IconButton(onClick = {
                        AmbientAudioService.togglePlayPause()
                        onToggle()
                    }) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                    IconButton(onClick = {
                        AmbientAudioService.stop()
                        onStop()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }
        }

        // Track list
        AmbientAudioService.tracks.forEach { track ->
            val isCurrentTrack = track.id == currentTrackId
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        AmbientAudioService.play(context, track)
                        onTrackSelected(track)
                    }
                    .background(
                        if (isCurrentTrack) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        else MaterialTheme.colorScheme.surface
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCurrentTrack) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (isCurrentTrack && isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = if (isCurrentTrack) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = track.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isCurrentTrack) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isCurrentTrack) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
