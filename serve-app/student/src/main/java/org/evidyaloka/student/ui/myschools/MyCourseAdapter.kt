package org.evidyaloka.student.ui.student

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.evidyaloka.core.model.Course
import org.evidyaloka.student.databinding.ItemMyCourseBinding
import org.evidyaloka.common.util.CourseTimetableViewUtils
import org.evidyaloka.common.util.SubjectViewUtils

class MyCourseAdapter() :
    RecyclerView.Adapter<MyCourseAdapter.MyCourseHolder>() {
    private val TAG = "MyCourseAdapter"

    private var courses: List<Course> = listOf()
    private lateinit var binding: ItemMyCourseBinding
    //private var onCourseClickListener: OnCourseClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCourseHolder {
        binding = ItemMyCourseBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return MyCourseHolder(binding.root)
    }

    /*public fun setClickListener(onCourseClickListener: OnCourseClickListener) {
        this.onCourseClickListener = onCourseClickListener
    }*/

    fun setItems(courses: List<Course>) {
        this.courses = courses
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return courses.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: MyCourseHolder, position: Int) {
        val course = courses[position]
        course?.let {
            holder.bind(it)
        }
    }

    inner class MyCourseHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemMyCourseBinding.bind(view)
        fun bind(course: Course) {
            with(binding) {
                val courseUI = CourseTimetableViewUtils.getCourseUISettings(course.name)
                subject.text = course.name
                icon.setImageResource(courseUI.icon())
                cardview.setBackgroundResource(SubjectViewUtils.getUIBackground(course.name))
            }

            binding.root.setOnClickListener {
                //onCourseClickListener?.OnItemClick(course)
            }
        }
    }
}