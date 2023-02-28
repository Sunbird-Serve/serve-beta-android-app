package org.evidyaloka.common.ui.subtopic

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import org.evidyaloka.common.databinding.LayoutSubjectTopicsViewBinding
import org.evidyaloka.core.model.SubTopic
import org.evidyaloka.core.model.Topic

class SubjectTopicsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = LayoutSubjectTopicsViewBinding.inflate(LayoutInflater.from(context), this)
    private lateinit var adapter: TopicAdapter
    fun setSubject(
        subjectName: String,
        list: List<Topic>,
        lockTopics: Boolean = false,
        numberOfChapters: Int = 3,
        numberOfSubtopicAllowedPerChapter: Int = 3,
        onSubtopicClicked: (topic: Topic, subTopic: SubTopic, isLockedContent: Boolean) -> Unit
    ) {
        val adapter = TopicAdapter(subjectName,lockTopics, numberOfChapters, numberOfSubtopicAllowedPerChapter, onSubtopicClicked)
        binding.rvSubjectTimetable.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL, false
        )
        binding.rvSubjectTimetable.adapter = adapter
        adapter.setItem(list)
    }

    fun setViewBackground(resId: Int) {
        binding.llMain.setBackgroundResource(resId)
    }

    fun setLessonCount(text: String){
        binding.tvLessonCount.text = text
    }

}