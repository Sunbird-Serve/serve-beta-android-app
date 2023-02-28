package org.evidyaloka.partner.ui.home.dsm

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_dsm_home.*
import kotlinx.android.synthetic.main.layout_dsm_home_is_partner_activated.*
import kotlinx.android.synthetic.main.layout_home_dsm_schools.*
import kotlinx.android.synthetic.main.layout_home_dsm_schools.pager_indicator
import org.evidyaloka.partner.R
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.DigitalSchool
import org.evidyaloka.core.partner.model.User
import org.evidyaloka.partner.ui.BaseFragment
import org.evidyaloka.partner.ui.home.HomeViewModel
import org.evidyaloka.common.util.Utils
import org.evidyaloka.partner.BuildConfig


@AndroidEntryPoint
class DsmHomeFragment : BaseFragment() {

    private val adapter = DsmSchoolAdapter()
    private lateinit var dialog: AlertDialog

    private var isLoaded = false
    private lateinit var mView: View
    private var user: User? = null
    private val viewModel: HomeViewModel by viewModels()
    private val TAG = "DsmHomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mView = inflater.inflate(R.layout.fragment_dsm_home, container, false)
        loadProfileDataAPI()

        if (user?.organization?.partnerStatus.equals(PartnerConst.PartnerStatus.Approved.name)) {
            loadDsmSchools()
        }

        return mView
    }

    override fun onResume() {
        super.onResume()
        updateToolbarTitle(" ")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        try {
            bottom_mgs_layout?.visibility = View.GONE
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
                        Log.e(TAG, "onScrolled")
                        try {
                            bottom_mgs_layout?.visibility = View.GONE
                            val schoolPosition: Int =
                                (it.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                            (((adapter.digitalSchools[schoolPosition]) as DigitalSchool))?.let {
                                if (it.status.equals(PartnerConst.SchoolStatus.Active.toString())) {
                                    if (it.courseCount == 0) {
                                        showAddBottomPopup(
                                            resources.getString(
                                                R.string.add_course_to_school,
                                                it.name
                                            )
                                        )
                                    } else
                                        if (it.courseCount > 0 && it.studentCount == 0) {
                                            showAddBottomPopup(
                                                resources.getString(
                                                    R.string.add_students_to_school,
                                                    it.name
                                                )
                                            )
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

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*
     * Function to check verification status of the user
     */

    private fun loadProfileDataAPI() {
        try {
            viewModel.getProfile1().observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { user ->
                            this.user = user
                            updateUser(user)
                            updateView(user)
                            if (user.organization.partnerStatus.equals(PartnerConst.PartnerStatus.Approved.name)) {
                                loadDsmSchools()
                            } else {
                                showUnactivatedView()
                            }
                        }
                    }

                    is Resource.GenericError -> {
                        response.error?.let {
                            handleGenericError(it.code)
                        }

                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

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

    private fun updateView(user: User) {
        try {
            tv_organization_name?.text = user.organization.name
            tv_dsm_date?.text = Utils.getCurrentDateIndMMMyyyy()
            tv_dsm_name?.text =
                getString(R.string.hi).plus(" ").plus(user.fname).plus(" ").plus(user.lname)
                    .plus("!")

            //Todo load organization logo url
            iv_organization_pic?.let {
                it.context?.let { context ->
                    Glide.with(context)
                        .load(BuildConfig.BASE_IMG_URL + user.organization.logo)
                        .placeholder(R.drawable.ic_school_logo_placeholder)
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

            iv_dsm_profile_pic?.let {
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

    private fun loadDsmSchools() {
        try {
            viewModel.getUserSchoolList(PartnerConst.RoleType.DSM)
                .observe(viewLifecycleOwner, Observer { response ->
                    when (response) {
                        is Resource.Success -> {
                            response.data?.listDigitalSchools?.let {
                                if ((it.isNotEmpty())) {
                                    showUserSchoolsView(it.toList())
                                } else {
                                    showActivatedView()
                                }
                            }
                        }
                        else -> {
                            showActivatedView()
                            handleNetworkError()
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
            view_schools?.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showActivatedView() {
        try {
            view_is_activated?.visibility = View.VISIBLE
            view_schools?.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun showUserSchoolsView(digitalSchools: List<DigitalSchool>) {
        try {
            view_is_activated?.visibility = View.GONE
            view_schools?.visibility = View.VISIBLE
            setAdapter(digitalSchools)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setAdapter(digitalSchools: List<DigitalSchool>) {
        try {
            adapter?.setItem(digitalSchools)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleGenericError(code: Int) {

    }


    enum class ClickType {
        SCHOOL, STUDENT, COURSE
    }

    fun showAddBottomPopup(mgs: String) {
        bottom_mgs_layout?.visibility = View.VISIBLE
        bottom_close?.setOnClickListener {
            bottom_mgs_layout?.visibility = View.GONE
        }
        bottom_mgs?.text = mgs
    }
}