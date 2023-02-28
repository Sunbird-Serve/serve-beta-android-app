package org.evidyaloka.player.exoplayer

import android.net.Uri
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import android.view.View.OnTouchListener
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.drm.MediaDrmCallback
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import org.evidyaloka.player.exoplayer.ExoMedia.RendererType
import org.evidyaloka.player.exoplayer.listener.CaptionListener
import org.evidyaloka.player.exoplayer.listener.ExoPlayerListener

/**
 * The basic APIs expected in the backing video view
 * implementations to allow us to create an abstraction
 * between the Native (Android) VideoView and the VideoView
 * using the ExoPlayer.
 */
interface VideoViewApi {
    interface OnSurfaceSizeChanged {
        fun onSurfaceSizeChanged(width: Int, height: Int)
    }

    fun setVideoUri(@Nullable uri: Uri?)
    fun setVideoUri(
        @Nullable uri: Uri?,
        @Nullable mediaSource: MediaSource?
    )

    /**
     * Sets the [MediaDrmCallback] to use when handling DRM for media.
     * This should be called before specifying the videos uri or path
     * <br></br>
     * **NOTE:** DRM is only supported on API 18 +
     *
     * @param drmCallback The callback to use when handling DRM media
     */
    fun setDrmCallback(@Nullable drmCallback: MediaDrmCallback?)

    @get:FloatRange(from = 0.0, to = 1.0)
    val volume: Float
    fun setVolume(@FloatRange(from = 0.0, to = 1.0) volume: Float): Boolean
    fun seekTo(@IntRange(from = 0) milliseconds: Long)
    val isPlaying: Boolean
    fun start()
    fun pause()

    /**
     * Performs the functionality to stop the video in playback
     *
     * @param clearSurface `true` if the surface should be cleared
     */
    fun stopPlayback(clearSurface: Boolean)

    /**
     * Prepares the media previously specified for playback.  This should only be called after
     * the playback has completed to restart playback from the beginning.
     *
     * @return `true` if the media was successfully restarted
     */
    fun restart(): Boolean
    fun suspend()
    fun release()

    @get:IntRange(from = 0)
    val duration: Long

    @get:IntRange(from = 0)
    val currentPosition: Long

    @get:IntRange(from = 0, to = 100)
    val bufferedPercent: Int

    @get:Nullable
    val windowInfo: WindowInfo?

    /**
     * Sets the playback speed for this MediaPlayer.
     *
     * @param speed The speed to play the media back at
     * @return True if the speed was set
     */
    fun setPlaybackSpeed(speed: Float): Boolean
    val playbackSpeed: Float
    fun setCaptionListener(@Nullable listener: CaptionListener?)
    fun trackSelectionAvailable(): Boolean

    @Deprecated("use {@link #setTrack(ExoMedia.RendererType, int, int)}")
    fun setTrack(@NonNull type: RendererType?, trackIndex: Int)
    fun setTrack(@NonNull type: RendererType?, groupIndex: Int, trackIndex: Int)
    fun getSelectedTrackIndex(@NonNull type: RendererType?, groupIndex: Int): Int

    /**
     * Clear all selected tracks for the specified renderer.
     * @param type The renderer type
     */
    fun clearSelectedTracks(@NonNull type: RendererType?)

    /**
     * Retrieves a list of available tracks to select from.  Typically [.trackSelectionAvailable]
     * should be called before this.
     *
     * @return A list of available tracks associated with each track type
     */
    @get:Nullable
    val availableTracks: Map<RendererType, TrackGroupArray>?

    /**
     * Enables or disables the track associated with the `type`. Note, by default all
     * tracks are enabled
     *
     * @param type The [com.devbrackets.android.exomedia.ExoMedia.RendererType] to enable or disable the track for
     * @param enabled `true` if the track should be enabled.
     */
    fun setRendererEnabled(@NonNull type: RendererType?, enabled: Boolean)

    /**
     * Return true if at least one renderer for the given type is enabled
     * @param type The renderer type
     * @return true if at least one renderer for the given type is enabled
     */
    fun isRendererEnabled(@NonNull type: RendererType?): Boolean

    fun setMeasureBasedOnAspectRatioEnabled(doNotMeasureBasedOnAspectRatio: Boolean)

    /**
     * Sets the rotation for the Video
     *
     * @param rotation The rotation to apply to the video
     * @param fromUser True if the rotation was requested by the user, false if it is from a video configuration
     */
    fun setVideoRotation(
        @IntRange(from = 0, to = 359) rotation: Int,
        fromUser: Boolean
    )

    fun setOnTouchListener(listener: OnTouchListener?)

    fun onVideoSizeChanged(
        width: Int,
        height: Int,
        pixelWidthHeightRatio: Float
    )

    fun setRepeatMode(@Player.RepeatMode repeatMode: Int)
}