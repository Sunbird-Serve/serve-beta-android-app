package org.evidyaloka.partner.ui.course

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.GsonBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_course.*
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.partner.R
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.common.view.SuccessDialogFragment
import org.evidyaloka.core.partner.model.AcademicYear
import org.evidyaloka.core.model.Course
import org.evidyaloka.core.model.CourseProvider
import org.evidyaloka.core.helper.ErrorData
import org.evidyaloka.partner.ui.BaseFragment
import java.util.*
import org.evidyaloka.common.util.Utils
import org.evidyaloka.core.partner.model.Language
import kotlin.collections.ArrayList

/*
 * This class is to Add Course / Offerings.
 */
@AndroidEntryPoint
class AddCourseFragment : BaseFragment() {
    private val TAG = "AddCourseFragment"

    private val viewModel: CourseViewModel by viewModels()
    private lateinit var mView: View
    private var courseProviderList = ArrayList<CourseProvider>()
    private var academicYearList = ArrayList<AcademicYear>()
    private var courseList = ArrayList<Course>()
    private var courseProviderListFromBundle: ArrayList<CourseProvider>? = null
    private var digitalSchoolId: Int? = null
    private var digitalSchoolName: String? = null

    private var digitalSchoolIdSelected = 0
    private var gradeSelected = 0
    private var courseIdSelected = 0
    private var courseProviderCode = ""
    private var languageId = 0

    private var academicYearIdSelected = 0
    private var startDataSelected = 0L
    private var endDataSelected = 0L

    //private var courseProviderIdSelected  = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            digitalSchoolId = it.getInt(PartnerConst.DIGITAL_SCHOOL_ID)
            digitalSchoolName = it.getString(PartnerConst.DIGITAL_SCHOOL_NAME)
            courseProviderListFromBundle = it.getSerializable(PartnerConst.COURSE_PROVIDER) as ArrayList<CourseProvider>
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (!this::mView.isInitialized)
            mView = inflater.inflate(R.layout.fragment_add_course, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        // Load Course Provider based on the digitalSchool
        courseProviderListFromBundle?.let {
            if (it.size > 0) {
                it[0].code?.let {
                    if (!"".equals(it)) {
                        loadCourseProviderData(it)
                    }
                }
            }
        }

        bt_submit?.setOnClickListener {
            handleAddCourse(false)
        }

        bt_submit_another?.setOnClickListener {
            handleAddCourse(true)
        }

        val materialDateBuilder: MaterialDatePicker.Builder<*> =
                MaterialDatePicker.Builder.datePicker()
        materialDateBuilder?.setTheme(R.style.CalendarTheme)
                .setTitleText(getString(R.string.calendar_select_date));
        val materialDatePicker = materialDateBuilder
                .build();

        etv_start_date?.setOnClickListener {
            if (!materialDatePicker?.isAdded) {
                materialDatePicker?.show(childFragmentManager, "START_DATE")
            }
        }

        etv_end_date?.setOnClickListener {
            if (!materialDatePicker.isAdded) {
                materialDatePicker?.show(childFragmentManager, "END_DATE")
            }
        }

        materialDatePicker?.addOnPositiveButtonClickListener {
            if (it is Long) {
                var date = Date(it)
                if (materialDatePicker.tag.equals("START_DATE")) {
                    var startDate = Utils.format_dd_MM_yyyy(date.time)
                    startDate?.let {
                        etv_start_date?.setText(it)
                    }
                    startDataSelected = date.time.toLong() / 1000
                } else {
                    var endDate = Utils.format_dd_MM_yyyy(date.time)
                    endDate?.let {
                        etv_end_date?.setText(it)
                    }
                    endDataSelected = date.time.toLong() / 1000

                }
            }
        }
    }

    /*
    * Fetch course providers
    * @param courseProviderCode String?, by default null > will load all the course providers
    */

