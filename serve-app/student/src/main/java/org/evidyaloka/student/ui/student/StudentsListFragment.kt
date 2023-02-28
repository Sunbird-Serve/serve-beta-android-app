package org.evidyaloka.student.ui.student

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.evidyaloka.common.extension.showNotification
import org.evidyaloka.common.receiver.InternetConnectionReceiver
import org.evidyaloka.common.util.Utils
import org.evidyaloka.core.Constants.CommonConst
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.model.Session
import org.evidyaloka.core.model.SubTopic
import org.evidyaloka.core.student.database.entity.CourseContentEntity
import org.evidyaloka.core.student.model.Student
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.FragmentStudentsListBinding
import org.evidyaloka.student.ui.ParentExploreActivity
import org.evidyaloka.student.ui.StudentHomeActivity
import org.evidyaloka.student.ui.home.HomeViewModel
import org.evidyaloka.student.ui.rtc.RtcViewFragmentArgs
import org.evidyaloka.student.utils.Util

@AndroidEntryPoint
class StudentsListFragment : Fragment(), OnStudentSelectedListener {

    private lateinit var binding: FragmentStudentsListBinding
    var studentAdapter = StudentAdapter()
    private val viewModel: HomeViewModel by viewModels()
    private val TAG = "StudentListFragment"

    private var userType: String? = null
    private var userAction: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            var args = StudentsListFragmentArgs.fromBundle(it)
            userType = args.userType
            userAction = args.userAction
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentsListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btAddStudent.visibility = userType?.let {
            if (CommonConst.PersonaType.Student.toString().equals(it)) {
                View.GONE
            } else {
                View.VISIBLE
            }
        } ?: View.VISIBLE

