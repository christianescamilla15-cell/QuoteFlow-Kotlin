package com.christianhernandez.quoteflow.ui.ambient

import android.content.Context
import android.media.MediaPlayer
import com.christianhernandez.quoteflow.R

data class AmbientTrack(
    val id: String,
    val name: String,
    val resId: Int,
)

object AmbientAudioService {
    val tracks = listOf(
        AmbientTrack("clean_soul", "Clean Soul", R.raw.clean_soul),
        AmbientTrack("floating_cities", "Floating Cities", R.raw.floating_cities),
        AmbientTrack("long_note_four", "Long Note Four", R.raw.long_note_four),
        AmbientTrack("lost_frontier", "Lost Frontier", R.raw.lost_frontier),
        AmbientTrack("meditation_01", "Meditation Impromptu I", R.raw.meditation_impromptu_01),
        AmbientTrack("meditation_02", "Meditation Impromptu II", R.raw.meditation_impromptu_02),
    )

    private var mediaPlayer: MediaPlayer? = null
    private var currentTrackId: String? = null
    private var isPlaying: Boolean = false

    fun play(context: Context, track: AmbientTrack) {
        stop()
        mediaPlayer = MediaPlayer.create(context, track.resId).apply {
            isLooping = true
            setVolume(0.4f, 0.4f)
            start()
        }
        currentTrackId = track.id
        isPlaying = true
    }

    fun stop() {
        mediaPlayer?.apply {
            if (isPlaying()) stop()
            release()
        }
        mediaPlayer = null
        currentTrackId = null
        isPlaying = false
    }

    fun pause() {
        mediaPlayer?.pause()
        isPlaying = false
    }

    fun resume() {
        mediaPlayer?.start()
        isPlaying = true
    }

    fun togglePlayPause() {
        if (isPlaying) pause() else resume()
    }

    fun isCurrentlyPlaying(): Boolean = isPlaying
    fun getCurrentTrackId(): String? = currentTrackId

    fun setVolume(volume: Float) {
        val v = volume.coerceIn(0f, 1f)
        mediaPlayer?.setVolume(v, v)
    }
}
