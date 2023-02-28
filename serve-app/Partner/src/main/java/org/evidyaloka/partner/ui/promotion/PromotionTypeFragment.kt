package org.evidyaloka.partner.ui.promotion

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_student.*
import org.evidyaloka.common.util.Utils
import org.evidyaloka.common.view.SuccessDialogFragment
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.AcademicYear
import org.evidyaloka.partner.R
import org.evidyaloka.partner.databinding.PromotionTypeFragmentBinding
import org.evidyaloka.partner.ui.BaseFragment
import java.util.HashMap

@AndroidEntryPoint
class PromotionTypeFragment : BaseFragment() {
    private val TAG = "PromotionTypeFragment"
    private val viewModel: PromotionTypeViewModel by viewModels()
    private lateinit var binding: PromotionTypeFragmentBinding
    private var grade: Int? = null
    private var studentsIdList: IntArray = intArrayOf()
    private var isAllSelected: Boolean = false
    private var digitalSchoolId: Int? = null
    private var courseProviderId: Int? = null
    private var studentPromotionType: String = "Active"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val arg = PromotionTypeFragmentArgs.fromBundle(it)
            grade = arg.grade
            studentsIdList = arg.studentsId
            isAllSelected = arg.isAllSelected
            digitalSchoolId = arg.digitalSchoolId
            courseProviderId = arg.courseProviderId
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PromotionTypeFragmentBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        grade?.let { binding.etvGrade.setText(it.toString()) }
        studentsIdList?.let {
            var countLable = if(isAllSelected) "All Students" else it.size.toString()
            binding.etvSelectedStudentsCount.setText(countLable)
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.promotion_types_array,
            R.layout.spinner_list_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.spPromotionType.setAdapter(adapter)
        }
        binding.spPromotionType.setOnItemClickListener { parent, view, position, id ->
            studentPromotionType = if(parent.getItemAtPosition(position).toString() == resources.getString(R.string.alumni)) "Alumni" else "Active"
        }


        binding.btSubmit.setOnClickListener {
            buildRequestData()?.let {
                viewModel.promoteStudent(it).observe(viewLifecycleOwner, Observer {
                    when(it){
                        is Resource.Success -> {
                            showSuccessAlert()
                        }
                        else -> {
                            showErrorAlert()
                        }
                    }
                })
            }
        }
    }

    fun showSuccessAlert(){
        SuccessDialogFragment.Builder(requireContext())
            .setIsDialogCancelable(true)
            .setTitle(getString(R.string.successful))
            .setDescription(getString(R.string.students_promoted))
            .setDismissTimer(PartnerConst.DIALOG_CLOSE_TIME)
            .setIcon(R.drawable.ic_checked_green)
            .setViewType(SuccessDialogFragment.DIALOG_TYPE.ALERT)
            .setOnDismissListener(object :SuccessDialogFragment.OnDismissListener{
                override fun OnDismiss(dialog: DialogInterface) {
                    navController.navigateUp()
                }
            })
            .build()
            .show(childFragmentManager,"")
    }

    fun showErrorAlert(){
        SuccessDialogFragment.Builder(requireContext())
            .setIsDialogCancelable(true)
            .setTitle(getString(R.string.error))
            .setDescription(getString(R.string.students_not_promoted))
            .setDismissTimer(PartnerConst.DIALOG_CLOSE_TIME)
            .setIcon(R.drawable.ic_close_44dp)
            .setViewType(SuccessDialogFragment.DIALOG_TYPE.ALERT)
            .setOnDismissListener(object :SuccessDialogFragment.OnDismissListener{
                override fun OnDismiss(dialog: DialogInterface) {
                    navController.navigateUp()
                }
            })
            .build()
            .show(childFragmentManager,"")
    }

    /**
     * builds request for Student Enrolment API
     * @return MapMap<String, Any>
     */
    fun buildRequestData(): HashMap<String, Any>? {
        var map: HashMap<String, Any>? = null
        try {
            map = hashMapOf()
            map[PartnerConst.STUDENT_IDS] = studentsIdList
            grade?.let { map[PartnerConst.GRADE] = it }
            map[PartnerConst.UPDATE_ALL_STUDENT] = isAllSelected
            map[PartnerConst.STUDENT_PROMOTION_TYPE] = studentPromotionType
            courseProviderId?.let{ map[PartnerConst.COURSE_PROVIDER_ID] = it }
            digitalSchoolId?.let {
                if (it != 0) {
                    map[PartnerConst.DIGITAL_SCHOOL_ID] = it
                }
            }
            Log.e(TAG, "buildRequestData: $map")
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e(TAG, e.message ?: "gson converter exception")
        }
        return map
    }
}