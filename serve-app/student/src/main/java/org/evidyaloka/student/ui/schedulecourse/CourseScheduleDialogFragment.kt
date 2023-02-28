package org.evidyaloka.student.ui.schedulecourse

import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.common.util.Utils.getAbbreviatedDayName
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.ViewLandingScheduleCourseBinding
import org.evidyaloka.student.model.DaySlot
import org.evidyaloka.student.model.TimeSlot
import org.evidyaloka.student.ui.schedulecourse.adapter.DayScheduleAdapter
import org.evidyaloka.student.ui.schedulecourse.adapter.TimeScheduleAdapter
import org.evidyaloka.student.utils.Util
import kotlin.collections.HashMap


@AndroidEntryPoint
class CourseScheduleDialogFragment : BottomSheetDialogFragment() {

    private val TAG = "TAG"
    private val viewModel: ScheduleCourseViewModel by viewModels()
    private lateinit var binding: ViewLandingScheduleCourseBinding

    var daySlotsList = mutableListOf<DaySlot>()
    var dayAdapter = DayScheduleAdapter()

    var timeSlotsList = mutableListOf<TimeSlot>()
    var timeAdapter = TimeScheduleAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = ViewLandingScheduleCourseBinding.inflate(layoutInflater, container, false)
        // get the views and attach the listener
        return binding.root
    }

    companion object {
        fun newInstance(): CourseScheduleDialogFragment {
            return CourseScheduleDialogFragment()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            Util.setWhiteNavigationBar(dialog)
        }
        dialog.setOnShowListener { setupBottomSheet(it) }
        return dialog
    }

    private fun setupBottomSheet(dialogInterface: DialogInterface) {
        val bottomSheetDialog = dialogInterface as BottomSheetDialog
        val bottomSheet = bottomSheetDialog.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        )
            ?: return
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)
        BottomSheetBehavior.from(bottomSheet).peekHeight =
            Resources.getSystem().getDisplayMetrics().heightPixels
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        daySlotsList = mutableListOf<DaySlot>(
            DaySlot(
                title = getAbbreviatedDayName(resources.getString(R.string.monday)),
                status = true
            ),
            DaySlot(
                title = getAbbreviatedDayName(resources.getString(R.string.tuesday)),
                status = true
            ),
            DaySlot(
                title = getAbbreviatedDayName(resources.getString(R.string.wednesday)),
                status = true
            ),
            DaySlot(
                title = getAbbreviatedDayName(resources.getString(R.string.thursday)),
                status = true
            ),
            DaySlot(
                title = getAbbreviatedDayName(resources.getString(R.string.friday)),
                status = true
            ),
            DaySlot(
                title = getAbbreviatedDayName(resources.getString(R.string.saturday)),
                status = true
            ),
            DaySlot(
                title = getAbbreviatedDayName(resources.getString(R.string.sunday)),
                status = false
            )
        )

        initTimeSlotView()
        initDaySlotView()

        binding.btBeginClass.setOnClickListener {

            if (validateSelectedSlots()) {
                //Todo call Schedule class API
                createScheduleBuildRequest()?.let {
                    viewModel.schedulecourse(it)
                        .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                            when (it) {
                                is Resource.Success -> {
                                    viewModel.updateStudentStatus(StudentConst.completed)
                                        .observe(viewLifecycleOwner,
                                            androidx.lifecycle.Observer {
                                                setCourseStartDateDialogShouldShow()
                                            })
                                }
                                is Resource.GenericError -> {
                                    it.code?.let {
                                        if (it <= 0)
                                            return@Observer
                                    }
                                    it?.error?.code?.let { code ->
                                        when (code) {
                                            35 -> {
                                                //User should be guardian and active
                                                showPopup(
                                                    getString(R.string.error),
                                                    getString(R.string.guardian_not_active)
                                                )
                                            }
                                            43 -> {
                                                showPopup(
                                                    getString(R.string.error),
                                                    getString(R.string.select_more_time)
                                                )
                                            }

                                        }
                                    }
                                }
                            }
                        })
                }
            }
        }

        binding.btCancel.setOnClickListener {
            dismissDialog()
        }

        viewModel.getSettings().observe(viewLifecycleOwner, Observer {
            it?.let {
                timeSlotsList.clear()
                if (it?.studyStudyTimeConfiguration?.size > 0) {
                    it.studyStudyTimeConfiguration?.forEach { stc ->
                        requireContext()?.let {
                            Log.e(TAG, "onViewCreated: " + stc)
                            TimeSlotFactory().getTimeSlot(
                                it,
                                stc.key,
                                stc
                            )?.let { it1 ->
                                timeSlotsList.add(it1)
                                timeAdapter.setItems(timeSlotsList)
                            }
                        }
                    }
                } else {
                    getSettingsIfNull()
                }
            } ?: getSettingsIfNull()
        })
    }

    private fun setCourseStartDateDialogShouldShow() {
        viewModel.setCourseStartDateDialogShouldShow(false)
            .observe(viewLifecycleOwner, Observer {
                try {
                    findNavController().navigate(R.id.action_scheduleCourseFragment_to_nav_home)
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)

                }
                dismissDialog()
            })
    }

    private fun dismissDialog() {
        dismiss()
    }

    private fun initDaySlotView() {
        binding.rvDays?.let {
            it.apply {
                layoutManager = LinearLayoutManager(
                    this.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                adapter = dayAdapter
                dayAdapter.setItems(daySlotsList)
            }
        }
    }


    private fun initTimeSlotView() {
        binding.rvTime?.let {
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

            hashMap[StudentConst.DAYS] = dayAdapter?.getSelectedDays()
            hashMap[StudentConst.SLOTS] = selectedTimeSlots
            Log.e(TAG, "selectedTimeSlots: " + selectedTimeSlots)
            Log.e(TAG, "selectedTimeSlots: " + dayAdapter?.getSelectedDays())
        }
        return hashMap
    }

    fun validateSelectedSlots(): Boolean {
        if (timeAdapter?.getSelectedTime().size <= 0) {
            //Todo Show time slot select
            showPopup(getString(R.string.error), getString(R.string.min_1_study_time))
            return false
        }

        if (dayAdapter?.getSelectedDays().size <= 2) {
            //Todo Show Day slot select
            showPopup(getString(R.string.error), getString(R.string.min_3_study_day))
            return false
        }

        if (timeAdapter?.getSelectedTime().size >= 1 && dayAdapter?.getSelectedDays().size >= 3) {
            return true
        } else {
            showPopup(getString(R.string.error), getString(R.string.min_1_time_3_days))
            return false;
        }


        return true
    }

    fun showPopup(title: String, message: String) {
        this.context?.let {
            Util.showPopupDialog(
                it,
                title = title,
                message = message
            )?.let {
                it.show()
            }
        }
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
}