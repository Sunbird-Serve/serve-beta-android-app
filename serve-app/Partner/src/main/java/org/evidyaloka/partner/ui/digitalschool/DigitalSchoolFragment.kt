package org.evidyaloka.partner.ui.digitalschool

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_digital_shcool.*
import org.evidyaloka.common.helper.PermissionRequester
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.common.util.Utils
import org.evidyaloka.common.helper.fileFormat
import org.evidyaloka.common.helper.getFileName
import org.evidyaloka.common.interfaces.IOnBackPressed
import org.evidyaloka.common.view.SuccessDialogFragment
import org.evidyaloka.core.helper.ErrorData
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.model.CourseProvider
import org.evidyaloka.core.partner.model.DigitalSchool
import org.evidyaloka.core.partner.model.FileDetails
import org.evidyaloka.partner.R
import org.evidyaloka.partner.ui.BaseFragment
import org.evidyaloka.partner.ui.stateList.SelectedStateAdapter
import java.io.IOException


const val DIGITAL_SCHOOL_DETAILS = "digital_school_Details"
const val IS_EDIT_DIGITAL_SCHOOL = "is_edit_digital_school"
const val IS_STATE_PINCODE_RESULT = "is_state_pincode_resilt"

/**
 * A simple [Fragment] subclass.
 * Use the [DigitalSchoolFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class DigitalSchoolFragment : BaseFragment(), IOnBackPressed {

    private val TAG = "DigitalSchoolFragment"


    // TODO: Rename and change types of parameters
    private var digitalShcoolDetails: DigitalSchool? = null
    private var isEditDigitalSchool: Boolean = false
    private var isStatePincodeResult: Boolean = false
    private var logoId: Int? = null
    private var logoUri: Uri? = null
    private var bannerId: Int? = null
    private var bannerUri: Uri? = null
    private var isLogoRemoved: Boolean = false
    private var isBannerRemoved: Boolean = false
    private var courseProviderList: List<CourseProvider> = listOf()
    private var courseProviderCode: CourseProvider? = null

    private val viewModel: DigitalSchoolViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isEditDigitalSchool = it.getBoolean(IS_EDIT_DIGITAL_SCHOOL)
            isStatePincodeResult = it.getBoolean(IS_STATE_PINCODE_RESULT)
            digitalShcoolDetails = it.getParcelable(DIGITAL_SCHOOL_DETAILS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_digital_shcool, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        slider.setStep1Text(getString(R.string.school_details))
        slider.setStep2Text(getString(R.string.state_and_pincode))
        slider.currentStep(1)
        if (isEditDigitalSchool) {
            bt_update_digitalschool?.visibility = View.VISIBLE
            bt_next?.visibility = View.GONE
            slider?.visibility = View.GONE
            bt_update_digitalschool?.setOnClickListener {
                updateSchool()
            }
        }
        digitalShcoolDetails?.selectedState?.let {
            if(it.size == 0){
                state_select_flow?.setHint(getString(R.string.select_state_and_pincode))
            }else{
                state_select_flow?.setHint(getString(R.string.select_another_state_and_pincode))
            }
        }
        if(isStatePincodeResult){
            view_ds_details?.visibility = View.GONE
            view_sate_pincode?.visibility = View.VISIBLE
            val stateAdapter = SelectedStateAdapter {
                navController.navigate(
                    DigitalSchoolFragmentDirections.actionDigitalSchoolFragmentToEditStatePincodeFragment(
                        digitalShcoolDetails,
                        it,
                        true
                    )
                )
            }
            digitalShcoolDetails?.selectedState?.let {
                stateAdapter.setItem(it)
            }

            rc_selected_state_pincode?.apply {
                visibility = View.VISIBLE
                adapter = stateAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
        }

        getCourseProviderData()


        bt_add_digitalschool?.setOnClickListener {
            submitSchool()
        }

        bt_next?.setOnClickListener {
//            logoUri?.let {
//                uploadLogo(it)
//            }?:submitDigitalSchool()
//            submitSchool()
            if(validateCourseDetails()) {
                view_ds_details?.visibility = View.GONE
                view_sate_pincode?.visibility = View.VISIBLE
                slider.currentStep(2)
            }
            hideKeyboard()
        }

        bt_skip?.setOnClickListener {
            digitalShcoolDetails?.selectedState = mutableListOf() //selected state will be cleared if any selected already
            submitSchool()
        }

        state_select_flow?.setOnClickListener {


           val mDigitalShcoolDetails = digitalShcoolDetails?: DigitalSchool(
                name = etv_ds_name?.text.toString(),
                description = etv_ds_description?.text.toString(),
                purpose = etv_ds_purpose?.text.toString(),
                courseProviderList = listOf(courseProviderCode) as List<CourseProvider>,
                logoUri = logoUri,
                bannerUri = bannerUri
            )

            navController.navigate(
                DigitalSchoolFragmentDirections.actionGlobalStateListFragment(
                    mDigitalShcoolDetails,
                    true
                )
            )
        }
        ib_logo_selector?.setOnClickListener {
            storagePermission.runWithPermission {
                showFileChooser(PICK_LOGO_IMAGE_REQUEST)
            }
        }
        ib_banner_selector?.setOnClickListener {
            storagePermission.runWithPermission {
                showFileChooser(PICK_BANNER_IMAGE_REQUEST)
            }
        }

        digitalShcoolDetails?.let {
            setValuesInEditUI()
        }

        iv_close_logo?.setOnClickListener {
            iv_close_logo?.visibility = View.GONE
            tv_logo_placeholder_text?.visibility = View.VISIBLE
            iv_logo?.setImageBitmap(null)
            logoUri = null
            isLogoRemoved = true
        }

        iv_close_banner?.setOnClickListener {
            iv_close_banner?.visibility = View.GONE
            tv_banner_placeholder_text?.visibility = View.GONE
            iv_banner?.setImageBitmap(null)
            bannerUri = null
            isBannerRemoved = true
        }

        try {
            viewModel.uploadLogo().observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.GenericError -> {
                        if (response.code == 123) {
                            showSnackBar(getString(R.string.logo_is_mandatory))
                        } else {
                            handleGenericError(response.code, response.error)
                        }
                    }
                }
            })
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }

    }

    override fun onBackPressed(): Boolean {
        return if (view_sate_pincode?.visibility == View.VISIBLE) {
            view_ds_details?.visibility = View.VISIBLE
            view_sate_pincode?.visibility = View.GONE
            true
        } else {
            false
        }
    }

    private val storagePermission = PermissionRequester(
        this,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        R.string.rationale_read_storage
    )

    private fun setValuesInEditUI() {
        try {
            etv_ds_name?.setText(digitalShcoolDetails?.name)
            etv_ds_description?.setText(digitalShcoolDetails?.description)
            etv_ds_purpose?.setText(digitalShcoolDetails?.purpose)
            digitalShcoolDetails?.courseProviderList?.let {
                for (courseProvider in it) {
                    etv_course_provider?.setText(courseProvider.name)
                    courseProviderCode = courseProvider
                }
            }

            if (digitalShcoolDetails?.status == PartnerConst.SchoolStatus.Active.name) {
                etv_ds_name?.isEnabled = false
                etv_ds_description?.isEnabled = false
                etv_ds_purpose?.isEnabled = false
                etv_course_provider?.isEnabled = false
            }

            if(isStatePincodeResult){
                logoUri = digitalShcoolDetails?.logoUri
                bannerUri = digitalShcoolDetails?.bannerUri
            }

            context?.let {
                Glide.with(it)
                    .load(digitalShcoolDetails?.bannerUrl)
                    .listener(object : RequestListener<Drawable> {

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            iv_close_banner?.visibility = View.GONE
                            tv_banner_placeholder_text?.visibility = View.VISIBLE

                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            iv_close_banner?.visibility = View.VISIBLE
                            tv_banner_placeholder_text?.visibility = View.GONE
                            return false
                        }
                    })
                    .into(iv_banner)

                Glide.with(it)
                    .load(digitalShcoolDetails?.logoUrl)
                    .circleCrop()
                    .listener(object : RequestListener<Drawable> {

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            iv_close_logo?.visibility = View.GONE
                            tv_logo_placeholder_text?.visibility = View.VISIBLE

                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            if (!isEditDigitalSchool)
                                iv_close_logo?.visibility = View.VISIBLE
                            tv_logo_placeholder_text?.visibility = View.GONE
                            return false
                        }
                    })
                    .into(iv_logo)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }

    }

    private fun submitSchool() {
        try {
            val files: ArrayList<FileDetails> = getFilesData()
            if (validateCourseDetails()) {
                buildRequestData()?.let {
                    viewModel.uploadFileAndSubmitSchool(files, it)
                        .observe(viewLifecycleOwner, Observer { response ->
                            when (response) {
                                is Resource.Success -> {
                                    hideProgressCircularBar()// Do not remove this function.
                                    showSuccessDialog(getString(R.string.digital_school_added_successfully))

                                }
                                is Resource.GenericError -> {
                                    hideProgressCircularBar()
                                    handleGenericError(response.code, response.error)
                                }
                                else -> {
                                    hideProgressCircularBar()
                                    handleNetworkError()
                                }
                            }
                        })
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    fun buildRequestData(): HashMap<String, Any>? {
        var map: HashMap<String, Any>? = null
        try {
            map = hashMapOf()
            // map.put(PARTNER_ID, etv_user_name.text.toString()) PARTNER_ID to be used from MainRepository
            map[PartnerConst.NAME] = etv_ds_name?.text.toString()
            map[PartnerConst.DESCRIPTION] = etv_ds_description?.text.toString()
            map[PartnerConst.PURPOSE] = etv_ds_purpose?.text.toString()
            courseProviderCode?.let {
                map[PartnerConst.COURSE_PROVIDER] = it.code.toString()
            }
            var stateList = mutableListOf<HashMap<String, Any>>()
            var stateMap: HashMap<String, Any> = hashMapOf()

            digitalShcoolDetails?.selectedState?.forEach {
                stateMap[PartnerConst.STATE_ID] = it.id
                var pincodeList = mutableListOf<HashMap<String, Any>>()
                it.pincodes?.forEach {
                    var pincodeMap: HashMap<String, Any> = hashMapOf()
                    pincodeMap[PartnerConst.PINCODE_ID] = it.id
                    pincodeMap[PartnerConst.PINCODE_CODE] = it.pincode.toString()
                    pincodeList.add(pincodeMap)
                }
                stateMap[PartnerConst.PINCODES] = pincodeList
                stateList.add(stateMap)
            }
            map[PartnerConst.SELECTED_STATES] = stateList

        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e(TAG, e.message ?: "gson converter exception")
        }
        return map
    }

    /*
    *This function validates the user details
    */
    private fun validateCourseDetails(): Boolean {
        var valid = true

        til_ds_name?.error =
            getString(R.string.invalid_name).takeIf {
                etv_ds_name?.text.toString().isEmpty()
            }?.also {
                valid = false
            }

        til_ds_description?.error =
            getString(R.string.invalid_description).takeIf {
                etv_ds_description?.text.toString().isEmpty()
            }
                ?.also {
                    valid = false
                }

        // Statement of purpose made optional. ESCHOOL-1399
//        til_ds_purpose?.error =
//            getString(R.string.invalid_statement_purpose).takeIf {
//                etv_ds_purpose?.text.toString().isEmpty()
//            }
//                ?.also {
//                    valid = false
//                }

        til_course_provider?.error =
            getString(R.string.invalid_academic).takeIf {
                courseProviderCode == null
            }
                ?.also {
                    valid = false
                }
        if(!isEditDigitalSchool && logoUri == null){
            showSnackBar(getString(R.string.logo_mandatory))
            valid = false
            scrollView?.scrollTo(0,ib_logo_selector.top)
        }

        return valid
    }

    /**
     * Function is responsible for handling server side error
     * Error message will be set based on error response
     */
    private fun handleGenericError(errorCode: Int?, error: ErrorData?) {
        if (error != null) {
            when (error.code) {
                12 -> {
                    til_ds_name.error = getString(R.string.school_name_already_exist)
                }
                2 -> {
                    showSnackBar(getString(R.string.error_required_field_missing))
                }
                23 -> {
                    showSnackBar(getString(R.string.only_digital_school_patner_allowed))
                }
                3 -> {
                    showSnackBar(getString(R.string.invalid_data_contact_admin))

                }
                else -> showSnackBar(error.message)
            }
        }
    }


    private fun updateSchool() {
        try {
            val files: ArrayList<FileDetails> = getFilesData()
            if (validateCourseDetails()) {
                buildRequestData()?.let {
                    viewModel.updateFileAndSchool(digitalShcoolDetails!!.id, files, it)
                        .observe(viewLifecycleOwner, Observer { response ->
                            when (response) {
                                is Resource.Success -> {
                                    hideProgressCircularBar() // Do not remove this function.
                                    showSuccessDialog(getString(R.string.digital_school_details_updated))
                                }
                                is Resource.GenericError -> {
                                    hideProgressCircularBar()
                                    handleGenericError(response.code, response.error)
                                }
                                else -> {
                                    hideProgressCircularBar()
                                    handleNetworkError()
                                }
                            }
                        })
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            showSnackBar(getString(R.string.invalid_school_details))
        }
    }

    private fun getFilesData(): ArrayList<FileDetails> {
        var files: ArrayList<FileDetails> = arrayListOf()
        logoUri?.let { uri ->
            context?.let { context ->
                Utils.getFileInputStream(context, uri)?.let { streamData ->
                    val fileName = uri.getFileName(context)
                    val fileFormat = fileName?.fileFormat()
                    files.add(
                        FileDetails(
                            PartnerConst.DocType.SCHOOLLOGO.value,
                            PartnerConst.DocFormat.formatOf(fileFormat!!).value,
                            fileName,
                            streamData
                        )
                    )
                }
            }
        }
        bannerUri?.let { uri ->
            context?.let { context ->
                Utils.getFileInputStream(context, uri)?.let { streamData ->
                    val fileName = uri.getFileName(context)
                    val fileFormat = fileName?.fileFormat()
                    files.add(
                        FileDetails(
                            PartnerConst.DocType.SCHOOLBANNER.value,
                            PartnerConst.DocFormat.formatOf(fileFormat!!).value,
                            fileName,
                            streamData
                        )
                    )
                }
            }
        }
        return files
    }

    private fun getCourseProviderData() {
        try {
            viewModel.getCourseProvider().observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        response?.data.let {
                            it?.let { it1 ->
                                setCourseProviderAdapter(it1)
                            }
                        }
                    }
                    is Resource.GenericError -> {

                    }
                }
            })
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    fun setCourseProviderAdapter(list: List<CourseProvider>) {
        courseProviderList = list
        context?.let {
            var adapter = ArrayAdapter(it, R.layout.spinner_list_item, list.map { it.name })
            etv_course_provider?.setAdapter(adapter)
            adapter.notifyDataSetChanged()
            etv_course_provider?.setOnItemClickListener { adapterView, mView, position, l ->
                courseProviderCode = courseProviderList.get(position)
            }
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DigitalSchoolFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(digitalSchool: DigitalSchool) =
            DigitalSchoolFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(DIGITAL_SCHOOL_DETAILS, digitalSchool)
                }
            }
    }

    //method to show file chooser
    private fun showFileChooser(requestCode: Int) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.select_picture)),
            requestCode
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_LOGO_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            try {
                data.data?.let { uri ->
                    activity?.let {
                        val bitmap = MediaStore.Images.Media.getBitmap(it.contentResolver, uri)
                        Glide.with(it)
                            .load(bitmap)
                            .circleCrop()
                            .listener(object : RequestListener<Drawable> {

                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    iv_close_logo?.visibility = View.GONE
                                    tv_logo_placeholder_text?.visibility = View.VISIBLE
                                    showSnackBar(resources.getString(R.string.invalid_image))
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    iv_close_logo?.visibility = View.VISIBLE
                                    tv_logo_placeholder_text?.visibility = View.GONE
                                    return false
                                }
                            })
                            .into(iv_logo)

                        logoUri = uri
                        isLogoRemoved = false
                    }
                }

            } catch (e: IOException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                showSnackBar(resources.getString(R.string.invalid_image))
            }
        }
        if (requestCode == PICK_BANNER_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            try {
                data.data?.let { uri ->
                    activity?.let {
                        val bitmap = MediaStore.Images.Media.getBitmap(it.contentResolver, uri)

                        iv_banner?.let { iv_banner ->
                            Glide.with(it)
                                .load(bitmap)
                                .listener(object : RequestListener<Drawable> {

                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        iv_close_banner?.visibility = View.GONE
                                        tv_banner_placeholder_text?.visibility = View.VISIBLE
                                        showSnackBar(resources.getString(R.string.invalid_image))
                                        return false
                                    }

                                    override fun onResourceReady(
                                        resource: Drawable?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        dataSource: DataSource?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        iv_close_banner?.visibility = View.VISIBLE
                                        tv_banner_placeholder_text?.visibility = View.GONE
                                        return false
                                    }
                                })
                                .into(iv_banner)
                        }

                        bannerUri = uri
                        isBannerRemoved = false
                    }
                }

            } catch (e: IOException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                showSnackBar(resources.getString(R.string.invalid_image))
            }
        }
    }

    fun showSuccessDialog(message: String) {
        context?.let {
            val onDismissListener = object : SuccessDialogFragment.OnDismissListener {
                override fun OnDismiss(dialog: DialogInterface) {
                    try{
                    navController.navigateUp()
                    }catch (e : Exception){
                        FirebaseCrashlytics.getInstance().recordException(e)

                    }
                }
            }
            SuccessDialogFragment.Builder(it)
                .setIsDialogCancelable(true)
                .setTitle(getString(R.string.successful))
                .setDescription(message)
                .setDismissTimer(PartnerConst.DIALOG_CLOSE_TIME)
                .setIcon(R.drawable.ic_check)
                .setButtonPositiveText(getString(R.string.ok))
                .setButtonType(SuccessDialogFragment.BUTTON_TYPE.ROUND_COLORED)
                .setViewType(SuccessDialogFragment.DIALOG_TYPE.ALERT)
                .setOnDismissListener(onDismissListener)
                .build()
                .show(childFragmentManager, "")

        }
    }

}