package org.evidyaloka.partner.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.common.util.Utils
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.User
import org.evidyaloka.partner.BuildConfig
import org.evidyaloka.partner.NavGraphDirections
import org.evidyaloka.partner.R
import org.evidyaloka.partner.ui.BaseFragment


/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    private val TAG = "LoginFragment"

    private val viewModel: AuthViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onResume() {
        hideToolbar()
        lockDrawer()
        hideNavHeaderRoleToggleButton()
        super.onResume()
    }

    override fun onPause() {
        unlockDrawer()
        super.onPause()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
//        checkUserAndNavigateHomeScreen()
        etv_user_name?.setText("")
        etv_password?.setText("")
        cb_accept_tnc?.isChecked = false
        etv_user_name?.addTextChangedListener(textListner)
        etv_password?.addTextChangedListener(textListner)
        cb_accept_tnc?.setOnCheckedChangeListener(checkBoxListener)

        bt_login?.setOnClickListener {
            if (validate()) {
                hideKeyboard()
                til_password?.error = null
                buildRequestData()?.let {
                    viewModel.doLogin(it).observe(viewLifecycleOwner, Observer { response ->
                        when (response) {
                            is Resource.Success -> {
                                etv_password.setText("")
                                etv_user_name.setText("")
                                cb_accept_tnc?.isChecked = false
                                response.data?.let { it1 -> saveUserNavigateToHome(it1 as User) }
                            }
                            is Resource.GenericError -> {
                                til_password.error = getString(R.string.invalid_credentials)
                            }
                        }
                    })
                }
            }
        }

        tv_register_as_partner?.setOnClickListener {
            try {
                val navOption = NavOptions.Builder().apply {
                    setPopUpTo(R.id.joinFragment, true)
                }.build()
                navController.navigate(LoginFragmentDirections.actionLoginFragmentToJoinFragment(), navOption)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)

            }
        }
        tv_terms_conditions?.setOnClickListener {
            val navigate = NavGraphDirections.commonNavigation(
                BuildConfig.TERMS_AND_CONDITIONS_URL,
                getString(R.string.menu_dashboard_terms_of_usage)
            )
            try {
                navController.navigate(navigate)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)

            }
        }
        tv_forgot_password?.setOnClickListener {
            startActivity(
                Intent.createChooser(
                    Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.FORGET_PASSWORD_URL)),
                    getString(R.string.open_with)
                )
            )
        }
    }


    private fun checkUserAndNavigateHomeScreen() {
        viewModel.getUser().observe(viewLifecycleOwner, Observer { user ->
            user?.takeIf { it.id != 0 }?.let {
                navigateUserHomeScreen(it)
            }
        })
    }

    /**
     * Generates the request body for login request
     * @return String
     */
    private fun buildRequestData(): Map<String, Any>? {
        var map: HashMap<String, Any>? = null
        try {
            map = hashMapOf()
            map.put("username", etv_user_name?.text.toString())
            map.put("password", etv_password?.text.toString())
            Utils.getDeviceInfo(requireContext(), "PARTNER")?.let {
                map.put("deviceInfo", it)
            }

        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "gson converter exception")
        }
        return map
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
            bt_login?.isEnabled =
                (!(etv_user_name?.text.toString().isBlank() || etv_password?.text.toString()
                    .isBlank()) && cb_accept_tnc?.isChecked == true)
        }

    }

    private val checkBoxListener = object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(p0: CompoundButton?, checked: Boolean) {
            bt_login?.isEnabled =
                (!(etv_user_name?.text.toString().isBlank() || etv_password?.text.toString()
                    .isBlank()) && cb_accept_tnc?.isChecked == true)
        }
    }

    /**
     * function navigates user to HomeScreen/Landing page based on the users role.
     * @param user User logged in user
     */
    private fun navigateUserHomeScreen(user: User) {
        try {
            if (user.roles.contains(PartnerConst.RoleType.DSP.name)) {
                val bundle = Bundle()
                if (user.roles.contains(PartnerConst.RoleType.DSM.name)) {
                    bundle.putSerializable(PartnerConst.DSP_HAS_DSM_ROLE, true)
                }
                navController.navigate(R.id.action_global_dspHomeFragment, bundle)
            } else {
                navController.navigate(R.id.action_global_dsmHomeFragment)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Saves logged in User Details to the SharedPreference & Navigates to Reset Password Screen
     * if isResetPwdRequired true else to Home Screen
     * @param user User logged in User
     */
    private fun saveUserNavigateToHome(user: User) {
        try {
            viewModel.saveUser(user).observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        if (user.isResetPwdRequired) {
                            try {
                                navController.navigate(LoginFragmentDirections.actionLoginFragmentToResetPasswordFragment())
                            } catch (e: Exception) {
                                FirebaseCrashlytics.getInstance().recordException(e)
                            }
                        } else {
                            navigateUserHomeScreen(user)
                        }
                    }
                    is Resource.GenericError -> {
                        til_password?.error = getString(R.string.invalid_credentials)
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun validate(): Boolean {
        var valid: Boolean = true
        til_password?.error =
            getString(R.string.invalid_credentials).takeIf {
                etv_user_name?.text.toString().isBlank() || etv_password?.text.toString()
                    .isBlank()
            }?.also {
                valid = false
            }
        return valid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showToolbar()
    }

}
