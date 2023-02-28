package org.evidyaloka.student.ui.student

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.evidyaloka.common.util.Utils
import org.evidyaloka.core.student.model.Student
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.ItemStudentViewBinding
import org.evidyaloka.student.utils.Util

class StudentAdapter() :
    RecyclerView.Adapter<StudentAdapter.StudentHolder>() {
    private val TAG = "StudentAdapter"

    //Todo change Student type
    private var students: List<Student> = listOf()
    private var studentSelectListener : OnStudentSelectedListener? = null

    fun setStudentSelectListener(studentSelectListener: OnStudentSelectedListener) {
        this.studentSelectListener = studentSelectListener
    }

    // lastSelectedPosition to select only one radio button across all list of students
    // By default first student will be selected.
    private var selectedStudent: Student? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentHolder {
        val binding = ItemStudentViewBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return StudentHolder(binding)
    }

    fun setItems(students: List<Student>) {
        this.students = students
        notifyDataSetChanged()
    }

    fun getSelectedStudent(): Student?{
        return selectedStudent
    }

    override fun getItemCount(): Int {
        return students.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(holder: StudentHolder, position: Int) {
        Log.e(TAG, "onBindViewHolder: ")
        val student = students[position]
        if(selectedStudent == null){
            selectedStudent = student
            studentSelectListener?.onStudentSelected(student)
        }
        student?.let {
            holder.bind(it,position)
        }
    }

    inner class StudentHolder(val binding: ItemStudentViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(student: Student, position: Int) {
            with(binding) {

                student.name?.let {
                    tvName.text = it
                }

                var profileImageUrl = ""
                student.profileUrl?.let {
                    profileImageUrl = it
                }

                student.gender?.let { gender ->

                    ivStudentPic?.let { iv ->
                        iv?.context?.let {

                            var genderPlaceholder = Util.genderPlaceHolderImage(it, gender,(position+1) % 3)

                            Glide.with(it)
                                .load(profileImageUrl)
                                .placeholder(genderPlaceholder)
                                .error(genderPlaceholder)
                                .transform(CenterCrop(), RoundedCorners(15))
                                .into(iv)

                        }
                    }
                }

                student.grade?.let { grade ->
                    tvGrade?.context?.let { con ->
                        try {
                            tvGrade.text =
                                con.getString(R.string.label_grade_with_colon).plus(grade).plus(
                                    Utils.ordinalSuffix(grade.toInt())
                                    //TODO; make sure grade is always integer
                                )
                        } catch (e: Exception) {
                            FirebaseCrashlytics.getInstance().recordException(e)
                            e.printStackTrace()
                        }
                    }
                }
            }

            binding.rbStudent.isChecked  = (selectedStudent == student)

            if (binding.rbStudent.isChecked) {
                selectedStudent = student
                binding.linearLayoutContainer.background =
                    binding.root.context?.getDrawable(R.drawable.layer_student_radio_button_checked)
            } else {
                binding.linearLayoutContainer.background =
                    binding.root.context?.getDrawable(R.drawable.layer_student_radio_button_unchecked)
            }

            binding.linearLayoutContainer.setOnClickListener {
                selectedStudent = student
                notifyDataSetChanged()
            }
        }
    }
}