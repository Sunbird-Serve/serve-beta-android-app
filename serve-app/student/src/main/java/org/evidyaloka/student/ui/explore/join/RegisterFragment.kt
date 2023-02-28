package org.evidyaloka.student.ui.explore.join

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import android.widget.CompoundButton
import android.widget.ScrollView
import android.widget.TextView
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
import kotlinx.coroutines.launch
import org.evidyaloka.common.helper.*
import org.evidyaloka.common.util.Utils
import org.evidyaloka.common.receiver.OtpReceiver
import org.evidyaloka.common.ui.banner.BannersAdapter
import org.evidyaloka.common.util.TutorialUtil
import org.evidyaloka.core.Constants.CommonConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.student.BuildConfig
import org.evidyaloka.student.R
import org.evidyaloka.student.StudentListNavigationDirections
import org.evidyaloka.student.databinding.FragmentParentRegisterBinding
import org.evidyaloka.student.ui.BaseFragment
import org.evidyaloka.student.ui.ParentExploreActivity

@AndroidEntryPoint
class RegisterFragment : BaseFragment() {
    private var TAG = RegisterFragment::class.java.simpleName
    private var userType: String? = null
    private var userAction: String? = null

    private var bannersAdapter = BannersAdapter()
    private var otpReceiver: OtpReceiver<RegisterFragment>? = null
    private var mReceiver: BroadcastReceiver? = null
    private var isNumChanged = false
    private val viewModel: JoinViewModel by viewModels()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var loginBottomSheet: BottomSheetBehavior<ScrollView>

