package org.evidyaloka.common.ui.subtopic

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.evidyaloka.common.util.CourseTimetableViewUtils
import org.evidyaloka.common.R
import org.evidyaloka.common.databinding.LayoutSubtopicTimtableAdapterBinding
import org.evidyaloka.common.databinding.LayoutTopicTimtableAdapterBinding
import org.evidyaloka.core.model.SubTopic
import org.evidyaloka.core.model.Topic

/**
 * @author Madhankumar
 * created on 15-04-2021
 *
 */
class TopicAdapter(
    val subject: String,
    val lockTopics: Boolean = false,
    val numberOfChapters: Int = 3,
    val numberOfSubtopicAllowedPerChapter: Int = 3,
    val onSubtopicClicked: (topic: Topic, subTopic: SubTopic, isLockedContent: Boolean) -> Unit
) : RecyclerView.Adapter<TopicAdapter.ViewHolder>() {

    private lateinit var context: Context
    private var subjectDetailsList: List<Topic> = listOf()

    inner class ViewHolder(val binding: LayoutTopicTimtableAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(topic: Topic, position: Int) {
            with(binding) {
                val courseUI = CourseTimetableViewUtils.getCourseUISettings(subject)
                ivIcon.setImageResource(courseUI.icon())
                tvTopic.text = topic.name
                tvSubtopicCount.text = topic.subtopics.size.toString().plus(" ").plus(context.getString(
                    org.evidyaloka.common.R.string.subtopics))
                tvLessons.text = context.getString(R.string.lesson)+" "+ (position + 1).toString()
                rvTopic.layoutManager = LinearLayoutManager(
                    rvTopic?.context,
                    LinearLayoutManager.HORIZONTAL, false
                )
                rvTopic.adapter = org.evidyaloka.common.ui.subtopic.TopicAdapter.SubTopicAdapter(
                    courseUI,
                    topic,
                    topic.subtopics,
                    lockTopics,
                    if (lockTopics && numberOfChapters >= position + 1) numberOfSubtopicAllowedPerChapter else 0,
                    onSubtopicClicked
                )
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding = LayoutTopicTimtableAdapterBinding.inflate(
            LayoutInflater.from(parent?.context),
            null,
            false
        )
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(subjectDetailsList[position], position)
    }

    override fun getItemCount(): Int {
        return subjectDetailsList.size
    }

    fun setItem(list: List<Topic>) {
        subjectDetailsList = list
        notifyDataSetChanged()
    }


    class SubTopicAdapter(
        val courseUI: CourseTimetableViewUtils.CourseUISettings,
        val topic: Topic,
        val subTopicList: List<SubTopic>,
        val lockTopics: Boolean = false,
        val numberOfSubtopicAllowedPerChapter: Int = 3,
        val onSubtopicClicked: (topic: Topic, subTopic: SubTopic, isLockedContent: Boolean) -> Unit
    ) :
        RecyclerView.Adapter<SubTopicAdapter.ViewHolder>() {
//        private var subTopicList: List<SubTopic> = listOf()


        inner class ViewHolder(val binding: LayoutSubtopicTimtableAdapterBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(subTopic: SubTopic, position: Int) {
                with(binding) {
                    tvName.text = subTopic.subtopicName
                    if (courseUI == CourseTimetableViewUtils.CourseUISettings.SCIENCE) {
                        icon.background = root.resources.getDrawable(R.drawable.ic_science_bg)
                    } else {
                        icon.background = root.resources.getDrawable(R.drawable.ic_math_bg)
                    }
                    num.text = (position + 1).toString()
                    if (subTopic.hasViewed) {
                        attend.apply {
                            visibility = View.VISIBLE
                            setImageResource(courseUI.complete())
                        }
                        cardView.setBackgroundResource(courseUI.background())
                    }
                    mainView.setOnClickListener {
                        onSubtopicClicked(
                            topic,
                            subTopic,
                            lockTopics && numberOfSubtopicAllowedPerChapter < position + 1
                        )
                    }
                    if (lockTopics && numberOfSubtopicAllowedPerChapter < position + 1) {
                        cardView.alpha = 0.5F
                        imLock.visibility = View.VISIBLE
                    }else{
                        cardView.alpha = 1F
                        imLock.visibility = View.GONE
                    }
                }
            }
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = LayoutSubtopicTimtableAdapterBinding.inflate(
                LayoutInflater.from(parent?.context),
                null,
                false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(subTopicList[position], position)
        }


        override fun getItemCount(): Int {
            return subTopicList.size
        }

    }
}