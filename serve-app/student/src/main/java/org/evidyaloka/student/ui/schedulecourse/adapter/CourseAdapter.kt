package org.evidyaloka.student.ui.schedulecourse.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.evidyaloka.core.model.Course
import org.evidyaloka.student.R
import org.evidyaloka.student.utils.CourseCardview

/**
 * @author Madhankumar
 * created on 16-03-2021
 *
 */
class CourseAdapter: RecyclerView.Adapter<CourseAdapter.CourseHolder>() {

    private var courses: List<Course> = listOf()
    private var selectedCoursesId: MutableList<Int> = mutableListOf()

    inner class CourseHolder(val view:View): RecyclerView.ViewHolder(view) {
        fun bind(course: Course){
            (view as CourseCardview).setSubject(course.name)
            view.setMedium(course.languageName + " " +view.context.getString(R.string.medium))
            if(course.isSelected) {
                selectedCoursesId.add(course.id)
                view.isChecked = true
            }

            view.setClickListener(object:
                CourseCardview.OnClickListener {
                override fun onClick(v: CourseCardview) {
                    if (v.isChecked) {
                        selectedCoursesId.add(course.id)
                    }else{
                        selectedCoursesId.remove(course.id)
                    }
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseHolder {
        val courseView = CourseCardview(
            parent.context,
            CourseCardview.SessionViewType.CHECKBOX
        )
        return CourseHolder(courseView)
    }

    override fun getItemCount(): Int {
        return courses.size
    }

    override fun onBindViewHolder(holder: CourseHolder, position: Int) {
        courses[position]?.let {
            holder.bind(it)
        }
    }

    fun setItem(list: List<Course>){
        courses = list
        notifyDataSetChanged()
    }

    fun getSelectedCourseId(): List<Int> = selectedCoursesId
}