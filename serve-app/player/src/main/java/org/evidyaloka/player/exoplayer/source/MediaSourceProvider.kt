package org.evidyaloka.player.exoplayer.source

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Handler
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.TransferListener
import org.evidyaloka.player.BuildConfig
import org.evidyaloka.player.exoplayer.ExoMedia
import org.evidyaloka.player.exoplayer.source.builder.DefaultMediaSourceBuilder
import org.evidyaloka.player.exoplayer.source.builder.MediaSourceBuilder
import org.evidyaloka.player.exoplayer.util.MediaSourceUtil

/**
 * Provides the functionality to determine which [MediaSource] should be used
 * to play a particular URL.
 */
class MediaSourceProvider {
    @NonNull
    @SuppressLint("DefaultLocale")
    protected var userAgent = java.lang.String.format(
        USER_AGENT_FORMAT,
        BuildConfig.VersionName,
        BuildConfig.VersionCode,
        Build.VERSION.RELEASE,
        Build.MODEL
    )

    @NonNull
    fun generate(
        @NonNull context: Context,
        @NonNull handler: Handler,
        @NonNull uri: Uri,
        @Nullable transferListener: TransferListener
    ): MediaSource {
        val sourceTypeBuilder =
            findByProviders(uri)

        // If a registered builder wasn't found then use the default
        val builder: MediaSourceBuilder =
            sourceTypeBuilder?.builder ?: DefaultMediaSourceBuilder()
        return builder.build(context, uri, userAgent, handler, transferListener)
    }

    class SourceTypeBuilder(
        @NonNull builder: MediaSourceBuilder,
        @Nullable uriScheme: String?,
        @Nullable extension: String?,
        @Nullable looseComparisonRegex: String?
    ) {
        @NonNull
        val builder: MediaSourceBuilder

        @Nullable
        val extension: String?

        @Nullable
        val uriScheme: String?

        @Nullable
        val looseComparisonRegex: String?

        @Deprecated("Use {@link #SourceTypeBuilder(MediaSourceBuilder, String, String, String)}")
        constructor(
            @NonNull builder: MediaSourceBuilder,
            @NonNull extension: String?,
            @Nullable looseComparisonRegex: String?
        ) : this(builder, null, extension, looseComparisonRegex) {
        }

        init {
            this.builder = builder
            this.uriScheme = uriScheme
            this.extension = extension
            this.looseComparisonRegex = looseComparisonRegex
        }
    }

    companion object {
        protected const val USER_AGENT_FORMAT = "ExoMedia %s (%d) / Android %s / %s"

        @Nullable
        protected fun findByProviders(@NonNull uri: Uri): SourceTypeBuilder? {
            // Uri Scheme (e.g. rtsp)
            var sourceTypeBuilder =
                findByScheme(uri)
            if (sourceTypeBuilder != null) {
                return sourceTypeBuilder
            }

            // Extension
            sourceTypeBuilder = findByExtension(uri)
            if (sourceTypeBuilder != null) {
                return sourceTypeBuilder
            }

            // Regex
            sourceTypeBuilder = findByLooseComparison(uri)
            return sourceTypeBuilder
        }

        @Nullable
        protected fun findByScheme(@NonNull uri: Uri): SourceTypeBuilder? {
            val scheme = uri.scheme
            if (scheme == null || scheme.isEmpty()) {
                return null
            }
            for (builder in ExoMedia.Data.sourceTypeBuilders) {
                if (builder.uriScheme != null && builder.uriScheme.equals(
                        scheme,
                        ignoreCase = true
                    )
                ) {
                    return builder
                }
            }
            return null
        }

        @Nullable
        protected fun findByExtension(@NonNull uri: Uri?): SourceTypeBuilder? {
            val extension: String? = uri?.let { MediaSourceUtil.getExtension(it) }
            if (extension == null || extension.isEmpty()) {
                return null
            }
            for (builder in ExoMedia.Data.sourceTypeBuilders) {
                if (builder.extension != null && builder.extension.equals(
                        extension,
                        ignoreCase = true
                    )
                ) {
                    return builder
                }
            }
            return null
        }

        @Nullable
        protected fun findByLooseComparison(@NonNull uri: Uri): SourceTypeBuilder? {
            for (builder in ExoMedia.Data.sourceTypeBuilders) {
                if (builder.looseComparisonRegex != null && uri.toString()
                        .matches(builder.looseComparisonRegex.toRegex())
                ) {
                    return builder
                }
            }
            return null
        }
    }
}