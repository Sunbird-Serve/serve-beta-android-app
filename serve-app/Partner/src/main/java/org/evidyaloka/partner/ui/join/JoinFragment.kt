package org.evidyaloka.partner.ui.join

import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ScrollView
import android.widget.TextView
import android.widget.TextView.BufferType
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.coroutines.launch
import org.evidyaloka.common.helper.*
import org.evidyaloka.common.receiver.OtpReceiver
import org.evidyaloka.common.ui.banner.BannersAdapter
import org.evidyaloka.common.util.TutorialUtil
import org.evidyaloka.common.util.Utils
import org.evidyaloka.core.Constants.CommonConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.ExploreData
import org.evidyaloka.core.partner.model.Organization
import org.evidyaloka.core.partner.model.User
import org.evidyaloka.partner.BuildConfig
import org.evidyaloka.partner.NavGraphDirections
import org.evidyaloka.partner.R
import org.evidyaloka.partner.databinding.FragmentJoinBinding
import org.evidyaloka.partner.ui.BaseFragment


@AndroidEntryPoint
class JoinFragment : BaseFragment() {
    private var TAG = JoinFragment::class.java.simpleName

    private var bannersAdapter = BannersAdapter()

    private var otpReceiver: OtpReceiver<JoinFragment>? = null
    private var isNumChanged = false
    private val viewModel: JoinViewModel by viewModels()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var formbottomSheet: BottomSheetBehavior<ScrollView>

