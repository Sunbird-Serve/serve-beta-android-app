package org.evidyaloka.partner.ui.student

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_student.*
import kotlinx.android.synthetic.main.fragment_student.bt_submit
import kotlinx.android.synthetic.main.fragment_student.etv_course
import kotlinx.android.synthetic.main.fragment_student.etv_grade
import kotlinx.android.synthetic.main.fragment_student.til_course
import kotlinx.android.synthetic.main.fragment_student.til_grade
import org.evidyaloka.common.helper.PermissionRequester
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.partner.R
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.common.view.SuccessDialogFragment
import org.evidyaloka.common.view.multiSelectionSpinner.KeyPairBoolData
import org.evidyaloka.common.view.multiSelectionSpinner.MultiSpinnerListener
import org.evidyaloka.core.partner.model.CourseOffering
import org.evidyaloka.core.partner.model.Language
import org.evidyaloka.core.partner.model.Student
import org.evidyaloka.core.helper.ErrorData
import org.evidyaloka.partner.ui.BaseFragment
import org.evidyaloka.common.util.Utils.format_dd_MM_yyyy_To_yyyy_MM_dd
import org.evidyaloka.common.util.Utils.formatyyyy_MM_dd_To_dd_MM_yyyy
import org.evidyaloka.common.helper.getFileName
import org.evidyaloka.common.helper.isValidName
import org.evidyaloka.common.helper.isValidPhoneNumber
import org.evidyaloka.common.util.Utils
import org.evidyaloka.core.model.CourseProvider
import java.io.IOException
import java.util.*

@AndroidEntryPoint
class StudentFragment : BaseFragment() {
    private val TAG = "StudentFragment"

    private var profilePicUri: Uri? = null
    private var profilePicId: Int? = null
    private var digitalSchoolId: Int? = null
    private var digitalSchoolName: String? = null
    private var isEditStudent: Boolean = false
    private var studentId: Int? = null
    private var selectedmediumOfStudy: Int = 0
    private var selectedOfferingId: List<Long> = listOf()
    private var offeringList: List<CourseOffering> = listOf()
    private var mediumOfStudy: List<Language> = listOf()
    private var courseProviderList: List<CourseProvider>? = null

