package org.evidyaloka.student.ui.doubt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.common.util.Utils
import org.evidyaloka.core.student.model.Doubt
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.ItemDoubtBinding
import org.evidyaloka.common.util.SubjectViewUtils

class DoubtAdapter() :
    PagedListAdapter<Doubt, DoubtAdapter.DoubtHolder>(DoubtAdapter.DOUBT_COMPARATOR) {
    private val TAG = "DoubtAdapter"
    private lateinit var binding: ItemDoubtBinding
    private var onItemClickListener : OnItemClickListner? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoubtHolder {
        binding = ItemDoubtBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return DoubtHolder(binding.root)
    }

    public fun setClickListener(onItemClickListener : OnItemClickListner){
        this.onItemClickListener = onItemClickListener
    }

    override fun onBindViewHolder(holder: DoubtHolder, position: Int) {
        val doubt = getItem(position)
        doubt?.let {
            holder.bind(it)
        }
    }

    inner class DoubtHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemDoubtBinding.bind(view)
        fun bind(doubt: Doubt) {
            with(binding) {
                tvSubject.text = doubt.topicName

                val courseUiSettings = SubjectViewUtils.getCourseUISettings(doubt.subjectName)
                ivIcon.setImageResource(courseUiSettings.icon())

                when (doubt.status) {
                    "1", "" -> {
                        tvStatus.apply {
                            text = StudentConst.DoubtStatus.Waiting.name
                            background = view?.context?.getDrawable(R.drawable.bg_class_taken)
                            llRoot.setBackgroundResource((R.drawable.bg_rtc_view_unselected))
                            view?.context?.resources?.getColor(R.color.class_taken_color, null)?.let {
                                setTextColor(it)
                            }
                        }
                    }
                    else ->{
                        tvStatus.apply {
                            text = StudentConst.DoubtStatus.Responded.name
                            background = view?.context?.getDrawable(R.drawable.bg_doubt_responded)
                            llRoot.setBackgroundResource((R.drawable.bg_rtc_view_selected))
                            view?.context?.resources?.getColor(R.color.color_doubt_responded, null)?.let {
                                setTextColor(it)
                            }
                        }
                    }
                }
                tvDate.text = Utils.formatDateInddMMMyyyyFromyyyyMMddHHmmss(doubt.createdDate)
            }

            binding.root.setOnClickListener {
                onItemClickListener?.OnItemClick(doubt)
                notifyDataSetChanged()
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