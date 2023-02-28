package org.evidyaloka.player.exoplayer

import android.content.Context
import android.net.Uri
import android.view.Surface
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.drm.MediaDrmCallback
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.ui.PlayerView
import org.evidyaloka.player.exoplayer.ExoMedia.RendererType
import org.evidyaloka.player.exoplayer.listener.CaptionListener
import org.evidyaloka.player.exoplayer.listener.ExoPlayerListener

class ExoVideoDelegate(
    @NonNull context: Context,
    @NonNull val view: PlayerView
) {
    protected var exoMediaPlayer: ExoMediaPlayer? = null
    protected var playRequested = false
    protected var context: Context

    init {
        this.context = context.applicationContext
        setup()
    }

    protected fun setup() {
        initExoPlayer()
    }

    protected fun initExoPlayer() {
        exoMediaPlayer = ExoMediaPlayer(context)
        view.player = exoMediaPlayer?.exoPlayer
    }



    fun setVideoUri(@Nullable uri: Uri?) {
        setVideoUri(uri, null)
    }

    fun setVideoUri(
        @Nullable uri: Uri?,
        @Nullable mediaSource: MediaSource?
    ) {
        //Makes sure the listeners get the onPrepared callback
        exoMediaPlayer!!.seekTo(0)
        if (mediaSource != null) {
            exoMediaPlayer!!.setMediaSource(mediaSource)
        } else if (uri != null) {
            exoMediaPlayer!!.setUri(uri)
        } else {
            exoMediaPlayer!!.setMediaSource(null)
        }
    }

    /**
     * Sets the [MediaDrmCallback] to use when handling DRM for media.
     * This should be called before specifying the videos uri or path
     * <br></br>
     * **NOTE:** DRM is only supported on API 18 +
     *
     * @param drmCallback The callback to use when handling DRM media
     */
    fun setDrmCallback(@Nullable drmCallback: MediaDrmCallback?) {
        exoMediaPlayer!!.setDrmCallback(drmCallback)
    }

    fun restart(): Boolean {
        if (!exoMediaPlayer!!.restart()) {
            return false
        }
        return true
    }

    @get:FloatRange(from = 0.0, to = 1.0)
    val volume: Float
        get() = exoMediaPlayer!!.volume

    fun setVolume(@FloatRange(from = 0.0, to = 1.0) volume: Float): Boolean {
        exoMediaPlayer!!.volume = volume
        return true
    }

    fun seekTo(@IntRange(from = 0) milliseconds: Long) {
        exoMediaPlayer!!.seekTo(milliseconds)
    }

    val isPlaying: Boolean
        get() = exoMediaPlayer!!.playWhenReady

    fun start() {
        exoMediaPlayer!!.playWhenReady = true
        playRequested = true
    }

    fun pause() {
        exoMediaPlayer!!.playWhenReady = false
        playRequested = false
    }

    fun addListener(listener: ExoPlayerListener){
        exoMediaPlayer?.addListener(listener)
    }

    /**
     * Performs the functionality to stop the video in playback
     *
     * @param clearSurface `true` if the surface should be cleared
     */
    fun stopPlayback(clearSurface: Boolean) {
        exoMediaPlayer!!.stop()
        playRequested = false
    }

    fun suspend() {
        exoMediaPlayer!!.release()
        playRequested = false
    }

    val duration: Long
        get() = exoMediaPlayer!!.duration

    val currentPosition: Long
        get() = exoMediaPlayer!!.currentPosition

    val bufferedPercent: Int
        get() = exoMediaPlayer!!.bufferedPercentage

    @get:Nullable
    val windowInfo: WindowInfo?
        get() = exoMediaPlayer!!.windowInfo

    fun trackSelectionAvailable(): Boolean {
        return true
    }

    fun setCaptionListener(@Nullable listener: CaptionListener?) {
        exoMediaPlayer!!.setCaptionListener(listener)
    }

    @Deprecated("use {@link #setTrack(ExoMedia.RendererType, int, int)}")
    fun setTrack(trackType: RendererType?, trackIndex: Int) {
        exoMediaPlayer!!.setSelectedTrack(trackType!!, trackIndex)
    }

    fun setTrack(@NonNull trackType: RendererType?, groupIndex: Int, trackIndex: Int) {
        exoMediaPlayer!!.setSelectedTrack(trackType!!, groupIndex, trackIndex)
    }

    fun getSelectedTrackIndex(@NonNull type: RendererType?, groupIndex: Int): Int {
        return exoMediaPlayer!!.getSelectedTrackIndex(type!!, groupIndex)
    }

    /**
     * Clear all selected tracks for the specified renderer.
     * @param type The renderer type
     */
    fun clearSelectedTracks(@NonNull type: RendererType?) {
        exoMediaPlayer!!.clearSelectedTracks(type!!)
    }

    @get:Nullable
    val availableTracks: Map<RendererType, TrackGroupArray>?
        get() = exoMediaPlayer!!.availableTracks

    fun setRendererEnabled(@NonNull type: RendererType?, enabled: Boolean) {
        exoMediaPlayer!!.setRendererEnabled(type!!, enabled)
    }

    /**
     * Return true if at least one renderer for the given type is enabled
     * @param type The renderer type
     * @return true if at least one renderer for the given type is enabled
     */
    fun isRendererEnabled(@NonNull type: RendererType?): Boolean {
        return exoMediaPlayer!!.isRendererEnabled(type!!)
    }

    fun setPlaybackSpeed(speed: Float): Boolean {
        return exoMediaPlayer!!.setPlaybackSpeed(speed)
    }

    val playbackSpeed: Float
        get() = exoMediaPlayer!!.playbackSpeed

    fun release() {
        exoMediaPlayer!!.release()
    }


    fun setRepeatMode(repeatMode: Int) {
        exoMediaPlayer!!.setRepeatMode(repeatMode)
    }

    fun onSurfaceReady(surface: Surface?) {
        exoMediaPlayer!!.setSurface(surface)
        if (playRequested) {
            exoMediaPlayer!!.playWhenReady = true
        }
    }

    fun onSurfaceDestroyed() {
        exoMediaPlayer!!.clearSurface()
    }

}