    private fun loadCourseProviderData(courseProviderCode: String? = null) {
        try {
            viewModel.getCourseProvider(courseProviderCode).observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        response?.data.let {
                            it?.let { list ->
                                courseProviderList = list as ArrayList<CourseProvider>
                                updateCourseProviderSpinner(courseProviderList.map { it.name })
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


    private fun updateCourseProviderSpinner(list: List<String>){
        etv_course_provider.let {
            var adapter = ArrayAdapter(requireContext(), R.layout.spinner_list_item, list)
            it.setAdapter(adapter)
            adapter?.notifyDataSetChanged()
            it.setOnItemClickListener { adapterView, mView, position, l ->
                courseProviderList[position].code?.let {
                    courseProviderCode = it
                }
                etv_grade?.setText("")
                etv_course?.setText("")
                etv_academic_year?.setText("")
                etv_start_date?.setText("")
                etv_end_date?.setText("")
                getLanguages()
                loadGrades()
                loadAcademicYearData(courseProviderList.get(position).id)
            }
        }

    }

    /*
    * Fetch course providers
    * @param courseProviderCode String?, by default null > will load all the course providers
    */

    private fun getLanguages() {
        try {
            viewModel.getLanguages().observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let {
                            updateLanguageSpinner(it)
                        }
                    }
                    is Resource.GenericError -> {
                        handleGenericError(response.error)
                    }
                }
            })
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }
    private fun updateLanguageSpinner(list: List<Language>){
        etv_medium_of_instruction.let {
            var adapter = ArrayAdapter(requireContext(), R.layout.spinner_list_item, list.map{it.name}.sorted())
            it.setAdapter(adapter)
            adapter?.notifyDataSetChanged()
            it.setOnItemClickListener { adapterView, mView, position, l ->
                list[position]?.let {
                    languageId = it.id
                }
                etv_grade?.setText("")
                etv_course?.setText("")
                etv_academic_year?.setText("")
                etv_start_date?.setText("")
                etv_end_date?.setText("")
            }
        }

    }

    /*
     * Get Grades from Shared Preference.
     */
    private fun loadGrades() {
        try {
            viewModel.getSettings().observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        response?.data.let {
                            if (it.gradeTo != 0 && it.gradeFrom != 0) {
                                updateGradeSpinner((it.gradeFrom..it.gradeTo).sorted().map { it })
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

    private fun updateGradeSpinner(list: List<Int>){
        etv_grade.let {
            var adapter = ArrayAdapter(requireContext(), R.layout.spinner_list_item, list)
            it.setAdapter(adapter)
            adapter?.notifyDataSetChanged()
            it.setOnItemClickListener { adapterView, mView, position, l ->
                etv_course?.setText("")
                etv_academic_year?.setText("")
                etv_start_date?.setText("")
                etv_end_date?.setText("")
                gradeSelected = list.get(position)
                loadCourseData()
            }
        }
    }

    /*
     * Fetch Academic Years From API
     * @param Int courseProviderId
     */
    private fun loadAcademicYearData(courseProviderId: Int) {
        try {
            viewModel.getAcademicYear(courseProviderId).observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        response?.data.let {
                            it?.let { it1  ->
                                it1 as List<AcademicYear>
                                academicYearList = ArrayList(it1)
                                updateAcademicYearSpinner(it1.map { it.academicYear })
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

    private fun updateAcademicYearSpinner(list: List<String>){
        etv_academic_year.let {
            var adapter = ArrayAdapter(requireContext(), R.layout.spinner_list_item, list)
            it.setAdapter(adapter)
            adapter?.notifyDataSetChanged()
            it.setOnItemClickListener { adapterView, mView, position, l ->
                etv_start_date?.setText("")
                etv_end_date?.setText("")
                academicYearIdSelected = academicYearList.get(position).id
            }
        }
    }

/*
 * Function to fetch Course Offerings from API
 */
/*private fun loadCourseOfferings() {
try{
    viewModel.getCourseOfferings().observe(viewLifecycleOwner, Observer { response ->
        when (response) {
            is Resource.Loading -> showProgressCircularBar()
            is Resource.Success -> {
                hideProgressCircularBar()
                response?.data.let {
                    if (it != null) {
                        updateSpinners(etv_academic_year, it)
                    }
                }
            }
            is Resource.GenericError -> {
                hideProgressCircularBar()
            }
        }
    })}catch (e: Exception){
            e.printStackTrace()
        }
}*/

    /*
     * Fetch Courses for selected grade & course provider
     *
     */
    private fun loadCourseData() {
        try {
            viewModel.getCourse(gradeSelected, courseProviderCode,languageId)
                    .observe(viewLifecycleOwner, Observer { response ->
                        when (response) {
                            is Resource.Success -> {
                                response?.data.let {
                                    it?.takeIf { it.isNotEmpty() }?.let { it1 ->
                                        courseList = it1 as ArrayList<Course>
                                        updateCourseSpinner(it1.map { it.name })
                                    }?:showNoContentAlert()
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

    private fun updateCourseSpinner(list: List<String>){
        etv_course.let {
            var adapter = ArrayAdapter(requireContext(), R.layout.spinner_list_item, list)
            it.setAdapter(adapter)
            adapter?.notifyDataSetChanged()
            it.setOnItemClickListener { adapterView, mView, position, l ->
                etv_academic_year?.setText("")
                etv_start_date?.setText("")
                etv_end_date?.setText("")
                courseIdSelected = courseList.get(position).id
                digitalSchoolId?.let { schoolId ->
                    bt_submit?.isEnabled = false
                    bt_submit_another?.isEnabled = false
                    checkCourse(courseIdSelected, schoolId)
                }
            }
        }
    }

    private fun showNoContentAlert(){
        MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialogTheme).apply {
            setTitle(getString(R.string.warning))
            setMessage(getString(R.string.no_content_available_for_grade))
            setPositiveButton(getString(R.string.ok)){
                dialog: DialogInterface?, which: Int ->
                gradeSelected = 0
                etv_grade?.text = null
                dialog?.dismiss()
            }
        }.create().show()
    }

    /*
     *Validate course details before passing data to API
     */
    private fun validateCourseDetails(): Boolean {
        var valid = true

        til_course_provider?.error =
                getString(R.string.invalid_provider).takeIf {
                    etv_course?.text.toString().isNullOrEmpty()
                }?.also {
                    valid = false
                }

        til_grade?.error =
                getString(R.string.invalid_grade).takeIf { etv_grade?.text.toString().isNullOrEmpty() }
                        ?.also {
                            valid = false
                        }

        til_course?.error =
                getString(R.string.invalid_course).takeIf {
                    etv_course?.text.toString().isNullOrEmpty()
                }
                        ?.also {
                            valid = false
                        }

        til_academic_year?.error =
                getString(R.string.invalid_academic).takeIf {
                    etv_academic_year?.text.toString().isNullOrEmpty()
                }
                        ?.also {
                            valid = false
                        }
        til_start_date?.error =
                getString(R.string.invalid_start_date).takeIf {
                    etv_start_date?.text.toString().isNullOrEmpty()
                }
                        ?.also {
                            valid = false
                        }

        til_end_date?.error =
                getString(R.string.invalid_end_date).takeIf {
                    etv_end_date?.text.toString().isNullOrEmpty()
                }
                        ?.also {
                            valid = false
                        }

        if (startDataSelected > endDataSelected) {

            til_start_date?.error =
                    getString(R.string.error_start_bigger_end).takeIf {
                        startDataSelected > endDataSelected
                    }
                            ?.also {
                                valid = false
                            }

        }

        return valid
    }

    /*
     * This function adds courses
     * @Param addMore Boolean to add More courses after adding one course
     */
    fun handleAddCourse(addMore: Boolean) {
        try {
            hideKeyboard()
            if (validateCourseDetails()) {
                buildRequestData()?.let {
                    viewModel.addCourse(it).observe(viewLifecycleOwner, Observer { response ->
                        when (response) {
                            is Resource.Success -> {
                                clearAllDetails()
                                if (addMore) {
                                    showSuccessView(true)
                                } else {
                                    showSuccessView(false)
                                }
                            }
                            is Resource.GenericError -> {
                                handleGenericError(response.error)
                            }
                        }
                    })
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun checkCourse(courseIdSelected: Int, digitalSchoolId: Int) {
        viewModel.checkCourse(courseIdSelected,digitalSchoolId).observe(viewLifecycleOwner , Observer { response ->
            when(response){
                is Resource.Success -> {
                    if(response.data?.isSimilarOfferingExist == true){
                        showDuplicateCourseAlert()
                    }else{
                        bt_submit?.isEnabled = true
                        bt_submit_another?.isEnabled = true
                    }
                }

                is Resource.GenericError -> {

                }
            }

        })
    }

    /*
     * Function to show successful response
     * @stayOnPage Boolean whether to stay on page or not
     */
    private fun showSuccessView(stayOnPage: Boolean) {
        context?.let {
            val onDismissListener = object: SuccessDialogFragment.OnDismissListener{
                override fun OnDismiss(dialog: DialogInterface) {
                    if (!stayOnPage) {
                        try{
                        navController.navigateUp()
                        }catch (e : Exception){
                            FirebaseCrashlytics.getInstance().recordException(e)

                        }
                    }
                }
            }
            SuccessDialogFragment.Builder(it)
                    .setIsDialogCancelable(true)
                    .setTitle(getString(R.string.thank_you))
                    .setDescription(String.format(getString(R.string.course_created_success), digitalSchoolName))
                    .setDismissTimer(PartnerConst.DIALOG_CLOSE_TIME)
                    .setIcon(R.drawable.ic_thumbs_up)
                    .setViewType(SuccessDialogFragment.DIALOG_TYPE.FULL_SCREEN)
                    .setOnDismissListener(onDismissListener)
                    .build()
                    .show(childFragmentManager,"")
        }
    }


    /*
     * Clear all the Edit/Spinner fields
     */
    fun clearAllDetails() {
        try {
            etv_course_provider?.setText("")
            etv_grade?.setText("")
            etv_course?.setText("")
            etv_academic_year?.setText("")
            etv_start_date?.setText("")
            etv_end_date?.setText("")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*
     * Builds Map request data for the add course offering API
     */
    fun buildRequestData(): Map<String, Any>? {
        var map: HashMap<String, Any>? = null
        try {
            map = hashMapOf()

            digitalSchoolId?.let { map.put(PartnerConst.DIGITAL_SCHOOL_ID, it) }
            map.put(PartnerConst.COURSE_ID, courseIdSelected)
            map.put(PartnerConst.ACADEMIC_YEAR_ID, academicYearIdSelected)
            map.put(PartnerConst.START_DATE, startDataSelected)
            map.put(PartnerConst.END_DATE, endDataSelected)

            var jsonData = GsonBuilder().create().toJson(map)

            Log.e(TAG, "buildRequestData: " + jsonData)
        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "gson converter exception")
        }
        return map
    }

    /*
     * Handles Generic Error.
     */
    private fun handleGenericError(error: ErrorData?) {
        error?.let {
            when (error.code) {
                16 -> {
                    showSnackBar(getString(R.string.error_center_does_not_exist))
                }
            }
        }
    }

    private fun showDuplicateCourseAlert(){
        MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialogTheme).apply {
            setMessage(getString(R.string.course_already_exist))
            setPositiveButton(getString(R.string.yes)){
                    dialog: DialogInterface?, which: Int ->
                bt_submit?.isEnabled = true
                bt_submit_another?.isEnabled = true
                dialog?.dismiss()
            }
            setNegativeButton(getString(R.string.no)){
                    dialog: DialogInterface?, which: Int ->
                dialog?.dismiss()
            }
        }.create().show()
    }
}