package org.evidyaloka.partner.ui.student

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
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_student_list.*
import kotlinx.android.synthetic.main.fragment_student_list.etv_grade
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.partner.R
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.AcademicYear
import org.evidyaloka.core.partner.model.CourseOffering
import org.evidyaloka.core.model.CourseProvider
import org.evidyaloka.partner.ui.BaseFragment

@AndroidEntryPoint
class StudentListFragment : BaseFragment() {
    private val TAG = "StudentListFragment"
    private val viewModel: StudentViewModel by viewModels()

    private lateinit var mView: View
    private var digitalSchoolId: Int? = null
    private var gradeSelected: Int? = null
    private var offeringIdSelected: Int? = null
    private var courseProviderSelectedId: Int? = null
    private var academicYearIdSelected: Int? = null

    private var academicYearList: List<AcademicYear> = listOf()
    private var courseProviderList: List<CourseProvider> = listOf()
    private var offeringList: List<CourseOffering> = listOf()
    var adapter = StudentAdapter({ isChecked:Boolean,studentId: Int -> onItemClick(isChecked,studentId) })

    private var courseProviderListFromBundle: ArrayList<CourseProvider>? = null

    //TODO get school id from bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            digitalSchoolId = it.getInt(PartnerConst.DIGITAL_SCHOOL_ID)
            courseProviderListFromBundle = it.getSerializable(PartnerConst.COURSE_PROVIDER) as ArrayList<CourseProvider>
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (!this::mView.isInitialized) {
            mView = inflater.inflate(R.layout.fragment_student_list, container, false)
        }

        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        getEnrolledStudents()

