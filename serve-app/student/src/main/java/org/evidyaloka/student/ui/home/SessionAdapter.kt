package org.evidyaloka.student.ui.home

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.common.helper.convertShortDateWitha
import org.evidyaloka.core.model.Session
import org.evidyaloka.student.R
import org.evidyaloka.student.ui.timetable.OnItemClickListner
import org.evidyaloka.student.utils.CourseCardview

/**
 * @author Madhankumar
 * created on 16-03-2021
 *
 */
class SessionAdapter: PagedListAdapter<Session,SessionAdapter.SessionHolder>(SESSION_COMPARATOR) {

    private var selectedCoursesId: MutableList<Int> = mutableListOf()
    private var onItemClickListener: OnItemClickListner? = null

    inner class SessionHolder(val view:View): RecyclerView.ViewHolder(view) {
        fun bind(session: Session){
            (view as CourseCardview).setSubject(session.subjectName)
            view.setTopic(session.topicName)
            view.setSessionTime(session.calDate.plus(" ").plus(session.startTime).convertShortDateWitha())
            view.setMedium(session.lngName + " " +view.context.getString(R.string.medium))
            if(session.classType.toInt() == StudentConst.ClassType.Live.value){
                view.setSessionStatus(CourseCardview.SessionState.LIVE)
            }else {
                view.setSessionStatus(if (session.hasAttended) CourseCardview.SessionState.COMPLETE else CourseCardview.SessionState.SCHEDULE)
                Log.e("session status : ",session.hasAttended.toString()+" --- "+ session.startTime )
            }
            view.setClickListener(object: CourseCardview.OnClickListener {
                override fun onClick(v: CourseCardview) {
                    onItemClickListener?.OnItemClick(session)
                    if (v.isChecked) {
                        selectedCoursesId.add(session.offeringId)
                    }else{
                        selectedCoursesId.remove(session.offeringId)
                    }
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionHolder {
        val courseView = CourseCardview(
            parent.context,
            CourseCardview.SessionViewType.COURSE
        )
        courseView.elevation = parent.context.resources.getDimension(R.dimen.cardview_default_elevation)
        courseView.cardElevation = parent.context.resources.getDimension(R.dimen.cardview_default_elevation)
        return SessionHolder(courseView)
    }



    override fun onBindViewHolder(holder: SessionHolder, position: Int) {
        val session = getItem(position)
        session?.let {
            holder.bind(it)
        }
    }

    fun setOnItemClickListener(listner: OnItemClickListner){
        onItemClickListener = listner
    }

    fun getSelectedCourseId(): List<Int> = selectedCoursesId


    companion object {
        private val SESSION_COMPARATOR = object : DiffUtil.ItemCallback<Session>() {
            override fun areItemsTheSame(oldItem: Session, newItem: Session): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Session, newItem: Session): Boolean =
                newItem.id == oldItem.id
        }
    }
}