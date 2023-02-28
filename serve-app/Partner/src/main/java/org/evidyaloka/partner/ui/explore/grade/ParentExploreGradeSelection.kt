package org.evidyaloka.partner.ui.explore.grade

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.common.explore.ExploreGradeSelection
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.ExploreData
import org.evidyaloka.partner.ui.explore.OnboardingViewModel
import java.util.*


@AndroidEntryPoint
class ParentExploreGradeSelection : ExploreGradeSelection() {
    private val TAG = "FragmentOnboarding"
    private val viewModel: OnboardingViewModel by viewModels()

    private var exploreData: ExploreData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            var args = ParentExploreGradeSelectionArgs.fromBundle(it)
            exploreData = args.exploreData
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exploreData?.courseProviderId?.let {
            viewModel.getGrades(it).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let {
                            gradeAdapter?.setItems(it.grades.sorted())
                        }

                    }
                    is Resource.GenericError -> {

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
                        ParentExploreGradeSelectionDirections.actionParentExploreGradeSelectionToPartnerExploreDigitalSchoolView(
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