    private lateinit var binding: FragmentParentRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            var args = RegisterFragmentArgs.fromBundle(it)
            userType = args.userType
            userAction = args.userAction
        }
        if (userType?.toString().equals(CommonConst.PersonaType.Parent.toString())) {
            setHasOptionsMenu(true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        getBanners()
        customTextView(binding.tvTermsConditions)
        viewModel.getProgressObserable().observe(viewLifecycleOwner, Observer
        { binding.progressCircular.visibility = if (it) View.VISIBLE else View.GONE })

        bottomSheetBehavior =
            BottomSheetBehavior.from(binding?.bottomSheetOtp?.bottomSheetContainer)
                .apply {
                    isDraggable = false
                };

        binding.etvMobile?.addTextChangedListener(textListener)
        binding.etvFullName?.addTextChangedListener(textListener)
        binding.cbAcceptTnc?.setOnCheckedChangeListener(checkBoxListener)

        binding.bottomSheetOtp?.etvOtp?.addTextChangedListener(textListenerOtp)

        binding.btSendOtp?.setOnClickListener { onSendOTPClick() }

        binding.bottomSheetOtp?.btVerifyOtp?.setOnClickListener { onVerifyClick() }
        binding.bottomSheetOtp.ivOtpIcon.apply {
            setImageDrawable(resources.getDrawable(R.drawable.ic_parent_registration_otp))
        }
        binding.root.doOnLayout {
            loginBottomSheet = BottomSheetBehavior.from(binding.scrollView).apply {
                setPeekHeight(binding.root.height - binding.linearLayout3.height - Utils.convertDpToPixel(20F,requireContext()).toInt())
            }
        }

        binding.etvFullName.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus)
                loginBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }
        binding.etvMobile.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus)
                loginBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.login_menu, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId.equals(R.id.nav_login)) {
            try {
                navController?.navigate(
                    RegisterFragmentDirections.actionFragmentRegisterToLoginFragment(
                        CommonConst.PersonaType.Parent.toString(),
                        CommonConst.UserActionType.Login.toString()
                    )
                )
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
        return super.onOptionsItemSelected(item)
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
        activity?.let{it.hideKeyboard()}
        binding.tilMobile?.error = null
        buildRequestDataSignin()?.let {
            viewModel.generateOTP(it)
                .observe(viewLifecycleOwner, androidx.lifecycle.Observer { response ->
                    when (response) {
                        is Resource.Success -> {
                            autoReceiveOTP()
                            binding.bottomSheetOtp?.tvMobile.text =
                                binding.etvMobile?.text.toString()
                            disablePreviousScreen()
                            startCountDownTimer()
                            showValidationScreen()
                        }
                        is Resource.GenericError -> {
                            // TODO Handle Errors
                            response?.error?.let {
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

                                    72 ->
                                        binding.tilMobile?.error =
                                            resources?.getString(R.string.error_guardian_exists)
                                }
                            }
                        }
                    }
                })
        }

    }

    fun onVerifyClick() {
        activity?.let{it.hideKeyboard()}
        buildRequestDataVerify()?.let {
            viewModel.verifyOTP(it)
                .observe(viewLifecycleOwner, androidx.lifecycle.Observer { response ->
                    when (response) {
                        is Resource.Success -> {
                            viewModel.cancelCountdown()
                            response?.data?.let {
                                navController?.navigate(
                                    RegisterFragmentDirections
                                        .actionFragmentRegisterToLocationAccess()
                                )
                            }
                        }
                        is Resource.GenericError -> {
                            // TODO Handle Errors
                            response?.error?.let {
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
                                    33 ->
                                        binding.bottomSheetOtp?.tilOtp?.error =
                                            resources?.getString(R.string.error_otp_invalid)

                                    72 ->
                                        binding.tilMobile?.error =
                                            resources?.getString(R.string.error_guardian_exists)

                                    73 ->
                                        binding.bottomSheetOtp?.tilOtp?.error =
                                            resources?.getString(R.string.error_otp_used)
                                }
                            }
                        }
                    }
                })
        }
    }

    private fun autoReceiveOTP(){
        if(otpReceiver == null) {
            otpReceiver = OtpReceiver(this) { otp, isTimeout ->
                otpReceiver = null
                if (!isTimeout && !otp.isNullOrBlank()) {
                    binding.bottomSheetOtp?.etvOtp?.setText(otp)
                    binding.bottomSheetOtp?.etvOtp?.placeCursorToEnd()
                    onVerifyClick()
                }
            }
        }
    }

    /**
     * Show OTP validation screen.
     */
    fun showValidationScreen() {
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        loginBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.scrollView.visibility = View.GONE
    }

    /**
     * Hide OTP validation screen.
     */
    fun hideValidationScreen() {
        bottomSheetBehavior?.setState(BottomSheetBehavior.STATE_COLLAPSED)
        loginBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        binding.scrollView.visibility = View.VISIBLE
    }

    /*
    * This function creates request body for login API/OTP Generation
    * returns Map
    */
    private fun buildRequestDataSignin(): Map<String, Any>? {
        var map: HashMap<String, Any>? = null
        try {
            map = hashMapOf()
            map.put("mobile", binding.etvMobile?.text.toString())
            Utils.getDeviceInfo(requireContext(),userType)?.let {
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
            map.put("mobile", binding.etvMobile?.text.toString())
            map.put("name", binding.etvFullName?.text.toString())
            map.put("otp", binding.bottomSheetOtp?.etvOtp?.text.toString())
            Utils.getDeviceInfo(requireContext(),userType)?.let {
                map.put("deviceInfo", it)
            }

            var persona = userType?.let{
                if(it.equals(CommonConst.PersonaType.Student.toString())){
                    CommonConst.PersonaType.Student.toString()
                }else{
                    CommonConst.PersonaType.Parent.toString()
                }
            } ?: CommonConst.PersonaType.Parent.toString()

            map.put(CommonConst.USER_TYPE, persona)

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
            binding.btSendOtp?.isEnabled = (!binding.etvMobile?.text.isBlank()
                    && binding.etvMobile?.text.toString().isValidPhoneNumber()
                    && binding.etvMobile?.text.toString().length == 10
                    && !binding.etvFullName?.text.isBlank() && binding.etvFullName?.text.toString()
                .isValidName())
                    && binding.cbAcceptTnc.isChecked
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
            Log.e(TAG, "onTextChanged: ")
            binding.bottomSheetOtp?.tilOtp?.error = null
            binding.bottomSheetOtp?.btVerifyOtp.isEnabled =
                (!binding.bottomSheetOtp?.etvOtp?.text.isBlank()
                        && (binding.bottomSheetOtp?.etvOtp?.text.toString().length == 4
                        || binding.bottomSheetOtp?.etvOtp?.text.toString().length == 6))
        }
    }

    /**
     * Function to show progress bar
     */
    fun showProgressCircularBar() {
        binding.progressCircular?.visibility = View.VISIBLE
    }

    /**
     * Function to hide progress bar
     */
    fun hideProgressCircularBar() {
        binding.progressCircular?.visibility = View.GONE
    }

    private fun openHomeActivity() {
        try {
            activity?.let {
                val intent = Intent(it, ParentExploreActivity::class.java)
                val bundle = Bundle()
                bundle.putString(
                    CommonConst.USER_ACTION,
                    CommonConst.UserActionType.Registered.toString()
                )
                bundle.putString(CommonConst.USER_TYPE, CommonConst.PersonaType.Parent.toString())
                intent.putExtras(bundle)
                startActivity(intent)
                (it as Activity).finish()
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
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
                it.context?.let { context ->
                    userType?.let {
                        if(it.equals(CommonConst.PersonaType.Student.toString())){
                            bannersAdapter.setItems(TutorialUtil.getStudentsTutorial(context))
                        }else{
                            bannersAdapter.setItems(TutorialUtil.getParentsTutorial(context))
                        }
                    } ?: bannersAdapter.setItems(TutorialUtil.getParentsTutorial(context))
                }
                lifecycleScope?.launch { AutoScrollList.autoScrollFeaturesList(it,bannersAdapter) }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun customTextView(view: TextView) {
        val spanTxt = SpannableStringBuilder(
            getString(R.string.tnc_1)
        )
        spanTxt.append(getString(R.string.tnc_2))
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val navigate = StudentListNavigationDirections.commonNavigation(
                    BuildConfig.TERMS_AND_CONDITIONS_URL, getString(
                        R.string.menu_terms_of_use
                    )
                )
                try {
                    navController.navigate(navigate)
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
        }, spanTxt.length - getString(R.string.tnc_2).length, spanTxt.length, 0)
        spanTxt.setSpan(ForegroundColorSpan(Color.BLACK), spanTxt.length - getString(R.string.tnc_2).length, spanTxt.length, 0)
        spanTxt.append(getString(R.string.tnc_3))
        spanTxt.append(getString(R.string.tnc_4))
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val navigate = StudentListNavigationDirections.commonNavigation(
                    BuildConfig.PRIVACY_POLICY_URL, getString(
                        R.string.menu_privacy_policy
                    )
                )
                try {
                    navController.navigate(navigate)
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
        }, spanTxt.length - getString(R.string.tnc_4).length, spanTxt.length, 0)
        spanTxt.setSpan(ForegroundColorSpan(Color.BLACK), spanTxt.length - getString(R.string.tnc_4).length, spanTxt.length, 0)
        view.movementMethod = LinkMovementMethod.getInstance()
        view.setText(spanTxt, TextView.BufferType.SPANNABLE)
    }


    private val checkBoxListener = object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(p0: CompoundButton?, checked: Boolean) {
            binding.btSendOtp?.isEnabled = (!binding.etvMobile?.text.isBlank()
                    && binding.etvMobile?.text.toString().isValidPhoneNumber()
                    && binding.etvMobile?.text.toString().length == 10 && binding.cbAcceptTnc?.isChecked == true)
        }
    }

}