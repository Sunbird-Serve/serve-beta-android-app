package org.evidyaloka.student.ui.myschools

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.common.util.Utils
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.student.model.School
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
class ChangeTimingFragment : BaseFragment() {

    private val TAG = "ChangeTimingFragment"
    private val viewModel: SchoolViewModel by viewModels()
    private lateinit var binding: FragmentStudyTimingBinding
    var daySlotsList = mutableListOf<DaySlot>()
    var dayAdapter = DayScheduleAdapter()

    var timeSlotsList = mutableListOf<TimeSlot>()
    var timeAdapter = TimeScheduleAdapter()

    var offerings: IntArray? = null
    var updateRequired: Boolean = false
    var school: School? = null
    var allSubjects: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val args =
                ChangeTimingFragmentArgs.fromBundle(it)
            school = args.school
            offerings = args.offerings
            updateRequired = args.updateRequired
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
        binding.tvStudyTimings.visibility = View.GONE
        binding.layoutStudyTiming.llButtons.visibility =
            if (updateRequired) View.VISIBLE else View.GONE

        allSubjects?.let {
            binding.tvSubjectNames.setText(getString(R.string.label_all_subjects) + it)
        }

        daySlotsList = mutableListOf<DaySlot>(
            DaySlot(
                title = Utils.getAbbreviatedDayName(resources.getString(R.string.monday)),
                status = false
            ),
            DaySlot(
                title = Utils.getAbbreviatedDayName(resources.getString(R.string.tuesday)),
                status = false
            ),
            DaySlot(
                title = Utils.getAbbreviatedDayName(resources.getString(R.string.wednesday)),
                status = false
            ),
            DaySlot(
                title = Utils.getAbbreviatedDayName(resources.getString(R.string.thursday)),
                status = false
            ),
            DaySlot(
                title = Utils.getAbbreviatedDayName(resources.getString(R.string.friday)),
                status = false
            ),
            DaySlot(
                title = Utils.getAbbreviatedDayName(resources.getString(R.string.saturday)),
                status = false
            ),
            DaySlot(
                title = Utils.getAbbreviatedDayName(resources.getString(R.string.sunday)),
                status = false
            )
        )

        getStudyPreferences()

        Log.e(TAG, "btBeginEnabled: " + binding.layoutStudyTiming.btBeginClass.isEnabled)

        binding.layoutStudyTiming.btCancel.setOnClickListener {
            //Todo change Timing API
            try{
            navController.navigateUp()
            }catch (e : Exception){
                FirebaseCrashlytics.getInstance().recordException(e)

            }
        }

        //initTimeSlotView()
        //initDaySlotView()

        initTimeSlotView()
        initDaySlotView()

        viewModel.getSettings().observe(viewLifecycleOwner, Observer {
            it?.let {
                timeSlotsList.clear()
                if (it?.studyStudyTimeConfiguration?.size > 0) {
                    it.studyStudyTimeConfiguration?.forEach { stc ->
                        requireContext()?.let {
                            Log.e(TAG, "onViewCreated: " + stc)
                            TimeSlotFactory().getTimeSlot(it, stc.key, stc)?.let { it1 ->
                                timeSlotsList.add(it1)
                                //timeAdapter.setItems(timeSlotsList)
                            }
                        }
                    }
                } else {
                    getSettingsIfNull()
                }
            } ?: getSettingsIfNull()
        })

