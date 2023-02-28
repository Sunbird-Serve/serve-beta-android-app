package org.evidyaloka.player.exoplayer.listener



/**
 * A listener for internal errors.
 *
 *
 * These errors are not visible to the user, and hence this listener is provided for
 * informational purposes only. Note however that an internal error may cause a fatal
 * error if the player fails to recover. If this happens, [ExoPlayerListener.onError]
 * will be invoked.
 */
interface InternalErrorListener {
    fun onAudioSinkUnderrun(
        bufferSize: Int,
        bufferSizeMs: Long,
        elapsedSinceLastFeedMs: Long
    )

    fun onDrmSessionManagerError(e: Exception?)
}