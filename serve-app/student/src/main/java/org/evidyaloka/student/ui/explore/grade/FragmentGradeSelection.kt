package org.evidyaloka.student.ui.explore.grade

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.common.explore.ExploreGradeSelection
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.student.model.ExploreData
import org.evidyaloka.student.R
import org.evidyaloka.student.ui.explore.OnboardingViewModel
import org.evidyaloka.student.ui.ParentExploreActivity
import org.evidyaloka.student.utils.Util


@AndroidEntryPoint
class FragmentGradeSelection : ExploreGradeSelection() {
    private val TAG = "FragmentOnboarding"
    private val viewModel: OnboardingViewModel by viewModels()

    private var exploreData: ExploreData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            var args = FragmentGradeSelectionArgs.fromBundle(it)
            exploreData = args.exploreData
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            (it as ParentExploreActivity).showToolbar()
            (it as ParentExploreActivity).setToolbarTitleGravity(Gravity.START)

        }
        binding.tvSubtitle.visibility = View.INVISIBLE

        exploreData?.courseProviderId?.let {
            viewModel.getGrades(it).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let {
                            gradeAdapter?.setItems(it.grades.sorted())
                        }

                    }
                    is Resource.GenericError -> {
                        Util.showPopupDialog(
                            requireContext(),
                            resources.getString(R.string.error),
                            resources.getString(R.string.error_data_not_valid)
                        )
                    }
                }
            })
        }

        binding.btNext.setOnClickListener {
            gradeAdapter.getSelectedGrade()?.let {
                try {
                    exploreData?.apply {
                        grade = it
                    }
                    findNavController()?.navigate(
                        FragmentGradeSelectionDirections.actionFragmentGradeSelectionToStudentExploreDigitalSchoolView(
                            exploreData
                        )
                    )
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
        }
    }
}