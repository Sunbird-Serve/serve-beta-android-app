package org.evidyaloka.player.exoplayer.listener

import com.google.android.exoplayer2.metadata.Metadata

/**
 * A listener for receiving ID3 metadata parsed from the media stream.
 */
interface MetadataListener {
    /**
     * Called each time there is a metadata associated with current playback time.
     *
     * @param metadata The metadata.
     */
    fun onMetadata(metadata: Metadata?)
}