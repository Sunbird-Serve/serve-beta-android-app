package org.evidyaloka.partner.ui.dsm

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_dsm_user_details.*
import kotlinx.android.synthetic.main.layout_dsm_personal_details.*
import org.evidyaloka.common.helper.PermissionRequester
import org.evidyaloka.common.helper.getFileName
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.DigitalSchool
import org.evidyaloka.core.partner.model.User
import org.evidyaloka.core.partner.network.entity.dsm.SchoolsAssigned
import org.evidyaloka.partner.R
import org.evidyaloka.partner.ui.BaseFragment
import java.io.IOException
import kotlin.collections.set


@AndroidEntryPoint
class UserDetailsFragment : BaseFragment() {

    private val TAG: String = "DsmUserDetailsFragment"

    private val viewModel: UserViewModel by viewModels()
    private lateinit var mView: View

    private var openType = UsersFragment.Companion.Type.DSM_PROFILE.name
    private var selectedUserId: Int? = null
    private var selectedSchoolId: Int? = null

    private var profilePicId: Int? = null
    private var logoUri: Uri? = null
    private var schoolsAssignedList: List<SchoolsAssigned> = listOf()
    private val storagePermission = PermissionRequester(
        this,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        R.string.rationale_read_storage
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openType = arguments?.get(UsersFragment.OPEN_TYPE).toString()
        selectedUserId = arguments?.getInt(PartnerConst.USER_ID)

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        if (!this::mView.isInitialized) {
            mView = inflater.inflate(R.layout.fragment_dsm_user_details, container, false)
        }
        return mView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        loadUserDetails()
        loadPartnersSchools()
        initRoleSpinner()

        iv_edit_user_details?.setOnClickListener {
            //TODO open Edit Fragment
            selectedUserId?.let {
                val arg = Bundle()
                arg.putInt(PartnerConst.USER_ID, it)
                profilePicId?.let { it1 -> arg.putInt(PartnerConst.PROFILE_PIC_ID, it1) }
                try{
                navController.navigate(R.id.action_userDetailsFragment_to_editUserDetailsFragment, arg)
                }catch (e : Exception){
                    FirebaseCrashlytics.getInstance().recordException(e)

                }
            }
        }

        iv_add_user_pic?.setOnClickListener {
            storagePermission.runWithPermission {
                showFileChooser()
            }
        }
        tv_schools_assigned?.setOnClickListener {
            openAssignedSchoolsDialog(schoolsAssignedList)
        }
    }

