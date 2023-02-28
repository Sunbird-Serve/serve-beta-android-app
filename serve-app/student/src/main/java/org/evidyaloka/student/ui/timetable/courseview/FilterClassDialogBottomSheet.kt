package org.evidyaloka.student.ui.timetable.courseview

import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.student.databinding.BottomSheetCourseTimetableBinding
import org.evidyaloka.student.ui.schedulecourse.ScheduleCourseViewModel
import org.evidyaloka.student.utils.Util


@AndroidEntryPoint

class FilterClassDialogBottomSheet : BottomSheetDialogFragment() {

    private val TAG = "FilterClassDialogBottomSheet"
    private val viewModel: ScheduleCourseViewModel by viewModels()
    private lateinit var binding: BottomSheetCourseTimetableBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = BottomSheetCourseTimetableBinding.inflate(layoutInflater, container, false)

        // get the views and attach the listener
        return binding.root
    }

    companion object {
        fun newInstance(): FilterClassDialogBottomSheet {
            return FilterClassDialogBottomSheet()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            Util.setWhiteNavigationBar(dialog);
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

        binding.btApply.setOnClickListener {
            //Todo apply filters
            dismissDialog()
        }

        binding.btReset.setOnClickListener {
            //TODO remove filters
            dismissDialog()
        }


    }

    private fun dismissDialog() {
        dismiss()
    }

}