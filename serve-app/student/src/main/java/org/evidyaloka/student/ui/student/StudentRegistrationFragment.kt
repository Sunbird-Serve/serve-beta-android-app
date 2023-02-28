package org.evidyaloka.student.ui.student

import android.Manifest
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.common.helper.PermissionRequester
import org.evidyaloka.common.util.Utils
import org.evidyaloka.common.helper.getFileName
import org.evidyaloka.common.helper.isValidName
import org.evidyaloka.common.helper.loadUrlWithGlideCircle
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.helper.ErrorData
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.student.model.ExploreData
import org.evidyaloka.core.student.model.Language
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.FragmentStudentRegistrationBinding
import org.evidyaloka.student.ui.BaseFragment
import java.util.*

@AndroidEntryPoint
class StudentRegistrationFragment : BaseFragment() {

    val TAG = "StudentRegistrationFrag"

    private var profilePicId: Int? = null
    private var profilePicUri: Uri? = null
    private var selectedmediumOfStudy: Int = 0
    private var mediumOfStudy: List<Language> = listOf()
    private val viewModel: StudentRegistrationViewModel by viewModels()
    private lateinit var binding: FragmentStudentRegistrationBinding
    private var exploreData: ExploreData? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            var args = StudentRegistrationFragmentArgs.fromBundle(it)
            exploreData = args.exploreData
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentStudentRegistrationBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val materialDateBuilder: MaterialDatePicker.Builder<*> = MaterialDatePicker.Builder.datePicker()
        materialDateBuilder
            .setTheme(R.style.CalendarTheme)
            .setTitleText("SELECT A DATE")
        val constraintsBuilderRange = CalendarConstraints.Builder()
            .setEnd(Calendar.getInstance().timeInMillis)
            .setValidator(DateValidatorPointBackward.now())
        val materialDatePicker = materialDateBuilder
            .setCalendarConstraints(constraintsBuilderRange.build())
            .build()

        binding.etvDob.setOnClickListener {
            if (!materialDatePicker.isAdded) {
                materialDatePicker.show(childFragmentManager, "DOB_DATE")
            }
        }
        materialDatePicker.addOnPositiveButtonClickListener {
            if (it is Long) {
                val date = Date(it)
                binding.etvDob.setText(Utils.format_dd_MM_yyyy(date.time))
            }
        }
        binding.rbgRelationshipType.check(when (exploreData?.relationShip) {
            getString(R.string.guardian) -> R.id.rb_guardian
            else -> R.id.rb_father
        })
        exploreData?.grade?.let { binding.etvGrade.setText(it.toString()) }
        binding.ibLogoSelector.setOnClickListener {
            storagePermission.runWithPermission {
                showFileChooser.launch("image/*")
            }
        }

        binding.cbConsent.setOnCheckedChangeListener { view, isChecked ->
            binding.btSubmit.isEnabled = isChecked
        }

        binding.btSubmit.setOnClickListener {
            if(validateStudentDetails()){
                profilePicUri?.let { uri -> uploadProfilePic(uri) }?: submitStudentEnrollment()
            }

        }

        exploreData?.let{
            binding.llSchoolDetails.visibility = View.VISIBLE
            binding.tvSchoolName.text = "${getString(R.string.join)} ${it.school?.name}"
            it.school?.schoolLogoUrl?.let { it1 -> binding.imSchoolLogo.loadUrlWithGlideCircle(it1) }
        }