    private val viewModel: StudentViewModel by viewModels()
    private val storagePermission = PermissionRequester(
        this,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        R.string.rationale_read_storage
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            digitalSchoolId = it.getInt(PartnerConst.DIGITAL_SCHOOL_ID)
            digitalSchoolName = it.getString(PartnerConst.DIGITAL_SCHOOL_NAME)
            studentId = it.getInt(PartnerConst.STUDENT_ID)
            isEditStudent = it.getBoolean(PartnerConst.IS_EDIT_STUDENT)
            it.getSerializable(PartnerConst.COURSE_PROVIDER)?.let {
                courseProviderList = it as List<CourseProvider>
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        if (isEditStudent && studentId != null && digitalSchoolId != null) {
            getStudentsDetails(studentId!!, digitalSchoolId!!)
            ll_student_enroll_button?.visibility = View.GONE
            bt_update?.visibility = View.VISIBLE
        }
        etv_course?.hintText = getString(R.string.please_select_grade_first)
        val materialDateBuilder: MaterialDatePicker.Builder<*> =
            MaterialDatePicker.Builder.datePicker()
        materialDateBuilder
            .setTheme(R.style.CalendarTheme)
            .setTitleText(getString(R.string.calendar_select_date))
        val constraintsBuilderRange = CalendarConstraints.Builder()
            .setEnd(Calendar.getInstance().timeInMillis)
            .setValidator(DateValidatorPointBackward.now())
        val materialDatePicker = materialDateBuilder
            .setCalendarConstraints(constraintsBuilderRange.build())
            .build()

        etv_dob?.setOnClickListener {
            if (!materialDatePicker.isAdded) {
                materialDatePicker.show(childFragmentManager, "DOB_DATE")
            }
        }
        materialDatePicker.addOnPositiveButtonClickListener {
            if (it is Long) {
                var date = Date(it)
                etv_dob?.setText(Utils.format_dd_MM_yyyy(date.time))
            }
        }
        bt_submit_and_add?.setOnClickListener {
            if (profilePicUri != null) {
                uploadProfilePic(profilePicUri!!, true)
            } else {
                submitStudentEnrollment(true)
            }
        }
        bt_submit?.setOnClickListener {
            if (profilePicUri != null) {
                uploadProfilePic(profilePicUri!!, false)
            } else {
                submitStudentEnrollment(false)
            }
        }
        ib_logo_selector?.setOnClickListener {
            storagePermission.runWithPermission {
                showFileChooser(PICK_LOGO_IMAGE_REQUEST)
            }
        }
        bt_update?.setOnClickListener {
            updateStudent()
        }
        if (courseProviderList.isNullOrEmpty()) {
            getDigitalSchool()
        } else {
            getLanguages()
        }
        loadGrades()
    }

    /**
     * This function is to get Student Details and update the UI
     * @param studentId Int current student
     * @param digitalSchoolId Int current digital school
     */
    private fun getStudentsDetails(studentId: Int, digitalSchoolId: Int) {
        try {
            viewModel.getStudentDetails(studentId, digitalSchoolId)
                .observe(viewLifecycleOwner, Observer { response ->
                    when (response) {
                        is Resource.Success -> {
                            response.data?.let {
                                setUIData(it)
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

    /**
     * loads/updates Grade Spinner with the list of grades
     * @param grades List<Int> list of grades
     */
    private fun updateGradeSpinner(grades: List<Int>) {
        try {
            context?.let {
                if (grades.size > 0) {
                    var adapter = ArrayAdapter(it, R.layout.spinner_list_item, grades)
                    etv_grade?.setAdapter(adapter)
                    etv_grade?.setOnItemClickListener { parent, view, position, id ->
                        getCourseOfferings()
                    }
                    adapter?.notifyDataSetChanged()
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    /*
     * This Function is responsible to fetch Grades from shared Preference
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
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    /*
     * This Function is responsible to fetch the languages from API
     */
    private fun getLanguages() {
        try {
            viewModel.getLanguages().observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let {
                            setLanguageAdapter(it)
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

    /**
     * loads/updates Medium of Study Spinner with the list of Language
     * @param list List<Language> list of language
     */
    private fun setLanguageAdapter(list: List<Language>) {
        try {
            mediumOfStudy = list
            context?.let {
                val adapter =
                    ArrayAdapter(it, R.layout.spinner_list_item, mediumOfStudy.map { it.name })
                etv_medium?.setAdapter(adapter)
                etv_medium?.setOnItemClickListener { parent, view, position, id ->
                    selectedmediumOfStudy = mediumOfStudy.get(position).id
                }
                adapter?.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    private fun getBoardSpecificLanguage(list: List<Language>): List<Language> {
        val board = courseProviderList?.takeIf { it.size > 0 }?.get(0)?.code
        return when (board) {
            "APSB" -> {
                list.filter { it.name in listOf("Telugu", "English") }
            }
            "MHSB" -> {
                list.filter { it.name in listOf("Marathi", "English") }
            }
            "TNSB" -> {
                list.filter { it.name in listOf("Tamil", "English") }
            }
            "UBSE" -> {
                list.filter { it.name in listOf("Hindi", "English") }
            }
            "DSERT" -> {
                list.filter { it.name in listOf("Kannada", "English") }
            }
            else -> {
                list
            }
        }
    }

    /**
     * This function is responsible for fetching courseOfferings for given digital school
     */
    private fun getCourseOfferings() {
        //TODO Change hardcoded dsId = 1
        try {
            digitalSchoolId?.let {
                val grade = etv_grade?.text.toString().takeIf { it.isNotEmpty() }?.toInt()
                viewModel.getCourseOfferings(null, it, grade, null,selectedmediumOfStudy)
                    .observe(viewLifecycleOwner, Observer { response ->
                        when (response) {
                            is Resource.Success -> {
                                response?.data.let {
                                    it?.takeIf { it.size > 0 }?.let {
                                        setOfferingsAdapter(it)
                                    } ?: showNoContentAlert()
                                }
                            }
                            is Resource.GenericError -> {

                            }
                        }
                    })
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    private fun showNoContentAlert() {
        MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialogTheme).apply {
            setMessage(getString(R.string.no_course_assigned_to_the_grade))
            setPositiveButton(getString(R.string.ok)) { dialog: DialogInterface?, which: Int ->
                etv_grade?.text = null
                dialog?.dismiss()
            }
        }.create().show()
    }

    /**
     * loads/updates Course Offerings Spinner with the list of CourseOffering
     * @param list List<CourseOffering> list of CourseOffering
     * @return Nothing
     */
    private fun setOfferingsAdapter(list: List<CourseOffering>) {
        try {
            etv_course?.hintText = getString(R.string.select_courses)
            offeringList = list
            val keyPairItems: MutableList<KeyPairBoolData> = mutableListOf()
            for (item in list) {
                keyPairItems.add(
                    KeyPairBoolData(
                        item.id.toLong(),
                        item.courseName,
                        selectedOfferingId.contains(item.id.toLong())
                    )
                )
            }
            etv_course.setItems(keyPairItems, object : MultiSpinnerListener {
                override fun onItemsSelected(selectedItems: List<KeyPairBoolData>?) {
                    selectedItems?.let { list ->
                        selectedOfferingId = list.map { it.id }
                    }
                }
            })
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    private fun getDigitalSchool() {
        digitalSchoolId?.let {
            viewModel.getDigitalSchool(it).observe(viewLifecycleOwner, Observer {
                when (it) {
                    is Resource.Success -> {
                        it.data?.listDigitalSchools?.takeIf { it.size > 0 }?.get(0)?.let {
                            courseProviderList = it.courseProviderList
                            getLanguages()
                        }

                    }
                    else -> {
                    }
                }
            })
        }
    }

    /**
     * This function is responsible to enroll student to a school
     */
    private fun submitStudentEnrollment(addMoreStudent: Boolean) {
        hideKeyboard()

        if (validateUserDetails()) {
            buildRequestData()?.let {
                try {
                    viewModel.enrolledStudents(it)
                        .observe(viewLifecycleOwner, Observer { response ->
                            when (response) {
                                is Resource.Success -> {
                                    //showSuccessDialog(!addMoreUser, getString(R.string.student_enrolled))
                                    showSuccessView(
                                        addMoreStudent,
                                        getString(R.string.thank_you),
                                        getString(R.string.student_enrolled_success),
                                        R.drawable.ic_thumbs_up
                                    )
                                    if (addMoreStudent) {
                                        clearAllDetails()
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
        }
    }

    private fun updateStudent() {
        try {
            hideKeyboard()
            if (validateUserDetails() && studentId != null && digitalSchoolId != null) {
                buildRequestData()?.let {
                    viewModel.updateStudents(studentId!!, digitalSchoolId!!, it)
                        .observe(viewLifecycleOwner, Observer { response ->
                            when (response) {
                                is Resource.Success -> {
                                    showSuccessView(
                                        false,
                                        getString(R.string.successful),
                                        getString(R.string.student_update),
                                        R.drawable.ic_check
                                    )
                                }
                                is Resource.GenericError -> {
                                    handleGenericError(response.error)
                                }
                            }
                        })
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    private fun clearAllDetails() {
        try {
            etv_student_name.setText("")
            etv_parent_name.setText("")
            etv_parent_phone.setText("")
            etv_dob.setText("")
            etv_school_name.setText("")
            etv_medium.setText("", false)
            selectedmediumOfStudy = 0
            etv_course.setClearText("")
            selectedOfferingId = listOf()
            etv_grade?.setText("", false)
            setOfferingsAdapter(offeringList)
            rbg_relationship_type.check(R.id.rb_father)
            rbg_gender.check(R.id.rb_boy)
            cb_consent.isChecked = false
            iv_close_logo?.visibility = View.GONE
            tv_logo_placeholder_text?.visibility = View.VISIBLE
            iv_student_profile.setImageBitmap(null)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    /**
     * update UI
     * @param student Student
     */
    private fun setUIData(student: Student) {
        try {
            etv_student_name?.setText(student.studentName)
            etv_parent_name?.setText(student.parentName)
            etv_parent_phone?.setText(student.mobile)
            student.dob?.let {
                var dob = formatyyyy_MM_dd_To_dd_MM_yyyy(it)
                dob?.let {
                    etv_dob?.setText(dob)
                }
            }
            etv_school_name?.setText(student.physicalSchoolName)
            etv_grade?.setText(student.grade, false)
//            if(student.status == PartnerConst.StudentStatus.Active && student.onboardingStatus == "completed"){
//                etv_grade?.isEnabled = false
//                etv_grade?.setAdapter(null)
//            }
            etv_medium?.setText(student.mediumOfStudy?.name, false)
            selectedmediumOfStudy = student.mediumOfStudy?.languageId ?: 0
//        etv_course.setClearText(student.offeringsOpted?.map { it.subject }.toString())
            selectedOfferingId = student.offeringsOpted?.map { it.id.toLong() } ?: listOf<Long>()
            rbg_relationship_type?.check(
                when (student.relationshipType) {
                    getString(R.string.father) -> R.id.rb_father
                    getString(R.string.mother) -> R.id.rb_mother
                    getString(R.string.guardian) -> R.id.rb_guardian
                    else -> R.id.rb_father
                }
            )
            rbg_gender?.check(
                when (student.gender) {
                    getString(R.string.boy) -> R.id.rb_boy
                    getString(R.string.girl) -> R.id.rb_girl
                    else -> R.id.rb_boy
                }
            )
            cb_consent?.isChecked = false
            getCourseOfferings()
            context?.let {
                iv_student_profile?.let { iv_student_profile ->
                    Glide.with(it)
                        .load(student.profileUrl)
                        .circleCrop()
                        .listener(object : RequestListener<Drawable> {

                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                iv_close_logo?.visibility = View.GONE
                                tv_logo_placeholder_text?.visibility = View.VISIBLE

                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                iv_close_logo?.visibility = View.VISIBLE
                                tv_logo_placeholder_text?.visibility = View.GONE
                                return false
                            }
                        })
                        .into(iv_student_profile)
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    /**
     * builds request for Student Enrolment API
     * @return MapMap<String, Any>
     */
    fun buildRequestData(): Map<String, Any>? {
        var map: HashMap<String, Any>? = null
        try {
            map = hashMapOf()
            // map.put(PARTNER_ID, etv_user_name.text.toString()) PARTNER_ID to be used from MainRepository
            map[PartnerConst.PARENT_NAME] = etv_parent_name?.text.toString()
            map[PartnerConst.STUDENT_NAME] = etv_student_name?.text.toString()
            map[PartnerConst.MOBILE] = etv_parent_phone?.text.toString()
            map[PartnerConst.RELATIONSHIP_TYPE] =
                when (rbg_relationship_type?.checkedRadioButtonId) {
                    R.id.rb_father -> PartnerConst.FATHER
                    R.id.rb_mother -> PartnerConst.MOTHER
                    R.id.rb_guardian -> PartnerConst.GUARDIAN
                    else -> PartnerConst.FATHER
                }

            var formattedDate = format_dd_MM_yyyy_To_yyyy_MM_dd(etv_dob?.text.toString())

            formattedDate?.let { map[PartnerConst.DOB] = formattedDate }

            map[PartnerConst.GENDER] = when (rbg_gender?.checkedRadioButtonId) {
                R.id.rb_boy -> PartnerConst.BOY
                R.id.rb_girl -> PartnerConst.GIRL
                else -> PartnerConst.BOY
            }
            map[PartnerConst.GRADE] = etv_grade?.text.toString()
            map[PartnerConst.MEDIUM_OF_STYDY] = selectedmediumOfStudy
            map[PartnerConst.HAS_TAKEN_GUARDIAN_CONSENT] = if (cb_consent.isChecked) 1 else 0
            map[PartnerConst.PHYSICAL_SCHOOL_NAME] = etv_school_name?.text.toString()
            map[PartnerConst.OFFERINGS_OPTED] = selectedOfferingId

            profilePicId?.let {
                map.put(PartnerConst.PROFILE_PIC_ID, it)
            }

            digitalSchoolId?.let {
                if (it != 0) {
                    map[PartnerConst.DIGITAL_SCHOOL_ID] = it
                }
            }
            Log.e(TAG, "buildRequestData: $map")
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e(TAG, e.message ?: "gson converter exception")
        }
        return map
    }

    /**
     * This function uploads Students Image.
     * @param uri Uri Image Uri
     * @param addMoreUser Boolean denotes if more user to be added
     */
    private fun uploadProfilePic(uri: Uri, addMoreUser: Boolean) {
        try {
            viewModel.uploadProgress().observe(viewLifecycleOwner, Observer {
                when (it > 0) {
                    true -> {
                        UpdateHorizontalDeterminateBar(it)
                    }
                    else -> {
                        hideHorizontalDeterminateBar()
                    }
                }
            })
            activity?.let {
                it.contentResolver?.openInputStream(uri)?.let { streamData ->
                    showHorizontalDeterminateBar()
                    viewModel.uploadFile(
                        PartnerConst.DocType.USERPROFILEPIC.value,
                        PartnerConst.DocFormat.JPG.value,
                        uri.getFileName(it)!!,
                        streamData
                    ).observe(viewLifecycleOwner, Observer { response ->
                        when (response) {
                            is Resource.Success -> {
                                hideHorizontalDeterminateBar()
                                profilePicId = response.data?.id
                                submitStudentEnrollment(addMoreUser)
                            }
                            else -> {
                                hideHorizontalDeterminateBar()
                                handleNetworkError()
                            }
                        }
                    })
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    /*
     *This function validates the student details
     */
    private fun validateUserDetails(): Boolean {
        var valid = true
        var requestfocusSet = false

        til_student_name?.error =
            getString(R.string.invalid_name).takeIf {
                !etv_student_name?.text.toString().isValidName()
            }?.also {
                valid = false
                if (!requestfocusSet) {
                    requestfocusSet = true
                    etv_student_name?.requestFocus()
                }
            }
        til_parent_name?.error =
            getString(R.string.invalid_name).takeIf {
                !etv_parent_name?.text.toString().isValidName()
            }?.also {
                valid = false
                if (!requestfocusSet) {
                    requestfocusSet = true
                    etv_parent_name?.requestFocus()
                }
            }
        til_parent_phone?.error =
            getString(R.string.invalid_phone).takeIf {
                !etv_parent_phone?.text.toString().isValidPhoneNumber()
            }?.also {
                valid = false
                if (!requestfocusSet) {
                    requestfocusSet = true
                    etv_parent_phone?.requestFocus()
                }
            }

        til_grade?.error =
            getString(R.string.invalid_grade).takeIf {
                etv_grade?.text.toString().isEmpty()
            }
                ?.also {
                    valid = false
                    if (!requestfocusSet) {
                        requestfocusSet = true
                        etv_grade?.requestFocus()
                    }
                }
        til_school_name.error =
            getString(R.string.invalid_school_details).takeIf {
                etv_school_name?.text.toString().isEmpty()
            }
                ?.also {
                    valid = false
                    if (!requestfocusSet) {
                        requestfocusSet = true
                        etv_school_name?.requestFocus()
                    }
                }

        til_medium.error =
            getString(R.string.invalid_school_details).takeIf {
                etv_medium?.text.toString().isEmpty()
            }
                ?.also {
                    valid = false
                    if (!requestfocusSet) {
                        requestfocusSet = true
                        etv_medium?.requestFocus()
                    }
                }
        til_course.error =
            getString(R.string.invalid_course).takeIf {
                selectedOfferingId.isEmpty()
            }
                ?.also {
                    valid = false
                    if (!requestfocusSet) {
                        requestfocusSet = true
                        etv_course?.requestFocus()
                    }
                }
        return valid
    }

    /**
     *  This function handles Generic Error
     */
    private fun handleGenericError(error: ErrorData?) {
        error?.let {

            when (it.code) {
                54 -> {
                    til_medium?.error = getString(R.string.invalid_language_type)
                }
                56 -> {
                    til_course?.error = getString(R.string.invalid_offerings)
                }
                52 -> {
                    til_relationship_type?.error = getString(R.string.invalid_relationship_type)
                }
                4 -> {
                    til_gender?.error = getString(R.string.invalid_gender_type)
                }
                82 -> {
                    var alert: SuccessDialogFragment? = null
                    alert = SuccessDialogFragment.Builder(requireContext())
                        .setIsDialogCancelable(true)
                        .setTitle(getString(R.string.error))
                        .setDescription(getString(R.string.active_student_grade_cannot_be_changed))
                        .setIcon(R.drawable.ic_cross_blue)
                        .setViewType(SuccessDialogFragment.DIALOG_TYPE.ALERT)
                        .setButtonPositiveText(getString(R.string.okay))
                        .setOnClickListner(View.OnClickListener {
                            alert?.dismiss()
                        })
                        .build()
                    alert?.show(childFragmentManager, "")
                }
                else -> {
                    showSnackBar(error.message)
                }
            }
        }
    }

    /**
     * This function opens CustomDialog
     * @param addMoreStudent handles if user should stay on same page or navigate to previous one
     * (Boolean if Submit and Add more button clicked stayOnPage is true, if Submit button clicked stayOnPage is false)
     */
    private fun showSuccessView(
        addMoreStudent: Boolean,
        title: String,
        description: String,
        icon: Int
    ) {
        context?.let {
            val onDismissListener = object : SuccessDialogFragment.OnDismissListener {
                override fun OnDismiss(dialog: DialogInterface) {
                    clearAllDetails()
                    if (!addMoreStudent) {
                        try {
                            navController.navigateUp()
                        } catch (e: Exception) {
                            FirebaseCrashlytics.getInstance().recordException(e)

                        }
                    }
                }
            }
            SuccessDialogFragment.Builder(it)
                .setIsDialogCancelable(true)
                .setTitle(title)
                .setDescription(description)
                .setDismissTimer(PartnerConst.DIALOG_CLOSE_TIME)
                .setIcon(icon)
                .setViewType(SuccessDialogFragment.DIALOG_TYPE.FULL_SCREEN)
                .setOnDismissListener(onDismissListener)
                .build()
                .show(childFragmentManager, "")

        }
    }

    //method to show file chooser
    private fun showFileChooser(requestCode: Int) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.select_picture)),
            requestCode
        )
    }

    /**
     * onActivityResult callback handles image selected from File chooser
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PROFILE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            try {
                data.data?.let { uri ->
                    activity?.let {
                        val bitmap = MediaStore.Images.Media.getBitmap(it.contentResolver, uri)
                        iv_student_profile?.let { iv_student_profile ->
                            Glide.with(it)
                                .load(bitmap)
                                .circleCrop()
                                .listener(object : RequestListener<Drawable> {

                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        iv_close_logo?.visibility = View.GONE
                                        tv_logo_placeholder_text?.visibility = View.VISIBLE
                                        showSnackBar(resources.getString(R.string.invalid_image))
                                        return false
                                    }

                                    override fun onResourceReady(
                                        resource: Drawable?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        dataSource: DataSource?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        iv_close_logo?.visibility = View.VISIBLE
                                        tv_logo_placeholder_text?.visibility = View.GONE
                                        return false
                                    }
                                })
                                .into(iv_student_profile)
                        }
                        profilePicUri = uri
                    }
                }

            } catch (e: IOException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                showSnackBar(resources.getString(R.string.invalid_image))
            }
        }
    }

}