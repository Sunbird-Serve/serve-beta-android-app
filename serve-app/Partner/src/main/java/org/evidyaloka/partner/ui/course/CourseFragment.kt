package org.evidyaloka.partner.ui.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_courses.*
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.partner.R
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.AcademicYear
import org.evidyaloka.core.partner.model.CourseOffering
import org.evidyaloka.core.model.CourseProvider
import org.evidyaloka.partner.ui.BaseFragment
import org.evidyaloka.partner.ui.digitalschool.DIGITAL_SCHOOL_ID

/**
 * CourseFragment class is responsible for listing all the courses offered by a School
 */
@AndroidEntryPoint
class CourseFragment : BaseFragment() {

    private val TAG = "CourseFragment"
    private val viewModel: CourseViewModel by viewModels()
    private lateinit var mView: View

    private var digitalSchoolId: Int? = null
    private var gradeSelected: Int? = null
    private var courseProviderSelectedId: Int? = null
    private var academicYearIdSelected: Int? = null

    private var academicYearList = ArrayList<AcademicYear>()
    private var courseProviderList = ArrayList<CourseProvider>()

    private var courseProviderListFromBundle: ArrayList<CourseProvider>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            digitalSchoolId = it.getInt(DIGITAL_SCHOOL_ID)
            courseProviderListFromBundle = it.getSerializable(PartnerConst.COURSE_PROVIDER) as ArrayList<CourseProvider>
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (!this::mView.isInitialized) {
            mView = inflater.inflate(R.layout.fragment_courses, container, false)
        }
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        loadCourseOfferings()
        courseProviderListFromBundle?.let {
            if (it.size > 0) {
                it[0].code?.let {
                    if (!"".equals(it)) {
                        loadCourseProviderData(it)
                    }
                }
            }
        }
        loadGrades()

        etv_course_provider?.let {
            updateSpinners(it, courseProviderList)
        }
        courseProviderSelectedId?.let {
            loadAcademicYearData(it)
        }
    }

    /**
     * function to update/load various spinners
     * @param view MaterialAutoCompleteTextView;
     * @param list: List<T>
     */
    private fun <T> updateSpinners(view: MaterialAutoCompleteTextView, list: List<T>) {
        try {
            context?.let {
                var adapter = ArrayAdapter(it, R.layout.spinner_list_item, list)
                view?.setAdapter(adapter)
                adapter?.notifyDataSetChanged()

                view?.setOnItemClickListener { adapterView, mView, position, l ->
                    when (view?.id) {
                        R.id.etv_academic_year -> {
                            etv_grade?.setText("")
                            gradeSelected = null
                            academicYearIdSelected = academicYearList[position].id
                            loadCourseOfferings()

                        }
                        R.id.etv_grade -> {
                            var gradeList = list as List<Int>
                            gradeSelected = gradeList[position]
                            loadCourseOfferings()
                        }
                        R.id.etv_course_provider -> {
                            courseProviderSelectedId = courseProviderList[position].id
                            etv_grade?.setText("")
                            etv_academic_year.setText("")
                            gradeSelected = null
                            academicYearIdSelected = null
                            loadAcademicYearData(courseProviderSelectedId)
                            loadCourseOfferings()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*
    * Function to fetch course providers from API
    * @param courseProviderCode String; default value null
    */

    private fun loadCourseProviderData(courseProviderCode: String? = null) {
        try {
            viewModel.getCourseProvider(courseProviderCode).observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        response?.data.let {

                            it?.let { list ->
                                courseProviderList = list as ArrayList<CourseProvider>
                                etv_course_provider?.let {etv_course_provider->
                                    updateSpinners(etv_course_provider, courseProviderList.map { it.name })
                                }
                            }
                        }
                    }
                    is Resource.GenericError -> {

                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*
     * Function to fetch Grades from shared Preferences
     */
    private fun loadGrades() {
        try {
            viewModel.getSettings().observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        response?.data.let {
                            etv_grade?.let { etv_grade ->
                                if (it.gradeTo != 0 && it.gradeFrom != 0) {
                                    updateSpinners(etv_grade, (it.gradeFrom..it.gradeTo).map { it }.sorted())
                                }
                            }
                        }
                    }
                    is Resource.GenericError -> {

                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*
     * Function to fetch Academic year from API
     * @param courseProviderId Int?
     */
    private fun loadAcademicYearData(cpId: Int?) {
        try {
            cpId?.let {
                viewModel.getAcademicYear(it).observe(viewLifecycleOwner, Observer { response ->
                    when (response) {
                        is Resource.Success -> {
                            response?.data.let {
                                it?.let { it1 ->
                                    it1 as List<AcademicYear>
                                    academicYearList = ArrayList(it1 )
                                    etv_academic_year?.let { etv_academic_year ->
                                        updateSpinners(etv_academic_year, it1.map { it.academicYear })
                                    }
                                }
                            }
                        }
                        is Resource.GenericError -> {
                        }
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*
     * Function to fetch Course Offerings associated with a digital school from API
     */
    private fun loadCourseOfferings() {
        //TODO Change hardcoded dsId = 1
        try {
            digitalSchoolId?.let {

                viewModel.getCourseOfferings(courseProviderSelectedId, it, gradeSelected, academicYearIdSelected).observe(viewLifecycleOwner, Observer { response ->
                    when (response) {
                        is Resource.Success -> {
                            response?.data.let {
                                it?.let {
                                    setCourseAdapter(it as List<CourseOffering>)
                                }
                            }
                        }
                        is Resource.GenericError -> {
                        }
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadCourseData() {
        try {
            viewModel.getCourseProvider().observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        response?.data.let {
                            it?.let { it1 ->
                                courseProviderList = it1 as ArrayList<CourseProvider>
                                etv_course_provider?.let { etv_course_provider ->
                                    updateSpinners(etv_course_provider, it1.map { it.name })
                                }
                            }
                        }
                    }
                    is Resource.GenericError -> {
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * this function uses CourseAdapter to show all the course offerings
     * @param list List<CourseOffering>
     */
    private fun setCourseAdapter(list: List<CourseOffering>) {
        try {
            var adapter = CourseAdapter(list,
                    { selectedCourseOffering: CourseOffering -> onCourseOfferingClick(selectedCourseOffering) })

            context?.let { context ->
                rv_users?.let {
                    it.layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    it.addItemDecoration(
                            DividerItemDecoration(
                                    rv_users.getContext(),
                                    DividerItemDecoration.VERTICAL
                            )
                    )
                    it.adapter = adapter
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * handles onClick event for CourseAdapter; loads course offering
     */
    private fun onCourseOfferingClick(courseOffering: CourseOffering) {
        val arg = Bundle()
        arg.putParcelable(PartnerConst.COURSE_OFFERING, courseOffering)
        try{
        findNavController().navigate(R.id.action_courseFragment_to_courseDetailsFragment, arg)
        }catch (e : Exception){
            FirebaseCrashlytics.getInstance().recordException(e)

        }
    }


}