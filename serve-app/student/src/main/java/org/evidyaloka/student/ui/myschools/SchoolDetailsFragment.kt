package org.evidyaloka.student.ui.myschools

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.model.Course
import org.evidyaloka.core.student.model.School
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.FragmentSchoolDetailsBinding
import org.evidyaloka.student.ui.BaseFragment
import org.evidyaloka.student.ui.StudentHomeActivity
import org.evidyaloka.student.ui.schedulecourse.adapter.CourseAdapter
import org.evidyaloka.student.ui.student.MyCourseAdapter
import org.evidyaloka.student.utils.Util


@AndroidEntryPoint
class SchoolDetailsFragment : BaseFragment() {

    private val TAG = SchoolDetailsFragment::class.java.simpleName
    private lateinit var binding: FragmentSchoolDetailsBinding
    private val viewModel: SchoolViewModel by viewModels()
    private val courseAdapter =
        CourseAdapter()
    private val myCourseAdapter =
        MyCourseAdapter()
    private var school: School? = null
    private var allSubjectsText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val args =
                SchoolDetailsFragmentArgs.fromBundle(it)
            school = args.school
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSchoolDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        school?.let { school ->
            try {
                activity?.let { (it as StudentHomeActivity).setToolbarTitle(school.name) }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

        binding.tvStudyTimings.setOnClickListener {
            Log.e(TAG, "onViewCreated: buttn click ")
            try{
            navController?.navigate(
                SchoolDetailsFragmentDirections.actionSchoolDetailsFragmentToStudyTimingFragment(
                    allSubjectsText
                )
            )}catch (e : Exception){
                FirebaseCrashlytics.getInstance().recordException(e)

            }
        }


        binding.rvMyCourses.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = myCourseAdapter
        }

        binding.rvCourseList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = courseAdapter
        }

        /* myCourseAdapter.setClickListener(object : OnCourseClickListener {
             override fun OnItemClick(course: Course) {
                 //Do nothing
             }
         })*/

        viewModel.getStudentOfferings()?.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    it.data?.let {
                        myCourseAdapter.setItems(it)

                        var first = true

                        it.forEach {
                            if (first) {
                                allSubjectsText = it.name
                                first = false
                            } else {
                                allSubjectsText = allSubjectsText.plus(", ").plus(it.name)
                            }
                        }
                        getSchoolOfferings(it)
                    }
                }
                is Resource.GenericError -> {
                    showPopup(
                        getString(R.string.label_sorry),
                        getString(R.string.could_not_able_to_fetch_data)
                    )
                }
            }
        })

        binding.btLearn.setOnClickListener {
            buildRequest()?.let {
                viewModel.addAdditionalOfferings(it).observe(viewLifecycleOwner, Observer {
                    when (it) {
                        is Resource.Success -> {
                            Log.e(TAG, "Success")
                            when (it.data?.id) {
                                1 -> {
                                    //Todo update required
                                    var offeringList =
                                        buildRequest()?.get(StudentConst.OFFERINGS) as List<Int>
                                    var offerings = offeringList.toIntArray()
                                    try{
                                    navController?.navigate(
                                        SchoolDetailsFragmentDirections.actionSchoolDetailsFragmentToChangeTimingFragment(
                                            true, offerings, school, allSubjectsText
                                        )
                                    )}catch (e : Exception){
                                        FirebaseCrashlytics.getInstance().recordException(e)

                                    }
                                }
                                0 -> {
                                    //Todo open dashboard!
                                    try{
                                    navController?.navigate(
                                        SchoolDetailsFragmentDirections.actionGlobalNavHome()
                                    )}catch (e : Exception){
                                        FirebaseCrashlytics.getInstance().recordException(e)

                                    }
                                }
                            }
                        }
                        is Resource.GenericError -> {
                            it.code?.let {
                                if (it <= 0)
                                    return@Observer
                            }
                            when (it.error?.code) {
                                2, 3, 36, 38 -> showPopup(
                                    getString(R.string.error),
                                    getString(R.string.data_submited_not_valid)
                                )
                                35 -> showPopup(
                                    getString(R.string.error),
                                    getString(R.string.user_should_be_guardian)
                                )
                                19 -> showPopup(
                                    getString(R.string.error),
                                    getString(R.string.digital_school_doesnot_exist)
                                )
                                16 -> showPopup(
                                    getString(R.string.error),
                                    getString(R.string.center_doesnot_exist)
                                )
                                56 -> showPopup(
                                    getString(R.string.error),
                                    getString(R.string.error_invalid_offerings)
                                )
                            }
                        }
                    }
                })
            } ?: showPopup(getString(R.string.error), getString(R.string.please_select_course))
        }
    }

    fun <T> Collection<T>.filterNotIn(collection: Collection<T>): Collection<T> {
        val set = collection.toSet()
        return filterNot { set.contains(it) }
    }

    fun getSchoolOfferings(myCourses: List<Course>) {
        viewModel.getSchoolOfferings()?.observe(viewLifecycleOwner, Observer {

            when (it) {
                is Resource.Success -> {
                    it.data?.let {
                        val courses: MutableList<Course> = mutableListOf()
                        it?.forEach { course ->
                            courses.add(Course(course.id, course.name, course.isSelected))
                        }
                        var additionalCourses = courses.filterNotIn(myCourses)

                        courseAdapter.setItem(additionalCourses as List<Course>)
                        binding.btLearn.isEnabled = additionalCourses?.size > 0
                    }
                }
                is Resource.GenericError -> {
                    showPopup(
                        getString(R.string.label_sorry),
                        getString(R.string.could_not_able_to_fetch_data)
                    )
                }
            }
        })
    }

    fun buildRequest(): Map<String, Any>? {
        return courseAdapter.getSelectedCourseId().takeIf { it.isNotEmpty() }?.let {
            HashMap<String, Any>().apply {
                this[StudentConst.OFFERINGS] = courseAdapter.getSelectedCourseId().distinct()
                school?.id?.let {
                    this[StudentConst.DIGITAL_SCHOOL_ID] = it
                }
            }
        }

    }
}