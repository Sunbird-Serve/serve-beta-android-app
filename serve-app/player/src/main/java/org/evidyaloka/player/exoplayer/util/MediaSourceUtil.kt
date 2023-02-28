package org.evidyaloka.player.exoplayer.util

import android.net.Uri
import androidx.annotation.NonNull
import androidx.annotation.Nullable

/**
 * A utility to handle the checks and comparisons when determining
 * the format for a media item.
 */
object MediaSourceUtil {
    @Nullable
    fun getExtension(@NonNull uri: Uri): String? {
        var path: String? = uri.lastPathSegment ?: return null
        var periodIndex = path?.lastIndexOf('.')
        if (periodIndex == -1 && uri.pathSegments.size > 1) {
            //Checks the second to last segment to handle manifest urls (e.g. "TearsOfSteelTeaser.ism/manifest")
            path = uri.pathSegments[uri.pathSegments.size - 2]
            periodIndex = path.lastIndexOf('.')
        }

        //If there is no period, prepend one to the last segment in case it is the extension without a period
        if (periodIndex == -1) {
            periodIndex = 0
            path = "." + uri.lastPathSegment
        }
        val rawExtension = periodIndex?.let { path!!.substring(it) }
        return rawExtension?.toLowerCase()
    }
}