    private lateinit var binding: FragmentJoinBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJoinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStop() {
        super.onStop()
        try {
            activity?.let {
                it.toolbarTitle?.text = ""
            }
        } catch (e: Exception) {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateToolbarTitle("")

        customTextView(binding.tvTermsConditions)

        activity?.let {
            it.toolbarTitle?.text = resources.getString(R.string.login)
            it.toolbarTitle?.setOnClickListener {
                var bundle = Bundle()
                bundle.putString(CommonConst.USER_TYPE, CommonConst.PersonaType.Partner.toString())
                bundle.putString(
                    CommonConst.USER_ACTION,
                    CommonConst.UserActionType.Login.toString()
                )
                navController?.navigate(R.id.loginFragment)
            }
        }

        getBanners()

        viewModel.getProgressObserable().observe(viewLifecycleOwner, Observer {
            binding.progressCircular.visibility =
                if (it) View.VISIBLE else View.GONE
        })

        bottomSheetBehavior =
            BottomSheetBehavior.from(binding?.bottomSheetOtp?.bottomSheetContainer).apply {
                isDraggable = false
            }

        binding.root.doOnLayout {
            formbottomSheet = BottomSheetBehavior.from(binding.scrollView).apply {
                setPeekHeight(
                    binding.root.height - binding.linearLayout3.height
                )
            }
        }

        val focusListner = object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (hasFocus)
                    formbottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        binding.etvFullName.onFocusChangeListener = focusListner
        binding.etvOrgName.onFocusChangeListener = focusListner

        binding.etvMobile?.addTextChangedListener(textListener)
        binding.etvFullName?.addTextChangedListener(textListener)
        binding.etvEmail?.addTextChangedListener(textListener)
        binding.etvFullName?.addTextChangedListener(textListener)


        binding.bottomSheetOtp?.etvOtp?.addTextChangedListener(textListenerOtp)

        binding.btSendOtp?.setOnClickListener {
            onSendOTPClick()
        }

        binding.bottomSheetOtp?.btVerifyOtp?.setOnClickListener {
            onVerifyClick()
        }

        binding.cbAcceptTnc.setOnCheckedChangeListener(checkBoxListener)

        binding.bottomSheetOtp?.tvChangeNumber?.setOnClickListener {
            isNumChanged = true
            binding.etvMobile?.setText("")
            binding.bottomSheetOtp?.tilOtp?.error = null
            binding.bottomSheetOtp?.etvOtp?.setText("")
            viewModel.cancelCountdown()
            enablePreviousScreen()
            hideValidationScreen()
        }

        binding.bottomSheetOtp?.tvResendOtp?.setOnClickListener {
            binding.bottomSheetOtp?.tilOtp?.error = null
            binding.bottomSheetOtp?.etvOtp?.setText("")
            viewModel.cancelCountdown()
            onSendOTPClick()
        }

        viewModel.duration().observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.bottomSheetOtp?.tvOtpTimeout?.text = Utils.formatTimout(it)
                if (it == 0) {
                    binding.bottomSheetOtp?.tvResendOtp?.isEnabled = true
                    viewModel.cancelCountdown()
                }
            }
        })
    }

    private fun disablePreviousScreen() {
        binding.etvMobile?.isEnabled = false
        binding.btSendOtp?.isEnabled = false
    }

    private fun enablePreviousScreen() {
        binding.etvMobile?.isEnabled = true
        binding.btSendOtp?.isEnabled = false
    }

    private fun startCountDownTimer() {
        viewModel.startCountdown()
        binding.bottomSheetOtp?.tvResendOtp?.isEnabled = false
    }

    fun onSendOTPClick() {
        isNumChanged = false
        hideKeyboard()
        binding.tilMobile?.error = null
        if (validateDetails() == false)
            return
        buildRequestDataSignin()?.let {
            viewModel.generateOTP(it).observe(viewLifecycleOwner, Observer {
                when (it) {
                    is Resource.Success -> {
                        autoReceiveOTP()
                        binding.bottomSheetOtp?.tvMobile.text =
                            binding.etvMobile?.text.toString()
                        disablePreviousScreen()
                        startCountDownTimer()
                        showValidationScreen()
                    }
                    is Resource.GenericError -> {
                        it?.error?.let {
                            when (it.code) {
                                2 ->
                                    binding.tilMobile?.error =
                                        resources?.getString(R.string.error_required_fields_missing)
                                3 ->
                                    binding.tilMobile?.error =
                                        resources?.getString(R.string.error_data_not_valid)
                                31 ->
                                    binding.tilMobile?.error =
                                        resources?.getString(R.string.error_invalid_mobile)
                                4, 5 -> binding.tilMobile?.error =
                                    getString(R.string.same_contact_details)
                                78 -> binding.tilOrgEmail.error =
                                    getString(R.string.email_already_registered_with_us)
                                79 -> binding.tilMobile.error =
                                    getString(R.string.mobile_number_already_registred_with_us)
                                80 -> binding.tilOrgName.error = getString(R.string.same_org_name)
                                else ->
                                    binding.tilMobile?.error =
                                        it.message
                            }
                        }
                    }
                }
            })
        }
    }


    private fun autoReceiveOTP() {
        if (otpReceiver == null) {
            otpReceiver = OtpReceiver(this@JoinFragment) { otp, isTimeout ->
                otpReceiver = null
                if (!isTimeout && !otp.isNullOrBlank()) {
                    binding.bottomSheetOtp?.etvOtp?.setText(otp)
                    binding.bottomSheetOtp?.etvOtp?.placeCursorToEnd()
                    onVerifyClick()
                }
            }
        }
    }

    fun onVerifyClick() {
        hideKeyboard()
        if (validateDetails() == false)
            return
        buildRequestDataVerify()?.let {
            viewModel.verifyOTP(it).observe(viewLifecycleOwner, Observer {
                when (it) {
                    is Resource.Success -> {
                        viewModel.cancelCountdown()
                        it?.data?.let {
                            it.organization = Organization(
                                name = binding.etvOrgName.text.toString(),
                                partnerName = binding.etvFullName.text.toString()
                            )
                            it.phone = binding.etvMobile.text.toString()
                            it.email = binding.etvEmail.text.toString()
                            it.fname = binding.etvFullName.text.toString()
                            saveUser(it)
                        }
                    }
                    is Resource.GenericError -> {
                        // TODO Handle Errors
                        it?.error?.let {
                            when (it.code) {
                                2 ->
                                    binding.bottomSheetOtp?.tilOtp?.error =
                                        resources?.getString(R.string.error_required_fields_missing)
                                3 ->
                                    binding.bottomSheetOtp?.tilOtp?.error =
                                        resources?.getString(R.string.error_data_not_valid)
                                31 ->
                                    binding.bottomSheetOtp?.tilOtp?.error =
                                        resources?.getString(R.string.error_invalid_mobile)
                                32 ->
                                    binding.bottomSheetOtp?.tilOtp?.error =
                                        resources?.getString(R.string.error_otp_expired)
                                else ->
                                    binding.bottomSheetOtp?.tilOtp?.error =
                                        resources?.getString(R.string.error_otp_invalid)
                            }
                        }
                    }
                }
            })
        }
    }

    fun saveUser(user: User) {
        viewModel.saveUser(user).observe(viewLifecycleOwner, Observer {
            var exploreData = ExploreData(user = user)
            navController.navigate(
                JoinFragmentDirections.actionJoinFragmentToPartnerExploreSchoolBoard(
                    exploreData
                )
            )
        })
    }


    /**
     * Show OTP validation screen.
     */
    fun showValidationScreen() {
        bottomSheetBehavior?.setState(BottomSheetBehavior.STATE_EXPANDED)
        formbottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.scrollView.visibility = View.GONE
    }

    /**
     * Hide OTP validation screen.
     */
    fun hideValidationScreen() {
        bottomSheetBehavior?.setState(BottomSheetBehavior.STATE_COLLAPSED)
        formbottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        binding.scrollView.visibility = View.VISIBLE
    }

    /**
     * This function validates the organizationEntity details fields
     * @return Boolean
     */
    private fun validateDetails(): Boolean {
        var valid: Boolean = true
        binding.tilFullName?.error =
            getString(R.string.invalid_name).takeIf {
                !binding.etvFullName?.text.toString().isValidName()
            }
                ?.also {
                    valid = false
                }

        binding.tilOrgName?.error =
            getString(R.string.invalid_name).takeIf {
                !binding.etvOrgName?.text.toString().isValidName()
            }
                ?.also {
                    valid = false
                }

        binding.tilOrgEmail?.error = getString(R.string.invalid_mail).takeIf {
            !binding.etvEmail?.text.toString().isValidEmail()
        }?.also {
            valid = false
        }
        binding.tilMobile?.error = getString(R.string.invalid_phone).takeIf {
            !binding.etvMobile?.text.toString().isValidPhoneNumber()
        }?.also {
            valid = false
        }
        return valid
    }

    /*
    * This function creates request body for login API/OTP Generation
    * returns Map
    */
    private fun buildRequestDataSignin(): Map<String, Any>? {
        var map: HashMap<String, Any>? = null
        try {
            map = hashMapOf()
            map.put("name", binding.etvFullName.text.toString())
            map.put("email", binding.etvEmail.text.toString())
            map.put("orgName", binding.etvOrgName.text.toString())
            map.put("mobile", binding.etvMobile?.text.toString())
            Utils.getDeviceInfo(requireContext(), "PARTNER")?.let {
                map.put("deviceInfo", it)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e(TAG, e.message ?: "gson converter exception")
        }
        return map
    }

    /*
     * This function creates request body for OTP verification
     * returns Map
     */
    private fun buildRequestDataVerify(): Map<String, Any>? {
        var map: HashMap<String, Any>? = null
        try {
            map = hashMapOf()
            map.put("name", binding.etvFullName.text.toString())
            map.put("email", binding.etvEmail.text.toString())
            map.put("orgName", binding.etvOrgName.text.toString())
            map.put("mobile", binding.etvMobile?.text.toString())
            map.put("otp", binding.bottomSheetOtp?.etvOtp?.text.toString())
            Utils.getDeviceInfo(requireContext(), "PARTNER")?.let {
                map.put("deviceInfo", it)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e(TAG, e.message ?: "gson converter exception")
        }
        return map
    }

    /**
     * text listener for observing valid mobile number.
     */
    private val textListener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            // do nothing
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // do nothing
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.tilMobile?.error = null

            binding.btSendOtp?.isEnabled = ((!binding.etvMobile?.text.isBlank()
                    && binding.etvMobile?.text.toString().isValidPhoneNumber()
                    && binding.etvMobile?.text.toString().length == 10
                    && !binding.etvFullName?.text.isBlank() && binding.etvFullName?.text.toString()
                .isValidName()
                    && !binding.etvEmail?.text.isBlank() && binding.etvEmail?.text.toString()
                .isValidEmail()
                    && !binding.etvOrgName?.text.isBlank() && binding.etvOrgName?.text.toString()
                .isValidName()) && binding.cbAcceptTnc.isChecked)
        }
    }

    private val textListenerOtp = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            // do nothing
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // do nothing
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            binding.bottomSheetOtp?.tilOtp?.error = null

            binding.bottomSheetOtp?.btVerifyOtp.isEnabled =
                (!binding.bottomSheetOtp?.etvOtp?.text.isBlank()
                        && (binding.bottomSheetOtp?.etvOtp?.text.toString().length == 4
                        || binding.bottomSheetOtp?.etvOtp?.text.toString().length == 6))
        }
    }


    private fun getBanners() {
        try {
            binding.rvBanners?.let {
                val snapHelper: SnapHelper = PagerSnapHelper()
                it.layoutManager = LinearLayoutManager(
                    it?.context,
                    LinearLayoutManager.HORIZONTAL, false
                )
                it.adapter = bannersAdapter
                binding.pagerIndicator?.attachToRecyclerView(it)
                snapHelper.attachToRecyclerView(it)
                it.context?.let {
                    bannersAdapter.setItems(TutorialUtil.getPartnersTutorial(it))
                }
                lifecycleScope?.launch { AutoScrollList.autoScrollFeaturesList(it,bannersAdapter) }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun customTextView(view: TextView) {
        try {
            val spanTxt = SpannableStringBuilder(
                getString(R.string.tnc_1)
            )
            spanTxt.append(getString(R.string.tnc_2))
            spanTxt.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
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
            }, spanTxt.length - getString(R.string.tnc_2).length, spanTxt.length, 0)
            spanTxt.setSpan(
                ForegroundColorSpan(view.resources.getColor(R.color.colorOnSecondary_60opc)),
                spanTxt.length - getString(R.string.tnc_2).length,
                spanTxt.length,
                0
            )
            spanTxt.append(getString(R.string.tnc_3))
            spanTxt.append(getString(R.string.tnc_4))
            spanTxt.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val navigate = NavGraphDirections.commonNavigation(
                        BuildConfig.PRIVACY_POLICY_URL,
                        getString(R.string.menu_dashboard_terms_of_usage)
                    )
                    try {
                        navController.navigate(navigate)
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)

                    }
                }
            }, spanTxt.length - getString(R.string.tnc_4).length, spanTxt.length, 0)
            spanTxt.setSpan(
                ForegroundColorSpan(
                    view.resources.getColor(
                        R.color.colorOnSecondary_60opc
                    )
                ), spanTxt.length - getString(R.string.tnc_4).length, spanTxt.length, 0
            )
            view.movementMethod = LinkMovementMethod.getInstance()
            view.setText(spanTxt, BufferType.SPANNABLE)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private val checkBoxListener = object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(p0: CompoundButton?, checked: Boolean) {
            binding.btSendOtp?.isEnabled = ((!binding.etvMobile?.text.isBlank()
                    && binding.etvMobile?.text.toString().isValidPhoneNumber()
                    && binding.etvMobile?.text.toString().length == 10
                    && !binding.etvFullName?.text.isBlank() && binding.etvFullName?.text.toString()
                .isValidName()
                    && !binding.etvEmail?.text.isBlank() && binding.etvEmail?.text.toString()
                .isValidEmail()
                    && !binding.etvOrgName?.text.isBlank() && binding.etvOrgName?.text.toString()
                .isValidName()) && binding.cbAcceptTnc?.isChecked)
        }
    }

}