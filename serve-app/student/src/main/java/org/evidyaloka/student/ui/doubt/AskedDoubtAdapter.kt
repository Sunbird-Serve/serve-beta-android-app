package org.evidyaloka.Doubt.ui.doubt

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.Constants.StudentConst.RESOLVED
import org.evidyaloka.common.util.Utils
import org.evidyaloka.common.helper.loadUrlWithGlideCircle
import org.evidyaloka.core.student.model.Doubt
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.ItemAskedDoubtBinding
import org.evidyaloka.student.ui.doubt.OnItemClickListner

class AskedDoubtAdapter() :
    PagedListAdapter<Doubt, AskedDoubtAdapter.DoubtHolder>(AskedDoubtAdapter.DOUBT_COMPARATOR) {
    private val TAG = "AskedDoubtAdapter"

    private var doubts: List<Doubt> = listOf()
    private lateinit var binding: ItemAskedDoubtBinding
    private var listener: OnItemClickListner? = null

    fun setClickListener(listener: OnItemClickListner) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoubtHolder {
        binding = ItemAskedDoubtBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return DoubtHolder(binding)
    }

    override fun onBindViewHolder(holder: DoubtHolder, position: Int) {
        var doubt = getItem(position)
        doubt?.let {
            holder.bind(it)
        }
    }

    inner class DoubtHolder(val binding: ItemAskedDoubtBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceAsColor")
        fun bind(doubt: Doubt) {
            with(binding) {
                //Todo
                doubt?.let {
                    //Todo check contentType first
                    when (it.resourceType) {
                        StudentConst.DoubtResourceType.Image.value.toString() -> {
                            binding.ivDoubtType.visibility = View.GONE
                            binding.tvDoubtType.visibility = View.GONE
                            binding.ivDoubt.visibility = View.VISIBLE
                            val requestOptions = RequestOptions()
                            requestOptions.transforms(CenterCrop(), RoundedCorners(40))
                            binding.ivDoubt.loadUrlWithGlideCircle(
                                it.url,
                                R.drawable.ic_student_placeholder,
                                requestOptions = requestOptions
                            )
                        }

                        StudentConst.DoubtResourceType.Video.value.toString() -> {
                            if (it.thumbnailUrl.isNullOrEmpty()) {
                                binding.ivDoubtType.visibility = View.VISIBLE
                                binding.tvDoubtType.visibility = View.VISIBLE
                                binding.ivDoubt.visibility = View.GONE
                                binding.ivDoubtType.setImageResource(R.drawable.ic_video_light)
                                binding.tvDoubtType.text =
                                    binding.tvDoubtType?.resources.getString(R.string.video)
                                return
                            }

                            binding.ivDoubtType.visibility = View.VISIBLE
                            binding.tvDoubtType.visibility = View.GONE
                            binding.ivDoubt.visibility = View.VISIBLE
                            binding.ivDoubtType.setImageResource(R.drawable.ic_video_white)
                            val requestOptions = RequestOptions()
                            requestOptions.transforms(CenterCrop(), RoundedCorners(40))
                            binding.ivDoubt.loadUrlWithGlideCircle(
                                it.thumbnailUrl,
                                R.drawable.ic_student_placeholder,
                                requestOptions = requestOptions
                            )

                            //Todo show play icon on IV
                        }

                        StudentConst.DoubtResourceType.Audio.value.toString() -> {
                            binding.ivDoubtType.visibility = View.VISIBLE
                            binding.tvDoubtType.visibility = View.VISIBLE
                            binding.ivDoubt.visibility = View.GONE
                            binding.ivDoubtType.setImageResource(R.drawable.ic_audio_light)
                            binding.tvDoubtType.text =
                                binding.tvDoubtType?.resources.getString(R.string.video)
                        }

                        StudentConst.DoubtResourceType.Text.value.toString() -> {
                            binding.ivDoubtType.visibility = View.VISIBLE
                            binding.tvDoubtType.visibility = View.VISIBLE
                            binding.ivDoubt.visibility = View.GONE
                            binding.ivDoubtType.setImageResource(R.drawable.ic_chat)
                            binding.tvDoubtType.text =
                                binding.tvDoubtType?.resources.getString(R.string.chat)
                        }

                        StudentConst.DoubtResourceType.Url.value.toString() -> {
                            binding.ivDoubtType.visibility = View.VISIBLE
                            binding.tvDoubtType.visibility = View.VISIBLE
                            binding.ivDoubt.visibility = View.GONE
                            binding.ivDoubtType.setImageResource(R.drawable.ic_link)
                            binding.tvDoubtType.text =
                                binding.tvDoubtType?.resources.getString(R.string.link)
                        }

                        else -> {
                            binding.ivDoubtType.visibility = View.GONE
                            binding.tvDoubtType.visibility = View.GONE
                            binding.ivDoubt.visibility = View.VISIBLE
                            val requestOptions = RequestOptions()
                            requestOptions.transforms(CenterCrop(), RoundedCorners(40))
                            binding.ivDoubt.loadUrlWithGlideCircle(
                                it.url,
                                R.drawable.ic_student_placeholder,
                                requestOptions = requestOptions
                            )
                        }
                    }

                    if (adapterPosition % 2 != 0) {
                        binding.root.setBackgroundResource(R.color.doubt_secondary)
                    } else {
                        binding.root.setBackgroundResource(R.color.doubt_primary)

                    }

                    binding.tvSubtopic.setText(it.subTopicName)
                    binding.tvTopic.setText(it.topicName)
                    Utils.formatDateInddMMMyyyyFromyyyyMMddHHmmss(it.createdDate)?.let {
                        binding.tvDate.setText(it)
                    }

                    if (it.status.equals(RESOLVED)) {
                        binding.btMakeCorrection.isEnabled = false
                    }

                    binding.btMakeCorrection.setOnClickListener {
                        listener?.OnItemClick(doubt, StudentConst.DoubtViewType.EDIT)
                    }
                }
            }

            binding.root.setOnClickListener {
                Log.e(TAG, "root clicked")
                listener?.OnItemClick(doubt, StudentConst.DoubtViewType.VIEW)
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
}