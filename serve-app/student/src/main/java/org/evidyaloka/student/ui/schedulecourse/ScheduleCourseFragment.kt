package org.evidyaloka.student.ui.schedulecourse

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.common.util.Utils
import org.evidyaloka.common.animation.slideOutAnimation
import org.evidyaloka.common.helper.loadUrlWithGlide
import org.evidyaloka.common.helper.loadUrlWithGlideCircle
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.model.Course
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.ItemProfileBinding
import org.evidyaloka.student.databinding.ScheduleCourseFragmentBinding
import org.evidyaloka.student.ui.BaseFragment
import org.evidyaloka.student.ui.schedulecourse.adapter.CourseAdapter
import java.util.*
import kotlin.collections.HashMap


@AndroidEntryPoint
class ScheduleCourseFragment : BaseFragment() {
    private lateinit var binding: ScheduleCourseFragmentBinding
    private lateinit var bindingProfile: ItemProfileBinding
    private val viewModel: ScheduleCourseViewModel by viewModels()
    private val courseAdapter =
        CourseAdapter()
    private val TAG = "ScheduleCourseFragment"
    private lateinit var courseScheduleBottomSheet: BottomSheetDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ScheduleCourseFragmentBinding.inflate(layoutInflater,container,false)
        bindingProfile = binding.profile
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        getCourseDetailsToSchedule()
        bindingProfile.tvDate.text = getString(R.string.today).plus(Utils.formatDatedMMMM(Date().time))
        binding.ivCloseInappMgs.setOnClickListener {
            binding.inappMgsLayout.slideOutAnimation()
        }
        binding.rvCourseList.adapter = courseAdapter
        binding.rvCourseList.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding.btLearn.setOnClickListener {
            buildRequest()?.let {
                viewModel.updateOffering(it).observe(viewLifecycleOwner, Observer {
                    when (it) {
                        is Resource.Success -> {
                            showScheduleCourseScreen()
                        }
                        is Resource.GenericError -> {
                            it.code?.let{
                                if(it <= 0)
                                    return@Observer
                            }
                            when (it.error?.code) {
                                2, 3, 36, 38 -> showPopup(
                                    getString(R.string.error),
                                    getString(R.string.data_submited_not_valid)
                                )
                                35 -> showPopup(
                                    getString(R.string.error),
                                    getString(R.string.user_should_be_guardian)
                                )
                                19 -> showPopup(
                                    getString(R.string.error),
                                    getString(R.string.digital_school_doesnot_exist)
                                )
                                16 -> showPopup(
                                    getString(R.string.error),
                                    getString(R.string.center_doesnot_exist)
                                )
                            }
                        }
                    }
                })
            }?:showPopup(getString(R.string.error),getString(R.string.please_select_course))
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.notification_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_notification -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }
    /**
     * Show Schedule Course Screen.```
     */
    fun showScheduleCourseScreen() {
        try{
            activity?.let {
                if(!this::courseScheduleBottomSheet.isInitialized) {
                    courseScheduleBottomSheet = CourseScheduleDialogFragment.newInstance()
                    if(!courseScheduleBottomSheet.isVisible)
                        courseScheduleBottomSheet?.show(
                            it.supportFragmentManager,
                            "courseScheduleBottomSheet"
                        )
                }else{
                    if(!courseScheduleBottomSheet.isVisible)
                        courseScheduleBottomSheet?.show(
                            it.supportFragmentManager,
                            "courseScheduleBottomSheet"
                        )
                }
            }
        }catch (e : Exception){
            FirebaseCrashlytics.getInstance().recordException(e)

        }

    }

    fun getCourseDetailsToSchedule(){
        viewModel.getSelectedStudent().observe(viewLifecycleOwner, Observer {
            bindingProfile.tvName.text = it.name
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(46))
            bindingProfile.ivProfile.loadUrlWithGlide(it.profileUrl,R.drawable.ic_student_placeholder,requestOptions = requestOptions)
            binding.schoolName.text = it.schools?.name
            it.schools?.logoUrl?.let { logo -> binding.imSchoolLogo.loadUrlWithGlideCircle(logo, R.drawable.ic_school_logo_placeholder) }
            if(it.onboardingStatus == StudentConst.schedule_not_opted) {
                showScheduleCourseScreen()
            }
        })
        viewModel.getCourses()?.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success -> {
                    it.data?.let{
                        courseAdapter.setItem(it)
                    }
                }
                is Resource.GenericError -> {
                    showPopup(getString(R.string.label_sorry),getString(R.string.could_not_able_to_fetch_data))
                }
            }
        })
    }

    fun buildRequest(): Map<String, Any>?{
        return courseAdapter.getSelectedCourseId().takeIf { it.isNotEmpty() }?.let {
            HashMap<String,Any>().apply {
                this[StudentConst.OFFERINGS] = courseAdapter.getSelectedCourseId()
            }
        }

    }

}