        context?.let {
            rv_students?.let { rv ->
                rv.layoutManager =
                        LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false)
                rv.addItemDecoration(
                        DividerItemDecoration(
                                rv.getContext(),
                                DividerItemDecoration.VERTICAL
                        )
                )
                rv.adapter = adapter
            }
        }

        courseProviderListFromBundle?.let {
            if (it.size > 0) {
                it[0].code?.let {
                    if (!"".equals(it)) {
                        getCourseProvider(it)
                    }
                }
            }
        }

        courseProviderSelectedId?.let {
            getAcademicYearData(it)
            getCourseOfferings()
        }

        academicYearIdSelected?.let {
            loadGrades()
            getEnrolledStudents()
        }

        gradeSelected?.let {
            getCourseOfferings()
            getEnrolledStudents()
        }

        offeringIdSelected?.let {
            getEnrolledStudents()
        }

    }

    /*
    * Function to fetch course providers from API
    */

    private fun getCourseProvider(courseProviderCode: String? = null) {
        try {
            viewModel.getCourseProvider(courseProviderCode).observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        response?.data.let {
                            it?.let { list ->
                                courseProviderList = list as ArrayList<CourseProvider>
                                setCourseProviderAdapter(courseProviderList.map { it.name })
                            }
                        }
                    }
                    is Resource.GenericError -> {
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun setCourseProviderAdapter(list: List<String>) {
        try {
            context?.let {
                var adapter = ArrayAdapter(it, R.layout.spinner_list_item, list)
                etv_course_provider?.setAdapter(adapter)
                //adapter?.notifyDataSetChanged()
                etv_course_provider?.setOnItemClickListener { parent, view, position, id ->
                    offeringIdSelected = null
                    academicYearIdSelected = null
                    gradeSelected = null
                    etv_offerings?.setText("")
                    etv_academic_year?.setText("")
                    etv_grade?.setText("")
                    courseProviderSelectedId = courseProviderList.get(position).id
                    courseProviderSelectedId?.let { getAcademicYearData(it) }
                    getEnrolledStudents()


                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    /*
     * Function to fetch Academic year from API
     */
    private fun getAcademicYearData(cpId: Int) {
        try {
            viewModel.getAcademicYear(cpId).observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        response?.data.let {
                            it?.let { list ->
                                academicYearList = list
                                setAcademicYearAdapter(list.map { it.academicYear })
                            }
                        }
                    }
                    is Resource.GenericError -> {

                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun setAcademicYearAdapter(list: List<String>) {
        try {
            context?.let {
                var adapter = ArrayAdapter(it, R.layout.spinner_list_item, list)
                etv_academic_year?.setAdapter(adapter)
                //adapter?.notifyDataSetChanged()
                etv_academic_year?.setOnItemClickListener { parent, view, position, id ->
                    academicYearIdSelected = academicYearList.get(position).id
                    etv_grade?.setText("")
                    etv_offerings?.setText("")
                    gradeSelected = null
                    offeringIdSelected = null
                    loadGrades()
                    getEnrolledStudents()

                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    private fun updateGradeSpinner(grades: List<Int>) {
        if (grades.size > 0) {
            try {
                context?.let {
                    var adapter = ArrayAdapter(it, R.layout.spinner_list_item, grades)
                    var gradeList = grades
                    etv_grade?.setAdapter(adapter)
                    //adapter?.notifyDataSetChanged()
                    etv_grade?.setOnItemClickListener { adapterView, mView, position, l ->
                        etv_offerings?.setText("")
                        offeringIdSelected = null
                        gradeSelected = gradeList.get(position)
                        getCourseOfferings()
                        getEnrolledStudents()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    /*
     * Function to fetch Grades
     */
    private fun loadGrades() {
        try {
            viewModel.getSettings().observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        response?.data.let {
                            if (it.gradeTo != 0 && it.gradeFrom != 0) {
                                updateGradeSpinner((it.gradeFrom..it.gradeTo).map { it }.sorted())
                            }
                        }
                    }
                    is Resource.GenericError -> {

                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }


    /*
     * Function to fetch Course Offerings from API
     */
    private fun getCourseOfferings() {
        //TODO Change hardcoded dsId = 1
        try {
            digitalSchoolId?.let {
                viewModel.getCourseOfferings(
                        courseProviderSelectedId,
                        it,
                        gradeSelected,
                        academicYearIdSelected
                ).observe(viewLifecycleOwner, Observer { response ->
                    when (response) {
                        is Resource.Success -> {
                            response?.data.let {
                                it?.let {
                                    offeringList = it
                                    //updateSpinners(etv_academic_year, it)
                                    setCourseOfferingsAdapter(it.map { it.courseName })
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
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun setCourseOfferingsAdapter(list: List<String>) {
        try {
            context?.let {
                var adapter = ArrayAdapter(it, R.layout.spinner_list_item, list)
                etv_offerings?.setAdapter(adapter)
                adapter?.notifyDataSetChanged()
                etv_offerings?.setOnItemClickListener { parent, view, position, id ->
                    offeringIdSelected = offeringList.get(position).id
                    getEnrolledStudents()
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    private fun getEnrolledStudents() {
        //TODO Change hardcoded dsId = 1
        try {
            digitalSchoolId?.let {
                viewModel.getEnrolledStudents(
                        PartnerConst.RoleType.DSM.name,
                        courseProviderSelectedId,
                        it,
                        gradeSelected,
                        offeringIdSelected,
                        academicYearIdSelected
                ).observe(viewLifecycleOwner, Observer { list ->
                    adapter?.submitList(list)
                })
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }

        try {
            viewModel.
            studentCountObserver.observe(viewLifecycleOwner, Observer {
                tv_students_count?.text = getString(R.string.total_students).plus(" ($it)")
            })
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    private fun onCourseOfferingClick(courseOffering: CourseOffering) {
        val arg = Bundle()
        arg.putParcelable(PartnerConst.COURSE_OFFERING, courseOffering)
        try{
        findNavController().navigate(R.id.action_courseFragment_to_courseDetailsFragment, arg)
        }catch (e : Exception){
            FirebaseCrashlytics.getInstance().recordException(e)

        }
    }

    fun onItemClick(isChecked:Boolean,studentId: Int) {
        digitalSchoolId?.let {
            val arguments = Bundle().apply {
                putBoolean(PartnerConst.IS_EDIT_STUDENT, true)
                putInt(PartnerConst.DIGITAL_SCHOOL_ID, it)
                putInt(PartnerConst.STUDENT_ID, studentId)
            }
            try{
            navController.navigate(
                    R.id.action_studentListFragment_to_studentsDetailsFragment,
                    arguments
            )
            }catch (e : Exception){
                FirebaseCrashlytics.getInstance().recordException(e)

            }
        }
    }


}