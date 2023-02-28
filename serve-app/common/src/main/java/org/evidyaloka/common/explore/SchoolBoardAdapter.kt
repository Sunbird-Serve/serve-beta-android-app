package org.evidyaloka.common.explore

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.evidyaloka.common.R
import org.evidyaloka.common.util.Utils
import org.evidyaloka.common.helper.loadDrawable
import org.evidyaloka.common.databinding.ItemSchoolBoardBinding
import org.evidyaloka.core.model.CourseProvider

class SchoolBoardAdapter() :
    RecyclerView.Adapter<SchoolBoardAdapter.SchoolBoardHolder>() {
    private val TAG = "SchoolBoardAdapter"

    //Todo change CourseProvoider type
    private var courseProviders: List<CourseProvider> = listOf()

    private var selectedCourseProvider: CourseProvider? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchoolBoardHolder {
        val binding = ItemSchoolBoardBinding.inflate(
            LayoutInflater.from(parent?.context),
            parent,
            false
        )
        return SchoolBoardHolder(binding)
    }

    fun setItems(courseProviders: List<CourseProvider>) {
        this.courseProviders = courseProviders
        notifyDataSetChanged()
    }

    fun getSelectedCourseProvoider(): CourseProvider?{
        return selectedCourseProvider
    }

    override fun getItemCount(): Int {
        return courseProviders.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(holder: SchoolBoardHolder, position: Int) {
        Log.e(TAG, "onBindViewHolder: ")
        val courseProvider = courseProviders[position]
        courseProvider.let {
            if(selectedCourseProvider == null)
                selectedCourseProvider = it
            holder.bind(it)
        }
    }

    inner class SchoolBoardHolder(val binding: ItemSchoolBoardBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(courseProvider: CourseProvider) {
            with(binding) {
                tvSchoolBoardName.text = courseProvider.name
                rbSchool.isChecked = (courseProvider == selectedCourseProvider)

                try {
                    ivBoardLogo.loadDrawable(
                        Utils.getCourseProvidersDrawable(courseProvider.code),
                        R.drawable.ic_school_logo_placeholder, R.drawable.ic_school_logo_placeholder
                    )
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }

                if (rbSchool.isChecked) {
                    selectedCourseProvider = courseProvider
                }

                rlSchoolBoard.setOnClickListener {
                    selectedCourseProvider = courseProvider
                    notifyDataSetChanged()
                }
            }
        }
    }
}