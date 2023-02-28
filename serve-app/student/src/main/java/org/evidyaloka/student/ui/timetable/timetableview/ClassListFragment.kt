package org.evidyaloka.student.ui.timetable.timetableview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.common.util.Utils
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.model.Session
import org.evidyaloka.core.model.Subject
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.FragmentClassListBinding
import org.evidyaloka.student.ui.BaseFragment
import org.evidyaloka.student.ui.StudentHomeActivity
import org.evidyaloka.student.ui.timetable.OnItemClickListner
import org.evidyaloka.student.ui.timetable.TimetableViewModel
import org.evidyaloka.student.utils.calendar.data.Day
import org.evidyaloka.student.utils.calendar.view.OnSwipeTouchListener
import org.evidyaloka.student.utils.calendar.widget.CollapsibleCalendar
import java.util.*

@AndroidEntryPoint
class ClassListFragment : BaseFragment() {

    private val TAG = "ClassListFragment"

    private var timetableAdapter =
        TimetableAdapter()

    private lateinit var binding: FragmentClassListBinding
    private var timetableId: Int? = null
    private val viewModel: TimetableViewModel by viewModels()
    private var cal = Calendar.getInstance(TimeZone.getDefault())
    private var startDate: Long? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::binding.isInitialized) {
            binding = FragmentClassListBinding.inflate(layoutInflater, container, false)
            openTimetableView()
            setTimtableQueryDate(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        startDate?.let {
            getTimetable(it, it)
        }
        updateUIColor(R.color.colorOnPrimary)
        updateToolBarTitleColor(R.color.chat_text_color)
        try {
            val classToggler = binding.rbGroupClass
            classToggler.check(R.id.rb_timetable)
            classToggler.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.rb_timetable -> {
                        //Todo show timetable
                        openTimetableView()
                    }
                    R.id.rb_subjects -> {
                        openSubjectsView()
                        //Todo show subjects
                    }
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun openSubjectsView() {
        binding.llTimetable.visibility = View.GONE
        binding.llSubject.visibility = View.VISIBLE
        viewModel.getSubjects().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it is Resource.Success)
                it.data?.let { list ->
                    binding.llSubject.adapter =
                        SubjectAdapter(list, object : OnSubjectClickListner {
                            override fun OnItemClick(subject: Subject) {
                                val navigate =
                                    ClassListFragmentDirections.actionClassListFragmentToSubjectTimetableFragment(
                                        subject,
                                        subject.subjectName
                                    )
                                try {
                                    navController.navigate(navigate)
                                } catch (e: Exception) {
                                    FirebaseCrashlytics.getInstance().recordException(e)

                                }
                            }
                        })

                }
        })
    }

    /**
     * To change the color of the app bar & status bar.
     */
    fun updateUIColor(color: Int? = null) {
        // update cardview border and bg
        activity?.let { it ->
            (it as StudentHomeActivity).updateUiStyle(color)
        }
    }

    /**
     * To change the color of the app bar & status bar.
     */
    fun updateToolBarTitleColor(color: Int) {
        // update cardview border and bg
        activity?.let { it ->
            (it as StudentHomeActivity).updateToolbarTitleColor(color)
        }
    }


    private fun openTimetableView() {
        binding.llTimetable.visibility = View.VISIBLE
        binding.llSubject.visibility = View.GONE
        //To hide or show expand icon
        binding.calendarView.setExpandIconVisible(false)

        val today = GregorianCalendar()
//        binding.calendarView.addEventTag(
//            today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(
//                Calendar.DAY_OF_MONTH
//            )
//        )
        today.add(Calendar.DATE, 1)
        binding.calendarView.select(
            Day(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
        )
//        binding.calendarView.selectedDay = Day(
//            today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(
//                Calendar.DAY_OF_MONTH
//            )
//        )
        binding.calendarView.selectedItemBackgroundDrawable =
            resources.getDrawable(R.drawable.calendar_selected_day, null)

        binding.calendarView.unSelectedItemBackgroundDrawable =
            resources.getDrawable(R.drawable.calendar_unselected_day, null)

//        binding.calendarView.addEventTag(
//            today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(
//                Calendar.DAY_OF_MONTH
//            ), Color.BLUE
//        )


        binding.calendarView.params = CollapsibleCalendar.Params(-365, 365)


        binding.ivCalendarLeft.setOnClickListener {
            Log.e(TAG, "ivCalendarLeft: ")
            binding.calendarView.prevWeek()
//            Selecting monday or start day of the month
            binding.calendarView.selectedDay?.let {
                val calWeek = Calendar.getInstance().apply {
                    set(it.year, it.month, it.day)
                }
                if (binding.calendarView.isPreWeekIsSameMonth()) {
                    calWeek.add(Calendar.WEEK_OF_MONTH, -1)
                    calWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                } else {
                    if (binding.calendarView.isPreWeekHasPreMonth()) {
                        calWeek.get(Calendar.DAY_OF_WEEK).let {
                            if (it > 2) {
                                calWeek.add(Calendar.DATE, -(it - 1))
                                if (calWeek.get(Calendar.DAY_OF_MONTH) != calWeek.getActualMaximum(
                                        Calendar.DATE
                                    )
                                )
                                    calWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                            } else {
                                calWeek.add(Calendar.DATE, -1)
                                if (calWeek.get(Calendar.DAY_OF_MONTH) == calWeek.getActualMinimum(
                                        Calendar.DATE
                                    )
                                ) {
                                    calWeek.add(Calendar.DATE, -1)
                                    calWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                                }
                            }
                        }
                    } else {
                        calWeek.set(Calendar.DATE, 1)
                        if (calWeek.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                            calWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                    }
                }
                calWeek
                binding.calendarView.select(
                    Day(
                        calWeek.get(Calendar.YEAR),
                        calWeek.get(Calendar.MONTH),
                        calWeek.get(Calendar.DAY_OF_MONTH)
                    )
                )
            }

        }

        binding.ivCalendarRight.setOnClickListener {
            Log.e(TAG, "ivCalendarRight: ")
            binding.calendarView.selectedDay?.let {
                val calWeek = Calendar.getInstance().apply {
                    set(it.year, it.month, it.day)
                }

                if (binding.calendarView.isNextWeekIsSameMonth()) {
                    val hasSameMonthView = binding.calendarView.isNextWeekHasSameMonth()
                    calWeek.add(
                        Calendar.DATE,
                        (Calendar.SATURDAY - calWeek.get(Calendar.DAY_OF_WEEK) + if (hasSameMonthView) 0 else 1)
                    )
                    calWeek.get(Calendar.DAY_OF_MONTH).let {
                        if (!hasSameMonthView) {
                            if (it == 1)
                                calWeek.add(Calendar.DATE, -1)
                            if (it != calWeek.getActualMaximum(Calendar.DATE))
                                calWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                        } else {
                            if (calWeek.get(Calendar.DAY_OF_MONTH) > 2)
                                calWeek.add(Calendar.DATE, 1)
                            calWeek.set(Calendar.DATE, 1)
                        }
                    }


                } else {
                    calWeek.add(
                        Calendar.DATE,
                        (Calendar.SATURDAY - calWeek.get(Calendar.DAY_OF_WEEK) + 1)
                    )
                    calWeek.set(Calendar.DATE, 1)
                    if (calWeek.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                        calWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                }
                calWeek
                binding.calendarView.select(
                    Day(
                        calWeek.get(Calendar.YEAR),
                        calWeek.get(Calendar.MONTH),
                        calWeek.get(Calendar.DAY_OF_MONTH)
                    )
                )
            }
            binding.calendarView.nextWeek()
        }

        binding.btCourseTimetable.setOnClickListener {
            Log.e(TAG, "btTimetableSubjectwise: ")
            try {
                binding.calendarView.selectedDay?.toUnixTime()?.let {
                    it?.let {
                        val startDateCal = Calendar.getInstance()
                        startDateCal.timeInMillis = it
                        startDateCal.apply {
                            //+1 because starting from Sunday
                            val startDay =
                                this.get(Calendar.DAY_OF_YEAR) + 1 - this.get(Calendar.DAY_OF_WEEK)
                            this.set(Calendar.DAY_OF_YEAR, startDay)
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MINUTE, 0)
                        }

                        val endDateCal = Calendar.getInstance().apply {
                            val endDay = startDateCal.get(Calendar.DAY_OF_YEAR) + 6
                            set(Calendar.DAY_OF_YEAR, endDay)
                            set(Calendar.HOUR_OF_DAY, 23)
                            set(Calendar.MINUTE, 59)
                            set(Calendar.SECOND, 59)
                        }
                        try {
                            navController.navigate(
                                ClassListFragmentDirections.actionClassListFragmentToCourseTimetableFragment(
                                    startDateCal.timeInMillis,
                                    endDateCal.timeInMillis
                                )
                            )
                        } catch (e: Exception) {
                            FirebaseCrashlytics.getInstance().recordException(e)

                        }
                    }
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)

            }
        }

        binding.rvClasses.apply {
            layoutManager = LinearLayoutManager(
                this.context,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = timetableAdapter

            timetableAdapter.setOnItemClickListener(object :
                OnItemClickListner {
                override fun OnItemClick(session: Session) {
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
                        bundle.putParcelable(StudentConst.SESSION, session)
                        timetableId?.let { bundle.putInt(StudentConst.TIMETABLE_ID, it) }
                        try {
                            findNavController().navigate(R.id.subTopicListFragment, bundle)
                        } catch (e: Exception) {
                            FirebaseCrashlytics.getInstance().recordException(e)

                        }
                    }
                }
            })
        }

        binding.calendarView.setCalendarListener(object : CollapsibleCalendar.CalendarListener {
            override fun onDayChanged() {
                Log.e(TAG, "onDayChanged: ")
                binding.tvCurrentDate.text =
                    binding.calendarView.selectedDay?.getDate()?.toString()
            }

            override fun onClickListener() {
                Log.e(TAG, "onClickListener: ")
                if (binding.calendarView.expanded) {
                    binding.calendarView.collapse(400)
                } else {
                    //collapsibleCalendar.expand(400)
                }
            }

            override fun onDaySelect() {
                binding.tvCurrentDate.text =
                    binding.calendarView.selectedDay?.getDate()?.toString()
                binding.calendarView.selectedDay?.let {
                    setTimtableQueryDate(it.year, it.month, it.day)
                }
            }

            override fun onItemClick(v: View) {
                Log.e(TAG, "onItemClick: " + binding.calendarView.selectedDay)
            }

            override fun onDataUpdate() {
                Log.e(TAG, "onDataUpdate: ")
                binding.tvCurrentDate.text =
                    binding.calendarView.selectedDay?.getDate()?.toString()

            }

            override fun onMonthChange() {
                Log.e(TAG, "onMonthChange: ")

            }

            override fun onWeekChange(position: Int) {
                Log.e(TAG, "onWeekChange: ")


            }
        })

        binding.layoutCalendar.setOnTouchListener(object :
            OnSwipeTouchListener(binding.layoutCalendar.context) {
            override fun onSwipeRight() {
                Log.e(TAG, "onSwipeRight: ")
                binding.calendarView.nextDay()
            }

            override fun onSwipeLeft() {
                Log.e(TAG, "onSwipeLeft:")
                binding.calendarView.prevDay()
            }

            override fun onSwipeTop() {
                Log.e(TAG, "onSwipeTop:")
                /*if(collapsibleCalendar.expanded){
                    collapsibleCalendar.collapse(400)
                }*/
            }

            override fun onSwipeBottom() {
                /*if(!collapsibleCalendar.expanded){
                    collapsibleCalendar.expand(400)
                }*/
            }
        })
    }

    private fun setTimtableQueryDate(year: Int, month: Int, day: Int) {
        startDate = Utils.getLocalDateInSeconds(year, month, day) / 1000
        startDate?.let {
            val endDate = it
            getTimetable(it, endDate)
        }

    }

    private fun getTimetable(startDate: Long, endDate: Long) {
        viewModel.getTimetable(startDate, endDate)
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                timetableAdapter.submitList(it)
            })

        viewModel.sessionDetailsObserver.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                if (it.containsKey(StudentConst.ERROR)) {
                    if (it[StudentConst.ERROR] == -1)
                        handleNetworkDialog(isFullScreen = false) {
                            getTimetable(
                                startDate,
                                endDate
                            )
                        }
                } else {
                    timetableId = it[StudentConst.timetableId] as Int?
                }
                if(it.containsKey(StudentConst.count) && (it[StudentConst.count] as Int) <= 0) {
                    binding.noClassLayout.visibility = View.VISIBLE
                }else {
                    binding.noClassLayout.visibility = View.GONE
                }
            }
        })
    }

}
