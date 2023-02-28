package org.evidyaloka.player.youtube

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import org.evidyaloka.player.*
import java.util.*
import kotlin.collections.HashSet

/**
 * @author Madhankumar
 * created on 23-03-2021
 *
 */
open class WebViewYouTubePlayer constructor(
    context: Context, videoId:String,startSeconds: Float, attrs: AttributeSet? = null, defStyleAttr: Int = 0
): WebView(context, attrs, defStyleAttr), YouTubePlayer, YouTubePlayerBridge.YouTubePlayerBridgeCallbacks,
    PlayerApi {

    private var mPlayerListner:YouTubePlayerListener? = null
    constructor(context: Context,videoId:String,startSeconds: Float,listner:YouTubePlayerListener):this(context,videoId, startSeconds){
        mPlayerListner = listner
    }

    private lateinit var youTubePlayerInitListener: (YouTubePlayer) -> Unit
    private var playerEventListener: PlayerListener? = null
    private var fullScreenListener: FullScreenListener? = null

    private val youTubePlayerListeners = HashSet<YouTubePlayerListener>()
    private val mainThreadHandler: Handler = Handler(Looper.getMainLooper())

    internal var isBackgroundPlaybackEnabled = false

    init {
        val iFramePlayerOptions = IFramePlayerOptions.Builder().controls(1).build()
        initialize({it.addListener(playerListener)},iFramePlayerOptions)
        initFullscreenDialog()
    }

    internal fun initialize(initListener: (YouTubePlayer) -> Unit, playerOptions: IFramePlayerOptions?) {
        youTubePlayerInitListener = initListener
        initWebView(playerOptions ?: IFramePlayerOptions.default)
    }

    override fun onYouTubeIFrameAPIReady() = youTubePlayerInitListener(this)

    override fun getInstance(): YouTubePlayer = this

    override fun loadVideo(videoId: String, startSeconds: Float) {
        mainThreadHandler.post { loadUrl("javascript:loadVideo('$videoId', $startSeconds)") }
    }

    override fun cueVideo(videoId: String, startSeconds: Float) {
        mainThreadHandler.post { loadUrl("javascript:cueVideo('$videoId', $startSeconds)") }
    }

    override fun play() {
        mainThreadHandler.post { loadUrl("javascript:playVideo()") }
    }

    override fun pause() {
        mainThreadHandler.post { loadUrl("javascript:pauseVideo()") }
    }

    override fun mute() {
        mainThreadHandler.post { loadUrl("javascript:mute()") }
    }

    override fun unMute() {
        mainThreadHandler.post { loadUrl("javascript:unMute()") }
    }

    override fun setVolume(volumePercent: Int) {
        require(!(volumePercent < 0 || volumePercent > 100)) { "Volume must be between 0 and 100" }

        mainThreadHandler.post { loadUrl("javascript:setVolume($volumePercent)") }
    }

    override fun seekTo(time: Float) {
        mainThreadHandler.post { loadUrl("javascript:seekTo($time)") }
    }

    override fun release() {
        this.destroy()
    }

    override fun setFullScreenListener(listener: FullScreenListener) {
        fullScreenListener = listener
    }

    override fun destroy() {
        removeAllViews()
        youTubePlayerListeners.clear()
        mainThreadHandler.removeCallbacksAndMessages(null)
        super.destroy()
    }

    override fun getListeners(): Collection<YouTubePlayerListener> {
        return Collections.unmodifiableCollection(HashSet(youTubePlayerListeners))
    }

    override fun addListener(listener: YouTubePlayerListener): Boolean {
        return youTubePlayerListeners.add(listener)
    }

    override fun removeListener(listener: YouTubePlayerListener): Boolean {
        return youTubePlayerListeners.remove(listener)
    }

    override var duration: Long = 0
    override var currentPosition: Long = 0

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(playerOptions: IFramePlayerOptions) {
        settings.javaScriptEnabled = true
        settings.mediaPlaybackRequiresUserGesture = false
        settings.cacheMode = WebSettings.LOAD_NO_CACHE

        addJavascriptInterface(YouTubePlayerBridge(this), "YouTubePlayerBridge")

        val htmlPage = Utils
            .readHTMLFromUTF8File(resources.openRawResource(R.raw.ayp_youtube_player))
            .replace("<<injectedPlayerVars>>", playerOptions.toString())

        loadDataWithBaseURL(playerOptions.getOrigin(), htmlPage, "text/html", "utf-8", null)

        // if the video's thumbnail is not in memory, show a black screen
        webChromeClient = WebViewChromeClient()
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        if (isBackgroundPlaybackEnabled && (visibility == View.GONE || visibility == View.INVISIBLE))
            return

        super.onWindowVisibilityChanged(visibility)
    }

    override fun setPlayerEventListner(listner: PlayerListener) {
        playerEventListener = listner
    }

    private val playerListener= object: YouTubePlayerListener {
        override fun onApiChange(youTubePlayer: YouTubePlayer) {

        }

        /**
         * Called periodically by the player, the argument is the number of seconds that have been played.
         * @param second current second of the playback
         */
        override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
            currentPosition = second.toLong() * 1000
        }

        /**
         * Called when an error occurs in the player. Check [PlayerConstants.PlayerError] to see all the possible values.
         * @param error a state from [PlayerConstants.PlayerError]
         */
        override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
            playerEventListener?.onError(when (error){
                PlayerConstants.PlayerError.VIDEO_NOT_FOUND -> PlayerError.VIDEO_NOT_FOUND
                PlayerConstants.PlayerError.INVALID_PARAMETER_IN_REQUEST -> PlayerError.INVALID_PARAMETER_IN_REQUEST
                PlayerConstants.PlayerError.VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER -> PlayerError.VIDEO_NOT_PLAYABLE_EMBEDED
                PlayerConstants.PlayerError.HTML_5_PLAYER -> PlayerError.VIDEO_NOT_PLAYABLE
                PlayerConstants.PlayerError.UNKNOWN -> PlayerError.UNKNOWN
            })
        }

        /**
         * Called every time the quality of the playback changes. Check [PlayerConstants.PlaybackQuality] to see all the possible values.
         * @param playbackQuality a state from [PlayerConstants.PlaybackQuality]
         */
        override fun onPlaybackQualityChange(
            youTubePlayer: YouTubePlayer,
            playbackQuality: PlayerConstants.PlaybackQuality
        ) {

        }

        /**
         * Called every time the speed of the playback changes. Check [PlayerConstants.PlaybackRate] to see all the possible values.
         * @param playbackRate a state from [PlayerConstants.PlaybackRate]
         */
        override fun onPlaybackRateChange(
            youTubePlayer: YouTubePlayer,
            playbackRate: PlayerConstants.PlaybackRate
        ) {

        }

        /**
         * Called when the player is ready to play videos. You should start using with the player only after this method is called.
         * @param youTubePlayer The [YouTubePlayer] object.
         */
        override fun onReady(youTubePlayer: YouTubePlayer) {
            videoId?.let {
                youTubePlayer.loadVideo(videoId, startSeconds)
            }
        }

        /**
         * Called every time the state of the player changes. Check [PlayerConstants.PlayerState] to see all the possible states.
         * @param state a state from [PlayerConstants.PlayerState]
         */
        override fun onStateChange(
            youTubePlayer: YouTubePlayer,
            state: PlayerConstants.PlayerState
        ) {
            playerEventListener?.onPlayeBackEvent(
                when (state) {
                    PlayerConstants.PlayerState.PLAYING -> {
                        this@WebViewYouTubePlayer.keepScreenOn = true
                        PlayerState.PLAYING
                    }
                    PlayerConstants.PlayerState.BUFFERING -> {
                        PlayerState.UNKNOWN

                    }
                    else -> {
                        this@WebViewYouTubePlayer.keepScreenOn = false
                        PlayerState.UNKNOWN
                    }
                }
            )
        }

        /**
         * Called when the total duration of the video is loaded. <br></br><br></br>
         * Note that getDuration() will return 0 until the video's metadata is loaded, which normally happens just after the video starts playing.
         * @param duration total duration of the video
         */
        override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
            this@WebViewYouTubePlayer.duration = duration.toLong() * 1000
        }

        /**
         * Called when the id of the current video is loaded
         * @param videoId the id of the video being played
         */
        override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {

        }

        /**
         * Called periodically by the player, the argument is the percentage of the video that has been buffered.
         * @param loadedFraction a number between 0 and 1 that represents the percentage of the video that has been buffered.
         */
        override fun onVideoLoadedFraction(youTubePlayer: YouTubePlayer, loadedFraction: Float) {

        }
    }
    private var mFullScreenDialog: Dialog? = null
    private var mExoPlayerFullscreen = false
    private var parentView:FrameLayout? = null
    private fun initFullscreenDialog() {
        mFullScreenDialog =
            object : Dialog(this.context, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
                override fun onBackPressed() {
                    if (mExoPlayerFullscreen) closeFullscreenDialog()
                    super.onBackPressed()
                }
            }
    }

    private fun openFullscreenDialog(view: View) {
        parentView = (this.parent as FrameLayout)
        parentView?.removeView(this)
        mFullScreenDialog!!.addContentView(
            view,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        mExoPlayerFullscreen = true
        mFullScreenDialog?.show()
        fullScreenListener?.enterFullScreen()
    }


    private fun closeFullscreenDialog() {
        fullScreenListener?.exitFullScreen()
//        (this.parent as ViewGroup).removeView(this)
        parentView?.addView(
            this
        )
        mExoPlayerFullscreen = false
        mFullScreenDialog?.dismiss()
    }

    inner class WebViewChromeClient: WebChromeClient() {

        override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
            super.onShowCustomView(view, callback)
            if (view != null) {
                openFullscreenDialog(view)
            }
        }

        override fun onHideCustomView() {
            super.onHideCustomView()
            closeFullscreenDialog()
        }
    }
}