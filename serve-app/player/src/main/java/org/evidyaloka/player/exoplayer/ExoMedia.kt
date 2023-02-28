package org.evidyaloka.player.exoplayer

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.google.android.exoplayer2.LoadControl
import com.google.android.exoplayer2.Renderer
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.HttpDataSource.BaseFactory
import com.google.android.exoplayer2.upstream.TransferListener
import org.evidyaloka.player.exoplayer.source.MediaSourceProvider
import org.evidyaloka.player.exoplayer.source.builder.DashMediaSourceBuilder
import org.evidyaloka.player.exoplayer.source.builder.HlsMediaSourceBuilder
import org.evidyaloka.player.exoplayer.source.builder.SsMediaSourceBuilder
import java.util.*

/**
 * A standard entry point for registering additional [Renderer]s and
 * [com.google.android.exoplayer2.source.MediaSource]s
 */
object ExoMedia {
    /**
     * Registers additional customized [Renderer]s
     * that will be used by the [com.google.android.exoplayer2.source.MediaSource]s to
     * correctly play media.
     *
     * @param type The type for the renderer
     * @param clazz The class of the customized Renderer
     */
    fun registerRenderer(
        @NonNull type: RendererType?,
        @NonNull clazz: Class<out Renderer?>
    ) {
        Data.registeredRendererClasses[type]?.add(clazz.name)
    }

    /**
     * Registers additional [com.google.android.exoplayer2.source.MediaSource]s for the specified file
     * extensions (and regexes). [com.google.android.exoplayer2.source.MediaSource]s registered here will take
     * precedence to the pre-configured ones.
     *
     * @param builder The builder for additional or customized media sources
     */
    fun registerMediaSourceBuilder(@NonNull builder: MediaSourceProvider.SourceTypeBuilder) {
        Data.sourceTypeBuilders.add(0, builder)
    }

    /**
     * Specifies the provider to use when building [HttpDataSource.BaseFactory]
     * instances for use with the [com.devbrackets.android.exomedia.core.source.builder.MediaSourceBuilder]s. This will
     * only be used for builders that haven't customized the [com.devbrackets.android.exomedia.core.source.builder.MediaSourceBuilder.buildDataSourceFactory]
     * method.
     *
     * @param provider The provider to use for the [com.devbrackets.android.exomedia.core.source.builder.MediaSourceBuilder]s
     */
    @Deprecated("Use {@link #setDataSourceFactoryProvider(DataSourceFactoryProvider)} instead as it is more permissive")
    fun setHttpDataSourceFactoryProvider(@Nullable provider: HttpDataSourceFactoryProvider?) {
        Data.httpDataSourceFactoryProvider =
            provider
    }

    /**
     * Specifies the provider to use when building [DataSource.Factory]
     * instances for use with the [com.devbrackets.android.exomedia.core.source.builder.MediaSourceBuilder]s. This will
     * only be used for builders that haven't customized the [com.devbrackets.android.exomedia.core.source.builder.MediaSourceBuilder.buildDataSourceFactory]
     * method.
     *
     * @param provider The provider to use for the [com.devbrackets.android.exomedia.core.source.builder.MediaSourceBuilder]s
     */
    fun setDataSourceFactoryProvider(@Nullable provider: DataSourceFactoryProvider?) {
        Data.dataSourceFactoryProvider = provider
    }

    /**
     * Specifies the [LoadControl] to use when building the [com.google.android.exoplayer2.ExoPlayer] instance
     * used in the [com.devbrackets.android.exomedia.ui.widget.VideoView] and [AudioPlayer]. This allows the
     * buffering amounts to be modified to better suit your needs which can be easily specified by using an instance of
     * [com.google.android.exoplayer2.DefaultLoadControl]. When the `loadControl` is `null`
     * the default instance of the [com.google.android.exoplayer2.DefaultLoadControl] will be used. This will only
     * take effect for any instances created *after* this was set.
     *
     * @param loadControl The [LoadControl] to use for any new [com.google.android.exoplayer2.ExoPlayer] instances
     */
    fun setLoadControl(@Nullable loadControl: LoadControl?) {
        Data.loadControl = loadControl
    }

    @Deprecated("Use {@link DataSourceFactoryProvider} instead")
    interface HttpDataSourceFactoryProvider {
        @NonNull
        fun provide(
            @NonNull userAgent: String?,
            @Nullable listener: TransferListener?
        ): BaseFactory?
    }

    interface DataSourceFactoryProvider {
        @NonNull
        fun provide(
            @NonNull userAgent: String?,
            @Nullable listener: TransferListener?
        ): DataSource.Factory?
    }

    enum class RendererType {
        AUDIO, VIDEO, CLOSED_CAPTION, METADATA
    }

    object Data {
        @NonNull
        val registeredRendererClasses: MutableMap<RendererType, MutableList<String>> =
            mutableMapOf()

        @NonNull
        val sourceTypeBuilders: MutableList<MediaSourceProvider.SourceTypeBuilder> =
            ArrayList<MediaSourceProvider.SourceTypeBuilder>()

        @Nullable
        @Deprecated("")
        @Volatile
        var httpDataSourceFactoryProvider: HttpDataSourceFactoryProvider? = null

        @Nullable
        @Volatile
        var dataSourceFactoryProvider: DataSourceFactoryProvider? = null

        @Nullable
        @Volatile
        var loadControl: LoadControl? = null

        @NonNull
        @Volatile
        var mediaSourceProvider: MediaSourceProvider = MediaSourceProvider()
        private fun instantiateRendererClasses() {
            // Instantiates the required values
            registeredRendererClasses[RendererType.AUDIO] = LinkedList()
            registeredRendererClasses[RendererType.VIDEO] = LinkedList()
            registeredRendererClasses[RendererType.CLOSED_CAPTION] = LinkedList()
            registeredRendererClasses[RendererType.METADATA] = LinkedList()

            // Adds the ExoPlayer extension library renderers
            val audioClasses =
                registeredRendererClasses[RendererType.AUDIO]
            audioClasses!!.add("com.google.android.exoplayer2.ext.opus.LibopusAudioRenderer")
            audioClasses.add("com.google.android.exoplayer2.ext.flac.LibflacAudioRenderer")
            audioClasses.add("com.google.android.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer")
            val videoClasses =
                registeredRendererClasses[RendererType.VIDEO]
            videoClasses!!.add("com.google.android.exoplayer2.ext.vp9.LibvpxVideoRenderer")
        }

        private fun instantiateSourceProviders() {
            // Adds the HLS, SmoothStream, and MPEG Dash registrations
            sourceTypeBuilders.add(
                MediaSourceProvider.SourceTypeBuilder(
                    HlsMediaSourceBuilder(),
                    null,
                    ".m3u8",
                    ".*\\.m3u8.*"
                )
            )
            sourceTypeBuilders.add(
                MediaSourceProvider.SourceTypeBuilder(
                    DashMediaSourceBuilder(),
                    null,
                    ".mpd",
                    ".*\\.mpd.*"
                )
            )
            sourceTypeBuilders.add(
                MediaSourceProvider.SourceTypeBuilder(
                    SsMediaSourceBuilder(),
                    null,
                    ".ism",
                    ".*\\.ism.*"
                )
            )
        }

        init {
            instantiateRendererClasses()
            instantiateSourceProviders()
        }
    }
}