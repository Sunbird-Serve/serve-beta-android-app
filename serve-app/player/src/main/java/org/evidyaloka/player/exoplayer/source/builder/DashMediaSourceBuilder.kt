package org.evidyaloka.player.exoplayer.source.builder

import android.content.Context
import android.net.Uri
import android.os.Handler
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.TransferListener

class DashMediaSourceBuilder : MediaSourceBuilder() {
    @NonNull
    override fun build(
        @NonNull context: Context,
        @NonNull uri: Uri,
        @NonNull userAgent: String,
        @NonNull handler: Handler,
        @Nullable transferListener: TransferListener
    ): MediaSource {
        val dataSourceFactory: DataSource.Factory = buildDataSourceFactory(context, userAgent, null)
        val meteredDataSourceFactory: DataSource.Factory =
            buildDataSourceFactory(context, userAgent, transferListener)
        return DashMediaSource.Factory(DefaultDashChunkSource.Factory(meteredDataSourceFactory), dataSourceFactory)
            .createMediaSource(uri)
    }
}