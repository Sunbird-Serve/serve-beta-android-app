package org.evidyaloka.partner.ui.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_course_details.*
import kotlinx.android.synthetic.main.item_course.view.*
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.partner.R
import org.evidyaloka.core.partner.model.CourseOffering
import org.evidyaloka.partner.ui.BaseFragment
import org.evidyaloka.common.util.Utils
import org.evidyaloka.partner.ui.helper.Helper

/**
 * Class for showcasing Course Details
 */
@AndroidEntryPoint
class CourseDetailsFragment : BaseFragment() {
    private val viewModel: CourseViewModel by viewModels()
    private lateinit var mView: View

    private var courseOffering: CourseOffering = CourseOffering()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        courseOffering = arguments?.get(PartnerConst.COURSE_OFFERING) as CourseOffering
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if (!this::mView.isInitialized)
            mView = inflater.inflate(R.layout.fragment_course_details, container, false)
        return mView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        courseOffering?.let {
            updateUI(it)
        }
    }

    /*
     * update UI
     * @param courseOffering CourseOffering
     */
    private fun updateUI(courseOffering: CourseOffering) {

        var drawableInt = Helper.courseIcon(courseOffering.courseName)
        try {
            context?.let {
                iv_course?.let { iv ->
                    Glide.with(it)
                        .load(drawableInt)
                        .placeholder(R.drawable.ic_course_placeholder)
                        .error(R.drawable.ic_course_placeholder)
                        .into(iv)
                }
            }
        } catch (e: Exception) {

        }

        tv_course_title?.setText(courseOffering.courseName)
//        tv_course_grade?.setText(courseOffering.grade)
        val grade = courseOffering.grade.takeIf { it.isNotEmpty() }?.toInt()
        grade?.let {
            tv_course_grade?.setText(
                Utils.getFormatedGrade(it).plus(" ").plus(
                    Utils.ordinalSuffix(it)).plus(" ").plus(resources.getString(R.string.label_grade)))
        }
        tv_course_provider?.setText(courseOffering.CourseProvider)
        tv_academic_year?.setText(courseOffering.AcademicYear)

        Utils.formatDateInddMMMMyyyy(courseOffering.StartDate).let {
            tv_start_date?.setText(Utils.formatDateInddMMMMyyyy(courseOffering.StartDate))
        }

        Utils.formatDateInddMMMMyyyy(courseOffering.EndDate).let {
            tv_end_date?.setText(Utils.formatDateInddMMMMyyyy(courseOffering.EndDate))
        }

    }
}