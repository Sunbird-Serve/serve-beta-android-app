package org.evidyaloka.student.ui.timetable.courseview

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.FragmentCourseTimetableBinding
import org.evidyaloka.student.ui.StudentHomeActivity
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.Constants.StudentConst.SESSION
import org.evidyaloka.core.Constants.StudentConst.TIMETABLE_ID
import org.evidyaloka.common.util.Utils
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.model.Course
import org.evidyaloka.core.model.Session
import org.evidyaloka.common.util.SubjectViewUtils
import org.evidyaloka.student.ui.BaseFragment
import org.evidyaloka.student.ui.timetable.OnItemClickListner
import org.evidyaloka.student.ui.timetable.TimetableViewModel
import java.util.*

@AndroidEntryPoint
class CourseTimetableFragment : BaseFragment() {

    private val TAG = "CourseTimetableFragment"

    private var timetableAdapter =
        CourseTimetableAdapter()
    private val viewModel: TimetableViewModel by viewModels()
    private var startDate: Long = 0L
    private var endDate: Long = 0L
    private var timetableId: Int? = null
    private var isMissedClasses: Boolean = false
    private var offeringId: Int? = null

    private lateinit var binding: FragmentCourseTimetableBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            CourseTimetableFragmentArgs.fromBundle(it)?.let {
                startDate = it.startDate
                endDate = it.endDate
                isMissedClasses = it.isMissedClassView
            }
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //Filter not part of MVP
        //inflater.inflate(R.menu.course_timetable, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.action_notification)
        if (item != null) item.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Filter not part of MVP
        /*if (item.itemId.equals(R.id.nav_filter)) {
            Log.e(TAG, "onOptionsItemSelected: ")
            activity?.let {
                var filterClassDialogBottomSheet = FilterClassDialogBottomSheet.newInstance()
                filterClassDialogBottomSheet?.show(
                    it.supportFragmentManager,
                    "filterClassDialogBottomSheet"
                )
            }
        }*/
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCourseTimetableBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        openTimetableView()
        getCourseList()