        loadGrades()
        getLanguages()
//        getSchoolBoard()
    }

    private val storagePermission = PermissionRequester(
        this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        R.string.rationale_storage_doubt
    )

    //method to show file chooser
    private val showFileChooser = registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let {
                profilePicUri = it
                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
                Glide.with(requireActivity())
                    .load(bitmap)
                    .circleCrop()
                    .listener(object : RequestListener<Drawable> {

                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            binding.ivCloseLogo.visibility = View.GONE
                            binding.tvLogoPlaceholderText?.visibility = View.VISIBLE
                            showSnackBar(getString(R.string.invalid_image))
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            binding.ivCloseLogo.visibility = View.VISIBLE
                            binding.tvLogoPlaceholderText.visibility = View.GONE
                            return false
                        }
                    })
                    .into(binding.ivStudentProfile)
            }
        }

    /**
     * This function uploads Students Image.
     * @param uri Uri Image Uri
     * @param addMoreUser Boolean denotes if more user to be added
     */
    private fun uploadProfilePic(uri: Uri) {
        try {

            activity?.let {
                it.contentResolver?.openInputStream(uri)?.let { streamData ->
                    viewModel.uploadFile(
                        StudentConst.DocType.STUDENT_PROFILE_PIC.value,
                        StudentConst.DocFormat.JPG.value,
                        uri.getFileName(it)!!,
                        streamData
                    ).observe(viewLifecycleOwner, Observer { response ->
                        when (response) {
                            is Resource.Success -> {
                                profilePicId = response.data?.id
                                submitStudentEnrollment()
                            }
                            else -> {
                                Toast.makeText(requireContext(),getString(R.string.err_network),Toast.LENGTH_LONG).show()
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

    /**
     * This function is responsible to enroll student to a school
     */
    private fun submitStudentEnrollment() {


        if (validateStudentDetails()) {
            buildRequestData()?.let {
                try {
                    viewModel.enrollStudents(it).observe(viewLifecycleOwner, Observer { response ->
                        when (response) {
                            is Resource.Success -> {
                                 navController.navigate(StudentRegistrationFragmentDirections
                                     .actionStudentRegistrationFragmentToFragmentThankYou())
                            }
                            is Resource.GenericError -> {
                                handleGenericError(response.error)
                            }
                        }
                    })
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * builds request for Student Enrolment API
     * @return MapMap<String, Any>
     */
    fun buildRequestData(): Map<String, Any>? {
        var map: HashMap<String, Any>? = null
        try {
            map = hashMapOf()
            // map.put(PARTNER_ID, etv_user_name.text.toString()) PARTNER_ID to be used from MainRepository
            map[StudentConst.STUDENT_NAME] = binding.etvStudentName?.text.toString()
            map[PartnerConst.RELATIONSHIP_TYPE] = when (binding.rbgRelationshipType?.checkedRadioButtonId) {
                R.id.rb_father -> PartnerConst.FATHER
                R.id.rb_mother -> PartnerConst.MOTHER
                R.id.rb_guardian -> PartnerConst.GUARDIAN
                else -> PartnerConst.FATHER
            }

            var formattedDate = Utils.format_dd_MM_yyyy_To_yyyy_MM_dd(binding.etvDob?.text.toString())

            formattedDate?.let { map[StudentConst.DOB] = formattedDate }

            map[StudentConst.GENDER] = when (binding.rbgGender?.checkedRadioButtonId) {
                R.id.rb_boy -> StudentConst.BOY
                R.id.rb_girl -> StudentConst.GIRL
                else -> StudentConst.BOY
            }
            map[StudentConst.GRADE] = binding.etvGrade?.text.toString()
            map[StudentConst.MEDIUM_OF_STYDY] = selectedmediumOfStudy
            map[StudentConst.HAS_TAKEN_GUARDIAN_CONSENT] = if (binding.cbConsent.isChecked) 1 else 0
            map[StudentConst.PHYSICAL_SCHOOL_NAME] = binding.etvSchoolName?.text.toString()

            profilePicId?.let {
                map.put(StudentConst.PROFILE_PIC_ID, it)
            }

            exploreData?.school?.id?.let {
                if (it != 0) {
                    map[StudentConst.DIGITAL_SCHOOL_ID] = it
                }
            }
            Log.e(TAG, "buildRequestData: $map")
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e(TAG, e.message ?: "gson converter exception")
        }
        return map
    }

    /*
     *This function validates the student details
     */
    private fun validateStudentDetails(): Boolean {
        var valid = true
        var requestfocusSet = false

        with(binding) {
            tilStudentName.error =
                getString(R.string.invalid_name).takeIf {
                    !etvStudentName.text.toString().isValidName()
                }?.also {
                    valid = false
                    if (!requestfocusSet) {
                        requestfocusSet = true
                        etvStudentName.requestFocus()
                    }
                }


            tilGrade.error =
                getString(R.string.invalid_grade).takeIf {
                    etvGrade.text.toString().isEmpty()
                }
                    ?.also {
                        valid = false
                        if (!requestfocusSet) {
                            requestfocusSet = true
                            etvGrade.requestFocus()
                        }
                    }
            tilSchoolName.error =
                getString(R.string.invalid_school_details).takeIf {
                    etvSchoolName.text.toString().isEmpty()
                }
                    ?.also {
                        valid = false
                        if (!requestfocusSet) {
                            requestfocusSet = true
                            etvSchoolName.requestFocus()
                        }
                    }

            tilMedium.error =
                getString(R.string.invalid_school_details).takeIf {
                    etvMedium.text.toString().isEmpty()
                }
                    ?.also {
                        valid = false
                        if (!requestfocusSet) {
                            requestfocusSet = true
                            etvMedium.requestFocus()
                        }
                    }

        }
        return valid
    }

    /**
     *  This function handles Generic Error
     */
    private fun handleGenericError(error: ErrorData?) {
        error?.let {

            when (it.code) {
                54 -> {
                    binding.tilMedium.error = getString(R.string.invalid_language_type)
                }
                53 -> {
                    binding.tilGrade.error = getString(R.string.invalid_grade)
                }
                55 -> {
                    Toast.makeText(requireContext(),getText(R.string.enrollment_failed_contact_admin),Toast.LENGTH_LONG).show()
                }

                else -> {
                    showSnackBar(error.message)
                }
            }
        }
    }


    /*
     * This Function is responsible to fetch Grades from shared Preference
     */
    private fun loadGrades() {
        try {
            exploreData?.courseProviderId?.let {
                viewModel.getGrades(it).observe(viewLifecycleOwner, Observer {
                    when(it){
                        is Resource.Success -> {
                            it.data?.let {
                                updateGradeSpinner(it.grades)
                            }
                        }
                        is Resource.GenericError -> {

                        }
                    }
                })
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

        /**
     * loads/updates Grade Spinner with the list of grades
     * @param grades List<Int> list of grades
     */
    private fun updateGradeSpinner(grades: List<Int>) {
        try {
            context?.let {
                if (grades.size > 0) {
                    var adapter = ArrayAdapter(it, R.layout.spinner_list_item, grades)
                    binding.etvGrade.setAdapter(adapter)
                    binding.etvGrade.setOnItemClickListener { parent, view, position, id ->

                    }
                    adapter.notifyDataSetChanged()
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }


    /*
    * This Function is responsible to fetch the languages from API
    */
    private fun getLanguages() {
        try {
            viewModel.getLanguages().observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let {
                            setLanguageAdapter(it)
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

    /**
     * loads/updates Medium of Study Spinner with the list of Language
     * @param list List<Language> list of language
     */
    private fun setLanguageAdapter(list: List<Language>) {
        try {
            mediumOfStudy = list
            context?.let {
                val adapter = ArrayAdapter(it, R.layout.spinner_list_item, mediumOfStudy.map { it.name })
                binding.etvMedium.setAdapter(adapter)
                binding.etvMedium.setOnItemClickListener { parent, view, position, id ->
                    selectedmediumOfStudy = mediumOfStudy.get(position).id
                }
                adapter?.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

}