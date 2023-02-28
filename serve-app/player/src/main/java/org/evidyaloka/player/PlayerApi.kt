package org.evidyaloka.player

/**
 * @author Madhankumar
 * created on 31-03-2021
 *
 */
interface PlayerApi {
    /**
     * Loads and automatically plays the video.
     * @param uri id of the video
     * @param startSeconds the time from which the video should start playing
     */
    fun loadVideo(uri: String, startSeconds: Float)

    /**
     * Loads the video's thumbnail and prepares the player to play the video. Does not automatically play the video.
     * @param uri id of the video
     * @param startSeconds the time from which the video should start playing
     */
    fun cueVideo(uri: String, startSeconds: Float)

    fun play()
    fun pause()

    /**
     *
     * @param time The absolute time in seconds to seek to
     */
    fun seekTo(time: Float)

    fun release()

    fun setFullScreenListener(listener: FullScreenListener)
    fun setPlayerEventListner(listner: PlayerListener)

    val duration: Long
    val currentPosition: Long

}