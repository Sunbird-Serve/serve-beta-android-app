package org.evidyaloka.student.ui.explore.join

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.student.databinding.LayoutStudentEnrolledSuccessBinding
import org.evidyaloka.student.ui.BaseFragment
import org.evidyaloka.student.ui.explore.OnboardingViewModel
import org.evidyaloka.student.ui.ParentExploreActivity

@AndroidEntryPoint
class FragmentThankYou : BaseFragment() {
    private val TAG = "FragmentOnboarding"
    private lateinit var binding: LayoutStudentEnrolledSuccessBinding
    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = LayoutStudentEnrolledSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btAddAnother.setOnClickListener {
            //Open location access screen to add another student.
            try{
                navController?.navigate(FragmentThankYouDirections.actionFragmentThankYouToFragmentLocationAccess())
            }catch (e: Exception){
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

        binding.btStartLearning.setOnClickListener {
            // Open Student list fragment to list all the students.
            try{
                navController?.navigate(FragmentThankYouDirections
                    .actionFragmentThankYouToStudentListFragment(null, null))
            }catch (e: Exception){
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.let { (it as ParentExploreActivity).showToolbar() }
    }

    override fun onResume() {
        super.onResume()
        activity?.let {
            (it as ParentExploreActivity).hideToolbar()
        }
    }
}