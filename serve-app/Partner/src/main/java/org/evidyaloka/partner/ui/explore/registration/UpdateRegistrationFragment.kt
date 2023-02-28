package org.evidyaloka.partner.ui.explore.registration

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
import org.evidyaloka.core.partner.model.ExploreData
import org.evidyaloka.core.partner.model.User
import org.evidyaloka.partner.NavGraphDirections
import org.evidyaloka.partner.databinding.FragmentUpdateRegistrationBinding
import org.evidyaloka.partner.ui.BaseFragment

/**
 * Registration Page for Partner/DSP
 */
@AndroidEntryPoint
class UpdateRegistrationFragment : BaseFragment(),
    IOnBackPressed {
    private val TAG: String = "RegistrationFragment"
    private val viewModel: UpdateRegistrationViewModel by viewModels()
    private lateinit var binding: FragmentUpdateRegistrationBinding
    private var exploreData: ExploreData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            val args = UpdateRegistrationFragmentArgs.fromBundle(it)
            exploreData = args.exploreData
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUpdateRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)

        exploreData?.let{
            with(binding){
                etvName.setText(it.user?.organization?.partnerName)
                etvPersonName.setText(it.user?.fname)
                etvPersonMail.setText(it.user?.email)
                etvPersonPhone.setText(it.user?.phone)
                etvOrgName.setText(it.user?.organization?.name)
            }
        }
        //Next button click listener
        binding.btNext?.setOnClickListener {
            if (validateOrganizationDetails()) {
                binding.thankyouLayout.visibility = View.GONE
                binding.etvPersonName?.text = binding.etvName?.text
                showPersonView()
            }
        }

        binding.etvPersonName?.addTextChangedListener(textListner)
        binding.etvPersonMail?.addTextChangedListener(textListner)
        binding.etvPersonPhone?.addTextChangedListener(textListner)
        binding.cbAcceptTnc?.setOnCheckedChangeListener(checkBoxListener)

        //Register button click listener
        binding.btRegister?.setOnClickListener {
            if (validatePersonDetails()) {
                hideKeyboard()
                buildRequestData()?.let {
                    viewModel.updateRegister(it).observe(viewLifecycleOwner, Observer { response ->
                        when (response) {
                            is Resource.Success -> {
                                exploreData?.user?.let {
                                    var user = it
                                    user.apply {
                                        fname = binding.etvPersonName.text.toString()
                                        lname = binding.etvPersonLname.text.toString()
                                        partnerStatus = "new"
                                        organization.apply {
                                            partnerStatus = "new"
                                            email = binding.etvOrgEmail.text.toString()
                                            phone = binding.etvOrgPhone.text.toString()
                                            address = binding.etvOrgAddress1.text.toString()
                                        }
                                    }
                                    saveUser(user)
                                }


                            }
                            is Resource.GenericError -> {
                                handleGenericError(response.code, response.error)
                            }
                        }
                    })
                }
            }
        }

        binding.tvTermsConditions?.setOnClickListener {
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
        binding.cbAcceptTnc?.setOnCheckedChangeListener(checkBoxListener)
    }

    private val checkBoxListener = object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(p0: CompoundButton?, checked: Boolean) {
            binding.btRegister?.isEnabled =
                (!(binding.etvPersonPhone?.text.toString()
                    .isBlank() || binding.etvPersonMail?.text.toString()
                    .isBlank() || binding.etvPersonName?.text.toString()
                    .isBlank()) && binding.cbAcceptTnc?.isChecked == true)
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
            binding.btRegister?.isEnabled =
                (!(binding.etvPersonPhone?.text.toString().isBlank() || binding.etvPersonMail?.text.toString()
                    .isBlank() || binding.etvPersonName?.text.toString()
                    .isBlank()) && binding.cbAcceptTnc?.isChecked == true)
        }

    }

    /**
     * function will make organizationEntity details view visible
     */
    private fun showOrganizationView() {
        try {
            binding.viewOrgInfo?.visibility = View.VISIBLE
            binding.viewPersonDetails?.visibility = View.GONE
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
            hideKeyboard()
            binding.viewOrgInfo?.visibility = View.GONE
            binding.viewPersonDetails?.visibility = View.VISIBLE
            binding.llTrackSteps.currentStep(2)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * function will make registration success view visible
     */
    private fun saveUser(user: User) {
        try {
            viewModel.saveUser(user).observe(viewLifecycleOwner, Observer {
                navController.navigate(UpdateRegistrationFragmentDirections.actionGlobalDspHomeFragment())
            })
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
                    binding.tilPersonMail?.error = "Contact person email already registered"
                    showPersonView()
                }
                76 -> {
                    binding.tilOrgName?.error = "organizationEntity name already registered"
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
                this["partnerName"] = binding.etvName?.text.toString()
                this["name"] = binding.etvOrgName?.text.toString()
                this["email"] = binding.etvOrgEmail?.text.toString()
                this["phone"] = binding.etvOrgPhone?.text.toString()
                this["address"] = binding.etvOrgAddress1?.text.toString()
            }
            val user = HashMap<String, Any>().apply {
                this["fname"] = binding.etvPersonName?.text.toString()
                this["lname"] = binding.etvPersonLname?.text.toString()
                this["email"] = binding.etvPersonMail?.text.toString()
                this["phone"] = binding.etvPersonPhone?.text.toString()
            }
            map = HashMap<String, Any>()
            map.put("partnerId",exploreData?.user!!.partnerId)
            map.put("userType","DSP")
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
        binding.tilName?.error =
            getString(R.string.invalid_name).takeIf { !binding.etvName?.text.toString().isValidName() }
                ?.also {
                    valid = false
                }

        binding.tilOrgName?.error =
            getString(R.string.invalid_name).takeIf { !binding.etvOrgName?.text.toString().isValidName() }
                ?.also {
                    valid = false
                }

        binding.tilOrgEmail?.error = getString(R.string.invalid_mail).takeIf {
            !binding.etvOrgEmail?.text.toString().isValidEmail()
        }?.also {
            valid = false
        }
        binding.tilOrgPhone?.error = getString(R.string.invalid_phone).takeIf {
            !binding.etvOrgPhone?.text.toString().isValidPhoneNumber()
        }?.also {
            valid = false
        }
        binding.tilOrgAddress1?.error =
            getString(R.string.invalid_address).takeIf { binding.etvOrgAddress1?.text.isNullOrEmpty() }
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
        binding.tilPersonName?.error =
            getString(R.string.invalid_name).takeIf {
                !binding.etvPersonName?.text.toString().isValidName()
            }
                ?.also {
                    valid = false
                }
        binding.tilPersonMail?.error =
            getString(R.string.invalid_mail).takeIf {
                !binding.etvPersonMail?.text.toString().isValidEmail()
            }
                ?.also {
                    valid = false
                }
        binding.tilPersonPhone?.error = getString(R.string.invalid_phone).takeIf {
            !binding.etvPersonPhone?.text.toString().isValidPhoneNumber()
        }?.also {
            valid = false
        }
        return valid
    }

    override fun onBackPressed(): Boolean {
        return if (binding.viewPersonDetails.visibility == View.VISIBLE) {
            binding.viewOrgInfo?.visibility = View.VISIBLE
            binding.viewPersonDetails?.visibility = View.GONE
            true
        } else {
            false
        }
    }
}