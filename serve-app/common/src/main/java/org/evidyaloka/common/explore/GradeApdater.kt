package org.evidyaloka.partner.ui.explore.grade

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.evidyaloka.common.databinding.ItemGradeBinding
import org.evidyaloka.common.R

class GradeAdapter() :
    RecyclerView.Adapter<GradeAdapter.GradeHolder>() {
    private val TAG = "GradeAdapter"

    private var grades: List<Int> = listOf()

    private var selectedGrade: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GradeHolder {
        val binding = ItemGradeBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return GradeHolder(binding)
    }

    fun setItems(grades: List<Int>) {
        this.grades = grades
        notifyDataSetChanged()
    }

    fun getSelectedGrade(): Int? {
        return selectedGrade
    }

    override fun getItemCount(): Int {
        return grades.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(holder: GradeHolder, position: Int) {
        Log.e(TAG, "onBindViewHolder: ")
        val grade = grades[position]
        grade.let {
            if(selectedGrade == null)
                selectedGrade = grade
            holder.bind(it)
        }
    }

    inner class GradeHolder(val binding: ItemGradeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(grade: Int) {
            with(binding) {
                (tvGrade.context.resources.getString(R.string.grade) + " " + grade).also {
                    tvGrade.text = it
                }
                rbGrade.isChecked = (selectedGrade == grade)
                if (rbGrade.isChecked) {
                    selectedGrade = grade
                    linearLayoutContainer.background =
                        root.context?.getDrawable(R.drawable.radio_grade_checked)
                    rbGrade.visibility = View.VISIBLE
                } else {
                    linearLayoutContainer.background =
                        root.context?.getDrawable(R.drawable.radio_grade_unchecked)
                    rbGrade.visibility = View.GONE
                }

                linearLayoutContainer.setOnClickListener {
                    selectedGrade = grade
                    notifyDataSetChanged()
                }
            }
        }
    }
}