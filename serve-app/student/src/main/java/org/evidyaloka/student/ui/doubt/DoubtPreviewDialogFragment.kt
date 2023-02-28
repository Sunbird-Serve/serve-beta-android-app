package org.evidyaloka.partner.ui.helper

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.evidyaloka.core.Constants.CommonConst
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.common.helper.loadUrlWithGlideCircle
import org.evidyaloka.core.student.model.Doubt
import org.evidyaloka.player.FullScreenListener
import org.evidyaloka.player.PlayerError
import org.evidyaloka.player.PlayerListener
import org.evidyaloka.player.PlayerState
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.LayoutImagePreviewBinding
import org.evidyaloka.common.util.FileReaderActivity
import org.evidyaloka.common.util.PdfReaderActivity

class DoubtPreviewDialogFragment private constructor(val builder: Builder) : DialogFragment() {

    private var viewType: DIALOG_TYPE = DoubtPreviewDialogFragment.DIALOG_TYPE.ALERT
    private var isDialogCancelable: Boolean = true
    private var onDismissListener: OnDismissListener? = null
    private var clickListner: View.OnClickListener? = null
    private var doubt: Doubt? = null

    private lateinit var binding: LayoutImagePreviewBinding

    init {
        viewType = builder.viewType
        isDialogCancelable = builder.isDialogCancelable
        onDismissListener = builder.onDismissListener
        clickListner = builder.clickListner
        doubt = builder.doubt
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutImagePreviewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun getTheme(): Int {
        if (DIALOG_TYPE.FULL_SCREEN.equals(viewType)) {
            return R.style.DialogTheme
        } else {
            return R.style.MaterialAlertDialogTheme
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        doubt?.let {
            isCancelable = isDialogCancelable
            try {
                if (!it.resourceType.isNullOrEmpty()) {
                    when (it.resourceType.toInt()) {
                        StudentConst.DoubtResourceType.Image.value -> {
                            binding.ivDoubt.visibility = View.VISIBLE
                            binding.tvDoubt.visibility = View.GONE
                            binding.flVideoView.visibility = View.GONE

                            var requestOptions = RequestOptions()
                            requestOptions.centerInside()
                            it.url?.let {
                                binding.ivDoubt.loadUrlWithGlideCircle(
                                    it,
                                    null,
                                    null,
                                    requestOptions
                                )
                            }
                        }
                        StudentConst.DoubtResourceType.Video.value -> {
                            //TODO open video
                            binding.ivDoubt.visibility = View.GONE
                            binding.tvDoubt.visibility = View.GONE
                            binding.flVideoView.visibility = View.VISIBLE
                            binding.adaptivePlayerView.setFullScreenListener(object :
                                FullScreenListener {
                                override fun enterFullScreen() {
                                    activity?.requestedOrientation =
                                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                                }

                                override fun exitFullScreen() {
                                    activity?.requestedOrientation =
                                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                }

                            })
                            binding.adaptivePlayerView.setPlayerEventListner(object :
                                PlayerListener {
                                override fun onError(playerState: PlayerError) {
                                    binding.video.text = when (playerState) {
                                        PlayerError.VIDEO_NOT_FOUND -> getString(R.string.video_not_found)
                                        PlayerError.VIDEO_NOT_PLAYABLE -> getString(R.string.video_is_not_playable)
                                        PlayerError.UNKNOWN -> getString(R.string.unknown_error)
                                        PlayerError.INVALID_PARAMETER_IN_REQUEST -> getString(R.string.invalid_video_url)
                                        PlayerError.OUT_OF_MEMORY -> getString(R.string.device_memory_full)
                                        PlayerError.VIDEO_NOT_PLAYABLE_EMBEDED -> getString(R.string.video_not_allow_to_play_embed)
                                        else -> getString(R.string.unknown_error)
                                    }
                                    binding.errorView.visibility = View.VISIBLE
                                }

                                override fun onPlayeBackEvent(playbackState: PlayerState) {
                                    if (playbackState == PlayerState.PLAYING) {
                                    }
                                }

                            })

                            try {
                                binding.adaptivePlayerView.setUrl(it.url, it.contentHost, 0f)
                            } catch (e: Exception) {
                            }

                        }
                        StudentConst.DoubtResourceType.Text.value -> {
                            binding.ivDoubt.visibility = View.GONE
                            binding.tvDoubt.visibility = View.VISIBLE
                            binding.flVideoView.visibility = View.GONE
                            binding.tvDoubt.text = it.text
                            //open in text
                        }
                        StudentConst.DoubtResourceType.Url.value -> {
                            //TODO open in browser
                            openInWebView(it.url, it.topicName, it.contentType)
                        }
                        StudentConst.DoubtResourceType.Audio.value -> {
                            //Todo Audio
                            openInWebView(it.url, it.topicName, it.contentType)
                        }
                        else -> {
                            //Todo
                            it.url?.let {

                                var requestOptions = RequestOptions()
                                requestOptions.centerInside()

                                binding.ivDoubt.loadUrlWithGlideCircle(
                                    it,
                                    R.drawable.ic_student_placeholder,
                                    R.drawable.ic_student_placeholder,
                                    requestOptions
                                )
                            }
                        }

                    }

                }
                binding.ivClose.setOnClickListener {
                    dismiss()
                }

            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    fun pauseVideo() {
        try {
            binding.adaptivePlayerView.pause()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        pauseVideo()
        onDismissListener?.OnDismiss(dialog)
        Log.e("SuccessDialogFragment", "isDismissed")
    }

    enum class DIALOG_TYPE {
        FULL_SCREEN, ALERT
    }

    interface OnDismissListener {
        fun OnDismiss(dialog: DialogInterface)
    }

    class Builder(val context: Context) {

        internal var doubt: Doubt? = null
            private set
        internal var viewType: DIALOG_TYPE = DoubtPreviewDialogFragment.DIALOG_TYPE.ALERT
            private set
        internal var isDialogCancelable: Boolean = true
            private set
        internal var onDismissListener: OnDismissListener? = null
            private set
        internal var clickListner: View.OnClickListener? = null
            private set

        fun setViewType(type: DIALOG_TYPE): Builder {
            this.viewType = type
            return this
        }

        fun build(): DoubtPreviewDialogFragment {
            return DoubtPreviewDialogFragment(this)
        }

        fun setIsDialogCancelable(isCancelable: Boolean): Builder {
            this.isDialogCancelable = isCancelable
            return this
        }

        fun setOnDismissListener(listener: OnDismissListener): Builder {
            this.onDismissListener = listener
            return this
        }

        fun setOnClickListner(listner: View.OnClickListener): Builder {
            this.clickListner = listner
            return this
        }

        fun setDoubt(doubt: Doubt): Builder {
            this.doubt = doubt
            return this
        }
    }

    private fun openInWebView(url: String, title: String, contentType: String) {
        dismiss()

        var url = url
        if (url.startsWith(CommonConst.URL_STARTS_WITH_DRIVE_URL) || url.startsWith(CommonConst.URL_STARTS_WITH_GOOGLE_DOC_URL)) {

        } else {
            if (contentType.equals(CommonConst.PDF)) {
                //Todo do nothing if it's PDF pass the url to PDFReaderActivity
                //url = GOOGLE_DOC_URL + it
            } else if (contentType.equals(CommonConst.MS_DOC) ||
                contentType.equals(CommonConst.MS_PPT) ||
                contentType.equals(CommonConst.MS_XLS)
            ) {
                url = CommonConst.GOOGLE_DOC_URL + url
            }
        }

        var bundle = Bundle()
        bundle.putString(StudentConst.WEB_URL, url)
        bundle.putString(StudentConst.PAGE_TITLE, title)
        bundle.putString(StudentConst.CONTENT_TYPE, contentType)


        var intent: Intent
        if (contentType.equals(CommonConst.PDF)) {
            intent = Intent(context, PdfReaderActivity::class.java)
        } else {
            intent = Intent(context, FileReaderActivity::class.java)
        }
        intent.putExtras(bundle)
        startActivity(intent)
    }
}