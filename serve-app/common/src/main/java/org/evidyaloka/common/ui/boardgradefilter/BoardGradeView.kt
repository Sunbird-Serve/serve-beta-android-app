package org.evidyaloka.common.ui.boardgradefilter

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import org.evidyaloka.common.R
import org.evidyaloka.common.databinding.LayoutGradeBoardSelectionBinding
import org.evidyaloka.core.model.CourseProvider

class BoardGradeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private var binding = LayoutGradeBoardSelectionBinding.inflate(LayoutInflater.from(context),this)
    private var courseProviderList: List<CourseProvider> = listOf()

    fun setGradeSpinner(list: List<Int>,onGradeSelected: (grade: Int) -> Unit){
        binding.grade.apply {
            setAdapter(ArrayAdapter(context, R.layout.spinner_list_item, list))
            setOnItemClickListener { parent, view, position, id ->
                onGradeSelected(list.get(position))
            }
        }
    }

    fun setBoard(list: List<CourseProvider>, onBoardSelected: (board: CourseProvider) -> Unit){
        courseProviderList = list
        binding.schoolBoard.apply {
            setAdapter(ArrayAdapter(context, R.layout.spinner_list_item,  list.map { it.name }))
            setOnItemClickListener { parent, view, position, id ->
                binding.schoolBoard.setSelection(0)
                onBoardSelected(courseProviderList[position])
            }
        }
    }

    fun setSelectedBoard(board: CourseProvider){
        binding.schoolBoard.setText(board.name)
    }

    fun setSelectedGrade(grade: Int?){
        binding.grade.setText(grade?.toString())
    }

}