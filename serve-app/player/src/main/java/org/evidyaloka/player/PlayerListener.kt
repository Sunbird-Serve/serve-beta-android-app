package org.evidyaloka.player

/**
 * @author Madhankumar
 * created on 05-04-2021
 *
 */
interface PlayerListener {
    fun onError(playerState:PlayerError)
    fun onPlayeBackEvent(playbackState: PlayerState)
}

enum class PlayerState {
    UNKNOWN, UNSTARTED, ENDED, PLAYING, PAUSED, BUFFERING, VIDEO_CUED
}
enum class PlayerError {
    UNKNOWN, INVALID_PARAMETER_IN_REQUEST, VIDEO_NOT_FOUND, VIDEO_NOT_PLAYABLE, VIDEO_NOT_PLAYABLE_EMBEDED, OUT_OF_MEMORY
}