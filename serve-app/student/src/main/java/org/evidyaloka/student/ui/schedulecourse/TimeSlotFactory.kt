package org.evidyaloka.student.ui.schedulecourse

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import com.yariksoffice.lingver.Lingver
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.student.model.StudyTimeConfiguration
import org.evidyaloka.student.R
import org.evidyaloka.student.model.TimeSlot
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class TimeSlotFactory {

    public fun getTimeSlot(
        context: Context,
        timeSlotKey: String,
        studeyTimeConfiguration: StudyTimeConfiguration
    ): TimeSlot? {
        when (timeSlotKey) {
            StudentConst.earlyMorning -> {
                return TimeSlot(
                    title = context?.resources?.getString(R.string.early_morning),
                    key = timeSlotKey,
                    timing = getTiming(context, timeSlotKey, studeyTimeConfiguration),
                    icon = R.drawable.ic_early_morning,
                    status = true,
                    background = R.drawable.selector_cb_early_morning,
                    textColor = ResourcesCompat.getColor(
                        context?.resources,
                        R.color.day_slot_color_1,
                        null
                    )
                )
            }
            StudentConst.morning -> {
                return TimeSlot(
                    title = context?.resources?.getString(R.string.morning),
                    key = timeSlotKey,
                    timing = getTiming(context, timeSlotKey, studeyTimeConfiguration),
                    icon = R.drawable.ic_morning,
                    status = false,
                    background = R.drawable.selector_cb_morning,
                    textColor = ResourcesCompat.getColor(
                        context?.resources,
                        R.color.day_slot_color_2,
                        null
                    )
                )
            }
            StudentConst.afternoon -> {
                return TimeSlot(
                    title = context?.resources?.getString(R.string.afternoon),
                    timing = getTiming(context, timeSlotKey, studeyTimeConfiguration),
                    icon = R.drawable.ic_afternoon,
                    status = false,
                    background = R.drawable.selector_cb_afternoon,
                    textColor = ResourcesCompat.getColor(
                        context?.resources,
                        R.color.day_slot_color_3,
                        null
                    )
                )
            }
            StudentConst.evening -> {
                return TimeSlot(
                    title = context?.resources?.getString(R.string.evening),
                    key = timeSlotKey,
                    timing = getTiming(context, timeSlotKey, studeyTimeConfiguration),
                    icon = R.drawable.ic_evening,
                    status = false,
                    background = R.drawable.selector_cb_evening,
                    textColor = ResourcesCompat.getColor(
                        context?.resources,
                        R.color.day_slot_color_4,
                        null
                    )
                )
            }
        }
        return null
    }

    fun getTiming(
        context: Context,
        timeSlotKey: String,
        timeConfiguration: StudyTimeConfiguration
    ): String {
        var timing = ""
        timeConfiguration?.let {
            if (it.startTimeMin == 0
                || (it.endTimeMin == 0)
            ) {
                when (timeSlotKey) {
                    StudentConst.earlyMorning -> {
                        timing =
                            context?.resources?.getString(R.string.early_morning_timing).toString()
                    }
                    StudentConst.morning -> {
                        timing =
                            context?.resources?.getString(R.string.morning_timing).toString()
                    }
                    StudentConst.afternoon -> {
                        timing =
                            context?.resources?.getString(R.string.afternoon_timing).toString()
                    }
                    StudentConst.evening -> {
                        timing = context?.resources?.getString(R.string.evening_timing).toString()
                    }
                }

            } else {
                timing =
                    getClassTime(context, it.startTimeMin)+ " - " + getClassTime(
                        context,
                        it.endTimeMin
                    )
            }
        }
        return timing
    }


    fun getClassTime(context: Context, durationInmins: Int): String {
        var sdf = SimpleDateFormat("mm");

        try {
            var date = sdf.parse("" + durationInmins);
            sdf = SimpleDateFormat("ha");
            //            sdf = SimpleDateFormat("hh:mm a", Lingver.getInstance().getLocale());
            return sdf.format(date)
        } catch (e: Exception) {
            return ""
        }
    }

}