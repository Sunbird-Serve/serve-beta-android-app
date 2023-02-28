package org.evidyaloka.player.exoplayer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.util.Log
import android.view.Surface
import androidx.annotation.FloatRange
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.Size
import androidx.collection.ArrayMap
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.C.AudioContentType
import com.google.android.exoplayer2.C.AudioUsage
import com.google.android.exoplayer2.Player.DefaultEventListener
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.analytics.AnalyticsCollector
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.audio.AudioRendererEventListener
import com.google.android.exoplayer2.decoder.DecoderCounters
import com.google.android.exoplayer2.drm.*
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.MetadataOutput
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroup
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.text.Cue
import com.google.android.exoplayer2.text.TextOutput
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.SelectionOverride
import com.google.android.exoplayer2.trackselection.MappingTrackSelector.MappedTrackInfo
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.util.Clock
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoRendererEventListener
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.evidyaloka.player.exoplayer.ExoMedia.RendererType
import org.evidyaloka.player.exoplayer.listener.*
import org.evidyaloka.player.exoplayer.renderer.RendererProvider
import org.evidyaloka.player.exoplayer.util.Repeater
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import com.google.android.exoplayer2.upstream.DefaultAllocator

import com.google.android.exoplayer2.DefaultLoadControl

import com.google.android.exoplayer2.LoadControl




