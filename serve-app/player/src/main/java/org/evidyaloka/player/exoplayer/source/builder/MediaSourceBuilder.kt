package org.evidyaloka.player.exoplayer.source.builder;

import android.content.Context
import android.net.Uri
import android.os.Handler
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.TransferListener
import org.evidyaloka.player.exoplayer.ExoMedia


public abstract class MediaSourceBuilder {

    @NonNull
    public abstract fun build(
        @NonNull context: Context,
        @NonNull uri: Uri,
        @NonNull userAgent: String,
        @NonNull handler: Handler,
        @Nullable transferListener: TransferListener
    ): MediaSource

    @NonNull
    protected fun buildDataSourceFactory(
        @NonNull context: Context,
        @NonNull userAgent: String,
        @Nullable listener: TransferListener?
    ):DataSource.Factory  {
        val provider: ExoMedia.DataSourceFactoryProvider? = ExoMedia.Data.dataSourceFactoryProvider
        var dataSourceFactory:DataSource.Factory?  = provider?.provide(userAgent, listener)

        // Handles the deprecated httpDataSourceFactoryProvider
        if (dataSourceFactory == null) {
            val httpProvider: ExoMedia.HttpDataSourceFactoryProvider?  = ExoMedia.Data.httpDataSourceFactoryProvider;
            dataSourceFactory = httpProvider?.provide(userAgent, listener)
        }

        // If no factory was provided use the default one
        if (dataSourceFactory == null) {
            dataSourceFactory = DefaultHttpDataSourceFactory(userAgent, listener);
        }

        return DefaultDataSourceFactory(context, listener, dataSourceFactory);
    }
}