        binding.layoutStudyTiming.btBeginClass.setOnClickListener {
            //Todo change Timing API
            Log.e(TAG, "btBeginClasssetOnClickListener: ")
            createScheduleBuildRequest()?.let {
                viewModel.updateSchedule(it).observe(viewLifecycleOwner, Observer {
                    when (it) {
                        is Resource.Success -> {
                            try{
                            navController?.navigate(ChangeTimingFragmentDirections.actionGlobalNavHome())
                            }catch (e : Exception){
                                FirebaseCrashlytics.getInstance().recordException(e)

                            }
                        }

                        is Resource.GenericError -> {
                            it.code?.let {
                                if (it <= 0)
                                    return@Observer
                            }
                            when (it.error?.code) {
                                2, 3, 36, 38, 42 -> showPopup(
                                    getString(R.string.error),
                                    getString(R.string.data_submited_not_valid)
                                )
                                43 -> {
                                    showPopup(
                                        getString(R.string.error),
                                        getString(R.string.select_more_time)
                                    )
                                }
                                44 -> {
                                    showPopup(
                                        getString(R.string.error),
                                        getString(R.string.error_system_settings_defined_wrong)
                                    )
                                }
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
                                56 -> {
                                    showPopup(
                                        getString(R.string.error),
                                        getString(R.string.error_invalid_student)
                                    )
                                }
                                68 -> {
                                    showPopup(
                                        getString(R.string.error),
                                        getString(R.string.error_slots_not_enough)
                                    )
                                }
                            }
                        }
                    }
                })
            }
        }

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
                //dayAdapter.setItems(daySlotsList)
            }
        }
    }

    private fun getStudyPreferences() {
        viewModel.getStudyPreferences()?.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    Log.e(TAG, "getStudyPreferences: " + it.data?.days)
                    it.data?.days?.let {
                        updateDaySlots(it)
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
        it.forEach { stc ->
            requireContext()?.let {
                //Pass status true for previously opted values.
                TimeSlotFactory().getTimeSlot(it, stc.key, stc)?.let { it1 ->
                    //Change selected to true
                    for (i in timeSlotsList.indices) {
                        if (timeSlotsList[i].key.equals(it1.key)) {
                            timeSlotsList[i].status = true
                            timeSlotsList[i].selectable = false
                        }
                    }
                }
            }
        }
        //initTimeSlotView()
        timeAdapter.setItems(timeSlotsList)
    }

    fun updateDaySlots(days: List<String>) {
        days.forEach {
            when (it) {
                StudentConst.Days.Monday.value.toString() -> {
                    daySlotsList[StudentConst.Days.Monday.value - 1].apply {
                        status = true
                        selectable = false
                    }
                }
                StudentConst.Days.Tuesday.value.toString() -> {
                    daySlotsList[StudentConst.Days.Tuesday.value - 1].apply {
                        status = true
                        selectable = false
                    }
                }
                StudentConst.Days.Wednesday.value.toString() -> {
                    daySlotsList[StudentConst.Days.Wednesday.value - 1].apply {
                        status = true
                        selectable = false
                    }
                }
                StudentConst.Days.Thursday.value.toString() -> {
                    daySlotsList[StudentConst.Days.Thursday.value - 1].apply {
                        status = true
                        selectable = false
                    }
                }
                StudentConst.Days.Friday.value.toString() -> {
                    daySlotsList[StudentConst.Days.Friday.value - 1].apply {
                        status = true
                        selectable = false
                    }
                }
                StudentConst.Days.Saturday.value.toString() -> {
                    daySlotsList[StudentConst.Days.Saturday.value - 1].apply {
                        status = true
                        selectable = false
                    }
                }
                StudentConst.Days.Sunday.value.toString() -> {
                    daySlotsList[StudentConst.Days.Sunday.value - 1].apply {
                        status = true
                        selectable = false
                    }
                }
                else -> {
                }
            }
        }
        initDaySlotView()
        dayAdapter.setItems(daySlotsList)
    }

    fun getSettingsIfNull() {
        timeSlotsList.clear()
        var timeSlots = listOf<TimeSlot>(
            TimeSlot(
                title = resources?.getString(R.string.early_morning),
                timing = resources?.getString(R.string.early_morning_timing),
                icon = R.drawable.ic_early_morning,
                status = true,
                background = R.drawable.selector_cb_early_morning,
                textColor = ResourcesCompat.getColor(
                    getResources(),
                    R.color.day_slot_color_1,
                    null
                )
            ),
            TimeSlot(
                title = resources?.getString(R.string.morning),
                timing = resources?.getString(R.string.morning_timing),
                icon = R.drawable.ic_morning,
                status = false,
                background = R.drawable.selector_cb_morning,
                textColor = ResourcesCompat.getColor(
                    getResources(),
                    R.color.day_slot_color_2,
                    null
                )
            ),
            TimeSlot(
                title = resources?.getString(R.string.afternoon),
                timing = resources?.getString(R.string.afternoon_timing),
                icon = R.drawable.ic_afternoon,
                status = false,
                background = R.drawable.selector_cb_afternoon,
                textColor = ResourcesCompat.getColor(
                    getResources(),
                    R.color.day_slot_color_3,
                    null
                )
            ),
            TimeSlot(
                title = resources?.getString(R.string.evening),
                timing = resources?.getString(R.string.evening_timing),
                icon = R.drawable.ic_evening,
                status = false,
                background = R.drawable.selector_cb_evening,
                textColor = ResourcesCompat.getColor(
                    getResources(),
                    R.color.day_slot_color_4,
                    null
                )
            )
        )

        timeSlotsList.addAll(timeSlots)
        timeAdapter.setItems(timeSlotsList)
    }

    fun createScheduleBuildRequest(): Map<String, Any> {
        var hashMap = HashMap<String, Any>()

        var selectedTimeSlots = arrayListOf<HashMap<String, Any>>()
        for (item in timeAdapter?.getSelectedTime()) {
            item?.let {
                var timeMap = HashMap<String, Any>()
                timeMap["key"] = when (it.toInt()) {
                    0 -> StudentConst.earlyMorning
                    1 -> StudentConst.morning
                    2 -> StudentConst.afternoon
                    3 -> StudentConst.evening
                    else -> StudentConst.earlyMorning
                }
                selectedTimeSlots.add(timeMap)
            }
        }

        hashMap[StudentConst.DAYS] = dayAdapter?.getSelectedDays()
        hashMap[StudentConst.SLOTS] = selectedTimeSlots
        school?.let {
            hashMap[StudentConst.DIGITAL_SCHOOL_ID] = it.id
        }
        updateRequired?.let {
            hashMap[StudentConst.IS_UPDATE_REQUIRED] = 1
        }
        offerings?.let {
            hashMap[StudentConst.NEW_OFFERINGS] = it.toList()
        }
        Log.e(TAG, hashMap.toString())
        return hashMap
    }

}