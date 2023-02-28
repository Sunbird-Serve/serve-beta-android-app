package org.evidyaloka.partner.ui.student

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.evidyaloka.partner.R
import org.evidyaloka.core.partner.model.Student
import org.evidyaloka.common.util.Utils
import org.evidyaloka.partner.databinding.ItemStudentBinding

class StudentAdapter(
    private val clickListener: (isChecked: Boolean, studentId: Int) -> Unit,
    private val showCheckbox: Boolean = false
) : PagedListAdapter<Student, StudentAdapter.StudentViewHolder>(STUDENT_COMPARATOR) {

    private var mShowCheckBox = showCheckbox
    private var isAllChecked: Boolean = false
    private var selectedStudents: MutableList<Int> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding =
            ItemStudentBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = getItem(position)
        if (student != null) {
            holder.bind(student, clickListener, mShowCheckBox, isAllChecked)
        }
    }

    fun toggleSelectAll(allSelected: Boolean) {
        isAllChecked = allSelected
        notifyDataSetChanged()
    }

    inner class StudentViewHolder(val binding: ItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            student: Student,
            clickListener: (isChecked: Boolean, studentId: Int) -> Unit,
            showCheckbox: Boolean = false,
            isAllChecked: Boolean = false
        ) {
            try {

                student?.let {

                    binding.ivStudentProfile?.let {

                        //Todo use logoID to show digitalSchool logo from Base url once API is live
                        it.context?.let { it1 ->
                            Glide.with(it1)
                                .load(student.profileUrl)
                                .circleCrop()
                                .placeholder(R.drawable.ic_student_placeholder)
                                .error(R.drawable.ic_student_placeholder)
                                //+ course.courseLogo)
                                .into(it)
                        }
                    }

                    binding.tvStudentName?.setText(student.studentName)
                    val grade = student.grade.takeIf { it.isNotEmpty() }?.toInt()
                    grade?.let {
                        binding.tvStudentGrade?.setText(
                            Utils.getFormatedGrade(it).plus(" ").plus(
                                Utils.ordinalSuffix(it)
                            ).plus(" ").plus(binding.root.resources.getString(R.string.label_grade))
                        )
                    }
                    binding.checkBox.visibility = if (showCheckbox) View.VISIBLE else View.INVISIBLE
                    binding.checkBox.setOnCheckedChangeListener(null)
                    if (isAllChecked) {
                        binding.checkBox.isChecked = true
                        binding.checkBox.isEnabled = false
                    } else {
                        binding.checkBox.isChecked = selectedStudents.contains(student.id)
                    }
                }

                binding.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                    clickListener(isChecked, student.id)
                    if (isChecked) {
                        selectedStudents.add(student.id)
                    } else {
                        selectedStudents.remove(student.id)
                    }
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                e.printStackTrace()
            }
        }
    }


    companion object {
        private val STUDENT_COMPARATOR = object : DiffUtil.ItemCallback<Student>() {
            override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean =
                newItem == oldItem
        }
    }
}