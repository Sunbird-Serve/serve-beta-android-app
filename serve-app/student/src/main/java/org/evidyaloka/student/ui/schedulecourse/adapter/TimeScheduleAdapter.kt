package org.evidyaloka.student.ui.schedulecourse.adapter

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.ItemTimeSlotBinding
import org.evidyaloka.student.model.TimeSlot

class TimeScheduleAdapter() : RecyclerView.Adapter<TimeScheduleAdapter.TimeScheduleHolder>() {
    private val TAG = "DayScheduleAdapter"
    private var dialog: AlertDialog? = null
    private var timeSlots: List<TimeSlot> = listOf()

    private var selectedTime = mutableSetOf<Integer>()

    fun setItems(timeSlots: List<TimeSlot>) {
        this.timeSlots = timeSlots
        notifyDataSetChanged()
    }

    fun getSelectedTime(): MutableSet<Integer> {
        return selectedTime
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeScheduleHolder {
        val binding = ItemTimeSlotBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return TimeScheduleHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeScheduleHolder, position: Int) {
        var timeSlot = timeSlots[position]
        holder.bind(timeSlot)
    }

    override fun getItemCount(): Int {
        return timeSlots.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class TimeScheduleHolder(val binding: ItemTimeSlotBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(timeSlot: TimeSlot) {
            with(binding) {
                timeSlot.timing?.let {
                    binding.tvDaySlotTiming.text = it
                }

                timeSlot.title?.let {
                    binding.tvDaySlotTitle.text = it
                }

                binding.rbTimeSlot.background = timeSlot.background?.let {
                    binding.rbTimeSlot?.context.getDrawable(it)
                }

                timeSlot.textColor?.let {
                    binding.tvDaySlotTiming.setTextColor(it)
                }

                rbTimeSlot.isChecked = (timeSlot.status == true)

                if (rbTimeSlot.isChecked) {
                    selectedTime.add(adapterPosition as Integer)
                } else {
                    selectedTime.remove(adapterPosition as Integer)
                }

                ivIcon.background = timeSlot.icon?.let {
                    binding.ivIcon?.context?.getDrawable(it)
                }

                if (!timeSlot.selectable) {
                    rbTimeSlot.isEnabled = false
                } else {
                    rbTimeSlot.setOnClickListener { view ->
                        if (selectedTime.contains(adapterPosition as Integer)) {
                            selectedTime.remove(adapterPosition as Integer)
                        } else {
                            selectedTime.add(adapterPosition as Integer)
                            if (selectedTime.size == 2 && dialog == null) {
                                view.context?.let {
                                    showPopUpWarning(it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showPopUpWarning(context: Context) {

        dialog = MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialogTheme).apply {
            setMessage(context.getString(R.string.only_1_class_per_subject))
            setPositiveButton(context.getString(R.string.ok)) { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
            }
        }.create()
        dialog?.show()
    }
}