        if (isMissedClasses) {
            setToolbarTitle(resources.getString(R.string.missed_class))
            binding.layoutCalendar?.visibility = View.GONE
            binding.rlCalendarHeader?.visibility = View.VISIBLE
            binding.tvCurrentDate?.text = Utils.formatDatedMonthText(startDate)
            binding.ivCalendarLeft?.setOnClickListener {
                changeMonth(false)
            }
            binding.ivCalendarRight?.setOnClickListener {
                changeMonth(true)
            }
        }
    }

    private fun openTimetableView() {
        var selectedDate = ""
        selectedDate = Utils.formatDatedMMMM(startDate)?.let { it } ?: ""
        selectedDate =
            selectedDate.plus(" - ").plus(Utils.formatDatedMMMM(endDate)?.let { it } ?: "")

        binding.tvCalendarTitle.setText(selectedDate)

        binding.rvClasses.apply {
            layoutManager = LinearLayoutManager(
                this.context,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = timetableAdapter
        }

        timetableAdapter?.setOnItemClickListener(object :
            OnItemClickListner {
            override fun OnItemClick(session: Session) {
                Log.e(TAG, "OnItemClick: ")
                if (session.classType.toInt() ==
                    StudentConst.ClassType.Live.value
                ) {
                    var bundle = Bundle()
                    bundle.putParcelable("session", session)
                    try {
                        findNavController().navigate(R.id.liveSessionFragment, bundle)
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)

                    }
                } else {
                    var bundle = Bundle()
                    bundle.putParcelable(SESSION, session)
                    timetableId?.let { bundle.putInt(TIMETABLE_ID, it) }
                    try {
                        findNavController().navigate(R.id.subTopicListFragment, bundle)
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)

                    }
                }
            }
        })

        getTimetable(startDate, endDate, offeringId)

    }

    /**
     * To change the color of the app bar & status bar.
     */
    fun updateUIColor(subject: String? = null) {
        // update cardview border and bg
        activity?.let { it ->
            (it as StudentHomeActivity).updateUiStyle(subject?.let {
                SubjectViewUtils.getUIBackground(
                    it
                )
            })
        }
    }

    /**
     * To get list of all the courses opted by student
     */
    fun getCourseList() {
        viewModel.getStudentCourses()?.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Success -> {
                    it.data?.let {
                        val courses: MutableList<Course> = mutableListOf()
                        // Add all a temporary key for selecting all the subjects
                        context?.let {
                            courses.add(
                                0, Course(
                                    0,
                                    name = it.resources.getString(R.string.labeL_all_class),
                                    isSelected = true
                                )
                            )
                        }
                        it?.forEach { course ->
                            courses.add(Course(course.id, course.name, course.isSelected))
                        }
                        initCourseSpinner(courses)
                    }
                }
                is Resource.GenericError -> {

                }
            }
        })
    }

    /**
     * function to update course Spinner
     */
    private fun updateCourseSpinner(list: List<Course>) {
        try {
            var l = list.map { it.name }
            binding.etvCourses?.apply {
                context?.let {
                    var adapter = ArrayAdapter(it, R.layout.item_spinner, l)
                    setAdapter(adapter)
                    adapter?.notifyDataSetChanged()
                    setOnItemClickListener { adapterView, mView, position, l ->
                        list[position].name?.let {
                            binding.etvCourses.setText(list[position].name)
                            updateUIColor(it.takeIf { it != "All" })
                            if (position == 0) {
                                offeringId = null
                                getTimetable(startDate, endDate)
                            } else {
                                offeringId = list[position]?.id
                                getTimetable(startDate, endDate, offeringId)
                            }
                        }
                        updateCourseSpinner(list)
                    }
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    /**
     * To initliaze the course spinner
     * and select All as default value.
     */
    private fun initCourseSpinner(list: List<Course>) {
        try {
            var l = list.map { it.name }
            binding.etvCourses?.apply {
                context?.let {
                    var adapter = ArrayAdapter(it, R.layout.item_spinner, l)
                    setAdapter(adapter)
                    adapter?.notifyDataSetChanged()
                    if (list.isNotEmpty()) {
                        binding.etvCourses.setSelection(0)
                        binding.etvCourses.setText(list[0].name)
                    }
                    updateCourseSpinner(list)
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    /**
     * To restore the previous app bar color
     */
    override fun onDestroyView() {
        super.onDestroyView()
    }

    /**
     * To restore the previous app bar color
     */
    override fun onDetach() {
        super.onDetach()
    }

    private fun getTimetable(startDate: Long, endDate: Long, offeringId: Int? = null) {
        Log.e(TAG, "getTimetable: " + startDate)
        Log.e(TAG, "getTimetable: " + endDate)
        viewModel.getTimetable(
            startDate / 1000,
            endDate / 1000,
            offeringId,
            classType = if (isMissedClasses) 1 else 2
        )
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                timetableAdapter.submitList(it)

                it?.let {
                    if (it.size > 1) {
                        binding.tvClassCount.text =
                            String.format(getString(R.string.plural_classes, it.size))
                    } else {
                        binding.tvClassCount.text =
                            String.format(getString(R.string.singular_classes, it.size))
                    }
                }
            })

        viewModel.sessionDetailsObserver.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                if (it.containsKey(StudentConst.ERROR)) {
                    if (it[StudentConst.ERROR] == -1)
                        handleNetworkDialog(isFullScreen = false) {
                            retryFetchData(
                                startDate,
                                endDate,
                                offeringId
                            )
                        }
                } else {
                    val classCount = it[StudentConst.classCount] as Int
                    timetableId = it[StudentConst.timetableId] as Int?

                    if (classCount > 1) {
                        binding.tvClassCount.text =
                            String.format(getString(R.string.plural_classes, classCount))
                    } else {
                        binding.tvClassCount.text =
                            String.format(getString(R.string.singular_classes, classCount))
                    }
                }
            }
        })
    }

    private fun retryFetchData(startDate: Long, endDate: Long, offeringId: Int?) {
        getTimetable(this.startDate, this.endDate, offeringId)
        getCourseList()
    }

    private fun changeMonth(isNextMonth: Boolean) {
        startDate = Utils.getLocalDateInSeconds(Calendar.getInstance().apply {
            timeInMillis = startDate
            add(Calendar.MONTH, if (isNextMonth) 1 else -1)
            set(
                Calendar.DAY_OF_MONTH,
                this.getActualMinimum(Calendar.DAY_OF_MONTH)
            )
        })
        endDate = Utils.getLocalDateInSeconds(Calendar.getInstance().apply {
            timeInMillis = startDate
            set(
                Calendar.DAY_OF_MONTH,
                this.getActualMaximum(Calendar.DAY_OF_MONTH)
            )
        })
        binding.tvCurrentDate?.text = Utils.formatDatedMonthText(startDate)
        getTimetable(startDate, endDate, offeringId)

    }
}