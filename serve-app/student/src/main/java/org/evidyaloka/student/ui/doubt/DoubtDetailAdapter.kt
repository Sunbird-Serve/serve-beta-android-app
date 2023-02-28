package org.evidyaloka.student.ui.doubt

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.common.util.Utils
import org.evidyaloka.common.helper.loadUrlWithGlide
import org.evidyaloka.common.helper.loadUrlWithGlideCircle
import org.evidyaloka.core.student.model.Doubt
import org.evidyaloka.core.student.model.Student
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.ItemDoubtDetailBinding


class DoubtDetailAdapter() :
    PagedListAdapter<Doubt, DoubtDetailAdapter.DoubtDetailHolder>(DoubtDetailAdapter.DOUBT_COMPARATOR) {
    private val TAG = "DoubtDetailAdapter"

    private var student: Student? = null
    private lateinit var binding: ItemDoubtDetailBinding
    private var onItemClickListener: OnItemClickListner? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoubtDetailHolder {
        binding =
            ItemDoubtDetailBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return DoubtDetailHolder(binding.root)
    }

    public fun setClickListener(onItemClickListener: OnItemClickListner) {
        this.onItemClickListener = onItemClickListener
    }

    fun setItem(student: Student?) {
        this.student = student
    }

    override fun onBindViewHolder(holder: DoubtDetailHolder, position: Int) {
        val doubt = getItem(position)
        doubt?.let {
            holder.bind(it)
        }
    }

    inner class DoubtDetailHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemDoubtDetailBinding.bind(view)
        fun bind(doubt: Doubt) {
            with(binding) {
                cardView.setOnClickListener {
                    onItemClickListener?.OnItemClick(doubt)
                }

                rlReceiver.setOnClickListener {
                    onItemClickListener?.OnItemClick(doubt)
                }

                rlSender.setOnClickListener {
                    onItemClickListener?.OnItemClick(doubt)
                }

                val requestOptions = RequestOptions()
                requestOptions.transforms(CenterCrop(), RoundedCorners(17))
                doubt?.let {
                    if (adapterPosition == 0) {
                        tvDay.visibility = VISIBLE

                        if (Utils.getCurrentDateIndMMMyyyy()
                                .equals(Utils.formatDateInddMMMyyyyFromyyyyMMddHHmmss(it.createdDate))) {
                            tvDay.text = view?.resources?.getString(R.string.label_today)
                        } else {
                            tvDay.text =
                                Utils.formatDateInddMMMyyyyFromyyyyMMddHHmmss(it.createdDate)
                        }
                    } else {
                        getItem(adapterPosition - 1)?.let { d0 ->
                            if (Utils.formatDateInddMMMyyyyFromyyyyMMddHHmmss(d0.createdDate)
                                    .equals(Utils.formatDateInddMMMyyyyFromyyyyMMddHHmmss(it.createdDate))) {
                                tvDay.visibility = GONE
                            } else {
                                tvDay.visibility = VISIBLE
                                tvDay.text =
                                    Utils.formatDateInddMMMyyyyFromyyyyMMddHHmmss(it.createdDate)
                            }
                        }
                    }

                    when (it.recordType) {

                        "1", "" -> {
                            //For student
                            llSender.visibility = VISIBLE
                            llReceiver.visibility = GONE

                            if (!it.text.isNullOrEmpty()) {
                                tvSenderText.visibility = View.VISIBLE
                                tvSenderText.text = it.text
                            } else {
                                tvSenderText.visibility = View.GONE
                            }

                            when (doubt.resourceType) {

                                StudentConst.DoubtResourceType.Image.value.toString() -> {
                                    it.url?.let {
                                        ivSenderDoubt.loadUrlWithGlide(
                                            it,
                                            requestOptions = requestOptions
                                        )
                                    }
                                }

                                StudentConst.DoubtResourceType.Video.value.toString() -> {

                                    if (it.thumbnailUrl.isNullOrEmpty()) {
                                        ivSenderDoubt.setImageResource(R.drawable.ic_video_response_light)
                                        return
                                    }

                                    ivSenderDoubt.loadUrlWithGlide(
                                        it.thumbnailUrl,
                                        R.drawable.ic_student_placeholder,
                                        requestOptions = requestOptions
                                    )

                                }

                                StudentConst.DoubtResourceType.Audio.value.toString() -> {
                                    ivSenderDoubt.setImageResource(R.drawable.ic_audio_response_light)
                                }

                                StudentConst.DoubtResourceType.Text.value.toString() -> {
                                    ivSenderDoubt.visibility = android.view.View.GONE
                                }

                                StudentConst.DoubtResourceType.Url.value.toString() -> {
                                    //Todo add another layout for url
                                    var content = ""
                                    if (!it.text.isNullOrEmpty()) {
                                        content = it.text + "<br /><br />" + getColoredSpanned(it.url, "#31B9ED")
                                    } else {
                                        getColoredSpanned(it.url, "#31B9ED")
                                    }
                                    tvSenderText.text = Html.fromHtml(content, FROM_HTML_MODE_LEGACY)
                                    ivSenderDoubt.visibility = View.GONE
                                }
                                else -> {
                                    it.url?.let {
                                        ivSenderDoubt.loadUrlWithGlide(
                                            it,
                                            requestOptions = requestOptions
                                        )
                                    }
                                }
                            }

                            //Dummy data
                            //Todo change to student name
                            student?.let {
                                ivSenderUser.loadUrlWithGlideCircle(
                                    it.profileUrl,
                                    R.drawable.ic_student_placeholder,
                                    R.drawable.ic_student_placeholder
                                )
                                tvSenderName.text = it.name
                            }
                            tvSenderTime.text =
                                Utils.formatDateInhhmmFromyyyyMMddHHmmssa(it.createdDate)
                        }
                        else -> {
                            //For teacher

                            llSender.visibility = GONE
                            llReceiver.visibility = VISIBLE

                            if(!it.text.isNullOrEmpty()){
                                tvReceiverText.visibility = View.VISIBLE
                                tvReceiverText.text = it.text
                            }else{
                                tvReceiverText.visibility = View.GONE
                            }

                            when (doubt.resourceType) {
                                StudentConst.DoubtResourceType.Image.value.toString() -> {
                                    it.url?.let {
                                        ivReceiverDoubt.loadUrlWithGlide(
                                            it,
                                            requestOptions = requestOptions
                                        )
                                    }
                                }

                                StudentConst.DoubtResourceType.Video.value.toString() -> {

                                    if (it.thumbnailUrl.isNullOrEmpty()) {
                                        ivReceiverDoubt.setImageResource(R.drawable.ic_video_response_dark)
                                        return
                                    }

                                    ivReceiverDoubt.loadUrlWithGlide(
                                        it.thumbnailUrl,
                                        R.drawable.ic_student_placeholder,
                                        requestOptions = requestOptions
                                    )

                                }

                                StudentConst.DoubtResourceType.Audio.value.toString() -> {
                                    ivReceiverDoubt.setImageResource(R.drawable.ic_audio_response_dark)
                                }

                                StudentConst.DoubtResourceType.Text.value.toString()
                                -> {
                                    ivReceiverDoubt.visibility = View.GONE
                                }

                                StudentConst.DoubtResourceType.Url.value.toString() -> {
                                    //Todo add another layout for url
                                    var content = ""
                                    if (!it.text.isNullOrEmpty()) {
                                        content = it.text + "<br /><br />" + getColoredSpanned(it.url, "#31B9ED")
                                    } else {
                                        getColoredSpanned(it.url, "#31B9ED")
                                    }
                                    tvReceiverText.text = Html.fromHtml(content, FROM_HTML_MODE_LEGACY)
                                    ivReceiverDoubt.visibility = View.GONE
                                }

                                else ->{
                                    it.url?.let {
                                        ivReceiverDoubt.loadUrlWithGlide(
                                            it,
                                            requestOptions = requestOptions
                                        )
                                    }
                                }
                            }

                            tvReceiverName.text = it.teacherName
                            tvReceiverTime.text =
                                Utils.formatDateInhhmmFromyyyyMMddHHmmssa(it.createdDate)
                            //TODO no placeholder for teacher
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val DOUBT_COMPARATOR = object : DiffUtil.ItemCallback<Doubt>() {
            override fun areItemsTheSame(oldItem: Doubt, newItem: Doubt): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Doubt, newItem: Doubt): Boolean =
                newItem.id == oldItem.id
        }
    }

    private fun getColoredSpanned(text: String, color: String): String? {
        return "<font color=$color>$text</font>"
    }

}