    private fun loadUserDetails() {
        try {
            selectedUserId?.let {
                Log.e("id", "" + it)
                viewModel.getUserDetails(it).observe(viewLifecycleOwner, Observer { response ->
                    when (response) {
                        is Resource.Success -> {
                            response.data?.let {

                                if (it.isNotEmpty()) {
                                    it[0]?.let {
                                        updateUI(it)

                                        if (it.schoolCount > 0) {
                                            setAssignedSchoolsText(it.schoolCount)
                                        }

                                        if (openType.equals(UsersFragment.Companion.Type.ASSIGNED_SCHOOL.name)) {
                                            openAssignedSchoolsDialog(it.schoolsAssigned)
                                        }

                                        schoolsAssignedList = it.schoolsAssigned
                                        if (schoolsAssignedList.size > 0) {

                                            setAssignedSchoolsText(schoolsAssignedList.size)
                                        }

                                        activateAssignSchoolToDsm(it.id)
                                    }
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

    /*
     * sets the number of schools assigned to a DSM
     */
    private fun setAssignedSchoolsText(schoolCount: Int) {
        try {
            rlSchoolsAssiged?.visibility = View.VISIBLE
            if (schoolCount == 1) {
                tv_schools_assigned?.setText(getString(R.string.one_school_assigned))
            } else {
                tv_schools_assigned?.setText(String.format(getString(R.string.no_of_school_assigned), schoolCount))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*
     *
     */
    private fun setDsmSchools(schools: List<SchoolsAssigned>) {
        try {
            if (schools.size > 0) {

                setAssignedSchoolsText(schools.size)

                if (openType.equals(UsersFragment.Companion.Type.ASSIGNED_SCHOOL.name)) {
                    openAssignedSchoolsDialog(schools)
                }

                rlSchoolsAssiged.setOnClickListener {
                    openAssignedSchoolsDialog(schools)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openAssignedSchoolsDialog(schoolList: List<SchoolsAssigned>) {
        try {
            schoolList?.let {
                if (it.isNotEmpty()) {
                    val dialogFragment = CustomDialogFragment()
                    val args = Bundle()

                    args.putString("DIALOG_TYPE", PartnerConst.DIALOG_TYPE.NORMAL.name)
                    args.putString("VIEW_TYPE", PartnerConst.VIEW_TYPE.DSM_ASSIGNED_SCHOOL.name)
                    args.putSerializable("SCHOOLS_ASSIGNED", ArrayList(it))
                    dialogFragment?.arguments = args
                    dialogFragment?.show(childFragmentManager, "")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*
     *
     */
    private fun activateAssignSchoolToDsm(dsmId: Int) {
        try {
            bt_save?.setOnClickListener {
                if (validateSpinners()) {
                    selectedSchoolId?.let {
                        if (schoolsAssignedList.map { it -> it.id }.contains(it)) {
                            showSnackBar(getString(R.string.same_dsm_assigned_to_ds))
                        } else {
                            try {
                                viewModel.assignUserSchool(dsmId, it)
                                        .observe(viewLifecycleOwner, Observer { response ->
                                            when (response) {
                                                is Resource.Success -> {
                                                    showSnackBar(getString(R.string.school_assigned_success))
                                                    clearSpinnersAfterSuccess()
                                                    loadUserDetails()
                                                }
                                                is Resource.GenericError -> {
                                                    response?.error?.let {
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
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadPartnersSchools() {
        try {
            viewModel.getUserSchoolList().observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let {
                            it?.listDigitalSchools?.let { it1 ->
                                initSchoolsSpinner(it1)
                            }
                        }
                    }
                    is Resource.GenericError -> {
                        handleGenericError(response.code)
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initSchoolsSpinner(schools: List<DigitalSchool>) {
        try {
            context?.let { context ->
                schools?.let { it1 ->
                    var schoolNames = it1.map { it.name }
                    var schoolAdapter = ArrayAdapter(context, R.layout.spinner_list_item, schoolNames)
                    etv_assign_school?.setAdapter(schoolAdapter)
                    schoolAdapter?.notifyDataSetChanged()

                    etv_assign_school?.setOnItemClickListener { adapterView, view, i, l ->
                        selectedSchoolId = it1.get(i).id
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun clearSpinnersAfterSuccess() {
        try {
            etv_role?.setText("")
            etv_assign_school?.setText("")
            selectedSchoolId = 0
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
    * Validates Assign Schools and Role Spinners
    */
    private fun validateSpinners(): Boolean {
        var valid = true
        til_role?.error = getString(R.string.invalid_role).takeIf {
            etv_role?.text.toString().isNullOrEmpty()
        }?.also {
            valid = false
        }

        til_assign_school?.error = getString(R.string.invalid_school).takeIf {
            etv_assign_school?.text.toString().isNullOrEmpty() || selectedSchoolId == null || selectedSchoolId == 0
        }?.also {
            valid = false
        }
        return valid
    }

    /**
     * updates the User Details
     */
    private fun updateUI(user: User) {
        try {

            tv_user_name?.setText(user.fname + " " + user.lname)

            iv_add_user_pic?.let {
                it.context?.let { context ->
                    Glide.with(context)
                            .load(user.profileImageUrl)
                            .placeholder(R.drawable.ic_user_placeholder)
                            .error(R.drawable.ic_user_placeholder)
                            .circleCrop()
                            .into(it)
                }
            }

            etv_first_name?.setText(user.fname)
            etv_last_name?.setText(user.lname)
            etv_mail?.setText(user.email)
            etv_phone?.setText(user.phone)

            //TODO after API Change set Taluk, district etc
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

    private fun createdUploadEntity(id: Int, type: String): HashMap<String, Any?> {
        val entityData = HashMap<String, Any?>()
        entityData["id"] = id
        entityData["type"] = type
        return entityData
    }

    //method to show file chooser
    private fun showFileChooser() {
        try {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PROFILE_IMAGE_REQUEST)
        } catch (e: Exception) {
            e.printStackTrace()
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
            activity?.let {
                it.contentResolver?.openInputStream(uri)?.let { streamData ->
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PROFILE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            try {
                data.data?.let { uri ->
                    activity?.let {
                        val bitmap = MediaStore.Images.Media.getBitmap(it.contentResolver, uri)

                        iv_add_user_pic?.let {
                            it.context?.let { context ->
                                Glide.with(context)
                                        .load(bitmap)
                                        .placeholder(R.drawable.ic_user_placeholder)
                                        .error(R.drawable.ic_user_placeholder)
                                        .circleCrop()
                                        .into(it)
                            }
                        }

                        logoUri = uri

                        logoUri?.let {
                            uploadProfilePic(it)
                        }
                    }
                }

            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this.context, "Invalid file", Toast.LENGTH_LONG).show()
            }
        }
    }

}