class ExoMediaPlayer(@field:NonNull @param:NonNull private val context: Context) :
    DefaultEventListener() {

    @get:NonNull
    @NonNull
    val exoPlayer: ExoPlayer

    @NonNull
    private val trackSelector: DefaultTrackSelector

    @NonNull
    private val adaptiveTrackSelectionFactory: AdaptiveTrackSelection.Factory

    @NonNull
    private val mainHandler: Handler

    @NonNull
    private val listeners: CopyOnWriteArrayList<ExoPlayerListener> =
        CopyOnWriteArrayList<ExoPlayerListener>()

    @NonNull
    private val stopped =
        AtomicBoolean()
    private var prepared = false

    @NonNull
    private val stateStore = StateStore()

    @NonNull
    private val bufferRepeater: Repeater = Repeater()

    @Nullable
    private var surface: Surface? = null

    @Nullable
    private var drmCallback: MediaDrmCallback? = null

    @Nullable
    private var mediaSource: MediaSource? = null

    @NonNull
    private var renderers: List<Renderer> = listOf()

    @NonNull
    private val bandwidthMeter = DefaultBandwidthMeter()

    @Nullable
    private var captionListener: CaptionListener? = null

    @Nullable
    private var metadataListener: MetadataListener? = null

    @Nullable
    private var internalErrorListener: InternalErrorListener? = null

    @Nullable
    private var bufferUpdateListener: OnBufferUpdateListener? = null

    @Nullable
    private var wakeLock: WakeLock? = null

    @NonNull
    private val capabilitiesListener = CapabilitiesListener()
    var audioSessionId = C.AUDIO_SESSION_ID_UNSET
        private set

    @FloatRange(from = 0.0, to = 1.0)
    protected var requestedVolume = 1.0f

    //Minimum Video you want to buffer while Playing
    private val MIN_BUFFER_DURATION = 2000

    //Max Video you want to buffer during PlayBack
    private val MAX_BUFFER_DURATION = 5000

    //Min Video you want to buffer before start Playing it
    private val MIN_PLAYBACK_START_BUFFER = 1500

    //Min video You want to buffer when user resumes video
    private val MIN_PLAYBACK_RESUME_BUFFER = 2000

    /**
     * Returns the [AnalyticsCollector] used for collecting analytics events.
     */
    @get:NonNull
    @NonNull
    val analyticsCollector: AnalyticsCollector

    init {
        bufferRepeater.repeaterDelay = BUFFER_REPEAT_DELAY
        bufferRepeater.setRepeatListener(BufferRepeatListener())
        mainHandler = Handler()
        val componentListener =
            ComponentListener()
        val rendererProvider = RendererProvider(
            context,
            mainHandler,
            componentListener,
            componentListener,
            componentListener,
            componentListener
        )
        val drmSessionManager =
            generateDrmSessionManager()
        rendererProvider.setDrmSessionManager(drmSessionManager)
        rendererProvider.generate()?.let { renderers = it }
        adaptiveTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        trackSelector = DefaultTrackSelector(adaptiveTrackSelectionFactory)
        val loadControl =
            if (ExoMedia.Data.loadControl != null) ExoMedia.Data.loadControl else getLoadControl()
        exoPlayer = SimpleExoPlayer.Builder(context)
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl!!)
            .build()
        exoPlayer.addListener(this)
        analyticsCollector = AnalyticsCollector(Clock.DEFAULT)
        analyticsCollector.setPlayer(exoPlayer)
        exoPlayer.addListener(analyticsCollector)
        setupDamSessionManagerAnalytics(drmSessionManager)
    }

    override fun onPlayerStateChanged(
        playWhenReady: Boolean,
        state: Int
    ) {
        reportPlayerState()
    }

    override fun onPlayerError(exception: ExoPlaybackException) {
        for (listener in listeners) {
            listener.onError(this, exception)
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
        this.drmCallback = drmCallback
    }

    fun setUri(@Nullable uri: Uri?) {
        setMediaSource(
            if (uri != null) ExoMedia.Data.mediaSourceProvider.generate(
                context,
                mainHandler,
                uri,
                bandwidthMeter
            ) else null
        )
    }

    fun setMediaSource(@Nullable source: MediaSource?) {
        if (mediaSource != null) {
            mediaSource!!.removeEventListener(analyticsCollector)
            analyticsCollector.resetForNewMediaSource()
        }
        source?.addEventListener(mainHandler, analyticsCollector)
        mediaSource = source

        prepared = false
        prepare()
    }

    fun addListener(listener: ExoPlayerListener?) {
        if (listener != null) {
            listeners.add(listener)
        }
    }

    fun removeListener(listener: ExoPlayerListener?) {
        if (listener != null) {
            listeners.remove(listener)
        }
    }

    fun setBufferUpdateListener(@Nullable listener: OnBufferUpdateListener?) {
        bufferUpdateListener = listener
        setBufferRepeaterStarted(listener != null)
    }

    fun setInternalErrorListener(@Nullable listener: InternalErrorListener?) {
        internalErrorListener = listener
    }

    fun setCaptionListener(@Nullable listener: CaptionListener?) {
        captionListener = listener
    }

    fun setMetadataListener(@Nullable listener: MetadataListener?) {
        metadataListener = listener
    }

    fun setSurface(@Nullable surface: Surface?) {
        this.surface = surface
        sendMessage(
            C.TRACK_TYPE_VIDEO,
            C.MSG_SET_SURFACE,
            surface,
            false
        )
    }

    @Nullable
    fun getSurface(): Surface? {
        return surface
    }

    @NonNull
    fun getBandwidthMeter(): BandwidthMeter {
        return bandwidthMeter
    }

    fun getLoadControl(): LoadControl = DefaultLoadControl.Builder()
            .setAllocator(DefaultAllocator(true, 16))
            .setBufferDurationsMs(
                MIN_BUFFER_DURATION,
                MAX_BUFFER_DURATION,
                MIN_PLAYBACK_START_BUFFER,
                MIN_PLAYBACK_RESUME_BUFFER
            )
            .setTargetBufferBytes(-1)
            .setPrioritizeTimeOverSizeThresholds(true).createDefaultLoadControl()

    /**
     * Adds an [AnalyticsListener] to receive analytics events.
     *
     * @param listener The listener to be added.
     */
    fun addAnalyticsListener(listener: AnalyticsListener?) {
        if (listener != null) {
            analyticsCollector.addListener(listener)
        }
    }

    /**
     * Removes an [AnalyticsListener].
     *
     * @param listener The listener to be removed.
     */
    fun removeAnalyticsListener(listener: AnalyticsListener?) {
        if (listener != null) {
            analyticsCollector.removeListener(listener)
        }
    }

    fun clearSurface() {
        if (surface != null) {
            surface!!.release()
        }
        surface = null
        sendMessage(
            C.TRACK_TYPE_VIDEO,
            C.MSG_SET_SURFACE,
            null,
            false
        )
    }

    @Deprecated("use {@link #clearSurface()} as this is no longer blocking")
    fun blockingClearSurface() {
        clearSurface()
    }// construct fake track group array for track groups from all the renderers of the same type// collect track groups from all the track renderers of the same type// Retrieves the available tracks

    // Maps the available tracks

    /**
     * Retrieves a list of available tracks
     *
     * @return A list of available tracks associated with each type
     */
    @get:Nullable
    val availableTracks: Map<RendererType, TrackGroupArray>?
        get() {
            if (playbackState == Player.STATE_IDLE) {
                return null
            }

            // Retrieves the available tracks
            val trackMap: MutableMap<RendererType, TrackGroupArray> = ArrayMap()
            val mappedTrackInfo = trackSelector.currentMappedTrackInfo ?: return trackMap.toMap()

            // Maps the available tracks
            val types: Array<RendererType> = arrayOf<RendererType>(
                RendererType.AUDIO,
                RendererType.VIDEO,
                RendererType.CLOSED_CAPTION,
                RendererType.METADATA
            )
            for (type in types) {
                val trackGroups: MutableList<TrackGroup> =
                    ArrayList()
                // collect track groups from all the track renderers of the same type
                for (exoPlayerTrackIndex in getExoPlayerTracksInfo(
                    type,
                    0,
                    mappedTrackInfo
                ).rendererTrackIndexes) {
                    val trackGroupArray =
                        mappedTrackInfo.getTrackGroups(exoPlayerTrackIndex)
                    for (i in 0 until trackGroupArray.length) {
                        trackGroups.add(trackGroupArray[i])
                    }
                }
                if (!trackGroups.isEmpty()) {
                    // construct fake track group array for track groups from all the renderers of the same type
                    trackMap[type] = TrackGroupArray(*trackGroups.toTypedArray())
                }
            }
            return trackMap.toMap()
        }

    @Deprecated("Use {@link #getSelectedTrackIndex(RendererType, int)}")
    fun getSelectedTrackIndex(@NonNull type: RendererType): Int {
        return getSelectedTrackIndex(type, 0)
    }

    fun getSelectedTrackIndex(@NonNull type: RendererType, groupIndex: Int): Int {
        // Retrieves the available tracks
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo
        val tracksInfo =
            getExoPlayerTracksInfo(type, groupIndex, mappedTrackInfo)
        val trackGroupArray =
            if (tracksInfo.rendererTrackIndex == C.INDEX_UNSET) null else mappedTrackInfo!!.getTrackGroups(
                tracksInfo.rendererTrackIndex
            )
        if (trackGroupArray == null || trackGroupArray.length == 0) {
            return -1
        }

        // Verifies the track selection has been overridden
        val selectionOverride = trackSelector.parameters
            .getSelectionOverride(tracksInfo.rendererTrackIndex, trackGroupArray)
        return if (selectionOverride == null || selectionOverride.groupIndex != tracksInfo.rendererTrackGroupIndex || selectionOverride.length <= 0) {
            -1
        } else selectionOverride.tracks[0]
        // In the current implementation only one track can be selected at a time so get the first one.
    }

    @Deprecated("Use {@link #setSelectedTrack(RendererType, int, int)}")
    fun setSelectedTrack(@NonNull type: RendererType, index: Int) {
        setSelectedTrack(type, 0, index)
    }

    fun setSelectedTrack(
        @NonNull type: RendererType,
        groupIndex: Int,
        trackIndex: Int
    ) {
        // Retrieves the available tracks
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo
        val tracksInfo =
            getExoPlayerTracksInfo(type, groupIndex, mappedTrackInfo)
        val trackGroupArray =
            if (tracksInfo.rendererTrackIndex == C.INDEX_UNSET || mappedTrackInfo == null) null else mappedTrackInfo.getTrackGroups(
                tracksInfo.rendererTrackIndex
            )
        if (trackGroupArray == null || trackGroupArray.length == 0 || trackGroupArray.length <= tracksInfo.rendererTrackGroupIndex) {
            return
        }

        // Finds the requested group
        val group = trackGroupArray[tracksInfo.rendererTrackGroupIndex]
        if (group == null || group.length <= trackIndex) {
            return
        }
        val parametersBuilder = trackSelector.buildUponParameters()
        for (rendererTrackIndex in tracksInfo.rendererTrackIndexes) {
            parametersBuilder.clearSelectionOverrides(rendererTrackIndex)
            if (tracksInfo.rendererTrackIndex == rendererTrackIndex) {
                // Specifies the correct track to use
                parametersBuilder.setSelectionOverride(
                    rendererTrackIndex, trackGroupArray,
                    SelectionOverride(tracksInfo.rendererTrackGroupIndex, trackIndex)
                )
                // make sure renderer is enabled
                parametersBuilder.setRendererDisabled(rendererTrackIndex, false)
            } else {
                // disable other renderers of the same type to avoid playback errors
                parametersBuilder.setRendererDisabled(rendererTrackIndex, true)
            }
        }
        trackSelector.setParameters(parametersBuilder)
    }

    /**
     * Clear all selected tracks for the specified renderer and re-enable all renderers so the player can select the default track.
     *
     * @param type The renderer type
     */
    fun clearSelectedTracks(@NonNull type: RendererType) {
        // Retrieves the available tracks
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo
        val tracksInfo =
            getExoPlayerTracksInfo(type, 0, mappedTrackInfo)
        val parametersBuilder = trackSelector.buildUponParameters()
        for (rendererTrackIndex in tracksInfo.rendererTrackIndexes) {
            // Reset all renderers re-enabling so the player can select the streams default track.
            parametersBuilder.setRendererDisabled(rendererTrackIndex, false)
                .clearSelectionOverrides(rendererTrackIndex)
        }
        trackSelector.setParameters(parametersBuilder)
    }

    fun setRendererEnabled(@NonNull type: RendererType, enabled: Boolean) {
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo
        val tracksInfo =
            getExoPlayerTracksInfo(type, 0, mappedTrackInfo)
        if (!tracksInfo.rendererTrackIndexes.isEmpty()) {
            var enabledSomething = false
            val parametersBuilder = trackSelector.buildUponParameters()
            for (rendererTrackIndex in tracksInfo.rendererTrackIndexes) {
                if (enabled) {
                    val selectionOverride = trackSelector.parameters
                        .getSelectionOverride(
                            rendererTrackIndex,
                            mappedTrackInfo!!.getTrackGroups(rendererTrackIndex)
                        )
                    // check whether the renderer has been selected before
                    // other renderers should be kept disabled to avoid playback errors
                    if (selectionOverride != null) {
                        parametersBuilder.setRendererDisabled(rendererTrackIndex, false)
                        enabledSomething = true
                    }
                } else {
                    parametersBuilder.setRendererDisabled(rendererTrackIndex, true)
                }
            }
            if (enabled && !enabledSomething) {
                // if nothing has been enabled enable the first sequential renderer
                parametersBuilder.setRendererDisabled(tracksInfo.rendererTrackIndexes[0], false)
            }
            trackSelector.setParameters(parametersBuilder)
        }
    }

    /**
     * Return true if at least one renderer for the given type is enabled
     * @param type The renderer type
     * @return true if at least one renderer for the given type is enabled
     */
    fun isRendererEnabled(@NonNull type: RendererType): Boolean {
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo
        val tracksInfo =
            getExoPlayerTracksInfo(type, 0, mappedTrackInfo)
        val parameters = trackSelector.parameters
        for (rendererTrackIndex in tracksInfo.rendererTrackIndexes) {
            if (!parameters.getRendererDisabled(rendererTrackIndex)) {
                return true
            }
        }
        return false
    }

    @get:FloatRange(from = 0.0, to = 1.0)
    var volume: Float
        get() = requestedVolume
        set(volume) {
            requestedVolume = volume
            sendMessage(
                C.TRACK_TYPE_AUDIO,
                C.MSG_SET_VOLUME,
                requestedVolume
            )
        }

    fun setAudioStreamType(streamType: Int) {
        @AudioUsage val usage =
            Util.getAudioUsageForStreamType(streamType)
        @AudioContentType val contentType =
            Util.getAudioContentTypeForStreamType(streamType)
        val audioAttributes =
            AudioAttributes.Builder()
                .setUsage(usage)
                .setContentType(contentType)
                .build()
        sendMessage(
            C.TRACK_TYPE_AUDIO,
            C.MSG_SET_AUDIO_ATTRIBUTES,
            audioAttributes
        )
    }

    fun forcePrepare() {
        prepared = false
    }

    fun prepare() {
        if (prepared || mediaSource == null) {
            return
        }
        if (!renderers.isEmpty()) {
            exoPlayer.stop()
        }
        stateStore.reset()
        exoPlayer.prepare(mediaSource!!)
        prepared = true
        stopped.set(false)
    }

    fun stop() {
        if (!stopped.getAndSet(true)) {
            exoPlayer.playWhenReady = false
            exoPlayer.stop()
        }
    }

    /**
     * TODO: Expose this
     * Seeks to the specified position in the media currently loaded specified by `positionMs`.
     * If `limitToCurrentWindow` is true then a seek won't be allowed to span across windows.
     * This should only be different if the media in playback has multiple windows (e.g. in the case of using a
     * `ConcatenatingMediaSource` with more than 1 source)
     *
     * @param positionMs           The position to seek to in the media
     * @param limitToCurrentWindow `true` to only seek in the current window
     */
    @JvmOverloads
    fun seekTo(positionMs: Long, limitToCurrentWindow: Boolean = false) {
        analyticsCollector.notifySeekStarted()
        if (limitToCurrentWindow) {
            exoPlayer.seekTo(positionMs)
            stateStore.setMostRecentState(
                stateStore.isLastReportedPlayWhenReady,
                StateStore.STATE_SEEKING
            )
            return
        }

        // We seek to the position in the timeline (may be across windows)
        val timeline = exoPlayer.currentTimeline
        val windowCount = timeline.windowCount
        var cumulativePositionMs: Long = 0
        val window =
            Timeline.Window()
        for (index in 0 until windowCount) {
            timeline.getWindow(index, window)
            val windowDurationMs = window.durationMs
            if (cumulativePositionMs < positionMs && positionMs <= cumulativePositionMs + windowDurationMs) {
                exoPlayer.seekTo(index, positionMs - cumulativePositionMs)
                stateStore.setMostRecentState(
                    stateStore.isLastReportedPlayWhenReady,
                    StateStore.STATE_SEEKING
                )
                return
            }
            cumulativePositionMs += windowDurationMs
        }
        Log.e(
            TAG,
            "Unable to seek across windows, falling back to in-window seeking"
        )
        exoPlayer.seekTo(positionMs)
        stateStore.setMostRecentState(
            stateStore.isLastReportedPlayWhenReady,
            StateStore.STATE_SEEKING
        )
    }

    /**
     * Seeks to the beginning of the media, and plays it. This method will not succeed if playback state is not `ExoPlayer.STATE_IDLE` or `ExoPlayer.STATE_ENDED`.
     *
     * @return `true` if the media was successfully restarted, otherwise `false`
     */
    fun restart(): Boolean {
        val playbackState = playbackState
        if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
            return false
        }
        seekTo(0)
        playWhenReady = true
        forcePrepare()
        prepare()
        return true
    }

    fun release() {
        setBufferRepeaterStarted(false)
        listeners.clear()
        if (mediaSource != null) {
            mediaSource!!.removeEventListener(analyticsCollector)
        }
        surface = null
        exoPlayer.release()
        stayAwake(false)
    }

    val playbackState: Int
        get() = exoPlayer.playbackState

    fun setPlaybackSpeed(speed: Float): Boolean {
        val params = PlaybackParameters(speed, 1.0f)
        exoPlayer.setPlaybackParameters(params)
        return true
    }

    val playbackSpeed: Float
        get() {
            val params = exoPlayer.playbackParameters
            return params.speed
        }

    val currentPosition: Long
        get() = getCurrentPosition(false)

    /**
     * TODO: Expose this
     * Returns the position in the media. If `limitToCurrentWindow` is `true` then the position
     * in the current window will be returned, otherwise the total position across all windows will be returned.
     * These should only be different if the media in playback has multiple windows (e.g. in the case of using a
     * `ConcatenatingMediaSource` with more than 1 source)
     *
     * @param limitToCurrentWindow If `true` the position within the current window will be returned
     * @return The current position in the media
     */
    fun getCurrentPosition(limitToCurrentWindow: Boolean): Long {
        val positionInCurrentWindow = exoPlayer.currentPosition
        if (limitToCurrentWindow) {
            return positionInCurrentWindow
        }

        // TODO cache the total time at the start of each window (e.g. Map<WindowIndex, cumulativeStartTimeMs>)
        // Adds the preceding window durations
        val timeline = exoPlayer.currentTimeline
        val maxWindowIndex =
            Math.min(timeline.windowCount - 1, exoPlayer.currentWindowIndex)
        var cumulativePositionMs: Long = 0
        val window =
            Timeline.Window()
        for (index in 0 until maxWindowIndex) {
            timeline.getWindow(index, window)
            cumulativePositionMs += window.durationMs
        }
        return cumulativePositionMs + positionInCurrentWindow
    }

    val duration: Long
        get() = exoPlayer.duration

    val bufferedPercentage: Int
        get() = exoPlayer.bufferedPercentage

    @get:Nullable
    val windowInfo: WindowInfo?
        get() {
            val timeline = exoPlayer.currentTimeline
            if (timeline.isEmpty) {
                return null
            }
            val currentWindowIndex = exoPlayer.currentWindowIndex
            val currentWindow = timeline.getWindow(
                currentWindowIndex,
                Timeline.Window(),
                true
            )
            return WindowInfo(
                exoPlayer.previousWindowIndex,
                currentWindowIndex,
                exoPlayer.nextWindowIndex,
                currentWindow
            )
        }

    var playWhenReady: Boolean
        get() = exoPlayer.playWhenReady
        set(playWhenReady) {
            exoPlayer.playWhenReady = playWhenReady
            stayAwake(playWhenReady)
        }

    /**
     * This function has the MediaPlayer access the low-level power manager
     * service to control the device's power usage while playing is occurring.
     * The parameter is a combination of [PowerManager] wake flags.
     * Use of this method requires [Manifest.permission.WAKE_LOCK]
     * permission.
     * By default, no attempt is made to keep the device awake during playback.
     *
     * @param context the Context to use
     * @param mode    the power/wake mode to set
     * @see PowerManager
     */
    fun setWakeMode(context: Context, mode: Int) {
        var wasHeld = false
        if (wakeLock != null) {
            if (wakeLock!!.isHeld) {
                wasHeld = true
                wakeLock!!.release()
            }
            wakeLock = null
        }

        //Acquires the wakelock if we have permissions to
        if (context.packageManager.checkPermission(
                Manifest.permission.WAKE_LOCK,
                context.packageName
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val pm =
                context.getSystemService(Context.POWER_SERVICE) as PowerManager
            if (pm != null) {
                wakeLock = pm.newWakeLock(
                    mode or PowerManager.ON_AFTER_RELEASE,
                    ExoMediaPlayer::class.java.name
                )
                wakeLock?.setReferenceCounted(false)
            } else {
                Log.e(
                    TAG,
                    "Unable to acquire WAKE_LOCK due to a null power manager"
                )
            }
        } else {
            Log.w(
                TAG,
                "Unable to acquire WAKE_LOCK due to missing manifest permission"
            )
        }
        stayAwake(wasHeld)
    }

    fun setRepeatMode(@Player.RepeatMode repeatMode: Int) {
        exoPlayer.repeatMode = repeatMode
    }

    protected fun getExoPlayerTrackType(@NonNull type: RendererType?): Int {
        when (type) {
            RendererType.AUDIO -> return C.TRACK_TYPE_AUDIO
            RendererType.VIDEO -> return C.TRACK_TYPE_VIDEO
            RendererType.CLOSED_CAPTION -> return C.TRACK_TYPE_TEXT
            RendererType.METADATA -> return C.TRACK_TYPE_METADATA
        }
        return C.TRACK_TYPE_UNKNOWN
    }

    protected fun getExoMediaRendererType(exoPlayerTrackType: Int): RendererType? {
        return when (exoPlayerTrackType) {
            C.TRACK_TYPE_AUDIO -> RendererType.AUDIO
            C.TRACK_TYPE_VIDEO -> RendererType.VIDEO
            C.TRACK_TYPE_TEXT -> RendererType.CLOSED_CAPTION
            C.TRACK_TYPE_METADATA -> RendererType.METADATA
            else -> null
        }
    }

    protected fun getExoPlayerTracksInfo(
        @NonNull type: RendererType,
        groupIndex: Int,
        mappedTrackInfo: MappedTrackInfo?
    ): ExoPlayerRendererTracksInfo {
        // holder for the all exo player renderer track indexes of the specified renderer type
        val exoPlayerRendererTrackIndexes: MutableList<Int> =
            ArrayList()
        // the exoplayer renderer track index related to the specified group index
        var exoPlayerRendererTrackIndex = C.INDEX_UNSET
        // the corrected exoplayer group index
        var exoPlayerRendererTrackGroupIndex = C.INDEX_UNSET
        var skippedRenderersGroupsCount = 0
        if (mappedTrackInfo != null) {
            for (rendererIndex in 0 until mappedTrackInfo.rendererCount) {
                val exoPlayerRendererType = mappedTrackInfo.getRendererType(rendererIndex)
                if (type === getExoMediaRendererType(exoPlayerRendererType)) {
                    exoPlayerRendererTrackIndexes.add(rendererIndex)
                    val trackGroups = mappedTrackInfo.getTrackGroups(rendererIndex)
                    if (skippedRenderersGroupsCount + trackGroups.length > groupIndex) {
                        if (exoPlayerRendererTrackIndex == C.INDEX_UNSET) {
                            // if the groupIndex belongs to the current exo player renderer
                            exoPlayerRendererTrackIndex = rendererIndex
                            exoPlayerRendererTrackGroupIndex =
                                groupIndex - skippedRenderersGroupsCount
                        }
                    } else {
                        skippedRenderersGroupsCount += trackGroups.length
                    }
                }
            }
        }
        return ExoPlayerRendererTracksInfo(
            exoPlayerRendererTrackIndexes,
            exoPlayerRendererTrackIndex,
            exoPlayerRendererTrackGroupIndex
        )
    }

    inner class ExoPlayerRendererTracksInfo(
        rendererTrackIndexes: List<Int>?,
        rendererTrackIndex: Int,
        rendererTrackGroupIndex: Int
    ) {
        /**
         * The exo player renderer track indexes
         */
        val rendererTrackIndexes: List<Int>

        /**
         * The renderer track index related to the requested `groupIndex`
         */
        val rendererTrackIndex: Int

        /**
         * The corrected exoplayer group index which may be used to obtain proper track group from the renderer
         */
        val rendererTrackGroupIndex: Int

        init {
            this.rendererTrackIndexes =
                Collections.unmodifiableList(rendererTrackIndexes)
            this.rendererTrackIndex = rendererTrackIndex
            this.rendererTrackGroupIndex = rendererTrackGroupIndex
        }
    }

    protected fun sendMessage(
        renderType: Int,
        messageType: Int,
        message: Any?,
        blocking: Boolean = false
    ) {
        if (renderers.isEmpty()) {
            return
        }
        val messages: MutableList<PlayerMessage> =
            ArrayList()
        for (renderer in renderers) {
            if (renderer.trackType == renderType) {
                messages.add(
                    exoPlayer.createMessage(renderer).setType(messageType).setPayload(message)
                )
            }
        }
        if (blocking) {
            blockingSendMessages(messages)
        } else {
            for (playerMessage in messages) {
                playerMessage.send()
            }
        }
    }

    /**
     * This was pulled from the *Deprecated* ExoPlayerImpl#blockingSendMessages method
     *
     * @param messages The messages
     */
    protected fun blockingSendMessages(messages: List<PlayerMessage>) {
        var wasInterrupted = false
        for (message in messages) {
            var blockMessage = true
            while (blockMessage) {
                try {
                    message.blockUntilDelivered()
                    blockMessage = false
                } catch (e: InterruptedException) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    wasInterrupted = true
                }
            }
        }
        if (wasInterrupted) {
            // Restore the interrupted status.
            Thread.currentThread().interrupt()
        }
    }

    /**
     * Used with playback state changes to correctly acquire and
     * release the wakelock if the user has enabled it with [.setWakeMode].
     * If the [.wakeLock] is null then no action will be performed.
     *
     * @param awake True if the wakelock should be acquired
     */
    protected fun stayAwake(awake: Boolean) {
        if (wakeLock == null) {
            return
        }
        if (awake && !wakeLock!!.isHeld) {
            wakeLock!!.acquire(WAKE_LOCK_TIMEOUT.toLong())
        } else if (!awake && wakeLock!!.isHeld) {
            wakeLock!!.release()
        }
    }

    /**
     * Generates the [DrmSessionManager] to use with the [RendererProvider]. This will
     * return null on API's &lt; {@value Build.VERSION_CODES#JELLY_BEAN_MR2}
     *
     * @return The [DrmSessionManager] to use or `null`
     */
    @Nullable
    protected fun generateDrmSessionManager(): DrmSessionManager<FrameworkMediaCrypto>? {
        // DRM is only supported on API 18 + in the ExoPlayer
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return null
        }

        // Widevine will capture the majority of use cases however playready is supported on all AndroidTV devices
        val uuid = C.WIDEVINE_UUID
        return try {
            val sessionManager =
                DefaultDrmSessionManager(
                    uuid,
                    FrameworkMediaDrm.newInstance(uuid),
                    DelegatedMediaDrmCallback(),
                    HashMap<String, String>()
                )
            sessionManager.addListener(mainHandler, capabilitiesListener)
            sessionManager
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.d(
                TAG,
                "Unable to create a DrmSessionManager due to an exception",
                e
            )
            null
        }
    }

    protected fun setupDamSessionManagerAnalytics(drmSessionManager: DrmSessionManager<FrameworkMediaCrypto>?) {
        if (drmSessionManager is DefaultDrmSessionManager<*>) {
            (drmSessionManager as DefaultDrmSessionManager<*>).addListener(
                mainHandler,
                analyticsCollector
            )
        }
    }

    private fun reportPlayerState() {
        val playWhenReady = exoPlayer.playWhenReady
        val playbackState = playbackState
        val newState = stateStore.getState(playWhenReady, playbackState)
        if (newState != stateStore.mostRecentState) {
            stateStore.setMostRecentState(playWhenReady, playbackState)

            //Makes sure the buffering notifications are sent
            if (newState == Player.STATE_READY) {
                setBufferRepeaterStarted(true)
            } else if (newState == Player.STATE_IDLE || newState == Player.STATE_ENDED) {
                setBufferRepeaterStarted(false)
            }

            //Because the playWhenReady isn't a state in itself, rather a flag to a state we will ignore informing of
            // see events when that is the only change.  Additionally, on some devices we get states ordered as
            // [seeking, ready, buffering, ready] while on others we get [seeking, buffering, ready]
            var informSeekCompletion = stateStore.matchesHistory(
                intArrayOf(
                    StateStore.STATE_SEEKING,
                    Player.STATE_BUFFERING,
                    Player.STATE_READY
                ), true
            )
            informSeekCompletion = informSeekCompletion or stateStore.matchesHistory(
                intArrayOf(
                    Player.STATE_BUFFERING,
                    StateStore.STATE_SEEKING,
                    Player.STATE_READY
                ), true
            )
            informSeekCompletion = informSeekCompletion or stateStore.matchesHistory(
                intArrayOf(
                    StateStore.STATE_SEEKING,
                    Player.STATE_READY,
                    Player.STATE_BUFFERING,
                    Player.STATE_READY
                ), true
            )
            for (listener in listeners) {
                listener.onStateChanged(playWhenReady, playbackState)
                if (informSeekCompletion) {
                    listener.onSeekComplete()
                }
            }
        }
    }

    private fun setBufferRepeaterStarted(start: Boolean) {
        if (start && bufferUpdateListener != null) {
            bufferRepeater.start()
        } else {
            bufferRepeater.stop()
        }
    }

    private class StateStore {
        //We keep the last few states because that is all we need currently
        private val prevStates =
            intArrayOf(Player.STATE_IDLE, Player.STATE_IDLE, Player.STATE_IDLE, Player.STATE_IDLE)

        fun reset() {
            for (i in prevStates.indices) {
                prevStates[i] = Player.STATE_IDLE
            }
        }

        fun setMostRecentState(playWhenReady: Boolean, state: Int) {
            val newState = getState(playWhenReady, state)
            if (prevStates[3] == newState) {
                return
            }
            prevStates[0] = prevStates[1]
            prevStates[1] = prevStates[2]
            prevStates[2] = prevStates[3]
            prevStates[3] = state
        }

        fun getState(playWhenReady: Boolean, state: Int): Int {
            return state or if (playWhenReady) FLAG_PLAY_WHEN_READY else 0
        }

        val mostRecentState: Int
            get() = prevStates[3]

        val isLastReportedPlayWhenReady: Boolean
            get() = prevStates[3] and FLAG_PLAY_WHEN_READY != 0

        fun matchesHistory(
            @Size(min = 1, max = 4) states: IntArray,
            ignorePlayWhenReady: Boolean
        ): Boolean {
            var flag = true
            val andFlag =
                if (ignorePlayWhenReady) FLAG_PLAY_WHEN_READY.inv() else 0x0.inv()
            val startIndex = prevStates.size - states.size
            for (i in startIndex until prevStates.size) {
                flag =
                    flag and (prevStates[i] and andFlag == states[i - startIndex] and andFlag)
            }
            return flag
        }

        companion object {
            const val FLAG_PLAY_WHEN_READY = -0x10000000
            const val STATE_SEEKING = 100
        }
    }

    private inner class BufferRepeatListener : Repeater.RepeatListener {
        override fun onRepeat() {
            bufferUpdateListener?.onBufferingUpdate(bufferedPercentage)
        }
    }

    /**
     * Delegates the [.drmCallback] so that we don't need to re-initialize the renderers
     * when the callback is set.
     */
    private inner class DelegatedMediaDrmCallback : MediaDrmCallback {
        @Throws(Exception::class)
        override fun executeProvisionRequest(
            uuid: UUID,
            request: ExoMediaDrm.ProvisionRequest
        ): ByteArray {
            return if (drmCallback != null) drmCallback!!.executeProvisionRequest(
                uuid,
                request
            ) else ByteArray(0)
        }

        @Throws(Exception::class)
        override fun executeKeyRequest(
            uuid: UUID,
            request: ExoMediaDrm.KeyRequest
        ): ByteArray {
            return if (drmCallback != null) drmCallback!!.executeKeyRequest(
                uuid,
                request
            ) else ByteArray(0)
        }
    }

    private inner class CapabilitiesListener : DefaultDrmSessionEventListener {
        override fun onDrmKeysLoaded() {
            // Purposefully left blank
        }

        override fun onDrmKeysRestored() {
            // Purposefully left blank
        }

        override fun onDrmKeysRemoved() {
            // Purposefully left blank
        }

        override fun onDrmSessionManagerError(e: Exception) {
            internalErrorListener?.onDrmSessionManagerError(e)
        }
    }

    private inner class ComponentListener : VideoRendererEventListener,
        AudioRendererEventListener, TextOutput, MetadataOutput {
        override fun onAudioEnabled(counters: DecoderCounters) {
            analyticsCollector.onAudioEnabled(counters)
        }

        override fun onAudioDisabled(counters: DecoderCounters) {
            audioSessionId = C.AUDIO_SESSION_ID_UNSET
            analyticsCollector.onAudioDisabled(counters)
        }

        override fun onAudioSessionId(sessionId: Int) {
            audioSessionId = sessionId
            analyticsCollector.onAudioSessionId(sessionId)
        }

        override fun onAudioDecoderInitialized(
            decoderName: String,
            initializedTimestampMs: Long,
            initializationDurationMs: Long
        ) {
            analyticsCollector.onAudioDecoderInitialized(
                decoderName,
                initializedTimestampMs,
                initializationDurationMs
            )
        }

        override fun onAudioInputFormatChanged(format: Format) {
            analyticsCollector.onAudioInputFormatChanged(format)
        }

        override fun onAudioSinkUnderrun(
            bufferSize: Int,
            bufferSizeMs: Long,
            elapsedSinceLastFeedMs: Long
        ) {
            internalErrorListener?.onAudioSinkUnderrun(
                bufferSize,
                bufferSizeMs,
                elapsedSinceLastFeedMs
            )
            analyticsCollector.onAudioSinkUnderrun(bufferSize, bufferSizeMs, elapsedSinceLastFeedMs)
        }

        override fun onVideoEnabled(counters: DecoderCounters) {
            analyticsCollector.onVideoEnabled(counters)
        }

        override fun onVideoDisabled(counters: DecoderCounters) {
            analyticsCollector.onVideoDisabled(counters)
        }

        override fun onVideoDecoderInitialized(
            decoderName: String,
            initializedTimestampMs: Long,
            initializationDurationMs: Long
        ) {
            analyticsCollector.onVideoDecoderInitialized(
                decoderName,
                initializedTimestampMs,
                initializationDurationMs
            )
        }

        override fun onVideoInputFormatChanged(format: Format) {
            analyticsCollector.onVideoInputFormatChanged(format)
        }

        override fun onDroppedFrames(count: Int, elapsedMs: Long) {
            analyticsCollector.onDroppedFrames(count, elapsedMs)
        }

        override fun onVideoSizeChanged(
            width: Int,
            height: Int,
            unappliedRotationDegrees: Int,
            pixelWidthHeightRatio: Float
        ) {
            for (listener in listeners) {
                listener.onVideoSizeChanged(
                    width,
                    height,
                    unappliedRotationDegrees,
                    pixelWidthHeightRatio
                )
            }
            analyticsCollector.onVideoSizeChanged(
                width,
                height,
                unappliedRotationDegrees,
                pixelWidthHeightRatio
            )
        }

        override fun onRenderedFirstFrame(surface: Surface?) {
            analyticsCollector.onRenderedFirstFrame(surface)
        }

        override fun onMetadata(metadata: Metadata) {
            metadataListener?.onMetadata(metadata)
            analyticsCollector.onMetadata(metadata)
        }

        override fun onCues(cues: List<Cue>) {
            captionListener?.onCues(cues)
        }
    }

    companion object {
        private const val TAG = "ExoMediaPlayer"
        private const val BUFFER_REPEAT_DELAY = 1000
        private const val WAKE_LOCK_TIMEOUT = 1000
    }


}