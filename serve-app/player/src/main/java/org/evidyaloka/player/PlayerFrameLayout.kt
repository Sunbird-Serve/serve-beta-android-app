package org.evidyaloka.player

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import org.evidyaloka.player.exoplayer.ExoMediaPlayer
import org.evidyaloka.player.exoplayer.SimpleExoPlayer
import org.evidyaloka.player.exoplayer.listener.ExoPlayerListener
import org.evidyaloka.player.youtube.PlayerConstants
import org.evidyaloka.player.youtube.WebViewYouTubePlayer
import org.evidyaloka.player.youtube.YouTubePlayer
import org.evidyaloka.player.youtube.YouTubePlayerListener
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @author Madhankumar
 * created on 01-04-2021
 *
 */
class PlayerFrameLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
): FrameLayout(context, attrs, defStyleAttr),LifecycleObserver {

    private val S3 = "s3"
    private val YOUTUBE = "youtube"

    private var playerView: PlayerApi? = null
    private var fullScreenListener: FullScreenListener? = null
    private var playerListener: PlayerListener? = null

    fun setUrl(uri: String, contentHost:String, startSeconds: Float){
        initiateView(uri, contentHost, startSeconds)
    }

    private fun initiateView(uri: String, contentHost:String, startSeconds: Float){
        playerView?.pause()
        when(contentHost) {
            S3 -> {
                if(playerView is SimpleExoPlayer){
                    playerView?.loadVideo(uri, startSeconds)
                }else {
                    playerView?.release()
                    removeAllViews()
                    playerView = SimpleExoPlayer(context)
                    playerView?.loadVideo(uri, startSeconds)
                    (playerView as SimpleExoPlayer).resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    addView(playerView as View)

                }
            }
            YOUTUBE -> {
                var youtubeID = extractYTId(uri)
                youtubeID?.let {
                    if(playerView is WebViewYouTubePlayer){
                        playerView?.loadVideo(youtubeID, startSeconds)
                    }else {
                        playerView?.release()
                        removeAllViews()
                        playerView = WebViewYouTubePlayer(context, youtubeID, startSeconds)
                        addView(playerView as View)
                    }
                }
            }
        }
        playerListener?.let { playerView?.setPlayerEventListner(it) }
        fullScreenListener?.let { playerView?.setFullScreenListener(it) }
    }

    fun setFullScreenListener(listener: FullScreenListener) {
        fullScreenListener = listener
    }

    fun setPlayerEventListner(listener: PlayerListener) {
        playerListener = listener
    }


    fun play(){
        playerView?.play()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pause(){
        playerView?.pause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop(){
        playerView?.pause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun release(){
        playerView?.release()
        playerView?.let {
            removeView(it as View)
        }
    }

    val duration: Long?
    get() = playerView?.duration

    val currentPosition: Long?
        get() = playerView?.currentPosition

    private fun extractYTId(ytUrl: String?): String? {
        var vId: String? = null
        val pattern: Pattern = Pattern.compile(
            "^(?:http|https)?(?:://)?(?:www\\.*youtube\\.com|www\\.*youtube-nocookie\\.com|youtu\\.?be).*?(?:v=|v/|embed/)(.{11})?(?:.*|\$)|^(?:http|https)?(?:://)?youtu(?:.*\\/v\\/|.*v\\=|\\.be\\/)([A-Za-z0-9_\\-]{11})"
        )
        val matcher: Matcher = pattern.matcher(ytUrl)
        if (matcher.matches()) {
            for(i in 1..matcher.groupCount()){
                 matcher.group(i)?.let {
                     vId = it
                }
            }
        }
       return vId
    }

}

//fun main(){
//    val url = "https://www.youtube.com/watch?v=_8rhIkApe1k&list=PLoj-HZxkBkxFZJSmd5XuVb3Vk-RBUjjoN&index=1"
//    var vId: String? = null
//    val pattern: Pattern = Pattern.compile(
//        "^(?:http|https)?(?:://)?(?:www\\.*youtube\\.com|www\\.*youtube-nocookie\\.com|youtu\\.?be).*?(?:v=|v/|embed/)(.{11})?(?:.*|$)|^(?:http|https)?(?:://)?youtu(?:.*\\/v\\/|.*v\\=|\\.be\\/)([A-Za-z0-9_\\-]{11})"
//    )
//    val matcher: Matcher = pattern.matcher(url)
//    if (matcher.matches()) {
//        for(i in 1..matcher.groupCount()){
//            matcher.group(i)?.let {
//                vId = it
//            }
//        }
//    }
//    vId?.let {
//        Log.e("Youtube video Id - ", it)
//    }
//}
//https://www.youtube.com/watch?v=_8rhIkApe1k&list=PLoj-HZxkBkxFZJSmd5XuVb3Vk-RBUjjoN&index=1