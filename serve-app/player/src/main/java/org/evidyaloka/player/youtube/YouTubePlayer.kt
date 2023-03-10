package org.evidyaloka.player.youtube

/**
 * @author Madhankumar
 * created on 23-03-2021
 *
 */
interface YouTubePlayer {
    /**
     * Loads and automatically plays the video.
     * @param videoId id of the video
     * @param startSeconds the time from which the video should start playing
     */
    fun loadVideo(videoId: String, startSeconds: Float)

    /**
     * Loads the video's thumbnail and prepares the player to play the video. Does not automatically play the video.
     * @param videoId id of the video
     * @param startSeconds the time from which the video should start playing
     */
    fun cueVideo(videoId: String, startSeconds: Float)

    fun play()
    fun pause()

    fun mute()
    fun unMute()

    /**
     * @param volumePercent Integer between 0 and 100
     */
    fun setVolume(volumePercent: Int)

    /**
     *
     * @param time The absolute time in seconds to seek to
     */
    fun seekTo(time: Float)

    fun addListener(listener: YouTubePlayerListener): Boolean
    fun removeListener(listener: YouTubePlayerListener): Boolean
}