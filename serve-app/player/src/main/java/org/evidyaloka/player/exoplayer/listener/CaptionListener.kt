package org.evidyaloka.player.exoplayer.listener

import com.google.android.exoplayer2.text.Cue

/**
 * A listener for receiving notifications of timed text.
 */
interface CaptionListener {
    fun onCues(cues: List<Cue?>?)
}