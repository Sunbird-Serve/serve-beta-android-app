package org.evidyaloka.player.exoplayer.listener

/**
 * Interface definition of a callback to be invoked indicating
 * the completion of a seek operation.
 */
interface OnSeekCompletionListener {
    /**
     * Called to indicate the completion of a seek operation.
     */
    fun onSeekComplete()
}