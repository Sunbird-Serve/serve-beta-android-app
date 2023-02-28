package org.evidyaloka.student.ui.timetable.timetableview

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.common.helper.convert24to12a
import org.evidyaloka.core.model.Session
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.ItemTimetableBinding
import org.evidyaloka.student.ui.timetable.OnItemClickListner
import org.evidyaloka.student.utils.CourseCardview

class TimetableAdapter :
    PagedListAdapter<Session, TimetableAdapter.ClassHolder>(
        SESSION_COMPARATOR
    ) {
    private val TAG = "TimetableAdapter"
    var isLastSessionAttended = false
    private var timetables: List<Session> = listOf()
    private var onItemClickListener: OnItemClickListner? = null
    

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassHolder {
        val binding = ItemTimetableBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
//        val courseView = CourseCardview(
//            parent.context,
//            CourseCardview.SessionType.TIMETABLE
//        )
//        binding.cardHolder.addView(courseView)
        return ClassHolder(binding)
    }

    fun setOnItemClickListener(listner: OnItemClickListner) {
        onItemClickListener = listner
    }


    override fun onBindViewHolder(holder: ClassHolder, position: Int) {
        //Todo
        val timetable = getItem(position)
        timetable?.let {
            holder.bind(it)
        }
    }

    inner class ClassHolder(val binding: ItemTimetableBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(timetable: Session) {
            with(binding) {
                //Todo
                timetable?.let { tt ->
                    var cardView = timeTableView
                    cardView.setSubject(tt.subjectName)
                    cardView.setTopic(tt.topicName)
                    tt.teacherName.takeIf { it.isNotBlank() || it.isNotEmpty() }?.let{ cardView.setTeacherName(tt.teacherName) }
                    cardView.setMedium(tt.lngName + " " +binding.root.context.getString(R.string.medium))
                    cardView.setSessionTime(tt.startTime.convert24to12a())
                    if (tt.classType.toInt() == StudentConst.ClassType.Live.value) {
                        cardView.setSessionStatus(CourseCardview.SessionState.LIVE)
                    } else {
                        cardView.setSessionStatus(if (tt.hasAttended) CourseCardview.SessionState.COMPLETE else CourseCardview.SessionState.SCHEDULE)
                    }

                    cardView.setClickListener(object : CourseCardview.OnClickListener {
                        override fun onClick(v: CourseCardview) {
                            onItemClickListener?.OnItemClick(tt)
                        }
                    })
                    Log.e("Attended", "subject: "+tt.subjectName+" "+tt.startTime.convert24to12a()+" attended:"+tt.hasAttended+" isLastSessionAttended:"+isLastSessionAttended)
                    if (tt.hasAttended) {
                        setTimeLineDrawable(binding,true,isLastSessionAttended)
                        isLastSessionAttended = true
                    } else {
                        if (isLastSessionAttended) {
                            setTimeLineDrawable(binding,false,isLastSessionAttended)
                        }else{
                            setSessionScheduled(binding)
                        }
                        isLastSessionAttended = false
                    }
                }

                if (adapterPosition.equals(getItemCount() - 1)) {
                    ivLineType.visibility = View.GONE
                }else{
                    ivLineType.visibility = View.VISIBLE
                }
                if (adapterPosition == 0) {
                    ivLineType2.visibility = View.INVISIBLE
                }else{
                    ivLineType2.visibility = View.VISIBLE
                }
            }

            binding.root.setOnClickListener {
                //Todo
            }
        }

        fun setTimeLineDrawable(binding: ItemTimetableBinding, isCompleteTimeline: Boolean, isLastAttended:Boolean=false) {
            binding.ivClassStatus.setImageDrawable(
                binding.ivClassStatus.resources.getDrawable(
                    if(isCompleteTimeline) R.drawable.ic_attended else R.drawable.ic_upcoming,
                    null
                )
            )
            if(isCompleteTimeline) {
                binding.ivLineType.setBackgroundResource(R.drawable.vertical_line_orange)
            }else{
                binding.ivLineType.setBackgroundResource(R.drawable.vertical_line_gray)
            }
            if(isLastAttended){
                Log.e("ivLineType2","vertical_line_orange "+isLastAttended)
                binding.ivLineType2.setBackgroundResource(R.drawable.vertical_line_orange)
            }else {
                Log.e("ivLineType2","vertical_line_gray "+isLastAttended)
                binding.ivLineType2.setBackgroundResource(R.drawable.vertical_line_gray)
            }
        }


        fun setSessionScheduled(binding: ItemTimetableBinding) {
            binding.ivClassStatus.setImageDrawable(
                binding.ivClassStatus.resources.getDrawable(
                    R.drawable.ic_unattended,
                    null
                )
            )
            binding.ivLineType.setBackgroundResource(R.drawable.vertical_line_gray)
            binding.ivLineType2.setBackgroundResource(R.drawable.vertical_line_gray)
        }

        
    }

    companion object {
        private val SESSION_COMPARATOR = object : DiffUtil.ItemCallback<Session>() {
            override fun areItemsTheSame(oldItem: Session, newItem: Session): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Session, newItem: Session): Boolean =
                newItem == oldItem
        }
    }
}