package org.evidyaloka.player.exoplayer

import androidx.annotation.NonNull
import com.google.android.exoplayer2.Timeline

class WindowInfo(
    val previousWindowIndex: Int,
    val currentWindowIndex: Int,
    val nextWindowIndex: Int,
    @field:NonNull @param:NonNull val currentWindow: Timeline.Window
) 