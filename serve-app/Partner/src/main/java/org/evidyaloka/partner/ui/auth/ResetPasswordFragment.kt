package org.evidyaloka.partner.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_reset_password.*
import kotlinx.android.synthetic.main.layout_password_reset_info.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.partner.R
import org.evidyaloka.common.interfaces.IOnBackPressed
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.partner.ui.BaseFragment
import org.evidyaloka.common.helper.isValidPassword


/**
 * ResetPasswordFragment class handles Reset Password functionality
 */
@AndroidEntryPoint
class ResetPasswordFragment : BaseFragment(),
    IOnBackPressed {
    private val TAG = "ResetPasswordFragment"
    //Time in milliseconds to go to previous screen after successful password reset
    private val timeMillis = 3000L
    private lateinit var dialog: AlertDialog

    private val viewModel: AuthViewModel by viewModels()
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        etv_old_password?.addTextChangedListener(textListner)
        etv_new_password?.addTextChangedListener(textListner)
        etv_re_password?.addTextChangedListener(textListner)

        bt_change_password?.setOnClickListener {
            if (validatePasswords()) {
                hideKeyboard()
                buildRequestData()?.let {
                    til_old_password?.error = null
                    try {
                        viewModel.changePassword(it).observe(viewLifecycleOwner, Observer { response ->
                            when (response) {
                                is Resource.Success -> {
                                    response.data?.let { it ->
                                        //showSuccessView()
                                        showSuccessDialog()
                                    }
                                }
                                is Resource.GenericError -> {
                                    handleGenericError(response.error?.code)
                                }
                            }
                        })
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    /**
     * Handles generic errors
     */
    private fun handleGenericError(errorCode: Int?) {
        when (errorCode) {
            1 -> {
                til_old_password?.error = getString(R.string.error_invalid_credentials)
            }
            2 -> {
                til_re_password?.error = getString(R.string.error_required_field_missing)
            }
            6 -> {
                til_old_password?.error = getString(R.string.error_invalid_old_pass)
            }
            7 -> {
                til_new_password?.error = getString(R.string.error_old_new_not_matching)
            }
            8 -> {
                til_new_password?.error = getString(R.string.error_new_pass_min)
            }
            9 -> {
                til_re_password?.error = getString(R.string.error_user_not_exist)
            }
            10 -> {
                til_re_password?.error = getString(R.string.error_unknown)
            }
            101 -> {
                //TODO invalid sesson ID, logout the user?
            }
            else -> handleNetworkError()
        }
    }

    //Successful password updation
    private fun showSuccessView() {
        try {
            view_password_reset_info?.visibility = View.GONE
            view_password_reset_success?.visibility = View.VISIBLE
            openLoginActivity()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //Successful password updation
    private fun showSuccessDialog() {
        try {
            context?.let {
                dialog = MaterialAlertDialogBuilder(it, R.style.MaterialAlertDialogTheme)
                        .setView(R.layout.custom_dialog).setCancelable(true).create()
                dialog?.show()
            }
            openLoginActivity()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Take user to login activity to make him login with new password
    // Finish after timeMillis
    // after successful password change
    private fun openLoginActivity() {
        viewModel.clearUser()
        lifecycleScope.launch {
            delay(timeMillis)
            //Assuming the screen has a back button; LoginFragment must be in stack
            // and ResetPassword Screen was invoked from login screen
            dialog?.let {
                it.dismiss()
            }
            navController.popBackStack(R.id.loginFragment, false)
        }
    }

    /**
     * text listener for username and password and enable login button accordingly
     */
    private val textListner = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            // do nothing
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // do nothing
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            try {
                bt_change_password?.isEnabled =
                        !(etv_new_password?.text.toString().isBlank()
                                || etv_re_password?.text.toString().isBlank()
                                || etv_old_password?.text.toString().isBlank())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Generates the request body for reset password request
     * @return Map<String, Any>
     */
    private fun buildRequestData(): Map<String, Any>? {
        var map: HashMap<String, Any>? = null
        try {
            map = HashMap<String, Any>()
            map.put(PartnerConst.OLD_PASSWORD, etv_old_password?.text.toString())
            map.put(PartnerConst.NEW_PASSWORD, etv_re_password?.text.toString())

        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "gson converter exception")
        }
        return map
    }

    /**
     * validates all the passwords entered
     */
    private fun validatePasswords(): Boolean {
        var valid: Boolean = true

        til_old_password?.error = getString(R.string.enter_valid_old_pass).takeIf {
            etv_old_password?.text.toString().isBlank()
        }?.also {
            valid = false
        }

        til_old_password?.error = getString(R.string.old_pass_new_cant_not_same).takeIf {
            (etv_old_password?.text.toString().equals(etv_re_password.text.toString())) ||
                    (etv_old_password?.text.toString().equals(etv_new_password?.text.toString()))
        }?.also {
            valid = false
        }

        til_new_password.error =
                (getString(R.string.invalid_password)).takeIf {
                    !etv_new_password?.text.toString().isValidPassword()
                }?.also {
                    valid = false
                }

        til_re_password.error =
                getString(R.string.invalid_password).takeIf {
                    !etv_re_password?.text.toString().isValidPassword()
                }?.also {
                    valid = false
                }

        til_re_password.error =
                getString(R.string.invalid_password_new_re_no_match).takeIf {
                    !etv_re_password?.text.toString().equals(etv_new_password?.text.toString())
                }?.also {
                    valid = false
                }


        return valid
    }

    // on back press user have to navigate to respective screen from reset password page. If not handled user will be navigated to login screen again.
    override fun onBackPressed(): Boolean {
        try {
            viewModel.getUser().observe(viewLifecycleOwner, Observer { user ->
                user?.takeIf { it.id != 0 }?.let {
                    if (user.roles.contains(PartnerConst.RoleType.DSP.name)) {
                        val bundle = Bundle()
                        if (user.roles.contains(PartnerConst.RoleType.DSM.name)) {
                            bundle.putSerializable(PartnerConst.DSP_HAS_DSM_ROLE, true)
                        }
                        navController.navigate(R.id.action_global_dspHomeFragment, bundle)
                    } else {
                        navController.navigate(R.id.action_global_dsmHomeFragment)
                    }

                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }

}