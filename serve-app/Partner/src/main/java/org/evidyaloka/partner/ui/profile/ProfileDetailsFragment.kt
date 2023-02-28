package org.evidyaloka.partner.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_organization_details.*
import kotlinx.android.synthetic.main.fragment_personal_details.*
import org.evidyaloka.common.helper.*
import org.evidyaloka.partner.R
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.User
import org.evidyaloka.partner.ui.BaseFragment
import java.io.IOException


@AndroidEntryPoint
class ProfileDetailsFragment : BaseFragment() {

    private val TAG: String = "PersonalDetailsFragment"

    private var pageType = ProfileDetailType.PROFILE
    private var roleType = PartnerConst.RoleType.DSP

    private var profilePicId: Int? = null

    private var profilePicUri: Uri? = null
    private var isLogoRemoved: Boolean = false

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var tempUser: User
    private val storagePermission = PermissionRequester(
        this,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        R.string.rationale_read_storage
    )
    companion object {
        private val PROFILE_PAGE_TYPE = "profile_page_type"

        enum class ProfileDetailType {
            PROFILE,
            ORGANIZATION
        }

        fun newInstance(
                typeProfile: ProfileDetailType,
                roleType: PartnerConst.RoleType
        ): ProfileDetailsFragment {
            val fragmentInstance = ProfileDetailsFragment()
            val args = Bundle()
            args.putSerializable(PROFILE_PAGE_TYPE, typeProfile)
            args.putSerializable(PartnerConst.ROLE, roleType)
            fragmentInstance.arguments = args
            return fragmentInstance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageType = arguments?.get(PROFILE_PAGE_TYPE) as ProfileDetailType
        roleType = arguments?.get(PartnerConst.ROLE) as PartnerConst.RoleType
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return when (pageType) {
            ProfileDetailType.PROFILE -> inflater.inflate(
                R.layout.fragment_personal_details,
                container,
                false
            )
            ProfileDetailType.ORGANIZATION -> inflater.inflate(
                R.layout.fragment_organization_details,
                container,
                false
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        getUserDetails()
        when (pageType) {
            ProfileDetailType.PROFILE -> initializeProfileView()
            ProfileDetailType.ORGANIZATION -> initializeOrganizationView()
        }
    }

    /**
     * initialize listener for person details
     */
    private fun initializeProfileView() {
        if (roleType.equals(PartnerConst.RoleType.DSM)) {
            disableProfileView()
        } else {
            bt_save?.setOnClickListener {
                if (profilePicUri != null) {
                    uploadProfilePic(profilePicUri!!)
                } else {
                    if (validatePersonDetails()) {
                        hideKeyboard()
                        updateProfileCall()
                    }
                }
            }
        }


        ib_logo_selector?.setOnClickListener {
            storagePermission.runWithPermission {
                showFileChooser()
            }
        }

        iv_close_logo?.setOnClickListener {
            iv_close_logo?.visibility = View.GONE
            tv_logo_placeholder_text?.visibility = View.VISIBLE
            iv_logo?.setImageBitmap(null)
            profilePicUri = null
            isLogoRemoved = true
        }
    }

    private fun disableProfileView() {
        bt_save?.isEnabled = false
        bt_save?.visibility = View.GONE

        etv_first_name?.isEnabled = false
        etv_first_name?.setBackgroundResource(R.drawable.edittext_rounded_onsecondary_disabled)

        etv_last_name?.isEnabled = false
        etv_last_name?.setBackgroundResource(R.drawable.edittext_rounded_onsecondary_disabled)

        etv_person_mail?.isEnabled = false
        etv_person_mail?.setBackgroundResource(R.drawable.edittext_rounded_onsecondary_disabled)

        etv_person_phone?.isEnabled = false
        etv_person_phone?.setBackgroundResource(R.drawable.edittext_rounded_onsecondary_disabled)

        ib_logo_selector?.isEnabled = false
        iv_close_logo?.isEnabled = false
        iv_close_logo?.visibility = View.GONE

    }

    //method to show file chooser
    private fun showFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            PICK_PROFILE_IMAGE_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PROFILE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            try {
                data.data?.let { uri ->
                    activity?.let {
                        val bitmap = MediaStore.Images.Media.getBitmap(it.contentResolver, uri)
                        iv_logo?.let { iv_logo ->
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
                        }

                        profilePicUri = uri
                        isLogoRemoved = false
                    }
                }

            } catch (e: IOException) {
                e.printStackTrace()
                showSnackBar(resources.getString(R.string.invalid_image))
            }
        }
    }

    private fun uploadProfilePic(uri: Uri) {
        try {
            viewModel.uploadProgress().observe(viewLifecycleOwner, Observer {
                when (it > 0) {
                    true -> {
                        UpdateHorizontalDeterminateBar(it)
                    }
                    else -> {
                        hideHorizontalDeterminateBar()
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            activity?.contentResolver?.openInputStream(uri)?.let { streamData ->
                activity?.let {
                    showHorizontalDeterminateBar()
                    viewModel.uploadFile(
                            PartnerConst.DocType.USERPROFILEPIC.value,
                            PartnerConst.DocFormat.JPG.value,
                            uri.getFileName(it)!!,
                            streamData
                    ).observe(viewLifecycleOwner, Observer { response ->
                        when (response) {
                            is Resource.Success -> {
                                hideHorizontalDeterminateBar()
                                profilePicId = response.data?.id
                                updateProfileCall()
                            }
                            else -> {
                                hideHorizontalDeterminateBar()
                                handleNetworkError()
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
     * initialize listener for organization details
     */
    private fun initializeOrganizationView() {
        if (roleType.equals(PartnerConst.RoleType.DSM)) {
            disableOrganizationView()
        } else {
            bt_update_organization?.setOnClickListener {
                if (validateOrganizationDetails()) {
                    hideKeyboard()
                    updateProfileCall()
                }
            }
        }
    }

    private fun disableOrganizationView() {
        bt_update_organization?.isEnabled = false
        bt_update_organization?.visibility = View.GONE

        etv_name?.isEnabled = false
        etv_name?.setBackgroundResource(R.drawable.edittext_rounded_onsecondary_disabled)

        etv_org_name?.isEnabled = false
        etv_org_name?.setBackgroundResource(R.drawable.edittext_rounded_onsecondary_disabled)

        etv_org_email?.isEnabled = false
        etv_org_email?.setBackgroundResource(R.drawable.edittext_rounded_onsecondary_disabled)

        etv_org_phone?.isEnabled = false
        etv_org_phone?.setBackgroundResource(R.drawable.edittext_rounded_onsecondary_disabled)

        etv_org_address_1?.isEnabled = false
        etv_org_address_1?.setBackgroundResource(R.drawable.edittext_rounded_onsecondary_disabled)
    }

    /**
     * Update profile details changes
     */
    private fun updateProfileCall() {
        buildRequestData()?.let {
            try {
                viewModel.updateProfile(it).observe(viewLifecycleOwner, Observer { response ->
                    when (response) {
                        is Resource.Success -> {
                            updatePreferenceUserData()
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
    }

    /**
     * save user details in preference.
     */
    private fun updatePreferenceUserData() {
        // get User data from Pref and update the new values from edittextviews
        if (pageType == ProfileDetailType.PROFILE) {
            tempUser?.let {
                it.apply {
                    this.fname = etv_first_name?.text.toString()
                    this.lname = etv_last_name?.text.toString()
                    this.email = etv_person_mail?.text.toString()
                    this.phone = etv_person_phone?.text.toString()
                }
            }
        } else {
            val organization = org.evidyaloka.core.partner.model.Organization(
                    partnerName = etv_name?.text.toString(),
                    name = etv_org_name?.text.toString(),
                    email = etv_org_email?.text.toString(),
                    phone = etv_org_phone?.text.toString(),
                    address = etv_org_address_1?.text.toString()
            )
            tempUser?.let {
                it.apply {
                    this.organization = organization
                }
            }
        }

        tempUser?.let {
            try {
                viewModel.saveUser(it).observe(viewLifecycleOwner, Observer { response ->
                    when (response) {
                        is Resource.Success -> {
                            showSnackBar(getString(R.string.update_successfully))
                        }
                        else -> {
                        }
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Functionality to fetch user object from preference
     * User details will be fetch in homescreen.
     *
     */

    private fun getUserDetails() {
        try {
            viewModel.getUser().observe(viewLifecycleOwner, Observer { user ->
                user?.let {
                    tempUser = it
                    updateUI(it)
                }
            })
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
                getString(R.string.invalid_name).takeIf {
                    !etv_first_name?.text.toString().isValidName()
                }
                        ?.also {
                            valid = false
                        }
        til_last_name?.error =
                getString(R.string.invalid_name).takeIf {
                    !etv_last_name?.text.toString().isValidName()
                }
                        ?.also {
                            valid = false
                        }
        til_person_mail?.error =
                getString(R.string.invalid_mail).takeIf {
                    !etv_person_mail?.text.toString().isValidEmail()
                }
                        ?.also {
                            valid = false
                        }
        til_person_phone?.error = getString(R.string.invalid_phone).takeIf {
            !etv_person_phone?.text.toString().isValidPhoneNumber()
        }?.also {
            valid = false
        }
        return valid
    }

    /**
     * This function validates the organization details fields
     * @return Boolean
     */
    private fun validateOrganizationDetails(): Boolean {
        var valid: Boolean = true
        til_name?.error =
                getString(R.string.invalid_name).takeIf { !etv_name?.text.toString().isValidName() }
                        ?.also {
                            valid = false
                        }

        til_org_name?.error =
                getString(R.string.invalid_org_name).takeIf {
                    !etv_org_name?.text.toString().isValidName()
                }?.also {
                    valid = false
                }

        til_org_email?.error = getString(R.string.invalid_mail).takeIf {
            !etv_org_email?.text.toString().isValidEmail()
        }?.also {
            valid = false
        }
        til_org_phone?.error = getString(R.string.invalid_phone).takeIf {
            !etv_org_phone?.text.toString().isValidPhoneNumber()
        }?.also {
            valid = false
        }
        til_org_address_1?.error =
                getString(R.string.invalid_address).takeIf { etv_org_address_1?.text.isNullOrEmpty() }
                        ?.also {
                            valid = false
                        }
        return valid
    }

/*
This function update the UI with Users personal details
 */

    private fun updateUI(user: User) {
        try {
            user?.let {
                if (pageType == ProfileDetailType.PROFILE) {
                    etv_first_name?.setText(it.fname)
                    etv_last_name?.setText(it.lname)
                    etv_person_mail?.setText(it.email)
                    etv_person_phone?.setText(it.phone)

                    var imageUrl = ""

                    /*if(!user.profileImageUrl.isNullOrEmpty()){
                        imageUrl = user.profileImageUrl
                    }*/

                    if(!user.profileImageShortUrl.isNullOrEmpty()){
                        imageUrl = user.profileImageShortUrl
                    }else{
                        if(!user.profileImageFullUrl.isNullOrEmpty()){
                            imageUrl = user.profileImageFullUrl
                        }
                    }

                    iv_logo?.let { iv_logo ->
                        activity?.let { activity ->
                            Glide.with(activity)
                                .load(imageUrl)
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
                                        //showSnackBar(resources.getString(R.string.invalid_image))
                                        if (roleType.equals(PartnerConst.RoleType.DSM) ||
                                                PartnerConst.PartnerStatus.Approved.name.equals(it.partnerStatus)
                                        ) {
                                            iv_close_logo?.visibility = View.GONE
                                            tv_logo_placeholder_text?.visibility = View.GONE
                                            iv_logo?.setImageResource(R.drawable.ic_user_placeholder)
                                        }
                                        return false
                                    }

                                    override fun onResourceReady(
                                            resource: Drawable?,
                                            model: Any?,
                                            target: Target<Drawable>?,
                                            dataSource: DataSource?,
                                            isFirstResource: Boolean
                                    ): Boolean {
                                        //iv_close_logo.visibility = View.VISIBLE
                                        tv_logo_placeholder_text?.visibility = View.GONE
                                        if (roleType.equals(PartnerConst.RoleType.DSM) || PartnerConst.PartnerStatus.Approved.name.equals(
                                                        it.partnerStatus
                                                )
                                        ) {
                                            disableProfileView()
                                        }
                                        return false
                                    }
                                })
                                .into(iv_logo)
                        }
                    }

                    if (roleType.equals(PartnerConst.RoleType.DSM) || PartnerConst.PartnerStatus.Approved.name.equals(
                                    it.partnerStatus
                            )
                    ) {
                        disableProfileView()
                    }

                } else {
                    val org = it.organization
                    org?.let {
                        etv_name?.setText(org.partnerName)
                        etv_org_name?.setText(org.name)
                        etv_org_email?.setText(org.email)
                        etv_org_phone?.setText(org.phone)
                        etv_org_address_1?.setText(org.address)
                    }
                    if (roleType.equals(PartnerConst.RoleType.DSM) || PartnerConst.PartnerStatus.Approved.name.equals(
                                    it.partnerStatus
                            )
                    ) {
                        disableOrganizationView()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
    }

    /**
     * Generates the request body for Partner registration
     * @return String
     */
    private fun buildRequestData(): Map<String, Map<String, Any>>? {
        val map = HashMap<String, Any>()
        val mapMain = HashMap<String, HashMap<String, Any>>()
        try {
            if (pageType == ProfileDetailType.PROFILE) {

                map.put(PartnerConst.PHONE, etv_person_phone.text.toString())
                map.put(PartnerConst.EMAIL, etv_person_mail.text.toString())
                map.put(PartnerConst.LAST_NAME, etv_last_name.text.toString())
                map.put(PartnerConst.FIRST_NAME, etv_first_name.text.toString())

                profilePicId?.let {
                    map.put(PartnerConst.PROFILE_PIC_ID, it)
                }

                mapMain.put("user", map)

            } else {
                map.put(PartnerConst.PARTNER_NAME, etv_name.text.toString())
                map.put(PartnerConst.NAME, etv_org_name.text.toString())
                map.put(PartnerConst.EMAIL, etv_org_email.text.toString())
                map.put(PartnerConst.PHONE, etv_org_phone.text.toString())
                map.put(PartnerConst.ADDRESS_LINE_1, etv_org_address_1.text.toString())

                mapMain.put("organization", map)
            }

        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "gson converter exception")
        }
        return mapMain
    }

    private fun handleGenericError(code: Int) {
        when (code) {
            1 -> {
                showSnackBar(getString(R.string.organization_name_exists))
            }
            2 -> {
                showSnackBar(getString(R.string.organization_email_exists))
            }
            3 -> {
                showSnackBar(getString(R.string.organization_mobile_exists))
            }
            4 -> {
                showSnackBar(getString(R.string.person_phone_exists))
            }
            else -> handleNetworkError()
        }
    }

}