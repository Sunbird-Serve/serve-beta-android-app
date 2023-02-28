package org.evidyaloka.common.ui.rtc

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Checkable
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.evidyaloka.common.R
import org.evidyaloka.common.util.Utils
import org.evidyaloka.common.helper.loadUrlWithGlideCircle
import org.evidyaloka.common.databinding.ItemRtcViewBinding


class CoursePlayerDetailsCardview(context: Context, val type: SessionType) : CardView(context),
    Checkable {
    private val TAG = "SessionDetailsCardview"

    interface OnClickListener {
        fun onClick(v: CoursePlayerDetailsCardview)
    }

    private var isChecked = false
    private var clickListener: OnClickListener? = null
    private var downloadClick: OnClickListener? = null
    private var deleteClick: OnClickListener? = null
    private lateinit var binding: ItemRtcViewBinding
    private var sessionType: SessionType =
        SessionType.VIDEO
    private val CORNER_RADIUS = 15F
    private val CORNER_ELEVATION = 2F
    private var downloadingAnimation: AnimationDrawable? = null

    init {
        binding = ItemRtcViewBinding.inflate(LayoutInflater.from(context), this)
        var height = when (type) {
            SessionType.VIDEO -> Utils.convertDpToPixel(89F, context)
            SessionType.ACTIVITY -> Utils.convertDpToPixel(89F, context)
            SessionType.WORKSHEET -> Utils.convertDpToPixel(89F, context)
            SessionType.TEXTBOOK -> Utils.convertDpToPixel(99F, context)
        }
        val parms = LayoutParams(
            LayoutParams.MATCH_PARENT,
            height.toInt()
        )
        parms.bottomMargin =
            Utils.convertDpToPixel(resources.getDimension(R.dimen.margin_8dp), context).toInt()
        parms.marginStart =
            Utils.convertDpToPixel(resources.getDimension(R.dimen.margin_8dp), context).toInt()
        parms.marginEnd =
            Utils.convertDpToPixel(resources.getDimension(R.dimen.margin_8dp), context).toInt()
        this.setLayoutParams(parms)
        this.radius = Utils.convertDpToPixel(CORNER_RADIUS, context)
        this.elevation = Utils.convertDpToPixel(CORNER_ELEVATION, context)
        this.cardElevation = Utils.convertDpToPixel(CORNER_ELEVATION, context)
        setClickable(true)
        setChecked(false)
        binding.ivDownload.setOnClickListener {
            downloadClick?.onClick(this)
        }
        binding.ibDelete.setOnClickListener {
            deleteClick?.onClick(this)
        }
        when (type) {
            SessionType.VIDEO -> {
                binding.llVideo.visibility = View.VISIBLE
            }
            SessionType.ACTIVITY, SessionType.WORKSHEET -> {
                binding.llActivity.visibility = View.VISIBLE
//                binding.tvActivityView.setOnClickListener {
//                    try {
//                        clickListener?.onClick(this)
//                    } catch (e: Exception) {
//                        FirebaseCrashlytics.getInstance().recordException(e)
//                    }
//                }
            }
            SessionType.TEXTBOOK -> {
                binding.llTextbook.visibility = View.VISIBLE
            }
        }
    }

    protected override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState: IntArray = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked()) {
            mergeDrawableStates(
                drawableState,
                CHECKED_STATE_SET
            )
        } else {
            mergeDrawableStates(
                drawableState,
                CHECKED_STATE_PRESS
            )
        }
        return drawableState
    }

    override fun performClick(): Boolean {
        toggle()
        return super.performClick()
    }

    override fun setChecked(checked: Boolean) {
        isChecked = checked
        clickListener?.onClick(this)
    }

    override fun isChecked(): Boolean {
        return isChecked
    }

    override fun toggle() {
        setChecked(!isChecked)
    }

    fun setBackgroundImage(id: Int) {
        this.setBackgroundResource(id)
    }

    fun setBackground(status: Int? = null) {
        setBackgroundImage(
            if (type == SessionType.TEXTBOOK) {
                R.drawable.ic_textbook_bg
            } else {
                status?.let {
                    if (it == 0) {
                        R.drawable.bg_rtc_view_unselected
                    } else {
                        R.drawable.bg_rtc_view_selected
                    }
                } ?: R.drawable.selector_rtc_view
            }
        )
    }

    fun setBackgroundTint(color: Int) {
        binding.ivActivity.setColorFilter(
            ContextCompat.getColor(context, color),
            android.graphics.PorterDuff.Mode.SRC_IN
        );
    }

    fun setViewButtonText(status: Int? = null) {
        var text = if (status == 2)
            resources.getString(R.string.label_bt_view)
        else resources.getString(R.string.label_bt_read)
        binding.tvActivityView.setText(text)
    }

    fun setTitle(title: String?, subtitle: String? = null) {
        when (type) {
            SessionType.VIDEO -> {
                binding.tvTitle.text = title
                subtitle?.let { setProgression(it) }
            }
            SessionType.ACTIVITY, SessionType.WORKSHEET -> {
                title?.let { binding.tvActivityTitle.text = it }
                subtitle?.takeIf { it.isNotEmpty() }?.let {
                    binding.tvActivitySubtitle.visibility = View.VISIBLE
                    binding.tvActivitySubtitle.text = it
                }
            }
            SessionType.TEXTBOOK -> {
            }
        }
    }

    fun setProgression(progression: String) {
        binding.tvProgression.setText(progression)

    }

    fun setProgressBar(progress: Int, duration: Int) {
        binding.progressBar.progress = progress
        binding.progressBar.max = duration
    }


    fun setThumbnail(thumbnailUrl: String?) {
        thumbnailUrl?.let {
            if (it.isEmpty()) {
                binding.ivThumbnail.setBackgroundResource(R.drawable.ic_video_placeholder)
            } else {
                val requestOptions = RequestOptions()
                requestOptions.transforms(CenterCrop(), RoundedCorners(6))
                binding.ivThumbnail.loadUrlWithGlideCircle(
                    it,
                    R.drawable.ic_player_tumbnail,
                    requestOptions = requestOptions
                )
            }
        } ?: binding.ivThumbnail.setBackgroundResource(R.drawable.ic_player_tumbnail)
    }

    fun setIcon(position: Int?) {
        position?.let {
            binding.ivActivity.setBackgroundResource(getActivityBackground(it))
        }
    }

    fun setTexbook(url: String?) {
        url?.let { binding.tvUrl.setText(it) }
    }

    fun setClickListener(listener: OnClickListener) {
        clickListener = listener
    }

    fun setDownloadClickListener(listener: OnClickListener) {
        binding.ivDownload.apply {
            visibility = View.VISIBLE
            setImageDrawable(resources.getDrawable(R.drawable.ic_download, null))
        }
        binding.llDownload.visibility = View.VISIBLE
        downloadClick = listener
    }

    fun setDeleteClickListener(listener: OnClickListener) {
        binding.ibDelete.apply {
            visibility = View.VISIBLE
        }
        deleteClick = listener
    }

    fun setDownloadedImage() {
        binding.tvDownloaded.visibility = View.VISIBLE
    }

    fun setDownloadingAnimation() {
        binding.ivDownload.apply {
            visibility = View.VISIBLE
            val downloadingAnimation =
                this.resources.getDrawable(R.drawable.anim_downloading, null) as AnimationDrawable
            setImageDrawable(downloadingAnimation)
            downloadingAnimation?.start()
        }
        binding.llDownload.visibility = View.VISIBLE

    }

    companion object {
        private val CHECKED_STATE_SET = intArrayOf(
            android.R.attr.state_checked
        )
        private val CHECKED_STATE_PRESS = intArrayOf(
            android.R.attr.state_pressed
        )

    }

    fun getActivityBackground(pos: Int): Int {
        var result = pos % 10

        return when (result) {
            9 -> {
                return R.drawable.ic_activity_9
            }
            8 -> {
                return R.drawable.ic_activity_8
            }
            7 -> {
                return R.drawable.ic_activity_7
            }
            6 -> {
                return R.drawable.ic_activity_6
            }
            5 -> {
                return R.drawable.ic_activity_5
            }
            4 -> {
                return R.drawable.ic_activity_4
            }
            3 -> {
                return R.drawable.ic_activity_3
            }
            2 -> {
                return R.drawable.ic_activity_2
            }
            1 -> {
                return R.drawable.ic_activity_1
            }
            0 -> {
                return R.drawable.ic_activity_10
            }
            else -> return R.drawable.ic_activity_10
        }
    }
}