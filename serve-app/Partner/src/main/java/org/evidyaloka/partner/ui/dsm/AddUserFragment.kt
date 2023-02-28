package org.evidyaloka.partner.ui.dsm

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
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.gson.GsonBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_user.*
import org.evidyaloka.common.helper.*
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.core.helper.ErrorData
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.DigitalSchool
import org.evidyaloka.partner.R
import org.evidyaloka.partner.ui.BaseFragment
import org.evidyaloka.partner.ui.HomeActivity
import java.io.IOException

@AndroidEntryPoint
class AddUserFragment : BaseFragment() {

    private val TAG: String = "AddUserFragment"
    private val viewModel: UserViewModel by viewModels()
    private var selectedSchoolId: Int = 0

    private var profilePicId: Int? = null
    private var profilePicUri: Uri? = null
    private var isLogoRemoved: Boolean = false
    private val storagePermission = PermissionRequester(
        this,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        R.string.rationale_read_storage
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        try {
            viewModel.getUserSchoolList().observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let {
                            initSchoolsSpinner(it?.listDigitalSchools)
                        }
                    }
                    is Resource.GenericError -> {
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

        initRoleSpinner()

        bt_submit_another?.setOnClickListener {
            if (profilePicUri != null) {
                uploadProfilePic(profilePicUri!!, true)
            } else {
                handleAddUser(true)
            }
        }

        bt_submit?.setOnClickListener {
            if (profilePicUri != null) {
                uploadProfilePic(profilePicUri!!, false)
            } else {
                handleAddUser(false)
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

    fun handleAddUser(addMoreUser: Boolean) {
        try {
            hideKeyboard()
            if (validateUserDetails()) {
                buildRequestData()?.let {
                    viewModel.addUser(it).observe(viewLifecycleOwner, Observer { response ->
                        when (response) {
                            is Resource.Success -> {
                                showSuccess()
                                if (addMoreUser) {
                                    clearAllDetails()
                                } else {
                                    (activity as HomeActivity).onBackPressed()
                                }
                            }
                            is Resource.GenericError -> {
                                handleGenericError(response.error)
                            }
                        }
                    })

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun handleGenericError(error: ErrorData?) {
        error?.let {

            when (it.code) {
                1 -> {
                    til_first_name?.error = getString(R.string.err_name_already_registered)
                }
                2 -> {
                    til_phone?.error = getString(R.string.err_mobile_number_already_registered)
                }
                3 -> {
                    til_email?.error = getString(R.string.err_email_already_registered)
                }
                4 -> {
                    til_email?.error = getString(R.string.error_user_exists)
                }
                20 -> {
                    til_role?.error = getString(R.string.invalid_role)
                }
                28 -> {
                    til_assign_school?.error = getString(R.string.error_school_not_exist)
                }
            }
        }
    }

    /*
     *
     */
    private fun initSchoolsSpinner(schools: List<DigitalSchool>) {
        try {

            schools?.let { it1 ->
                var schoolNames = it1.map { it.name }

                context?.let {
                    var schoolAdapter = ArrayAdapter(it, R.layout.spinner_list_item, schoolNames)
                    etv_assign_school?.setAdapter(schoolAdapter)
                    schoolAdapter?.notifyDataSetChanged()
                    etv_assign_school?.setOnItemClickListener { adapterView, view, i, l ->
                        selectedSchoolId = schools.get(i).id
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initRoleSpinner() {
        try {
            context?.let {
                var roleAdapter = ArrayAdapter(it, R.layout.spinner_list_item, listOf(
                    PartnerConst.SpinnerRoleType.values()[0]))
                etv_role?.setAdapter(roleAdapter)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*
     *This function validates the user details
     */
    private fun validateUserDetails(): Boolean {
        var valid = true

        til_first_name?.error =
                getString(R.string.invalid_first_name).takeIf {
                    !etv_first_name?.text.toString().isValidName()
                }?.also {
                    valid = false
                }

        til_last_name?.error =
                getString(R.string.invalid_last_name).takeIf {
                    !etv_last_name?.text.toString().isValidName()
                }
                        ?.also {
                            valid = false
                        }

        til_email?.error =
                getString(R.string.invalid_mail).takeIf { !etv_email?.text.toString().isValidEmail() }
                        ?.also {
                            valid = false
                        }

        til_phone?.error =
                getString(R.string.invalid_phone).takeIf {
                    !etv_phone?.text.toString().isValidPhoneNumber()
                }
                        ?.also {
                            valid = false
                        }

        til_taluk?.error =
                getString(R.string.invalid_taluk).takeIf { etv_taluk?.text.toString().isNullOrEmpty() }
                        ?.also {
                            valid = false
                        }

        til_district?.error =
                getString(R.string.invalid_district_alone).takeIf {
                    etv_district?.text.toString().isNullOrEmpty()
                }
                        ?.also {
                            valid = false
                        }

        til_state?.error =
                getString(R.string.invalid_state).takeIf { etv_state?.text.toString().isNullOrEmpty() }
                        ?.also {
                            valid = false
                        }

        til_pincode?.error =
                getString(R.string.invalid_pincode).takeIf {
                    !etv_pincode?.text.toString().isValidPincode()
                }
                        ?.also {
                            valid = false
                        }

        til_role?.error =
                getString(R.string.invalid_role).takeIf { etv_role?.text.toString().isNullOrEmpty() }
                        ?.also {
                            valid = false
                        }


        //Assign to school can be empty
        /*
        til_assign_school.error =
                getString(R.string.invalid_school).takeIf {
                    etv_assign_school.text.toString().isNullOrEmpty() ||
                            selectedSchoolId.equals(0)
                }
                        ?.also {
                            valid = false
                        }
         */

        return valid
    }

    fun buildRequestData(): Map<String, Any>? {
        var map: HashMap<String, Any>? = null
        try {
            map = hashMapOf()
            // map.put(PARTNER_ID, etv_user_name.text.toString()) PARTNER_ID to be used from MainRepository
            map.put(PartnerConst.FIRST_NAME, etv_first_name?.text.toString())
            map.put(PartnerConst.LAST_NAME, etv_last_name?.text.toString())
            map.put(PartnerConst.EMAIL, etv_email?.text.toString())
            map.put(PartnerConst.PHONE, etv_phone?.text.toString())
            map.put(PartnerConst.TALUK, etv_taluk?.text.toString())
            map.put(PartnerConst.STATE, etv_state?.text.toString())
            map.put(PartnerConst.PINCODE, etv_pincode?.text.toString())
            map.put(PartnerConst.DISTRICT, etv_district?.text.toString())

            profilePicId?.let {
                map.put(PartnerConst.PROFILE_PIC_ID, it)
            }

            map.put(PartnerConst.ROLE, etv_role?.text.toString())

            selectedSchoolId?.let {
                if (it != 0) {
                    map.put(PartnerConst.DIGITAL_SCHOOL_ID, it)
                }
            }

            var jsonData = GsonBuilder().create().toJson(map)

            Log.e(TAG, "buildRequestData: " + jsonData)
        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "gson converter exception")
        }
        return map
    }

    fun showSuccess() {
        try {
            if (activity != null) {
                (activity as HomeActivity)
                        .showSnackBar(getString(R.string.user_added_success))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clearAllDetails() {
        try {
            etv_first_name?.setText("")
            etv_last_name?.setText("")
            etv_email?.setText("")
            etv_phone?.setText("")
            etv_taluk?.setText("")
            etv_state?.setText("")
            etv_pincode?.setText("")
            etv_district?.setText("")

            //etv_address_1.setText("")
            etv_role?.setText("")
            etv_assign_school?.setText("")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createdUploadEntity(id: Int, type: String): HashMap<String, Any?> {
        val entityData = HashMap<String, Any?>()
        entityData["id"] = id
        entityData["type"] = type
        return entityData
    }

    private fun uploadProfilePic(uri: Uri, addMoreUser: Boolean) {
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
                                handleAddUser(addMoreUser)
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

    //method to show file chooser
    private fun showFileChooser() {
        try {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                    Intent.createChooser(intent, "Select Picture"),
                    PICK_PROFILE_IMAGE_REQUEST
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == PICK_PROFILE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
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

                            profilePicUri = uri
                            isLogoRemoved = false
                        }
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    showSnackBar(resources.getString(R.string.invalid_image))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}