package org.evidyaloka.partner.ui.digitalschool

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_digital_school_details.*
import kotlinx.android.synthetic.main.layout_digital_school_details_popup.view.*
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.partner.R
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.common.helper.RoundedCornersTransformation
import org.evidyaloka.core.model.CourseProvider
import org.evidyaloka.core.partner.model.DigitalSchool
import org.evidyaloka.partner.ui.BaseFragment
import org.evidyaloka.common.util.Utils


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameter
internal const val DIGITAL_SCHOOL_ID = "digitalSchoolId"

/**
 * A simple [Fragment] subclass.
 * Use the [DigitalSchoolDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class DigitalSchoolDetailsFragment : BaseFragment() {

    private val viewModel: DigitalSchoolViewModel by activityViewModels()
    private lateinit var mView: View
    private var mSchoolDetails: DigitalSchool? = null
    private var courseProviderListFromBundle: ArrayList<CourseProvider>? =
        ArrayList<CourseProvider>()
    private var courseProvider: CourseProvider? = null

    // TODO: Rename and change types of parameters
    private var digitalSchoolId: Int? = null
    private var role: String? = null
    private var digitalSchoolName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            digitalSchoolId = it.getInt(DIGITAL_SCHOOL_ID)
            digitalSchoolName = it.getString(PartnerConst.DIGITAL_SCHOOL_NAME)
            role = it.getString(PartnerConst.ROLE)
            courseProviderListFromBundle =
                it.getSerializable(PartnerConst.COURSE_PROVIDER) as ArrayList<CourseProvider>

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (!this::mView.isInitialized)
            mView = inflater.inflate(R.layout.fragment_digital_school_details, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        role?.takeIf { it.equals(PartnerConst.RoleType.DSM.name) }?.let {
            ll_dsm_action?.visibility = View.VISIBLE
            ll_dsm_name?.visibility = View.GONE
        }
        digitalSchoolId?.let { id ->
            viewModel.getDigitalSchool(id).observe(viewLifecycleOwner, Observer { response ->

                when (response) {
                    is Resource.Success -> {
                        try {
                            response.data?.let { schoolDetails ->
                                mSchoolDetails =
                                    schoolDetails.listDigitalSchools.takeIf { it.isNotEmpty() }
                                        ?.get(0)
                                mSchoolDetails?.let { setUIValues(it) }
                                courseProvider =
                                    mSchoolDetails?.courseProviderList?.takeIf { it.isNotEmpty() }
                                        ?.get(0)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    else -> {
                        handleNetworkError()
                    }
                }
            })
        }

        bt_add_course.setOnClickListener {
            //Todo pass digital schoolID to course Fragment
            mSchoolDetails?.let {
                if (PartnerConst.SchoolStatus.Active.name.equals(it.status)) {
                    digitalSchoolId?.let { id ->
                        val bundle = Bundle()
                        bundle.putInt(DIGITAL_SCHOOL_ID, id)
                        digitalSchoolName?.let {
                            bundle.putString(
                                PartnerConst.DIGITAL_SCHOOL_NAME,
                                it
                            )
                        }
                        courseProviderListFromBundle?.let {
                            bundle.putSerializable(PartnerConst.COURSE_PROVIDER, it)
                        }
                        try {
                            navController.navigate(
                                R.id.action_dsmHomeFragment_to_addCourseFragment,
                                bundle
                            )
                        } catch (e: Exception) {
                            FirebaseCrashlytics.getInstance().recordException(e)

                        }
                    }
                } else {
                    showSnackBar(getString(R.string.school_verification_pending))
                }
            }
        }

        bt_enroll_student?.setOnClickListener {
            //Todo pass digital schoolID to course Fragment
            mSchoolDetails?.let {
                if (PartnerConst.SchoolStatus.Active.name.equals(it.status)) {
                    digitalSchoolId?.let { id ->
                        val bundle = Bundle()
                        bundle.putInt(DIGITAL_SCHOOL_ID, id)
                        digitalSchoolName?.let {
                            bundle.putString(
                                PartnerConst.DIGITAL_SCHOOL_NAME,
                                it
                            )
                        }
                        courseProviderListFromBundle?.let {
                            bundle.putSerializable(PartnerConst.COURSE_PROVIDER, it)
                        }
                        try {
                            navController.navigate(R.id.action_global_studentFragment, bundle)
                        } catch (e: Exception) {
                            FirebaseCrashlytics.getInstance().recordException(e)

                        }
                    }
                } else {
                    showSnackBar("School verification pending.")
                }
            }
        }

        bt_menu.setOnClickListener {
            it?.let {
                showPopupWindow()
            }
        }
        bt_assign_dsm?.setOnClickListener {
            try {
                navController.navigate(R.id.usersFragment)
            } catch (e: Exception) {
            }
        }

        role?.takeIf { it.equals(PartnerConst.RoleType.DSP.name) }?.let {
            state_select_flow?.visibility = View.VISIBLE
            state_select_flow?.setOnClickListener {
                navController.navigate(
                    DigitalSchoolDetailsFragmentDirections.actionDigitalSchoolDetailsFragmentToStatePincodeViewFragment(
                        mSchoolDetails
                    )
                )
            }
        }
    }

    fun showPopupWindow() {
        /* TODO 1. Color not changing as per VD
        2. Change Icons for popup as per VD, ask Anoop
        3.  make sure the size of popup is compatible with other screen size phones*/
        try {
            var popupWindow = PopupWindow()
            var inflater =
                requireContext().applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = inflater.inflate(R.layout.layout_digital_school_details_popup, null)
            popupWindow = PopupWindow(
                view,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                true
            )
            popupWindow.setBackgroundDrawable(ColorDrawable(requireContext().resources.getColor(R.color.white)))
            popupWindow.showAsDropDown(bt_menu, 10, 10, (Gravity.BOTTOM or Gravity.END))

            view.rlAllStudents?.setOnClickListener {
                popupWindow.dismiss()
                //TODO All Students
            }
            if(role == PartnerConst.RoleType.DSP.name)
                view.rlPromotStudents?.visibility = View.GONE

            view.rlPromotStudents?.setOnClickListener {
                popupWindow.dismiss()
                digitalSchoolId?.let { id ->
                    val bundle = Bundle()
                    bundle.putInt(DIGITAL_SCHOOL_ID, id)
                    // digitalSchoolName?.let { bundle.putString(DIGITAL_SCHOOL_NAME, it) }
                    courseProvider?.let {
                        bundle.putParcelable(PartnerConst.COURSE_PROVIDER, it)
                    }
                    try {
                        navController.navigate(
                            R.id.action_digitalSchoolDetailsFragment_to_studentsPromotionFragment,
                            bundle
                        )
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)

                    }
                }
            }

            view.rlStudentsEnrolled?.setOnClickListener {
                popupWindow.dismiss()
                digitalSchoolId?.let { id ->
                    val bundle = Bundle()
                    bundle.putInt(DIGITAL_SCHOOL_ID, id)
                    // digitalSchoolName?.let { bundle.putString(DIGITAL_SCHOOL_NAME, it) }
                    courseProviderListFromBundle?.let {
                        bundle.putSerializable(PartnerConst.COURSE_PROVIDER, it)
                    }
                    try {
                        navController.navigate(R.id.action_global_studentListFragment, bundle)
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)

                    }
                }
            }

            view.rlCourses?.setOnClickListener {
                popupWindow.dismiss()
                digitalSchoolId?.let { id ->
                    val bundle = Bundle()
                    bundle.putInt(DIGITAL_SCHOOL_ID, id)
                    // digitalSchoolName?.let { bundle.putString(DIGITAL_SCHOOL_NAME, it) }
                    courseProviderListFromBundle?.let {
                        bundle.putSerializable(PartnerConst.COURSE_PROVIDER, it)
                    }
                    navController.navigate(
                        R.id.action_digitalSchoolDetailsFragment_to_courseFragment,
                        bundle
                    )
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        role?.takeIf { it.equals(PartnerConst.RoleType.DSP.name) }?.let {
            inflater.inflate(R.menu.edit_menu, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_edit -> {
                editDigitalSchool()
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun editDigitalSchool() {
        try {
            val arguments = Bundle().apply {
                putBoolean(IS_EDIT_DIGITAL_SCHOOL, true)
                putParcelable(DIGITAL_SCHOOL_DETAILS, mSchoolDetails)
            }
            navController.navigate(
                R.id.action_digitalSchoolDetailsFragment_to_digitalSchoolFragment,
                arguments
            )
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    private fun setUIValues(school: DigitalSchool) {
        try {
            context?.let {
                Glide.with(it)
                    .load(school.bannerUrl ?: R.mipmap.digital_school_banner)
                    .placeholder(R.mipmap.digital_school_banner)
                    .error(R.mipmap.digital_school_banner)
                    .transform(
                        RoundedCornersTransformation(
                            Utils.convertDpToPixel(45F, requireContext()).toInt(),
                            0,
                            RoundedCornersTransformation.CornerType.TOP_RIGHT
                        )
                    )
                    .into(iv_banner)
                Glide.with(it)
                    .load(school.logoUrl)
                    .placeholder(R.drawable.ic_school_logo_placeholder)
                    .error(R.drawable.ic_school_logo_placeholder)
                    .circleCrop()
                    .into(iv_logo)
            }
            updateToolbarTitle(school.name)
            tv_ds_description?.text = school.description
            if (school.dsmId != null) {
                tv_ds_dsm_name?.text = school.dsmFirstName?.let {
                    school.dsmFirstName?.plus(" ")?.plus(school.dsmLastName)
                }
            } else {
                ll_dsm_name?.visibility = View.GONE
                bt_assign_dsm?.visibility = View.VISIBLE
            }
            tv_ds_dsm_name?.text = school.dsmFirstName?.let {
                school.dsmFirstName?.plus(" ")?.plus(school.dsmLastName)
            } ?: getString(R.string.not_assigned)
//            tv_status?.text = school.status
            if (school.status == PartnerConst.SchoolStatus.Active.name) {
                tv_status?.setText(PartnerConst.SchoolStatusType.APPROVED.toString())
                tv_status?.background = resources.getDrawable(R.drawable.button_approved, null)
            } else {
                tv_status?.setText(PartnerConst.SchoolStatusType.PENDING.toString())
                tv_status?.background = resources.getDrawable(R.drawable.button_pending, null)
            }
            tv_ds_purpose?.text = school.purpose
            school?.courseProviderList?.let {
                for (courseProvider in it)
                    tv_ds_course_provider?.setText(courseProvider.name)
            }
            tv_ds_grades?.text = school.courseCount.toString()
            tv_ds_no_students?.text = school.studentCount.toString()
            school.createdOn.toLong()?.takeIf { it > 0 }?.let {
                tv_ds_enrolled_on?.text = Utils.formatddMMMMyyyy(it * 1000)
            }

            school.courseCount?.let {
                bt_enroll_student?.isEnabled = it > 0
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param schoolId Parameter
         * @return A new instance of fragment DigitalSchoolDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(schoolId: Int) =
            DigitalSchoolDetailsFragment()
                .apply {
                    arguments = Bundle().apply {
                        putInt(DIGITAL_SCHOOL_ID, schoolId)
                    }
                }
    }
}