package org.evidyaloka.player.exoplayer

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.drm.MediaDrmCallback
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.ui.PlaybackControlView
import com.google.android.exoplayer2.ui.PlayerView
import org.evidyaloka.player.*
import org.evidyaloka.player.exoplayer.listener.CaptionListener
import org.evidyaloka.player.exoplayer.listener.ExoPlayerListener
import org.evidyaloka.player.youtube.PlayerConstants

/**
 * @author Madhankumar
 * created on 31-03-2021
 *
 */
open class SimpleExoPlayer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
): PlayerView(context, attrs, defStyleAttr), VideoViewApi,PlayerApi {

    private var fullScreenListener: FullScreenListener? = null
    private var mExoPlayerFullscreen = false
    private var mFullScreenButton: FrameLayout? = null
    private var mFullScreenIcon: ImageView? = null
    private var mFullScreenDialog: Dialog? = null
    private var parentView:ViewGroup? = null

    var delegate: ExoVideoDelegate

    init {
        delegate = ExoVideoDelegate(context, this)
        this.setShowBuffering(SHOW_BUFFERING_WHEN_PLAYING)
        initFullscreenDialog()
        initFullscreenButton()
    }

    override fun setVideoUri(uri: Uri?) {
        delegate.setVideoUri(uri)
    }

    override fun setVideoUri(uri: Uri?, mediaSource: MediaSource?) {
        delegate.setVideoUri(uri, mediaSource)
    }

    /**
     * Sets the [MediaDrmCallback] to use when handling DRM for media.
     * This should be called before specifying the videos uri or path
     * <br></br>
     * **NOTE:** DRM is only supported on API 18 +
     *
     * @param drmCallback The callback to use when handling DRM media
     */
    override fun setDrmCallback(drmCallback: MediaDrmCallback?) {
        delegate.setDrmCallback(drmCallback)
    }

    override val volume: Float
        get() = delegate.volume

    override fun setVolume(volume: Float): Boolean {
        return delegate.setVolume(volume)
    }

    override fun seekTo(milliseconds: Long) {
        delegate.seekTo(milliseconds)
    }

    override val isPlaying: Boolean
        get() = delegate.isPlaying

    override fun start() {
        delegate.start()
    }

    /**
     * Loads and automatically plays the video.
     * @param uri id of the video
     * @param startSeconds the time from which the video should start playing
     */
    override fun loadVideo(uri: String, startSeconds: Float) {
        cueVideo(uri, startSeconds)
        this.start()
    }

    /**
     * Loads the video's thumbnail and prepares the player to play the video. Does not automatically play the video.
     * @param uri id of the video
     * @param startSeconds the time from which the video should start playing
     */
    override fun cueVideo(uri: String, startSeconds: Float) {
        this.setVideoUri(Uri.parse(uri))
        startSeconds.takeIf { it > 0 }?.let { this.seekTo(it) }
    }


    override fun play() {
        this.start()
    }

    override fun pause() {
        delegate.pause()
    }

    /**
     *
     * @param time The absolute time in seconds to seek to
     */
    override fun seekTo(time: Float) {
        delegate.seekTo(time.toLong() * 1000)
    }

    /**
     * Performs the functionality to stop the video in playback
     *
     * @param clearSurface `true` if the surface should be cleared
     */
    override fun stopPlayback(clearSurface: Boolean) {
        delegate.stopPlayback(clearSurface)
    }

    /**
     * Prepares the media previously specified for playback.  This should only be called after
     * the playback has completed to restart playback from the beginning.
     *
     * @return `true` if the media was successfully restarted
     */
    override fun restart(): Boolean {
        return delegate.restart()
    }

    override fun suspend() {
        delegate.suspend()
    }

    override fun release() {
        delegate.release()
    }

    override fun setFullScreenListener(listener: FullScreenListener) {
        fullScreenListener = listener
    }

    override val duration: Long
        get() = delegate.duration
    override val currentPosition: Long
        get() = delegate.currentPosition
    override val bufferedPercent: Int
        get() = delegate.bufferedPercent
    override val windowInfo: WindowInfo?
        get() = delegate.windowInfo

    /**
     * Sets the playback speed for this MediaPlayer.
     *
     * @param speed The speed to play the media back at
     * @return True if the speed was set
     */
    override fun setPlaybackSpeed(speed: Float): Boolean {
        return delegate.setPlaybackSpeed(speed)
    }

    override val playbackSpeed: Float
        get() = delegate.playbackSpeed

    override fun setPlayerEventListner(listner: PlayerListener) {
        delegate?.addListener(object : ExoPlayerListener {
            override fun onStateChanged(playWhenReady: Boolean, playbackState: Int) {
                listner?.onPlayeBackEvent(
                    when (playbackState) {
                        ExoPlayer.STATE_READY -> {
                            this@SimpleExoPlayer.keepScreenOn = true
                            PlayerState.PLAYING
                        }
                        ExoPlayer.STATE_BUFFERING -> {
                            PlayerState.UNKNOWN
                        }
                        else -> {
                            this@SimpleExoPlayer.keepScreenOn = false
                            PlayerState.UNKNOWN
                        }
                    }
                )
            }

            override fun onError(exoMediaPlayer: ExoMediaPlayer?, e: ExoPlaybackException?) {
                listner?.onError(
                    when (e?.type) {
                        ExoPlaybackException.TYPE_SOURCE -> PlayerError.VIDEO_NOT_FOUND
                        ExoPlaybackException.TYPE_RENDERER -> PlayerError.VIDEO_NOT_PLAYABLE
                        ExoPlaybackException.TYPE_UNEXPECTED -> PlayerError.UNKNOWN
                        ExoPlaybackException.TYPE_REMOTE -> PlayerError.INVALID_PARAMETER_IN_REQUEST
                        ExoPlaybackException.TYPE_OUT_OF_MEMORY -> PlayerError.OUT_OF_MEMORY
                        else -> PlayerError.UNKNOWN
                    }
                )
            }

            override fun onVideoSizeChanged(
                width: Int,
                height: Int,
                unAppliedRotationDegrees: Int,
                pixelWidthHeightRatio: Float
            ) {

            }

            /**
             * Called to indicate the completion of a seek operation.
             */
            override fun onSeekComplete() {

            }

        })
    }



    override fun setCaptionListener(listener: CaptionListener?) {
        delegate.setCaptionListener(listener)
    }

    override fun trackSelectionAvailable(): Boolean {
        return delegate.trackSelectionAvailable()
    }

    override fun setTrack(type: ExoMedia.RendererType?, trackIndex: Int) {
        delegate.setTrack(type, trackIndex)
    }

    override fun setTrack(type: ExoMedia.RendererType?, groupIndex: Int, trackIndex: Int) {
       delegate.setTrack(type, groupIndex, trackIndex)
    }

    override fun getSelectedTrackIndex(type: ExoMedia.RendererType?, groupIndex: Int): Int {
        return delegate.getSelectedTrackIndex(type, groupIndex)
    }

    /**
     * Clear all selected tracks for the specified renderer.
     * @param type The renderer type
     */
    override fun clearSelectedTracks(type: ExoMedia.RendererType?) {
        delegate.clearSelectedTracks(type)
    }

    /**
     * Retrieves a list of available tracks to select from.  Typically [.trackSelectionAvailable]
     * should be called before this.
     *
     * @return A list of available tracks associated with each track type
     */
    override val availableTracks: Map<ExoMedia.RendererType, TrackGroupArray>?
        get() = delegate.availableTracks

    /**
     * Enables or disables the track associated with the `type`. Note, by default all
     * tracks are enabled
     *
     * @param type The [com.devbrackets.android.exomedia.ExoMedia.RendererType] to enable or disable the track for
     * @param enabled `true` if the track should be enabled.
     */
    override fun setRendererEnabled(type: ExoMedia.RendererType?, enabled: Boolean) {
        delegate.setRendererEnabled(type, enabled)
    }

    /**
     * Return true if at least one renderer for the given type is enabled
     * @param type The renderer type
     * @return true if at least one renderer for the given type is enabled
     */
    override fun isRendererEnabled(type: ExoMedia.RendererType?): Boolean {
       return delegate.isRendererEnabled(type)
    }

    override fun setMeasureBasedOnAspectRatioEnabled(doNotMeasureBasedOnAspectRatio: Boolean) {

    }

    /**
     * Sets the rotation for the Video
     *
     * @param rotation The rotation to apply to the video
     * @param fromUser True if the rotation was requested by the user, false if it is from a video configuration
     */
    override fun setVideoRotation(rotation: Int, fromUser: Boolean) {

    }

    override fun onVideoSizeChanged(width: Int, height: Int, pixelWidthHeightRatio: Float) {

    }

    override fun setRepeatMode(repeatMode: Int) {

    }

    private fun initFullscreenDialog() {
        mFullScreenDialog =
            object : Dialog(this.context, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
                override fun onBackPressed() {
                    if (mExoPlayerFullscreen) closeFullscreenDialog()
                    super.onBackPressed()
                }
            }
    }

    private fun initFullscreenButton() {
        val controlView: PlaybackControlView = this.findViewById(R.id.exo_controller)
        mFullScreenIcon =
            controlView.findViewById(R.id.exo_fullscreen_icon)
        mFullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button)
        mFullScreenButton?.setOnClickListener {
            if (!mExoPlayerFullscreen) openFullscreenDialog() else closeFullscreenDialog()
        }
    }

    private fun openFullscreenDialog() {
        parentView = (this.parent as ViewGroup)
        parentView?.removeView(this)
        mFullScreenDialog!!.addContentView(
            this,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        mFullScreenIcon!!.setImageDrawable(
            ContextCompat.getDrawable(
                this.context,
                R.drawable.ic_fullscreen_skrink
            )
        )
        mExoPlayerFullscreen = true
        mFullScreenDialog!!.show()
        fullScreenListener?.enterFullScreen()
    }


    private fun closeFullscreenDialog() {
        fullScreenListener?.exitFullScreen()
        (this.parent as ViewGroup).removeView(this)
        parentView?.addView(
            this
        )
        mExoPlayerFullscreen = false
        mFullScreenDialog!!.dismiss()
        mFullScreenIcon!!.setImageDrawable(
            ContextCompat.getDrawable(
                this.context,
                R.drawable.ic_fullscreen_expand
            )
        )
    }


}