        binding.ivMenu.setOnClickListener {
            activity?.let { (it as ParentExploreActivity).openDrawer() }
        }
        viewModel.getProgressObserable().observe(viewLifecycleOwner, Observer {
            binding.progressCircular.visibility =
                if (it) View.VISIBLE else View.GONE
        })
        disableLetsStartButton()
        binding.rvStudents.let {
            it.apply {
                layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                )
                adapter = studentAdapter
            }
        }
        ping()
        updateFCMToken()
        registerNetworkCallback()

        binding.btLetsStart.setOnClickListener {
            studentAdapter?.let {
                it.getSelectedStudent()?.let { student ->
                    Log.e(TAG, "onCreate: " + it)
                    if (student.status != StudentConst.Status.Active.toString()) {
                        Util.showPopupDialog(
                            requireContext(),
                            title = getString(R.string.label_sorry),
                            message = getString(R.string.label_your_account_inactive)
                        )?.let {
                            it.show()
                        }
                        return@let
                    }
                    student.onboardingStatus?.let {
                        viewModel.setSelectedStudent(student)
                            .observe(viewLifecycleOwner, Observer {
                                if (it) {
                                    activity?.finish()
                                    startActivity(
                                        Intent(
                                            requireContext(),
                                            StudentHomeActivity::class.java
                                        )
                                    )
                                }
                            })
                    }
                }
            }
        }

        binding.btAddStudent.setOnClickListener {
            try {
                findNavController().navigate(
                    StudentsListFragmentDirections.actionStudentListToFragmentLocationAccess(
                        showToolBar = true
                    )
                )
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

        try {
            viewModel.mErrorObservable?.observe(viewLifecycleOwner, Observer {
                when (it?.code) {
                    401, 403 -> {
                        logoutuser()
                    }
                }
            })
        } catch (e: Exception) {
        }
    }

    private fun registerNetworkCallback() {
        var dialog: AlertDialog? = null
        InternetConnectionReceiver(this){ isConnected ->
            lifecycleScope.launch(Dispatchers.Main) {
                if (isConnected) {
                    dialog?.dismiss()
                    getStudentsList()
                } else {
                    dialog = Util.showPopupDialog(
                        requireContext(),
                        title = getString(R.string.no_internet_connection),
                        message = getString(R.string.you_are_offline_please_click_on_downloads_under_menu_to_view_the_downloaded_videos_worksheets),
                        buttonText = getString(R.string.menu_downloads),
                        onClickListener = View.OnClickListener {
                            dialog?.dismiss()
                            findNavController().navigate(R.id.downloadFragment)
                        }
                    )
                    dialog?.let {
                        it.setCancelable(false)
                        it.show()
                    }
                }
            }
        }
    }

    private fun buildPendingIntent(
        context: Context,
        content: CourseContentEntity?
    ): PendingIntent? {
        content?.let {
            with(content) {
                var args = RtcViewFragmentArgs(
                    Session(
                        id = sessionId ?: 0,
                        topicId = topicId ?: 0,
                        topicName = topicName ?: "",
                        offeringId = offeringId ?: 0,
                        videoLink = "file:///data/user/0/org.evidyaloka/files/477"
                    ),
                    timetableId = timetableId ?: 0,
                    subTopic = SubTopic(subtopicId ?: 0, subTopicName ?: "")
                ).toBundle()

                return NavDeepLinkBuilder(context)
                    .setGraph(R.navigation.mobile_navigation)
                    .setDestination(R.id.rtcnavigation)
                    .setComponentName(StudentHomeActivity::class.java)
                    .setArguments(args)
                    .createPendingIntent()
            }
        }
        return null
    }


    private fun logoutuser() {
        try {
            viewModel.clearUser().observe(viewLifecycleOwner, Observer {
                activity?.finish()
                startActivity(Intent(requireContext(), ParentExploreActivity::class.java))
            })
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    private fun initAdapter(students: List<Student>) {
        try {
            students?.let {
                if (it.size > 0) {
                    studentAdapter?.setItems(it)
                    enableLetsStartButton()
                    userType?.let { userType ->
                        if (CommonConst.PersonaType.Student.toString().equals(userType)) {
                            if (it.size == 1) {
                                studentAdapter?.setStudentSelectListener(this)
                            }
                        }
                    }
                } else {
                    // Irrespective of persona, if there no student, show Fab button.
                    binding.btAddStudent.visibility = View.VISIBLE
                    try {
                        findNavController().navigate(StudentsListFragmentDirections.actionStudentListToFragmentLocationAccess())
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    /**
     * Function to show progress bar
     */
    fun showProgressCircularBar() {
        binding.progressCircular.visibility = View.VISIBLE
    }

    /**
     * Function to hide progress bar
     */
    fun hideProgressCircularBar() {
        binding.progressCircular.visibility = View.GONE
    }

    fun disableLetsStartButton() {
        binding.btLetsStart.isEnabled = false
    }

    fun enableLetsStartButton() {
        binding.btLetsStart.isEnabled = true
    }


    fun getStudentsList() {
        try {
            viewModel.getStudents().observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let {
                            initAdapter(it)
                        }
                    }

                    is Resource.GenericError -> {
                        when(response?.code){
                            402,102 -> {
                                logoutuser()
                            }
                        }
                    }
                }
            })
        } catch (e: Exception) {
        }
    }

    fun ping() {
        try {
            viewModel.ping().observe(viewLifecycleOwner, Observer {
                //do nothing
            })
        } catch (e: Exception) {
        }
    }

    fun updateFCMToken() {
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                // Get new FCM registration token
                val token = task.result
                var map = HashMap<String, Any>().apply {
                    put(StudentConst.PUSH_TOKEN, token)
                    Utils.getDeviceInfo(requireContext(), userType)?.let {
                        put("deviceInfo", it)
                    }
                }
                try {
                    viewModel.updateFCMToken(map).observe(viewLifecycleOwner, Observer {

                    })
                } catch (e: Exception) {
                }

            })
        } catch (e: Exception) {
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            activity?.let { (it as ParentExploreActivity).hideToolbar() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            activity?.let { (it as ParentExploreActivity).showToolbar() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStudentSelected(student: Student) {
        try {
            studentAdapter?.let {
                it.getSelectedStudent()?.let { student ->
                    if (student.status != StudentConst.Status.Active.toString()) {
                        Util.showPopupDialog(
                            requireContext(),
                            title = getString(R.string.label_sorry),
                            message = getString(R.string.label_your_account_inactive)
                        )?.let {
                            it.show()
                        }
                        return@let
                    }
                    student.onboardingStatus?.let {
                        viewModel.setSelectedStudent(student)
                            .observe(viewLifecycleOwner, Observer {
                                if (it) {
                                    activity?.finish()
                                    startActivity(
                                        Intent(
                                            requireContext(),
                                            StudentHomeActivity::class.java
                                        )
                                    )
                                }
                            })
                    }
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}