package org.evidyaloka.partner.ui.dsm

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_dsm_personal_details_edit.*
import org.evidyaloka.common.helper.isValidEmail
import org.evidyaloka.common.helper.isValidName
import org.evidyaloka.common.helper.isValidPhoneNumber
import org.evidyaloka.common.helper.isValidPincode
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.User
import org.evidyaloka.partner.R
import org.evidyaloka.partner.ui.BaseFragment
import kotlin.collections.set


@AndroidEntryPoint
class EditUserDetailsFragment : BaseFragment() {

    private val TAG: String = "DsmUserDetailsFragment"
    private lateinit var mView: View
    private val viewModel: UserViewModel by viewModels()

    private var selectedUserId: Int? = null
    private var profilePicId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedUserId = arguments?.getInt(PartnerConst.USER_ID)
        profilePicId = arguments?.getInt(PartnerConst.PROFILE_PIC_ID)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        if (!this::mView.isInitialized) {
            mView = inflater.inflate(R.layout.layout_dsm_personal_details_edit, container, false)
        }
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        loadUserDetails()

        bt_save_user_details?.setOnClickListener {
            if (validatePersonDetails()) {
                hideKeyboard()
                updateProfileCall()
            }
        }
    }

    private fun loadUserDetails() {
        try {
            selectedUserId?.let {
                viewModel.getUserDetails(it).observe(viewLifecycleOwner, Observer { response ->
                    when (response) {
                        is Resource.Success -> {
                            response.data?.let {
                                it[0]?.let {
                                    updateProfileDetails(it)
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

    /**
     * This function validates the personal details fields
     * @return Boolean
     */
    private fun validatePersonDetails(): Boolean {
        var valid: Boolean = true
        til_first_name?.error =
                getString(R.string.invalid_first_name).takeIf {
                    !etv_first_name?.text.toString().isValidName()
                }
                        ?.also {
                            valid = false
                        }

        til_last_name?.error =
                getString(R.string.invalid_last_name).takeIf { !etv_last_name?.text.toString().isValidName() }
                        ?.also {
                            valid = false
                        }
        til_mail?.error =
                getString(R.string.invalid_mail).takeIf { !etv_mail?.text.toString().isValidEmail() }
                        ?.also {
                            valid = false
                        }
        til_phone?.error = getString(R.string.invalid_phone).takeIf {
            !etv_phone?.text.toString().isValidPhoneNumber()
        }?.also {
            valid = false
        }

        til_taluk?.error = getString(R.string.invalid_taluk)
                .takeIf { etv_taluk?.text.isNullOrEmpty() }?.also {
                    valid = false
                }

        til_district?.error = getString(R.string.invalid_district_alone)
                .takeIf { etv_district?.text.isNullOrEmpty() }?.also {
                    valid = false
                }

        til_state?.error = getString(R.string.invalid_state)
                .takeIf { etv_state?.text.isNullOrEmpty() }?.also {
                    valid = false
                }

        til_pincode?.error = getString(R.string.invalid_pincode)
                .takeIf { !etv_pincode?.text.toString().isValidPincode() }?.also {
                    valid = false
                }

        return valid
    }

    private fun updateProfileCall() {
        try {
            buildRequestData()?.let { it1 ->
                selectedUserId?.let { id ->
                    viewModel.updateDsmUser(id, it1)
                            .observe(viewLifecycleOwner, Observer { response ->
                                when (response) {
                                    is Resource.Success -> {
                                        showSnackBar(getString(R.string.dsm_profile_update_success))
                                        loadUserDetails()
                                    }
                                    is Resource.GenericError -> {
                                        response?.error?.let {
                                            handleGenericError(it.code)
                                        }
                                    }
                                }
                            })
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * updates the User Details
     */
    private fun updateProfileDetails(user: User) {
        try {
            etv_first_name?.setText(user.fname)
            etv_last_name?.setText(user.lname)
            etv_mail?.setText(user.email)
            etv_phone?.setText(user.phone)
            etv_taluk?.setText(user.taluk)
            etv_district?.setText(user.district)
            etv_state?.setText(user.state)
            etv_pincode?.setText(user.pincode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleGenericError(code: Int?) {
        when (code) {
            1 -> {
                showSnackBar(getString(R.string.error_mobile_already_registered))
            }
            2 -> {
                showSnackBar(getString(R.string.error_email_already_registered))
            }
            28 -> {
                showSnackBar(getString(R.string.error_school_not_exist))
            }
            else -> handleNetworkError()
        }
    }

    private fun buildRequestData(): Map<String, Any>? {
        val data = HashMap<String, Any>()

        try {
            data[PartnerConst.FIRST_NAME] = etv_first_name?.text.toString()
            data[PartnerConst.LAST_NAME] = etv_last_name?.text.toString()
            data[PartnerConst.PHONE] = etv_phone?.text.toString()
            data[PartnerConst.TALUK] = etv_taluk?.text.toString()
            data[PartnerConst.STATE] = etv_state?.text.toString()
            data[PartnerConst.DISTRICT] = etv_district?.text.toString()
            data[PartnerConst.PINCODE] = etv_pincode?.text.toString()
            profilePicId?.let {
                data[PartnerConst.PROFILE_PIC_ID] = it
            }
            Log.e(TAG, "buildRequestData: " + PartnerConst.SpinnerRoleType.DSM.toString())
            data[PartnerConst.ROLE] = PartnerConst.SpinnerRoleType.DSM.toString()

        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "gson converter exception")
        }
        return data
    }
}