package org.evidyaloka.partner.ui.student

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_students_details.*
import org.evidyaloka.common.util.Utils
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.core.helper.ErrorData
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.Student
import org.evidyaloka.partner.R
import org.evidyaloka.partner.ui.BaseFragment
import org.evidyaloka.partner.ui.digitalschool.DIGITAL_SCHOOL_ID
import java.util.*


@AndroidEntryPoint
class StudentsDetailsFragment : BaseFragment() {
    private var digitalSchoolId: Int? = null
    private var studentId: Int? = null
    private val viewModel: StudentViewModel by viewModels()
    private var isActiveStudent: Boolean? = null
    private var mobile: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            digitalSchoolId = it.getInt(DIGITAL_SCHOOL_ID)
            studentId = it.getInt(PartnerConst.STUDENT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_students_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        if (studentId != null && digitalSchoolId != null) {
            try {
                viewModel.getStudentDetails(studentId!!, digitalSchoolId!!)
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_active_menu, menu)
        isActiveStudent?.let {
            if (it) {
                menu.findItem(R.id.menu_active).setVisible(false)
                menu.findItem(R.id.menu_inactive).setVisible(true)
            } else {
                menu.findItem(R.id.menu_active).setVisible(true)
                menu.findItem(R.id.menu_inactive).setVisible(false)
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_edit -> {
                editDigitalSchool()
                true
            }
            R.id.menu_active -> {
                showStatusChangeAlert(PartnerConst.StudentStatus.Active)
            }
            R.id.menu_inactive -> {
                showStatusChangeAlert(PartnerConst.StudentStatus.Alumni)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showStatusChangeAlert(status: PartnerConst.StudentStatus) {
        val message =
            String.format(getString(R.string.do_you_want_status_student), status.name.toLowerCase())
        MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialogThemeCustomButton).apply {
            setTitle(getString(R.string.warning))
            setMessage(message)
            setPositiveButton(getString(R.string.yes)) { dialog: DialogInterface?, which: Int ->
                updateStudentStatus(status)
                dialog?.dismiss()
            }
            setNegativeButton(getString(R.string.no)) { dialog: DialogInterface?, which: Int ->
                dialog?.dismiss()
            }
        }.create().show()
    }

    private fun updateStudentStatus(status: PartnerConst.StudentStatus) {
        var map: HashMap<String, Any> = hashMapOf()
        map[PartnerConst.STUDENT_STATUS] = status.value
        map[PartnerConst.HAS_TAKEN_GUARDIAN_CONSENT] = 1
        map[PartnerConst.MOBILE] = mobile.toString()
        digitalSchoolId?.let {
            if (it != 0) {
                map[PartnerConst.DIGITAL_SCHOOL_ID] = it
            }
        }
        viewModel.updateStudents(studentId!!, digitalSchoolId!!, map)
            .observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        isActiveStudent = (status == PartnerConst.StudentStatus.Active)
                        activity?.invalidateOptionsMenu()
                        chip_status?.text = status?.name
                        chip_status?.isChecked = isActiveStudent as Boolean
                        showSuccessDialog(
                            getString(R.string.successful),
                            getString(R.string.student_status_updated_successfully)
                        )?.show()
                    }
                    is Resource.GenericError -> {
                        handleGenericError(response.error)
                    }
                }
            })
    }

    private fun editDigitalSchool() {
        if (digitalSchoolId != null && studentId != null) {
            val arguments = Bundle().apply {
                putBoolean(PartnerConst.IS_EDIT_STUDENT, true)
                putInt(DIGITAL_SCHOOL_ID, digitalSchoolId!!)
                putInt(PartnerConst.STUDENT_ID, studentId!!)
            }
            try {
                navController.navigate(
                    R.id.action_global_studentFragment,
                    arguments
                )
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)

            }
        }
    }

    private fun setUIData(student: Student) {
        try {
            mobile = student.mobile
            tv_name?.setText(student.studentName)
            tv_parent_name?.setText(student.parentName)
            tv_parent_contact?.setText(student.mobile)

            student.dob?.let {
                val dob = Utils.formatyyyy_MM_dd_To_dd_MM_yyyy(it)
                dob?.let { tv_dob?.setText(it) }
            }

            tv_school?.setText(student.physicalSchoolName)
            tv_grade?.setText(student.grade)
            tv_medium?.setText(student.mediumOfStudy?.name)
            tv_enrolled_course?.setText(student.offeringsOpted?.map { it.subject }.toString())
            tv_relationship?.setText(student.relationshipType)
            tv_gender?.setText(student.gender)

            context?.let {
                Glide.with(it)
                    .load(student.profileUrl)
                    .error(R.drawable.ic_student_placeholder)
                    .placeholder(R.drawable.ic_student_placeholder)
                    .circleCrop()
                    .into(iv_profile)
            }
            val courses = student.offeringsOpted?.map { it.subject }
            if (courses != null) {
                for (course in courses) {
                    val chip = Chip(chip_group_courses?.context)
                    chip.setTextColor(resources.getColor(R.color.colorOnSecondary))
                    chip.setText(course)
                    chip_group_courses?.addView(chip)
                }
            }
            isActiveStudent = student.status == PartnerConst.StudentStatus.Active
            chip_status?.text = student.status?.name
            chip_status?.isChecked = isActiveStudent as Boolean
            activity?.invalidateOptionsMenu()

        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    private fun handleGenericError(error: ErrorData?) {
        error?.let {

            when (it.code) {
                else -> {
                    showSnackBar(error.message)
                }
            }
        }
    }
}