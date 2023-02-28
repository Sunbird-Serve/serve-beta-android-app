package org.evidyaloka.student.ui.student

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.evidyaloka.common.R
import org.evidyaloka.common.databinding.LayoutDsOfferingsAdapterBinding
import org.evidyaloka.core.model.Course

class DSOfferingsAdapter(val courses: List<Course>, val onCourseSelected: (course: Course) -> Unit) :
    RecyclerView.Adapter<DSOfferingsAdapter.CourseHolder>() {
    private val TAG = "CourseAdapter"


    //private var onCourseClickListener: OnCourseClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseHolder {
        val binding = LayoutDsOfferingsAdapterBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return CourseHolder(binding)
    }

    /*public fun setClickListener(onCourseClickListener: OnCourseClickListener) {
        this.onCourseClickListener = onCourseClickListener
    }*/

    override fun getItemCount(): Int {
        return courses.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: CourseHolder, position: Int) {
        val course = courses[position]
        course?.let {
            holder.bind(it)
        }
    }

    inner class CourseHolder(val binding: LayoutDsOfferingsAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(course: Course) {
            with(binding) {
                val courseUI = getCourseUISettings(course.name)
                subject.text = course.name
                medium.text = course.languageName+" "+ binding.root.resources.getString(R.string.medium)
                icon.setImageResource(courseUI)
            }

            binding.root.setOnClickListener {
                onCourseSelected(course)
            }
        }
    }

    fun getCourseUISettings(courseTitle: String): Int {
        return when {
            courseTitle.contains("math", true) -> {
                R.drawable.ic_subject_maths
            }
            courseTitle.contains("social", true) -> {
                R.drawable.ic_subject_social_studies
            }
            courseTitle.contains("science", true) -> {
                R.drawable.ic_subject_science
            }
            courseTitle.contains("english", true) -> {
                R.drawable.ic_subject_english
            }
            courseTitle.contains("hindi", true) -> {
                R.drawable.ic_subject_hindi
            }
            courseTitle.contains("kannada", true) -> {
                R.drawable.ic_subject_kannada
            }
            courseTitle.contains("telugu", true) -> {
                R.drawable.ic_subject_telugu
            }
            courseTitle.contains("tamil", true) -> {
                R.drawable.ic_subject_tamil
            }
            courseTitle.contains("malayalam", true) -> {
                R.drawable.ic_subject_malyalam
            }
            courseTitle.contains("computer", true) -> {
                R.drawable.ic_subject_cs
            }
            courseTitle.contains("marathi", true) -> {
                R.drawable.ic_subject_marathi
            }
            courseTitle.contains("bengali", true) -> {
                R.drawable.ic_subject_bengali
            }
            else -> {
                R.drawable.ic_subject_maths
            }
        }
    }
}