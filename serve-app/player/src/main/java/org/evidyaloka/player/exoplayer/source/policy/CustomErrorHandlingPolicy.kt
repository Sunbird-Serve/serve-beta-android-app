package org.evidyaloka.player.exoplayer.source.policy

import android.util.Log
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy
import com.google.android.exoplayer2.upstream.HttpDataSource
import java.io.IOException
import com.google.android.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException
import java.net.SocketTimeoutException


class CustomErrorHandlingPolicy(): DefaultLoadErrorHandlingPolicy() {
    override fun getBlacklistDurationMsFor(
        dataType: Int,
        loadDurationMs: Long,
        exception: IOException?,
        errorCount: Int
    ): Long {
        return if (exception is InvalidResponseCodeException
            && (exception as InvalidResponseCodeException).responseCode == 500
        ) {
            DEFAULT_TRACK_BLACKLIST_MS
        } else super.getBlacklistDurationMsFor(dataType, loadDurationMs, exception, errorCount)
    }

    override fun getRetryDelayMsFor(
        dataType: Int,
        loadDurationMs: Long,
        exception: IOException?,
        errorCount: Int
    ): Long {
        Log.e("Exoplayer custom", exception.toString())
        return if (exception?.cause is SocketTimeoutException) {
            ((1 shl Math.min(errorCount - 1, 4)) * 1000).toLong()
        } else super.getRetryDelayMsFor(dataType, loadDurationMs, exception, errorCount)
    }

    override fun getMinimumLoadableRetryCount(dataType: Int): Int {
        return super.getMinimumLoadableRetryCount(dataType)
    }

}