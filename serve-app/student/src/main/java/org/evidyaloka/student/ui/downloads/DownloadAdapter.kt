package org.evidyaloka.student.ui.downloads

import android.app.DownloadManager
import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.evidyaloka.core.Constants.CommonConst
import org.evidyaloka.core.model.rtc.ContentAttributes
import org.evidyaloka.core.student.database.entity.CourseContentEntity
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.ItemDownloadViewBinding

class DownloadAdapter() :
    RecyclerView.Adapter<DownloadAdapter.ViewHolder>() {
    private val TAG = "SchoolAdapter"

    private var content: List<CourseContentEntity> = listOf()
    private lateinit var binding: ItemDownloadViewBinding
    private var onItemClickListner: OnItemClickListner? = null
    private var onDeleteClickListner: OnItemClickListner? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding =
            ItemDownloadViewBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return ViewHolder(binding)
    }

    public fun setClickListener(onItemClickListner: OnItemClickListner) {
        this.onItemClickListner = onItemClickListner
    }

    public fun setDeleteClickListener(onItemClickListner: OnItemClickListner) {
        this.onDeleteClickListner = onItemClickListner
    }

    fun setItems(content: List<CourseContentEntity>) {
        this.content = content
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return content.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val content = content[position]
        content?.let {
            holder.bind(it)
        }
    }

    inner class ViewHolder(val binding: ItemDownloadViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(content: CourseContentEntity) {
            with(binding) {
                tvTitle.text = content.title
                content.subtitle?.takeIf { it.isNotEmpty() || it.isNotBlank() }?.let {
                    tvSubtitle.apply {
                        text = it
                        visibility = View.VISIBLE
                    }
                }

                if (content.contentType == CommonConst.ContentType.VIDEO.value) {
                    ivTypeLogo.setImageResource(R.drawable.ic_video_placeholder)
                } else {
                    ivTypeLogo.setImageResource(getActivityBackground(this@ViewHolder.adapterPosition))
                    ivTypeLogo.background = null
                }

                if (content.downloadStatus == null ||
                    content.downloadStatus == DownloadManager.STATUS_RUNNING ||
                    content.downloadStatus == DownloadManager.STATUS_PENDING ||
                    content.downloadStatus == DownloadManager.STATUS_PAUSED
                ) {
                    setDownloadingAnimation()
                } else if (content.downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {
                    ibDelete.apply {
                        visibility = View.VISIBLE
                        setOnClickListener {
                            onDeleteClickListner?.OnItemClick(content)
                        }
                    }
                }

            }

            binding.root.setOnClickListener {
                onItemClickListner?.OnItemClick(content)
            }
        }
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

    private fun setDownloadingAnimation() {
        binding.ivDownload.apply {
            visibility = View.VISIBLE
            val downloadingAnimation =
                this.resources.getDrawable(R.drawable.anim_downloading, null) as AnimationDrawable
            setImageDrawable(downloadingAnimation)
            downloadingAnimation?.start()
        }
        binding.llDownload.visibility = View.VISIBLE
    }

    private fun deleteDownloadedContent() {

    }
}