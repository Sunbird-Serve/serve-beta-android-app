package org.evidyaloka.student.ui.timetable.timetableview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import org.evidyaloka.core.model.Subject
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.LayoutSubjectAdapterBinding
import org.evidyaloka.common.util.CourseTimetableViewUtils
import org.evidyaloka.common.util.SubjectViewUtils

/**
 * @author Madhankumar
 * created on 13-04-2021
 *
 */
class SubjectAdapter(val list: List<Subject>, val listner: OnSubjectClickListner) :
    BaseAdapter() {
    private val TAG= "SubjectAdapter"

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    override fun getCount(): Int {
        return list.size
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     * data set.
     * @return The data at the specified position.
     */
    override fun getItem(position: Int): Any? {
        return if (null != list) list.get(position) else null
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * [android.view.LayoutInflater.inflate]
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position The position of the item within the adapter's data set of the item whose view
     * we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     * is non-null and of an appropriate type before using. If it is not possible to convert
     * this view to display the correct data, this method can create a new view.
     * Heterogeneous lists can specify their number of view types, so that this View is
     * always of the right type (see [.getViewTypeCount] and
     * [.getItemViewType]).
     * @param parent The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var binding = LayoutSubjectAdapterBinding.inflate(
            LayoutInflater.from(parent?.context),
            parent,
            false
        )
        val subjectItem = list[position]
        with(binding) {
            subjectItem?.let { subject ->
                val courseUI = CourseTimetableViewUtils.getCourseUISettings(subject.subjectName)
                cardview.setBackgroundResource(SubjectViewUtils.getUIBackground(subject.subjectName))
                icon.setImageResource(courseUI.icon())
                this.subject.text = subject.subjectName
//                try{
//                    circularProgressbar.progressDrawable = circularProgressbar
//                        .context?.resources?.getDrawable(courseUI.progressDrawable(), null)
//                }catch (e : Exception){
//                    FirebaseCrashlytics.getInstance().recordException(e)
//                }
                parent?.context?.let { context ->
                    count.text = String.format(
                        context.getString(R.string.subject_count),
                        *arrayOf(subject.totalTopicsViewed, subject.totalTopics)
                    )
                }
//                setSecondaryProgress(rlProgressBar.context, Util.dakerColor(courseUI.progressColor(),0.8F))
                cardview.setOnClickListener {
                    listner?.OnItemClick(subject)
                }
                circularProgressbar.max = subject.totalTopics
                circularProgressbar.progress = subject.totalTopicsViewed
            }
        }
        return binding.root
    }
}
