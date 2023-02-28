package org.evidyaloka.partner.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.common.helper.isValidEmail
import org.evidyaloka.common.helper.isValidName
import org.evidyaloka.common.helper.isValidPhoneNumber
import org.evidyaloka.common.interfaces.IOnBackPressed
import org.evidyaloka.partner.BuildConfig
import org.evidyaloka.partner.R
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.helper.ErrorData
import org.evidyaloka.partner.NavGraphDirections
import org.evidyaloka.partner.databinding.FragmentRegistrationBinding
import org.evidyaloka.partner.ui.BaseFragment

/**
 * Registration Page for Partner/DSP
 */
@AndroidEntryPoint
class RegistrationFragment : BaseFragment(),
    IOnBackPressed {
    private val TAG: String = "RegistrationFragment"
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: FragmentRegistrationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        //Next button click listener
        binding.viewOrgInfo.btNext?.setOnClickListener {
            if (validateOrganizationDetails()) {
                binding.viewPersonDetails.etvPersonName?.text = binding.viewOrgInfo.etvName?.text
                showPersonView()
            }
        }

        binding.viewOrgInfo.tvHaveAccount?.setOnClickListener {
            navController.popBackStack()
        }

        binding.viewPersonDetails.etvPersonName?.addTextChangedListener(textListner)
        binding.viewPersonDetails.etvPersonMail?.addTextChangedListener(textListner)
        binding.viewPersonDetails.etvPersonPhone?.addTextChangedListener(textListner)
        binding.viewPersonDetails.cbAcceptTnc?.setOnCheckedChangeListener(checkBoxListener)

        //Register button click listener
        binding.viewPersonDetails.btRegister?.setOnClickListener {
            if (validatePersonDetails()) {
                hideKeyboard()
                buildRequestData()?.let {
                    viewModel.doRegister(it).observe(viewLifecycleOwner, Observer { response ->
                        when (response) {
                            is Resource.Success -> {
                                showSuccessView()
                            }
                            is Resource.GenericError -> {
                                handleGenericError(response.code, response.error)
                            }
                        }
                    })
                }
            }
        }
        //Login button click listener on confirmation screen
        binding.viewOrgInfo.tvHaveAccount?.setOnClickListener {
            navController.popBackStack()
        }
        binding.viewPersonDetails.tvTermsConditions?.setOnClickListener {
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
        binding.viewPersonDetails.cbAcceptTnc?.setOnCheckedChangeListener(checkBoxListener)
    }

    private val checkBoxListener = object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(p0: CompoundButton?, checked: Boolean) {
            binding.viewPersonDetails.btRegister?.isEnabled =
                (!(binding.viewPersonDetails.etvPersonPhone?.text.toString()
                    .isBlank() || binding.viewPersonDetails.etvPersonMail?.text.toString()
                    .isBlank() || binding.viewPersonDetails.etvPersonName?.text.toString()
                    .isBlank()) && binding.viewPersonDetails.cbAcceptTnc?.isChecked == true)
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
            binding.viewPersonDetails.btRegister?.isEnabled =
                (!(binding.viewPersonDetails.etvPersonPhone?.text.toString().isBlank() || binding.viewPersonDetails.etvPersonMail?.text.toString()
                    .isBlank() || binding.viewPersonDetails.etvPersonName?.text.toString()
                    .isBlank()) && binding.viewPersonDetails.cbAcceptTnc?.isChecked == true)
        }

    }

    /**
     * function will make organizationEntity details view visible
     */
    private fun showOrganizationView() {
        try {
            binding.viewOrgInfo.root?.visibility = View.VISIBLE
            binding.viewPersonDetails?.root.visibility = View.GONE
            binding.viewRegistrationConfirmation.root?.visibility = View.GONE
            binding.llTrackSteps.currentStep(1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * function will make person/Admin details view visible
     */
    private fun showPersonView() {
        try {
            binding.viewOrgInfo.root?.visibility = View.GONE
            binding.viewPersonDetails?.root.visibility = View.VISIBLE
            binding.viewRegistrationConfirmation.root?.visibility = View.GONE
            binding.llTrackSteps.currentStep(2)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * function will make registration success view visible
     */
    private fun showSuccessView() {
        try {
            binding.viewOrgInfo.root?.visibility = View.GONE
            binding.viewPersonDetails?.root.visibility = View.GONE
            binding.llTrackSteps?.visibility = View.GONE
            hideToolbar()
            binding.viewRegistrationConfirmation.root?.visibility = View.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Function is responsible for handling server side error
     * Error message will be set based on error response
     */
    private fun handleGenericError(errorCode: Int?, error: ErrorData?) {
        if (error != null) {
            when (error.code) {
                1, 2, 3, 14 -> {
                    error?.message?.let { showSnackBar(it) }
                }
                4 -> {
                    binding.viewPersonDetails.tilPersonMail?.error = "Contact person email already registered"
                    showPersonView()
                }
                5 -> {
                    binding.viewOrgInfo.tilOrgName?.error = "organizationEntity name already registered"
                    showOrganizationView()
                }
                else -> handleNetworkError()
            }
        }
    }

    /**
     * Generates the request body for Partner registration
     * @return String
     */
    private fun buildRequestData(): Map<String, Any>? {
        var map: HashMap<String, Any>? = null
        try {
            val organization = HashMap<String, Any>().apply {
                this["partnerName"] = binding.viewOrgInfo.etvName?.text.toString()
                this["name"] = binding.viewOrgInfo.etvOrgName?.text.toString()
                this["email"] = binding.viewOrgInfo.etvOrgEmail?.text.toString()
                this["phone"] = binding.viewOrgInfo.etvOrgPhone?.text.toString()
                this["address"] = binding.viewOrgInfo.etvOrgAddress1?.text.toString()
            }
            val user = HashMap<String, Any>().apply {
                this["fname"] = binding.viewPersonDetails.etvPersonName?.text.toString()
                this["lname"] = binding.viewPersonDetails.etvPersonLname?.text.toString()
                this["email"] = binding.viewPersonDetails.etvPersonMail?.text.toString()
                this["phone"] = binding.viewPersonDetails.etvPersonPhone?.text.toString()
            }
            map = HashMap<String, Any>()
            map.put("organization", organization)
            map.put("contact", user)
        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "gson converter exception")
        }
        return map
    }


    /**
     * This function validates the organizationEntity details fields
     * @return Boolean
     */
    private fun validateOrganizationDetails(): Boolean {
        var valid: Boolean = true
        binding.viewOrgInfo.tilName?.error =
            getString(R.string.invalid_name).takeIf { !binding.viewOrgInfo.etvName?.text.toString().isValidName() }
                ?.also {
                    valid = false
                }

        binding.viewOrgInfo.tilOrgName?.error =
            getString(R.string.invalid_name).takeIf { !binding.viewOrgInfo.etvOrgName?.text.toString().isValidName() }
                ?.also {
                    valid = false
                }

        binding.viewOrgInfo.tilOrgEmail?.error = getString(R.string.invalid_mail).takeIf {
            !binding.viewOrgInfo.etvOrgEmail?.text.toString().isValidEmail()
        }?.also {
            valid = false
        }
        binding.viewOrgInfo.tilOrgPhone?.error = getString(R.string.invalid_phone).takeIf {
            !binding.viewOrgInfo.etvOrgPhone?.text.toString().isValidPhoneNumber()
        }?.also {
            valid = false
        }
        binding.viewOrgInfo.tilOrgAddress1?.error =
            getString(R.string.invalid_address).takeIf { binding.viewOrgInfo.etvOrgAddress1?.text.isNullOrEmpty() }
                ?.also {
                    valid = false
                }
        return valid
    }

    /**
     * This function validates the person / Admin details fields
     * @return Boolean
     */
    private fun validatePersonDetails(): Boolean {
        var valid: Boolean = true
        binding.viewPersonDetails.tilPersonName?.error =
            getString(R.string.invalid_name).takeIf {
                !binding.viewPersonDetails.etvPersonName?.text.toString().isValidName()
            }
                ?.also {
                    valid = false
                }
        binding.viewPersonDetails.tilPersonMail?.error =
            getString(R.string.invalid_mail).takeIf {
                !binding.viewPersonDetails.etvPersonMail?.text.toString().isValidEmail()
            }
                ?.also {
                    valid = false
                }
        binding.viewPersonDetails.tilPersonPhone?.error = getString(R.string.invalid_phone).takeIf {
            !binding.viewPersonDetails.etvPersonPhone?.text.toString().isValidPhoneNumber()
        }?.also {
            valid = false
        }
        return valid
    }

    override fun onBackPressed(): Boolean {
        return if (binding.viewPersonDetails.root.visibility == View.VISIBLE) {
            binding.viewOrgInfo.root?.visibility = View.VISIBLE
            binding.viewPersonDetails.root?.visibility = View.GONE
            true
        } else {
            false
        }
    }
}