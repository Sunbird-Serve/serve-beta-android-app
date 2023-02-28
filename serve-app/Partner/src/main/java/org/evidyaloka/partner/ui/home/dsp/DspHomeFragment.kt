package org.evidyaloka.partner.ui.home.dsp

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_dsp_home.*
import kotlinx.android.synthetic.main.layout_home_is_partner_activated.*
import kotlinx.android.synthetic.main.layout_home_partner_schools.*
import org.evidyaloka.common.util.Utils
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.DigitalSchool
import org.evidyaloka.core.partner.model.User
import org.evidyaloka.partner.BuildConfig
import org.evidyaloka.partner.R
import org.evidyaloka.partner.ui.BaseFragment
import org.evidyaloka.partner.ui.digitalschool.DIGITAL_SCHOOL_ID
import org.evidyaloka.partner.ui.home.HomeViewModel


@AndroidEntryPoint
class DspHomeFragment : BaseFragment() {

    private lateinit var dialog: AlertDialog
    private var isLoaded = false
    private lateinit var mView: View
    private val viewModel: HomeViewModel by viewModels()
    private val TAG = "DsmHomeFragment"
    private var user: User? = null
    var adapter = DspSchoolAdapter() { selectedDigitalSchoolItem: DigitalSchool ->
        onSchoolItemClick(selectedDigitalSchoolItem)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mView = inflater.inflate(R.layout.fragment_dsp_home, container, false)
        loadProfileDataAPI()
        updateFCMToken()
        if (user?.organization?.partnerStatus.equals(PartnerConst.PartnerStatus.Approved.name)) {
            loadPartnersSchools()
        }

        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        try {
            rv_schools?.let {
                val snapHelper: SnapHelper = PagerSnapHelper()
                it.layoutManager = LinearLayoutManager(
                    it?.context,
                    LinearLayoutManager.HORIZONTAL, false
                )
                it.adapter = adapter
                pager_indicator?.attachToRecyclerView(it)
                snapHelper.attachToRecyclerView(it)

                it.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                    }

                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        try {
                            val schoolPosition: Int =
                                (it.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                            (((adapter.digitalSchools[schoolPosition]) as DigitalSchool))?.let {
                                if (it.status.equals(PartnerConst.SchoolStatus.Active.toString())) {
                                    if (it.dsmId == null || it.dsmId == 0) {
                                        showAssignDsmPopup(it.name)?.show()
                                    }
                                }
                            }
                            super.onScrolled(recyclerView, dx, dy)
                        } catch (e: java.lang.Exception) {
                            FirebaseCrashlytics.getInstance().recordException(e)
                        }
                    }
                })
                try {
                    it.adapter?.itemCount?.let { count ->
                        if (count > 0) {
                            it.scrollToPosition(0)
                        }
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }

            cv_add_users?.setOnClickListener {
                if (user?.organization?.partnerStatus.equals(PartnerConst.PartnerStatus.Approved.name)) {
                    try {
                        navController.navigate(R.id.action_global_addUserFragment)
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)

                    }
                }
            }

            cv_add_school?.setOnClickListener {
                if (user?.organization?.partnerStatus.equals(PartnerConst.PartnerStatus.Approved.name)) {
                    try {
                        navController.navigate(R.id.action_dspHomeFragment_to_digitalSchoolFragment)
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)

                    }
                }
            }

            bt_add_digital_school?.setOnClickListener {
                if (user?.organization?.partnerStatus.equals(PartnerConst.PartnerStatus.Approved.name)) {
                    try {
                        navController.navigate(R.id.action_dspHomeFragment_to_digitalSchoolFragment)
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)

                    }
                }
            }

            if(::dialog.isInitialized && dialog.isShowing){
                dialog.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        updateToolbarTitle(" ")
    }
