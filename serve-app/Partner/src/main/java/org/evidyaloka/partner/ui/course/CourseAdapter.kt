package org.evidyaloka.partner.ui.course

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_course.view.*
import org.evidyaloka.common.util.Utils
import org.evidyaloka.partner.R
import org.evidyaloka.core.partner.model.CourseOffering
import org.evidyaloka.partner.ui.helper.Helper

/**
 * Adapter class for Course Item
 *
 */
class CourseAdapter(
        private val courses: List<CourseOffering>,
        private val clickListener: (offering: CourseOffering) -> Unit
) : RecyclerView.Adapter<CourseAdapter.CourseHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseHolder {
        return CourseHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return courses.size
    }

    override fun onBindViewHolder(holder: CourseHolder, position: Int) {
        val course = courses[position]
        holder.bind(course, clickListener)
    }

    class CourseHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(
            course: CourseOffering,
            clickListener: (course: CourseOffering) -> Unit
        ) {

            course?.let {

                view.tv_course_name?.setText(it.courseName)

                val grade = it.grade.takeIf { it.isNotEmpty() }?.toInt()
                grade?.let {
                    view.tv_grade?.setText(
                        Utils.getFormatedGrade(it).plus(" ").plus(
                            Utils.ordinalSuffix(it)).plus(" ").plus(view.resources.getString(R.string.label_grade)))
                }

                var drawableInt = Helper.courseIcon(it.courseName)

                view.iv_course_logo?.let {

                    it.context?.let { it1 ->
                        Glide.with(it1)
                            .load(drawableInt)
                            .placeholder(R.drawable.ic_course_placeholder)
                            .error(R.drawable.ic_course_placeholder)
                            .into(it)
                    }
                }
            }

            view.setOnClickListener {
                clickListener(course)
            }

        }
    }
}