package org.evidyaloka.student.ui.home

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.evidyaloka.common.animation.slideOutAnimation
import org.evidyaloka.common.helper.loadUrlWithGlide
import org.evidyaloka.common.helper.loadUrlWithGlideCircle
import org.evidyaloka.common.receiver.InternetConnectionReceiver
import org.evidyaloka.common.util.Utils
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.model.Session
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.FragmentHomeBinding
import org.evidyaloka.student.databinding.ItemProfileBinding
import org.evidyaloka.student.ui.BaseFragment
import org.evidyaloka.student.ui.timetable.OnItemClickListner
import org.evidyaloka.student.utils.Util
import java.util.*

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var bindingProfile: ItemProfileBinding
    private val viewModel: HomeViewModel by viewModels()
    private val sessionAdapter = SessionAdapter()
    private var loadingDialog: AlertDialog? = null
    private var isFutureSession = false
    private var timetableId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::binding.isInitialized) {
            binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
            bindingProfile = binding.profile
            binding.rvCourseList.adapter = sessionAdapter
            binding.rvCourseList.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL, false
            )
            binding.btTimetable.setOnClickListener {
                try {
                    findNavController().navigate(R.id.action_nav_home_to_classListFragment)
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
            binding.ivCloseInappMgs.setOnClickListener {
                binding.inappMgsLayout.slideOutAnimation()
            }

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setNetworkErrorObserver(viewModel)
        registerNetworkCallback()
        sessionAdapter.setOnItemClickListener(object : OnItemClickListner {
            override fun OnItemClick(session: Session) {
                if (session.classType.toInt() ==
                    StudentConst.ClassType.Live.value
                ) {
                    val navigate =
                        HomeFragmentDirections.actionNavHomeToLiveSessionFragment(session)
                    try {
                        findNavController().navigate(navigate)
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                } else {
                    val navigate = timetableId?.let {
                        HomeFragmentDirections.actionNavHomeToSubTopicListFragment(session, it)
                    }
                    if (navigate != null) {
                        try {
                            findNavController().navigate(navigate)
                        } catch (e: Exception) {
                            FirebaseCrashlytics.getInstance().recordException(e)
                        }
                    }
                }
            }
        })
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

    private fun getSelectedStudent() {
        viewModel.getSelectedStudent().observe(viewLifecycleOwner, Observer {
            bindingProfile.tvDate.text =
                getString(R.string.today).plus(Utils.formatDatedMMMM(Date().time))
            bindingProfile.tvName.text = getString(R.string.hi).plus(" ").plus(it.name).plus("!")
            binding.tvSchoolName.text = it.schools?.name
            it.schools?.logoUrl?.let { it1 -> binding.ivSchoolLogo.loadUrlWithGlideCircle(it1,R.drawable.ic_school_logo_placeholder) }
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(46))
            bindingProfile.ivProfile.loadUrlWithGlide(
                it.profileUrl,
                R.drawable.ic_student_placeholder,
                requestOptions = requestOptions
            )
            getTodaySession()
            binding.tvSessionCount.text =
                String.format(getString(R.string.you_have_no_class), 0)
        })
    }

    private fun registerNetworkCallback() {
        var dialog: AlertDialog? = null
        InternetConnectionReceiver(this@HomeFragment) { isConnected ->
            lifecycleScope.launch(Dispatchers.Main) {
                if (isConnected) {
                    dialog?.dismiss()
                    getSelectedStudent()
                } else {
                    dialog = Util.showPopupDialog(
                        requireContext(),
                        title = getString(R.string.no_internet_connection),
                        message = getString(R.string.you_are_offline_please_click_on_downloads_under_menu_to_view_the_downloaded_videos_worksheets),
                        buttonText = getString(R.string.menu_downloads),
                        onClickListener = View.OnClickListener {
                            dialog?.dismiss()
                            findNavController().navigate(R.id.downloadFragment2)
                        }
                    )
                    dialog?.let {
                        it.setCancelable(false)
                        if (!it.isShowing) it.show()
                    }
                }
            }
        }
    }

    fun getTodaySession() {
        val startDate = Utils.getLocalDateInSeconds() / 1000
        val endDate = startDate
        getSessions(startDate, endDate)
        observeTodaySession()
    }

    fun getSessions(startDate: Long, endDate: Long) {
        viewModel.getSessions(startDate, endDate).observe(viewLifecycleOwner, Observer {
            sessionAdapter.submitList(null)
            sessionAdapter.submitList(it)
        })
    }

    private fun observeTodaySession() {
        viewModel.sessionDetailsObserver.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.containsKey(StudentConst.ERROR)) {
                    if (it[StudentConst.ERROR] == -1) {
                        handleNetworkDialog(false) { getTodaySession() }
                    } else {

                    }
                } else {
                    val sessionCount = it[StudentConst.count]
                    val timetableStatus = it[StudentConst.timetableStatus]
                    timetableId = it[StudentConst.timetableId] as Int?
                    if (timetableStatus == StudentConst.TimetableStatus.completed.toString() || timetableStatus == StudentConst.TimetableStatus.completed.toString()) {
                        loadingDialog?.let { if (it.isShowing) it.dismiss() }
                        checkShowCourseStartDate()
                        if (sessionCount == 0) {
                            val startDate = Utils.getLocalDateInSeconds() / 1000
                            val endDate = startDate + 604800 // 1 week second is 604800
                            getSessions(startDate, endDate)
                            observeNextWeekSession()
                        }

                        try {
                            sessionCount?.let {
                                if ((it as Int) > 1) {
                                    binding.tvSessionCount.text =
                                        String.format(
                                            getString(R.string.you_have_no_classes),
                                            it
                                        )
                                } else {
                                    binding.tvSessionCount.text =
                                        String.format(
                                            getString(R.string.you_have_no_class),
                                            it
                                        )
                                }
                            }
                        } catch (e: Exception) {
                            FirebaseCrashlytics.getInstance().recordException(e)
                        }

                    } else {
                        showLoadingSchedulePopUp()
                    }
                }
            }
        })
    }

    private fun observeNextWeekSession() {
        viewModel.sessionDetailsObserver.observe(viewLifecycleOwner, Observer {
            it?.let {
                val sessionCount = it[StudentConst.count]
                val timetableStatus = it[StudentConst.timetableStatus]
                timetableId = it[StudentConst.timetableId] as Int?
                if (timetableStatus == StudentConst.TimetableStatus.completed.toString() || timetableStatus == StudentConst.TimetableStatus.completed.toString()) {
                    loadingDialog?.let { if (it.isShowing) it.dismiss() }

                    try {
                        sessionCount?.let {
                            if ((it as Int) > 1) {
                                binding.tvSessionCount.text =
                                    String.format(
                                        getString(R.string.you_will_have_no_classes_next),
                                        it
                                    )
                            } else {
                                binding.tvSessionCount.text =
                                    String.format(
                                        getString(R.string.you_will_have_no_class_next),
                                        it
                                    )
                            }
                        }
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                } else {
                    showLoadingSchedulePopUp()
                }

            }
        })
    }

    private fun showLoadingSchedulePopUp() {
        context?.let {
            if (loadingDialog == null)
                loadingDialog = Util.showLoadingPopupDialog(it)
            if (loadingDialog?.isShowing == false)
                loadingDialog?.show()

        }
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            delay(10 * 1000) // delayed for 10 seconds
            withContext(Dispatchers.Main) {
                val startDate = Utils.getLocalDateInSeconds() / 1000
                val endDate = startDate
                getSessions(startDate, endDate)
                observeTodaySession()
            }
        }
    }

    private fun checkShowCourseStartDate() {
        viewModel.shouldShowcourseStartDate().observe(viewLifecycleOwner, Observer {
            if (it) {
                getMissedClasses()
            }
        })
    }

    private fun getMissedClasses() {
        viewModel.getMissedClasses().observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    if (resource.data?.count ?: 0 <= 0)
                        return@Observer
                    resource.data?.firstMissedDate?.let {
                        Utils.formatyyyy_MM_dd_To_dd_MM_yyyy(it)?.let { date ->
                            showCourseStartAlert(
                                date
                            )
                        }
                    }
                }
                is Resource.GenericError -> {

                }
            }
        })
    }

    private fun showCourseStartAlert(startDate: String) {
        var dialog:AlertDialog? = null
        dialog = Util.showPopupDialog(
            requireContext(),
            null,
            String.format(getString(R.string.your_course_started_on), startDate),
            R.drawable.ic_bunny_reading,
            getString(R.string.ok),
            View.OnClickListener {
                dialog?.dismiss()
                viewModel.setCourseStartDateDialogShow(false)
                    .observe(viewLifecycleOwner, Observer { })
            }
        )
        dialog?.show()
    }
}