/*
 * Function to check verification status of the user
 */

    private fun updateUser(user: User) {
        try {
            viewModel.getUser().observe(viewLifecycleOwner, Observer {

                it?.let {

                    it.apply {
                        phone = user.phone
                        email = user.email
                        fname = user.fname
                        lname = user.lname
                        profileImageUrl = user.profileImageUrl
                        profileImageFullUrl = user.profileImageFullUrl
                        profileImageShortUrl = user.profileImageShortUrl

                        organization.apply {
                            name = user.organization.name
                            partnerName = user.organization.partnerName
                            email = user.organization.email
                            phone = user.organization.phone
                            address = user.organization.address
                            partnerStatus = user.organization.partnerStatus
                            id = user.organization.id
                        }
                    }

                    viewModel.saveUser(it)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateFCMToken() {
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                // Get new FCM registration token
                val token = task.result
                var map = HashMap<String, Any>().apply {
                    put(StudentConst.PUSH_TOKEN, token)
                    Utils.getDeviceInfo(requireContext(), "PARTNER")?.let {
                        put("deviceInfo", it)
                    }
                }
                try {
                    viewModel.updateFCMToken(map).observe(viewLifecycleOwner, Observer {

                    })
                } catch (e: Exception) {
                }

            })
        } catch (e: Exception) {
        }
    }

    private fun loadProfileDataAPI() {
        try {
            viewModel.getProfile1().observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { user ->
                            this.user = user
                            if (user.roles.contains(PartnerConst.RoleType.DSM.name))
                                showNavHeaderRoleToggleButton()
                            updateUser(user)
                            updateView(user)
                            if (user.organization.partnerStatus.equals(PartnerConst.PartnerStatus.Approved.name)) {
                                loadPartnersSchools()
                            } else {
                                showUnactivatedView()
                            }
                        }
                    }

                    is Resource.GenericError -> {
                        handleGenericError()
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateView(user: User) {
        try {
            tv_organization_name?.text = user.organization.name
            tv_dsp_date?.text = Utils.getCurrentDateIndMMMyyyy()
            tv_dsp_name?.text =
                getString(R.string.hi).plus(" ").plus(user.fname).plus(" ").plus(user.lname)
                    .plus("!")

            //Todo load organization logo url
            iv_organization_pic?.let {
                it.context?.let { context ->
                    Glide.with(context)
                        .load(BuildConfig.BASE_IMG_URL + user.organization.logo)
                        .placeholder(R.drawable.ic_logo_placeholder)
                        .error(R.drawable.ic_school_logo_placeholder)
                        .into(it)
                }
            }

            var imageUrl = ""

            /*if(!user.profileImageUrl.isNullOrEmpty()){
                imageUrl = user.profileImageUrl
            }*/

            if (!user.profileImageShortUrl.isNullOrEmpty()) {
                imageUrl = user.profileImageShortUrl
            } else if (!user.profileImageFullUrl.isNullOrEmpty()) {
                imageUrl = user.profileImageFullUrl
            }

            iv_dsp_profile_pic?.let {
                it.context?.let { context ->
                    Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_user_placeholder)
                        .error(R.drawable.ic_user_placeholder)
                        .into(it)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadPartnersSchools() {
        try {
            viewModel.getUserSchoolList(PartnerConst.RoleType.DSP)
                .observe(viewLifecycleOwner, Observer { response ->
                    when (response) {
                        is Resource.Success -> {
                            response.data?.listDigitalSchools?.let {
                                if ((it.isNotEmpty())) {
                                    showPartnerSchoolsView(it.toList())
                                } else {
                                    showActivatedView()
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

    fun showUnactivatedView() {
        try {
            view_is_activated?.visibility = View.VISIBLE
            cardView?.visibility = View.VISIBLE
            ll_verified_but_school_added?.visibility = View.GONE
            view_schools?.visibility = View.GONE
            ll_call?.setOnClickListener {
                val number = getString(R.string.evidylaloka_contact_number)
                val call: Uri = Uri.parse("tel:$number")
                val surf = Intent(Intent.ACTION_DIAL, call)
                startActivity(surf)

            }
            showActive(false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showActivatedView() {
        try {
            view_is_activated?.visibility = View.VISIBLE
            view_schools?.visibility = View.GONE
            cardView?.visibility = View.GONE
            ll_verified_but_school_added?.visibility = View.VISIBLE
            showActive(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showActive(show: Boolean) {
        try {
            context?.let {
                var colorStateList: ColorStateList
                if (show) {
                    colorStateList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            it,
                            R.color.cv_landing_active
                        )
                    )

                    tv_add_school?.setTextColor(getColor(it, R.color.cv_landing_active));
                    tv_add_users?.setTextColor(getColor(it, R.color.cv_landing_active));

                } else {

                    colorStateList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            it,
                            R.color.cv_landing_inactive
                        )
                    )

                    tv_add_school?.setTextColor(getColor(it, R.color.cv_landing_inactive));
                    tv_add_users?.setTextColor(getColor(it, R.color.cv_landing_inactive));
                }

                iv_add_users?.let {
                    ImageViewCompat.setImageTintList(it, colorStateList)
                }

                iv_add_school?.let {
                    ImageViewCompat.setImageTintList(it, colorStateList)
                }

                cv_add_users?.isEnabled = show
                cv_add_school?.isEnabled = show
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showPartnerSchoolsView(digitalSchools: List<DigitalSchool>) {
        try {
            view_is_activated?.visibility = View.GONE
            view_schools?.visibility = View.VISIBLE
            adapter?.setItem(digitalSchools)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleGenericError() {
//        if (activity != null) {
//            (activity as HomeActivity).openLogin()
//        }
    }

    private fun onSchoolItemClick(digitalSchool: DigitalSchool) {
        try {
            val arg = Bundle()
            arg.putInt(DIGITAL_SCHOOL_ID, digitalSchool.id)
            arg.putString(PartnerConst.ROLE, PartnerConst.RoleType.DSP.name)
            digitalSchool.courseProviderList?.let {

                arg.putSerializable(PartnerConst.COURSE_PROVIDER, it as ArrayList)
            }
            navController.navigate(R.id.action_global_digitalSchoolDetailsFragment, arg)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showAssignDsmPopup(title: String): AlertDialog {
        val alertLayout: View = layoutInflater.inflate(R.layout.layout_assign_dsm_msg, null)
        alertLayout.findViewById<TextView>(R.id.dlg_title)?.text =
            resources.getString(R.string.assign_dsm_to_school, title)
        activity?.let {
            dialog = MaterialAlertDialogBuilder(it, R.style.MaterialAlertDialogTheme)
                .setView(alertLayout).setCancelable(true).create()
        }
        alertLayout.findViewById<Button>(R.id.bt_ok)?.setOnClickListener {
            dialog?.dismiss()
        }
        return dialog
    }
}