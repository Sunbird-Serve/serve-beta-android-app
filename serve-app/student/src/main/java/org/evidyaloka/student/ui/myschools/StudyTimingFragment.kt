package org.evidyaloka.student.ui.myschools

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.common.util.Utils
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.student.model.StudyTimeConfiguration
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.FragmentStudyTimingBinding
import org.evidyaloka.student.model.DaySlot
import org.evidyaloka.student.model.TimeSlot
import org.evidyaloka.student.ui.BaseFragment
import org.evidyaloka.student.ui.schedulecourse.TimeSlotFactory
import org.evidyaloka.student.ui.schedulecourse.adapter.DayScheduleAdapter
import org.evidyaloka.student.ui.schedulecourse.adapter.TimeScheduleAdapter

@AndroidEntryPoint
class StudyTimingFragment : BaseFragment() {
    private val TAG = "StudyTimingFragment"
    private val viewModel: SchoolViewModel by viewModels()
    private lateinit var binding: FragmentStudyTimingBinding
    var dayAdapter = DayScheduleAdapter()
    var timeAdapter = TimeScheduleAdapter()
    var allSubjects: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val args =
                StudyTimingFragmentArgs.fromBundle(it)
            allSubjects = args.allSubjects
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudyTimingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.layoutStudyTiming.llButtons.visibility = View.GONE
        getStudyPreferences()

        binding.tvStudyTimings.visibility = View.GONE
        binding.tvStudyTimings.setOnClickListener {
            try{
            navController?.navigate(
            StudyTimingFragmentDirections.
            actionStudyTimingsFragmentToChangeTimingFragment(false, null, null, allSubjects))
            }catch (e : Exception){
                FirebaseCrashlytics.getInstance().recordException(e)
            }}

        allSubjects?.let {
            binding.tvSubjectNames.setText(getString(R.string.label_all_subjects) + it)
        }
        //initTimeSlotView()
        //initDaySlotView()
    }

    private fun initTimeSlotView() {
        binding.layoutStudyTiming.rvTime?.let {
            it.apply {
                layoutManager = GridLayoutManager(
                    this.context, 2,
                    GridLayoutManager.VERTICAL,
                    false
                )
                adapter = timeAdapter
            }
        }
    }

    private fun initDaySlotView() {
        binding.layoutStudyTiming.rvDays?.let {
            it.apply {
                layoutManager = LinearLayoutManager(
                    this.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                adapter = dayAdapter
            }
        }
    }

    private fun getStudyPreferences() {
        viewModel.getStudyPreferences()?.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    Log.e(TAG, "getStudyPreferences: " + it.data?.days)
                    it.data?.days?.let {
                       var sortedDaySlot= it.sortedBy { it }
                        updateDaySlots(sortedDaySlot)
                    }

                    it.data?.slots?.let {
                        updateTimeSlots(it)
                    }
                }
                is Resource.GenericError -> {
                    it?.error?.code?.let { code ->
                        //TODO use correct Error code.
                        showPopup(
                            getString(R.string.label_sorry),
                            getString(R.string.could_not_able_to_fetch_data)
                        )
                    }
                }
            }
        })
    }

    private fun updateTimeSlots(it: List<StudyTimeConfiguration>) {
        var timeSlots = mutableListOf<TimeSlot>()
        it.forEach { stc ->
            requireContext()?.let {
                TimeSlotFactory().getTimeSlot(it, stc.key, stc)?.let { it1 ->
                    //Change selected to true
                    it1.status = true
                    it1.selectable = false
                    timeSlots.add(it1)
                }
            }
        }
        initTimeSlotView()
        timeAdapter.setItems(timeSlots)
    }

    fun updateDaySlots(days: List<String>) {
        var daySlots = mutableListOf<DaySlot>()
        days.forEach {
            var daySlot = when (it) {
                StudentConst.Days.Monday.value.toString() -> {
                    DaySlot(
                        id = 1,
                        title = Utils.getAbbreviatedDayName(resources.getString(R.string.monday)),
                        status = true,
                        selectable = false
                    )
                }
                StudentConst.Days.Tuesday.value.toString() -> {
                    DaySlot(
                        id = 2,
                        title = Utils.getAbbreviatedDayName(resources.getString(R.string.tuesday)),
                        status = true,
                        selectable = false
                    )
                }
                StudentConst.Days.Wednesday.value.toString() -> {
                    DaySlot(
                        id = 3,
                        title = Utils.getAbbreviatedDayName(resources.getString(R.string.wednesday)),
                        status = true,
                        selectable = false
                    )
                }
                StudentConst.Days.Thursday.value.toString() -> {
                    DaySlot(
                        id = 4,
                        title = Utils.getAbbreviatedDayName(resources.getString(R.string.thursday)),
                        status = true,
                        selectable = false
                    )
                }
                StudentConst.Days.Friday.value.toString() -> {
                    DaySlot(
                        id = 5,
                        title = Utils.getAbbreviatedDayName(resources.getString(R.string.friday)),
                        status = true,
                        selectable = false
                    )
                }
                StudentConst.Days.Saturday.value.toString() -> {
                    DaySlot(
                        id = 6,
                        title = Utils.getAbbreviatedDayName(resources.getString(R.string.saturday)),
                        status = true,
                        selectable = false
                    )
                }
                StudentConst.Days.Sunday.value.toString() -> {
                    DaySlot(
                        id = 7,
                        title = Utils.getAbbreviatedDayName(resources.getString(R.string.sunday)),
                        status = true,
                        selectable = false
                    )
                }
                else -> null
            }
            daySlot?.let {
                daySlots.add(it)
            }
            Log.e(TAG, "updateDaySlots: " + daySlots)
        }
        initDaySlotView()
        dayAdapter.setItems(daySlots)
    }

}