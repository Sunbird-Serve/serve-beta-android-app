package org.evidyaloka.common.ui.rtc

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.evidyaloka.common.R
import org.evidyaloka.common.databinding.LayoutCourseplayerRecyclerViewBinding
import org.evidyaloka.common.databinding.LayoutCourseplayerViewpagerBinding

class CoursePlayerViewPager @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val binding =
        LayoutCourseplayerViewpagerBinding.inflate(LayoutInflater.from(context), this)
    private val adapter = CoursePlayerViewPagerAdapter()

    init {
        try {

            binding.pager.adapter = adapter
            binding.pager.setPageTransformer(ForegroundToBackgroundPageTransformer())

            TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
                context?.let {
                    when (position) {
                        0 -> {
                            tab.text = it.getString(R.string.label_video_lessons)
                        }
                        1 -> {
                            tab.text = it.getString(R.string.label_activity)
                        }
                        2 -> {
                            tab.text = it.getString(R.string.label_worksheet)
                        }
                        3 -> {
                            tab.text = it.getString(R.string.label_textbook)
                        }
                    }
                }
            }.attach()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    fun setAdapterData(list: List<CoursePlayerPagerList>, showDownloadOption:Boolean = true) {
        adapter.setItem(list,showDownloadOption)
    }

    fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        binding.pager.setCurrentItem(item, smoothScroll)
    }


    fun setOnItemClickListner(listner: OnItemClickListner?) {
        adapter.setOnItemClickListner(listner)
    }

    fun setDownloadClickListener(listener: OnItemClickListner) {
        adapter.setDownloadClickListener(listener)
    }

    fun setDeleteClickListener(listener: OnItemClickListner) {
        adapter.setDeleteClickListener(listener)
    }

    private inner class CoursePlayerViewPagerAdapter :
        RecyclerView.Adapter<CoursePlayerViewPagerAdapter.sessionHolder>() {
        private var list: List<CoursePlayerPagerList> = listOf()
        private var listner: OnItemClickListner? = null
        private var downloadClick: OnItemClickListner? = null
        private var deleteClick: OnItemClickListner? = null
        var isDownloadOptionShown:Boolean = false

        inner class sessionHolder(val binding: LayoutCourseplayerRecyclerViewBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(contentAttribute: CoursePlayerPagerList, pos: Int) {

                val viewPagerAdapter = CoursePlayerDetailsAdapter(contentAttribute.type)
                binding.rvDetails.apply {
                    layoutManager =
                        LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
                    adapter = viewPagerAdapter

                }
                if (contentAttribute.item.isNullOrEmpty() && contentAttribute.type != SessionType.VIDEO) showEmptyView() else hideEmptyView()
                binding.flTextbookBg.visibility =
                    if (contentAttribute.type == SessionType.TEXTBOOK) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }

                viewPagerAdapter?.apply {

                    type = contentAttribute.type
                    setItem(contentAttribute.item,
                        isDownloadOptionShown
                    )
                    notifyDataSetChanged()
                    setOnItemClickListener(listner)
                    downloadClick?.let { setDownloadClickListener(it) }
                    deleteClick?.let{ setDeleteClickListener(it) }
                }


            }

            private fun showEmptyView() {
                binding.emptyView.visibility = View.VISIBLE
                binding.rvDetails.visibility = View.GONE
            }

            private fun hideEmptyView() {
                binding.emptyView.visibility = View.GONE
                binding.rvDetails.visibility = View.VISIBLE
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): sessionHolder {
            var binding = LayoutCourseplayerRecyclerViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return sessionHolder(binding)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: sessionHolder, position: Int) {
            list[position]?.let {
                holder.bind(it, position)
            }
        }

        fun setItem(list: List<CoursePlayerPagerList>,  showDownloadOption:Boolean = false) {
            this.list = list
            this.isDownloadOptionShown = showDownloadOption
            notifyDataSetChanged()
        }

        fun setOnItemClickListner(listner: OnItemClickListner?) {
            this.listner = listner
        }

        fun setDownloadClickListener(listener: OnItemClickListner) {
            downloadClick = listener
        }

        fun setDeleteClickListener(listener: OnItemClickListner) {
            deleteClick = listener
        }

    }
}