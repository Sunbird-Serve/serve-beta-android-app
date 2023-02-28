package org.evidyaloka.student.ui.timetable.courseview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.common.util.Utils
import org.evidyaloka.common.util.Utils.convertyyyyMMhhhmmToLong
import org.evidyaloka.common.helper.convert24to12a
import org.evidyaloka.core.model.Session
import org.evidyaloka.common.util.CourseTimetableViewUtils
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.ItemCourseTimetableBinding
import org.evidyaloka.student.ui.timetable.OnItemClickListner
import java.util.*

class CourseTimetableAdapter() :
    PagedListAdapter<Session, CourseTimetableAdapter.ClassHolder>(
        SESSION_COMPARATOR
    ) {

    private val TAG = "CourseTimetableAdapter"
    private var context:Context? = null
    var isUpComingShown = false
    private var timetables: List<Session> = listOf()
    private var onItemClickListener: OnItemClickListner? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassHolder {
        val binding =
            ItemCourseTimetableBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        context = parent?.context
        return ClassHolder(binding)
    }

    fun setOnItemClickListener(listner: OnItemClickListner) {
        onItemClickListener = listner
    }

    fun setItems(timetables: List<Session>) {
        this.timetables = timetables
    }


    override fun onBindViewHolder(holder: ClassHolder, position: Int) {
        val timetable = getItem(position)
        timetable?.let {
            holder.bind(it)
        }
    }

    inner class ClassHolder(val binding: ItemCourseTimetableBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(timetable: Session) {
            with(binding) {
                timetable?.let { tt ->
                    val courseUiSettings =
                        CourseTimetableViewUtils.getCourseUISettings(tt.subjectName)
                    if (tt.hasAttended) {
                        showClassTaken(courseUiSettings)
                    } else {
                        convertyyyyMMhhhmmToLong(tt.calDate.plus(" " + tt.startTime))?.let {
                            if (it > Calendar.getInstance().timeInMillis) {
                                //if class's time is more than current time.
                                showClassUpcoming(tt.classType)
                            } else {
                                //if class's time is less than current time.
                                //show past classes
                                showClassPast(tt.classType)
                            }
                        }
                    }

                    tvSubjectName.text = tt.subjectName
                    tvTopicName.text = tt.topicName
                    Utils.formatDateInddMMMMyyyyFromyyyyMMdd(tt.calDate)?.let {
                        tvClassDate.text = it
                    }

                    tvTimings.text = tt.startTime.convert24to12a().plus(" - ").plus(tt.endTime.convert24to12a())
                    binding.ivTopic.setImageResource(courseUiSettings.icon())
                }
            }

            binding.root.setOnClickListener {
                onItemClickListener?.OnItemClick(timetable)
            }
        }

        //Missed classes
        fun showClassTaken(courseUiSettings: CourseTimetableViewUtils.CourseUISettings) {

            //Background image
            binding.llBackground.apply {
                background =
                    context.resources.getDrawable(courseUiSettings.background())
            }

            // class status background
            binding.tvClassStatus.apply {
                background =
                    context.resources.getDrawable(R.drawable.bg_class_taken)
            }

            // class status text color
            binding.tvClassStatus.apply {
                setTextColor(context.resources.getColor(R.color.class_taken_color))
            }
            // class status text
            binding.tvClassStatus.text =
                binding.tvClassStatus.context.resources.getString(R.string.label_class_taken)

            binding.ivClassTaken.apply {
                setBackgroundResource(courseUiSettings.complete())
            }

            binding.ivClassTaken.visibility = View.VISIBLE
            binding.tvJoin.visibility = View.GONE
            binding.tvAttend.visibility = View.GONE
        }

        //past classes
        fun showClassPast(classType: String) {
            binding.llBackground.apply {
                background =
                    context.resources.getDrawable(R.drawable.bg_course_timetable_unselected)
            }

            binding.tvClassStatus.apply {
                background =
                    context.resources.getDrawable(R.drawable.bg_class_missed)
                setTextColor(
                    context.resources.getColor(
                        R.color.class_missed_color
                    )
                )
                text =
                    binding.tvClassStatus.context.resources.getString(R.string.labeL_missed_class)
            }

            binding.ivClassTaken.visibility = View.GONE

            if (classType.toInt() == StudentConst.ClassType.Live.value) {
                binding.tvAttend.visibility = View.GONE
                binding.tvJoin.isEnabled = false
                binding.tvJoin.visibility = View.VISIBLE
            } else {
                binding.tvAttend.visibility = View.VISIBLE
                binding.tvJoin.visibility = View.GONE
            }
        }

        //Upcoming classes or future classes
        fun showClassUpcoming(classType: String) {
            binding.llBackground.apply {
                background =
                    context.resources.getDrawable(R.drawable.bg_course_timetable_unselected)
            }

            binding.tvClassStatus.apply {
                background =
                    context.resources.getDrawable(R.drawable.bg_class_scheduled)
                setTextColor(context.resources.getColor(R.color.class_scheduled_color))
                text = binding.tvClassStatus.context.resources.getString(R.string.label_scheduled)
            }

            binding.ivClassTaken.visibility = View.GONE
            if (classType.toInt() == StudentConst.ClassType.Live.value) {
                binding.tvAttend.visibility = View.GONE
                binding.tvJoin.isEnabled = true
                binding.tvJoin.visibility = View.VISIBLE
            } else {
                binding.tvAttend.visibility = View.VISIBLE
                binding.tvJoin.visibility = View.GONE
            }
        }
    }

    companion object {
        private val SESSION_COMPARATOR = object : DiffUtil.ItemCallback<Session>() {
            override fun areItemsTheSame(oldItem: Session, newItem: Session): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Session, newItem: Session): Boolean =
                newItem.id == oldItem.id
        }
    }
}