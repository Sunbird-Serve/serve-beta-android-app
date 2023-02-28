package org.evidyaloka.student.ui.bookmark

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.evidyaloka.core.student.database.entity.BookmarkEntity
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.ItemDownloadViewBinding

class BookmarkAdapter() :
    RecyclerView.Adapter<BookmarkAdapter.ViewHolder>() {
    private val TAG = "SchoolAdapter"

    private var content: List<BookmarkEntity> = listOf()
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

    fun setItems(content: List<BookmarkEntity>) {
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
        fun bind(content: BookmarkEntity) {
            with(binding) {
                tvTitle.text = content.topicName
                content.subTopicName?.takeIf { it.isNotEmpty() || it.isNotBlank() }?.let {
                    tvSubtitle.apply {
                        text = it
                        visibility = View.VISIBLE
                    }
                }

                ivTypeLogo.setImageResource(getActivityBackground(this@ViewHolder.adapterPosition))
                ivTypeLogo.background = null

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
}