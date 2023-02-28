package org.evidyaloka.player.exoplayer.listener

import com.google.android.exoplayer2.ExoPlaybackException
import org.evidyaloka.player.exoplayer.ExoMediaPlayer

/**
 * A listener for core [ExoMediaPlayer] events
 */
interface ExoPlayerListener : OnSeekCompletionListener {
    fun onStateChanged(playWhenReady: Boolean, playbackState: Int)
    fun onError(exoMediaPlayer: ExoMediaPlayer?, e: ExoPlaybackException?)
    fun onVideoSizeChanged(
        width: Int,
        height: Int,
        unAppliedRotationDegrees: Int,
        pixelWidthHeightRatio: Float
    )
}