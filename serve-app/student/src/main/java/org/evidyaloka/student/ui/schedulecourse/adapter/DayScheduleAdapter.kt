package org.evidyaloka.student.ui.schedulecourse.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.ItemDaySlotBinding
import org.evidyaloka.student.model.DaySlot


class DayScheduleAdapter() :
    RecyclerView.Adapter<DayScheduleAdapter.DayHolder>() {
    private val TAG = "DayScheduleAdapter"
    private lateinit var binding: ItemDaySlotBinding
    private var daySlots: List<DaySlot> = mutableListOf()

    private var selectedDays = mutableSetOf<Integer>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayHolder {
        binding = ItemDaySlotBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return DayHolder(binding.root)
    }

    override fun onBindViewHolder(holder: DayHolder, position: Int) {
        Log.e(TAG, "onBindViewHolder: ")
        var daySlot = daySlots[position]
        holder.bind(daySlot)
    }

    fun setItems(daySlots: List<DaySlot>) {
        this.daySlots = daySlots
        notifyDataSetChanged()
    }

    fun getSelectedDays(): Set<Integer> {
        return selectedDays
    }

    override fun getItemCount(): Int {
        return daySlots.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class DayHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemDaySlotBinding.bind(view)

        fun bind(daySlot: DaySlot) {

            var selectedDayId = (adapterPosition + 1) as Integer
            with(binding) {
                daySlot?.let {
                    it.title?.let {
                        binding.rbDay.text = it
                    }
                    rbDay.isChecked = (it.status == true)

                    if (rbDay.isChecked) {
                        selectedDays.add(selectedDayId)
                    } else {
                        selectedDays.remove(selectedDayId)
                    }

                    if(!it.selectable){
                        rbDay.isEnabled =false
                        rbDay.setTextColor(rbDay.context.resources.getColor(R.color.black))
                    }else{
                        rbDay.setOnClickListener {
                            Log.e(TAG, "bind: "+ "setOnClickListener")
                            if (selectedDays.contains(selectedDayId)) {
                                selectedDays.remove(selectedDayId)
                            } else {
                                selectedDays.add(selectedDayId)
                            }
                        }
                    }
                }
            }
        }
    }
}