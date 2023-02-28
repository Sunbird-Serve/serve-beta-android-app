package org.evidyaloka.player.exoplayer.renderer

import android.content.Context
import android.os.Handler
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.google.android.exoplayer2.Renderer
import com.google.android.exoplayer2.audio.AudioCapabilities
import com.google.android.exoplayer2.audio.AudioRendererEventListener
import com.google.android.exoplayer2.audio.MediaCodecAudioRenderer
import com.google.android.exoplayer2.drm.DrmSessionManager
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector
import com.google.android.exoplayer2.metadata.MetadataDecoderFactory
import com.google.android.exoplayer2.metadata.MetadataOutput
import com.google.android.exoplayer2.metadata.MetadataRenderer
import com.google.android.exoplayer2.text.TextOutput
import com.google.android.exoplayer2.text.TextRenderer
import com.google.android.exoplayer2.video.MediaCodecVideoRenderer
import com.google.android.exoplayer2.video.VideoRendererEventListener
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.evidyaloka.player.exoplayer.ExoMedia
import java.util.*

/**
 * Provides all the necessary [Renderer]s
 */
class RendererProvider(
    @field:NonNull @param:NonNull protected var context: Context,
    @field:NonNull @param:NonNull protected var handler: Handler?,
    @NonNull captionListener: TextOutput,
    @NonNull metadataListener: MetadataOutput,
    @NonNull audioRendererEventListener: AudioRendererEventListener,
    @NonNull videoRendererEventListener: VideoRendererEventListener
) {

    @NonNull
    protected var captionListener: TextOutput

    @NonNull
    protected var metadataListener: MetadataOutput

    @NonNull
    protected var audioRendererEventListener: AudioRendererEventListener

    @NonNull
    protected var videoRendererEventListener: VideoRendererEventListener

    @Nullable
    protected var mdrmSessionManager: DrmSessionManager<FrameworkMediaCrypto>? = null
    protected var droppedFrameNotificationAmount = 50; private set
    protected var videoJoiningTimeMs = 5000; private set
    fun setDrmSessionManager(@Nullable drmSessionManager: DrmSessionManager<FrameworkMediaCrypto>?) {
        this.mdrmSessionManager = drmSessionManager
    }

    fun setDroppedFrameNotificationAmount(droppedFrameNotificationAmount: Int) {
        this.droppedFrameNotificationAmount = droppedFrameNotificationAmount
    }

    fun setVideoJoiningTimeMs(videoJoiningTimeMs: Int) {
        this.videoJoiningTimeMs = videoJoiningTimeMs
    }

    @NonNull
    fun generate(): MutableList<Renderer>? {
        val renderers: MutableList<Renderer> = ArrayList<Renderer>()
        renderers.addAll(buildAudioRenderers()!!)
        renderers.addAll(buildVideoRenderers()!!)
        renderers.addAll(buildCaptionRenderers()!!)
        renderers.addAll(buildMetadataRenderers()!!)
        return renderers
    }

    @NonNull
    protected fun buildAudioRenderers(): MutableList<Renderer>? {
        val renderers: MutableList<Renderer> = ArrayList<Renderer>()
        renderers.add(
            MediaCodecAudioRenderer(
                context,
                MediaCodecSelector.DEFAULT,
                mdrmSessionManager,
                true,
                handler,
                audioRendererEventListener,
                AudioCapabilities.getCapabilities(context)
            )
        )

        // Adds any registered classes
        val classNames: MutableList<String>? =
            ExoMedia.Data.registeredRendererClasses.get(ExoMedia.RendererType.AUDIO)
        if (classNames != null) {
            for (className in classNames) {
                try {
                    val clazz = Class.forName(className)
                    val constructor = clazz.getConstructor(
                        Handler::class.java, AudioRendererEventListener::class.java
                    )
                    val renderer: Renderer =
                        constructor.newInstance(handler, audioRendererEventListener) as Renderer
                    renderers.add(renderer)
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    // Purposefully left blank
                }
            }
        }
        return renderers
    }

    @NonNull
    protected fun buildVideoRenderers(): MutableList<Renderer>? {
        val renderers: MutableList<Renderer> = ArrayList<Renderer>()
        renderers.add(
            MediaCodecVideoRenderer(
                context,
                MediaCodecSelector.DEFAULT,
                videoJoiningTimeMs.toLong(),
                mdrmSessionManager,
                false,
                handler,
                videoRendererEventListener,
                droppedFrameNotificationAmount
            )
        )

        // Adds any registered classes
        val classNames: MutableList<String>? =
            ExoMedia.Data.registeredRendererClasses.get(ExoMedia.RendererType.VIDEO)
        if (classNames != null) {
            for (className in classNames) {
                try {
                    val clazz = Class.forName(className)
                    val constructor = clazz.getConstructor(
                        Boolean::class.javaPrimitiveType,
                        Long::class.javaPrimitiveType,
                        Handler::class.java,
                        VideoRendererEventListener::class.java,
                        Int::class.javaPrimitiveType
                    )
                    val renderer: Renderer = constructor.newInstance(
                        true,
                        videoJoiningTimeMs,
                        handler,
                        videoRendererEventListener,
                        droppedFrameNotificationAmount
                    ) as Renderer
                    renderers.add(renderer)
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    // Purposefully left blank
                }
            }
        }
        return renderers
    }

    @NonNull
    protected fun buildCaptionRenderers(): MutableList<Renderer>? {
        val renderers: MutableList<Renderer> = ArrayList<Renderer>()
        renderers.add(TextRenderer(captionListener, handler!!.looper))
        return renderers
    }

    @NonNull
    protected fun buildMetadataRenderers(): MutableList<Renderer>? {
        val renderers: MutableList<Renderer> = ArrayList<Renderer>()
        renderers.add(
            MetadataRenderer(
                metadataListener,
                handler!!.looper,
                MetadataDecoderFactory.DEFAULT
            )
        )
        return renderers
    }

    init {
        this.captionListener = captionListener
        this.metadataListener = metadataListener
        this.audioRendererEventListener = audioRendererEventListener
        this.videoRendererEventListener = videoRendererEventListener
    }
}